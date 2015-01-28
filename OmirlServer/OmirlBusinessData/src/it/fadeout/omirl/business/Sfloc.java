package it.fadeout.omirl.business;

import it.fadeout.omirl.viewmodels.SensorViewModel;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name="lamps")
public class Sfloc {
	
	@Id
	@Column(name="id")
	private
	Integer id;
	
	@Column(name="dtrfsec")
	private
	Date dtrfsec;
	
	@Column(name="lat")
	private
	Integer lat;
	
	@Column(name="lon")
	private
	Integer lon;
	
	@Column(name="intensity")
	private
	Integer intensity;
	
	@Column(name="commt")
	private
	String commt;
	
	@Column(name="dt_insert")
	private
	Date dt_insert;
	
	public SensorViewModel GetSflocViewModel()
	{
		SensorViewModel oViewModel = new SensorViewModel();
		
		oViewModel.setShortCode("");
		oViewModel.setAlt(-1);
		oViewModel.setMunicipality("Fulmine");
		oViewModel.setName(this.getCommt());
		oViewModel.setLat(this.getLat().doubleValue() / 100000);
		oViewModel.setLon(this.getLon().doubleValue() / 100000);
		oViewModel.setValue(this.getIntensity());
		oViewModel.setRefDate(this.dtrfsec);
		
		return oViewModel;
	}

	public Integer getLat() {
		return lat;
	}

	public void setLat(Integer lat) {
		this.lat = lat;
	}

	public Date getDtrfsec() {
		return dtrfsec;
	}

	public void setDtrfsec(Date dtrfsec) {
		this.dtrfsec = dtrfsec;
	}

	public Integer getLon() {
		return lon;
	}

	public void setLon(Integer lon) {
		this.lon = lon;
	}

	public Integer getIntensity() {
		return intensity;
	}

	public void setIntensity(Integer intensity) {
		this.intensity = intensity;
	}

	public String getCommt() {
		return commt;
	}

	public void setCommt(String commt) {
		this.commt = commt;
	}

	public Date getDtinsert() {
		return dt_insert;
	}

	public void setDtinsert(Date dt_insert) {
		this.dt_insert = dt_insert;
	}
}
