package it.fadeout.omirl.business.config;

public class MapInfoAggregationConfig {
	String modifier;
	String shapeFile;
	String path;
	String fieldID;
	float undefValue = -999000000.0f;
	
	public String getModifier() {
		return modifier;
	}
	public void setModifier(String modifier) {
		this.modifier = modifier;
	}
	public String getShapeFile() {
		return shapeFile;
	}
	public void setShapeFile(String shapeFile) {
		this.shapeFile = shapeFile;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getFieldID() {
		return fieldID;
	}
	public void setFieldID(String fieldID) {
		this.fieldID = fieldID;
	}
	public float getUndefValue() {
		return undefValue;
	}
	public void setUndefValue(float undefValue) {
		this.undefValue = undefValue;
	}
}
