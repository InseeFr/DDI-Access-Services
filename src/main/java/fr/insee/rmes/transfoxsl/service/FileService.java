package fr.insee.rmes.transfoxsl.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

@Service
public class FileService {

    public static File convertToFile(MultipartFile file) throws IOException {
        File convFile = File.createTempFile("input", ".xml");
        try (OutputStream fos = Files.newOutputStream(convFile.toPath())) {
            fos.write(file.getBytes());
        }
        return convFile;
    }
}