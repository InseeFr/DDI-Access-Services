package fr.insee.rmes.ToColecticaApi.models;

public enum TransactionType {

    COMMITASLATESTWITHLATESTCHILDRENANDPROPAGATEVERSIONS("CommitAsLatestWithLatestChildrenAndPropagateVersions"),
    COPYCOMMIT("CopyCommit");
    private String type = "" ;
    TransactionType(String type) {
        this.type=type;
    }
}
