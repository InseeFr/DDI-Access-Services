package fr.insee.rmes.tocolecticaapi.controller;

import fr.insee.rmes.exceptions.ExceptionColecticaUnreachable;
import fr.insee.rmes.exceptions.RmesException;
import fr.insee.rmes.model.DDIItemType;
import fr.insee.rmes.tocolecticaapi.models.TransactionType;
import fr.insee.rmes.tocolecticaapi.service.ColecticaService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import java.io.IOException;
import java.util.HexFormat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
@SpringBootTest(properties = { "fr.insee.rmes.bauhaus.lg1=fr", "fr.insee.rmes.bauhaus.lg2=en"})
class PostItemTest {

    @MockitoBean
    ColecticaService colecticaService;


    @Test
    void shouldReturnResponseEntityWhenByType() throws ExceptionColecticaUnreachable, IOException, RmesException {
        when(colecticaService.getByType(DDIItemType.CODE_LIST)).thenReturn("result");
        PostItem postItem = new PostItem(colecticaService);
        String actual = postItem.byType(DDIItemType.CODE_LIST).toString();
        Assertions.assertEquals("<200 OK OK,result,[]>",actual);
    }

    @Test
    void shouldReturnResponseEntityWhenSendUpdateColectica() throws RmesException {
        doNothing().when(colecticaService).sendUpdateColectica("a", TransactionType.COPYCOMMIT);
        PostItem postItem = new PostItem(colecticaService);
        String actual = postItem.sendUpdateColectica("a", TransactionType.COPYCOMMIT).toString();
        Assertions.assertEquals("<200 OK OK,Transaction success,[]>",actual);
    }

    @Test
    void shouldReturnResponseEntityWhenUploadItem() throws IOException, RmesException {
        MockMultipartFile mockMultipartFile = new MockMultipartFile("name","originalFilename","contentType", HexFormat.of().parseHex("e04fd020ea3a6910a2d808002b30309d"));
        doNothing().when(colecticaService).sendUpdateColectica(new String(mockMultipartFile.getBytes()), TransactionType.COPYCOMMIT);
        PostItem postItem = new PostItem(colecticaService);
        String actual = postItem.sendUpdateColectica(new String(mockMultipartFile.getBytes()), TransactionType.COPYCOMMIT).toString();
        Assertions.assertEquals("<200 OK OK,Transaction success,[]>",actual);

    }

}
