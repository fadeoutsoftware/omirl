package it.fadeout.omirl.business;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class SummaryInfoEntity {
	@Id
	@Column(name="station_name")
	String stationName;
	@Column(name="value")
	double value;
	@Column(name="reference_date")
	Date referenceDate;
	
	public String getStationName() {
		return stationName;
	}
	public void setStationName(String stationName) {
		this.stationName = stationName;
	}
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	public Date getReferenceDate() {
		return referenceDate;
	}
	public void setReferenceDate(Date referenceDate) {
		this.referenceDate = referenceDate;
	}
}
