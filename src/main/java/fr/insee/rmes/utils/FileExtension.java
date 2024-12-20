package fr.insee.rmes.utils;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum FileExtension {

    ODT_EXTENSION(".odt", new MediaType("application", "vnd.oasis.opendocument.text")),
    ODS_EXTENSION(".ods", new MediaType("application", "vnd.oasis.opendocument.spreadsheet")),
    ZIP_EXTENSION(".zip", new MediaType("application", "zip")),
    PDF_EXTENSION(".pdf", null),
    XML_EXTENSION(".xml", null),
    FODT_EXTENSION(".fodt", null);

    private final String extension;
    private final MediaType mediaType;

    public MediaType getMediaTypeFromExtension() {
        if (mediaType == null) {
            throw new IllegalStateException("Unexpected value: " + extension);
        }
        return mediaType;
    }

    public static FileExtension forHeader(String acceptHeader) {
        return switch (acceptHeader) {
            case "application/octet-stream" -> PDF_EXTENSION;
            case "flatODT" -> FODT_EXTENSION;
            case "XML" -> XML_EXTENSION;
            // also "application/vnd.oasis.opendocument.text" -> ODT_EXTENSION
            case null, default -> ODT_EXTENSION;
        };
    }

    public String extension() {
        return extension;
    }

}

