package it.fadeout.omirl.business;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "station_data")
public class StationData {

	@EmbeddedId
	StationDataPk station_data_Pk;
	@Column(name="rain_05m")
	Double rain_05m;
	@Column(name="rain_10m")
	Double rain_10m;
	@Column(name="rain_15m")
	Double rain_15m;
	@Column(name="rain_30m")
	Double rain_30m;
	@Column(name="rain_01h")
	Double rain_01h;
	@Column(name="rain_01h_part")
	Double rain_01h_part;
	@Column(name="rain_03h")
	Double rain_03h;
	@Column(name="rain_06h")
	Double rain_06h;
	@Column(name="rain_12h")
	Double rain_12h;
	@Column(name="rain_24h")
	Double rain_24h;
	@Column(name="rain_07d")
	Double rain_07d;
	@Column(name="rain_15d")
	Double rain_15d;
	@Column(name="rain_30d")
	Double rain_30d;
	@Column(name="mean_air_temp")
	Double mean_air_temp;
	@Column(name="min_air_temp")
	Double min_air_temp;
	@Column(name="max_air_temp")
	Double max_air_temp;
	@Column(name="mean_creek_level")
	Double mean_creek_level;
	@Column(name="max_creek_level")
	Double max_creek_level;
	@Column(name="instant_creek_level")
	Double instant_creek_level;
	@Column(name="mean_sea_level_press")
	Double mean_sea_level_press;
	@Column(name="humidity")
	Double humidity;
	@Column(name="solar_radiation_pwr")
	Double solar_radiation_pwr;
	@Column(name="mean_wind_speed")
	Double mean_wind_speed;
	@Column(name="wind_gust")
	Double wind_gust;
	@Column(name="prevailing_wind_dir")
	Double prevailing_wind_dir;
	@Column(name="battery_voltage")
	Double battery_voltage;
	@Column(name="leaf_wetness")
	Double leaf_wetness;
	@Column(name="update_date")
	Date update_date;
	@Column(name="insert_date")
	Date insert_date;
	@Column(name="mean_snow_depth")
	Double mean_snow_depth;
	@Column(name="mean_wave_heigth")
	Double mean_wave_heigth;
	@Column(name="max_wave_heigth")
	Double max_wave_heigth;
	@Column(name="mean_wave_period")
	Double mean_wave_period;
	@Column(name="peak_wave_period")
	Double peak_wave_period;
	@Column(name="trend_creek_level")
	Integer trend_creek_level;
	
	
	
	public StationDataPk getStation_data_Pk() {
		return station_data_Pk;
	}
	public void setStation_data_Pk(StationDataPk station_data_Pk) {
		this.station_data_Pk = station_data_Pk;
	}
	
//	public String getStation_code() {
//		return station_code;
//	}
//	public void setStation_code(String station_code) {
//		this.station_code = station_code;
//	}
//	public Date getReference_date() {
//		return reference_date;
//	}
//	public void setReference_date(Date reference_date) {
//		this.reference_date = reference_date;
//	}
	public Double getRain_05m() {
		return rain_05m;
	}
	public void setRain_05m(Double rain_05m) {
		this.rain_05m = rain_05m;
	}
	public Double getRain_10m() {
		return rain_10m;
	}
	public void setRain_10m(Double rain_10m) {
		this.rain_10m = rain_10m;
	}
	public Double getRain_15m() {
		return rain_15m;
	}
	public void setRain_15m(Double rain_15m) {
		this.rain_15m = rain_15m;
	}
	public Double getRain_30m() {
		return rain_30m;
	}
	public void setRain_30m(Double rain_30m) {
		this.rain_30m = rain_30m;
	}
	public Double getRain_01h() {
		return rain_01h;
	}
	public void setRain_01h(Double rain_01h) {
		this.rain_01h = rain_01h;
	}
	public Double getRain_03h() {
		return rain_03h;
	}
	public void setRain_03h(Double rain_03h) {
		this.rain_03h = rain_03h;
	}
	public Double getRain_06h() {
		return rain_06h;
	}
	public void setRain_06h(Double rain_06h) {
		this.rain_06h = rain_06h;
	}
	public Double getRain_12h() {
		return rain_12h;
	}
	public void setRain_12h(Double rain_12h) {
		this.rain_12h = rain_12h;
	}
	public Double getRain_24h() {
		return rain_24h;
	}
	public void setRain_24h(Double rain_24h) {
		this.rain_24h = rain_24h;
	}
	public Double getRain_07d() {
		return rain_07d;
	}
	public void setRain_07d(Double rain_07d) {
		this.rain_07d = rain_07d;
	}
	public Double getRain_15d() {
		return rain_15d;
	}
	public void setRain_15d(Double rain_15d) {
		this.rain_15d = rain_15d;
	}
	public Double getRain_30d() {
		return rain_30d;
	}
	public void setRain_30d(Double rain_30d) {
		this.rain_30d = rain_30d;
	}
	public Double getMean_air_temp() {
		return mean_air_temp;
	}
	public void setMean_air_temp(Double mean_air_temp) {
		this.mean_air_temp = mean_air_temp;
	}
	public Double getMin_air_temp() {
		return min_air_temp;
	}
	public void setMin_air_temp(Double min_air_temp) {
		this.min_air_temp = min_air_temp;
	}
	public Double getMax_air_temp() {
		return max_air_temp;
	}
	public void setMax_air_temp(Double max_air_temp) {
		this.max_air_temp = max_air_temp;
	}
	public Double getMean_creek_level() {
		return mean_creek_level;
	}
	public void setMean_creek_level(Double mean_creek_level) {
		this.mean_creek_level = mean_creek_level;
	}
	public Double getMax_creek_level() {
		return max_creek_level;
	}
	public void setMax_creek_level(Double max_creek_level) {
		this.max_creek_level = max_creek_level;
	}
	public Double getInstant_creek_level() {
		return instant_creek_level;
	}
	public void setInstant_creek_level(Double instant_creek_level) {
		this.instant_creek_level = instant_creek_level;
	}
	public Double getMean_sea_level_press() {
		return mean_sea_level_press;
	}
	public void setMean_sea_level_press(Double mean_sea_level_press) {
		this.mean_sea_level_press = mean_sea_level_press;
	}
	public Double getHumidity() {
		return humidity;
	}
	public void setHumidity(Double humidity) {
		this.humidity = humidity;
	}
	public Double getSolar_radiation_pwr() {
		return solar_radiation_pwr;
	}
	public void setSolar_radiation_pwr(Double solar_radiation_pwr) {
		this.solar_radiation_pwr = solar_radiation_pwr;
	}
	public Double getMean_wind_speed() {
		return mean_wind_speed;
	}
	public void setMean_wind_speed(Double mean_wind_speed) {
		this.mean_wind_speed = mean_wind_speed;
	}
	public Double getWind_gust() {
		return wind_gust;
	}
	public void setWind_gust(Double wind_gust) {
		this.wind_gust = wind_gust;
	}
	public Double getPrevailing_wind_dir() {
		return prevailing_wind_dir;
	}
	public void setPrevailing_wind_dir(Double prevailing_wind_dir) {
		this.prevailing_wind_dir = prevailing_wind_dir;
	}
	public Double getBattery_voltage() {
		return battery_voltage;
	}
	public void setBattery_voltage(Double battery_voltage) {
		this.battery_voltage = battery_voltage;
	}
	public Double getLeaf_wetness() {
		return leaf_wetness;
	}
	public void setLeaf_wetness(Double leaf_wetness) {
		this.leaf_wetness = leaf_wetness;
	}
	public Date getUpdate_date() {
		return update_date;
	}
	public void setUpdate_date(Date update_date) {
		this.update_date = update_date;
	}
	public Date getInsert_date() {
		return insert_date;
	}
	public void setInsert_date(Date insert_date) {
		this.insert_date = insert_date;
	}
	public Double getMean_snow_depth() {
		return mean_snow_depth;
	}
	public void setMean_snow_depth(Double mean_snow_depth) {
		this.mean_snow_depth = mean_snow_depth;
	}
	public Double getMean_wave_heigth() {
		return mean_wave_heigth;
	}
	public void setMean_wave_heigth(Double mean_wave_heigth) {
		this.mean_wave_heigth = mean_wave_heigth;
	}
	public Double getMax_wave_heigth() {
		return max_wave_heigth;
	}
	public void setMax_wave_heigth(Double max_wave_heigth) {
		this.max_wave_heigth = max_wave_heigth;
	}
	public Double getMean_wave_period() {
		return mean_wave_period;
	}
	public void setMean_wave_period(Double mean_wave_period) {
		this.mean_wave_period = mean_wave_period;
	}
	public Double getPeak_wave_period() {
		return peak_wave_period;
	}
	public void setPeak_wave_period(Double peak_wave_period) {
		this.peak_wave_period = peak_wave_period;
	}
	public Integer getTrend_creek_level() {
		return trend_creek_level;
	}
	public void setTrend_creek_level(Integer trend_creek_level) {
		this.trend_creek_level = trend_creek_level;
	}
	public Double getRain_01h_part() {
		return rain_01h_part;
	}
	public void setRain_01h_part(Double rain_01h_part) {
		this.rain_01h_part = rain_01h_part;
	}
}
