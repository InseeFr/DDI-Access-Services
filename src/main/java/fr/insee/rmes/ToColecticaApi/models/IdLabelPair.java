package fr.insee.rmes.ToColecticaApi.models;

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

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }
}
