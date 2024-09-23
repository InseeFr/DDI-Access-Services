package fr.insee.rmes.transfoxsl.service.internal;

import fr.insee.rmes.transfoxsl.service.XsltTransformationService;

import java.io.IOException;
import java.io.InputStream;

public record DDIDerefencer(XsltTransformationService xsltTransformationService) {

    public static final String DEREFERENCE_XSL = "dereference.xsl";

    public String dereferenceToString(InputStream inputStream) throws IOException {
        // Première transformation - XML en sortie
        return xsltTransformationService.transformToXmlString(inputStream, DEREFERENCE_XSL);
    }

    public byte[] intermediateDereference(InputStream inputStream) throws IOException {
        return xsltTransformationService.transformToXml(inputStream, DEREFERENCE_XSL);
    }

}
