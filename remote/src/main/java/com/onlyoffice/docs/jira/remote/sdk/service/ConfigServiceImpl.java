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

package com.onlyoffice.docs.jira.remote.sdk.service;

import com.onlyoffice.docs.jira.remote.api.Context;
import com.onlyoffice.docs.jira.remote.api.JiraContext;
import com.onlyoffice.docs.jira.remote.api.XForgeTokenType;
import com.onlyoffice.docs.jira.remote.client.jira.JiraClient;
import com.onlyoffice.docs.jira.remote.client.jira.dto.JiraAttachment;
import com.onlyoffice.docs.jira.remote.client.jira.dto.JiraPermission;
import com.onlyoffice.docs.jira.remote.client.jira.dto.JiraPermissions;
import com.onlyoffice.docs.jira.remote.client.jira.dto.JiraPermissionsKey;
import com.onlyoffice.docs.jira.remote.client.jira.dto.JiraUser;
import com.onlyoffice.docs.jira.remote.security.SecurityUtils;
import com.onlyoffice.docs.jira.remote.security.XForgeTokenRepository;
import com.onlyoffice.manager.document.DocumentManager;
import com.onlyoffice.manager.security.JwtManager;
import com.onlyoffice.manager.settings.SettingsManager;
import com.onlyoffice.manager.url.UrlManager;
import com.onlyoffice.model.common.User;
import com.onlyoffice.model.documenteditor.config.EditorConfig;
import com.onlyoffice.model.documenteditor.config.document.Permissions;
import com.onlyoffice.model.documenteditor.config.document.Type;
import com.onlyoffice.model.documenteditor.config.editorconfig.Customization;
import com.onlyoffice.model.documenteditor.config.editorconfig.Mode;
import com.onlyoffice.model.documenteditor.config.editorconfig.customization.Close;
import com.onlyoffice.service.documenteditor.config.DefaultConfigService;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class ConfigServiceImpl extends DefaultConfigService {
    private final JiraClient jiraClient;
    private final XForgeTokenRepository xForgeTokenRepository;

    public ConfigServiceImpl(final DocumentManager documentManager,
                             final UrlManager urlManager,
                             final JwtManager jwtManager,
                             final SettingsManager settingsManager, final JiraClient jiraClient,
                             final XForgeTokenRepository xForgeTokenRepository) {
        super(documentManager, urlManager, jwtManager, settingsManager);

        this.xForgeTokenRepository = xForgeTokenRepository;
        this.jiraClient = jiraClient;
    }

    @Override
    public EditorConfig getEditorConfig(final String fileId, final Mode mode, final Type type) {
        EditorConfig editorConfig = super.getEditorConfig(fileId, mode, type);

        Context context = SecurityUtils.getCurrentAppContext();

        switch (context.getProduct()) {
            case JIRA:
                JiraUser user = jiraClient.getUser(
                        context.getCloudId().toString(),
                        xForgeTokenRepository.getXForgeToken(
                                SecurityUtils.getCurrentXForgeUserTokenId(),
                                XForgeTokenType.USER
                        )
                );

                editorConfig.setLang(user.getLocale());

                return editorConfig;
            default:
                throw new UnsupportedOperationException("Unsupported product: " + context.getProduct());
        }
    }

    @Override
    public Permissions getPermissions(final String fileId) {
        Context context = SecurityUtils.getCurrentAppContext();

        switch (context.getProduct()) {
            case JIRA:
                JiraContext jiraContext = (JiraContext) context;

                JiraAttachment jiraAttachment = jiraClient.getAttachment(
                        jiraContext.getCloudId(),
                        fileId,
                        xForgeTokenRepository.getXForgeToken(
                                SecurityUtils.getCurrentXForgeUserTokenId(),
                                XForgeTokenType.USER
                        )
                );

                JiraPermissions jiraPermissions = jiraClient.getIssuePermissions(
                        jiraContext.getCloudId(),
                        jiraContext.getIssueId(),
                        List.of(
                                JiraPermissionsKey.CREATE_ATTACHMENTS,
                                JiraPermissionsKey.DELETE_OWN_ATTACHMENTS,
                                JiraPermissionsKey.DELETE_ALL_ATTACHMENTS
                        ),
                        xForgeTokenRepository.getXForgeToken(
                                SecurityUtils.getCurrentXForgeUserTokenId(),
                                XForgeTokenType.USER
                        )
                );

                JiraPermission createAttachments = jiraPermissions.getPermissions()
                        .get(JiraPermissionsKey.CREATE_ATTACHMENTS);

                JiraPermission deleteAttachments;

                if (jiraAttachment.getAuthor().getAccountId()
                        .equals(SecurityUtils.getCurrentPrincipal().getSubject())) {
                    deleteAttachments = jiraPermissions.getPermissions()
                            .get(JiraPermissionsKey.DELETE_OWN_ATTACHMENTS);
                } else {
                    deleteAttachments = jiraPermissions.getPermissions()
                            .get(JiraPermissionsKey.DELETE_ALL_ATTACHMENTS);
                }

                return Permissions.builder()
                        .edit(createAttachments.isHavePermission() && deleteAttachments.isHavePermission())
                        .build();
            default:
                throw new UnsupportedOperationException("Unsupported product: " + context.getProduct());
        }
    }

    @Override
    public Customization getCustomization(final String fileId) {
        Customization customization = super.getCustomization(fileId);

        customization.setClose(
                Close.builder()
                        .visible(true)
                        .build()
        );

        return customization;
    }

    @Override
    public User getUser() {
        Context context = SecurityUtils.getCurrentAppContext();

        switch (context.getProduct()) {
            case JIRA:
                JiraUser user = jiraClient.getUser(
                        context.getCloudId().toString(),
                        xForgeTokenRepository.getXForgeToken(
                                SecurityUtils.getCurrentXForgeUserTokenId(),
                                XForgeTokenType.USER
                        )
                );

                return User.builder()
                        .id(user.getAccountId())
                        .name(user.getDisplayName())
                        .image(user.getAvatarUrls().get("24x24"))
                        .build();
            default:
                throw new UnsupportedOperationException("Unsupported product: " + context.getProduct());
        }
    }
}
