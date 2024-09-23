package fr.insee.rmes.transfoxsl.service;

import fr.insee.rmes.exceptions.XsltTransformationException;
import lombok.extern.slf4j.Slf4j;
import net.sf.saxon.s9api.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
@Slf4j
public class XsltTransformationService {

    private final Processor processor;

    public XsltTransformationService() {
        this.processor = new Processor(false);
    }

    byte[] transform(InputStream inputStream, String xslFileName, SerializerConfigurer serializerConfigurer) throws XsltTransformationException, IOException {
        try {
            log.atDebug().log(()->"Starting transformation with XSLT file: "+ xslFileName);

            XsltCompiler compiler = processor.newXsltCompiler();

            // Remplacer l'utilisation de getFile() par getInputStream() pour charger la ressource du classpath
            InputStream xslInputStream = new ClassPathResource(xslFileName).getInputStream();
            XsltExecutable executable = compiler.compile(new StreamSource(xslInputStream));
            XsltTransformer transformer = executable.load();

            transformer.setSource(new StreamSource(inputStream));

            // Utilisation d'un OutputStream pour capturer la sortie en mémoire
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            Serializer serializer = serializerConfigurer.configure(outputStream);

            transformer.setDestination(serializer);
            transformer.transform();

           return outputStream.toByteArray();

//            // Si sortie texte, on peut découper par ligne et retourner une liste de String
//            if (isTextOutput) {
//                return Arrays.asList(result.split("\n"));
//            } else {
//                // Pour XML, on retourne une seule chaîne sous forme de liste (vous pouvez adapter selon vos besoins)
//                return Collections.singletonList(result);
//            }
        } catch (SaxonApiException e) {
            throw new XsltTransformationException("Error during XSLT transformation", e);
        }
    }

    public String transformToXmlString(InputStream inputStream, String xslFileName)throws XsltTransformationException, IOException {
        return toXmlString(transformToXml(inputStream, xslFileName));
    }

    public byte[] transformToXml(InputStream inputStream, String xslFileName) throws IOException {
        return transform(inputStream, xslFileName, SerializerConfigurer.forXmlString(processor));
    }

    private String toXmlString(byte[] result) {
        return new String(result);
    }

    public byte[] transformToRawText(InputStream stream, String ddi2VtlXsl) throws IOException {
        return transform(stream, ddi2VtlXsl, SerializerConfigurer.forRawText(processor));
    }
}
