package fr.insee.rmes.tocolecticaapi.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RessourcePackage(@JsonProperty("CodeListSchemes")
                               List<CodeListScheme> codeListSchemes,

                               @JsonProperty("ItemName")
                               Map<String, String> itemName,

                               @JsonProperty("IsPublished")
                               boolean isPublished,

                               @JsonProperty("IsPopulated")
                               boolean isPopulated,

                               @JsonProperty("Version")
                               int version,

                               @JsonProperty("VersionDate")
                               Date versionDate,

                               @JsonProperty("CompositeId")
                               CompositeId compositeId,

                               @JsonProperty("AgencyId")
                               String agencyId,

                               @JsonProperty("Identifier")
                               String identifier,

                               @JsonProperty("ItemType")
                               String itemType) {


    public static final String LABEL_FR = "fr-FR";

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CodeListScheme(@JsonProperty("CodeLists") List<CodeList> codeLists) {

        private List<Map<String, String>> mapCodesToLabels() {
            List<Map<String, String>> listOfMappings = codeLists().getFirst().initMapCodeList();

            codeLists().forEach(codeList -> codeList.addMapCodeListTo(listOfMappings));

            return listOfMappings;
        }
    }


    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CodeList(@JsonProperty("ItemType")
                           String itemType,
                           @JsonProperty("Codes")
                           List<Code> codes,

                           @JsonProperty("ItemName")
                           Map<String, String> itemName,

                           @JsonProperty("Label")
                           Map<String, String> label,

                           @JsonProperty("DisplayLabel")
                           String displayLabel,

                           @JsonProperty("IsPublished")
                           boolean isPublished,

                           @JsonProperty("IsPopulated")
                           boolean isPopulated,

                           @JsonProperty("Version")
                           int version,

                           @JsonProperty("VersionDate")
                           Date versionDate,

                           @JsonProperty("CompositeId")
                           CompositeId compositeId,

                           @JsonProperty("AgencyId")
                           String agencyId,

                           @JsonProperty("Identifier")
                           String identifier) {

        private List<Map<String, String>> initMapCodeList() {
            return codes().stream()
                    .map(code -> {
                        Map<String, String> map = new HashMap<>();
                        map.put(itemName().get(LABEL_FR), code.value());
                        map.put("Label", code.category().label().get(LABEL_FR));
                        return map;
                    })
                    .toList();
        }

        private void addMapCodeListTo(List<Map<String, String>> listOfMappings) {
            Iterator<Code> codeIterator = codes().iterator();
            for (Map<String, String> mapping : listOfMappings) {
                mapping.put(itemName().get(LABEL_FR), codeIterator.next().value());
            }
        }
    }


    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Code(@JsonProperty("Value")
                       String value,

                       @JsonProperty("Category")
                       Category category,

                       @JsonProperty("AgencyId")
                       String agencyId,

                       @JsonProperty("Identifier")
                       String identifier) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Category(@JsonProperty("ItemName")
                           Map<String, String> itemName,

                           @JsonProperty("Label")
                           Map<String, String> label,

                           @JsonProperty("DisplayLabel")
                           String displayLabel,

                           @JsonProperty("IsPublished")
                           boolean isPublished,

                           @JsonProperty("IsPopulated")
                           boolean isPopulated,

                           @JsonProperty("Version")
                           int version,

                           @JsonProperty("VersionDate")
                           Date versionDate,

                           @JsonProperty("CompositeId")
                           CompositeId compositeId,

                           @JsonProperty("AgencyId")
                           String agencyId,

                           @JsonProperty("Identifier")
                           String identifier) {
    }


    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CompositeId(@JsonProperty("Item1")
                              String item1,

                              @JsonProperty("Item2")
                              int item2,

                              @JsonProperty("Item3")
                              String item3) {


    }

    public List<Map<String, String>> mapCodesToLabels() {
        return codeListSchemes.getFirst().mapCodesToLabels();
    }

}
