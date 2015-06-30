package it.fadeout.omirl.business;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class WindDataSeriePoint {
	
	@Id
	@Column(name="reference_date")
	Date refDate;
	
	@Column(name="mean_wind_speed")
	private
	double windSpeed;
	
	@Column(name="prevailing_wind_dir")
	private
	double windDir;
	
	public Date getRefDate() {
		return refDate;
	}
	public void setRefDate(Date refDate) {
		this.refDate = refDate;
	}
	public double getWindSpeed() {
		return windSpeed;
	}
	public void setWindSpeed(double windSpeed) {
		this.windSpeed = windSpeed;
	}
	public double getWindDir() {
		return windDir;
	}
	public void setWindDir(double windDir) {
		this.windDir = windDir;
	}
	
}
