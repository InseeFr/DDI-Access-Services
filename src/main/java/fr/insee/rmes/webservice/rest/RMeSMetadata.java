package fr.insee.rmes.webservice.rest;

import java.nio.charset.StandardCharsets;
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

import fr.insee.rmes.metadata.model.Code;
import fr.insee.rmes.metadata.model.CodeList;
import fr.insee.rmes.metadata.model.ColecticaItem;
import fr.insee.rmes.metadata.model.ColecticaItemRefList;
import fr.insee.rmes.metadata.model.Unit;
import fr.insee.rmes.metadata.service.MetadataService;
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

	@GET
	@Path("item/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Gets the item with id {id}", notes = "Get an item from Colectica Repository, given it's {id}", response = ColecticaItem.class)
	public Response getItem(@PathParam(value = "id") String id) throws Exception {
		try {
			ColecticaItem item = metadataService.getItem(id);
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
			ColecticaItemRefList refs = metadataService.getChildrenRef(id);
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
			List<ColecticaItem> children = metadataService.getItems(query);
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
			String codeList = metadataService.getCodeList(id, resourcePackageId);
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
	@Path("serie/{id}/ddi")
	@Produces(MediaType.APPLICATION_XML)
	@ApiOperation(value = "Get a Serie", notes = "Gets a DDI document with a serie from Colectica repository reference {id}", response = String.class)
	public Response getSerie(@PathParam(value = "id") String id,
			@QueryParam(value = "resourcePackageId") String resourcePackageId) throws Exception {
		try {
			String serie = metadataService.getSerie(id, resourcePackageId);

			StreamingOutput stream = output -> {
				try {
						output.write(serie.getBytes(StandardCharsets.UTF_8));

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
	@Path("operation/{id}/ddi")
	@Produces(MediaType.APPLICATION_XML)
	@ApiOperation(value = "Get DDI document", notes = "Gets a DDI document with a operation from Colectica repository reference {id}", response = String.class)
	public Response getOperation(@PathParam(value = "id") String id,
			@QueryParam(value = "resourcePackageId") String resourcePackageId) throws Exception {
		try {
			String operation = metadataService.getOperation(id, resourcePackageId);

			StreamingOutput stream = output -> {
				try {
						output.write(operation.getBytes(StandardCharsets.UTF_8));
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
	@Path("datacollection/{id}/ddi")
	@Produces(MediaType.APPLICATION_XML)
	@ApiOperation(value = "Get DDI document", notes = "Gets a DDI document with a data-collection from Colectica repository reference {id}", response = String.class)
	public Response getDataCollection(@PathParam(value = "id") String id,
			@QueryParam(value = "resourcePackageId") String resourcePackageId) throws Exception {
		try {
			String collection = metadataService.getDataCollection(id, resourcePackageId);

			StreamingOutput stream = output -> {
				try {
						output.write(collection.getBytes(StandardCharsets.UTF_8));
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
			String sequence = metadataService.getSequence(id, resourcePackageId);

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
	public Response getQuestionnaire(@PathParam(value = "id") String id,
			@QueryParam(value = "resourcePackageId") String resourcePackageId) throws Exception {
		try {
			String questionnaire = metadataService.getQuestionnaire(id, resourcePackageId);

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
	public Response getQuestion(@PathParam(value = "id") String id,
			@QueryParam(value = "resourcePackageId") String resourcePackageId) throws Exception {
		try {
			String questionnaire = metadataService.getQuestion(id, resourcePackageId);

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

}