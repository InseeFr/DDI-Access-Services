package fr.insee.rmes.utils.ddi;

public enum Envelope {
	
	CATEGORY_SCHEME("ddi-envelope-categoryScheme.xml"),
	CODE_LIST_SCHEME("ddi-envelope-codeListScheme.xml"),
	CATEGORY_SCHEME_FRAGMENT("ddi-envelope-fragmentDocument-categoryScheme.xml"),
	DEFAULT("ddi-enveloppe.xml"),
	FRAGMENT("ddi-fragment-enveloppe.xml");
	
	
	
	
	
	
	private String nameFile="";
	
	Envelope(String nameFile){

	    this.nameFile = nameFile;

	  }
	
	@Override
	public String toString()
	{
		return nameFile;
	}
	
	
	
	
}
