package fr.insee.rmes.config;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import org.junit.jupiter.api.Test;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.MappedJwtClaimSetConverter;

import java.text.ParseException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityConfigTest {

    @Test
    void jwtGrantedAuthoritiesConverterTest() throws ParseException {
        //Chemin pour récupérer la liste des rôles dans le jwt (token)
        String oidcClaimRole = "";
        //Chemin pour récupérer le username dans le jwt (token)
        String oidcClaimUsername = "";
        String token = "<KEY>";
        GrantedAuthority[] expectedGrantedAuthorities={};
        Jwt jwt = createJwt(token, JWTParser.parse(token));
        SecurityConfig securityConfig = new SecurityConfig(new InseeSecurityTokenProperties(oidcClaimRole, oidcClaimUsername), null, null, null);
        Converter<Jwt, Collection<GrantedAuthority>> converter = securityConfig.jwtGrantedAuthoritiesConverter();

        assertThat(converter.convert(jwt)).containsExactly(expectedGrantedAuthorities);

    }

    private Jwt createJwt(String token, JWT parsedJwt) throws ParseException {
        JWTClaimsSet jwtClaimsSet = parsedJwt.getJWTClaimsSet();
        Map<String, Object> headers = new LinkedHashMap<>(parsedJwt.getHeader().toJSONObject());
        Map<String, Object> claims = MappedJwtClaimSetConverter.withDefaults(Collections.emptyMap()).convert(jwtClaimsSet.getClaims());
        return Jwt.withTokenValue(token)
                .headers((h) -> h.putAll(headers))
                .claims((c) -> c.putAll(claims))
                .build();
    }

}