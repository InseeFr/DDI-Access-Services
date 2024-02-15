package fr.insee.rmes.metadata.client;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.http.entity.ContentType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import fr.insee.rmes.config.keycloak.KeycloakServices;
import fr.insee.rmes.metadata.exceptions.ExceptionColecticaUnreachable;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.rmes.metadata.model.ColecticaItem;
import fr.insee.rmes.metadata.model.ColecticaItemPostRef;
import fr.insee.rmes.metadata.model.ColecticaItemPostRefList;
import fr.insee.rmes.metadata.model.ColecticaItemRef;
import fr.insee.rmes.metadata.model.ColecticaItemRefList;
import fr.insee.rmes.metadata.model.ColecticaSearchItemRequest;
import fr.insee.rmes.metadata.model.ColecticaSearchItemResponse;
import fr.insee.rmes.metadata.model.ColecticaSearchSetRequest;
import fr.insee.rmes.metadata.model.Relationship;
import fr.insee.rmes.metadata.model.ObjectColecticaPost;
import fr.insee.rmes.metadata.model.Unit;
import lombok.NonNull;

@Service
public class MetadataClientImpl implements MetadataClient {

	private static final Logger log = LogManager.getLogger(MetadataClientImpl.class);

	@Autowired
	private RestTemplate restTemplate;

	
	@NonNull
	@Autowired
	private KeycloakServices kc;

	private String token;

	@Value("${fr.insee.rmes.api.remote.metadata.url}")
	String serviceUrl;

	@Value("${fr.insee.rmes.api.remote.metadata.agency}")
	String agency;

	@Value("${fr.insee.rmes.api.remote.metadata.key}")
	String apiKey;
	
	private static final String CONTENT_TYPE = "Content-type";
	private static final String AUTHORIZATION = "Authorization";
	private static final String AUTHORIZATION_TYPE = "Bearer ";
	
	/**
     * Gets a new token keycloak if expired.
     * @throws ExceptionColecticaUnreachable 
     */
    public String getFreshToken() throws ExceptionColecticaUnreachable, JsonProcessingException {
        if ( ! kc.isTokenValid(token)) {
            token = getToken();
        }
        return token;
    }

    /**
     * Gets a token keycloak
     * @return token
     * @throws ExceptionColecticaUnreachable 
     */
    public String getToken() throws ExceptionColecticaUnreachable, JsonProcessingException {
        return kc.getKeycloakAccessToken();

    }

    /**
     * Gets an item with its Colectica id
     */
	public ColecticaItem getItem(String id) throws Exception {
		String url = String.format("%s/api/v1/item/%s/%s", serviceUrl, agency, id);
		log.info("GET Item on " + id);
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		headers.add(CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
		headers.add(AUTHORIZATION, AUTHORIZATION_TYPE + getFreshToken());
		HttpEntity<ColecticaItem> request = new HttpEntity<>(headers);
		return restTemplate.exchange(url, HttpMethod.GET, request, ColecticaItem.class).getBody();
	}

	/**
	 * Retrieves multiple items with their triplets of identifiers
	 */
	public List<ColecticaItem> getItems(ColecticaItemRefList query) throws Exception {
		String url = String.format("%s/api/v1/item/_getList", serviceUrl);
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
		headers.add(CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
		headers.add(AUTHORIZATION, AUTHORIZATION_TYPE + getFreshToken());
		HttpEntity<ColecticaItemRefList> request = new HttpEntity<>(query, headers);
		ResponseEntity<ColecticaItem[]> response = restTemplate.exchange(url, HttpMethod.POST, request,
				ColecticaItem[].class);
		log.info("GET Items with query : " + query.toString());
		return Arrays.asList(response.getBody());
	}
	
	/**
	 * Searches in the repository for items, according to the provided search options.
	 * @param req
	 * @return 
	 * @throws ExceptionColecticaUnreachable 
	 */
	@Override
	public ColecticaSearchItemResponse searchItems(ColecticaSearchItemRequest req) throws ExceptionColecticaUnreachable, JsonProcessingException {
		String url = String.format("%s/api/v1/_query", serviceUrl);
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
		headers.add(CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
		headers.add(AUTHORIZATION, AUTHORIZATION_TYPE + getFreshToken());
		HttpEntity<ColecticaSearchItemRequest> request = new HttpEntity<>(req, headers);
//		ResponseEntity<ColecticaSearchItemResponse> response = restTemplate.exchange(url, HttpMethod.POST, request, ColecticaSearchItemResponse.class);
		log.info("GET Items with query : " + req.toString());
		return  restTemplate.exchange(url, HttpMethod.POST, request, ColecticaSearchItemResponse.class).getBody();
	}
	
	/**/
	@Override
	public Relationship[] searchSets(ColecticaSearchSetRequest setRequest) {
		String url = String.format("%s/api/v1/_query/set", serviceUrl);
		RestTemplate restTemplate = new RestTemplate();
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
		headers.add(CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
		headers.add(AUTHORIZATION, AUTHORIZATION_TYPE + token);
		HttpEntity<ColecticaSearchSetRequest> request = new HttpEntity<>(setRequest, headers);
		ResponseEntity<Relationship[]> response = restTemplate.exchange(url, HttpMethod.POST, request,
				Relationship[].class);
		return response.getBody();
	}

	/**
	 * Get the children's identifiers of an item with its Colectica id
	 */
	public ColecticaItemRefList getChildrenRef(String id) throws Exception {
		String url = String.format("%s/api/v1/set/%s/%s", serviceUrl, agency, id);
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
		headers.add(CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
		headers.add(AUTHORIZATION, AUTHORIZATION_TYPE + getFreshToken());
		HttpEntity<String> request = new HttpEntity<>(headers);
		ResponseEntity<ColecticaItemRef.Unformatted[]> response;
		response = restTemplate.exchange(url, HttpMethod.GET, request, ColecticaItemRef.Unformatted[].class);
		List<ColecticaItemRef> refs = Arrays.asList(response.getBody()).stream()
				.map(unformatted -> unformatted.format()).collect(Collectors.toList());
		log.info("Get ChildrenRef for id : " + id);
		return new ColecticaItemRefList(refs);
	}

	/**
	 * Get the number of the latest version of an item with its Colectica id
	 */
	public Integer getLastestVersionItem(String id) throws Exception {
		String url = String.format("%s/api/v1/item/%s/%s/versions/_latest", serviceUrl, agency, id);
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		headers.add(CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
		headers.add(AUTHORIZATION, AUTHORIZATION_TYPE + getFreshToken());
		HttpEntity<ColecticaItem> request = new HttpEntity<>(headers);
		log.info("GET LastestVersion for Item " + id);
		return restTemplate.exchange(url, HttpMethod.GET, request, Integer.class).getBody();

	}

	@Override
	public List<Unit> getUnits() throws Exception {
		//Units are not retrieved from a repository but from a file in resources folder
		//Change this method when units are available in repository
		final ObjectMapper objectMapper = new ObjectMapper();
        URL resource = getClass().getClassLoader().getResource("measure-units.json");
		return objectMapper.readValue(
				new File(resource.toURI()), 
		        new TypeReference<List<Unit>>(){});
	}

	@Override
	public String postItems(ColecticaItemPostRefList colecticaItemsList) throws Exception {
		String url = String.format("%s/api/v1/item", serviceUrl);

		MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
		headers.add(CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
		headers.add(AUTHORIZATION, AUTHORIZATION_TYPE + getFreshToken());
		HttpEntity<ColecticaItemPostRefList> request = new HttpEntity<>(colecticaItemsList, headers);
		ResponseEntity<ColecticaItem[]> response = restTemplate.exchange(url, HttpMethod.POST, request,
				ColecticaItem[].class);
		return response.getStatusCode().toString();
	}

	@Override
	public String postItem(ColecticaItemPostRef ref) throws ExceptionColecticaUnreachable, JsonProcessingException {

		List<ColecticaItemPostRef> items = new ArrayList<ColecticaItemPostRef>();
		ColecticaItemPostRefList colecticaItemsList = new ColecticaItemPostRefList();
		colecticaItemsList.setItems(items);
		String url = String.format("%s/api/v1/item", serviceUrl);
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
		headers.add(CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
		headers.add(AUTHORIZATION, AUTHORIZATION_TYPE + getFreshToken());
		HttpEntity<ColecticaItemPostRefList> request = new HttpEntity<>(colecticaItemsList, headers);
		ResponseEntity<ColecticaItem[]> response = restTemplate.exchange(url, HttpMethod.POST, request,
				ColecticaItem[].class);
		return response.getStatusCode().toString();

	}

	@Override
	public Relationship[] getRelationship(ObjectColecticaPost objectColecticaPost) throws ExceptionColecticaUnreachable, JsonProcessingException {
		String url = String.format("%s/api/v1/_query/relationship/byobject", serviceUrl);

		MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
		headers.add(CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
		headers.add(AUTHORIZATION, AUTHORIZATION_TYPE + getFreshToken());
		HttpEntity<ObjectColecticaPost> request = new HttpEntity<>(objectColecticaPost, headers);
		ResponseEntity<Relationship[]> response = restTemplate.exchange(url, HttpMethod.POST, request,
				Relationship[].class);
		return response.getBody();
	}

	@Override
	public Relationship[] getRelationshipChildren(ObjectColecticaPost objectColecticaPost) throws ExceptionColecticaUnreachable, JsonProcessingException {
		String url = String.format("%s/api/v1/_query/relationship/bysubject", serviceUrl);

		MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
		headers.add(CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
		headers.add(AUTHORIZATION, AUTHORIZATION_TYPE + getFreshToken());
		HttpEntity<ObjectColecticaPost> request = new HttpEntity<>(objectColecticaPost, headers);
		ResponseEntity<Relationship[]> response = restTemplate.exchange(url, HttpMethod.POST, request,
				Relationship[].class);
		return response.getBody();
	}
	
	/**
	 * Call Colectica API to retrieve objects of a defined type that are referencing a specific item identified with its triplet
	 * 
	 */
	@Override
	public Relationship[] getItemsReferencingSpecificItem(ObjectColecticaPost objectColecticaPost) throws ExceptionColecticaUnreachable, JsonProcessingException {
		String url = String.format("%s/api/v1/_query/relationship/byobject", serviceUrl);

		MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
		headers.add(CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
		headers.add(AUTHORIZATION, AUTHORIZATION_TYPE + getFreshToken());
		HttpEntity<ObjectColecticaPost> request = new HttpEntity<>(objectColecticaPost, headers);
		ResponseEntity<Relationship[]> response = restTemplate.exchange(url, HttpMethod.POST, request,
				Relationship[].class);
		return response.getBody();
	}

}
