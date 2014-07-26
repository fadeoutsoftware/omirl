package it.fadeout.omirl.business;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="creek_thresholds")
public class CreekThreshold {
	@Id
	@Column(name="code")
	String code;
	@Column(name="ymin")
	Double ymin;
	@Column(name="ymax")
	Double ymax;
	@Column(name="sms")
	Double sms;
	@Column(name="orange")
	Double orange;
	@Column(name="red")
	Double red;
	@Column(name="flood")
	Double flood;
	@Column(name="black")
	Double black;
	@Column(name="white")
	Double white;
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public Double getYmin() {
		return ymin;
	}
	public void setYmin(Double ymin) {
		this.ymin = ymin;
	}
	public Double getYmax() {
		return ymax;
	}
	public void setYmax(Double ymax) {
		this.ymax = ymax;
	}
	public Double getSms() {
		return sms;
	}
	public void setSms(Double sms) {
		this.sms = sms;
	}
	public Double getOrange() {
		return orange;
	}
	public void setOrange(Double orange) {
		this.orange = orange;
	}
	public Double getRed() {
		return red;
	}
	public void setRed(Double red) {
		this.red = red;
	}
	public Double getFlood() {
		return flood;
	}
	public void setFlood(Double flood) {
		this.flood = flood;
	}
	public Double getBlack() {
		return black;
	}
	public void setBlack(Double black) {
		this.black = black;
	}
	public Double getWhite() {
		return white;
	}
	public void setWhite(Double white) {
		this.white = white;
	}
}
