package ru.afonskiy.messenger.service;

import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;
import ru.afonskiy.messenger.configuration.KeycloakConfig;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsernameService {
    private final Keycloak keycloak;
    private final KeycloakConfig keycloakConfig;

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

}
