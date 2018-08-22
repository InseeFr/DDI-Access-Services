package fr.insee.rmes.metadata.model;

import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import fr.insee.rmes.metadata.utils.XpathProcessor;
import fr.insee.rmes.metadata.utils.XpathProcessorImpl;

public class DDIfragment implements Comparable<DDIfragment> {

	private String uuid;
	private String version;
	private String agency;

	private String fragment;

	private List<Node> nodesUpdatedOrCreated;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getAgency() {
		return agency;
	}

	public void setAgency(String agency) {
		this.agency = agency;
	}

	public String getFragment() {
		return fragment;
	}

	public void setFragment(String fragment) {
		this.fragment = fragment;
	}

	public List<Node> getNodesUpdatedOrCreated() {
		return nodesUpdatedOrCreated;
	}

	public void setNodesUpdatedOrCreated(List<Node> nodesUpdatedOrCreated) {
		this.nodesUpdatedOrCreated = nodesUpdatedOrCreated;
	}

	@Override
	public String toString() {
		return "DDIfragment [uuid=" + uuid + ", version=" + version + ", agency=" + agency + ", fragment=" + fragment
				+ ", children=" + nodesUpdatedOrCreated + "]";
	}

	@Override
	public int compareTo(DDIfragment o) {
		int result = 0;
		XpathProcessor xpathProcessor = new XpathProcessorImpl();
		NodeList nodesItemGot, nodesNewItem;
		try {
			nodesItemGot = xpathProcessor.queryList(this.getFragment(), "/Fragment/*");
			nodesNewItem = xpathProcessor.queryList(o.getFragment(), "/Fragment/*");

			if (!(nodesItemGot.getLength() == nodesNewItem.getLength())) {
				if (!(nodesItemGot.item(0).getTextContent().equals(nodesNewItem.item(0).getTextContent()))) {
					this.getNodesUpdatedOrCreated().add(nodesNewItem.item(0));
				}
				result = 1;
			} else {
				for (int i = 0; i < nodesNewItem.getLength(); i++) {

					if (!(nodesNewItem.item(i).getTextContent().equals(nodesItemGot.item(i).getTextContent()))) {
						this.getNodesUpdatedOrCreated().add(nodesNewItem.item(0));
						result = 1;
					}

				}
			}
			return 0;

		} catch (Exception e) {
			return 1;
		}

	}

}
