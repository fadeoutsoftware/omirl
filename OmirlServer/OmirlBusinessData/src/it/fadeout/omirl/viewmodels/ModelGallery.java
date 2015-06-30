package it.fadeout.omirl.viewmodels;

import java.util.ArrayList;
import java.util.Date;

public class ModelGallery {
	String model;
	String variable;
	String subVarialbe;
	Date refDateMin;
	ArrayList<ModelImage> images = new ArrayList<ModelImage>();
	
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getVariable() {
		return variable;
	}
	public void setVariable(String variable) {
		this.variable = variable;
	}
	public String getSubVarialbe() {
		return subVarialbe;
	}
	public void setSubVarialbe(String subVarialbe) {
		this.subVarialbe = subVarialbe;
	}
	public Date getRefDateMin() {
		return refDateMin;
	}
	public void setRefDateMin(Date refDateMin) {
		this.refDateMin = refDateMin;
	}
	public ArrayList<ModelImage> getImages() {
		return images;
	}
	public void setImages(ArrayList<ModelImage> images) {
		this.images = images;
	}
}
