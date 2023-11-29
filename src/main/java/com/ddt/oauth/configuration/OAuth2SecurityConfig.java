package com.ddt.oauth.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.web.SecurityFilterChain;

import java.util.UUID;

@Configuration
@EnableWebSecurity
public class OAuth2SecurityConfig {
    @Value("${oauth2.login-page}")
    private String oauth2LoginPage;
    @Value("${oauth2.provider-logout-endpoint}")
    private String oauth2ProviderLogoutEndpoint;

    @Value("${oauth2.logout-success-url}")
    private String oauth2LogoutSuccessUrl;

    private final static Logger logger = LoggerFactory.getLogger(OAuth2SecurityConfig.class);
    @Bean
    OAuth2AuthorizationRequestResolver authorizationRequestResolver(ClientRegistrationRepository clients) {
        StringKeyGenerator stateGenerator = () -> UUID.randomUUID().toString();
        DefaultOAuth2AuthorizationRequestResolver authorizationRequestResolver =
                new DefaultOAuth2AuthorizationRequestResolver(clients, "/oauth2/authorize");
        authorizationRequestResolver.setAuthorizationRequestCustomizer((request) -> request
                .state(stateGenerator.generateKey())
        );
        return authorizationRequestResolver;
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, OAuth2AuthorizationRequestResolver authorizationRequestResolver) throws Exception {
        http.authorizeHttpRequests((authsz) -> authsz
                        .requestMatchers("/", "/home", "/index.html","/error","/error.html","/logout","/logout.html","/logged_out","/webjars/**").permitAll()
                        .anyRequest().authenticated())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sessionConfigg -> sessionConfigg.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .oauth2Login(oauth2 ->  {
                    Customizer.withDefaults().customize(oauth2);
                    oauth2.authorizationEndpoint(authsz ->
                                authsz.authorizationRequestResolver(authorizationRequestResolver));
                    oauth2.loginPage(oauth2LoginPage);

                })
                .logout(logout -> {
                        logout.logoutUrl("/logout");
                        logout.logoutSuccessUrl("/logged_out");
                        logout.invalidateHttpSession(true);
                        logout.deleteCookies("JSESSIONID");
                        logout.logoutSuccessHandler(((request, response, authentication) -> {
                            response.sendRedirect(oauth2ProviderLogoutEndpoint);
                        }));
                });

        return http.build();
    }
}
