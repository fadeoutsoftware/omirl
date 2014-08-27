package it.fadeout.omirl.viewmodels;

public class UserViewModel {
	boolean isLogged;
	String name;
	String mail;
	int role;
	String defaultStatics;
	String defaultMap;
	String defaultSensorType;
	Double defaultLat;
	Double defaultLon;
	Integer defaultZoom;
	String sessionId;

	
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
	public boolean isLogged() {
		return isLogged;
	}
	public void setLogged(boolean isLogged) {
		this.isLogged = isLogged;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public String getMail() {
		return mail;
	}
	public void setMail(String mail) {
		this.mail = mail;
	}
	public String getDefaultStatics() {
		return defaultStatics;
	}
	public void setDefaultStatics(String defaultStatics) {
		this.defaultStatics = defaultStatics;
	}
	public String getDefaultMap() {
		return defaultMap;
	}
	public void setDefaultMap(String defaultMap) {
		this.defaultMap = defaultMap;
	}

}
