package fr.insee.rmes.model;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.w3c.dom.Node;
import static org.junit.jupiter.api.Assertions.*;

class ItemWithParentTest {

    @Mock
    ColecticaItem colecticaItem;
    Node itemNode;
    ColecticaItem parent;
    Node parentNode;
    ColecticaItem ressourcePackage;
    Node ressourcePackageNode;
    DDIItemType typeParent;


    @Test
    void shouldTestToString() {
        ItemWithParent itemWithParent = new ItemWithParent();

        ColecticaItem parent = new ColecticaItem();
        parent.setIdentifier("parent");
        ColecticaItem item= new ColecticaItem();
        item.setIdentifier("item");

        itemWithParent.setParent(parent);
        itemWithParent.setTypeParent(DDIItemType.DDI_INSTANCE);
        itemWithParent.setItem(item);
        itemWithParent.setItemNode(itemNode);
        itemWithParent.setParentNode(parentNode);
        itemWithParent.setRessourcePackage(ressourcePackage);
        itemWithParent.setRessourcePackageNode(ressourcePackageNode);

        assertTrue(
                itemWithParent.toString().contains(String.valueOf(itemWithParent.getItem())) &&
                itemWithParent.toString().contains(String.valueOf(itemWithParent.getParent())) &&
                itemWithParent.toString().contains(String.valueOf(itemWithParent.getTypeParent())) &&
                !itemWithParent.toString().contains(String.valueOf(itemWithParent.getItemNode()))&&
                !itemWithParent.toString().contains(String.valueOf(itemWithParent.getParentNode())) &&
                !itemWithParent.toString().contains(String.valueOf(itemWithParent.getRessourcePackage()))&&
                !itemWithParent.toString().contains(String.valueOf(itemWithParent.getRessourcePackageNode()))
        );
    }
}