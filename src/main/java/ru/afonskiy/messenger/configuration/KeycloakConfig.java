package ru.afonskiy.messenger.configuration;

import lombok.Data;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "keycloak")
public class KeycloakConfig {
    private String url;
    private String realm;
    private String clientId;
    private String clientSecret;
    private String validateRealm;

    @Bean
    public Keycloak keycloak() {
        return KeycloakBuilder.builder()
                .clientId("messenger-service")
                .realm(realm)
                .serverUrl(url)
                .clientSecret(clientSecret)
                .grantType("client_credentials")
                .build();
    }
}
