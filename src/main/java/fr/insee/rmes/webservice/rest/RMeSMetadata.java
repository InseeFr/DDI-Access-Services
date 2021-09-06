package fr.insee.rmes.webservice.rest;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import fr.insee.rmes.metadata.model.ColecticaItem;
import fr.insee.rmes.metadata.model.ColecticaItemRef;
import fr.insee.rmes.metadata.model.ColecticaItemRefList;
import fr.insee.rmes.metadata.model.RelationshipOut;
import fr.insee.rmes.metadata.model.Unit;
import fr.insee.rmes.metadata.service.MetadataService;
import fr.insee.rmes.metadata.service.MetadataServiceItem;
import fr.insee.rmes.metadata.service.codeList.CodeListService;
import fr.insee.rmes.metadata.service.ddiinstance.DDIInstanceService;
import fr.insee.rmes.metadata.service.fragmentInstance.FragmentInstanceService;
import fr.insee.rmes.metadata.service.groups.GroupsService;
import fr.insee.rmes.metadata.service.questionnaire.QuestionnaireService;
import fr.insee.rmes.metadata.service.variableBook.VariableBookService;
import fr.insee.rmes.search.model.DDIItemType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * Main WebService class of the MetaData service
 *
 */
@Path("/meta-data")
@Api(value = "DDI MetaData API")
public class RMeSMetadata {

	final static Logger logger = LogManager.getLogger(RMeSMetadata.class);

	@Autowired
	MetadataService metadataService;

	@Autowired
	QuestionnaireService questionnaireService;

	@Autowired
	DDIInstanceService ddiInstanceService;

	@Autowired
	CodeListService codeListService;

	@Autowired
	MetadataServiceItem metadataServiceItem;

	@Autowired
	VariableBookService variableBookServiceItem;

	@Autowired
	GroupsService groupService;

	@Autowired
	FragmentInstanceService fragmentInstanceService;

	@GET
	@Path("colectica-item/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Get the item with id {id}", notes = "Get an item from Colectica Repository, given it's {id}", response = ColecticaItem.class)
	public Response getItem(@PathParam(value = "id") String id) throws Exception {
		try {
			ColecticaItem item = metadataServiceItem.getItem(id);
			return Response.ok().entity(item).build();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

	@GET
	@Path("colectica-item/{id}/refs/")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Get the colectica item children refs with parent id {id}", notes = "This will give a list of object containing a reference id, version and agency. Note that you will"
			+ "need to map response objects keys to be able to use it for querying items "
			+ "(see /items doc model)", response = ColecticaItemRefList.class)
	public Response getChildrenRef(@PathParam(value = "id") String id) throws Exception {
		try {
			ColecticaItemRefList refs = metadataServiceItem.getChildrenRef(id);
			return Response.ok().entity(refs).build();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}
	
	@GET
	@Path("colectica-items/{itemType}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Get all referenced items of a certain type", notes = "Retrieve a list of ColecticaItem of the type defined", response = ColecticaItem.class, responseContainer = "List")
	public Response getItemsByType(@PathParam (value = "itemType") DDIItemType itemType)
			throws Exception {
		try {		
			List<ColecticaItem> children = metadataService.getItemsByType(itemType);
			return Response.ok().entity(children).build();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}
	
	@GET
	@Path("colectica-item/{id}/toplevel-refs/")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Get the colectica item toplevel parents refs with item id {id}", notes = "This will give a list of object containing a triple identifier (reference id, version and agency) and the itemtype. Note that you will"
			+ "need to map response objects keys to be able to use it for querying items "
			+ "(see /items doc model)", response = RelationshipOut[].class)
	public Response gettopLevelRefs(@PathParam(value = "id") String id) throws Exception {
		try {
			List<RelationshipOut> refs = metadataServiceItem.getTopLevelRefs(id);
			return Response.ok().entity(refs).build();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}
	
	@GET
	@Path("variables/{idQuestion}/ddi")
	@Produces(MediaType.APPLICATION_XML)
	@ApiOperation(value = "Get the variables that references a specific question", response = String.class)
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
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

	@GET
	@Path("units")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Get units measure", notes = "This will give a list of objects containing the uri and the label for all units", response = Unit.class, responseContainer = "List")
	public Response getUnits() throws Exception {
		try {
			List<Unit> units = metadataService.getUnits();
			return Response.ok().entity(units).build();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

	@POST
	@Path("colectica-items")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Get all de-referenced items", notes = "Maps a list of ColecticaItemRef given as a payload to a list of actual full ColecticaItem objects", response = ColecticaItem.class, responseContainer = "List")
	public Response getItems(
			@ApiParam(value = "Item references query object", required = true) ColecticaItemRefList query)
			throws Exception {
		try {
			List<ColecticaItem> children = metadataServiceItem.getItems(query);
			return Response.ok().entity(children).build();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

	@GET
	@Path("fragmentInstance/{id}/ddi")
	@Produces(MediaType.APPLICATION_XML)
	@ApiOperation(value = "Get DDI document", notes = "Get a DDI document from Colectica repository including an item thanks to its {id} and its children as fragments.", response = String.class)
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
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

	@GET
	@Path("ddi-instance/{id}/ddi")
	@Produces(MediaType.APPLICATION_XML)
	@ApiOperation(value = "Get DDI document of a DDI instance", notes = "Get a DDI document of a DDI Instance from Colectica repository reference {id}", response = String.class)
	public Response getDDIInstance(@PathParam(value = "id") String id) throws Exception {

		try {
			String questionnaire = ddiInstanceService.getDDIInstance(id);
			StreamingOutput stream = stringToStream(questionnaire);
			return Response.ok(stream).build();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();
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