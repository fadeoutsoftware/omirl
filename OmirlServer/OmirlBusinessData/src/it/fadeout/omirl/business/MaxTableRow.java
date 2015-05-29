package it.fadeout.omirl.business;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class MaxTableRow {
	@Column(name="value")
	double value;
	@Column(name="reference_date")
	Date reference_date;
	@Column(name="station_name")
	String station_name;
	@Id
	@Column(name="station_code")
	String station_code;
	
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	public Date getReference_date() {
		return reference_date;
	}
	public void setReference_date(Date reference_date) {
		this.reference_date = reference_date;
	}
	public String getStation_name() {
		return station_name;
	}
	public void setStation_name(String station_name) {
		this.station_name = station_name;
	}
	public String getStation_code() {
		return station_code;
	}
	public void setStation_code(String station_code) {
		this.station_code = station_code;
	}
}
