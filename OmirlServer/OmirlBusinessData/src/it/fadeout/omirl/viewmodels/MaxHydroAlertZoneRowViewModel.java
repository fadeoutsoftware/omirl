package it.fadeout.omirl.viewmodels;

import java.util.Date;

public class MaxHydroAlertZoneRowViewModel {

	private String station;
	private String code;
	private String municipality;
	private String district;
	private String basin;
	private String river;
	private Date date24HMax;
	private double valueOnDate24HMax;
	private Date dateRef;
	private double valueOnDateRef;
	private String warnArea;
	
	public String getStation() {
		return station;
	}
	public void setStation(String station) {
		this.station = station;
	}
	public String getMunicipality() {
		return municipality;
	}
	public void setMunicipality(String municipality) {
		this.municipality = municipality;
	}
	public String getDistrict() {
		return district;
	}
	public void setDistrict(String district) {
		this.district = district;
	}
	public String getBasin() {
		return basin;
	}
	public void setBasin(String basin) {
		this.basin = basin;
	}
	public String getRiver() {
		return river;
	}
	public void setRiver(String river) {
		this.river = river;
	}
	
	public String getWarnArea() {
		return warnArea;
	}
	public void setWarnArea(String warnArea) {
		this.warnArea = warnArea;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public Date getDate24HMax() {
		return date24HMax;
	}
	public void setDate24HMax(Date dateMax) {
		this.date24HMax = dateMax;
	}
	public double getValueOnDate24HMax() {
		return valueOnDate24HMax;
	}
	public void setValueOnDate24HMax(double valueOnDateMax) {
		this.valueOnDate24HMax = valueOnDateMax;
	}
	public Date getDateRef() {
		return dateRef;
	}
	public void setDateRef(Date dateRef) {
		this.dateRef = dateRef;
	}
	public double getValueOnDateRef() {
		return valueOnDateRef;
	}
	public void setValueOnDateRef(double valueOnDateRef) {
		this.valueOnDateRef = valueOnDateRef;
	}
	
}
