package it.fadeout.omirl.business;

import it.fadeout.omirl.viewmodels.SensorViewModel;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class SensorLastData {
	@Id
	@Column(name="station_code")
	String station_code;
	@Column(name="municipality")
    String municipality;
	@Column(name="name")
    String name;
	@Column(name="lat")
    Double lat;
	@Column(name="lon")
    Double lon;
	@Column(name="elevation")
    Double elevation;
	@Column(name="reference_date")
	Date reference_date;
	@Column(name="sensorvalue")
	Double sensorvalue;
	@Column(name="sensorincrement")
	Double sensorincrement;
	
	public String getStation_code() {
		return station_code;
	}
	public void setStation_code(String station_code) {
		this.station_code = station_code;
	}
	public String getMunicipality() {
		return municipality;
	}
	public void setMunicipality(String municipality) {
		this.municipality = municipality;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Double getLat() {
		return lat;
	}
	public void setLat(Double lat) {
		this.lat = lat;
	}
	public Double getLon() {
		return lon;
	}
	public void setLon(Double lon) {
		this.lon = lon;
	}
	public Double getElevation() {
		return elevation;
	}
	public void setElevation(Double elevation) {
		this.elevation = elevation;
	}
	public Date getReference_date() {
		return reference_date;
	}
	public void setReference_date(Date reference_date) {
		this.reference_date = reference_date;
	}
	public Double getSensorvalue() {
		return sensorvalue;
	}
	public void setSensorvalue(Double sensorvalue) {
		this.sensorvalue = sensorvalue;
	}
	public Double getSensorincrement() {
		return sensorincrement;
	}
	public void setSensorincrement(Double sensorincrement) {
		this.sensorincrement = sensorincrement;
	}
	
	public SensorViewModel getSensorViewModel() {
		SensorViewModel oSensor = new SensorViewModel();
		
		if (this.lat == null || this.lon == null || this.station_code == null) {
			return null;
		}
		
		if (this.elevation != null) oSensor.setAlt(this.elevation.intValue());
		else oSensor.setAlt(-1);
		oSensor.setImgPath("");
		oSensor.setLat(this.lat.doubleValue() / 100000);
		oSensor.setLon(this.lon.doubleValue() / 100000);
		if (name != null) oSensor.setName(name);
		else oSensor.setName(this.station_code);
		if (this.getMunicipality()!= null) {
			oSensor.setMunicipality(this.getMunicipality());
		}
		else {
			oSensor.setMunicipality("-");
		}
		
		oSensor.setOtherHtml("");
		oSensor.setRefDate(this.reference_date);
		oSensor.setShortCode(this.station_code);
		oSensor.setStationId(1);
		oSensor.setValue(sensorvalue.doubleValue());
		
		if (sensorincrement != null) {
			oSensor.setIncrement(sensorincrement.intValue());
		}
		else {
			oSensor.setIncrement(0);
		}
		
		
		return oSensor;
	}
}
