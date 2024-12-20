package fr.insee.rmes.tocolecticaapi.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

public class ControllerUtils {
    private ControllerUtils() {}

    static ResponseEntity<Resource> xmltoResponseEntity(Resource response, String fileNameWithExtension, MediaType contentType) throws IOException {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename(fileNameWithExtension).build().toString())
                .contentLength(response.contentLength())
                .contentType(contentType)
                .body(response);
    }

    static ResponseEntity<String> xmltoResponseEntity(String xmlContent) {
        return ResponseEntity.ok(xmlContent.replace("\\\"", "\"").replace("ï»¿", ""));
    }

}
