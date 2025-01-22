package fr.insee.rmes.utils;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
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

    public static String filterBOM(byte[] bytes) {
        if (hasBom(bytes)){
            return new String(Arrays.copyOfRange(bytes, 3, bytes.length), StandardCharsets.UTF_8);
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }

    private static boolean hasBom(byte[] bytes) {
        return bytes.length > 2
                && bytes[0] == (byte) 0xEF
                && bytes[1] == (byte) 0xBB
                && bytes[2] == (byte) 0xBF;
    }

}
