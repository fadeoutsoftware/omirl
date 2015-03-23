package it.fadeout.omirl.viewmodels;

import java.util.ArrayList;

public class SectionChartViewModel {
	
	/**
	 * Chart Image Link
	 */
	String sImageLink;
	
	/**
	 * Other Chart links
	 */
	ArrayList<String> otherChart = new ArrayList<>();

	public String getsImageLink() {
		return sImageLink;
	}

	public void setsImageLink(String sImageLink) {
		this.sImageLink = sImageLink;
	}

	public ArrayList<String> getOtherChart() {
		return otherChart;
	}

	public void setOtherChart(ArrayList<String> otherChart) {
		this.otherChart = otherChart;
	}	
}
