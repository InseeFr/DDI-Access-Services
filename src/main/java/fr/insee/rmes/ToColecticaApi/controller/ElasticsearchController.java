package fr.insee.rmes.tocolecticaapi.controller;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import co.elastic.clients.elasticsearch.core.search.TotalHitsRelation;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.rmes.search.model.DDIItemType;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.websocket.server.PathParam;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@Hidden
public class ElasticsearchController {
    private static final String HTTPS = "https://";
    private static final String HTTP = "http://";
    private static final String AUTHORIZATION = "Authorization";
    private static final String KBN = "kbn-xsrf";
    private static final String REPORTING = "reporting";
    private static final String ERREUR_ES = "Une erreur s'est produite lors de la requête Elasticsearch.";
    private static final String SEARCH = "/_search";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLI_JSON = "application/json";
    private static final String APIKEYHEADER = "apiKey ";
    static final Logger logger = LogManager.getLogger(ElasticsearchController.class);

    @Value("${fr.insee.rmes.elasticsearch.host}")
    private String  elasticHost;

    @Value("${fr.insee.rmes.elasticsearch.port}")
    private int  elasticHostPort;

    @Value("${fr.insee.rmes.elasticsearch.apiId}")
    private String apiId;

    @Value("${fr.insee.rmes.elasticsearch.apikey}")
    private String apiKey;

    private final ElasticsearchClient elasticsearchClient;


    @Autowired
    public ElasticsearchController(ElasticsearchClient elasticsearchClient) {
        this.elasticsearchClient = elasticsearchClient;
    }

    @GetMapping("/search/elastic/{index}/field")
    public ResponseEntity<String> searchField(@PathVariable("index") String index) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String apiUrl;

            // Choose the appropriate URL based on the elasticHost value
            if (elasticHost.contains("kube")) {
                apiUrl = HTTPS + elasticHost + ":" + elasticHostPort + "/" + index + "/?pretty";
            } else {
                apiUrl = HTTP + elasticHost + ":" + elasticHostPort + "/" + index + "/?pretty";
            }

            HttpGet httpGet = new HttpGet(apiUrl);

            if (!elasticHost.contains("kube")) {
                // Add Basic Authentication header if not using Kubernetes
                httpGet.addHeader(AUTHORIZATION, APIKEYHEADER + apiKey);
            }

            httpGet.addHeader(KBN, REPORTING);

            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                int statusCode = response.getStatusLine().getStatusCode();
                String responseBody = EntityUtils.toString(response.getEntity());

                if (statusCode == 200) {
                    return ResponseEntity.ok(responseBody);
                } else {
                    return ResponseEntity.status(statusCode).body("Elasticsearch returned an unexpected status code: " + statusCode);
                }
            }
        } catch (IOException e) {
            return ResponseEntity.status(500).body("An error occurred while making the Elasticsearch request.");
        }
    }

    @GetMapping("/search/elastic/_search/{index}/{texte}")
    public  ResponseEntity<String> searchText(
            @Parameter(
                    description = "nom de l'index (portal ou colectica)",
                    required = true,
                    schema = @Schema(
                            type = "string", example = "portal_registered_item")) @PathParam("index") String index,
            @Parameter(
                    description = "mot recherché",
                    required = true,
                    schema = @Schema(
                            type = "string", example = "voyage")) @PathParam("texte") String texte) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet;

            if (elasticHost.contains("kube")) {
                httpGet = new HttpGet(HTTPS + elasticHost + ":" + elasticHostPort + "/" + index + "/_search?q="+ texte);
            
            }
            else {
                httpGet = new HttpGet(HTTP + elasticHost + ":" + elasticHostPort + "/" + index + "/_search?q=" + texte);
                httpGet.addHeader(AUTHORIZATION, APIKEYHEADER + apiKey);
            }

            
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                String responseBody = EntityUtils.toString(response.getEntity());

                return ResponseEntity.ok(responseBody);
            }
        } catch (IOException e) {
            return ResponseEntity.status(500).body(ERREUR_ES);
        }
    }

    @PostMapping("/search/elastic/matchAll")
    public ResponseEntity<String> searchElastic() {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost();

            if (elasticHost.contains("kube")) {
                httpPost = new HttpPost(HTTPS + elasticHost + ":" + elasticHostPort + SEARCH);
            }
            else {
                httpPost = new HttpPost(HTTP + elasticHost + ":" + elasticHostPort + SEARCH);
                httpPost.setHeader(AUTHORIZATION, APIKEYHEADER + apiKey);
            }
            httpPost.addHeader(KBN, REPORTING);
            httpPost.addHeader(CONTENT_TYPE, APPLI_JSON);
            String requestBody = "{ \"query\": { \"match_all\": {} }, \"size\":10000 }";
            httpPost.setEntity(new StringEntity(requestBody));

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                String responseBody = EntityUtils.toString(response.getEntity());

                return ResponseEntity.ok(responseBody);
            }
        } catch (IOException e) {
            return ResponseEntity.status(500).body(ERREUR_ES);
        }
    }

    @PostMapping("/search/elastic/matchType/{type}")
    public ResponseEntity<String> byType(
            @PathVariable ("type") DDIItemType type
    ) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost();

            if (elasticHost.contains("kube")) {
                httpPost = new HttpPost(HTTPS + elasticHost + ":" + elasticHostPort + SEARCH);
            }
            else {
                httpPost = new HttpPost(HTTP + elasticHost + ":" + elasticHostPort + SEARCH);
                httpPost.setHeader(AUTHORIZATION, APIKEYHEADER + apiKey);
            }

            httpPost.addHeader(KBN, REPORTING);
            httpPost.setHeader(CONTENT_TYPE, APPLI_JSON);
            String requestBody = "{ \"query\": { \"match\": {\"itemType\":\""+ type.getUUID().toLowerCase() +"\"} }, \"size\":10000 }";
            httpPost.setEntity(new StringEntity(requestBody));

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                String responseBody = EntityUtils.toString(response.getEntity());

                return ResponseEntity.ok(responseBody);
            }
        } catch (IOException e) {
            return ResponseEntity.status(500).body(ERREUR_ES);
        }
    }


@PostMapping("/search/elastic/{field}/{texte}/search")
    public ResponseEntity<String> searchElastic(
            @PathVariable String texte,
            @PathVariable String field
            ) {
       try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

           HttpPost httpPost = new HttpPost();

           if (elasticHost.contains("kube")) {
               httpPost = new HttpPost(HTTPS + elasticHost + ":" + elasticHostPort + SEARCH);
           }
           else {
               httpPost = new HttpPost(HTTP + elasticHost + ":" + elasticHostPort + SEARCH);
               httpPost.setHeader(AUTHORIZATION, APIKEYHEADER + apiKey);
           }


                httpPost.addHeader(KBN, REPORTING);
                httpPost.setHeader(CONTENT_TYPE, APPLI_JSON);
                String requestBody = "{ \"query\": { \"match\": {\""+field+"\":\"" + texte + "\"} }, \"size\":10000 }";
                httpPost.setEntity(new StringEntity(requestBody));

                try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                    String responseBody = EntityUtils.toString(response.getEntity());

                    return ResponseEntity.ok(responseBody);
                }
            } catch (IOException e) {
                return ResponseEntity.status(500).body(ERREUR_ES);
            }



    }

    @PostMapping("/search/elastic/{date}")
    public ResponseEntity<?> searchElasticDate(
            @PathVariable String date) {
        try {

            RestClient restClient = RestClient.builder(
                    new HttpHost(elasticHost, elasticHostPort)).build();

// Creation du mapper
            ElasticsearchTransport transport = new RestClientTransport(
                    restClient, new JacksonJsonpMapper());

// Création du client API
            ElasticsearchClient client = new ElasticsearchClient(transport);

            SearchResponse<ObjectNode> search = client.search(s -> s
                            .index("colectica_registered_item")
                            .query(q -> q
                                    .range(t -> t
                                            .field("versionDate")
                                            .gte(JsonData.fromJson(date))
                                    )),
                    ObjectNode.class);

            TotalHits total = search.hits().total();
            boolean isExactResult = total.relation() == TotalHitsRelation.Eq;

            if (isExactResult) {
                String resultTot = "There are " + total.value() + " results";
                logger.info(resultTot);
            } else {
                String resultTotPlus ="There are more than " + total.value() + " results";
                logger.info(resultTotPlus);
            }

            List<Hit<ObjectNode>> hits = search.hits().hits();
            ArrayList<String> sortie = new ArrayList<>();
            for (Hit<ObjectNode> hit : search.hits().hits()) {
                ObjectNode result = hit.source();
                sortie.add(result.toString());
                String resultScore="Found product " + result + ", score " + hit.score();
                logger.info(resultScore);

            }

            return ResponseEntity.ok(sortie);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de l'exécution de la requête Elasticsearch : " + e.getMessage());
        }
    }
}