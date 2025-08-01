package fr.insee.rmes.tocolecticaapi.controller;

import fr.insee.rmes.config.InseeSecurityTokenProperties;
import fr.insee.rmes.config.SecurityConfig;
import fr.insee.rmes.tocolecticaapi.fragments.DdiFragmentServiceImpl;
import fr.insee.rmes.tocolecticaapi.service.ColecticaService;
import fr.insee.rmes.transfoxsl.service.XsltTransformationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GetItem.class)
@Import(value = {DdiFragmentServiceImpl.class, SecurityConfig.class, XsltTransformationService.class})
@EnableConfigurationProperties(InseeSecurityTokenProperties.class)
@ActiveProfiles("dev")
class GetItemTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ColecticaService colecticaService;

    /***
     * This test will :
     * - use the method which generates the json from the DDI (colecticaService.searchColecticaInstanceByUuid()). This
     * method will use a hard coded DDI file /getItemTest/physicalInstance.xml.
     * - check that the json generated is conform with the expected json
     * @throws Exception
     */
    @Test
    void whenGetDataRelationshipWithUuid_shouldReturnRightJson() throws Exception {
        String uuid="9a7f1abd-10ec-48f3-975f-fcfedb7dc4cd";
        var dataRelationShipEndpoint="/Item/ddiFragment/"+uuid+"/dataRelationship";
        when(colecticaService.searchColecticaInstanceByUuid(uuid)).thenReturn(read("/getItemTest/physicalInstance.xml"));
        mockMvc.perform(get(dataRelationShipEndpoint).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().json(read("/getItemTest/9a7f1abd-10ec-48f3-975f-fcfedb7dc4cd_expected.json")));
    }

    /***
     * This test will :
     * - use the method which generates the json from the DDI (colecticaService.searchColecticaInstanceByUuid()). This
     * method will use a hard coded DDI file /getItemTest/physicalInstance.xml.
     * - check that the json generated is conform with the expected json
     * @throws Exception
     */
    @Test
    void whenGetDataRelationshipWithUuidAndVersion_shouldReturnRightJson() throws Exception {
        String uuid="9a7f1abd-10ec-48f3-975f-fcfedb7dc4cd9a7f1abd-10ec-48f3-975f-fcfedb7dc4cd";
        int version = 2;
        var dataRelationShipEndpoint="/Item/ddiFragment/"+uuid+"/"+version+"/dataRelationship";
        when(colecticaService.searchColecticaInstanceByUuid(uuid+"/"+version)).thenReturn(read("/getItemTest/physicalInstance.xml"));
        mockMvc.perform(get(dataRelationShipEndpoint).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().json(read("/getItemTest/9a7f1abd-10ec-48f3-975f-fcfedb7dc4cd_expected.json")));
    }


    private static String read(String fileName) throws Exception {
        Path path = Path.of(GetItemTest.class.getResource(fileName).toURI());
        try (Stream<String> lines = Files.lines(path)){
            return lines.collect(Collectors.joining());
        }
    }

}