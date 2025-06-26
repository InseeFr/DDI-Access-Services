package fr.insee.rmes.tocolecticaapi.controller;

import fr.insee.rmes.config.InseeSecurityTokenProperties;
import fr.insee.rmes.config.SecurityConfig;
import fr.insee.rmes.exceptions.RmesException;
import fr.insee.rmes.tocolecticaapi.fragments.DdiFragmentServiceImpl;
import fr.insee.rmes.tocolecticaapi.models.TransactionType;
import fr.insee.rmes.tocolecticaapi.service.ColecticaService;
import fr.insee.rmes.transfoxsl.service.XsltTransformationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;

@WebMvcTest(PostItem.class)
@Import(value = {DdiFragmentServiceImpl.class, SecurityConfig.class, XsltTransformationService.class})
@EnableConfigurationProperties(InseeSecurityTokenProperties.class)
class PostItemTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ColecticaService colecticaService;

    @Mock
    PostItem postItem;

    @Test
    void shouldReturnNullResponseWhenSendUpdateColectica() throws RmesException {
        doNothing().when(colecticaService).sendUpdateColectica("ddiUpdatingInJson", TransactionType.COPYCOMMIT);
        assertNull(postItem.sendUpdateColectica("ddiUpdatingInJson", TransactionType.COPYCOMMIT));
    }
}