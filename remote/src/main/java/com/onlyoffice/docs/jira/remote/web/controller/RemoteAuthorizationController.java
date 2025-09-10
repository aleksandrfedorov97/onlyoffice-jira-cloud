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

package com.onlyoffice.docs.jira.remote.web.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlyoffice.docs.jira.remote.aop.CurrentFitContext;
import com.onlyoffice.docs.jira.remote.api.Context;
import com.onlyoffice.docs.jira.remote.api.FitContext;
import com.onlyoffice.docs.jira.remote.api.JiraContext;
import com.onlyoffice.docs.jira.remote.api.Product;
import com.onlyoffice.docs.jira.remote.api.XForgeTokenType;
import com.onlyoffice.docs.jira.remote.configuration.ForgeProperties;
import com.onlyoffice.docs.jira.remote.security.RemoteAppJwtService;
import com.onlyoffice.docs.jira.remote.security.SecurityUtils;
import com.onlyoffice.docs.jira.remote.security.XForgeTokenRepository;
import com.onlyoffice.docs.jira.remote.web.dto.authorization.AuthorizationRequest;
import com.onlyoffice.docs.jira.remote.web.dto.authorization.AuthorizationResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.Map;


@RestController
@RequestMapping("/api/v1/remote/authorization")
@RequiredArgsConstructor
public class RemoteAuthorizationController {
    @Value("${app.base-url}")
    private String baseUrl;
    @Value("${app.security.ttl.default}")
    private long ttlDefault;

    private final ForgeProperties forgeProperties;
    private final RemoteAppJwtService remoteAppJwtService;
    private final XForgeTokenRepository xForgeTokenRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping
    public ResponseEntity<AuthorizationResponse> getAuthorization(
            final @AuthenticationPrincipal Jwt principal,
            final @CurrentFitContext FitContext fitContext,
            final @RequestHeader("x-forge-oauth-system") String xForgeSystemToken,
            final @RequestHeader("x-forge-oauth-user") String xForgeUserToken,
            final @Valid @RequestBody AuthorizationRequest request
    ) throws ParseException {
        Product product = forgeProperties.getProductByAppId(principal.getAudience().getFirst());
        String accountId = principal.getClaimAsString("principal");

        Context remoteAppTokenContext = switch (product) {
            case JIRA -> JiraContext.builder()
                    .product(product)
                    .cloudId(fitContext.cloudId())
                    .issueId(request.getParentId())
                    .attachmentId(request.getEntityId())
                    .build();
            default -> throw new UnsupportedOperationException();
        };

        xForgeTokenRepository.saveXForgeToken(
                SecurityUtils.createXForgeSystemTokenId(
                        remoteAppTokenContext.getProduct(),
                        remoteAppTokenContext.getCloudId()
                ),
                xForgeSystemToken,
                XForgeTokenType.SYSTEM
        );
        xForgeTokenRepository.saveXForgeToken(
                SecurityUtils.createXForgeUserTokenId(
                        remoteAppTokenContext.getProduct(),
                        remoteAppTokenContext.getCloudId(),
                        accountId
                ),
                xForgeUserToken,
                XForgeTokenType.USER
        );

        String token = remoteAppJwtService.encode(
                accountId,
                "/editor/" + product.toString().toLowerCase(),
                ttlDefault,
                objectMapper.convertValue(remoteAppTokenContext, new TypeReference<Map<String, Object>>() { })
        ).getTokenValue();

        return ResponseEntity.ok(
                new AuthorizationResponse(baseUrl, token)
        );
    }
}
