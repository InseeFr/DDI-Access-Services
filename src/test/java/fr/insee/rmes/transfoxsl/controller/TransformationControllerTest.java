package fr.insee.rmes.transfoxsl.controller;

import fr.insee.rmes.config.InseeSecurityTokenProperties;
import fr.insee.rmes.config.SecurityConfig;
import fr.insee.rmes.exceptions.XsltTransformationException;
import fr.insee.rmes.transfoxsl.service.XsltTransformationService;
import fr.insee.rmes.transfoxsl.utils.MultipartFileUtils;
import fr.insee.rmes.utils.export.XDocReport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@WebMvcTest(TransformationController.class)
@Import(SecurityConfig.class)
@EnableConfigurationProperties(InseeSecurityTokenProperties.class)
@ActiveProfiles("dev")
class TransformationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private XsltTransformationService xsltTransformationService;

    @MockitoBean
    private MultipartFileUtils multipartFileUtils;

    @MockitoBean
    private XDocReport xDocReport;

    @Test
    @WithMockUser // Simule un utilisateur authentifié
    void ddi2vtl_ShouldReturnPlainText_WhenTransformationIsSuccessful() throws Exception {
        // Mocking MultipartFile and transformation service
        MockMultipartFile mockFile = new MockMultipartFile("file", "test.xml", "application/xml", "<xml></xml>".getBytes());

        // Mocking conversion to InputStream
        when(multipartFileUtils.convertToInputStream(mockFile)).thenReturn(new ByteArrayInputStream("<xml></xml>".getBytes()));

        // Mocking transformation steps
        byte[] intermediateOutput = "<intermediate>XML</intermediate>".getBytes();
        byte[] finalOutput = "VTL rule 1\nVTL rule 2".getBytes();

        when(xsltTransformationService.transformToXml(any(), anyString())).thenReturn(intermediateOutput);
        when(xsltTransformationService.transformToRawText(any(), anyString())).thenReturn(finalOutput);

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
        when(xsltTransformationService.transformToXml(any(), anyString()))
                .thenThrow(new XsltTransformationException("Transformation failed during the XSLT processing.", null));

        // Perform the request and expect the server error
        mockMvc.perform(MockMvcRequestBuilders.multipart("/xsl/ddi2vtl")
                        .file(mockFile)
                        .with(csrf())) // Ajoute un jeton CSRF fictif
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser() // Simule un utilisateur authentifié
    void dataRelationShiptoJson_ShouldReturnJson_WhenTransformationIsSuccessful() throws Exception {
        // Mocking MultipartFile and transformation service
        MockMultipartFile mockFile = new MockMultipartFile("file", "test.xml", "application/xml", "<xml></xml>".getBytes());

        // Mocking conversion to InputStream
        when(multipartFileUtils.convertToInputStream(mockFile)).thenReturn(new ByteArrayInputStream("<xml></xml>".getBytes()));

        // Mocking transformation result
        byte[] intermediateOutput = "[{\"VTL\":\"rule 1\", \"VTL2\":\"rule 2\"}]".getBytes();

        when(xsltTransformationService.transformToRawText(any(InputStream.class), anyString()))
                .thenReturn(intermediateOutput);

        // Expected formatted JSON output (as an object)
        String expectedJson = "[{\n  \"VTL\" : \"rule 1\",\n  \"VTL2\" : \"rule 2\"\n}]";

        // Perform the request with CSRF token and verify the JSON response
        mockMvc.perform(MockMvcRequestBuilders.multipart("/xsl/dataRelationShiptoJson")
                        .file(mockFile))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));
    }



    @Test
    @WithMockUser
    void ddi2vtlBrut_ShouldReturnPlainText_WhenTransformationIsSuccessful() throws Exception {
        // Mocking MultipartFile and transformation service
        MockMultipartFile mockFile = new MockMultipartFile("file", "test.xml", "application/xml", "<xml></xml>".getBytes());

        // Mocking conversion to InputStream
        when(multipartFileUtils.convertToInputStream(mockFile)).thenReturn(new ByteArrayInputStream("<xml></xml>".getBytes()));

        // Mocking transformation steps
       byte[] intermediateOutput = "<intermediate>XML</intermediate>".getBytes();
       byte[] finalOutput = "VTL rule 1\nVTL rule 2".getBytes();

        when(xsltTransformationService.transformToXml(any(InputStream.class), anyString()))
                .thenReturn(intermediateOutput);
        when(xsltTransformationService.transformToRawText(any(InputStream.class), anyString()))
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
