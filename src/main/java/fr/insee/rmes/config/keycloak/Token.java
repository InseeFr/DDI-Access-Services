package fr.insee.rmes.config.keycloak;

import com.fasterxml.jackson.annotation.JsonProperty;

record Token ( @JsonProperty("access_token")
               String accessToken){
}
