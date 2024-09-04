package fr.insee.rmes.transfoxsl.service;

import fr.insee.rmes.exceptions.XsltTransformationException;
import net.sf.saxon.s9api.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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

    public List<String> transform(InputStream inputStream, String xslFileName, boolean isTextOutput) throws XsltTransformationException, IOException {
        try {
            logger.log(Level.INFO, "Starting transformation with XSLT file: {0}", xslFileName);

            XsltCompiler compiler = processor.newXsltCompiler();

            // Charger le fichier XSL depuis le classpath
            InputStream xslInputStream = new ClassPathResource(xslFileName).getInputStream();
            XsltExecutable executable = compiler.compile(new StreamSource(xslInputStream));
            XsltTransformer transformer = executable.load();

            transformer.setSource(new StreamSource(inputStream));

            // Utilisation d'un OutputStream pour capturer la sortie en mémoire
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            Serializer out = processor.newSerializer(outputStream);
            if (isTextOutput) {
                out.setOutputProperty(Serializer.Property.METHOD, "text");
                out.setOutputProperty(Serializer.Property.OMIT_XML_DECLARATION, "yes");
            } else {
                out.setOutputProperty(Serializer.Property.METHOD, "xml");
            }

            transformer.setDestination(out);
            transformer.transform();

            // Capture de la sortie sous forme de chaîne
            String result = outputStream.toString(StandardCharsets.UTF_8);

            // Si c'est une sortie texte simple, on peut continuer à découper, sinon retourner directement le JSON complet
            if (isTextOutput) {
                // Si la sortie est un JSON valide, ne pas découper par ligne
                if (result.trim().startsWith("[") || result.trim().startsWith("{")) {
                    return Collections.singletonList(result);
                }
                // Sinon, découper par ligne (utile pour d'autres formats texte simples)
                return Arrays.asList(result.split("\n"));
            } else {
                // Pour XML ou JSON, on retourne le résultat complet comme une seule chaîne
                return Collections.singletonList(result);
            }
        } catch (SaxonApiException e) {
            throw new XsltTransformationException("Error during XSLT transformation", e);
        } catch (IOException e) {
            throw new IOException("I/O error during XSLT transformation", e);
        }
    }
}
