package org.example.fitvisionback.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "rate-limit")
public class RateLimitConfig {

    private boolean enabled = true;
    private int capacity = 100;
    private int refillTokens = 50;
    private long refillSeconds = 60;
    private Auth auth = new Auth();

    @Getter
    @Setter
    public static class Auth {
        private int capacity = 10;
        private int refillTokens = 5;
        private long refillSeconds = 60;
    }
}
