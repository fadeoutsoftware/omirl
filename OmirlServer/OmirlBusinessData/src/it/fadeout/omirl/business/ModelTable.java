package it.fadeout.omirl.business;

public class ModelTable {

	private String modelCode;
	
	private String modelName;
	
	private Boolean hasSubFolders=false;

	public String getModelCode() {
		return modelCode;
	}

	public void setModelCode(String modelCode) {
		this.modelCode = modelCode;
	}

	public Boolean getHasSubFolders() {
		return hasSubFolders;
	}

	public void setHasSubFolders(Boolean hasSubFolders) {
		this.hasSubFolders = hasSubFolders;
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
}
