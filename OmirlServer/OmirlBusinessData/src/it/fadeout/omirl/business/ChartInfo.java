package it.fadeout.omirl.business;


public class ChartInfo {
	String name;
	String type;
	String subtitle;
	Double conversionFactor;
	double axisYMaxValue;
	double axisYMinValue;
	double axisYTickInterval;
	String axisYTitle;
	String tooltipValueSuffix;
	String columnName;
	String folderName;
	String sensorType;
	int daysLength;
	boolean hasFixedWindow;
	String dashStyle;
	int lineWidth = 2;
	String color;
	//Refresh in minutes
	int refreshTime;
	long lastRefreshTime;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getSubtitle() {
		return subtitle;
	}
	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}
	public Double getConversionFactor() {
		return conversionFactor;
	}
	public void setConversionFactor(Double conversionFactor) {
		this.conversionFactor = conversionFactor;
	}
	public double getAxisYMaxValue() {
		return axisYMaxValue;
	}
	public void setAxisYMaxValue(double axisYMaxValue) {
		this.axisYMaxValue = axisYMaxValue;
	}
	public double getAxisYMinValue() {
		return axisYMinValue;
	}
	public void setAxisYMinValue(double axisYMinValue) {
		this.axisYMinValue = axisYMinValue;
	}
	public double getAxisYTickInterval() {
		return axisYTickInterval;
	}
	public void setAxisYTickInterval(double axisYTickInterval) {
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
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	public String getFolderName() {
		return folderName;
	}
	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}
	public String getSensorType() {
		return sensorType;
	}
	public void setSensorType(String sensorType) {
		this.sensorType = sensorType;
	}
	public int getDaysLength() {
		return daysLength;
	}
	public void setDaysLength(int daysLength) {
		this.daysLength = daysLength;
	}
	public boolean isHasFixedWindow() {
		return hasFixedWindow;
	}
	public void setHasFixedWindow(boolean hasFixedWindow) {
		this.hasFixedWindow = hasFixedWindow;
	}
	
	public String getDashStyle() {
		return dashStyle;
	}
	public void setDashStyle(String dashStyle) {
		this.dashStyle = dashStyle;
	}
	public int getLineWidth() {
		return lineWidth;
	}
	public void setLineWidth(int lineWidth) {
		this.lineWidth = lineWidth;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public int getRefreshTime() {
		return refreshTime;
	}
	public void setRefreshTime(int refreshTime) {
		this.refreshTime = refreshTime;
	}
	public long getLastRefreshTime() {
		return lastRefreshTime;
	}
	public void setLastRefreshTime(long lastRefreshTime) {
		this.lastRefreshTime = lastRefreshTime;
	}
}
