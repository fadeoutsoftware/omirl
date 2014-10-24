package it.fadeout.omirl.business;

public class DynamicLayerInfo {
	String styleId;
	String layerId;
	boolean isShapeFile;
	public String getStyleId() {
		return styleId;
	}
	public void setStyleId(String styleId) {
		this.styleId = styleId;
	}
	public String getLayerId() {
		return layerId;
	}
	public void setLayerId(String layerId) {
		this.layerId = layerId;
	}
	public boolean isShapeFile() {
		return isShapeFile;
	}
	public void setShapeFile(boolean isShapeFile) {
		this.isShapeFile = isShapeFile;
	}
}
