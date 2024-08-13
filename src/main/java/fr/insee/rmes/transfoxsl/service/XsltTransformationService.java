package fr.insee.rmes.transfoxsl.service;

import net.sf.saxon.s9api.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class XsltTransformationService {

    private static final Logger logger = Logger.getLogger(XsltTransformationService.class.getName());
    private final Processor processor;

    public XsltTransformationService() {
        this.processor = new Processor(false);
    }


    public File transform(File inputFile, String xslFileName, boolean isTextOutput) throws Exception {
        logger.log(Level.INFO, "Starting transformation with XSLT file: {0}", xslFileName);

        // Définir l'extension de sortie en fonction de la transformation souhaitée
        String outputExtension = isTextOutput ? ".txt" : ".xml";
        File tempOutputFile = File.createTempFile("output", outputExtension);

        XsltCompiler compiler = processor.newXsltCompiler();
        File xslFile = new ClassPathResource(xslFileName).getFile();
        XsltExecutable executable = compiler.compile(new StreamSource(xslFile));
        XsltTransformer transformer = executable.load();

        transformer.setSource(new StreamSource(inputFile));

        try (OutputStream outputStream = Files.newOutputStream(tempOutputFile.toPath())) {
            Serializer out = processor.newSerializer(outputStream);
            if (isTextOutput) {
                out.setOutputProperty(Serializer.Property.METHOD, "text");  // Configurer pour un texte simple
                out.setOutputProperty(Serializer.Property.OMIT_XML_DECLARATION, "yes");  // Pas de déclaration XML pour le texte
            } else {
                out.setOutputProperty(Serializer.Property.METHOD, "xml");  // Configurer pour un XML si nécessaire
            }
            transformer.setDestination(out);
            transformer.transform();
        }

        logger.log(Level.INFO, "Transformation completed for XSLT file: {0}", xslFileName);
        logger.log(Level.INFO, "Output file content: {0}", Files.readString(tempOutputFile.toPath()));

        return tempOutputFile;
    }

}