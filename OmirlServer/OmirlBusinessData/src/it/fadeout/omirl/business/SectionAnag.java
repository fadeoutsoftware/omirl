package it.fadeout.omirl.business;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="section_anag")
public class SectionAnag {
	@Id
	@Column(name="code")
	String code;
	@Column(name="mask")
	Integer  mask;
	@Column(name="lat")
	Double lat;
	@Column(name="lon")
	Double lon;
	@Column(name="name")
	String name;
	@Column(name="basin")
	String basin;
	@Column(name="river")
	String river;
	@Column(name="district")
	String district;
	@Column(name="ygird")
	Integer ygird;
	@Column(name="xgrid")
	Integer xgrid;
	@Column(name="basin_area")
	Double basin_area;
	@Column(name="basin_class")
	String basin_class;
	@Column(name="warn_area")
	String warn_area;
	@Column(name="elev")
	Double elev;
	@Column(name="rating_curve")
	String rating_curve;
	@Column(name="q_ord")
	Integer q_ord;
	@Column(name="q_extr")
	Integer q_extr;
	@Column(name="q_2")
	Integer q_2;
	@Column(name="q_2_9")
	Integer q_2_9;
	@Column(name="q_5")
	Integer q_5;
	@Column(name="q_10")
	Integer q_10;
	@Column(name="q_20")
	Integer q_20;
	@Column(name="q_30")
	Integer q_30;
	@Column(name="q_50")
	Integer q_50;
	@Column(name="q_100")
	Integer q_100;
	@Column(name="q_200")
	Integer q_200;
	@Column(name="osservato")
	Integer osservato;
	@Column(name="piccoli_bacini")
	Integer piccoli_bacini;
	@Column(name="deterministico")
	Integer deterministico;
	@Column(name="rainfarm")
	Integer rainfarm;
	@Column(name="radar")
	Integer radar;
	@Column(name="catena_magra")
	Integer catena_magra;
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public Integer getMask() {
		return mask;
	}
	public void setMask(Integer mask) {
		this.mask = mask;
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getBasin() {
		return basin;
	}
	public void setBasin(String basin) {
		this.basin = basin;
	}
	public String getRiver() {
		return river;
	}
	public void setRiver(String river) {
		this.river = river;
	}
	public String getDistrict() {
		return district;
	}
	public void setDistrict(String district) {
		this.district = district;
	}
	public Integer getYgird() {
		return ygird;
	}
	public void setYgird(Integer ygird) {
		this.ygird = ygird;
	}
	public Integer getXgrid() {
		return xgrid;
	}
	public void setXgrid(Integer xgrid) {
		this.xgrid = xgrid;
	}
	public Double getBasin_area() {
		return basin_area;
	}
	public void setBasin_area(Double basin_area) {
		this.basin_area = basin_area;
	}
	public String getBasin_class() {
		return basin_class;
	}
	public void setBasin_class(String basin_class) {
		this.basin_class = basin_class;
	}
	public String getWarn_area() {
		return warn_area;
	}
	public void setWarn_area(String warn_area) {
		this.warn_area = warn_area;
	}
	public Double getElev() {
		return elev;
	}
	public void setElev(Double elev) {
		this.elev = elev;
	}
	public String getRating_curve() {
		return rating_curve;
	}
	public void setRating_curve(String rating_curve) {
		this.rating_curve = rating_curve;
	}
	public Integer getQ_ord() {
		return q_ord;
	}
	public void setQ_ord(Integer q_ord) {
		this.q_ord = q_ord;
	}
	public Integer getQ_extr() {
		return q_extr;
	}
	public void setQ_extr(Integer q_extr) {
		this.q_extr = q_extr;
	}
	public Integer getQ_2() {
		return q_2;
	}
	public void setQ_2(Integer q_2) {
		this.q_2 = q_2;
	}
	public Integer getQ_2_9() {
		return q_2_9;
	}
	public void setQ_2_9(Integer q_2_9) {
		this.q_2_9 = q_2_9;
	}
	public Integer getQ_5() {
		return q_5;
	}
	public void setQ_5(Integer q_5) {
		this.q_5 = q_5;
	}
	public Integer getQ_10() {
		return q_10;
	}
	public void setQ_10(Integer q_10) {
		this.q_10 = q_10;
	}
	public Integer getQ_20() {
		return q_20;
	}
	public void setQ_20(Integer q_20) {
		this.q_20 = q_20;
	}
	public Integer getQ_30() {
		return q_30;
	}
	public void setQ_30(Integer q_30) {
		this.q_30 = q_30;
	}
	public Integer getQ_50() {
		return q_50;
	}
	public void setQ_50(Integer q_50) {
		this.q_50 = q_50;
	}
	public Integer getQ_100() {
		return q_100;
	}
	public void setQ_100(Integer q_100) {
		this.q_100 = q_100;
	}
	public Integer getQ_200() {
		return q_200;
	}
	public void setQ_200(Integer q_200) {
		this.q_200 = q_200;
	}
	public Integer getOsservato() {
		return osservato;
	}
	public void setOsservato(Integer osservato) {
		this.osservato = osservato;
	}
	public Integer getPiccoli_bacini() {
		return piccoli_bacini;
	}
	public void setPiccoli_bacini(Integer piccoli_bacini) {
		this.piccoli_bacini = piccoli_bacini;
	}
	public Integer getDeterministico() {
		return deterministico;
	}
	public void setDeterministico(Integer deterministico) {
		this.deterministico = deterministico;
	}
	public Integer getRainfarm() {
		return rainfarm;
	}
	public void setRainfarm(Integer rainfarm) {
		this.rainfarm = rainfarm;
	}
	public Integer getRadar() {
		return radar;
	}
	public void setRadar(Integer radar) {
		this.radar = radar;
	}
	public Integer getCatena_magra() {
		return catena_magra;
	}
	public void setCatena_magra(Integer catena_magra) {
		this.catena_magra = catena_magra;
	}
}
