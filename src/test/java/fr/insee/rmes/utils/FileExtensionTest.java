package fr.insee.rmes.utils;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class FileExtensionTest {

    @Test
    void shouldReturnIllegalStateExceptionWhenGetMediaTypeFromExtension() {
        var pdfExtension =  FileExtension.PDF_EXTENSION;
        IllegalStateException exception = assertThrows(IllegalStateException.class, pdfExtension::getMediaTypeFromExtension);
        assertEquals("Unexpected value: .pdf",exception.getMessage());
    }

    @Test
    void shouldNotReturnIllegalStateExceptionWhenGetMediaTypeFromExtension() {
        var result = FileExtension.ZIP_EXTENSION.getMediaTypeFromExtension();
        String expectedClass ="org.springframework.http.MediaType";
        assertEquals(expectedClass, result.getClass().getName());
    }

    @Test
    void shouldReturnStringExtensionWhenExtension(){

        List<FileExtension> listFileExtensions = List.of(FileExtension.ODT_EXTENSION,
                FileExtension.ODS_EXTENSION,
                FileExtension.ZIP_EXTENSION,
                FileExtension.PDF_EXTENSION,
                FileExtension.XML_EXTENSION,
                FileExtension.FODT_EXTENSION);

        List<String> listExtensions = new ArrayList<>();

        for (FileExtension fileExtension : listFileExtensions){
            System.out.println(fileExtension.extension());
            listExtensions.add(fileExtension.extension());
        }
        assertEquals(listExtensions,List.of(".odt",".ods",".zip",".pdf",".xml",".fodt"));
    }

    @Test
    void shouldReturnFileExtensionWhenForHeader() {
        FileExtension pdfExtension = FileExtension.forHeader("application/octet-stream");
        FileExtension fodtExtension = FileExtension.forHeader("flatODT");
        FileExtension xmlExtension = FileExtension.forHeader("XML");
        FileExtension other = FileExtension.forHeader("DDI-Access-Services");

        List<FileExtension> fileExtensionsList = List.of(pdfExtension,fodtExtension,xmlExtension,other);
        List<FileExtension> expected = List.of(FileExtension.PDF_EXTENSION,FileExtension.FODT_EXTENSION,FileExtension.XML_EXTENSION,FileExtension.ODT_EXTENSION);

        assertEquals(expected, fileExtensionsList);
    }

}