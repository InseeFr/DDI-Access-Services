package fr.insee.rmes.tocolecticaapi.models;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RessourcePackage {

    @JsonProperty("CodeListSchemes")
    private List<CodeListScheme> codeListSchemes;

    @JsonProperty("ItemName")
    private Map<String, String> itemName;

    @JsonProperty("IsPublished")
    private boolean isPublished;

    @JsonProperty("IsPopulated")
    private boolean isPopulated;

    @JsonProperty("Version")
    private int version;

    @JsonProperty("VersionDate")
    private Date versionDate;

    @JsonProperty("CompositeId")
    private CodeListScheme.CodeList.CompositeId compositeId;

    @JsonProperty("AgencyId")
    private String agencyId;

    @JsonProperty("Identifier")
    private String identifier;

    @JsonProperty("ItemType")
    private String itemType;
    // Getters and Setters


    public Map<String, String> getItemName() {
        return itemName;
    }

    public void setItemName(Map<String, String> itemName) {
        this.itemName = itemName;
    }

    public boolean isPublished() {
        return isPublished;
    }

    public void setPublished(boolean published) {
        isPublished = published;
    }

    public boolean isPopulated() {
        return isPopulated;
    }

    public void setPopulated(boolean populated) {
        isPopulated = populated;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Date getVersionDate() {
        return versionDate;
    }

    public void setVersionDate(Date versionDate) {
        this.versionDate = versionDate;
    }


    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }


    public String getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(String agencyId) {
        this.agencyId = agencyId;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public List<CodeListScheme> getCodeListSchemes() {
        return codeListSchemes;
    }

    public void setCodeListSchemes(List<CodeListScheme> codeListSchemes) {
        this.codeListSchemes = codeListSchemes;
    }


    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CodeListScheme {
        @JsonProperty("CodeLists")
        private List<CodeList> codeLists;

        public List<CodeList> getCodeLists() {
            return codeLists;
        }

        public void setCodeLists(List<CodeList> codeLists) {
            this.codeLists = codeLists;
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class CodeList {

            @JsonProperty("ItemType")
            private String itemType;
            @JsonProperty("Codes")
            private List<Code> codes;

            @JsonProperty("ItemName")
            private Map<String, String> itemName;

            @JsonProperty("Label")
            private Map<String, String> label;

            @JsonProperty("DisplayLabel")
            private String displayLabel;

            @JsonProperty("IsPublished")
            private boolean isPublished;

            @JsonProperty("IsPopulated")
            private boolean isPopulated;

            @JsonProperty("Version")
            private int version;

            @JsonProperty("VersionDate")
            private Date versionDate;

            @JsonProperty("CompositeId")
            private CompositeId compositeId;

            @JsonProperty("AgencyId")
            private String agencyId;

            @JsonProperty("Identifier")
            private String identifier;


            public List<Code> getCodes() {
                return codes;
            }

            public void setCodes(List<Code> codes) {
                this.codes = codes;
            }

            public Map<String, String> getItemName() {
                return itemName;
            }

            public void setItemName(Map<String, String> itemName) {
                this.itemName = itemName;
            }

            public Map<String, String> getLabel() {
                return label;
            }

            public void setLabel(Map<String, String> label) {
                this.label = label;
            }

            public String getDisplayLabel() {
                return displayLabel;
            }

            public void setDisplayLabel(String displayLabel) {
                this.displayLabel = displayLabel;
            }

            public boolean isPublished() {
                return isPublished;
            }

            public void setPublished(boolean published) {
                isPublished = published;
            }

            public boolean isPopulated() {
                return isPopulated;
            }

            public void setPopulated(boolean populated) {
                isPopulated = populated;
            }

            public int getVersion() {
                return version;
            }

            public void setVersion(int version) {
                this.version = version;
            }

            public Date getVersionDate() {
                return versionDate;
            }

            public void setVersionDate(Date versionDate) {
                this.versionDate = versionDate;
            }

            public CompositeId getCompositeId() {
                return compositeId;
            }

            public void setCompositeId(CompositeId compositeId) {
                this.compositeId = compositeId;
            }

            public String getAgencyId() {
                return agencyId;
            }

            public void setAgencyId(String agencyId) {
                this.agencyId = agencyId;
            }

            public String getIdentifier() {
                return identifier;
            }

            public void setIdentifier(String identifier) {
                this.identifier = identifier;
            }


            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class Code {

                @JsonProperty("Value")
                private String value;

                @JsonProperty("Category")
                private Category category;

                @JsonProperty("AgencyId")
                private String agencyId;

                @JsonProperty("Identifier")
                private String identifier;


                public String getValue() {
                    return value;
                }

                public void setValue(String value) {
                    this.value = value;
                }

                public Category getCategory() {
                    return category;
                }

                public void setCategory(Category category) {
                    this.category = category;
                }

                public String getAgencyId() {
                    return agencyId;
                }

                public void setAgencyId(String agencyId) {
                    this.agencyId = agencyId;
                }

                public String getIdentifier() {
                    return identifier;
                }

                public void setIdentifier(String identifier) {
                    this.identifier = identifier;
                }

                @JsonIgnoreProperties(ignoreUnknown = true)
                public static class Category {

                    @JsonProperty("ItemName")
                    private Map<String, String> itemName;

                    @JsonProperty("Label")
                    private Map<String, String> label;

                    @JsonProperty("DisplayLabel")
                    private String displayLabel;

                    @JsonProperty("IsPublished")
                    private boolean isPublished;

                    @JsonProperty("IsPopulated")
                    private boolean isPopulated;

                    @JsonProperty("Version")
                    private int version;

                    @JsonProperty("VersionDate")
                    private Date versionDate;

                    @JsonProperty("CompositeId")
                    private CompositeId compositeId;

                    @JsonProperty("AgencyId")
                    private String agencyId;

                    @JsonProperty("Identifier")
                    private String identifier;


                    public Map<String, String> getItemName() {
                        return itemName;
                    }

                    public void setItemName(Map<String, String> itemName) {
                        this.itemName = itemName;
                    }

                    public Map<String, String> getLabel() {
                        return label;
                    }

                    public void setLabel(Map<String, String> label) {
                        this.label = label;
                    }

                    public String getDisplayLabel() {
                        return displayLabel;
                    }

                    public void setDisplayLabel(String displayLabel) {
                        this.displayLabel = displayLabel;
                    }

                    public boolean isPublished() {
                        return isPublished;
                    }

                    public void setPublished(boolean published) {
                        isPublished = published;
                    }

                    public boolean isPopulated() {
                        return isPopulated;
                    }

                    public void setPopulated(boolean populated) {
                        isPopulated = populated;
                    }

                    public int getVersion() {
                        return version;
                    }

                    public void setVersion(int version) {
                        this.version = version;
                    }

                    public Date getVersionDate() {
                        return versionDate;
                    }

                    public void setVersionDate(Date versionDate) {
                        this.versionDate = versionDate;
                    }

                    public CompositeId getCompositeId() {
                        return compositeId;
                    }

                    public void setCompositeId(CompositeId compositeId) {
                        this.compositeId = compositeId;
                    }

                    public String getAgencyId() {
                        return agencyId;
                    }

                    public void setAgencyId(String agencyId) {
                        this.agencyId = agencyId;
                    }

                    public String getIdentifier() {
                        return identifier;
                    }

                    public void setIdentifier(String identifier) {
                        this.identifier = identifier;
                    }
                }

            }


            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class CompositeId {

                @JsonProperty("Item1")
                private String item1;

                @JsonProperty("Item2")
                private int item2;

                @JsonProperty("Item3")
                private String item3;


                public String getItem1() {
                    return item1;
                }

                public void setItem1(String item1) {
                    this.item1 = item1;
                }

                public int getItem2() {
                    return item2;
                }

                public void setItem2(int item2) {
                    this.item2 = item2;
                }

                public String getItem3() {
                    return item3;
                }

                public void setItem3(String item3) {
                    this.item3 = item3;
                }

            }

        }

    }

    public static class ItemNameWithCodes {
        private Map<String, String> itemName;
        private List<String> codes;

        public Map<String, String> getItemName() {
            return itemName;
        }

        public void setItemName(Map<String, String> itemName) {
            this.itemName = itemName;
        }

        public List<String> getCodes() {
            return codes;
        }

        public void setCodes(List<String> codes) {
            this.codes = codes;
        }
    }
    public List<String> getAllValuesFromCodeListScheme(int x) {
        List<String> values = new ArrayList<>();

        // Vérifiez si l'indice x est valide pour la liste codeListSchemes
        if (x >= 0 && x < codeListSchemes.size()) {
            CodeListScheme scheme = codeListSchemes.get(x);
            if (scheme != null && scheme.getCodeLists() != null) {
                // Itérer sur toutes les CodeLists
                for (CodeListScheme.CodeList codeList : scheme.getCodeLists()) {
                    if (codeList.getCodes() != null) {
                        // Itérer sur tous les Codes dans la liste
                        for (CodeListScheme.CodeList.Code code : codeList.getCodes()) {
                            values.add(code.getValue());
                        }
                    }
                }
            }
        }
        return values;
    }

    public List<Map<String, String>> getAllItemNamesFromCodeListScheme(int x) {
        List<Map<String, String>> itemNamesList = new ArrayList<>();

        // Vérifiez si l'indice x est valide pour la liste codeListSchemes
        if (x >= 0 && x < codeListSchemes.size()) {
            CodeListScheme scheme = codeListSchemes.get(x);
            if (scheme != null && scheme.getCodeLists() != null) {
                // Itérer sur toutes les CodeLists
                for (CodeListScheme.CodeList codeList : scheme.getCodeLists()) {
                    if (codeList.getItemName() != null) {
                        // Ajoutez la carte itemName à la liste des noms d'items
                        itemNamesList.add(codeList.getItemName());
                    }
                }
            }
        }
        return itemNamesList;
    }

    public List<ItemNameWithCodes> getAllItemNamesWithCodesFromCodeListScheme(int x) {
        List<ItemNameWithCodes> itemNamesWithCodesList = new ArrayList<>();

        // Vérifiez si l'indice x est valide pour la liste codeListSchemes
        if (x >= 0 && x < codeListSchemes.size()) {
            CodeListScheme scheme = codeListSchemes.get(x);
            if (scheme != null && scheme.getCodeLists() != null) {
                // Itérer sur toutes les CodeLists
                for (CodeListScheme.CodeList codeList : scheme.getCodeLists()) {
                    if (codeList.getItemName() != null) {
                        // Créez un nouvel objet pour stocker l'itemName et la liste des codes
                        ItemNameWithCodes itemNameWithCodes = new ItemNameWithCodes();
                        itemNameWithCodes.setItemName(codeList.getItemName());
                        List<String> codes = new ArrayList<>();
                        for (CodeListScheme.CodeList.Code code : codeList.getCodes()) {
                            codes.add(code.getValue());
                        }
                        itemNameWithCodes.setCodes(codes);
                        // Ajoutez l'objet à la liste des noms d'items avec les codes
                        itemNamesWithCodesList.add(itemNameWithCodes);
                    }
                }
            }
        }
        return itemNamesWithCodesList;
    }

    public List<Map<String, String>> mapCodesToLabels() {
        List<Map<String, String>> listOfMappings = new ArrayList<>();
        CodeListScheme cls = this.codeListSchemes.get(0); // Obtention du premier CodeListScheme et normalement unique pour le moment

        // Nous allons d'abord créer une liste de tous les labels depuis la première CodeList où chaque code est relié à un unique pays
        List<String> labels = cls.getCodeLists().get(0).getCodes().stream()
                .map(code -> code.getCategory().getLabel().get("fr-FR"))
                .collect(Collectors.toList());

        // Ensuite, nous parcourons toutes les CodeLists pour récuperer les codes
        for (int i = 0; i < cls.getCodeLists().size(); i++) {
            RessourcePackage.CodeListScheme.CodeList currentCodeList = cls.getCodeLists().get(i);
            List<RessourcePackage.CodeListScheme.CodeList.Code> codes = currentCodeList.getCodes();

            // Itérer sur chaque code dans la CodeList actuelle
            for (int j = 0; j < codes.size(); j++) {
                Map<String, String> mapping = new HashMap<>();
                RessourcePackage.CodeListScheme.CodeList.Code currentCode = codes.get(j);

                // Utiliser le label correspondant de la première CodeList
                mapping.put(currentCodeList.getItemName().get("fr-FR"), currentCode.getValue());
                if (i == 0) { // pour la première CodeList, on ajoute aussi le label
                    mapping.put("Label", labels.get(j));
                }

                // Si c'est la première CodeList, initialiser les éléments de la liste des mappings
                if (i == 0) {
                    listOfMappings.add(mapping);
                } else {
                    // Ajouter les données de code aux mappings existants pour les autres CodeLists
                    listOfMappings.get(j).putAll(mapping);
                }
            }
        }

        return listOfMappings;
    }
}
