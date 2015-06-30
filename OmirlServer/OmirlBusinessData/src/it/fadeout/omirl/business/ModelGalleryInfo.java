package it.fadeout.omirl.business;

import java.util.ArrayList;

public class ModelGalleryInfo {
	String model;	
	String codeModel;
	ArrayList<ModelImageInfo> variables = new ArrayList<ModelImageInfo>();
	
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}

	public String getCodeModel() {
		return codeModel;
	}
	public void setCodeModel(String codeModel) {
		this.codeModel = codeModel;
	}
	public ArrayList<ModelImageInfo> getVariables() {
		return variables;
	}
	public void setVariables(ArrayList<ModelImageInfo> variables) {
		this.variables = variables;
	}
}
