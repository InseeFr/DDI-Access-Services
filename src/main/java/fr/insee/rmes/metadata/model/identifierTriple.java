package fr.insee.rmes.metadata.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class identifierTriple {

	/*
	 * [ { "Item1": { "Item1": "string", "Item2": 0, "Item3": "string" },
	 * "Item2": "string" } ]
	 */

	@JsonProperty("Item1")
	private String Item1;

	@JsonProperty("Item2")
	private Integer Item2;

	@JsonProperty("Item3")
	private String Item3;

	public String getItem1() {
		return Item1;
	}

	public void setItem1(String item1) {
		Item1 = item1;
	}

	public Integer getItem2() {
		return Item2;
	}

	public void setItem2(Integer item2) {
		Item2 = item2;
	}

	public String getItem3() {
		return Item3;
	}

	public void setItem3(String item3) {
		Item3 = item3;
	}

	@Override
	public String toString() {
		return "identifierTriple [Item1=" + Item1 + ", Item2=" + Item2 + ", Item3=" + Item3 + "]";
	}

}
