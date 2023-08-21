package fr.insee.rmes.search.model;

public class IdLabelPair {
    private String id;
    private String label;

    public IdLabelPair(String id, String label) {
        this.id = id;
        this.label = label;
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }
}
