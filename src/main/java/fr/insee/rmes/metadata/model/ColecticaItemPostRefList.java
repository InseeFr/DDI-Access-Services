package fr.insee.rmes.metadata.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ColecticaItemPostRefList {

	/**
	 * Example: { "Items": [ { "ItemType": "7E47C269-BCAB-40F7-A778-AF7BBC4E3D00",
	 * "AgencyId": "int.example", "Version": 0, "Identifier":
	 * "f1e672e3-2c5d-4c4c-b9d5-a18fc4d1bc16", "Item": "<Fragment
	 * xmlns:r=\"ddi:reusable:3_2\" xmlns=\"ddi:instance:3_2\"> <Category
	 * isUniversallyUnique=\"true\" versionDate=\"2015-11-16T21:18:04.5513144Z\"
	 * isMissing=\"false\" xmlns=\"ddi:logicalproduct:3_2\">
	 * <r:URN>urn:ddi:int.example:f1e672e3-2c5d-4c4c-b9d5-a18fc4d1bc16:1</r:URN>
	 * <r:Agency>int.example</r:Agency>
	 * <r:ID>f1e672e3-2c5d-4c4c-b9d5-a18fc4d1bc16</r:ID> <r:Version>1</r:Version>
	 * <r:VersionResponsibility>test</r:VersionResponsibility> <r:Label> <r:Content
	 * xml:lang=\"en-US\">Sample Category</r:Content> </r:Label> </Category>
	 * </Fragment>", "Notes": [], "VersionDate": "2017-09-11",
	 * "VersionResponsibility": "test", "IsPublished": true, "IsDeprecated": false,
	 * "IsProvisional": false, "ItemFormat": "c0ca1bd4-1839-4233-a5b5-906da0302b89"
	 * } ], "Options": { } }
	 */

	@JsonProperty("Items")
	private List<ColecticaItemPostRef> items = new ArrayList<ColecticaItemPostRef>();

	@JsonProperty("Options")
	private Map<String, Object> options = new HashMap<String, Object>();

	public List<ColecticaItemPostRef> getItems() {
		return items;
	}

	public void setItems(List<ColecticaItemPostRef> items) {
		this.items = items;
	}

	public Map<String, Object> getOptions() {
		return options;
	}

	public void setOptions(Map<String, Object> options) {
		this.options = options;
	}

}
