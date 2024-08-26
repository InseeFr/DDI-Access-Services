package fr.insee.rmes.model;

import java.util.List;

public class PhysicalInstance extends ColecticaItem {

    private String id;
    private String urn;
    private String dataFileVersion;
    private List<String> dataFileLocations;
    private String grossFileStructure;
    private String byteOrder;
    private String statisticalSummary;
    private String softwareUsed;
    private String qualityStatement;

    // Constructeur
    public PhysicalInstance(String id, String urn, String dataFileVersion, List<String> dataFileLocations,
                            String grossFileStructure, String byteOrder, String statisticalSummary,
                            String softwareUsed, String qualityStatement) {
        this.id = id;
        this.urn = urn;
        this.dataFileVersion = dataFileVersion;
        this.dataFileLocations = dataFileLocations;
        this.grossFileStructure = grossFileStructure;
        this.byteOrder = byteOrder;
        this.statisticalSummary = statisticalSummary;
        this.softwareUsed = softwareUsed;
        this.qualityStatement = qualityStatement;
    }

    // Getters et Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrn() {
        return urn;
    }

    public void setUrn(String urn) {
        this.urn = urn;
    }

    public String getDataFileVersion() {
        return dataFileVersion;
    }

    public void setDataFileVersion(String dataFileVersion) {
        this.dataFileVersion = dataFileVersion;
    }

    public List<String> getDataFileLocations() {
        return dataFileLocations;
    }

    public void setDataFileLocations(List<String> dataFileLocations) {
        this.dataFileLocations = dataFileLocations;
    }

    public String getGrossFileStructure() {
        return grossFileStructure;
    }

    public void setGrossFileStructure(String grossFileStructure) {
        this.grossFileStructure = grossFileStructure;
    }

    public String getByteOrder() {
        return byteOrder;
    }

    public void setByteOrder(String byteOrder) {
        this.byteOrder = byteOrder;
    }

    public String getStatisticalSummary() {
        return statisticalSummary;
    }

    public void setStatisticalSummary(String statisticalSummary) {
        this.statisticalSummary = statisticalSummary;
    }

    public String getSoftwareUsed() {
        return softwareUsed;
    }

    public void setSoftwareUsed(String softwareUsed) {
        this.softwareUsed = softwareUsed;
    }

    public String getQualityStatement() {
        return qualityStatement;
    }

    public void setQualityStatement(String qualityStatement) {
        this.qualityStatement = qualityStatement;
    }

    @Override
    public String toString() {
        return "PhysicalInstance{" +
                "id='" + id + '\'' +
                ", urn='" + urn + '\'' +
                ", dataFileVersion='" + dataFileVersion + '\'' +
                ", dataFileLocations=" + dataFileLocations +
                ", grossFileStructure='" + grossFileStructure + '\'' +
                ", byteOrder='" + byteOrder + '\'' +
                ", statisticalSummary='" + statisticalSummary + '\'' +
                ", softwareUsed='" + softwareUsed + '\'' +
                ", qualityStatement='" + qualityStatement + '\'' +
                '}';
    }
}
