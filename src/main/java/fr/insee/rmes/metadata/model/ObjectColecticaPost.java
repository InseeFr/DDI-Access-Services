package fr.insee.rmes.metadata.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectColecticaPost {

	/*
	 * { "ItemTypes": [ "StringUUID" ], "TargetItem": { "AgencyId": "String",
	 * "Identifier": "StringUUID", "Version": Integer },
	 * "UseDistinctResultItem": true, "UseDistinctTargetItem": true }
	 */

	@JsonProperty("ItemTypes")
	private List<String> ItemTypes;

	@JsonProperty("TargetItem")
	private TargetItem targetItem;

	@JsonProperty("UseDistinctResultItem")
	private Boolean UseDistinctResultItem;

	@JsonProperty("UseDistinctTargetItem")
	private Boolean UseDistinctTargetItem;

	public List<String> getItemTypes() {
		return ItemTypes;
	}

	public void setItemTypes(List<String> itemTypes) {
		ItemTypes = itemTypes;
	}

	public TargetItem getTargetItem() {
		return targetItem;
	}

	public void setTargetItem(TargetItem targetItem) {
		this.targetItem = targetItem;
	}

	public Boolean getUseDistinctResultItem() {
		return UseDistinctResultItem;
	}

	public void setUseDistinctResultItem(Boolean useDistinctResultItem) {
		UseDistinctResultItem = useDistinctResultItem;
	}

	public Boolean getUseDistinctTargetItem() {
		return UseDistinctTargetItem;
	}

	public void setUseDistinctTargetItem(Boolean useDistinctTargetItem) {
		UseDistinctTargetItem = useDistinctTargetItem;
	}

	@Override
	public String toString() {
		return "RelationshipPost [ItemTypes=" + ItemTypes + ", targetItem=" + targetItem + ", UseDistinctResultItem="
				+ UseDistinctResultItem + ", UseDistinctTargetItem=" + UseDistinctTargetItem + "]";
	}

	public String toJson() throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.writeValueAsString(this);
	}

}
