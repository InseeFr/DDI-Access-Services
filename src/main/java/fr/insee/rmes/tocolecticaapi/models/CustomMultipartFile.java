package fr.insee.rmes.tocolecticaapi.models;

import org.apache.commons.fileupload.FileItem;
public class CustomMultipartFile extends CommonsMultipartFile {
    public CustomMultipartFile(FileItem fileItem) {
        super(fileItem);
    }
}
