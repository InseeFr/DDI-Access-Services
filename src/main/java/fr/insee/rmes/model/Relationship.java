package fr.insee.rmes.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

public class Relationship {

	/*
	 * [ { "Item1": { "Item1": "string", "Item2": 0, "Item3": "string" },
	 * "Item2": "string" } ]
	 */

	@JsonProperty("Item1")
	private identifierTriple identifierTriple;

	@JsonProperty("Item2")
	private String typeItem;

	public identifierTriple getIdentifierTriple() {
		return identifierTriple;
	}

	public void setIdentifierTriple(identifierTriple identifierTriple) {
		this.identifierTriple = identifierTriple;
	}

	@Override
	public String toString() {
		return "Relationship [identifierTriple=" + identifierTriple + ", typeItem=" + typeItem + "]";
	}

	public String getTypeItem() {
		return typeItem;
	}

	public void setTypeItem(String typeItem) {
		this.typeItem = typeItem;
	}

	public String toJson() throws JacksonException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.writeValueAsString(this);
	}

}
