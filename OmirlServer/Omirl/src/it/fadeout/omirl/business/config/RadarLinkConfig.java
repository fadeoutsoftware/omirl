package it.fadeout.omirl.business.config;

import it.fadeout.omirl.viewmodels.RadarLink;

import java.util.ArrayList;

public class RadarLinkConfig {
	// Unique link id
	String linkCode;	
	// Link to the icon image
	String link;
	// Flag to detect if exists a third level
	boolean hasThirdLevel;
	// Tooltip
	String description;
	// Path
	String filePath;
	// Link to the legend image path
	String legendLink;
	// Link to the WMS server
	String layerWMS;
	
	ArrayList<RadarLinkConfig> children = new ArrayList<RadarLinkConfig>();
	/*
	String code;
	int count;
	String mesUnit;
	String columnName;
	*/	
	
	public String getLinkCode() {
		return linkCode;
	}
	public void setLinkCode(String linkCode) {
		this.linkCode = linkCode;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
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
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getLegendLink() {
		return legendLink;
	}
	public void setLegendLink(String legendLink) {
		this.legendLink = legendLink;
	}
	public ArrayList<RadarLinkConfig> getChildren() {
		return children;
	}
	public void setChildren(ArrayList<RadarLinkConfig> children) {
		this.children = children;
	}
	
	public RadarLink getRadarLink() {
		RadarLink oLink = new RadarLink();
		
		oLink.setDescription(description);
		oLink.setHasThirdLevel(hasThirdLevel);
		oLink.setLegendLink(legendLink);
		oLink.setLink(link);
		oLink.setLinkCode(linkCode);
		oLink.setSelected(false);
		oLink.setLayerWMS(layerWMS);
		
		if (this.children == null) {
			oLink.setHasChilds(false);
		}
		else {
			if (this.children.size()>0) oLink.setHasChilds(true);
			else oLink.setHasChilds(false);
		}
		
		return oLink;
	}
	public String getLayerWMS() {
		return layerWMS;
	}
	public void setLayerWMS(String layerWMS) {
		this.layerWMS = layerWMS;
	}
}
