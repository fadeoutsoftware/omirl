package it.fadeout.omirl.business.config;

import it.fadeout.omirl.viewmodels.SensorLink;

public class SensorLinkConfig {
	String code;
	String description;
	String imageLinkOn;
	String imageLinkOff;
	String imageLinkInv;
	int count;
	String legendLink;
	String mesUnit;
	String filePath;
	String columnName;
	boolean isVisible = true;
	boolean isClickable = true;
	
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
	public String getImageLinkOn() {
		return imageLinkOn;
	}
	public void setImageLinkOn(String imageLinkOn) {
		this.imageLinkOn = imageLinkOn;
	}
	public String getImageLinkOff() {
		return imageLinkOff;
	}
	public void setImageLinkOff(String imageLinkOff) {
		this.imageLinkOff = imageLinkOff;
	}
	public String getImageLinkInv() {
		return imageLinkInv;
	}
	public void setImageLinkInv(String imageLinkInv) {
		this.imageLinkInv = imageLinkInv;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getLegendLink() {
		return legendLink;
	}
	public void setLegendLink(String legendLink) {
		this.legendLink = legendLink;
	}
	public String getMesUnit() {
		return mesUnit;
	}
	public void setMesUnit(String mesUnit) {
		this.mesUnit = mesUnit;
	}
	
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	
	public SensorLink getSensorLink() {
		SensorLink oSensorLink = new SensorLink();
		oSensorLink.setActive(false);
		oSensorLink.setCode(code);
		oSensorLink.setCount(count);
		oSensorLink.setDescription(description);
		oSensorLink.setImageLinkInv(imageLinkInv);
		oSensorLink.setImageLinkOff(imageLinkOff);
		oSensorLink.setImageLinkOn(imageLinkOn);
		oSensorLink.setLegendLink(legendLink);
		oSensorLink.setMesUnit(mesUnit);
		oSensorLink.setClickable(isClickable);
		oSensorLink.setVisible(isVisible);
		
		return oSensorLink;
	}
	public boolean getIsVisible() {
		return isVisible;
	}
	public void setIsVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}
	public boolean getIsClickable() {
		return isClickable;
	}
	public void setIsClickable(boolean isClickable) {
		this.isClickable = isClickable;
	}

}
