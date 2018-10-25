package fr.insee.rmes.utils.ddi;

public enum ItemFormat {
	
	DDI_31("34F5DC49-BE0C-4919-9FC2-F84BE994FA34"),
	DDI_32("C0CA1BD4-1839-4233-A5B5-906DA0302B89");
	
	private String uuid="";
	
	ItemFormat (String uuid){

	    this.uuid = uuid;

	  }
	
	@Override
	public String toString()
	{
		return uuid;
	}

}
