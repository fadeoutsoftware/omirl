package it.fadeout.omirl.business;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name="omirlusers")
public class OmirlUser {
	
	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy = "increment")	
	@Column(name="iduser")	
	int idUser;
	@Column(name="userid")
	String userId;
	@Column(name="password")
	String password;
	@Column(name="name")
	String name;
	@Column(name="role")
	int role;
	@Column(name="defaultSensorType")
	String defaultSensorType;
	@Column(name="defaultLat")
	Double defaultLat;
	@Column(name="defaultLon")
	Double defaultLon;
	@Column(name="defaultZoom")
	Integer defaultZoom;
	@Column(name="defaultMap")
	String defaultMap;
	@Column(name="defaultStatics")
	String defaultStatics;
	
	public int getIdUser() {
		return idUser;
	}
	public void setIdUser(int idUser) {
		this.idUser = idUser;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getRole() {
		return role;
	}
	public void setRole(int role) {
		this.role = role;
	}
	public String getDefaultSensorType() {
		return defaultSensorType;
	}
	public void setDefaultSensorType(String defaultSensorType) {
		this.defaultSensorType = defaultSensorType;
	}
	public Double getDefaultLat() {
		return defaultLat;
	}
	public void setDefaultLat(Double defaultLat) {
		this.defaultLat = defaultLat;
	}
	public Double getDefaultLon() {
		return defaultLon;
	}
	public void setDefaultLon(Double defaultLon) {
		this.defaultLon = defaultLon;
	}
	public Integer getDefaultZoom() {
		return defaultZoom;
	}
	public void setDefaultZoom(Integer defaultZoom) {
		this.defaultZoom = defaultZoom;
	}
	public String getDefaultMap() {
		return defaultMap;
	}
	public void setDefaultMap(String defaultMap) {
		this.defaultMap = defaultMap;
	}
	public String getDefaultStatics() {
		return defaultStatics;
	}
	public void setDefaultStatics(String defaultStatics) {
		this.defaultStatics = defaultStatics;
	}
}
