package it.fadeout.omirl.viewmodels;

import java.util.ArrayList;
import java.util.Date;

import javax.persistence.Transient;

public class MaxTableViewModel {
	Date generationDate;
	
	ArrayList<MaxTableRowViewModel> alertZones = new ArrayList<>();

	ArrayList<MaxTableRowViewModel> districts = new ArrayList<>();
	
	@Transient
	private
	String updateDateTime;

	public Date getGenerationDate() {
		return generationDate;
	}

	public void setGenerationDate(Date generationDate) {
		this.generationDate = generationDate;
	}

	public ArrayList<MaxTableRowViewModel> getAlertZones() {
		return alertZones;
	}

	public void setAlertZones(ArrayList<MaxTableRowViewModel> alertZones) {
		this.alertZones = alertZones;
	}

	public ArrayList<MaxTableRowViewModel> getDistricts() {
		return districts;
	}

	public void setDistricts(ArrayList<MaxTableRowViewModel> districts) {
		this.districts = districts;
	}

	public String getUpdateDateTime() {
		return updateDateTime;
	}

	public void setUpdateDateTime(String updateDateTime) {
		this.updateDateTime = updateDateTime;
	}
}
