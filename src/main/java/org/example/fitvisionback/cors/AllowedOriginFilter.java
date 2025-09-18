package org.example.fitvisionback.cors;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AllowedOriginFilter implements Filter {


    @Value("${application.frontend.url}")
    private String FRONTED_URL;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String origin = req.getHeader("Origin");

        if (origin != null && !origin.equals(FRONTED_URL)) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden: Invalid origin");
            return;
        }

        chain.doFilter(request, response);
    }
}