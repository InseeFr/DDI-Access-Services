package fr.insee.rmes.model;

import fr.insee.rmes.model.DDIItemType;
import org.w3c.dom.Node;

public class ItemWithParent {

	private ColecticaItem item;

	private Node itemNode;

	private ColecticaItem parent;

	private Node parentNode;

	private ColecticaItem ressourcePackage;

	private Node ressourcePackageNode;

	private DDIItemType typeParent;

	public ColecticaItem getItem() {
		return item;
	}

	public void setItem(ColecticaItem item) {
		this.item = item;
	}

	public ColecticaItem getParent() {
		return parent;
	}

	public void setParent(ColecticaItem parent) {
		this.parent = parent;
	}

	public Node getItemNode() {
		return itemNode;
	}

	public void setItemNode(Node itemNode) {
		this.itemNode = itemNode;
	}

	public Node getParentNode() {
		return parentNode;
	}

	public void setParentNode(Node parentNode) {
		this.parentNode = parentNode;
	}

	public DDIItemType getTypeParent() {
		return typeParent;
	}

	public void setTypeParent(DDIItemType typeParent) {
		this.typeParent = typeParent;
	}
	
	

	public ColecticaItem getRessourcePackage() {
		return ressourcePackage;
	}

	public void setRessourcePackage(ColecticaItem ressourcePackage) {
		this.ressourcePackage = ressourcePackage;
	}

	public Node getRessourcePackageNode() {
		return ressourcePackageNode;
	}

	public void setRessourcePackageNode(Node ressourcePackageNode) {
		this.ressourcePackageNode = ressourcePackageNode;
	}

	@Override
	public String toString() {
		return "ItemWithParent [item=" + item + ", parent=" + parent + ", typeParent=" + typeParent + "]";
	}

}
