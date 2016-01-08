package it.fadeout.omirl.business.config;

import it.fadeout.omirl.viewmodels.MapLink;

import java.util.ArrayList;

public class MapLinkConfig {
	String link;
	int linkId;
	boolean hasThirdLevel;
	String description;
	String layerWMS;
	String legendLink;
	String layerID;
	int accessLevel;
	
	ArrayList<MapThirdLevelLinkConfig> thirdLevels = new ArrayList<MapThirdLevelLinkConfig>();
	ArrayList<MapLinkConfig> secondLevels = new ArrayList<MapLinkConfig>();
	
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public int getLinkId() {
		return linkId;
	}
	public void setLinkId(int linkId) {
		this.linkId = linkId;
	}
	public boolean isHasThirdLevel() {
		return hasThirdLevel;
	}
	public void setHasThirdLevel(boolean hasThirdLevel) {
		this.hasThirdLevel = hasThirdLevel;
	}
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
	public String getLegendLink() {
		return legendLink;
	}
	public void setLegendLink(String legendLink) {
		this.legendLink = legendLink;
	}
	public String getLayerID() {
		return layerID;
	}
	public void setLayerID(String layerID) {
		this.layerID = layerID;
	}
	
	public ArrayList<MapLinkConfig> getSecondLevels() {
		return secondLevels;
	}
	public void setSecondLevels(ArrayList<MapLinkConfig> secondLevels) {
		this.secondLevels = secondLevels;
	}	
	
	public ArrayList<MapThirdLevelLinkConfig> getThirdLevels() {
		return thirdLevels;
	}
	public void setThirdLevels(ArrayList<MapThirdLevelLinkConfig> thirdLevels) {
		this.thirdLevels = thirdLevels;
	}
	
	public MapLink getMapLink() {
		MapLink oLink = new MapLink();
		
		oLink.setDescription(description);
		oLink.setHasThirdLevel(hasThirdLevel);
		oLink.setLayerID(layerID);
		oLink.setLayerWMS(layerWMS);
		oLink.setLegendLink(legendLink);
		oLink.setLink(link);
		oLink.setLinkId(linkId);
		oLink.setSelected(false);
		
		return oLink;
	}
	public int getAccessLevel() {
		return accessLevel;
	}
	public void setAccessLevel(int accessLevel) {
		this.accessLevel = accessLevel;
	}
}
