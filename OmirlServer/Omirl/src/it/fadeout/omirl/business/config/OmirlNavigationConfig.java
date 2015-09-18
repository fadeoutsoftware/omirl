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
	ArrayList<TableLinkConfig> dataTableLinks = new ArrayList<>();
	ArrayList<TableLinkConfig> tableLinks = new ArrayList<>();
	private ArrayList<LegendConfig> sensorLegends = new ArrayList<>();
	private ArrayList<GalleryLinkConfig> galleryLinks = new ArrayList<GalleryLinkConfig>();
	private ArrayList<HydroModelLinkConfig> HydroModelLinks = new ArrayList<HydroModelLinkConfig>();
	
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
	public ArrayList<TableLinkConfig> getDataTableLinks() {
		return dataTableLinks;
	}
	public void setDataTableLinks(ArrayList<TableLinkConfig> dataTableLinks) {
		this.dataTableLinks = dataTableLinks;
	}
	public ArrayList<TableLinkConfig> getTableLinks() {
		return tableLinks;
	}
	public void setTableLinks(ArrayList<TableLinkConfig> tableLinks) {
		this.tableLinks = tableLinks;
	}
	public ArrayList<LegendConfig> getSensorLegends() {
		return sensorLegends;
	}
	public void setSensorLegends(ArrayList<LegendConfig> sensorLegends) {
		this.sensorLegends = sensorLegends;
	}
	public ArrayList<GalleryLinkConfig> getGalleryLinks() {
		
		return galleryLinks;
	}
	public void setGalleryLinks(ArrayList<GalleryLinkConfig> galleryLinks) {
		this.galleryLinks = galleryLinks;
	}
	public ArrayList<HydroModelLinkConfig> getHydroModelLinks() {
		return HydroModelLinks;
	}
	public void setHydroModelLinks(ArrayList<HydroModelLinkConfig> hydroModelLinks) {
		HydroModelLinks = hydroModelLinks;
	}
	
}
