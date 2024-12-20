package fr.insee.rmes.tocolecticaapi.service;

import fr.insee.rmes.exceptions.RmesExceptionIO;
import fr.insee.rmes.model.DDIItemType;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.net.URI;

import static fr.insee.rmes.transfoxsl.utils.RestClientUtils.readBodySafely;

@Component
public record ElasticService(@Value("${fr.insee.rmes.elasticsearch.url}") String elasticUrl,
                             @Value("${fr.insee.rmes.elasticsearch.apikey}") String elasticApiKey,
                             RestClient elasticClient
                             ) {

    private static final String APIKEYHEADER = "apiKey ";
    private static final String SEARCH = "/_search";

    private RestClient initRestClient() {
        return RestClient.builder()
                .defaultHeader(HttpHeaders.AUTHORIZATION, APIKEYHEADER+elasticApiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .baseUrl(URI.create(this.elasticUrl+"/"))
                .build();
    }

    public String search(@NonNull String index, @NonNull String texte) {
        return elasticClient.get()
                .uri(index + "/_search?q=*" + texte + "*")
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new RmesExceptionIO(response.getStatusCode().value(), "Bad request or inexisting resource", readBodySafely(response));
                })
                .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                    throw new RmesExceptionIO(response.getStatusCode().value(), "Error while searching into Colectica", readBodySafely(response));
                })
                .body(String.class);
    }

    private String postSearch(String index, String postedSearchQuery) {
        return elasticClient.post()
                .uri("/" + index + SEARCH)
                .body(postedSearchQuery)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new RmesExceptionIO(response.getStatusCode().value(), "Bad request or inexisting resource", readBodySafely(response));
                })
                .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                    throw new RmesExceptionIO(response.getStatusCode().value(), "Error while searching into Colectica", readBodySafely(response));
                })
                .body(String.class);
    }

    public String searchType(@NonNull String index, @NonNull DDIItemType type) {
        String typeUuid = String.valueOf(type.getUUID()).toLowerCase();
        return postSearch(index, "{\"query\": {\"match\": {\"itemType\":\"" + typeUuid + "\"}}, \"_source\": true, \"size\": 10000, \"from\": 0}");
    }

    public String searchTextByType(String index, String texte, DDIItemType type) {

        String typeUuid = String.valueOf(type.getUUID()).toLowerCase();
        return postSearch(index, "{\n" +
                "  \"query\": {\n" +
                "    \"bool\": {\n" +
                "      \"must\": [\n" +
                "        { \"match\": { \"itemType\":\"" + typeUuid + "\" }},\n" +
                "        { \"query_string\": {\n" +
                "            \"query\": \"*" + texte + "*\",\n" +
                "            \"fields\": [\"*\"]\n" +
                "          }\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  }\n" +
                "}");
    }

}
