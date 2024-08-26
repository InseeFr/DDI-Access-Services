package fr.insee.rmes.transfoxsl.utils;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class MultipartFileUtilsTest {

    @Mock
    private MultipartFileUtils multipartFileUtils; // Mock class

    @Before
    public void setUp() {
        // Initialiser les mocks
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void convertToInputStream_ShouldReturnValidInputStream_WhenFileIsProvided() throws Exception {
        // Mocking MultipartFile
        MockMultipartFile mockFile = new MockMultipartFile("file", "test.xml", "application/xml", "<xml></xml>".getBytes());

        // Mocking convertToInputStream method
        when(multipartFileUtils.convertToInputStream(any())).thenReturn(new ByteArrayInputStream("<xml></xml>".getBytes()));

        // Convert to InputStream
        InputStream inputStream = multipartFileUtils.convertToInputStream(mockFile);

        // Assertions
        assertNotNull(inputStream);
        String content = new BufferedReader(new InputStreamReader(inputStream))
                .lines().collect(Collectors.joining("\n"));
        assertEquals("<xml></xml>", content);
    }
}