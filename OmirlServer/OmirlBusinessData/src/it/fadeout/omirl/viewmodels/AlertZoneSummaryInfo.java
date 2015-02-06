package it.fadeout.omirl.viewmodels;

import java.util.Date;

public class AlertZoneSummaryInfo {
	String description;
	String stationMin;
	double min;
	String stationMax;
	double max;
	Date refDateMin;
	Date refDateMax;
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getStationMin() {
		return stationMin;
	}
	public void setStationMin(String stationMin) {
		this.stationMin = stationMin;
	}
	public double getMin() {
		return min;
	}
	public void setMin(double min) {
		this.min = min;
	}
	public String getStationMax() {
		return stationMax;
	}
	public void setStationMax(String stationMax) {
		this.stationMax = stationMax;
	}
	public double getMax() {
		return max;
	}
	public void setMax(double max) {
		this.max = max;
	}
	public Date getRefDateMin() {
		return refDateMin;
	}
	public void setRefDateMin(Date refDateMin) {
		this.refDateMin = refDateMin;
	}
	public Date getRefDateMax() {
		return refDateMax;
	}
	public void setRefDateMax(Date refDateMax) {
		this.refDateMax = refDateMax;
	}
}
