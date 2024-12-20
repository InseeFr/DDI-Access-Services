package fr.insee.rmes.transfoxsl.service.internal;

import fr.insee.rmes.transfoxsl.service.XsltTransformationService;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public record DDIDerefencer(XsltTransformationService xsltTransformationService) {

    public static final String DEREFERENCE_XSL = "dereference.xsl";

    public String dereferenceToString(@NonNull InputStream inputStream) throws IOException {
        // Première transformation - XML en sortie
        return xsltTransformationService.transformToXmlString(inputStream, DEREFERENCE_XSL);
    }

    public byte[] intermediateDereference(@NonNull InputStream inputStream) throws IOException {
        return xsltTransformationService.transformToXml(inputStream, DEREFERENCE_XSL);
    }

    public byte[] intermediateDereference(@NonNull String xmlString) throws IOException {
        return intermediateDereference(new ByteArrayInputStream(xmlString.getBytes()));
    }

}
