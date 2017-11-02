package fr.insee.rmes.metadata.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ColecticaItemRef {

	@JsonProperty("Identifier")
	public String identifier;

	@JsonProperty("Version")
	public Integer version;

	@JsonProperty("AgencyId")
	public String agencyId;

	public ColecticaItemRef() {
	}

	public ColecticaItemRef(String identifier, Integer version, String agencyId) {
		this.agencyId = agencyId;
		this.identifier = identifier;
		this.version = version;
	}

	public Unformatted unformat() {
		return new Unformatted(identifier, version, agencyId);
	}

	public static class Unformatted {

		@JsonProperty("Item1")
		public String item1;

		@JsonProperty("Item2")
		public Integer item2;

		@JsonProperty("Item3")
		public String item3;

		public Unformatted(String item1, Integer item2, String item3) {
			this.item1 = item1;
			this.item2 = item2;
			this.item3 = item3;
		}

		public ColecticaItemRef format() {
			return new ColecticaItemRef(item1, item2, item3);
		}

	}
}
