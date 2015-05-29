package it.fadeout.omirl.business;

public class SectionLayerInfo {
	String modelName;
	String modelCode;
	String flagColumn;
	Boolean hasSubFolders=false;
	
	public String getModelName() {
		return modelName;
	}
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
	public String getModelCode() {
		return modelCode;
	}
	public void setModelCode(String modelCode) {
		this.modelCode = modelCode;
	}
	public String getFlagColumn() {
		return flagColumn;
	}
	public void setFlagColumn(String flagColumn) {
		this.flagColumn = flagColumn;
	}
	public Boolean getHasSubFolders() {
		return hasSubFolders;
	}
	public void setHasSubFolders(Boolean hasSubFolders) {
		this.hasSubFolders = hasSubFolders;
	}

}
