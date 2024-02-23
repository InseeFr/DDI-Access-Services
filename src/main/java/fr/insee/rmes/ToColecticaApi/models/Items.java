package fr.insee.rmes.tocolecticaapi.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.insee.rmes.metadata.model.Category;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Items{
    public Items(Collection<Category> singletonList) {

    }

    @JsonProperty("Items")
    public ArrayList<Item> getItems() {
        return this.items; }
    public void setItems(ArrayList<Item> items) {
        this.items = items; }
    ArrayList<Item> items;

    public List<Item> getItemsList() {
        return items;
    }

    public void addItem(Item item) {
        items.add(item);
    }
}
