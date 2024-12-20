package fr.insee.rmes.transfoxsl.controller;

import fr.insee.rmes.exceptions.VtlTransformationException;
import fr.insee.rmes.transfoxsl.service.XsltTransformationService;
import fr.insee.rmes.transfoxsl.utils.MultipartFileUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@WebMvcTest(TransformationController.class)
@ActiveProfiles("dev")
class TransformationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private XsltTransformationService xsltTransformationService;

    @MockitoBean
    private MultipartFileUtils multipartFileUtils;

    @Test
    @WithMockUser // Simule un utilisateur authentifié
    void ddi2vtl_ShouldReturnPlainText_WhenTransformationIsSuccessful() throws Exception {
        // Mocking MultipartFile and transformation service
        MockMultipartFile mockFile = new MockMultipartFile("file", "test.xml", "application/xml", "<xml></xml>".getBytes());

        // Mocking conversion to InputStream
        when(multipartFileUtils.convertToInputStream(mockFile)).thenReturn(new ByteArrayInputStream("<xml></xml>".getBytes()));

        // Mocking transformation steps
        List<String> intermediateOutput = List.of("<intermediate>XML</intermediate>");
        List<String> finalOutput = List.of("VTL rule 1", "VTL rule 2");

        when(xsltTransformationService.transform(any(), anyString(), anyBoolean())).thenReturn(intermediateOutput);
        when(xsltTransformationService.transform(any(), anyString(), anyBoolean())).thenReturn(finalOutput);

        // Perform the request and verify the response
        mockMvc.perform(MockMvcRequestBuilders.multipart("/xsl/ddi2vtl")
                        .file(mockFile)
                        .with(csrf())) // Ajoute un jeton CSRF fictif
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain"))
                .andExpect(content().string("VTL rule 1\nVTL rule 2"));
    }

    @Test
    @WithMockUser
    void ddi2vtl_ShouldReturnServerError_WhenTransformationFails() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile("file", "test.xml", "application/xml", "<xml></xml>".getBytes());

        // Simulate MultipartFile conversion to InputStream
        when(multipartFileUtils.convertToInputStream(any())).thenReturn(new ByteArrayInputStream("<xml></xml>".getBytes()));

        // Simulate transformation failure with the correct exception
        when(xsltTransformationService.transform(any(), anyString(), anyBoolean()))
                .thenThrow(new VtlTransformationException("Transformation failed during the XSLT processing."));

        // Perform the request and expect the server error
        mockMvc.perform(MockMvcRequestBuilders.multipart("/xsl/ddi2vtl")
                        .file(mockFile)
                        .with(csrf())) // Ajoute un jeton CSRF fictif
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser  // Simule un utilisateur authentifié
    void dataRelationShiptoJson_ShouldReturnJson_WhenTransformationIsSuccessful() throws Exception {
        // Mocking MultipartFile and transformation service
        MockMultipartFile mockFile = new MockMultipartFile("file", "test.xml", "application/xml", "<xml></xml>".getBytes());

        // Mocking conversion to InputStream
        when(multipartFileUtils.convertToInputStream(mockFile)).thenReturn(new ByteArrayInputStream("<xml></xml>".getBytes()));

        // Mocking transformation result
        String intermediateOutput = "[{\"VTL\":\"rule 1\", \"VTL2\":\"rule 2\"}]";

        when(xsltTransformationService.transform(any(InputStream.class), anyString(), anyBoolean()))
                .thenReturn(List.of(intermediateOutput));

        // Expected formatted JSON output (as an object)
        String expectedJson = "[{\n  \"VTL\" : \"rule 1\",\n  \"VTL2\" : \"rule 2\"\n}]";

        // Perform the request with CSRF token and verify the JSON response
        mockMvc.perform(MockMvcRequestBuilders.multipart("/xsl/dataRelationShiptoJson") // Correct URL
                        .file(mockFile)
                        .with(csrf()))  // Add CSRF token to the request
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)) // Verify JSON content type
                .andExpect(content().json(expectedJson)); // Verify formatted JSON response
    }



    @Test
    @WithMockUser
    void ddi2vtlBrut_ShouldReturnPlainText_WhenTransformationIsSuccessful() throws Exception {
        // Mocking MultipartFile and transformation service
        MockMultipartFile mockFile = new MockMultipartFile("file", "test.xml", "application/xml", "<xml></xml>".getBytes());

        // Mocking conversion to InputStream
        when(multipartFileUtils.convertToInputStream(mockFile)).thenReturn(new ByteArrayInputStream("<xml></xml>".getBytes()));

        // Mocking transformation steps
        List<String> intermediateOutput = List.of("<intermediate>XML</intermediate>");
        List<String> finalOutput = List.of("VTL rule 1", "VTL rule 2");

        when(xsltTransformationService.transform(any(InputStream.class), anyString(), anyBoolean()))
                .thenReturn(intermediateOutput);
        when(xsltTransformationService.transform(any(InputStream.class), anyString(), anyBoolean()))
                .thenReturn(finalOutput);

        // Perform the request and verify the plain text response
        mockMvc.perform(MockMvcRequestBuilders.multipart("/xsl/ddi2vtlBrut")
                        .file(mockFile)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN))
                .andExpect(content().string("VTL rule 1\nVTL rule 2"));
    }
}
