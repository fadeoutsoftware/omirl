package it.fadeout.omirl.viewmodels;

import it.fadeout.omirl.business.config.LegendStepConfig;

import java.util.ArrayList;

public class SensorLink {
	String code;
	String description;
	String imageLinkOn;
	String imageLinkOff;
	String imageLinkInv;
	int count;
	boolean isActive;
	String legendLink;
	String mesUnit;
	boolean isVisible = true;
	boolean isClickable = true;
	private ArrayList<LegendStepConfig> legends = new ArrayList<LegendStepConfig>();
	
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
	public boolean isActive() {
		return isActive;
	}
	public void setActive(boolean isActive) {
		this.isActive = isActive;
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
	public boolean isVisible() {
		return isVisible;
	}
	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}
	public boolean isClickable() {
		return isClickable;
	}
	public void setClickable(boolean isClickable) {
		this.isClickable = isClickable;
	}
	public ArrayList<LegendStepConfig> getLegends() {
		return legends;
	}
	public void setLegends(ArrayList<LegendStepConfig> legends) {
		this.legends = legends;
	}
}
