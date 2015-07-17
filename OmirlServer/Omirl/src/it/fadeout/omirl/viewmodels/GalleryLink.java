package it.fadeout.omirl.viewmodels;

import java.util.ArrayList;

public class GalleryLink {
	String code;
	String description;
	String imageLinkOff;
	boolean isActive;
	String location;
	private String codeVariable;
	private String codeParent;
	private ArrayList<GalleryLink> sublevelGalleryLink = new ArrayList<GalleryLink>();
	
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
	public ArrayList<GalleryLink> getSublevelGalleryLink() {
		return sublevelGalleryLink;
	}
	public void setSublevelGalleryLink(ArrayList<GalleryLink> sublevelGalleryLink) {
		this.sublevelGalleryLink = sublevelGalleryLink;
	}
	public String getCodeVariable() {
		return codeVariable;
	}
	public void setCodeVariable(String codeVariable) {
		this.codeVariable = codeVariable;
	}
	public String getCodeParent() {
		return codeParent;
	}
	public void setCodeParent(String codeParent) {
		this.codeParent = codeParent;
	}
}
