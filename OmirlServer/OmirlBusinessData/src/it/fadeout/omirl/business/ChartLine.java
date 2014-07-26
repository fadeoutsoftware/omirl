package it.fadeout.omirl.business;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ChartLine {
	Double value;
	String color;
	String name;
	
	public Double getValue() {
		return value;
	}
	public void setValue(Double value) {
		this.value = value;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}	
}
