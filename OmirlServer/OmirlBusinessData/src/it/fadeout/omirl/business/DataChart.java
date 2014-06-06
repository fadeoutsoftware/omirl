package it.fadeout.omirl.business;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DataChart {
	String title;
	String subTitle;
	

    Double axisYMinValue;
    Double axisYMaxValue;
    Double axisYTickInterval;
    String axisYTitle;
    String tooltipValueSuffix;
	
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

	public Double getAxisYMinValue() {
		return axisYMinValue;
	}

	public void setAxisYMinValue(Double axisYMinValue) {
		this.axisYMinValue = axisYMinValue;
	}

	public Double getAxisYMaxValue() {
		return axisYMaxValue;
	}

	public void setAxisYMaxValue(Double axisYMaxValue) {
		this.axisYMaxValue = axisYMaxValue;
	}

	public Double getAxisYTickInterval() {
		return axisYTickInterval;
	}

	public void setAxisYTickInterval(Double axisYTickInterval) {
		this.axisYTickInterval = axisYTickInterval;
	}

	public String getAxisYTitle() {
		return axisYTitle;
	}

	public void setAxisYTitle(String axisYTitle) {
		this.axisYTitle = axisYTitle;
	}

	public String getTooltipValueSuffix() {
		return tooltipValueSuffix;
	}

	public void setTooltipValueSuffix(String tooltipValueSuffix) {
		this.tooltipValueSuffix = tooltipValueSuffix;
	}

}
