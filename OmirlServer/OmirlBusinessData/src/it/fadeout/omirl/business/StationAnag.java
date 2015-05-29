package it.fadeout.omirl.business;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="station_anag")
public class StationAnag {
	@Id
	@Column(name="station_code")
	String station_code;
	@Column(name="name")
	String name;
	@Column(name="lat")
	double lat;
	@Column(name="lon")
	double lon;
	@Column(name="elevation")
	Double elevation;
	@Column(name="basin")
	String basin;
	@Column(name="river")
	String river;
	@Column(name="district")
	String district;
	@Column(name="municipality")
	String municipality;
	@Column(name="rain_05m_every")
	Integer rain_05m_every;
	@Column(name="rain_10m_every")
	Integer rain_10m_every;
	@Column(name="rain_15m_every")
	Integer rain_15m_every;
	@Column(name="rain_30m_every")
	Integer rain_30m_every;
	@Column(name="rain_01h_every")
	Integer rain_01h_every;
	@Column(name="rain_03h_every")
	Integer rain_03h_every;
	@Column(name="rain_06h_every")
	Integer rain_06h_every;
	@Column(name="rain_12h_every")
	Integer rain_12h_every;
	@Column(name="rain_24h_every")
	Integer rain_24h_every;
	@Column(name="rain_07d_every")
	Integer rain_07d_every;
	@Column(name="rain_15d_every")
	Integer rain_15d_every;
	@Column(name="rain_30d_every")
	Integer rain_30d_every;
	@Column(name="mean_air_temp_every")
	Integer mean_air_temp_every;
	@Column(name="min_air_temp_every")
	Integer min_air_temp_every;
	@Column(name="max_air_temp_every")
	Integer max_air_temp_every;
	@Column(name="mean_creek_level_every")
	Integer mean_creek_level_every;
	@Column(name="max_creek_level_every")
	Integer max_creek_level_every;
	@Column(name="instant_creek_level_every")
	Integer instant_creek_level_every;
	@Column(name="mean_sea_level_press_every")
	Integer mean_sea_level_press_every;
	@Column(name="humidity_every")
	Integer humidity_every;
	@Column(name="solar_radiation_pwr_every")
	Integer solar_radiation_pwr_every;
	@Column(name="mean_wind_speed_every")
	Integer mean_wind_speed_every;
	@Column(name="wind_gust_every")
	Integer wind_gust_every;
	@Column(name="prevailing_wind_dir_every")
	Integer prevailing_wind_dir_every;
	@Column(name="battery_voltage_every")
	Integer battery_voltage_every;
	@Column(name="leaf_wetness_every")
	Integer leaf_wetness_every;
	@Column(name="warn_area")
	String warn_area;
	@Column(name="near_sea")
	Integer near_sea;
	@Column(name="webcam_every")
	Integer webcam_every;
	@Column(name="mean_wave_height_every")
	Integer mean_wave_height_every;
	@Column(name="mean_snow_depth_every")
	Integer mean_snow_depth_every;
	
	public String getStation_code() {
		return station_code;
	}
	public void setStation_code(String station_code) {
		this.station_code = station_code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	public Double getElevation() {
		return elevation;
	}
	public void setElevation(Double elevation) {
		this.elevation = elevation;
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
	public String getMunicipality() {
		return municipality;
	}
	public void setMunicipality(String municipality) {
		this.municipality = municipality;
	}
	public Integer getRain_05m_every() {
		return rain_05m_every;
	}
	public void setRain_05m_every(Integer rain_05m_every) {
		this.rain_05m_every = rain_05m_every;
	}
	public Integer getRain_10m_every() {
		return rain_10m_every;
	}
	public void setRain_10m_every(Integer rain_10m_every) {
		this.rain_10m_every = rain_10m_every;
	}
	public Integer getRain_15m_every() {
		return rain_15m_every;
	}
	public void setRain_15m_every(Integer rain_15m_every) {
		this.rain_15m_every = rain_15m_every;
	}
	public Integer getRain_30m_every() {
		return rain_30m_every;
	}
	public void setRain_30m_every(Integer rain_30m_every) {
		this.rain_30m_every = rain_30m_every;
	}
	public Integer getRain_01h_every() {
		return rain_01h_every;
	}
	public void setRain_01h_every(Integer rain_01h_every) {
		this.rain_01h_every = rain_01h_every;
	}
	public Integer getRain_03h_every() {
		return rain_03h_every;
	}
	public void setRain_03h_every(Integer rain_03h_every) {
		this.rain_03h_every = rain_03h_every;
	}
	public Integer getRain_06h_every() {
		return rain_06h_every;
	}
	public void setRain_06h_every(Integer rain_06h_every) {
		this.rain_06h_every = rain_06h_every;
	}
	public Integer getRain_12h_every() {
		return rain_12h_every;
	}
	public void setRain_12h_every(Integer rain_12h_every) {
		this.rain_12h_every = rain_12h_every;
	}
	public Integer getRain_24h_every() {
		return rain_24h_every;
	}
	public void setRain_24h_every(Integer rain_24h_every) {
		this.rain_24h_every = rain_24h_every;
	}
	public Integer getRain_07d_every() {
		return rain_07d_every;
	}
	public void setRain_07d_every(Integer rain_07d_every) {
		this.rain_07d_every = rain_07d_every;
	}
	public Integer getRain_15d_every() {
		return rain_15d_every;
	}
	public void setRain_15d_every(Integer rain_15d_every) {
		this.rain_15d_every = rain_15d_every;
	}
	public Integer getRain_30d_every() {
		return rain_30d_every;
	}
	public void setRain_30d_every(Integer rain_30d_every) {
		this.rain_30d_every = rain_30d_every;
	}
	public Integer getMean_air_temp_every() {
		return mean_air_temp_every;
	}
	public void setMean_air_temp_every(Integer mean_air_temp_every) {
		this.mean_air_temp_every = mean_air_temp_every;
	}
	public Integer getMin_air_temp_every() {
		return min_air_temp_every;
	}
	public void setMin_air_temp_every(Integer min_air_temp_every) {
		this.min_air_temp_every = min_air_temp_every;
	}
	public Integer getMax_air_temp_every() {
		return max_air_temp_every;
	}
	public void setMax_air_temp_every(Integer max_air_temp_every) {
		this.max_air_temp_every = max_air_temp_every;
	}
	public Integer getMean_creek_level_every() {
		return mean_creek_level_every;
	}
	public void setMean_creek_level_every(Integer mean_creek_level_every) {
		this.mean_creek_level_every = mean_creek_level_every;
	}
	public Integer getMax_creek_level_every() {
		return max_creek_level_every;
	}
	public void setMax_creek_level_every(Integer max_creek_level_every) {
		this.max_creek_level_every = max_creek_level_every;
	}
	public Integer getInstant_creek_level_every() {
		return instant_creek_level_every;
	}
	public void setInstant_creek_level_every(Integer instant_creek_level_every) {
		this.instant_creek_level_every = instant_creek_level_every;
	}
	public Integer getMean_sea_level_press_every() {
		return mean_sea_level_press_every;
	}
	public void setMean_sea_level_press_every(Integer mean_sea_level_press_every) {
		this.mean_sea_level_press_every = mean_sea_level_press_every;
	}
	public Integer getHumidity_every() {
		return humidity_every;
	}
	public void setHumidity_every(Integer humidity_every) {
		this.humidity_every = humidity_every;
	}
	public Integer getSolar_radiation_pwr_every() {
		return solar_radiation_pwr_every;
	}
	public void setSolar_radiation_pwr_every(Integer solar_radiation_pwr_every) {
		this.solar_radiation_pwr_every = solar_radiation_pwr_every;
	}
	public Integer getMean_wind_speed_every() {
		return mean_wind_speed_every;
	}
	public void setMean_wind_speed_every(Integer mean_wind_speed_every) {
		this.mean_wind_speed_every = mean_wind_speed_every;
	}
	public Integer getWind_gust_every() {
		return wind_gust_every;
	}
	public void setWind_gust_every(Integer wind_gust_every) {
		this.wind_gust_every = wind_gust_every;
	}
	public Integer getPrevailing_wind_dir_every() {
		return prevailing_wind_dir_every;
	}
	public void setPrevailing_wind_dir_every(Integer prevailing_wind_dir_every) {
		this.prevailing_wind_dir_every = prevailing_wind_dir_every;
	}
	public Integer getBattery_voltage_every() {
		return battery_voltage_every;
	}
	public void setBattery_voltage_every(Integer battery_voltage_every) {
		this.battery_voltage_every = battery_voltage_every;
	}
	public Integer getLeaf_wetness_every() {
		return leaf_wetness_every;
	}
	public void setLeaf_wetness_every(Integer leaf_wetness_every) {
		this.leaf_wetness_every = leaf_wetness_every;
	}
	public String getWarn_area() {
		return warn_area;
	}
	public void setWarn_area(String warn_area) {
		this.warn_area = warn_area;
	}
	public Integer getNear_sea() {
		return near_sea;
	}
	public void setNear_sea(Integer near_sea) {
		this.near_sea = near_sea;
	}
	public Integer getWebcam_every() {
		return webcam_every;
	}
	public void setWebcam_every(Integer webcam_every) {
		this.webcam_every = webcam_every;
	}
	public Integer getMean_wave_height_every() {
		return mean_wave_height_every;
	}
	public void setMean_wave_height_every(Integer mean_wave_height_every) {
		this.mean_wave_height_every = mean_wave_height_every;
	}
	public Integer getMean_snow_depth_every() {
		return mean_snow_depth_every;
	}
	public void setMean_snow_depth_every(Integer mean_snow_depth_every) {
		this.mean_snow_depth_every = mean_snow_depth_every;
	}
}
