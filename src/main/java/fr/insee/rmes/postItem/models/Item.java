package fr.insee.rmes.postItem.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class Item{
    @JsonProperty("ItemType")
    public String getItemType() {
        return this.itemType; }
    public void setItemType(String itemType) {
        this.itemType = itemType; }
    String itemType;
    @JsonProperty("AgencyId")
    public String getAgencyId() {
        return this.agencyId; }
    public void setAgencyId(String agencyId) {
        this.agencyId = agencyId; }
    String agencyId;
    @JsonProperty("Version")
    public int getVersion() {
        return this.version; }
    public void setVersion(int version) {
        this.version = version; }
    int version;
    @JsonProperty("Identifier")
    public String getIdentifier() {
        return this.identifier; }
    public void setIdentifier(String identifier) {
        this.identifier = identifier; }
    String identifier;
    @JsonProperty("Item")
    public String getItem() {
        return this.item; }
    public void setItem(String item) {
        this.item = item; }
    String item;
    @JsonProperty("VersionDate")
    public Date getVersionDate() {
        return this.versionDate; }
    public void setVersionDate(Date versionDate) {
        this.versionDate = versionDate; }
    Date versionDate;
    @JsonProperty("VersionResponsibility")
    public String getVersionResponsibility() {
        return this.versionResponsibility; }
    public void setVersionResponsibility(String versionResponsibility) {
        this.versionResponsibility = versionResponsibility; }
    String versionResponsibility;
    @JsonProperty("IsPublished")
    public boolean getIsPublished() {
        return this.isPublished; }
    public void setIsPublished(boolean isPublished) {
        this.isPublished = isPublished; }
    boolean isPublished;
    @JsonProperty("IsDeprecated")
    public boolean getIsDeprecated() {
        return this.isDeprecated; }
    public void setIsDeprecated(boolean isDeprecated) {
        this.isDeprecated = isDeprecated; }
    boolean isDeprecated;
    @JsonProperty("IsProvisional")
    public boolean getIsProvisional() {
        return this.isProvisional; }
    public void setIsProvisional(boolean isProvisional) {
        this.isProvisional = isProvisional; }
    boolean isProvisional;
    @JsonProperty("ItemFormat")
    public String getItemFormat() {
        return this.itemFormat; }
    public void setItemFormat(String itemFormat) {
        this.itemFormat = itemFormat; }
    String itemFormat;
}

