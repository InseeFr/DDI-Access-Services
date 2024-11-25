package fr.insee.rmes.transfoxsl.service.internal;

import fr.insee.rmes.transfoxsl.service.XsltTransformationService;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public record DDITransformerToVtl(XsltTransformationService xsltTransformationService, DDIDerefencer ddiDerefencer) {

    public static final String DDI_2_VTL_XSL = "ddi2vtl.xsl";

    public DDITransformerToVtl(XsltTransformationService xsltTransformationService){
        this(xsltTransformationService, new DDIDerefencer(xsltTransformationService));
    }

    public byte[] transform(InputStream inputStream) throws IOException {


        // Deuxième transformation - Texte en sortie (on récupère directement une liste de lignes de texte)
        return xsltTransformationService.transformToRawText(ddiDerefencer.intermediateDereference(inputStream), DDI_2_VTL_XSL);

    }
}
