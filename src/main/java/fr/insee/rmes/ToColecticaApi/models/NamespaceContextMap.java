package fr.insee.rmes.ToColecticaApi.models;

import javax.xml.namespace.NamespaceContext;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class NamespaceContextMap implements NamespaceContext {
    private final Map<String, String> prefixMap;

    public NamespaceContextMap(String... mappings) {
        prefixMap = new HashMap<>();
        for (int i = 0; i < mappings.length; i += 2) {
            prefixMap.put(mappings[i], mappings[i + 1]);
        }
    }

    @Override
    public String getNamespaceURI(String prefix) {
        return prefixMap.getOrDefault(prefix, "");
    }

    @Override
    public String getPrefix(String namespaceURI) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator getPrefixes(String namespaceURI) {
        throw new UnsupportedOperationException();
    }
}