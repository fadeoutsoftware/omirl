package it.fadeout.omirl.business;

public class SectionLayerInfo {
	String modelName;
	String modelCode;
	String flagColumn;
	Boolean hasSubFolders=false;
	private Boolean disableOnMap=false;
	Boolean timeRewind = false;
	int maxDelayMinutes = 120;
	
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
	public Boolean getDisableOnMap() {
		return disableOnMap;
	}
	public void setDisableOnMap(Boolean disableOnMap) {
		this.disableOnMap = disableOnMap;
	}
	public Boolean getTimeRewind() {
		return timeRewind;
	}
	public void setTimeRewind(Boolean timeRewind) {
		this.timeRewind = timeRewind;
	}
	public int getMaxDelayMinutes() {
		return maxDelayMinutes;
	}
	public void setMaxDelayMinutes(int maxDelayMinutes) {
		this.maxDelayMinutes = maxDelayMinutes;
	}

}
