package it.fadeout.omirl.business.config;

import it.fadeout.omirl.viewmodels.StaticLink;

public class StaticLinkConfig {
	String description;
	String layerWMS;
	String layerID;
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getLayerWMS() {
		return layerWMS;
	}
	public void setLayerWMS(String layerWMS) {
		this.layerWMS = layerWMS;
	}
	public String getLayerID() {
		return layerID;
	}
	public void setLayerID(String layerID) {
		this.layerID = layerID;
	}
	
	public StaticLink getStaticLink() {
		StaticLink oStatic = new StaticLink();
		oStatic.setDescription(description);
		oStatic.setLayerID(layerID);
		oStatic.setLayerWMS(layerWMS);
		oStatic.setSelected(false);
		return oStatic;
	}
}
