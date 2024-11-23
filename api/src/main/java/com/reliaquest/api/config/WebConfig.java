package com.reliaquest.api.config;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class WebConfig {
    @Value("${api.server-uri}")
    private String serverUri;

    @Value("${api.connection-timeout}")
    private long connectionTimeout;

    @Value("${api.read-timeout}")
    private long readTimeout;

    @Value("${api.read-timeout}")
    private long itConnectionTimeout;

    @Value("${api.read-timeout}")
    private long itReadTimeout;

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.rootUri(serverUri)
                .setConnectTimeout(Duration.ofMillis(connectionTimeout))
                .setReadTimeout(Duration.ofMillis(readTimeout))
                .build();
    }

    @Bean
    public RestTemplate restTemplateIntegration(RestTemplateBuilder builder) {
        return builder.setConnectTimeout(Duration.ofMillis(itConnectionTimeout))
                .setReadTimeout(Duration.ofMillis(itReadTimeout))
                .build();
    }
}
