package fr.insee.rmes.utils;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.List;

public final class HttpUtils {
    public static final String ATTACHMENT = "attachment";
    public static final String CONTENT_DISPOSITION = "Content-Disposition";

    private HttpUtils() {
    }

    public static HttpHeaders generateHttpHeaders(String fileName, FileExtension extension, int maxLength) {
        MediaType contentType = extension.getMediaTypeFromExtension();

        ContentDisposition content = ContentDisposition.builder(HttpUtils.ATTACHMENT).filename(
                FilesUtils.reduceFileNameSize(
                        FilesUtils.removeAsciiCharacters(fileName), maxLength)
                ).build();

        List<String> allowHeaders = List.of(CONTENT_DISPOSITION,
                "X-Missing-Documents",
                "Access-Control-Allow-Origin",
                "Access-Control-Allow-Credentials");


        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentDisposition(content);
        responseHeaders.setContentType(contentType);
        responseHeaders.setAccessControlExposeHeaders(allowHeaders);
        return responseHeaders;
    }

}
