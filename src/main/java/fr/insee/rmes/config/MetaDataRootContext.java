package fr.insee.rmes.config;

import java.util.ArrayList;
import java.util.List;

public class MetaDataRootContext {

	List<String> subGroupIds;
	
	List<String> ressourcePackageIds;

	public MetaDataRootContext(){
		subGroupIds = new ArrayList<String>();
		ressourcePackageIds = new ArrayList<String>();
	}
	
	
	public List<String> getSubGroupIds() {
		return subGroupIds;
	}

	public void setSubGroupIds(List<String> subGroupIds) {
		this.subGroupIds = subGroupIds;
	}


	public List<String> getRessourcePackageIds() {
		return ressourcePackageIds;
	}

	public void setRessourcePackageIds(List<String> ressourcePackageIds) {
		this.ressourcePackageIds = ressourcePackageIds;
	}
	
	
}
