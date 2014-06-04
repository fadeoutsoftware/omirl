package it.fadeout.omirl.business;

import java.util.ArrayList;

import javax.persistence.Entity;

@Entity
public class DataSerie {
	String type;
	String name;
	ArrayList<DataSeriePoint> data = new ArrayList<>();
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<DataSeriePoint> getData() {
		return data;
	}
	public void setData(ArrayList<DataSeriePoint> data) {
		this.data = data;
	}
	
}
