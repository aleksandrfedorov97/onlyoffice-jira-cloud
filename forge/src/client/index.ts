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

import { APIResponse, asUser, invokeRemote, route } from "@forge/api";

import {
  Attachment,
  ClientError,
  Format,
  RemoteAppAuthorization,
  RemoteSettings,
} from "../../src/types/types";

export const postRemoteAppAuthorization = async (
  issueId: string,
  attachmentId: string,
): Promise<RemoteAppAuthorization> => {
  return await _executeRequest<RemoteAppAuthorization>(
    async () => {
      return await invokeRemote("onlyoffice-backend", {
        method: "POST",
        path: "/api/v1/remote/authorization",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          parentId: issueId,
          entityId: attachmentId,
        }),
      });
    },
    async (response: APIResponse) => {
      return await response.json();
    },
    1,
  );
};

export const postRemoteCreateAttachment = async (
  issueId: string,
  title: string,
  type: string,
  locale: string,
): Promise<Attachment> => {
  return await _executeRequest<Attachment>(
    async () => {
      return await invokeRemote("onlyoffice-backend", {
        method: "POST",
        path: "/api/v1/remote/create",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          parentId: issueId,
          title: title,
          documentType: type,
          locale: locale,
        }),
      });
    },
    async (response: APIResponse) => {
      return await response.json();
    },
    1,
  );
};

export const getRemoteFormats = async (): Promise<Format[]> => {
  return await _executeRequest<Format[]>(
    async () => {
      return await invokeRemote("onlyoffice-backend", {
        method: "GET",
        path: "/api/v1/remote/formats",
        headers: {
          "Content-Type": "application/json",
        },
      });
    },
    async (response: APIResponse) => {
      const data = await response.json();

      return data.formats || {};
    },
    1,
  );
};

export const getRemoteSettings = async (): Promise<RemoteSettings> => {
  return await _executeRequest<RemoteSettings>(
    async () => {
      return await invokeRemote("onlyoffice-backend", {
        method: "GET",
        path: "/api/v1/remote/settings",
        headers: {
          "Content-Type": "application/json",
        },
      });
    },
    async (response: APIResponse) => {
      return await response.json();
    },
    1,
  );
};

export const postRemoteSettings = async (): Promise<RemoteSettings> => {
  return await _executeRequest<RemoteSettings>(
    async () => {
      return await invokeRemote("onlyoffice-backend", {
        method: "POST",
        path: "/api/v1/remote/settings",
        headers: {
          "Content-Type": "application/json",
        },
      });
    },
    async (response: APIResponse) => {
      return await response.json();
    },
    1,
  );
};

export const getAttachmentContentUrl = async (
  attachmentId: string,
): Promise<string> => {
  return await _executeRequest<string>(
    async () => {
      return await asUser().requestJira(
        route`/rest/api/3/attachment/content/${attachmentId}`,
        {
          method: "GET",
          redirect: "manual",
          headers: {
            "Content-Type": "application/json",
          },
        },
      );
    },
    (response: APIResponse) => {
      return response.headers.get("location") || "";
    },
    1,
  );
};

async function _executeRequest<T>(
  onRequest: () => Promise<APIResponse>,
  onResponse: (response: APIResponse) => T | Promise<T>,
  retry = 0,
): Promise<T> {
  try {
    const response = await onRequest();

    if (response.ok || response.status === 303) {
      return await onResponse(response);
    }

    throw new ClientError(
      `Request failed with status ${response.status}: ${response.statusText}`,
      response.status,
    );
  } catch (error) {
    if (
      error instanceof TypeError &&
      error.cause instanceof Error &&
      error.cause.name === "ConnectTimeoutError" &&
      retry > 0
    ) {
      return _executeRequest(onRequest, onResponse, retry - 1);
    }

    throw error;
  }
}
