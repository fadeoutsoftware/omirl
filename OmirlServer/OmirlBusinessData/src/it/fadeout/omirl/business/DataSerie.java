package it.fadeout.omirl.business;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@XmlRootElement
public class DataSerie {
	String type;
	String name;
	int axisId = 0;
	List<Object []> data = new ArrayList<>();
	
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
	public List<Object []> getData() {
		return data;
	}
	public void setData(List<Object []> data) {
		this.data = data;
	}
	public int getAxisId() {
		return axisId;
	}
	public void setAxisId(int axisId) {
		this.axisId = axisId;
	}
	
}
