package it.fadeout.omirl.business.config;

import it.fadeout.omirl.viewmodels.TableLink;

public class TableLinkConfig {
	String code;
	String description;
	String imageLinkOff;
	boolean isActive;
	String location;
	boolean isPrivate;
	int accessLevel;
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getImageLinkOff() {
		return imageLinkOff;
	}
	public void setImageLinkOff(String imageLinkOff) {
		this.imageLinkOff = imageLinkOff;
	}
	public boolean isActive() {
		return isActive;
	}
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public boolean isPrivate() {
		return isPrivate;
	}
	public void setPrivate(boolean isPrivate) {
		this.isPrivate = isPrivate;
	}
	
	public TableLink getTableLink() {
		TableLink oTableLink = new TableLink();
		oTableLink.setActive(this.isActive());
		oTableLink.setCode(this.getCode());
		oTableLink.setDescription(this.getDescription());
		oTableLink.setImageLinkOff(this.getImageLinkOff());
		oTableLink.setLocation(this.getLocation());
		
		return oTableLink;
	}
	public int getAccessLevel() {
		return accessLevel;
	}
	public void setAccessLevel(int accessLevel) {
		this.accessLevel = accessLevel;
	}

}
