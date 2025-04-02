package fr.insee.rmes.utils.export;

import org.junit.jupiter.api.Test;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ClasspathUriResolverTest {

    @Test
    void shouldReturnNotNullSources() throws TransformerException {
        ClasspathUriResolver classpathUriResolver = new ClasspathUriResolver();
        List<Source> sources = new ArrayList<>();
        sources.add(classpathUriResolver.resolve("../..test","anyString"));
        sources.add(classpathUriResolver.resolve("..test","anyString"));
        sources.add(classpathUriResolver.resolve("test","anyString"));
        assertTrue(sources.getFirst()!=null && sources.get(1)!=null && sources.get(2)!=null);
    } 
}