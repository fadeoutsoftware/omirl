package it.fadeout.omirl.viewmodels;

import java.util.Date;

public class WindSummaryInfo {
	String description;
	String stationGust;
	double gust;
	String stationMax;
	double max;
	Date refDateWind;
	Date refDateGust;
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
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
	public String getStationGust() {
		return stationGust;
	}
	public void setStationGust(String stationGust) {
		this.stationGust = stationGust;
	}
	public double getGust() {
		return gust;
	}
	public void setGust(double gust) {
		this.gust = gust;
	}
	public Date getRefDateWind() {
		return refDateWind;
	}
	public void setRefDateWind(Date refDateWind) {
		this.refDateWind = refDateWind;
	}
	public Date getRefDateGust() {
		return refDateGust;
	}
	public void setRefDateGust(Date refDateGust) {
		this.refDateGust = refDateGust;
	}

}
