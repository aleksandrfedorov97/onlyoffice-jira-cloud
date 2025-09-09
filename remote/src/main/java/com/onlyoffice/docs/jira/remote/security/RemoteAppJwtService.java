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

package com.onlyoffice.docs.jira.remote.security;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtAudienceValidator;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Service
public class RemoteAppJwtService {
    private final NimbusJwtEncoder nimbusJwtEncoder;
    private final NimbusJwtDecoder nimbusJwtDecoder;

    public RemoteAppJwtService(final @Value("${app.security.secret}") String secret) {
        SecretKey secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "RAW");
        JWKSource<SecurityContext> jwkSource = new ImmutableSecret<>(secret.getBytes());

        this.nimbusJwtDecoder = NimbusJwtDecoder
                .withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();

        this.nimbusJwtEncoder = new NimbusJwtEncoder(jwkSource);
    }

    public Jwt encode(final String subject, final String audience, final long lifeTimeInMinutes,
                      final Map<String, Object> context) {
        JwsHeader header = JwsHeader
                .with(MacAlgorithm.HS256)
                .type("JWT")
                .build();

        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(TimeUnit.MINUTES.toSeconds(lifeTimeInMinutes));

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(subject)
                .audience(List.of(audience))
                .issuedAt(now)
                .expiresAt(expiry)
                .claim("context", context)
                .build();

        JwtEncoderParameters parameters = JwtEncoderParameters.from(header, claims);

        return nimbusJwtEncoder.encode(parameters);
    }

    public Jwt decode(final String token, final String audience) {
        List<OAuth2TokenValidator<Jwt>> validators = List.of(
                new JwtTimestampValidator(),
                new JwtAudienceValidator(audience)
        );

        nimbusJwtDecoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(validators));

        return nimbusJwtDecoder.decode(token);
    }
}
