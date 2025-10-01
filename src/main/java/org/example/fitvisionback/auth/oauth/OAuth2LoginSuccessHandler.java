package org.example.fitvisionback.auth.oauth;

import org.example.fitvisionback.auth.service.JwtService;
import org.example.fitvisionback.credits.model.Credits;
import org.example.fitvisionback.credits.service.CreditsService;
import org.example.fitvisionback.user.entity.AuthenticationProvider;
import org.example.fitvisionback.user.entity.Role;
import org.example.fitvisionback.user.entity.User;
import org.example.fitvisionback.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final CreditsService creditsService;

    @Value("${application.frontend.url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        DefaultOAuth2User oauthUser = (DefaultOAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oauthUser.getAttributes();
        String email = attributes.getOrDefault("email", "").toString();
        String name = attributes.getOrDefault("name", "").toString();

        Optional<User> userOptional = userRepository.findByEmail(email);
        User user;
        if (userOptional.isEmpty()) {
            user = User.builder()
                    .email(email)
                    .name(name)
                    .role(Role.USER)
                    .provider(AuthenticationProvider.GOOGLE)
                    .build();

            userRepository.save(user);

            Credits credits = Credits.builder()
                    .user(user)
                    .credits(0)
                    .build();

            this.creditsService.save(credits);
        } else {
            user = userOptional.get();
        }

        String jwtToken = jwtService.generateToken(user);
        
        String redirectUrl = UriComponentsBuilder.fromUriString(frontendUrl+"/auth/callback")
                .queryParam("token", jwtToken)
                .build().toUriString();
        //

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
