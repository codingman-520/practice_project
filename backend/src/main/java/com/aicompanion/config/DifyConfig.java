package com.aicompanion.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Data
@Configuration
@ConfigurationProperties(prefix = "dify")
public class DifyConfig {
    
    private Chat chat = new Chat();
    private Workflow workflow = new Workflow();

    @Data
    public static class Chat {
        private String apiKey;
        private String baseUrl;
    }

    @Data
    public static class Workflow {
        private String apiKey;
        private String baseUrl;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
