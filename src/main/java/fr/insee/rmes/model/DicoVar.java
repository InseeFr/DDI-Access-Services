package fr.insee.rmes.model;

import lombok.Getter;

@Getter
public enum DicoVar {

    CONCIS("/xslTransformerFiles/dicoCodes/dicoConcisPatternContent.xml"),
    CONCIS_AVEC_EXPRESSION("/xslTransformerFiles/dicoCodes/dicoConcisDescrPatternContent.xml"),
    SCINDABLE("/xslTransformerFiles/dicoCodes/dicoScindablePatternContent.xml"),
    NON_SCINDABLE("/xslTransformerFiles/dicoCodes/dicoNonScindablePatternContent.xml");

    private final String transformerFilePath;

    DicoVar(String transformerFilePath) {
        this.transformerFilePath = transformerFilePath;
    }

}
