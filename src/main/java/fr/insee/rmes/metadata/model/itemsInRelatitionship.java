package fr.insee.rmes.metadata.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class itemsInRelatitionship {

	/*
	 * [ { "Item1": { "Item1": "string", "Item2": 0, "Item3": "string" },
	 * "Item2": "string" } ]
	 */

	@JsonProperty("Item1")
	private identifierTriple Item1;

	@JsonProperty("Item2")
	private String Item2;

	public identifierTriple getItem1() {
		return Item1;
	}

	public void setItem1(identifierTriple item1) {
		Item1 = item1;
	}

	public String getItem2() {
		return Item2;
	}

	public void setItem2(String item2) {
		Item2 = item2;
	}

	@Override
	public String toString() {
		return "itemsInRelatitionship [Item1=" + Item1 + ", Item2=" + Item2 + "]";
	}

	
}
