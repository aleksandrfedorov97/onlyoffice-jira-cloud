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

package com.onlyoffice.docs.jira.remote.sdk.manager;

import com.onlyoffice.docs.jira.remote.api.Context;
import com.onlyoffice.docs.jira.remote.api.XForgeTokenType;
import com.onlyoffice.docs.jira.remote.client.jira.JiraClient;
import com.onlyoffice.docs.jira.remote.client.jira.dto.JiraSettings;
import com.onlyoffice.docs.jira.remote.entity.DemoServerConnection;
import com.onlyoffice.docs.jira.remote.entity.DemoServerConnectionId;
import com.onlyoffice.docs.jira.remote.repository.DemoServerConnectionRepository;
import com.onlyoffice.docs.jira.remote.security.SecurityUtils;
import com.onlyoffice.docs.jira.remote.security.XForgeTokenRepository;
import com.onlyoffice.manager.settings.DefaultSettingsManager;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;


@AllArgsConstructor
@Component
public class SettingsMangerImpl extends DefaultSettingsManager {
    private static final String SETTINGS_KEY = "onlyoffice-docs.settings";

    private final JiraClient jiraClient;
    private final XForgeTokenRepository xForgeTokenRepository;
    private final DemoServerConnectionRepository demoServerConnectionRepository;

    private final Map<String, String> settings = new HashMap<>();

    @Override
    public String getSetting(final String name) {
        Context context = SecurityUtils.getCurrentAppContext();

        if (!Objects.isNull(context)) {
            if (name.equals("demo-start")) {
               DemoServerConnection demoServerConnection = demoServerConnectionRepository.findById(
                        DemoServerConnectionId.builder()
                                .cloudId(context.getCloudId())
                                .product(context.getProduct())
                                .build()
                ).orElse(null);

               if (Objects.nonNull(demoServerConnection)) {
                   return demoServerConnection.getStartDate();
               } else {
                   return null;
               }
            }

            JiraSettings jiraSettings = jiraClient.getSettings(
                    SETTINGS_KEY,
                    xForgeTokenRepository.getXForgeToken(
                            SecurityUtils.getCurrentXForgeSystemTokenId(),
                            XForgeTokenType.SYSTEM
                    )
            );

            return Optional.ofNullable(jiraSettings.getValue().get(name))
                    .map(String::valueOf)
                    .orElse(null);
        }

        return settings.get(name);
    }

    @Override
    public void setSetting(final String name, final String value) {
        settings.put(name, value);
    }
}
