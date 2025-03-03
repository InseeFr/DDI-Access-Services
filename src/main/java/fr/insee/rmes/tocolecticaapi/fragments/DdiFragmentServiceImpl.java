package fr.insee.rmes.tocolecticaapi.fragments;

import fr.insee.rmes.exceptions.RmesException;
import fr.insee.rmes.exceptions.XsltTransformationException;
import fr.insee.rmes.tocolecticaapi.service.ColecticaService;
import fr.insee.rmes.transfoxsl.service.XsltTransformationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Slf4j
@Service
public record DdiFragmentServiceImpl(XsltTransformationService xsltTransformationService, ColecticaService colecticaService) implements DdiFragmentService {

    static final String ddiRelationshipToJsonXsl = "/xslTransformerFiles/ddiDatarelationship2Json.xsl";

    @Override
    public String extractDataRelationship(String uuid) throws RmesException {
        try {
            String ddiInstance = colecticaService.searchColecticaInstanceByUuid(uuid);
            log.trace("Ddi Instance returned by colectica :\n{}", ddiInstance);
            return new String(xsltTransformationService.transformToRawText(
                    new ByteArrayInputStream(ddiInstance.getBytes()),
                    ddiRelationshipToJsonXsl
            ));
        } catch (IOException | XsltTransformationException e) {
            log.debug("Error while processing DDI Instance : "+e.getMessage(), e);
            throw new RmesException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while processing DDI to json", e.getMessage());
        }
    }
}
