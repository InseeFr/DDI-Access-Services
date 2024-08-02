package fr.insee.rmes.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class ObjectColecticaPost {

	/*
	 * { "ItemTypes": [ "StringUUID" ], "TargetItem": { "AgencyId": "String",
	 * "Identifier": "StringUUID", "Version": Integer },
	 * "UseDistinctResultItem": true, "UseDistinctTargetItem": true }
	 */

	@JsonProperty("ItemTypes")
	private List<String> itemTypes;

	@JsonProperty("TargetItem")
	private TargetItem targetItem;

	@JsonProperty("UseDistinctResultItem")
	private Boolean useDistinctResultItem;

	@JsonProperty("UseDistinctTargetItem")
	private Boolean useDistinctTargetItem;

	public List<String> getItemTypes() {
		return itemTypes;
	}

	public void setItemTypes(List<String> itemTypes) {
		this.itemTypes = itemTypes;
	}

	public TargetItem getTargetItem() {
		return targetItem;
	}

	public void setTargetItem(TargetItem targetItem) {
		this.targetItem = targetItem;
	}

	public Boolean getUseDistinctResultItem() {
		return useDistinctResultItem;
	}

	public void setUseDistinctResultItem(Boolean useDistinctResultItem) {
		this.useDistinctResultItem = useDistinctResultItem;
	}

	public Boolean getUseDistinctTargetItem() {
		return useDistinctTargetItem;
	}

	public void setUseDistinctTargetItem(Boolean useDistinctTargetItem) {
		this.useDistinctTargetItem = useDistinctTargetItem;
	}

	@Override
	public String toString() {
		return "RelationshipPost [ItemTypes=" + itemTypes + ", targetItem=" + targetItem + ", UseDistinctResultItem="
				+ useDistinctResultItem + ", UseDistinctTargetItem=" + useDistinctTargetItem + "]";
	}

	public String toJson() throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.writeValueAsString(this);
	}

}
