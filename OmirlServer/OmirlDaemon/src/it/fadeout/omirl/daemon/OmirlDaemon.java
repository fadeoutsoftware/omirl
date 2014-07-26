package it.fadeout.omirl.daemon;

import it.fadeout.omirl.business.ChartLine;
import it.fadeout.omirl.business.CreekThreshold;
import it.fadeout.omirl.business.DataChart;
import it.fadeout.omirl.business.DataSerie;
import it.fadeout.omirl.business.DataSeriePoint;
import it.fadeout.omirl.business.SensorLastData;
import it.fadeout.omirl.business.StationAnag;
import it.fadeout.omirl.business.StationLastData;
import it.fadeout.omirl.data.CreekThresholdRepository;
import it.fadeout.omirl.data.HibernateUtils;
import it.fadeout.omirl.data.StationAnagRepository;
import it.fadeout.omirl.data.StationDataRepository;
import it.fadeout.omirl.data.StationLastDataRepository;
import it.fadeout.omirl.viewmodels.SensorViewModel;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

public class OmirlDaemon {
	
	HashMap<String, CreekThreshold> m_aoThresholds = new HashMap<>();
	private OmirlDaemonConfiguration m_oConfig;
	private String m_sConfigurationFile = "";
	
	/**
	 * Omirl Daemon
	 * @param args
	 */
	public static void main(String[] args) {
		
		if (args == null || args.length<1) {
			//WriteSampleConfig();
			System.out.println("OmirlDaemon - Missing Configuration File Config");
			System.out.println("Usage OmirlDaemon \"FILEPATH\". Closing now");
			return;
		}
		
		//testDate();
		
		//WriteSampleConfig();
		
		OmirlDaemon oDaemon = new OmirlDaemon();
		oDaemon.OmirlDaemonCycle(args[0]);

	}	
		
	public void OmirlDaemonCycle(String sConfigurationFile)
	{
		System.out.println("OmirlDaemon - Starting " + new Date());
		
		try {
			System.out.println("OmirlDaemon - Reading Configuration File " + sConfigurationFile);
			m_oConfig = (OmirlDaemonConfiguration) SerializationUtils.deserializeXMLToObject(sConfigurationFile);
			m_sConfigurationFile = sConfigurationFile;
		} catch (Exception e) {
			
			System.out.println("OmirlDaemon - Error reading conf file. Closing daemon");
			e.printStackTrace();
			return;
		}
				
		Date oLastDate = null;
		
		try {
			
			// Cycle Forever!
			while (true) {

				Date oActualDate = new Date();
				
				// Start 
				System.out.println("OmirlDaemon - Cycle Start " + oActualDate);	
				
				if (DayChanged(oActualDate, oLastDate)) {
					oLastDate=oActualDate;
					DailyTask();
				}
				
				try {
					
					// Date Format for File Serialization
					SimpleDateFormat oDateFormat = new SimpleDateFormat("HHmm");
					

					// Charts
					StationAnagRepository oStationAnagRepository = new StationAnagRepository();
					StationDataRepository oStationDataRepository = new StationDataRepository();

					// Get Start Date Time Filter
					long lNowTime = new Date().getTime();
					
					// TODO: questo 15 è il numero di giorni metterlo in configurazione
					long lInterval = 15 * 24 * 60 * 60 * 1000;
					Date oChartsStartDate = new Date(lNowTime-lInterval);				

					// Get all the stations
					List<StationAnag> aoAllStations = oStationAnagRepository.SelectAll(StationAnag.class);
					
					// For Each
					for (StationAnag oStationAnag : aoAllStations) {
						
						ArrayList<String> asOtherLinks = new ArrayList<>();
						
						// TEMPERATURE CHART
						if (oStationAnag.getMean_air_temp_every() != null) asOtherLinks.add("Termo");
						if (oStationAnag.getRain_01h_every() != null) asOtherLinks.add("Pluvio");
						if (oStationAnag.getMean_creek_level_every() != null) asOtherLinks.add("Idro");
						if (oStationAnag.getMean_wind_speed_every() != null) asOtherLinks.add("Vento");
						if (oStationAnag.getHumidity_every() != null) asOtherLinks.add("Igro");
						if (oStationAnag.getSolar_radiation_pwr_every() != null) asOtherLinks.add("Radio");
						if (oStationAnag.getLeaf_wetness_every() != null) asOtherLinks.add("Foglie");
						if (oStationAnag.getMean_sea_level_press_every() != null)  asOtherLinks.add("Press");
						if (oStationAnag.getBattery_voltage_every() != null) asOtherLinks.add("Batt");
						//if (oStationAnag.get != null) asOtherLinks.add("humidity");
						
						
						try {
							
							// TEMPERATURE CHART
							if (oStationAnag.getMean_air_temp_every() != null) {
								
								DataChart oDataChart = new DataChart();
								
								DataSerie oDataSerie = new DataSerie();	
								oDataSerie.setType("line");
								List<DataSeriePoint> aoPoints = oStationDataRepository.getDataSerie(oStationAnag.getStation_code(), "mean_air_temp", oChartsStartDate);
								
								DataSeriePointToDataSerie(aoPoints,oDataSerie);
								
								oDataSerie.setName("Temperatura");

								oDataChart.getDataSeries().add(oDataSerie);
								oDataChart.setTitle(oStationAnag.getMunicipality() + " - " + oStationAnag.getName());
								oDataChart.setSubTitle("Temperatura");
								oDataChart.setAxisYMaxValue(36.0);
								oDataChart.setAxisYMinValue(-4.0);
								oDataChart.setAxisYTickInterval(2.0);
								oDataChart.setAxisYTitle("Temperatura (°C)");
								oDataChart.setTooltipValueSuffix(" °C");
								
								oDataChart.getOtherChart().addAll(asOtherLinks);
								serializeStationChart(oDataChart,m_oConfig, oStationAnag.getStation_code(), "temp", oDateFormat);
							}
						}
						catch(Exception oChartEx) {
							oChartEx.printStackTrace();
						}
						
						try {
							
							// RAIN CHART
							if (oStationAnag.getRain_01h_every() != null) {
								
								DataChart oDataChart = new DataChart();
								
								DataSerie oDataSerie = new DataSerie();
								oDataSerie.setType("column");
								
								List<DataSeriePoint> aoPoints = oStationDataRepository.getDataSerie(oStationAnag.getStation_code(), "rain_01h", oChartsStartDate);
								
								DataSeriePointToDataSerie(aoPoints,oDataSerie, 0.1);
								
								oDataSerie.setName("Pioggia 1h");

								oDataChart.getDataSeries().add(oDataSerie);
								oDataChart.setTitle(oStationAnag.getMunicipality() + " - " + oStationAnag.getName());
								oDataChart.setSubTitle("Pioggia");
								oDataChart.setAxisYMaxValue(150.0);
								oDataChart.setAxisYMinValue(0.0);
								oDataChart.setAxisYTickInterval(10.0);
								oDataChart.setAxisYTitle("Pioggia Oraria (mm)");
								oDataChart.setTooltipValueSuffix(" mm");
								
								
								// Add Cumulated Serie
								DataSerie oCumulatedSerie = new DataSerie();
								oCumulatedSerie.setName("Cumulata");
								oCumulatedSerie.setType("line");
								
								
								for (int iPoints = 0; iPoints<oDataSerie.getData().size(); iPoints++) {
									// Get The histogram point
									Object [] oHistoPoint = oDataSerie.getData().get(iPoints);
									
									// Create the cumulated point
									Object [] adPoint = new Object[2];
									adPoint[0] = new Long((Long)oHistoPoint[0]);
									
									// Is the first?
									if (iPoints==0)
									{
										// Initialize
										adPoint[1] = new Double((Double)oHistoPoint[1]);
									}
									else 
									{
										// Sum to the previos value
										Object [] adOldPoint = oCumulatedSerie.getData().get(iPoints-1);  
										adPoint[1] = ((Double) adOldPoint[1] + (Double)oHistoPoint[1]);
									}
									
									// Add the point to the serie
									oCumulatedSerie.getData().add(adPoint);
								}
								
								oDataChart.getDataSeries().add(oCumulatedSerie);
								
								oDataChart.getOtherChart().addAll(asOtherLinks);
								serializeStationChart(oDataChart,m_oConfig, oStationAnag.getStation_code(), "rain1h", oDateFormat);
							}
						}
						catch(Exception oChartEx) {
							oChartEx.printStackTrace();
						}			
						
						
						
						
						try {
							
							// HYDRO CHART
							if (oStationAnag.getMean_creek_level_every() != null) {
								
								DataChart oDataChart = new DataChart();
								
								DataSerie oDataSerie = new DataSerie();	
								oDataSerie.setType("line");
								List<DataSeriePoint> aoPoints = oStationDataRepository.getDataSerie(oStationAnag.getStation_code(), "mean_creek_level", oChartsStartDate);
								
								DataSeriePointToDataSerie(aoPoints,oDataSerie, 0.1);
								
								oDataSerie.setName("Livello Idrometrico");

								oDataChart.getDataSeries().add(oDataSerie);
								oDataChart.setTitle(oStationAnag.getMunicipality() + " - " + oStationAnag.getName());
								oDataChart.setSubTitle("Livello Idrometrico");
								oDataChart.setAxisYMaxValue(10.0);
								oDataChart.setAxisYMinValue(-1.0);
								oDataChart.setAxisYTickInterval(1.0);
								oDataChart.setAxisYTitle("Livello Idrometrico (m)");
								oDataChart.setTooltipValueSuffix(" m");
								
								CreekThreshold oThreshold = m_aoThresholds.get(oStationAnag.getStation_code());
								
								if (oThreshold != null)
								{
									oDataChart.setAxisYMaxValue(oThreshold.getYmax());
									oDataChart.setAxisYMinValue(oThreshold.getYmin());
									
									double dAxisTickInterval = (oDataChart.getAxisYMaxValue()-oDataChart.getAxisYMinValue())/11.0;
									dAxisTickInterval = Math.floor(dAxisTickInterval);
									if (dAxisTickInterval == 0.0) dAxisTickInterval = 1.0;
									
									oDataChart.setAxisYTickInterval(dAxisTickInterval);
									
									ChartLine oOrange = new ChartLine();
									oOrange.setColor("#FF6600");
									oOrange.setName("Soglia Arancione");
									oOrange.setValue(oThreshold.getOrange());
									
									ChartLine oRed = new ChartLine();
									oRed.setColor("#FF0000");
									oRed.setName("Soglia Rossa");
									oRed.setValue(oThreshold.getRed());
									
									oDataChart.getHorizontalLines().add(oOrange);
									oDataChart.getHorizontalLines().add(oRed);
								}
								
								oDataChart.getOtherChart().addAll(asOtherLinks);
								serializeStationChart(oDataChart,m_oConfig, oStationAnag.getStation_code(), "idro", oDateFormat);
							}
						}
						catch(Exception oChartEx) {
							oChartEx.printStackTrace();
						}						
						
						
						
						try {
							
							// WIND CHART
							if (oStationAnag.getMean_wind_speed_every() != null) {
								
								DataChart oDataChart = new DataChart();
								
								DataSerie oDataSerie = new DataSerie();	
								oDataSerie.setType("line");
								List<DataSeriePoint> aoPoints = oStationDataRepository.getDataSerie(oStationAnag.getStation_code(), "mean_wind_speed", oChartsStartDate);
								
								DataSeriePointToDataSerie(aoPoints,oDataSerie,3.6);
								
								oDataSerie.setName("Velocità del Vento");

								oDataChart.getDataSeries().add(oDataSerie);
								oDataChart.setTitle(oStationAnag.getMunicipality() + " - " + oStationAnag.getName());
								oDataChart.setSubTitle("Vento");
								oDataChart.setAxisYMaxValue(150.0);
								oDataChart.setAxisYMinValue(0.0);
								oDataChart.setAxisYTickInterval(10.0);
								oDataChart.setAxisYTitle("Velocità (km/h)");
								oDataChart.setTooltipValueSuffix(" km/h");
								
								oDataChart.getOtherChart().addAll(asOtherLinks);
								serializeStationChart(oDataChart,m_oConfig, oStationAnag.getStation_code(), "wind", oDateFormat);
							}
						}
						catch(Exception oChartEx) {
							oChartEx.printStackTrace();
						}							
						

						
						try {
							
							// UMIDITY CHART
							if (oStationAnag.getHumidity_every() != null) {
								
								DataChart oDataChart = new DataChart();
								
								DataSerie oDataSerie = new DataSerie();	
								oDataSerie.setType("line");
								List<DataSeriePoint> aoPoints = oStationDataRepository.getDataSerie(oStationAnag.getStation_code(), "humidity", oChartsStartDate);
								
								DataSeriePointToDataSerie(aoPoints,oDataSerie);
								
								oDataSerie.setName("Umidità Relativa");

								oDataChart.getDataSeries().add(oDataSerie);
								oDataChart.setTitle(oStationAnag.getMunicipality() + " - " + oStationAnag.getName());
								oDataChart.setSubTitle("Umidità");
								oDataChart.setAxisYMaxValue(100.0);
								oDataChart.setAxisYMinValue(0.0);
								oDataChart.setAxisYTickInterval(10.0);
								oDataChart.setAxisYTitle("Umidità Relativa (%)");
								oDataChart.setTooltipValueSuffix(" %");
								
								oDataChart.getOtherChart().addAll(asOtherLinks);
								serializeStationChart(oDataChart,m_oConfig, oStationAnag.getStation_code(), "igro", oDateFormat);
							}
						}
						catch(Exception oChartEx) {
							oChartEx.printStackTrace();
						}							
						
						
						
						
						try {
							
							// RADIATION CHART
							if (oStationAnag.getSolar_radiation_pwr_every() != null) {
								
								DataChart oDataChart = new DataChart();
								
								DataSerie oDataSerie = new DataSerie();	
								oDataSerie.setType("line");
								List<DataSeriePoint> aoPoints = oStationDataRepository.getDataSerie(oStationAnag.getStation_code(), "solar_radiation_pwr", oChartsStartDate);
								
								DataSeriePointToDataSerie(aoPoints,oDataSerie, 10.0);
								
								oDataSerie.setName("Radiazione Solare Media");

								oDataChart.getDataSeries().add(oDataSerie);
								oDataChart.setTitle(oStationAnag.getMunicipality() + " - " + oStationAnag.getName());
								oDataChart.setSubTitle("Radiometri");
								oDataChart.setAxisYMaxValue(1200.0);
								oDataChart.setAxisYMinValue(0.0);
								oDataChart.setAxisYTickInterval(50.0);
								oDataChart.setAxisYTitle("Radiazione (W/m2)");
								oDataChart.setTooltipValueSuffix(" W/m2");
								
								oDataChart.getOtherChart().addAll(asOtherLinks);
								serializeStationChart(oDataChart,m_oConfig, oStationAnag.getStation_code(), "radio", oDateFormat);
							}
						}
						catch(Exception oChartEx) {
							oChartEx.printStackTrace();
						}			
						
						
						
						try {
							
							// BAGNATURA FOGLIARE CHART
							if (oStationAnag.getLeaf_wetness_every() != null) {
								
								DataChart oDataChart = new DataChart();
								
								DataSerie oDataSerie = new DataSerie();	
								oDataSerie.setType("line");
								List<DataSeriePoint> aoPoints = oStationDataRepository.getDataSerie(oStationAnag.getStation_code(), "leaf_wetness", oChartsStartDate);
								
								DataSeriePointToDataSerie(aoPoints,oDataSerie);
								
								oDataSerie.setName("Bagnatura Fogliare");

								oDataChart.getDataSeries().add(oDataSerie);
								oDataChart.setTitle(oStationAnag.getMunicipality() + " - " + oStationAnag.getName());
								oDataChart.setSubTitle("Percentuale");
								oDataChart.setAxisYMaxValue(100.0);
								oDataChart.setAxisYMinValue(0.0);
								oDataChart.setAxisYTickInterval(10.0);
								oDataChart.setAxisYTitle("Bagnatura Fogliare (%)");
								oDataChart.setTooltipValueSuffix(" %");
								
								oDataChart.getOtherChart().addAll(asOtherLinks);
								serializeStationChart(oDataChart,m_oConfig, oStationAnag.getStation_code(), "leafs", oDateFormat);
							}
						}
						catch(Exception oChartEx) {
							oChartEx.printStackTrace();
						}	
						
						try {
							
							// PRESSIONE CHART
							if (oStationAnag.getMean_sea_level_press_every() != null) {
								
								DataChart oDataChart = new DataChart();
								
								DataSerie oDataSerie = new DataSerie();	
								oDataSerie.setType("line");
								List<DataSeriePoint> aoPoints = oStationDataRepository.getDataSerie(oStationAnag.getStation_code(), "mean_sea_level_press", oChartsStartDate);
								
								DataSeriePointToDataSerie(aoPoints,oDataSerie);
								
								oDataSerie.setName("Pressione Atmosferica");

								oDataChart.getDataSeries().add(oDataSerie);
								oDataChart.setTitle(oStationAnag.getMunicipality() + " - " + oStationAnag.getName());
								oDataChart.setSubTitle("Pressione al livello del mare");
								oDataChart.setAxisYMaxValue(1040.0);
								oDataChart.setAxisYMinValue(980.0);
								oDataChart.setAxisYTickInterval(5.0);
								oDataChart.setAxisYTitle("Pressione al livello del mare (hPa)");
								oDataChart.setTooltipValueSuffix(" hPa");
								
								oDataChart.getOtherChart().addAll(asOtherLinks);
								serializeStationChart(oDataChart,m_oConfig, oStationAnag.getStation_code(), "press", oDateFormat);
							}
						}
						catch(Exception oChartEx) {
							oChartEx.printStackTrace();
						}	
						
						
						try {
							
							// BATTERY CHART
							if (oStationAnag.getBattery_voltage_every() != null) {
								
								DataChart oDataChart = new DataChart();
								
								DataSerie oDataSerie = new DataSerie();	
								oDataSerie.setType("line");
								List<DataSeriePoint> aoPoints = oStationDataRepository.getDataSerie(oStationAnag.getStation_code(), "battery_voltage", oChartsStartDate);
								
								DataSeriePointToDataSerie(aoPoints,oDataSerie);
								
								oDataSerie.setName("Tensione Batteria");

								oDataChart.getDataSeries().add(oDataSerie);
								oDataChart.setTitle(oStationAnag.getMunicipality() + " - " + oStationAnag.getName());
								oDataChart.setSubTitle("Tensione Batteria");
								oDataChart.setAxisYMaxValue(15.0);
								oDataChart.setAxisYMinValue(7.0);
								oDataChart.setAxisYTickInterval(1.0);
								oDataChart.setAxisYTitle("Tensione Batteria (V)");
								oDataChart.setTooltipValueSuffix(" V");
								
								oDataChart.getOtherChart().addAll(asOtherLinks);
								serializeStationChart(oDataChart,m_oConfig, oStationAnag.getStation_code(), "batt", oDateFormat);
							}
						}
						catch(Exception oChartEx) {
							oChartEx.printStackTrace();
						}	
						
						
						try {
							
							// MARE CHART
							if (oStationAnag.getBattery_voltage_every() != null) {
								
								DataChart oDataChart = new DataChart();
								
								DataSerie oDataSerie = new DataSerie();	
								oDataSerie.setType("line");
								List<DataSeriePoint> aoPoints = oStationDataRepository.getDataSerie(oStationAnag.getStation_code(), "mean_wave_heigth", oChartsStartDate);
								
								DataSeriePointToDataSerie(aoPoints,oDataSerie);
								
								oDataSerie.setName("Lunghezza d'Onda");

								oDataChart.getDataSeries().add(oDataSerie);
								oDataChart.setTitle(oStationAnag.getMunicipality() + " - " + oStationAnag.getName());
								oDataChart.setSubTitle("Stato del Mare");
								oDataChart.setAxisYMaxValue(15.0);
								oDataChart.setAxisYMinValue(7.0);
								oDataChart.setAxisYTickInterval(1.0);
								oDataChart.setAxisYTitle("Lunghezza Media Onda(m)");
								oDataChart.setTooltipValueSuffix(" m");
								
								oDataChart.getOtherChart().addAll(asOtherLinks);
								serializeStationChart(oDataChart,m_oConfig, oStationAnag.getStation_code(), "boa", oDateFormat);
							}
						}
						catch(Exception oChartEx) {
							oChartEx.printStackTrace();
						}	
						
						
						/*
						try {
							
							// SNOW CHART
							if (oStationAnag.get != null) {
								
								DataChart oDataChart = new DataChart();
								
								DataSerie oDataSerie = new DataSerie();	
								oDataSerie.setType("line");
								List<DataSeriePoint> aoPoints = oStationDataRepository.getDataSerie(oStationAnag.getStation_code(), "mean_snow_depth", oChartsStartDate);
								
								DataSeriePointToDataSerie(aoPoints,oDataSerie);
								
								oDataSerie.setName("Lunghezza d'Onda");

								oDataChart.getDataSeries().add(oDataSerie);
								oDataChart.setTitle(oStationAnag.getMunicipality() + " - " + oStationAnag.getName());
								oDataChart.setSubTitle("Stato del Mare");
								oDataChart.setAxisYMaxValue(15.0);
								oDataChart.setAxisYMinValue(7.0);
								oDataChart.setAxisYTickInterval(1.0);
								oDataChart.setAxisYTitle("Lunghezza Media Onda(m)");
								oDataChart.setTooltipValueSuffix(" m");
								
								oDataChart.getOtherChart().addAll(asOtherLinks);
								serializeStationChart(oDataChart,oConfig, oStationAnag.getStation_code(), "snow", oDateFormat);
							}
						}
						catch(Exception oChartEx) {
							oChartEx.printStackTrace();
						}							
						
						*/
						
						
					}

					// Get The stations
					StationLastDataRepository oLastRepo = new StationLastDataRepository();
					
					SerializeSensorLast("rain1h", oLastRepo,  m_oConfig, oDateFormat);
					SerializeSensorLast("temp", oLastRepo,  m_oConfig, oDateFormat);
					SerializeSensorLast("idro", oLastRepo,  m_oConfig, oDateFormat);
					SerializeSensorLast("igro", oLastRepo,  m_oConfig, oDateFormat);
					SerializeSensorLast("radio", oLastRepo,  m_oConfig, oDateFormat);
					SerializeSensorLast("leafs", oLastRepo,  m_oConfig, oDateFormat);
					SerializeSensorLast("batt", oLastRepo,  m_oConfig, oDateFormat);
					SerializeSensorLast("press", oLastRepo,  m_oConfig, oDateFormat);
					SerializeSensorLast("snow", oLastRepo,  m_oConfig, oDateFormat);
					SerializeSensorLast("boa", oLastRepo,  m_oConfig, oDateFormat);
					SerializeSensorLast("wind", oLastRepo,  m_oConfig, oDateFormat);
					
					

				}
				catch(Exception oEx) {
					oEx.printStackTrace();
				}									
				
				System.out.println("OmirlDaemon - Cycle End " + new Date());
				
				try {
					Thread.sleep(m_oConfig.getMinutesPolling()*60*1000);
				}
				catch(Exception oEx) {
					oEx.printStackTrace();
				}
			}

		}
		catch(Exception oEx) {
			oEx.printStackTrace();
		}
		finally {
			HibernateUtils.shutdown();
		}		
	}

	/**
	 * Peforms tasks that the Daemon has to do daily
	 */
	private void DailyTask() {
		
		System.out.println("OmirlDaemon - DailyTask");
		
		RefreshConfiguration();
		
		RefreshThresholds();

	}
	
	public void RefreshConfiguration() {
		try {
			// Refresh Configuration File
			System.out.println("OmirlDaemon - Reading Configuration File " + m_sConfigurationFile);
			m_oConfig = (OmirlDaemonConfiguration) SerializationUtils.deserializeXMLToObject(m_sConfigurationFile);
		} catch (Exception e) {
			
			System.out.println("OmirlDaemon - Error reading conf file. Closing daemon");
			e.printStackTrace();
			return;
		}		
	}
	
	public void RefreshThresholds() {
		try {
			
			// Refresh thresholds
			System.out.println("OmirlDaemon - Refreshing Thresholds");
			
			CreekThresholdRepository oCreekThresholdRepository = new CreekThresholdRepository();
			List<CreekThreshold> aoThresholds = oCreekThresholdRepository.SelectAll(CreekThreshold.class);
			
			m_aoThresholds.clear();
			
			for (CreekThreshold oThreshold : aoThresholds) {
				oThreshold.setBlack(oThreshold.getBlack()/10.0);
				oThreshold.setFlood(oThreshold.getFlood()/10.0);
				oThreshold.setOrange(oThreshold.getOrange()/10.0);
				oThreshold.setRed(oThreshold.getRed()/10.0);
				oThreshold.setSms(oThreshold.getSms()/10.0);
				oThreshold.setWhite(oThreshold.getWhite()/10.0);
				oThreshold.setYmax(oThreshold.getYmax()/10.0);
				oThreshold.setYmin(oThreshold.getYmin()/10.0);
				
				m_aoThresholds.put(oThreshold.getCode(), oThreshold);
			}
			
		} catch (Exception e) {
			
			System.out.println("OmirlDaemon - Error reading Sensor Thresholds.");
			e.printStackTrace();
		}		
	}

	public boolean DayChanged(Date oActualDate, Date oLastDate)
	{
		if (oLastDate==null) return true;
		
		long lActualTime = oActualDate.getTime();
		long lLastTime = oActualDate.getTime();
		
		long lDay = 24L*60L*60L*1000L;
		
		if ((lActualTime-lLastTime)>lDay) return true;
		return false;
	}
	
	public void DataSeriePointToDataSerie(List<DataSeriePoint> aoPoints, DataSerie oDataSerie) {
		DataSeriePointToDataSerie(aoPoints, oDataSerie, 1.0);
	}

	public void DataSeriePointToDataSerie(List<DataSeriePoint> aoPoints, DataSerie oDataSerie, double dConversionFactor) {
		if (aoPoints != null) {
			for (int iPoints = 0; iPoints<aoPoints.size(); iPoints++) {
				Object [] adPoint = new Object[2];
				adPoint[0] = new Long(aoPoints.get(iPoints).getRefDate().getTime());
				adPoint[1] = new Double(aoPoints.get(iPoints).getVal())*dConversionFactor;
				oDataSerie.getData().add(adPoint);
			}
		}		
	}
	
	/**
	 * Serializes an XML file with the last observations of all the stations with a specified sensor
	 * @param sName
	 * @param oLastRepo
	 * @param oConfig
	 * @param oDateFormat
	 */
	public void SerializeSensorLast(String sName, StationLastDataRepository oLastRepo, OmirlDaemonConfiguration oConfig, DateFormat oDateFormat) {
		
		
		try {
			List<SensorLastData> aoSensorLast = oLastRepo.selectByStationType("lastdata"+ sName);
			
			if (aoSensorLast != null) {
				
				// One List for each sensor type
				List<SensorViewModel> aoSensoViewModel = new ArrayList<>();
				
				for (SensorLastData oSensorLastData : aoSensorLast) {
					try {
						SensorViewModel oSensorViewModel = oSensorLastData.getSensorViewModel();
						if (oSensorViewModel != null) {
							aoSensoViewModel.add(oSensorViewModel);
						}						
					}
					catch(Exception oInnerEx) {
						oInnerEx.printStackTrace();
					}
				}
					
				SensorDataSpecialWork(aoSensoViewModel, sName);
				
				Date oDate = new Date();
				
				String sFullPath = getSubPath(oConfig.getFileRepositoryPath()+"/stations/" + sName,oDate);
				
				if (sFullPath != null)  {
					String sFileName = sName+oDateFormat.format(oDate)+".xml"; 
					SerializationUtils.serializeObjectToXML(sFullPath+"/"+sFileName, aoSensoViewModel);
				}
			}
			else {
				System.out.println("OmirlDaemon ");
				System.out.println("OmirlDaemon - There was an error reading last values");
			}							
		}
		catch(Exception oEx) {
			oEx.printStackTrace();
		}
	}
	
	/**
	 * Utility function that extends stations serialization for type-specific work
	 * @param aoSensorList
	 * @param sType
	 */
	public void SensorDataSpecialWork(List<SensorViewModel> aoSensorList, String sType) {
		if (sType == "wind") {
			
			ArrayList<Double> aoWindLimits = new ArrayList<>();
			aoWindLimits.add(0.514444);
			aoWindLimits.add(1.543332);
			aoWindLimits.add(4.115552);
			aoWindLimits.add(6.687772);
			aoWindLimits.add(9.259992);
			aoWindLimits.add(11.832212);
			aoWindLimits.add(14.404432);
			aoWindLimits.add(16.976652);
			aoWindLimits.add(19.548872);
			aoWindLimits.add(22.121092);
			aoWindLimits.add(24.693312);
			aoWindLimits.add(27.265532);
			//aoWindLimits.add(29.837752);
			
			ArrayList<String> aoWindImages = new ArrayList<>();
			aoWindImages.add("img/sensors/wind_0.png");
			aoWindImages.add("img/sensors/wind_1.png");
			aoWindImages.add("img/sensors/wind_2.png");
			aoWindImages.add("img/sensors/wind_3.png");
			aoWindImages.add("img/sensors/wind_4.png");
			aoWindImages.add("img/sensors/wind_5.png");
			aoWindImages.add("img/sensors/wind_6.png");
			aoWindImages.add("img/sensors/wind_7.png");
			aoWindImages.add("img/sensors/wind_8.png");
			aoWindImages.add("img/sensors/wind_9.png");
			aoWindImages.add("img/sensors/wind_10.png");
			aoWindImages.add("img/sensors/wind_11.png");
			aoWindImages.add("img/sensors/wind_12.png");
			//aoWindImages.add("img/sensors/wind_13.png");

			
			for (SensorViewModel oViewModel : aoSensorList) {
				// Init with the max value
				oViewModel.setImgPath(aoWindImages.get(aoWindImages.size()-1));
				
				// Find the right limit
				for (int iLimits = 0; iLimits<aoWindLimits.size(); iLimits++){
					if (oViewModel.getValue()<aoWindLimits.get(iLimits)) {
						// Se the image path!
						oViewModel.setImgPath(aoWindImages.get(iLimits));
						
						// Convert to Km/h
						oViewModel.setValue(oViewModel.getValue()*3.6);
						break;
					}
				}
			}
		}
		else if (sType == "idro") {
			for (SensorViewModel oViewModel : aoSensorList) {
				oViewModel.setValue(oViewModel.getValue()/10.0);
				
				CreekThreshold oThreshold = m_aoThresholds.get(oViewModel.getShortCode());
				
				if (oThreshold != null )
				{
					Double dValue = oViewModel.getValue();
					
					String sOtherHtml = "0";
					
					if (dValue>=oThreshold.getBlack())
					{
						sOtherHtml = "5";
					}
					else if (dValue>=oThreshold.getFlood())
					{
						sOtherHtml = "4";
					}
					else if (dValue>=oThreshold.getRed())
					{
						sOtherHtml = "3";
					}
					else if (dValue>=oThreshold.getOrange())
					{
						sOtherHtml = "2";
					}
					else if (dValue>=oThreshold.getSms())
					{
						sOtherHtml = "1";
					}
					else
					{
						sOtherHtml = "0";
					}
					
					oViewModel.setOtherHtml(sOtherHtml);
				}
				
			}
		}
		else if (sType == "rain") {
			for (SensorViewModel oViewModel : aoSensorList) {
				oViewModel.setValue(oViewModel.getValue()/10.0);
			}
		}		
		else if (sType == "radio") {
			for (SensorViewModel oViewModel : aoSensorList) {
				oViewModel.setValue(oViewModel.getValue()*10.0);
			}			
		}
	}
	
	/**
	 * Gets a full path starting from the Base Path appending oDate
	 * @param sBasePath
	 * @param oDate
	 * @return
	 */
	public String getSubPath(String sBasePath, Date oDate) {
		SimpleDateFormat oDateFormat = new SimpleDateFormat("yyyy/MM/dd");
		
		File oBasePathDir = new File(sBasePath);
		
		if (!oBasePathDir.exists()) {
			if (!oBasePathDir.mkdirs()) return null;
		}
		
		String sFullDir = sBasePath + "/" + oDateFormat.format(oDate);
		
		File oFullPathDir = new File(sFullDir);
		if (!oFullPathDir.exists()) {
			if (!oFullPathDir.mkdirs()) return null;
		}
		
		return sFullDir;
	}
	
	
	/**
	 * Gets the Data Serie of a single station for a single sensor type starting from the specified date
	 * @param sStationCode
	 * @param sColumnName
	 * @param oStartDate
	 */
	public void getStationDataSerie(String sStationCode, String sColumnName, Date oStartDate) {
		
		StationDataRepository oStationDataRepository = new StationDataRepository();
		
		List<DataSeriePoint> aoPoints = oStationDataRepository.getDataSerie(sStationCode,sColumnName,oStartDate);
		
		if (aoPoints != null) {
			for (DataSeriePoint oDataSeriePoint : aoPoints) {
				System.out.println("Time " + oDataSeriePoint.getRefDate() + ": " + oDataSeriePoint.getVal());
			}
		}
	}
	
	public void serializeStationChart(DataChart oChart, OmirlDaemonConfiguration oConfig, String sStationCode, String sChartName, DateFormat oDateFormat) {
		try {
			Date oDate = new Date();
			
			//String sFullPath = getSubPath(oConfig.getFileRepositoryPath()+"/charts/" + sStationCode + "/" + sChartName,oDate);
			String sFullPath = getSubPath(oConfig.getFileRepositoryPath()+"/charts" ,oDate) + "/" + sStationCode + "/" + sChartName;
			
			File oPath = new File(sFullPath);
			
			if (!oPath.exists())
			{
				oPath.mkdirs();
			}
			
			if (sFullPath != null)  {
				String sFileName = sChartName+oDateFormat.format(oDate)+".xml"; 
				SerializationUtils.serializeObjectToXML(sFullPath+"/"+sFileName, oChart);
			}			
		}
		catch(Exception oEx) {
			oEx.printStackTrace();
		}
		
	}
	
	public static void WriteSampleConfig() {
		OmirlDaemonConfiguration oConfig = new OmirlDaemonConfiguration();
		oConfig.setFileRepositoryPath("C:\\temp\\Omirl\\Files");
		oConfig.setMinutesPolling(2);
		oConfig.setChartTimeRangeDays(16);
		
		try {
			SerializationUtils.serializeObjectToXML("C:\\temp\\Omirl\\OmirlDaemonConfigSAMPLE.xml", oConfig);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void Test() {
		try {
//			StationAnagRepository oRepo = new StationAnagRepository();
//			StationAnag oStation = oRepo.selectByStationCode("AGORR");
//			
//			if (oStation != null) {
//				System.out.println(oStation.getName());
//			}
//			
			StationLastDataRepository oLastRepo = new StationLastDataRepository();
			
			List<StationLastData> aoLastValues = oLastRepo.SelectAll(StationLastData.class);
			
			//oLastRepo.SelectAll(oClass)

			if (aoLastValues != null) {
				for (StationLastData oLast : aoLastValues) {
					System.out.println(oLast.getStation_code() + " = " + oLast.getRain_05m());
				}
				
			}

		}
		catch(Exception oEx) {
			oEx.printStackTrace();
		}
		finally {
			HibernateUtils.shutdown();
		}		
	}
	
	public static void testDate()
	{
		Date oDate = new Date();
		
		System.out.println("Now = " + oDate);
		
		long lTime = oDate.getTime();
		
		System.out.println("Time = " + lTime);
		
		Date oDate2 = new Date(1406023200000l);

		System.out.println("Now 2 = " + oDate2);
		
		lTime = oDate2.getTime();
		
		System.out.println("Time 2 = " + lTime);
		
		Calendar oCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		
		//oCalendar.setTimeZone(TimeZone.getTimeZone("GMT"));
		oCalendar.setTimeInMillis(1406023200000l);
		
		System.out.println("Now 3 = " + oCalendar.getTime());
		System.out.println("Time 3 = " + oCalendar.getTime().getTime());
		
		
	}


}
