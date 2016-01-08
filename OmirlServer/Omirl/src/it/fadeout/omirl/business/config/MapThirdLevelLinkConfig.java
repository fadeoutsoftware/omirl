package it.fadeout.omirl.business.config;

import it.fadeout.omirl.viewmodels.MapThirdLevelLink;

public class MapThirdLevelLinkConfig {
	boolean isDefault;
	String description;
	String layerIDModifier;
	int accessLevel;
	
	public boolean isDefault() {
		return isDefault;
	}
	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getLayerIDModifier() {
		return layerIDModifier;
	}
	public void setLayerIDModifier(String layerIDModifier) {
		this.layerIDModifier = layerIDModifier;
	}
	
	public MapThirdLevelLink getMapThirdLevelLink() {
		MapThirdLevelLink oThird = new MapThirdLevelLink();
		oThird.setDefault(isDefault);
		oThird.setDescription(description);
		oThird.setLayerIDModifier(layerIDModifier);
		
		return oThird;
	}
	public int getAccessLevel() {
		return accessLevel;
	}
	public void setAccessLevel(int accessLevel) {
		this.accessLevel = accessLevel;
	}	
}
