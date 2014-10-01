package it.fadeout.omirl.business.config;

import java.util.ArrayList;

public class OmirlNavigationConfig {
	
	String filesBasePath = "";
	ArrayList<MapLinkConfig> mapLinks = new ArrayList<MapLinkConfig>();
	ArrayList<SensorLinkConfig> sensorLinks = new ArrayList<SensorLinkConfig>();
	ArrayList<StaticLinkConfig> staticLinks = new ArrayList<StaticLinkConfig>();
	ArrayList<HydroLinkConfig> hydroLinks = new ArrayList<HydroLinkConfig>();
	ArrayList<RadarLinkConfig> radarLinks = new ArrayList<RadarLinkConfig>();
	ArrayList<SatelliteLinkConfig> satelliteLinks = new ArrayList<SatelliteLinkConfig>();
	
	public String getFilesBasePath() {
		return filesBasePath;
	}
	public void setFilesBasePath(String filesBasePath) {
		this.filesBasePath = filesBasePath;
	}	
	public ArrayList<MapLinkConfig> getMapLinks() {
		return mapLinks;
	}
	public void setMapLinks(ArrayList<MapLinkConfig> mapLinks) {
		this.mapLinks = mapLinks;
	}
	public ArrayList<SensorLinkConfig> getSensorLinks() {
		return sensorLinks;
	}
	public void setSensorLinks(ArrayList<SensorLinkConfig> sensorLinks) {
		this.sensorLinks = sensorLinks;
	}
	public ArrayList<StaticLinkConfig> getStaticLinks() {
		return staticLinks;
	}
	public void setStaticLinks(ArrayList<StaticLinkConfig> staticLinks) {
		this.staticLinks = staticLinks;
	}
	public ArrayList<HydroLinkConfig> getHydroLinks() {
		return hydroLinks;
	}
	public void setHydroLinks(ArrayList<HydroLinkConfig> hydroLinks) {
		this.hydroLinks = hydroLinks;
	}
	public ArrayList<RadarLinkConfig> getRadarLinks() {
		return radarLinks;
	}
	public void setRadarLinks(ArrayList<RadarLinkConfig> radarLinks) {
		this.radarLinks = radarLinks;
	}
	public ArrayList<SatelliteLinkConfig> getSatelliteLinks() {
		return satelliteLinks;
	}
	public void setSatelliteLinks(ArrayList<SatelliteLinkConfig> satelliteLinks) {
		this.satelliteLinks = satelliteLinks;
	}
	
}
