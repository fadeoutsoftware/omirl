package it.fadeout.omirl.daemon;

import it.fadeout.omirl.business.StationLastData;
import it.fadeout.omirl.data.HibernateUtils;
import it.fadeout.omirl.data.StationLastDataRepository;
import it.fadeout.omirl.viewmodels.SensorViewModel;

import java.io.File;
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
					
					// Get The stations
					StationLastDataRepository oLastRepo = new StationLastDataRepository();
					// Get all the last values
					List<StationLastData> aoLastValues = oLastRepo.SelectAll(StationLastData.class);
					
					if (aoLastValues != null) {
						
						// One List for each sensor type
						List<SensorViewModel> aoRain5m = new ArrayList<>();
						List<SensorViewModel> aoRain10m = new ArrayList<>();
						List<SensorViewModel> aoRain15m = new ArrayList<>();
						List<SensorViewModel> aoRain30m = new ArrayList<>();
						List<SensorViewModel> aoRain1h = new ArrayList<>();
						List<SensorViewModel> aoRain3h = new ArrayList<>();
						List<SensorViewModel> aoRain6h = new ArrayList<>();
						List<SensorViewModel> aoRain12h = new ArrayList<>();
						List<SensorViewModel> aoRain24h = new ArrayList<>();
						List<SensorViewModel> aoRain7d = new ArrayList<>();
						List<SensorViewModel> aoRain15d = new ArrayList<>();
						List<SensorViewModel> aoRain30d = new ArrayList<>();
						List<SensorViewModel> aoMeanTemp = new ArrayList<>();
						List<SensorViewModel> aoMeanLevel = new ArrayList<>();
						List<SensorViewModel> aoHumidity = new ArrayList<>();
						List<SensorViewModel> aoRadiation = new ArrayList<>();
						List<SensorViewModel> aoLeafs = new ArrayList<>();
						List<SensorViewModel> aoPressure = new ArrayList<>();
						List<SensorViewModel> aoBattery = new ArrayList<>();
//						List<SensorViewModel> aoMinTemp = new ArrayList<>();
//						List<SensorViewModel> aoMaxTemp = new ArrayList<>();
						
						// Generate Filtered Lists
						for (StationLastData oLast : aoLastValues) {

							// Check if the value exists
							if (oLast.getRain_05m() != null) {
								// Obtain the sensor view model
								SensorViewModel oSensor = oLast.getSensorViewModel(oLast.getRain_05m());
								// Add it to the list
								if (oSensor!=null) aoRain5m.add(oSensor);
							}
							
							
							// Check if the value exists
							if (oLast.getRain_10m() != null) {
								// Obtain the sensor view model
								SensorViewModel oSensor = oLast.getSensorViewModel(oLast.getRain_10m());
								// Add it to the list
								if (oSensor!=null) aoRain10m.add(oSensor);
							}
							
							
							// Check if the value exists
							if (oLast.getRain_15m() != null) {
								// Obtain the sensor view model
								SensorViewModel oSensor = oLast.getSensorViewModel(oLast.getRain_15m());
								// Add it to the list
								if (oSensor!=null) aoRain15m.add(oSensor);
							}
							
							// Check if the value exists
							if (oLast.getRain_30m() != null) {
								// Obtain the sensor view model
								SensorViewModel oSensor = oLast.getSensorViewModel(oLast.getRain_30m());
								// Add it to the list
								if (oSensor!=null) aoRain30m.add(oSensor);
							}	
							
							
							// Check if the value exists
							if (oLast.getRain_01h() != null) {
								// Obtain the sensor view model
								SensorViewModel oSensor = oLast.getSensorViewModel(oLast.getRain_01h());
								// Add it to the list
								if (oSensor!=null) aoRain1h.add(oSensor);
							}	
							
							// Check if the value exists
							if (oLast.getRain_03h() != null) {
								// Obtain the sensor view model
								SensorViewModel oSensor = oLast.getSensorViewModel(oLast.getRain_03h());
								// Add it to the list
								if (oSensor!=null) aoRain3h.add(oSensor);
							}	
							
							// Check if the value exists
							if (oLast.getRain_06h() != null) {
								// Obtain the sensor view model
								SensorViewModel oSensor = oLast.getSensorViewModel(oLast.getRain_06h());
								// Add it to the list
								if (oSensor!=null) aoRain6h.add(oSensor);
							}	
							
							// Check if the value exists
							if (oLast.getRain_12h() != null) {
								// Obtain the sensor view model
								SensorViewModel oSensor = oLast.getSensorViewModel(oLast.getRain_12h());
								// Add it to the list
								if (oSensor!=null) aoRain12h.add(oSensor);
							}	
							
							// Check if the value exists
							if (oLast.getRain_24h() != null) {
								// Obtain the sensor view model
								SensorViewModel oSensor = oLast.getSensorViewModel(oLast.getRain_24h());
								// Add it to the list
								if (oSensor!=null) aoRain24h.add(oSensor);
							}
							
							// Check if the value exists
							if (oLast.getRain_07d() != null) {
								// Obtain the sensor view model
								SensorViewModel oSensor = oLast.getSensorViewModel(oLast.getRain_07d());
								// Add it to the list
								if (oSensor!=null) aoRain7d.add(oSensor);
							}	
							
							// Check if the value exists
							if (oLast.getRain_15d() != null) {
								// Obtain the sensor view model
								SensorViewModel oSensor = oLast.getSensorViewModel(oLast.getRain_15d());
								// Add it to the list
								if (oSensor!=null) aoRain15d.add(oSensor);
							}	
							
							// Check if the value exists
							if (oLast.getRain_30d() != null) {
								// Obtain the sensor view model
								SensorViewModel oSensor = oLast.getSensorViewModel(oLast.getRain_30d());
								// Add it to the list
								if (oSensor!=null) aoRain30d.add(oSensor);
							}	

							// Check if the value exists
							if (oLast.getMean_air_temp() != null) {
								// Obtain the sensor view model
								SensorViewModel oSensor = oLast.getSensorViewModel(oLast.getMean_air_temp());
								// Add it to the list
								if (oSensor!=null) aoMeanTemp.add(oSensor);
							}	
							
							// Check if the value exists
							if (oLast.getMean_creek_level() != null) {
								// Obtain the sensor view model
								SensorViewModel oSensor = oLast.getSensorViewModel(oLast.getMean_creek_level());
								// Add it to the list
								if (oSensor!=null) aoMeanLevel.add(oSensor);
							}		
						
							
							
							// Check if the value exists
							if (oLast.getHumidity() != null) {
								// Obtain the sensor view model
								SensorViewModel oSensor = oLast.getSensorViewModel(oLast.getHumidity());
								// Add it to the list
								if (oSensor!=null) aoHumidity.add(oSensor);
							}		
							
							
							// Check if the value exists
							if (oLast.getSolar_radiation_pwr() != null) {
								// Obtain the sensor view model
								SensorViewModel oSensor = oLast.getSensorViewModel(oLast.getSolar_radiation_pwr());
								// Add it to the list
								if (oSensor!=null) aoRadiation.add(oSensor);
							}		
							
							// Check if the value exists
							if (oLast.getLeaf_wetness()!= null) {
								// Obtain the sensor view model
								SensorViewModel oSensor = oLast.getSensorViewModel(oLast.getLeaf_wetness());
								// Add it to the list
								if (oSensor!=null) aoLeafs.add(oSensor);
							}		
							
							// Check if the value exists
							if (oLast.getMean_sea_level_press() != null) {
								// Obtain the sensor view model
								SensorViewModel oSensor = oLast.getSensorViewModel(oLast.getMean_sea_level_press());
								// Add it to the list
								if (oSensor!=null) aoPressure.add(oSensor);
							}		
							
							// Check if the value exists
							if (oLast.getBattery_voltage() != null) {
								// Obtain the sensor view model
								SensorViewModel oSensor = oLast.getSensorViewModel(oLast.getBattery_voltage());
								// Add it to the list
								if (oSensor!=null) aoBattery.add(oSensor);
							}		
							
						}
						
						Date oDate = new Date();
						SimpleDateFormat oDateFormat = new SimpleDateFormat("HHmm");
						
						String sFullPath = getSubPath(oConfig.getFileRepositoryPath()+"/stations/rain1h",oDate);
						
						if (sFullPath != null)  {
							String sFileName = "rain01h"+oDateFormat.format(oDate)+".xml"; 
							SerializationUtils.serializeObjectToXML(sFullPath+"/"+sFileName, aoRain1h);
						}
						
						sFullPath = getSubPath(oConfig.getFileRepositoryPath()+"/stations/temp",oDate);
						
						if (sFullPath != null)  {
							String sFileName = "temp"+oDateFormat.format(oDate)+".xml"; 
							SerializationUtils.serializeObjectToXML(sFullPath+"/"+sFileName, aoMeanTemp);
						}						
						
						sFullPath = getSubPath(oConfig.getFileRepositoryPath()+"/stations/idro",oDate);
						
						if (sFullPath != null)  {
							String sFileName = "idro"+oDateFormat.format(oDate)+".xml"; 
							SerializationUtils.serializeObjectToXML(sFullPath+"/"+sFileName, aoMeanTemp);
						}	
						
						sFullPath = getSubPath(oConfig.getFileRepositoryPath()+"/stations/igro",oDate);
						
						if (sFullPath != null)  {
							String sFileName = "idro"+oDateFormat.format(oDate)+".xml"; 
							SerializationUtils.serializeObjectToXML(sFullPath+"/"+sFileName, aoHumidity);
						}
						
						sFullPath = getSubPath(oConfig.getFileRepositoryPath()+"/stations/radio",oDate);
						
						if (sFullPath != null)  {
							String sFileName = "idro"+oDateFormat.format(oDate)+".xml"; 
							SerializationUtils.serializeObjectToXML(sFullPath+"/"+sFileName, aoRadiation);
						}	
						
						sFullPath = getSubPath(oConfig.getFileRepositoryPath()+"/stations/leafs",oDate);
						
						if (sFullPath != null)  {
							String sFileName = "idro"+oDateFormat.format(oDate)+".xml"; 
							SerializationUtils.serializeObjectToXML(sFullPath+"/"+sFileName, aoLeafs);
						}	
						
						sFullPath = getSubPath(oConfig.getFileRepositoryPath()+"/stations/batt",oDate);
						
						if (sFullPath != null)  {
							String sFileName = "idro"+oDateFormat.format(oDate)+".xml"; 
							SerializationUtils.serializeObjectToXML(sFullPath+"/"+sFileName, aoBattery);
						}	
						
						sFullPath = getSubPath(oConfig.getFileRepositoryPath()+"/stations/press",oDate);
						
						if (sFullPath != null)  {
							String sFileName = "idro"+oDateFormat.format(oDate)+".xml"; 
							SerializationUtils.serializeObjectToXML(sFullPath+"/"+sFileName, aoPressure);
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
	
	public static void WriteSampleConfig() {
		OmirlDaemonConfiguration oConfig = new OmirlDaemonConfiguration();
		oConfig.setFileRepositoryPath("C:\\temp\\Omirl\\Files");
		oConfig.setMinutesPolling(2);
		
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
			
			List<StationLastData> aoLastValues = oLastRepo.selectByStationType("rain_05m");
			
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
