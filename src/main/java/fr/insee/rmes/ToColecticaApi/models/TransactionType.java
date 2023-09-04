package fr.insee.rmes.ToColecticaApi.models;

public enum TransactionType {

    COMMITASLATESTWITHLATESTCHILDRENANDPROPAGATEVERSIONS("CommitAsLatestWithLatestChildrenAndPropagateVersions");

    private String type = "" ;
    TransactionType(String type) {
        this.type=type;
    }
}
