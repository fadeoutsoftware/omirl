package it.fadeout.omirl.viewmodels;

import it.fadeout.omirl.business.StationAnag;


/**
 * @author Daniele Fiori
 * View Model for the station used in the
 * mobile app
 */
public class MobileStation {
	String id;
	int type;
	String name;
	String description;
	double lat;
	double lon;
	
	public MobileStation(StationAnag stationModel)
	{
		this.id = stationModel.getStation_code();
		this.name = stationModel.getName();
		this.description = "";
		this.type = 0;
		this.lat = stationModel.getLat();
		this.lon = stationModel.getLon();
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLon() {
		return lon;
	}
	public void setLon(double lon) {
		this.lon = lon;
	}

}
