package it.fadeout.omirl.business;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class DataSeriePoint {
	@Id
	@Column(name="reference_date")
	Date refDate;
	@Column(name="value")
	double val;
	
	public Date getRefDate() {
		return refDate;
	}
	public void setRefDate(Date refDate) {
		this.refDate = refDate;
	}
	public double getVal() {
		return val;
	}
	public void setVal(double value) {
		this.val = value;
	}
}
