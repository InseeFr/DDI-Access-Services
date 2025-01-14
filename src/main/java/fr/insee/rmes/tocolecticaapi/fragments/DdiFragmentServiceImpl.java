package fr.insee.rmes.tocolecticaapi.fragments;

import fr.insee.rmes.exceptions.RmesException;
import fr.insee.rmes.tocolecticaapi.service.ColecticaService;
import fr.insee.rmes.transfoxsl.service.XsltTransformationService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Service
public record DdiFragmentServiceImpl(XsltTransformationService xsltTransformationService, ColecticaService colecticaService) implements DdiFragmentService {

    static final String ddiRelationshipToJsonXsl = "/xslTransformerFiles/ddiDatarelationship2Json.xsl";

    @Override
    public String extractDataRelationship(String uuid) throws RmesException {
        try {
            return xsltTransformationService.transformToXmlString(
                    new ByteArrayInputStream(colecticaService.searchColecticaInstanceByUuid(uuid).getBytes()),
                    ddiRelationshipToJsonXsl
            );
        } catch (IOException e) {
            throw new RmesException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while processing DDI to json", e.getMessage());
        }
    }
}
