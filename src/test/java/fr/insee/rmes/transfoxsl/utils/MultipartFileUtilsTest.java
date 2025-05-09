package fr.insee.rmes.transfoxsl.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class MultipartFileUtilsTest {

    @Mock
    private MultipartFileUtils multipartFileUtils; // Mock class

    @BeforeEach
    public void setUp() {
        // Initialiser les mocks
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void convertToInputStream_ShouldReturnValidInputStream_WhenFileIsProvided() throws Exception {
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

    @Test
    void shouldThrowIllegalArgumentExceptionWhenConvertToInputStream() throws Exception {
        MultipartFileUtils multipartFileUtilsExample = new MultipartFileUtils();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> multipartFileUtilsExample.convertToInputStream(null));
        assertEquals("MultipartFile cannot be null",exception.getMessage());
    }






}
