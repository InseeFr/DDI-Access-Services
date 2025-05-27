package fr.insee.rmes.tocolecticaapi.controller;

import fr.insee.rmes.exceptions.ExceptionColecticaUnreachable;
import fr.insee.rmes.exceptions.RmesException;
import fr.insee.rmes.tocolecticaapi.models.TransactionType;
import fr.insee.rmes.tocolecticaapi.service.ColecticaService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import java.io.IOException;
import static org.mockito.Mockito.doNothing;


@ExtendWith(MockitoExtension.class)
@SpringBootTest(properties = { "fr.insee.rmes.bauhaus.lg1=fr", "fr.insee.rmes.bauhaus.lg2=en"})
class DeleteItemTest {

    @MockitoBean
    private ColecticaService colecticaService;

    @Test
    void shouldReturnResponseWhenDeleteCodeList() throws ExceptionColecticaUnreachable, RmesException, IOException {
        doNothing().when(colecticaService).sendDeleteColectica("d6c08ec1-c4d2-4b9a-b358-b23aa4e0af93", TransactionType.COPYCOMMIT);
        DeleteItem deleteItem = new DeleteItem(colecticaService);
        String actual = deleteItem.deleteCodeList("d6c08ec1-c4d2-4b9a-b358-b23aa4e0af93").toString();
        Assertions.assertEquals("<204 NO_CONTENT No Content,[]>",actual);
    }
}