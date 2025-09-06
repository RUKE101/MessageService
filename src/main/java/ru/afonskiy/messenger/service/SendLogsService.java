package ru.afonskiy.messenger.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ru.afonskiy.messenger.configuration.LoggingConfig;


@Service
@RequiredArgsConstructor
public class SendLogsService {

    private final WebClient.Builder webClientBuilder;
    private final LoggingConfig loggingConfig;
    private WebClient webClient;

    @PostConstruct
    public void init() {
        this.webClient = webClientBuilder.baseUrl(loggingConfig.getUrl()).build();
    }

    // Асинхронная отправка логов
    public void sendLogs(String action, String error, String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        String body = String.format("{\"action\":\"%s\",\"error\":\"%s\"}", action, error);

        webClient.post()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .toEntity(String.class)
                .doOnNext(responseEntity -> {
                })
                .doOnError(e -> {
                    if (e instanceof org.springframework.web.reactive.function.client.WebClientResponseException ex) {
                        System.err.println("Failed to send log. Status: " + ex.getRawStatusCode() + ", Body: " + ex.getResponseBodyAsString());
                    } else {
                        System.err.println("Failed to send log: " + e.getMessage());
                    }
                })
                .subscribe();
    }
}


