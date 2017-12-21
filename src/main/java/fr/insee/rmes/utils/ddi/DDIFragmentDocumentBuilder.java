package fr.insee.rmes.utils.ddi;

import java.util.HashMap;
import java.util.Map;

public class DDIFragmentDocumentBuilder {
	
	private Map<String, Enum<Envelope>> fragments = new HashMap<String, Enum<Envelope>>();

	public Map<String, Enum<Envelope>> getFragments() {
		return fragments;
	}

	public void setFragments(Map<String, Enum<Envelope>> fragments) {
		this.fragments = fragments;
	}
	
	

}
