package fr.insee.rmes.transfoxsl.utils;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
@Service
public class MultipartFileUtils {

    public InputStream convertToInputStream(MultipartFile file) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("MultipartFile cannot be null");
        }
        return file.getInputStream();
    }
}