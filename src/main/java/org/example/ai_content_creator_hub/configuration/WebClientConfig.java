package org.example.ai_content_creator_hub.configuration;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.example.ai_content_creator_hub.exception.OpenAIServiceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
public class WebClientConfig {
    @Value("${openai.api.base-url-v1}")
    private String openAiBaseUrlV1;

    @Value("${google.api.base_url_v2}")
    private String googleBaseUrlV2;

    @Bean
    public WebClient openAiWebClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder
                .baseUrl(openAiBaseUrlV1)
                .clientConnector(new ReactorClientHttpConnector(httpClient()))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .filter(errorMappingFilter)
                .build();
    }

    @Bean
    public WebClient googleWebClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder.baseUrl(googleBaseUrlV2)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    /** Shared Reactor Netty client with sensible timeouts. */
    private HttpClient httpClient() {
        return HttpClient.create()
                .compress(true)
                .responseTimeout(Duration.ofSeconds(30))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5_000)
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(30))
                        .addHandlerLast(new WriteTimeoutHandler(30)));
    }

    /** Reusable error filter: converts 4xx/5xx into OpenAIServiceException/Generic exception with body text. */
    private ExchangeFilterFunction errorMappingFilter =
            ExchangeFilterFunction.ofResponseProcessor(response -> {
                HttpStatusCode status = response.statusCode();
                if (status.isError()) {
                    return response.bodyToMono(String.class)
                            .defaultIfEmpty("")
                            .flatMap(body -> {
                                // Pick a domain-specific exception if you like; here I use OpenAIServiceException as example.
                                return Mono.error(new OpenAIServiceException(
                                        "HTTP " + status.value() + " from upstream: " + body));
                            });
                }
                return Mono.just(response);
            });
}
