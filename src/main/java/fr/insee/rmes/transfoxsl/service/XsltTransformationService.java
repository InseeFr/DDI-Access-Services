package fr.insee.rmes.transfoxsl.service;

import fr.insee.rmes.exceptions.XsltTransformationException;
import net.sf.saxon.s9api.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class XsltTransformationService {

    private static final Logger logger = Logger.getLogger(XsltTransformationService.class.getName());
    private final Processor processor;

    public XsltTransformationService() {
        this.processor = new Processor(false);
    }

    public List<String> transform(InputStream inputStream, String xslFileName, boolean isTextOutput) throws Exception {
        try {
            logger.log(Level.INFO, "Starting transformation with XSLT file: {0}", xslFileName);

            XsltCompiler compiler = processor.newXsltCompiler();
            File xslFile = new ClassPathResource(xslFileName).getFile();
            XsltExecutable executable = compiler.compile(new StreamSource(xslFile));
            XsltTransformer transformer = executable.load();

            transformer.setSource(new StreamSource(inputStream));

            // Utilisation d'un OutputStream pour capturer la sortie en mémoire
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            Serializer out = processor.newSerializer(outputStream);
            if (isTextOutput) {
                out.setOutputProperty(Serializer.Property.METHOD, "text");  // Configurer pour un texte simple
                out.setOutputProperty(Serializer.Property.OMIT_XML_DECLARATION, "yes");  // Pas de déclaration XML pour le texte
            } else {
                out.setOutputProperty(Serializer.Property.METHOD, "xml");  // Configurer pour un XML si nécessaire
            }
            transformer.setDestination(out);
            transformer.transform();

            // Capture de la sortie sous forme de chaîne
            String result = outputStream.toString();

            // Si sortie texte, on peut découper par ligne et retourner une liste de String
            if (isTextOutput) {
                return Arrays.asList(result.split("\n"));
            } else {
                // Pour XML, on retourne une seule chaîne sous forme de liste (vous pouvez adapter selon vos besoins)
                return Collections.singletonList(result);
            }
        } catch (Exception e) {
            throw new XsltTransformationException("Error during XSLT transformation", e);
        }
    }
}
