package it.fadeout.omirl.viewmodels;

import java.util.ArrayList;

public class SectionChartViewModel {
	
	/**
	 * Chart Image Link
	 */
	String imageLink;
	
	/**
	 * Other Chart links
	 */
	ArrayList<String> otherChart = new ArrayList<>();

	public String getImageLink() {
		return imageLink;
	}

	public void setImageLink(String sImageLink) {
		this.imageLink = sImageLink;
	}

	public ArrayList<String> getOtherChart() {
		return otherChart;
	}

	public void setOtherChart(ArrayList<String> otherChart) {
		this.otherChart = otherChart;
	}	
}
