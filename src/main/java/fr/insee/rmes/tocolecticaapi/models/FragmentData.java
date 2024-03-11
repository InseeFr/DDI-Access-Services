package fr.insee.rmes.tocolecticaapi.models;

import java.util.Objects;

public class FragmentData {
    String identifier;
    String agencyId;
    String version;

    // Constructeur, getters, setters, equals et hashCode
    // Implémentez equals et hashCode pour comparer les données correctement


    public FragmentData() {
    }

    public FragmentData(String identifier, String agencyId, String version) {
        this.identifier = identifier;
        this.agencyId = agencyId;
        this.version = version;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(String agencyId) {
        this.agencyId = agencyId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FragmentData that)) return false;
        return Objects.equals(identifier, that.identifier) && Objects.equals(agencyId, that.agencyId) && Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, agencyId, version);
    }
}