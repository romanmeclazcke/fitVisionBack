package org.example.fitvisionback.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class OAuthRedirectController {

    @GetMapping("/google")
    public ResponseEntity<Void> redirectToGoogle(HttpServletRequest request) {
        String redirectUrl = request.getContextPath() + "/oauth2/authorization/google";
        return ResponseEntity.status(302)
                .header(HttpHeaders.LOCATION, redirectUrl)
                .build();
    }
}
