package fr.insee.rmes.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;


import java.util.Optional;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig{

    @Value("${fr.insee.rmes.metadata.cors.allowedOrigin}")
    private Optional<String> allowedOrigin;

    private static final String[] SWAGGER_WHITELIST = {
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
    };

    @Bean
    protected SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.cors(withDefaults())
                .authorizeRequests().anyRequest().permitAll();
//        http.authorizeHttpRequests(authorize ->
//                authorize.requestMatchers(SWAGGER_WHITELIST).permitAll()
//        );


        return http.build();
    }



}
