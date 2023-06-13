package fr.insee.rmes.postItem.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class Items{
    @JsonProperty("Items")
    public ArrayList<Item> getItems() {
        return this.items; }
    public void setItems(ArrayList<Item> items) {
        this.items = items; }
    ArrayList<Item> items;
}
