package fr.insee.rmes.transfoxsl.service.internal;

import fr.insee.rmes.exceptions.XsltTransformationException;
import fr.insee.rmes.transfoxsl.service.XsltTransformationService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public record DDITransformerToVtl(XsltTransformationService xsltTransformationService, DDIDerefencer ddiDerefencer) {

    public static final String DDI_2_VTL_XSL = "ddi2vtl.xsl";

    public DDITransformerToVtl(XsltTransformationService xsltTransformationService){
        this(xsltTransformationService, new DDIDerefencer(xsltTransformationService));
    }

    public byte[] transform(InputStream inputStream) throws IOException, XsltTransformationException {
        // Deuxième transformation - Texte en sortie (on récupère directement une liste de lignes de texte)
        return xsltTransformationService.transformToRawText(new ByteArrayInputStream(ddiDerefencer.intermediateDereference(inputStream)), DDI_2_VTL_XSL);
    }
}
