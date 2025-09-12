/**
 *
 * (c) Copyright Ascensio System SIA 2025
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import React, {
  useEffect,
  useState,
  useRef,
  type ReactNode,
  useContext,
} from "react";

import Button from "@atlaskit/button/new";
import Flag, { ActionsType, FlagGroup } from "@atlaskit/flag";
import StatusWarningIcon from "@atlaskit/icon/core/status-warning";
import { Flex, xcss } from "@atlaskit/primitives";
import Spinner from "@atlaskit/spinner";
import { token } from "@atlaskit/tokens";
import { events, invoke, view } from "@forge/bridge";
import { FullContext } from "@forge/bridge/out/types";

import { Attachment, RemoteAppAuthorization } from "../../../src/types/types";
import { getAttachmentsForIssue, getUsers } from "../../client";
import { AppContext } from "../../context/AppContext";

const styles = {
  mainContainer: xcss({
    display: "flex",
    flexDirection: "column",
    width: "100%",
    height: "100%",
    margin: "0",
    padding: "0",
    justifyContent: "center",
    alignItems: "center",
  }),
  iframe: {
    width: "100%",
    height: "100%",
    border: "unset",
  },
};

type SessionExpiredFlag = {
  description: string;
  icon: ReactNode;
  id: string;
  time: number;
  title: string;
  actions: ActionsType;
};

const defaultSessionExpiredFlagData: SessionExpiredFlag = {
  id: "session-expired-flag",
  title: "",
  description: "",
  icon: (
    <StatusWarningIcon label="Warning" color={token("color.icon.warning")} />
  ),
  time: 59,
  actions: [],
};

export type EditorPageProps = {
  context: FullContext;
};

const EditorPage: React.FC<EditorPageProps> = ({ context }) => {
  const iframeRef = useRef<HTMLIFrameElement>(null);
  const [loading, setLoading] = useState(true);
  const remoteAppUrl = useRef<string | null>(null);
  const [token, setToken] = useState<string>();
  const [sessionExpiredFlag, setSessionExpiredFlag] =
    useState<SessionExpiredFlag>();

  const issueId = context.extension.issue.id;
  const attachmentId = context.extension.modal.attachmentId;
  const mode = context.extension.modal.mode;

  const { t, setAppError } = useContext(AppContext);

  const sendMessageToIframe = (
    name: string,
    data: object | undefined | null,
  ) => {
    if (
      remoteAppUrl.current &&
      iframeRef.current &&
      iframeRef.current.contentWindow
    ) {
      iframeRef.current.contentWindow.postMessage(
        {
          type: name,
          data: data,
        },
        remoteAppUrl.current,
      );
    }
  };

  useEffect(() => {
    events.on("JIRA_ISSUE_CHANGED", () => {
      getAttachmentsForIssue(issueId)
        .then((attachments: Attachment[]) => {
          if (
            !attachments.some((attachment) => attachment.id === attachmentId)
          ) {
            sendMessageToIframe("STOP_EDITING", {
              message: t("page.editor.messages.stop-editing-by-cause-delete"),
            });
          }
        })
        .catch((error) => {
          console.error("Error fetching attachments:", error);
        });
    });

    invoke<RemoteAppAuthorization>("authorizeRemoteApp", {
      issueId,
      attachmentId,
    })
      .then((data: RemoteAppAuthorization) => {
        setToken(data.token);
        remoteAppUrl.current = data.remoteAppUrl;
      })
      .catch((error) => {
        console.error("Error fetching data:", error);

        setAppError({
          title: t("error-state.common.title"),
          description: t("error-state.common.description"),
          secondaryAction: <Button onClick={closeWindow}>Close</Button>,
        });
      });
  }, []);

  const closeWindow = () => {
    view.close();
  };

  const onSessionExpired = () => {
    invoke<RemoteAppAuthorization>("authorizeRemoteApp", {
      issueId,
      attachmentId,
    })
      .then((data: RemoteAppAuthorization) => {
        sendMessageToIframe("UPDATE_CONFIG", {
          mode: mode,
          token: data.token,
        });
      })
      .catch((error) => {
        console.error(error);

        sessionFailedToExtend();
      });
  };

  const onRequestUsers = (c: string, ids: string[]) => {
    getUsers(ids)
      .then((values) => {
        const users = values.map((user) => ({
          id: user.accountId,
          name: user.displayName,
          email: user.emailAddress,
          image: user.avatarUrls["24x24"],
        }));

        sendMessageToIframe("SET_USERS", {
          c: c,
          users: users,
        });
      })
      .catch((error) => {
        console.error("Error fetching users:", error);
      });
  };

  const sessionSuccessfullyExtended = () => {
    const timer = setInterval(() => {
      const onFinal = () => {
        clearInterval(timer);
        sendMessageToIframe("RELOAD_EDITOR", null);
      };

      setSessionExpiredFlag((prev) => {
        if (prev && prev.time - 1 < 0) {
          onFinal();
          return undefined;
        }

        return {
          ...defaultSessionExpiredFlagData,
          time: prev ? prev.time - 1 : defaultSessionExpiredFlagData.time,
          title: t("labels.warning"),
          description: `${t("page.editor.messages.session-expired")}
             ${t("page.editor.messages.please-save-changes")}
             ${t("page.editor.messages.editor-will-be-reloaded").replace("{time}", String(prev ? prev.time - 1 : defaultSessionExpiredFlagData.time))}`,
        };
      });
    }, 1000);
  };

  const sessionFailedToExtend = () => {
    const timer = setInterval(() => {
      const onFinal = () => {
        clearInterval(timer);
        sendMessageToIframe("STOP_EDITING", {
          message: t(
            "page.editor.messages.stop-editing-by-cause-session-expired",
          ),
        });
      };

      setSessionExpiredFlag((prev) => {
        if (prev && prev.time - 1 < 0) {
          onFinal();
          return undefined;
        }

        return {
          ...defaultSessionExpiredFlagData,
          time: prev ? prev.time - 1 : defaultSessionExpiredFlagData.time,
          title: t("labels.warning"),
          description: `${t("page.editor.messages.session-expired")}
            ${t("page.editor.messages.update-session-failed")} ${t("page.editor.messages.please-save-changes")}
            ${t("page.editor.messages.editin-will-stop").replace("{time}", String(prev ? prev.time - 1 : defaultSessionExpiredFlagData.time))}`,
        };
      });
    }, 1000);
  };

  useEffect(() => {
    if (remoteAppUrl.current) {
      const handleMessage = (event: MessageEvent) => {
        if (event.origin !== remoteAppUrl.current) return;

        const { type, data } = event.data;

        if (type === "DOCS_API_UNDEFINED") {
          setAppError({
            title: t("error-state.docs-api-undefined.title"),
            description: t("error-state.docs-api-undefined.description"),
            secondaryAction: <Button onClick={closeWindow}>Close</Button>,
          });
        }

        if (type === "PAGE_IS_LOADED") {
          setLoading(false);
        }

        if (type === "DOCUMENT_READY") {
          const { demo } = data;

          if (demo) {
            sendMessageToIframe("SHOW_MESSAGE", {
              message: t("page.editor.messages.you-using-demo-server"),
            });
          }
        }

        if (type === "REQUEST_CLOSE") {
          closeWindow();
        }

        if (type === "REQUEST_USERS") {
          const { c, ids } = data;

          onRequestUsers(c, ids);
        }

        if (type === "SESSION_EXPIRED") {
          onSessionExpired();
        }

        if (type === "CONFIG_UPDATED") {
          sessionSuccessfullyExtended();
        }

        if (type === "ERROR_UPDATE_CONFIG") {
          sessionFailedToExtend();
        }
      };

      window.addEventListener("message", handleMessage);

      return () => {
        window.removeEventListener("message", handleMessage);
      };
    }
  }, [remoteAppUrl.current]);

  return (
    <Flex xcss={styles.mainContainer}>
      {loading && <Spinner size="xlarge" label={t("labels.loading")} />}
      {token && remoteAppUrl.current && (
        <iframe
          ref={iframeRef}
          style={{
            ...styles.iframe,
            display: loading ? "none" : "block",
          }}
          src={`${remoteAppUrl.current}/editor/jira?mode=${mode}&token=${token}`}
        />
      )}
      {sessionExpiredFlag && (
        <FlagGroup>
          <Flag
            id={sessionExpiredFlag.id}
            title={sessionExpiredFlag.title}
            description={sessionExpiredFlag.description}
            icon={sessionExpiredFlag.icon}
            actions={sessionExpiredFlag.actions}
          />
        </FlagGroup>
      )}
    </Flex>
  );
};

export default EditorPage;
