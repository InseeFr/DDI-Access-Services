package fr.insee.rmes.transfoxsl.service;

import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.Serializer;

import java.io.OutputStream;

public interface SerializerConfigurer {

    static SerializerConfigurer forXmlString(Processor processor) {
        return outputStream -> {
            var serializer = processor.newSerializer(outputStream);
            serializer.setOutputProperty(Serializer.Property.METHOD, "xml");
            return serializer;
        };
    }

    static SerializerConfigurer forRawText(Processor processor) {
        return outputStream -> {
            var serializer = processor.newSerializer(outputStream);
            serializer.setOutputProperty(Serializer.Property.METHOD, "text");  // Configurer pour un texte simple
            serializer.setOutputProperty(Serializer.Property.OMIT_XML_DECLARATION, "yes");  // Pas de déclaration XML pour le texte
            return serializer;
        };
    }

    Serializer configure(OutputStream outputStream);
}
