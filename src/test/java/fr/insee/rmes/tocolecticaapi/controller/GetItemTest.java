package fr.insee.rmes.tocolecticaapi.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.insee.rmes.config.InseeSecurityTokenProperties;
import fr.insee.rmes.config.SecurityConfig;
import fr.insee.rmes.exceptions.ExceptionColecticaUnreachable;
import fr.insee.rmes.exceptions.RmesException;
import fr.insee.rmes.model.DDIItemType;
import fr.insee.rmes.tocolecticaapi.fragments.DdiFragmentService;
import fr.insee.rmes.tocolecticaapi.fragments.DdiFragmentServiceImpl;
import fr.insee.rmes.tocolecticaapi.service.ColecticaService;
import fr.insee.rmes.transfoxsl.service.XsltTransformationService;
import org.json.JSONArray;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
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

    @Mock
    DdiFragmentService ddiFragmentService;

    @Mock
    ColecticaService colecticaServiceOther;

    @Test
    void whenGetDataRelationshipWithUuid_shouldReturnRightJson() throws Exception {
        String uuid="34abf2d5-f0bb-47df-b3d2-42ff7f8f5874";
        var dataRelationShipEndpoint="/Item/ddiFragment/"+uuid+"/dataRelationship";
        when(colecticaService.searchColecticaInstanceByUuid(uuid)).thenReturn(read("/getItemTest/physicalInstance.xml"));
        mockMvc.perform(get(dataRelationShipEndpoint).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().json(read("/getItemTest/34abf2d5-f0bb-47df-b3d2-42ff7f8f5874_expected.json")));
    }

    @Test
    void whenGetDataRelationshipWithUuidAndVersion_shouldReturnRightJson() throws Exception {
        String uuid="34abf2d5-f0bb-47df-b3d2-42ff7f8f5874";
        int version = 2;
        var dataRelationShipEndpoint="/Item/ddiFragment/"+uuid+"/"+version+"/dataRelationship";
        when(colecticaService.searchColecticaInstanceByUuid(uuid+"/"+version)).thenReturn(read("/getItemTest/physicalInstance.xml"));
        mockMvc.perform(get(dataRelationShipEndpoint).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().json(read("/getItemTest/34abf2d5-f0bb-47df-b3d2-42ff7f8f5874_expected.json")));
    }


    private static String read(String fileName) throws Exception {
        Path path = Path.of(GetItemTest.class.getResource(fileName).toURI());
        try (Stream<String> lines = Files.lines(path)){
            return lines.collect(Collectors.joining());
        }
    }



    @Test
    void shouldReturnResponseWhenFindInstanceByUuidColectica() throws RmesException {
        when(colecticaServiceOther.searchColecticaInstanceByUuid("d6c08ec1-c4d2-4b9a-b358-b23aa4e0af93")).thenReturn("result");
        GetItem getItem = new GetItem(colecticaServiceOther,ddiFragmentService);
        String actual = getItem.findFragmentByUuidColectica("d6c08ec1-c4d2-4b9a-b358-b23aa4e0af93").toString();
        Assertions.assertEquals("<200 OK OK,[]>",actual);
    }

    @Test
    void shouldReturnResponseWhenExtractDataRelationshipFromFragment() throws RmesException {
        when(ddiFragmentService.extractDataRelationship("16a35b68-4479-4282-95ed-ff7d151746e4")).thenReturn("result");
        GetItem getItem = new GetItem(colecticaServiceOther,ddiFragmentService);
        String actual = getItem.extractDataRelationshipFromFragment("16a35b68-4479-4282-95ed-ff7d151746e4").toString();
        Assertions.assertEquals("<200 OK OK,result,[]>",actual);
    }

    @Test
    void shouldReturnResponseWhenFilteredSearchText() throws RmesException {
        when(colecticaServiceOther.filteredSearchText("index", "texte")).thenReturn("result");
        GetItem getItem = new GetItem(colecticaServiceOther,ddiFragmentService);
        String actual = getItem.filteredSearchText("index", "texte").toString();
        Assertions.assertEquals("<200 OK OK,result,[]>",actual);

    }

    @Test
    void shouldReturnResponseEntityWhenFilteredSearchTextByType() throws RmesException {
        when(colecticaServiceOther.searchTexteByType("index", "texte",DDIItemType.QUESTION_ITEM)).thenReturn("mocked");
        GetItem getItem = new GetItem(colecticaServiceOther,ddiFragmentService);
        String actual = getItem.filteredSearchTextByType("index","texte", DDIItemType.QUESTION_ITEM).toString();
        Assertions.assertEquals("<200 OK OK,mocked,[]>",actual);
    }

    @Test
    void shouldReturnErrorWhenFilteredSearchTextByType() throws RmesException {
        when(colecticaServiceOther.searchTexteByType("?????", "texte",DDIItemType.QUESTION_ITEM)).thenReturn("mocked");
        GetItem getItem = new GetItem(colecticaServiceOther,ddiFragmentService);
        String actual = getItem.filteredSearchTextByType("?????","texte", DDIItemType.QUESTION_ITEM).toString();
        Assertions.assertEquals("<400 BAD_REQUEST Bad Request,Invalid input,[]>",actual);
    }

    @Test
    void shouldReturnResponseWhenSearchByType() throws RmesException {
        when(colecticaServiceOther.searchByType("index", DDIItemType.QUESTION_ITEM)).thenReturn("result");
        GetItem getItem = new GetItem(colecticaServiceOther,ddiFragmentService);
        String actual = getItem.searchByType("index", DDIItemType.QUESTION_ITEM).toString();
        Assertions.assertEquals("<200 OK OK,result,[]>",actual);
    }

    @Test
    void shouldReturnResponseWhenGetJsonWithChild() throws JsonProcessingException {
        when(colecticaServiceOther.getJsonWithChild("d6c08ec1-c4d2-4b9a-b358-b23aa4e0af93","id","label")).thenReturn(null);
        GetItem getItem = new GetItem(colecticaServiceOther,ddiFragmentService);
        Assertions.assertNull(getItem.getJsonWithChild("d6c08ec1-c4d2-4b9a-b358-b23aa4e0af93","id","label"));
    }

    @Test
    void shouldReturnResponseWhenGetResourcePackage() throws ExceptionColecticaUnreachable, JsonProcessingException {
        when(colecticaServiceOther.getRessourcePackage("16a35b68-4479-4282-95ed-ff7d151746e4")).thenReturn("mocked message");
        GetItem getItem = new GetItem(colecticaServiceOther,ddiFragmentService);
        String actual = getItem.getRessourcePackage("16a35b68-4479-4282-95ed-ff7d151746e4");
        Assertions.assertEquals("mocked message",actual);
    }

    @Test
    void shouldReturnResponseWhenFindFragmentByUuidWithChildrenColectica() throws RmesException {
        when(colecticaServiceOther.findFragmentByUuidWithChildren("16a35b68-4479-4282-95ed-ff7d151746e4")).thenReturn(new JSONArray());
        GetItem getItem = new GetItem(colecticaServiceOther,ddiFragmentService);
        String actual = getItem.findFragmentByUuidWithChildrenColectica("16a35b68-4479-4282-95ed-ff7d151746e4").toString();
        Assertions.assertEquals("<200 OK OK,[],[]>",actual);
    }

    @Test
    void shouldReturnResponseWhenFindInstanceByUuidColecticaForUuidAndVersion() throws RmesException {
        when(colecticaServiceOther.searchColecticaInstanceByUuid("16a35b68-4479-4282-95ed-ff7d151746e4")).thenReturn("mocked answer");
        GetItem getItem = new GetItem(colecticaServiceOther,ddiFragmentService);
        String actual = getItem.findInstanceByUuidColectica("16a35b68-4479-4282-95ed-ff7d151746e4").toString();
        Assertions.assertEquals("<200 OK OK,mocked answer,[]>",actual);
    }


}

