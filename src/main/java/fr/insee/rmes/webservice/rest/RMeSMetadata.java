package fr.insee.rmes.webservice.rest;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
import fr.insee.rmes.metadata.model.ColecticaItemPostRef;
import fr.insee.rmes.metadata.model.ColecticaItemPostRefList;
import fr.insee.rmes.metadata.model.ColecticaItemRefList;
import fr.insee.rmes.metadata.model.ColecticaPostRefDisplayed;
import fr.insee.rmes.metadata.model.Unit;
import fr.insee.rmes.metadata.service.MetadataService;
import fr.insee.rmes.metadata.service.MetadataServiceItem;
import fr.insee.rmes.metadata.service.codeList.CodeListService;
import fr.insee.rmes.utils.ddi.ItemFormat;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * Main WebService class of the MetaData service
 *
 * @author I6VWID
 */
@Path("/meta-data")
@Api(value = "DDI MetaData API")
public class RMeSMetadata {

	final static Logger logger = LogManager.getLogger(RMeSMetadata.class);

	@Autowired
	MetadataService metadataService;
	
	@Autowired
	CodeListService codeListService;
	
	@Autowired
	MetadataServiceItem metadataServiceItem;

	@GET
	@Path("item/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Gets the item with id {id}", notes = "Get an item from Colectica Repository, given it's {id}", response = ColecticaItem.class)
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
	@Path("item/{id}/refs/")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Get the children refs with parent id {id}", notes = "This will give a list of object containing a reference id, version and agency. Note that you will"
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
	@Path("items")
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
	@Path("item/{id}/ddi")
	@Produces(MediaType.APPLICATION_XML)
	@ApiOperation(value = "Get DDI document", notes = "Gets a full DDI document from Colectica repository reference {id}", response = String.class)
	public Response getFullDDI(@PathParam(value = "id") String id,
			@QueryParam(value = "resourcePackageId") String resourcePackageId) throws Exception {
		try {
			String ddiDocument = metadataService.getDDIDocument(id, resourcePackageId);
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
	@Path("codeList/{id}/ddi")
	@Produces(MediaType.APPLICATION_XML)
	@ApiOperation(value = "Get the codeList", notes = "Gets a DDI document with a codeList from Colectica repository reference {id}", response = String.class)
	public Response getCodeList(@PathParam(value = "id") String id,
			@QueryParam(value = "resourcePackageId") String resourcePackageId) throws Exception {
		try {
			String codeList = codeListService.getCodeList(id, resourcePackageId);
			StreamingOutput stream = output -> {
				try {
					output.write(codeList.getBytes(StandardCharsets.UTF_8));
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
	@Path("sequence/{id}/ddi")
	@Produces(MediaType.APPLICATION_XML)
	@ApiOperation(value = "Get DDI document", notes = "Gets a DDI document with a sequence from Colectica repository reference {id}", response = String.class)
	public Response getSequence(@PathParam(value = "id") String id,
			@QueryParam(value = "resourcePackageId") String resourcePackageId) throws Exception {
		try {
			String sequence = metadataService.getSequence(id);

			StreamingOutput stream = output -> {
				try {
					output.write(sequence.getBytes(StandardCharsets.UTF_8));

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
	@Path("questionnaire/{id}/ddi")
	@Produces(MediaType.APPLICATION_XML)
	@ApiOperation(value = "Get DDI document of a questionnaire", notes = "Gets a DDI document with a Questionnaire from Colectica repository reference {id}", response = String.class)
	public Response getQuestionnaire(@PathParam(value = "id") String id) throws Exception {
		try {
			String questionnaire = metadataService.getQuestionnaire(id);

			StreamingOutput stream = output -> {
				try {
					output.write(questionnaire.getBytes(StandardCharsets.UTF_8));

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
	@Path("question/{id}/ddi")
	@Produces(MediaType.APPLICATION_XML)
	@ApiOperation(value = "Get DDI document of a question", notes = "Gets a DDI document with a Question from Colectica repository reference {id}", response = String.class)
	public Response getQuestion(@PathParam(value = "id") String id) throws Exception {
		try {
			String questionnaire = metadataService.getQuestion(id);

			StreamingOutput stream = output -> {
				try {
					output.write(questionnaire.getBytes(StandardCharsets.UTF_8));

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

	@POST
	@Path("item/ddi/")
	@Produces(MediaType.TEXT_HTML)
	@ApiOperation(value = "add a new Item", notes = "Add a new Item in the Colectica repository reference {id}", response = String.class)
	public Response postItems(@ApiParam(value = "refsDisplayed") ColecticaPostRefDisplayed refsDisplayed)
			throws Exception {
		try {
			// TODO: add @PUT instead of @POST
			ColecticaItemPostRefList listOfPostItems = new ColecticaItemPostRefList();
			List<ColecticaItemPostRef> items = new ArrayList<ColecticaItemPostRef>();
			ColecticaItemPostRef colecticaPostItemRef = new ColecticaItemPostRef();
			colecticaPostItemRef.setItem(refsDisplayed.getItem());
			// TODO: Create a Map with each UUID as key and the name of the item as value
			// colecticaPostItemRef.setItemFormat(metadataService.getItemFormatrepository());
			colecticaPostItemRef.setVersionDate(LocalDateTime.now().toString());
			/*
			 * set the DDI version of the item 
			 * 3.1 : "34F5DC49-BE0C-4919-9FC2-F84BE994FA34"
			 * 3.2 : "C0CA1BD4-1839-4233-A5B5-906DA0302B89"
			 */
			colecticaPostItemRef.setItemFormat(ItemFormat.DDI_32);
			items.add(colecticaPostItemRef);
			listOfPostItems.setItems(items);
			Map<ColecticaItemPostRef, String> results = metadataServiceItem.postNewItems(listOfPostItems);
			StreamingOutput stream = output -> {
				try {
					for (ColecticaItemPostRef result : results.keySet()) {
						if (!(results.get(result).equals("200"))) {
							throw new RMeSException(Integer.valueOf(results.get(result)),
									"An error Occured from the Repository",
									"The item identifier is : " + result.identifier);
						}

					}
				} catch (Exception e) {
					throw new RMeSException(500, "Transformation error", e.getMessage());
				}
			};
			return Response.ok(stream).build();
		} catch (

		Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

	@POST
	@Path("item/ddi/update")
	@Produces(MediaType.TEXT_HTML)
	@ApiOperation(value = "update an Item", notes = "Update an Item already present in the Colectica repository reference {id}", response = String.class)
	public Response postItemsUpdate(@ApiParam(value = "refs") ColecticaItemPostRefList refs) throws Exception {
		try {
			Map<ColecticaItemPostRef, String> results = metadataServiceItem.postUpdateItems(refs);

			StreamingOutput stream = output -> {
				try {
					for (ColecticaItemPostRef result : results.keySet()) {
						if (!(results.get(result).equals("200"))) {
							throw new RMeSException(Integer.valueOf(results.get(result)),
									"An error Occured from the Repository",
									"The item identifier is : " + result.identifier);
						}

					}
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

}