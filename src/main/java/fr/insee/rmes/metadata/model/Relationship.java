package fr.insee.rmes.metadata.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Relationship {

	/*
	 * [ { "Item1": { "Item1": "string", "Item2": 0, "Item3": "string" },
	 * "Item2": "string" } ]
	 */

	@JsonProperty
	private List<itemsInRelatitionship> Item1;

	public Relationship(List<itemsInRelatitionship> Item1) {
		this.Item1 = Item1;
	}

	public List<itemsInRelatitionship> getItem1() {
		return Item1;
	}

	public void setItem1(List<itemsInRelatitionship> item1) {
		Item1 = item1;
	}

	@Override
	public String toString() {
		return "Relationship [Item1=" + Item1 + "]";
	}

}
