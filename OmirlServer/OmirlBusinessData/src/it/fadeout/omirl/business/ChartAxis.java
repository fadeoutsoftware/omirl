package it.fadeout.omirl.business;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ChartAxis {
    Double axisYMinValue;
    Double axisYMaxValue;
    Double axisYTickInterval;
    String axisYTitle;
    boolean isOpposite = false;
    
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

	public boolean isIsOpposite() {
		return isOpposite;
	}

	public void setIsOpposite(boolean isOpposite) {
		this.isOpposite = isOpposite;
	}
}
