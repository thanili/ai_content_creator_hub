package org.example.ai_content_creator_hub.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Value("${openai.api.base-url-v1}")
    private String openAiBaseUrlV1;

    @Value("${google.api.base_url_v2}")
    private String googleBaseUrlV2;

    @Bean
    public WebClient openAiWebClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder.baseUrl(openAiBaseUrlV1).build();
    }

    @Bean
    public WebClient googleWebClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder.baseUrl(googleBaseUrlV2)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
