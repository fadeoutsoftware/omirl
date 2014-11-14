package it.fadeout.omirl.business;

import java.util.ArrayList;

public class WindSummaryConfiguration {
	ArrayList<String> costalCodes = new ArrayList<>();
	ArrayList<String> internalCodes = new ArrayList<>();
	
	public ArrayList<String> getCostalCodes() {
		return costalCodes;
	}
	public void setCostalCodes(ArrayList<String> costalCodes) {
		this.costalCodes = costalCodes;
	}
	public ArrayList<String> getInternalCodes() {
		return internalCodes;
	}
	public void setInternalCodes(ArrayList<String> internalCodes) {
		this.internalCodes = internalCodes;
	}
	

}
