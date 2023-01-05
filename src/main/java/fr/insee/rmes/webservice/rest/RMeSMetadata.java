package fr.insee.rmes.webservice.rest;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import fr.insee.rmes.metadata.model.ColecticaItem;
import fr.insee.rmes.metadata.model.ColecticaItemRefList;
import fr.insee.rmes.metadata.model.RelationshipOut;
import fr.insee.rmes.metadata.model.Unit;
import fr.insee.rmes.metadata.service.MetadataService;
import fr.insee.rmes.metadata.service.MetadataServiceItem;
import fr.insee.rmes.metadata.service.ddiinstance.DDIInstanceService;
import fr.insee.rmes.metadata.service.fragmentInstance.FragmentInstanceService;
import fr.insee.rmes.search.model.DDIItemType;

/**
 * Main WebService class of the MetaData service
 *
 */
@Path("/meta-data")
//@Api(value = "DDI MetaData API")
@OpenAPIDefinition(info = @Info(description = "DDI MetaData API"))
public class RMeSMetadata {

	final static Logger log = LogManager.getLogger(RMeSMetadata.class);

	@Autowired
	MetadataService metadataService;

	@Autowired
	DDIInstanceService ddiInstanceService;
	
	@Autowired
	MetadataServiceItem metadataServiceItem;

	@Autowired
	FragmentInstanceService fragmentInstanceService;

	@GET
	@Path("colectica-item/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(summary = "Get the item with id {id}", description = "Get an item from Colectica Repository, given it's {id}")
	public Response getItem(@PathParam(value = "id") String id) throws Exception {
		try {
			ColecticaItem item = metadataServiceItem.getItem(id);
			return Response.ok().entity(item).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}

	@GET
	@Path("colectica-item/{id}/refs/")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(summary = "Get the colectica item children refs with parent id {id}", description = "This will give a list of object containing a reference id, version and agency. Note that you will"
			+ "need to map response objects keys to be able to use it for querying items "
			+ "(see /items doc model)")
	public Response getChildrenRef(@PathParam(value = "id") String id) throws Exception {
		try {
			ColecticaItemRefList refs = metadataServiceItem.getChildrenRef(id);
			return Response.ok().entity(refs).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}
	
	@GET
	@Path("colectica-items/{itemType}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Operation(summary = "Get all referenced items of a certain type", description = "Retrieve a list of ColecticaItem of the type defined")
	public Response getItemsByType(@PathParam (value = "itemType") DDIItemType itemType)
			throws Exception {
		try {		
			List<ColecticaItem> children = metadataService.getItemsByType(itemType);
			return Response.ok().entity(children).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}
	
	@GET
	@Path("colectica-item/{id}/toplevel-refs/")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(summary = "Get the colectica item toplevel parents refs with item id {id}", description = "This will give a list of object containing a triple identifier (reference id, version and agency) and the itemtype. Note that you will"
			+ "need to map response objects keys to be able to use it for querying items "
			+ "(see /items doc model)")
	public Response gettopLevelRefs(@PathParam(value = "id") String id) throws Exception {
		try {
			List<RelationshipOut> refs = metadataServiceItem.getTopLevelRefs(id);
			return Response.ok().entity(refs).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}
	
	@GET
	@Path("variables/{idQuestion}/ddi")
	@Produces(MediaType.APPLICATION_XML)
	@Operation(summary = "Get the variables that references a specific question")
	public Response getVariablesFromQuestion(@PathParam(value = "idQuestion") String idQuestion,
			@QueryParam(value="agency") String agency,
			@QueryParam(value="version") String version) throws Exception {
		Map<String,String> params = new HashMap<String,String>();
		params.put("idQuestion",idQuestion);
		params.put("agency",agency);
		params.put("version",version);
		try {
			String ddiDocument = metadataService.getVariablesFromQuestionId(params);
			StreamingOutput stream = output -> {
				try {
					output.write(ddiDocument.getBytes(StandardCharsets.UTF_8));
				} catch (Exception e) {
					throw new RMeSException(500, "Transformation error", e.getMessage());
				}
			};
			return Response.ok(stream).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}

	@GET
	@Path("units")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(summary = "Get units measure", description = "This will give a list of objects containing the uri and the label for all units")
	public Response getUnits() throws Exception {
		try {
			List<Unit> units = metadataService.getUnits();
			return Response.ok().entity(units).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}

	@POST
	@Path("colectica-items")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Operation(summary = "Get all de-referenced items", description = "Maps a list of ColecticaItemRef given as a payload to a list of actual full ColecticaItem objects")
	public Response getItems(
			@Parameter(description = "Item references query object", required = true) ColecticaItemRefList query)
			throws Exception {
		try {
			List<ColecticaItem> children = metadataServiceItem.getItems(query);
			return Response.ok().entity(children).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}

	@GET
	@Path("fragmentInstance/{id}/ddi")
	@Produces(MediaType.APPLICATION_XML)
	@Operation(summary = "Get DDI document", description = "Get a DDI document from Colectica repository including an item thanks to its {id} and its children as fragments.")
	public Response getDDIDocumentFragmentInstance(@PathParam(value = "id") String id,
			@QueryParam(value="withChild") boolean withChild) throws Exception {
		try {
			String ddiDocument = fragmentInstanceService.getFragmentInstance(id, null, withChild);
			StreamingOutput stream = output -> {
				try {
					output.write(ddiDocument.getBytes(StandardCharsets.UTF_8));
				} catch (Exception e) {
					throw new RMeSException(500, "Transformation error", e.getMessage());
				}
			};
			return Response.ok(stream).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}

	@GET
	@Path("ddi-instance/{id}/ddi")
	@Produces(MediaType.APPLICATION_XML)
	@Operation(summary = "Get DDI document of a DDI instance", description = "Get a DDI document of a DDI Instance from Colectica repository reference {id}")
	public Response getDDIInstance(@PathParam(value = "id") String id) throws Exception {

		try {
			String questionnaire = ddiInstanceService.getDDIInstance(id);
			StreamingOutput stream = stringToStream(questionnaire);
			return Response.ok(stream).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}
	
	private StreamingOutput stringToStream(String string) {
		StreamingOutput stream = output -> {
			try {
				output.write(string.getBytes(StandardCharsets.UTF_8));
			} catch (Exception e) {
				throw new RMeSException(500, "Transformation error", e.getMessage());
			}
		};
		return stream;
	}

}