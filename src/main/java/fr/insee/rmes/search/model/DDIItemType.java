package fr.insee.rmes.search.model;

public enum DDIItemType {

	QUESTION("Question","question"),
	QUESTIONNAIRE("Instrument","instrument"),
	SEQUENCE("Sequence","sequence"),
	DATA_COLLECTION("Data Collection","data-collection"),
	STUDY_UNIT("Study Unit","study-unit"),
	CODE_LIST("Code List","code-list"),
	GROUP("Group","group"),
	SUB_GROUP("Sub Group","sub-group"),
	DDI_INSTANCE("DDIInstance","DDIInstance");
	
	private String name = "" ; 
	private String type ="";
	
	DDIItemType(String name, String type){
		this.name=name;
		this.type=type;
	}
	
	public String toString(){
		return this.name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	
}
