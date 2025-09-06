package ru.afonskiy.messenger.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class GetCurrentUIID {
    public String getCurrentUIID() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw  new IllegalStateException("Authentication is null");
        }
        JwtAuthenticationToken authenticationToken = (JwtAuthenticationToken) authentication;
        return authenticationToken.getToken().getClaimAsString("sub");
    }
}
