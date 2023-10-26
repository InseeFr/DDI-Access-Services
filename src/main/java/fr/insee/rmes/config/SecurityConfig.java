package fr.insee.rmes.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

import java.util.List;
import java.util.Optional;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig {

    @Value("${fr.insee.rmes.metadata.cors.allowedOrigin}")
    private Optional<String> allowedOrigin;

    private static final String[] SWAGGER_WHITELIST = {
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()

                .httpBasic(withDefaults())
                .formLogin(withDefaults())
                .logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/login").permitAll())
                .headers(headers -> {
                    headers.frameOptions(frameOptions -> frameOptions.disable());
                    headers.cacheControl(cacheControl -> cacheControl.disable());
                    headers.xssProtection(xss -> xss.disable());
                    headers.contentTypeOptions(contentType -> contentType.disable());
                    headers.httpStrictTransportSecurity(hsts -> hsts.disable());
                    headers.contentSecurityPolicy(contentSecurityPolicy -> {
                        contentSecurityPolicy.policyDirectives("default-src 'self'; script-src 'self' 'unsafe-inline' 'unsafe-eval'");
                    });
                    headers.referrerPolicy(referrerPolicy -> referrerPolicy.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER));
                      headers.addHeaderWriter((request, response) -> {
                        response.setHeader("X-Content-Type-Options", "nosniff");
                        response.setHeader("X-XSS-Protection", "1; mode=block");
                        response.setHeader("Strict-Transport-Security", "max-age=31536000 ; includeSubDomains");
                        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
                        response.setHeader("Pragma", "no-cache");
                        response.setHeader("Expires", "0");
                    });
                })
                .cors(cors -> {
                    if (allowedOrigin.isPresent()) {
                        cors.configurationSource(request -> {
                            var corsConfiguration = new org.springframework.web.cors.CorsConfiguration();
                            corsConfiguration.setAllowedOrigins(List.of(allowedOrigin.get()));
                            corsConfiguration.setAllowedMethods(List.of(HttpMethod.GET.name(), HttpMethod.POST.name(), HttpMethod.PUT.name(), HttpMethod.DELETE.name()));
                            corsConfiguration.setAllowedHeaders(List.of("*"));
                            return corsConfiguration;
                        });
                    } else {
                        log.warn("No allowed origin defined");
                    }
                })

                .authorizeRequests(authorize -> {
                    authorize.anyRequest().permitAll();
                });

        return http.build();
    }
}
