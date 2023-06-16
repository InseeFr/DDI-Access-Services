package fr.insee.rmes.search.controller;

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
public class ElasticsearchController {

    @Value("${fr.insee.rmes.elasticsearch.host}")
    private String  elasticHost;

    @Value("${fr.insee.rmes.elasticsearch.port}")
    private int  elasticHostPort;


    private final ElasticsearchClient elasticsearchClient;


    final static Logger logger = LogManager.getLogger(ElasticsearchController.class);

    @Autowired
    public ElasticsearchController(ElasticsearchClient elasticsearchClient) {
        this.elasticsearchClient = elasticsearchClient;
    }

    @GetMapping("/search/elastic/{index}/field")
    public ResponseEntity<?> searchField(
            @Parameter(
                    description = "nom de l'index",
                    required = true,
                    schema = @Schema(
                            type = "string", example="colectica_registered_item")) @PathParam("index") String index) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet("http://" + elasticHost + ":" + elasticHostPort +"/"+ index);
            httpGet.addHeader("kbn-xsrf", "reporting");

            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                String responseBody = EntityUtils.toString(response.getEntity());

                return ResponseEntity.ok(responseBody);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Une erreur s'est produite lors de la requête Elasticsearch.");
        }
    }

    @GetMapping("/search/elastic/_search/{index}/{texte}")
    public ResponseEntity<?> searchText(
            @Parameter(
                    description = "nom de l'index",
                    required = true,
                    schema = @Schema(
                            type = "string", example="colectica_registered_item")) @PathParam("index") String index,
            @Parameter(
                    description = "mot recherché",
                    required = true,
                    schema = @Schema(
                            type = "string", example="voyage")) @PathParam("texte") String texte) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet("http://" + elasticHost + ":" + elasticHostPort +"/"+index+"/_search?q="+ texte);
            httpGet.addHeader("kbn-xsrf", "reporting");

            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                String responseBody = EntityUtils.toString(response.getEntity());

                return ResponseEntity.ok(responseBody);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Une erreur s'est produite lors de la requête Elasticsearch.");
        }
    }

    @PostMapping("/search/elastic/matchAll")
    public ResponseEntity<?> searchElastic() {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String url = "http://" + elasticHost + ":" + elasticHostPort + "/_search";
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader("kbn-xsrf", "reporting");
            httpPost.setHeader("Content-Type", "application/json");

            String requestBody = "{ \"query\": { \"match_all\": {} }, \"size\":10000 }";
            httpPost.setEntity(new StringEntity(requestBody));

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                String responseBody = EntityUtils.toString(response.getEntity());

                return ResponseEntity.ok(responseBody);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Une erreur s'est produite lors de la requête Elasticsearch.");
        }
    }

@PostMapping("/search/elastic/{field}/{texte}/search")
    public ResponseEntity<?> searchElastic(
            @PathVariable String texte,
            @PathVariable String field) {
        try {

// Creation du client attention port forward du container elastic pour que ça fonctionne en local
            RestClient restClient = RestClient.builder(
                    new HttpHost(elasticHost, elasticHostPort)).build();

// Creation du mapper
            ElasticsearchTransport transport = new RestClientTransport(
                    restClient, new JacksonJsonpMapper());

// Création du client API
            ElasticsearchClient client = new ElasticsearchClient(transport);
//Création de la requête
            SearchResponse<ObjectNode> search = client.search(s -> s
                            .index("colectica_registered_item")
                            .query(q -> q
                                    .match(t -> t
                                            .field(field)
                                            .query(texte)
                                    )),
                    ObjectNode.class);

            TotalHits total = search.hits().total();
            boolean isExactResult = total.relation() == TotalHitsRelation.Eq;

            if (isExactResult) {
                logger.info("There are " + total.value() + " results");
            } else {
                logger.info("There are more than " + total.value() + " results");
            }

            List<Hit<ObjectNode>> hits = search.hits().hits();
            ArrayList<String> sortie = new ArrayList<>();
            for (Hit<ObjectNode> hit : search.hits().hits()) {
                ObjectNode result = hit.source();
                sortie.add(result.toString());
                Double score = hit.score();
                sortie.add(String.valueOf(score));
                logger.info("Found product " + result+ ", score " + hit.score());

            }
            return ResponseEntity.ok(sortie);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new String[]{"Erreur lors de l'exécution de la requête Elasticsearch : " + e.getMessage()});
        }
    }

    @PostMapping("/search/elastic/{date}")
    public ResponseEntity<?> searchElasticDate(
            @PathVariable String date) {
        try {

// Creation du client attention port forward du container elastic pour que ça fonctionne en local
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
                logger.info("There are " + total.value() + " results");
            } else {
                logger.info("There are more than " + total.value() + " results");
            }

            List<Hit<ObjectNode>> hits = search.hits().hits();
            ArrayList<String> sortie = new ArrayList<>();
            for (Hit<ObjectNode> hit : search.hits().hits()) {
                ObjectNode result = hit.source();
                sortie.add(result.toString());
                logger.info("Found product " + result.toString() + ", score " + hit.score());

            }

            return ResponseEntity.ok(sortie);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de l'exécution de la requête Elasticsearch : " + e.getMessage());
        }
    }
}