package fr.insee.rmes.tocolecticaapi.controller;

import fr.insee.rmes.tocolecticaapi.fragments.DdiFragment;
import fr.insee.rmes.tocolecticaapi.service.ColecticaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GetItem.class)
@ActiveProfiles("dev")
class GetItemTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ColecticaService colecticaService;

    @Test
    void whenGetDataRelationship_shouldReturnRightJson() throws Exception {
        String uuid="34abf2d5-f0bb-47df-b3d2-42ff7f8f5874";
        var dataRelationShipEndpoint="ddiFragment/"+uuid+"/dataRelationship";
        mockMvc.perform(get(dataRelationShipEndpoint).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().json(read("34abf2d5-f0bb-47df-b3d2-42ff7f8f5874_expected.json")));
    }

    private String read(String fileName) throws Exception {
        try (Stream<String> lines = Files.lines(Path.of(fileName))){
            return lines.collect(Collectors.joining());
        }
    }

    @TestConfiguration
    static class ConfigurationForTest{
        @Bean
        DdiFragment ddiFragment(){
            return new DdiFragment() {
                @Override
                public String extractDataRelationship(String uuid) {
                    return "";
                }
            };
        }
    }

}