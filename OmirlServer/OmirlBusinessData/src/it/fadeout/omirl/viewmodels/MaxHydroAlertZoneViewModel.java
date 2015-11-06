package it.fadeout.omirl.viewmodels;

import java.util.ArrayList;
import java.util.Date;

import javax.persistence.Transient;

import com.owlike.genson.convert.DefaultConverters.PrimitiveConverterFactory.doubleConverter;

public class MaxHydroAlertZoneViewModel {
	
	private ArrayList<MaxHydroAlertZoneRowViewModel> alertZonesA = new ArrayList<>();
	private ArrayList<MaxHydroAlertZoneRowViewModel> alertZonesB = new ArrayList<>();
	private ArrayList<MaxHydroAlertZoneRowViewModel> alertZonesC = new ArrayList<>();
	private ArrayList<MaxHydroAlertZoneRowViewModel> alertZonesD = new ArrayList<>();
	private ArrayList<MaxHydroAlertZoneRowViewModel> alertZonesE = new ArrayList<>();
	private ArrayList<MaxHydroAlertZoneRowViewModel> alertZonesM = new ArrayList<>();
	private ArrayList<MaxHydroAlertZoneRowViewModel> alertZonesCPlus = new ArrayList<>();
	private ArrayList<MaxHydroAlertZoneRowViewModel> alertZonesCLess = new ArrayList<>();
	
	@Transient
	private String updateDateTime;
	
	public ArrayList<MaxHydroAlertZoneRowViewModel> getAlertZonesA() {
		return alertZonesA;
	}
	public void setAlertZonesA(ArrayList<MaxHydroAlertZoneRowViewModel> alertZonesA) {
		this.alertZonesA = alertZonesA;
	}
	public ArrayList<MaxHydroAlertZoneRowViewModel> getAlertZonesB() {
		return alertZonesB;
	}
	public void setAlertZonesB(ArrayList<MaxHydroAlertZoneRowViewModel> alertZonesB) {
		this.alertZonesB = alertZonesB;
	}
	public ArrayList<MaxHydroAlertZoneRowViewModel> getAlertZonesC() {
		return alertZonesC;
	}
	public void setAlertZonesC(ArrayList<MaxHydroAlertZoneRowViewModel> alertZonesC) {
		this.alertZonesC = alertZonesC;
	}
	public ArrayList<MaxHydroAlertZoneRowViewModel> getAlertZonesD() {
		return alertZonesD;
	}
	public void setAlertZonesD(ArrayList<MaxHydroAlertZoneRowViewModel> alertZonesD) {
		this.alertZonesD = alertZonesD;
	}
	public ArrayList<MaxHydroAlertZoneRowViewModel> getAlertZonesE() {
		return alertZonesE;
	}
	public void setAlertZonesE(ArrayList<MaxHydroAlertZoneRowViewModel> alertZonesE) {
		this.alertZonesE = alertZonesE;
	}
	public ArrayList<MaxHydroAlertZoneRowViewModel> getAlertZonesM() {
		return alertZonesM;
	}
	public void setAlertZonesM(ArrayList<MaxHydroAlertZoneRowViewModel> alertZonesM) {
		this.alertZonesM = alertZonesM;
	}
	public ArrayList<MaxHydroAlertZoneRowViewModel> getAlertZonesCPlus() {
		return alertZonesCPlus;
	}
	public void setAlertZonesCPlus(ArrayList<MaxHydroAlertZoneRowViewModel> alertZonesCPlus) {
		this.alertZonesCPlus = alertZonesCPlus;
	}
	public ArrayList<MaxHydroAlertZoneRowViewModel> getAlertZonesCLess() {
		return alertZonesCLess;
	}
	public void setAlertZonesCLess(ArrayList<MaxHydroAlertZoneRowViewModel> alertZonesCLess) {
		this.alertZonesCLess = alertZonesCLess;
	}
	public String getUpdateDateTime() {
		return updateDateTime;
	}
	public void setUpdateDateTime(String updateDateTime) {
		this.updateDateTime = updateDateTime;
	}
}
