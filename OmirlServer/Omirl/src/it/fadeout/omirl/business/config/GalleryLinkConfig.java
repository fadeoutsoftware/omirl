package it.fadeout.omirl.business.config;

import it.fadeout.omirl.viewmodels.GalleryLink;

import java.util.ArrayList;

public class GalleryLinkConfig {
	String code;
	String description;
	String imageLinkOff;
	boolean isActive;
	String location;
	boolean isPrivate;
	private String codeVariable;
	private String codeParent;
	private ArrayList<GalleryLinkConfig> sublevelGalleryLinkConfig = new ArrayList<GalleryLinkConfig>();
	
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
	
	public String getCodeVariable() {
		return codeVariable;
	}
	public void setCodeVariable(String codeVariable) {
		this.codeVariable = codeVariable;
	}
	
	public ArrayList<GalleryLinkConfig> getSublevelGalleryLinkConfig() {
		return sublevelGalleryLinkConfig;
	}
	public void setSublevelGalleryLinkConfig(ArrayList<GalleryLinkConfig> sublevelGalleryLinkConfig) {
		this.sublevelGalleryLinkConfig = sublevelGalleryLinkConfig;
	}
	
	
	
	public GalleryLink getGalleryLink() {
		GalleryLink oGalleryLink = new GalleryLink();
		oGalleryLink.setActive(this.isActive());
		oGalleryLink.setCode(this.getCode());
		oGalleryLink.setDescription(this.getDescription());
		oGalleryLink.setImageLinkOff(this.getImageLinkOff());
		oGalleryLink.setLocation(this.getLocation());
		oGalleryLink.setCodeVariable(this.getCodeVariable());
		oGalleryLink.setCodeParent(null);
		ArrayList<GalleryLinkConfig> oSubLevel = this.getSublevelGalleryLinkConfig();
		if (oSubLevel != null)
		{
			for (GalleryLinkConfig galleryLinkConfig : oSubLevel) {
				GalleryLink oSubGalleryLink = new GalleryLink();
				oSubGalleryLink.setActive(galleryLinkConfig.isActive());
				oSubGalleryLink.setCode(galleryLinkConfig.getCode());
				oSubGalleryLink.setDescription(galleryLinkConfig.getDescription());
				oSubGalleryLink.setImageLinkOff(galleryLinkConfig.getImageLinkOff());
				oSubGalleryLink.setLocation(galleryLinkConfig.getLocation());
				oSubGalleryLink.setCodeVariable(galleryLinkConfig.getCodeVariable());
				oSubGalleryLink.setCodeParent(oGalleryLink.getCode());
				oGalleryLink.getSublevelGalleryLink().add(oSubGalleryLink);
			}
		}
		
		return oGalleryLink;
	}
	public String getCodeParent() {
		return codeParent;
	}
	public void setCodeParent(String codeParent) {
		this.codeParent = codeParent;
	}
	
	
}
