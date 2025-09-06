package ru.afonskiy.messenger.jwt.util;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import ru.afonskiy.messenger.configuration.KeycloakConfig;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtUtils {

    private final Keycloak keycloak;
    private final KeycloakConfig keycloakConfig;
    private final JwtDecoder jwtDecoder;

    public boolean validateToken(String token) {
        try {
            jwtDecoder.decode(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        Jwt jwt = jwtDecoder.decode(token);
        return jwt.getClaimAsString("preferred_username"); // Keycloak username
    }

    public boolean usernameExists(String username) {
        try {
            List<UserRepresentation> users = keycloak
                    .realm(keycloakConfig.getValidateRealm())
                    .users()
                    .search(username, true);

            return users.stream()
                    .anyMatch(user -> user.getUsername().equals(username));
        }catch (Exception ex){
            throw new IllegalStateException("Ошибка работы Keycloak: " + ex.getMessage());
        }
    }

    public String getCurrentUIID() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new IllegalStateException("Authentication is null");
        }
        JwtAuthenticationToken authenticationToken = (JwtAuthenticationToken) authentication;
        return authenticationToken.getToken().getClaimAsString("sub");
    }
}
