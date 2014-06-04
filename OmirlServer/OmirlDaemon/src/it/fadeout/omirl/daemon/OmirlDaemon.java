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
								
								if (aoPoints != null) {
									oDataSerie.getData().addAll(aoPoints);
								}
								
								oDataSerie.setName("Temperatura Media");

								oDataChart.getDataSeries().add(oDataSerie);
								oDataChart.setTitle(oStationAnag.getMunicipality() + " - " + oStationAnag.getName());
								oDataChart.setSubTitle("Temperatura");
								
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
								
								if (aoPoints != null) {
									oDataSerie.getData().addAll(aoPoints);
								}
								
								oDataSerie.setName("Pioggia 1h");

								oDataChart.getDataSeries().add(oDataSerie);
								oDataChart.setTitle(oStationAnag.getMunicipality() + " - " + oStationAnag.getName());
								oDataChart.setSubTitle("Pioggia");
								
								serializeStationChart(oDataChart,oConfig, oStationAnag.getStation_code(), "rain1h", oDateFormat);
							}
						}
						catch(Exception oChartEx) {
							oChartEx.printStackTrace();
						}						
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
		else if (sType == "idro") {
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
