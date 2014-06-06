package it.fadeout.omirl.daemon;

import it.fadeout.omirl.business.DataChart;
import it.fadeout.omirl.business.DataSerie;
import it.fadeout.omirl.business.DataSeriePoint;
import it.fadeout.omirl.business.SensorLastData;
import it.fadeout.omirl.business.StationAnag;
import it.fadeout.omirl.business.StationLastData;
import it.fadeout.omirl.data.HibernateUtils;
import it.fadeout.omirl.data.StationAnagRepository;
import it.fadeout.omirl.data.StationDataRepository;
import it.fadeout.omirl.data.StationLastDataRepository;
import it.fadeout.omirl.viewmodels.SensorViewModel;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OmirlDaemon {

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
		
		//WriteSampleConfig();
		
		System.out.println("OmirlDaemon - Starting " + new Date());
		
		OmirlDaemonConfiguration oConfig = null;
		
		try {
			System.out.println("OmirlDaemon - Reading Configuration File " + args[0]);
			oConfig = (OmirlDaemonConfiguration) SerializationUtils.deserializeXMLToObject(args[0]);
		} catch (Exception e) {
			
			System.out.println("OmirlDaemon - Error reading conf file. Closing daemon");
			e.printStackTrace();
			return;
		}
		
		try {
			
			// Cycle Forever!
			while (true) {
				
				// Start 
				System.out.println("OmirlDaemon - Cycle Start " + new Date());
				
				
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
						
						try {
							
							// TEMPERATURE CHART
							if (oStationAnag.getMean_air_temp_every() != null) {
								
								DataChart oDataChart = new DataChart();
								
								DataSerie oDataSerie = new DataSerie();	
								oDataSerie.setType("line");
								List<DataSeriePoint> aoPoints = oStationDataRepository.getDataSerie(oStationAnag.getStation_code(), "mean_air_temp", oChartsStartDate);
								
								DataSeriePointToDataSerie(aoPoints,oDataSerie);
								
								oDataSerie.setName("Temperatura Media");

								oDataChart.getDataSeries().add(oDataSerie);
								oDataChart.setTitle(oStationAnag.getMunicipality() + " - " + oStationAnag.getName());
								oDataChart.setSubTitle("Temperatura");
								oDataChart.setAxisYMaxValue(36.0);
								oDataChart.setAxisYMinValue(-4.0);
								oDataChart.setAxisYTickInterval(2.0);
								oDataChart.setAxisYTitle("Temperatura Media (°C)");
								oDataChart.setTooltipValueSuffix(" °C");
								
								serializeStationChart(oDataChart,oConfig, oStationAnag.getStation_code(), "temp", oDateFormat);
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
								
								serializeStationChart(oDataChart,oConfig, oStationAnag.getStation_code(), "rain1h", oDateFormat);
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
								
								oDataSerie.setName("Livello Medio");

								oDataChart.getDataSeries().add(oDataSerie);
								oDataChart.setTitle(oStationAnag.getMunicipality() + " - " + oStationAnag.getName());
								oDataChart.setSubTitle("Livello");
								oDataChart.setAxisYMaxValue(10.0);
								oDataChart.setAxisYMinValue(-1.0);
								oDataChart.setAxisYTickInterval(1.0);
								oDataChart.setAxisYTitle("Livello Medio (m)");
								oDataChart.setTooltipValueSuffix(" m");
								
								serializeStationChart(oDataChart,oConfig, oStationAnag.getStation_code(), "idro", oDateFormat);
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
								
								serializeStationChart(oDataChart,oConfig, oStationAnag.getStation_code(), "wind", oDateFormat);
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
								
								serializeStationChart(oDataChart,oConfig, oStationAnag.getStation_code(), "igro", oDateFormat);
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
								
								DataSeriePointToDataSerie(aoPoints,oDataSerie);
								
								oDataSerie.setName("Radiazione Solare Media");

								oDataChart.getDataSeries().add(oDataSerie);
								oDataChart.setTitle(oStationAnag.getMunicipality() + " - " + oStationAnag.getName());
								oDataChart.setSubTitle("Radiometri");
								oDataChart.setAxisYMaxValue(1200.0);
								oDataChart.setAxisYMinValue(0.0);
								oDataChart.setAxisYTickInterval(50.0);
								oDataChart.setAxisYTitle("Radiazione (W/m2)");
								oDataChart.setTooltipValueSuffix(" W/m2");
								
								serializeStationChart(oDataChart,oConfig, oStationAnag.getStation_code(), "radio", oDateFormat);
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
								
								serializeStationChart(oDataChart,oConfig, oStationAnag.getStation_code(), "leafs", oDateFormat);
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
								
								serializeStationChart(oDataChart,oConfig, oStationAnag.getStation_code(), "press", oDateFormat);
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
								
								serializeStationChart(oDataChart,oConfig, oStationAnag.getStation_code(), "batt", oDateFormat);
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
								
								serializeStationChart(oDataChart,oConfig, oStationAnag.getStation_code(), "boa", oDateFormat);
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
					
					SerializeSensorLast("rain1h", oLastRepo,  oConfig, oDateFormat);
					SerializeSensorLast("temp", oLastRepo,  oConfig, oDateFormat);
					SerializeSensorLast("idro", oLastRepo,  oConfig, oDateFormat);
					SerializeSensorLast("igro", oLastRepo,  oConfig, oDateFormat);
					SerializeSensorLast("radio", oLastRepo,  oConfig, oDateFormat);
					SerializeSensorLast("leafs", oLastRepo,  oConfig, oDateFormat);
					SerializeSensorLast("batt", oLastRepo,  oConfig, oDateFormat);
					SerializeSensorLast("press", oLastRepo,  oConfig, oDateFormat);
					SerializeSensorLast("snow", oLastRepo,  oConfig, oDateFormat);
					SerializeSensorLast("boa", oLastRepo,  oConfig, oDateFormat);
					SerializeSensorLast("wind", oLastRepo,  oConfig, oDateFormat);
					
					

				}
				catch(Exception oEx) {
					oEx.printStackTrace();
				}									
				
				System.out.println("OmirlDaemon - Cycle End " + new Date());
				
				try {
					Thread.sleep(oConfig.getMinutesPolling()*60*1000);
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
	
	public static void DataSeriePointToDataSerie(List<DataSeriePoint> aoPoints, DataSerie oDataSerie) {
		DataSeriePointToDataSerie(aoPoints, oDataSerie, 1.0);
	}

	public static void DataSeriePointToDataSerie(List<DataSeriePoint> aoPoints, DataSerie oDataSerie, double dConversionFactor) {
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
	public static void SerializeSensorLast(String sName, StationLastDataRepository oLastRepo, OmirlDaemonConfiguration oConfig, DateFormat oDateFormat) {
		
		
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
	public static void SensorDataSpecialWork(List<SensorViewModel> aoSensorList, String sType) {
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
		else if (sType == "idro" || sType == "rain") {
			for (SensorViewModel oViewModel : aoSensorList) {
				oViewModel.setValue(oViewModel.getValue()/10.0);
			}
				
		}
	}
	
	/**
	 * Gets a full path starting from the Base Path appending oDate
	 * @param sBasePath
	 * @param oDate
	 * @return
	 */
	public static String getSubPath(String sBasePath, Date oDate) {
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
	public static void getStationDataSerie(String sStationCode, String sColumnName, Date oStartDate) {
		
		StationDataRepository oStationDataRepository = new StationDataRepository();
		
		List<DataSeriePoint> aoPoints = oStationDataRepository.getDataSerie(sStationCode,sColumnName,oStartDate);
		
		if (aoPoints != null) {
			for (DataSeriePoint oDataSeriePoint : aoPoints) {
				System.out.println("Time " + oDataSeriePoint.getRefDate() + ": " + oDataSeriePoint.getVal());
			}
		}
	}
	
	public static void serializeStationChart(DataChart oChart, OmirlDaemonConfiguration oConfig, String sStationCode, String sChartName, DateFormat oDateFormat) {
		try {
			Date oDate = new Date();
			
			String sFullPath = getSubPath(oConfig.getFileRepositoryPath()+"/charts/" + sStationCode + "/" + sChartName,oDate);
			
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

}
