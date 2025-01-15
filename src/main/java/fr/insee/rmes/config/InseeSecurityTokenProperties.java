package fr.insee.rmes.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
        prefix = "fr.insee.rmes.security.token"
)
public record InseeSecurityTokenProperties(
        //Chemin pour récupérer la liste des rôles dans le jwt (token)
        String oidcClaimRole,
        //Chemin pour récupérer le username dans le jwt (token)
        String oidcClaimUsername) {
}
