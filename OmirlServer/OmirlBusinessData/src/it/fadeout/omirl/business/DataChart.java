package it.fadeout.omirl.business;

import java.util.ArrayList;

public class DataChart {
	String title;
	String subTitle;
	
	ArrayList<DataSerie> dataSeries = new ArrayList<>();

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSubTitle() {
		return subTitle;
	}

	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}

	public ArrayList<DataSerie> getDataSeries() {
		return dataSeries;
	}

	public void setDataSeries(ArrayList<DataSerie> dataSeries) {
		this.dataSeries = dataSeries;
	}
}
