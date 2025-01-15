package fr.insee.rmes.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig {

    private final InseeSecurityTokenProperties inseeSecurityTokenProperties;

    private final String administrateurRole;

    private final String gestionnaireColectica;

    private final String[] whiteList;

    private static final String ROLE_PREFIX = "ROLE_";

    public SecurityConfig(InseeSecurityTokenProperties inseeSecurityTokenProperties,  @Value("${fr.insee.rmes.role.administrateur}") String administrateurRole,
                          @Value("${fr.insee.rmes.role.gestionnaire.colectica}") String gestionnaireColectica,
                          @Value("#{'${fr.insee.rmes.security.whitelist-matchers}'.split(',')}")String[] whiteList) {
        this.inseeSecurityTokenProperties = inseeSecurityTokenProperties;
        this.administrateurRole = administrateurRole;
        this.gestionnaireColectica = gestionnaireColectica;
        this.whiteList = whiteList;
    }

    @Bean
    @Profile("prod")
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                //disable sessions (stateless)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        for (var pattern : whiteList) {
            http.authorizeHttpRequests(authorize ->
                    authorize
                            .requestMatchers(AntPathRequestMatcher.antMatcher(pattern)).permitAll()
            );
        }
        http.authorizeHttpRequests(authorize -> authorize
                // Permit all GET requests on any URL
                .requestMatchers(HttpMethod.GET, "/**").permitAll()
                // Require specific roles for POST, PUT, DELETE, PATCH methods on any URL
                .requestMatchers(HttpMethod.POST, "/**").hasAnyRole(administrateurRole, gestionnaireColectica)
                .requestMatchers(HttpMethod.PUT, "/**").hasAnyRole(administrateurRole, gestionnaireColectica)
                .requestMatchers(HttpMethod.DELETE, "/**").hasAnyRole(administrateurRole, gestionnaireColectica)
                .requestMatchers(HttpMethod.PATCH, "/**").hasAnyRole(administrateurRole, gestionnaireColectica)
                // Ensure all other requests are authenticated
                .anyRequest().authenticated());
        http
                .formLogin(AbstractAuthenticationFilterConfigurer::permitAll)
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));

        return http.build();
    }
    @Bean
    @Profile("dev")
    public SecurityFilterChain filterChainNoSecurity(HttpSecurity http) throws Exception {
        //Allow frames to be able tu use the H2 web console
        http.headers(headers -> headers
                .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin
                )
        );

        http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll())
                .formLogin(AbstractAuthenticationFilterConfigurer::permitAll)
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));
        return http.build();
    }
    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setPrincipalClaimName(inseeSecurityTokenProperties.oidcClaimUsername());
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter());
        return jwtAuthenticationConverter;
    }

    Converter<Jwt, Collection<GrantedAuthority>> jwtGrantedAuthoritiesConverter() {
        return source -> {

            String[] claimPath = inseeSecurityTokenProperties.oidcClaimRole().split("\\.");
            Map<String, Object> claims = source.getClaims();
            try {

                for (int i = 0; i < claimPath.length - 1; i++) {
                    claims = (Map<String, Object>) claims.get(claimPath[i]);
                }

                List<String> roles = (List<String>) claims.getOrDefault(claimPath[claimPath.length - 1], List.of());
                //if we need to add customs roles to every connected user we could define this variable (static or from properties)
                //roles.addAll(defaultRolesForUsers);
                return Collections.unmodifiableCollection(roles.stream().map(s -> new GrantedAuthority() {
                    @Override
                    public String getAuthority() {
                        return ROLE_PREFIX + s;
                    }

                    @Override
                    public String toString() {
                        return getAuthority();
                    }
                }).toList());
            } catch (ClassCastException e) {
                return List.of();
            }
        };
    }

}
