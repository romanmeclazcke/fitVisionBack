package org.example.fitvisionback.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Order(1)
@Slf4j
@RequiredArgsConstructor
public class RateLimitingFilter extends OncePerRequestFilter {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    private final RateLimitConfig rateLimitConfig;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (!rateLimitConfig.isEnabled()) {
            filterChain.doFilter(request, response);
            return;
        }

        boolean isLogin = isLoginEndpoint(request);

        // Clave única por IP + tipo, para que no compartan el mismo bucket
        String bucketKey = resolveKey(request) + (isLogin ? ":login" : ":global");
        Bucket bucket = buckets.computeIfAbsent(bucketKey, k -> newBucket(isLogin));

        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
            return;
        }

        response.setStatus(429);
        response.setContentType("application/json");
        response.getWriter().write("{\"message\":\"Too many requests\"}");
    }

    /**
     * Creates a new Bucket with the appropriate rate limit configuration based on whether it's for login or general requests.
     * @param isLogin true if the bucket is for login requests, false for general requests
     * @return a configured Bucket instance
     */
    private Bucket newBucket(boolean isLogin) {
        int cap = isLogin ? rateLimitConfig.getAuth().getCapacity() : rateLimitConfig.getCapacity();
        int tokens = isLogin ? rateLimitConfig.getAuth().getRefillTokens() : rateLimitConfig.getRefillTokens();
        long seconds = isLogin ? rateLimitConfig.getAuth().getRefillSeconds() : rateLimitConfig.getRefillSeconds();

        Bandwidth limit = Bandwidth.classic(cap, Refill.intervally(tokens, Duration.ofSeconds(seconds)));
        return Bucket.builder().addLimit(limit).build();
    }

    private String resolveKey(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    /**
     *  Resolve if the request is for the login endpoint, to apply a different rate limit.
     * @param request request to check
     * @return true if the request is for the login endpoint, false otherwise
     */
    private boolean isLoginEndpoint(HttpServletRequest request) {
        String path = request.getRequestURI();
        if (path == null) {
            return false;
        }

        String contextPath = request.getContextPath();
        if (contextPath != null && !contextPath.isBlank() && path.startsWith(contextPath)) {
            path = path.substring(contextPath.length());
        }

        return path.startsWith("/auth/login") || path.startsWith("/api/auth/login") || path.startsWith("/api/v1/auth/login");
    }
}
