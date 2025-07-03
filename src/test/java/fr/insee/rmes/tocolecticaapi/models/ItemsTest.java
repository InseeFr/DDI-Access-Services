package fr.insee.rmes.tocolecticaapi.models;

import fr.insee.rmes.model.Category;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import java.util.ArrayList;
import java.util.Collection;
import static org.junit.jupiter.api.Assertions.*;

class ItemsTest {

    @Mock
    Collection<Category> singletonList;

    @Test
    void shouldReturnAttributesWhenItem(){

        Items items = new Items(singletonList);

        ArrayList<Item> list = new ArrayList<>();
        list.add(new Item());
        list.add(new Item());

        Item thirdItem =  new Item();
        thirdItem.setItem("mockedItem");

        items.setItems(list);
        items.addItem(thirdItem);

        assertTrue(items.getItems()==list && items.getItemsList()==list && list.size()==3);

    }

}