package fr.insee.rmes.config.keycloak;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Token {
    @JsonProperty("access_token")
    String accessToken;

    private String tokentype;

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokentype;
    }
}
