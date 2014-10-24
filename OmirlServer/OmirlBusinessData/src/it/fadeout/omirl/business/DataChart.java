package it.fadeout.omirl.business;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DataChart {
	
	/**
	 * Chart Title
	 */
	String title;
	/**
	 * Chart SubTitle
	 */
	String subTitle;
	/**
	 * Tooltip suffix
	 */
    String tooltipValueSuffix;
    /**
     * Additional vertical axes
     */
    ArrayList<ChartAxis> verticalAxes = new ArrayList<>();
	/**
	 * Dataseries
	 */
	ArrayList<DataSerie> dataSeries = new ArrayList<>();
	/**
	 * Horizontal Lines
	 */
	ArrayList<ChartLine> horizontalLines = new ArrayList<>();
	
	/**
	 * Other Chart links
	 */
	ArrayList<String> otherChart = new ArrayList<>();

	/**
	 * Main Axes Min Value
	 */
    Double axisYMinValue;
    /**
     * Main Axes Max Value
     */
    Double axisYMaxValue;
    /**
     * Main Axes Tick interval 
     */
    Double axisYTickInterval;
    /**
     * Main Axes Title
     */
    String axisYTitle;
    /**
     * Flag to know if main Axis is opposite
     */
    boolean axisIsOpposite = false;
    
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



	public String getTooltipValueSuffix() {
		return tooltipValueSuffix;
	}

	public void setTooltipValueSuffix(String tooltipValueSuffix) {
		this.tooltipValueSuffix = tooltipValueSuffix;
	}

	public ArrayList<ChartLine> getHorizontalLines() {
		return horizontalLines;
	}

	public void setHorizontalLines(ArrayList<ChartLine> horizontalLines) {
		this.horizontalLines = horizontalLines;
	}

	public ArrayList<String> getOtherChart() {
		return otherChart;
	}

	public void setOtherChart(ArrayList<String> otherChart) {
		this.otherChart = otherChart;
	}

	public ArrayList<ChartAxis> getVerticalAxes() {
		return verticalAxes;
	}

	public void setVerticalAxes(ArrayList<ChartAxis> verticalAxes) {
		this.verticalAxes = verticalAxes;
	}

	public boolean isAxisIsOpposite() {
		return axisIsOpposite;
	}

	public void setAxisIsOpposite(boolean axisIsOpposite) {
		this.axisIsOpposite = axisIsOpposite;
	}
}
