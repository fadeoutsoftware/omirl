package it.fadeout.acronet.daemon;

import it.fadeout.omirl.business.AnagTableInfo;
import it.fadeout.omirl.business.ChartAxis;
import it.fadeout.omirl.business.ChartInfo;
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
import it.fadeout.omirl.data.SavedPeriodRepository;
import it.fadeout.omirl.data.StationAnagRepository;
import it.fadeout.omirl.data.StationDataRepository;
import it.fadeout.omirl.data.StationLastDataRepository;
import it.fadeout.omirl.viewmodels.SensorListTableRowViewModel;
import it.fadeout.omirl.viewmodels.SensorListTableViewModel;
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

import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;

import Experience.Corba.ExpService.IExpService;
import Experience.Corba.Services.StationServiceNew.IStationServiceNew;
import Experience.Corba.Services.StationServiceNew.IStationServiceNewOperations;
import Experience.Corba.StructuresDefinition.DataSource;
import Experience.Corba.StructuresDefinition.DataSourceHandle;
import Experience.Data.ApparentTemperature.DewPoint;
import Experience.Lib.API.CMediatore;
import Experience.Lib.Exceptions.ExpException;
import Experience.Lib.Services.ServicesId;
import Experience.Lib.Suppliers.Data.DataSourcesHandler;
import Experience.Lib.Util.ExpDropsClient.ExpDropsClient;

public class AcronetDaemon {
	
	HashMap<String, CreekThreshold> m_aoThresholds = new HashMap<>();
	private AcronetDaemonConfiguration m_oConfig;
	private String m_sConfigurationFile = "";
	
	// Date Format for File Serialization
	SimpleDateFormat m_oDateFormat = new SimpleDateFormat("HHmm");

	
	/**
	 * Omirl Daemon
	 * @param args
	 */
	public static void main(String[] args) {
		
		if (args == null || args.length<1) {
			//WriteSampleConfig();
			System.out.println("AcronetDaemon - Missing Configuration File Config");
			System.out.println("Usage AcronetDaemon \"FILEPATH\". Closing now");
			return;
		}
		
		//Test();
		//testDate();
		
		//WriteSampleConfig();
		
		AcronetDaemon oDaemon = new AcronetDaemon();
		oDaemon.AcronetDaemonCycle(args[0]);
	}	
	
	/**
	 * Gets the name of value column from minutes resolution
	 * @param iResolution
	 * @return
	 */
	public String getRainColumnNameFromNative(int iResolution) {
		String sReturn = "";
		switch (iResolution) {
		case 5:
			sReturn = "rain_05m";
			break;
		case 10:
			sReturn = "rain_10m";
			break;
		case 15:
			sReturn = "rain_15m";
			break;
		case 30:
			sReturn = "rain_30m";
			break;			
		}
		
		return sReturn;
	}
	
	/**
	 * Finds Chart Info Object in the array stored in the configuration starting by relative column name
	 * @param sSensorColumn
	 * @return
	 */
	public List<ChartInfo> getChartInfoFromSensorColumn(String sSensorColumn) {
		ArrayList<ChartInfo> aoInfo = new ArrayList<>();

		for (int iChartsInfo = 0; iChartsInfo<m_oConfig.getChartsInfo().size(); iChartsInfo++) {
			ChartInfo oInfo = m_oConfig.getChartsInfo().get(iChartsInfo);
			if (oInfo.getColumnName().equals(sSensorColumn)) {
				aoInfo.add(oInfo);
			}
		}
		
		return aoInfo;
	}
	
	/**
	 * Finds Chart Info Object in the array stored in the configuration starting by relative Sensor Code
	 * @param sSensorCode
	 * @return
	 */
	public List<ChartInfo> getChartInfoFromSensorCode(String sSensorCode) {
		ArrayList<ChartInfo> aoInfo = new ArrayList<>();

		for (int iChartsInfo = 0; iChartsInfo<m_oConfig.getChartsInfo().size(); iChartsInfo++) {
			ChartInfo oInfo = m_oConfig.getChartsInfo().get(iChartsInfo);
			if (oInfo.getSensorType().equals(sSensorCode)) {
				aoInfo.add(oInfo);
			}
		}
		
		return aoInfo;
	}

	
	/**
	 * Main Omirl Daemon Cycle
	 * @param sConfigurationFile	Configuration File Path
	 */
	public void AcronetDaemonCycle(String sConfigurationFile)
	{
		System.out.println("AcronetDaemon - Starting " + new Date());
		
		try {
			System.out.println("AcronetDaemon - Reading Configuration File " + sConfigurationFile);
			m_oConfig = (AcronetDaemonConfiguration) SerializationUtils.deserializeXMLToObject(sConfigurationFile);
			m_sConfigurationFile = sConfigurationFile;
		} catch (Exception e) {
			
			System.out.println("AcronetDaemon - Error reading conf file. Closing daemon");
			e.printStackTrace();
			return;
		}
				
		Date oLastDate = null;
		
		IStationServiceNewOperations oStationsService = null;
		
		try {
			String sExpServer = "130.251.104.19"; 
			CMediatore.init(sExpServer);
			
			ExpDropsClient oClient = new ExpDropsClient(sExpServer);
			String sIOR = oClient.GetServiceIOR(ServicesId.STATION_SERVICENEW_ID);
			
			if (sIOR == null) {
				//TODO: 
				return;
			}
			
			IExpService oService = CMediatore.GetComponent(sIOR);
			oStationsService = (IStationServiceNewOperations) oService;
			
		} catch (ExpException e) {
			e.printStackTrace();
		}
		

		
		try {
			
			// Cycle Forever!
			while (true) {

				Date oActualDate = new Date();
				
				// Start 
				System.out.println("AcronetDaemon - Cycle Start " + oActualDate);	
				
				if (DayChanged(oActualDate, oLastDate)) {
					oLastDate=oActualDate;
					//TODO Riattivare
					//DailyTask();
				}
				
				try {
						
					DataSource [] aoDataSources = oStationsService.GetAvailableDataSource();
					
					DataSource oOpenDataSource = null;
					
					if (aoDataSources!=null) {
						for (DataSource oDataSource : aoDataSources) {
							if (oDataSource.m_sNome.equals("Merged")) {
								oOpenDataSource = oDataSource;
								break;
							}
						}
					}
					
					if (oOpenDataSource == null) continue;
					
					DataSourceHandle oSourceHandle = oStationsService.OpenDataSource(oOpenDataSource);
					
					//TODO:
					
					// Ottenere Anagrafica stazioni Acronet
					
					// Salvare il layer pubblico
					
					// Per ogni tipo di sensore
						// Salvare il layer puntuale di quel sensore
					
					// Per ogni centralina
						// Fare i grafici
					
					oStationsService.CloseDataSource(oSourceHandle);
				}
				catch(Exception oEx) {
					oEx.printStackTrace();
				}									
				
				System.out.println("AcronetDaemon - Cycle End " + new Date());
				
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
	 * Gets the Date to use for the query to select data for charts
	 * @param iDays Number of days of the chart
	 * @param bFixedWindow Flag to know if the window is fixed or mobile
	 * @return Date to use
	 */
	Date GetChartStartDate(int iDays, boolean bFixedWindow) {
		
		// Compute interval
		long lInterval = iDays * 24 * 60 * 60 * 1000;
		
		// Create now and get ms
		Date oNow = new Date();
		long lNow = oNow.getTime();
		
		// Compute start date
		long lStartDate = lNow - lInterval;
		
		DateTimeZone.setDefault(DateTimeZone.UTC);
		
		// Create date
		DateTime oRetDate = new DateTime(lStartDate);
		//LocalDateTime oLocalRetDate = new LocalDateTime(lStartDate);
		//DateTime oRetDate = new LocalDateTime(lStartDate).toDateTime();  
		
		// If is fixed
		if (bFixedWindow) {
			// Starts at 00:00
			oRetDate = oRetDate.withHourOfDay(0);
			oRetDate = oRetDate.withMinuteOfHour(0);
			oRetDate = oRetDate.withSecondOfMinute(0);
			oRetDate = oRetDate.withMillisOfSecond(0);
		}
		
		long lRetDate = oRetDate.getMillis();
		Date oTest = new Date(lRetDate);
		
		// Return Date
		return oRetDate.toDate();
	}
	
	/**
	 * Saves a Standard Chart
	 * @param aoInfo
	 * @param oStationAnag
	 * @param asOtherLinks
	 * @param oStationDataRepository
	 * @param oChartsStartDate
	 */
	DataChart SaveStandardChart( List<ChartInfo> aoInfo, StationAnag oStationAnag, ArrayList<String> asOtherLinks, StationDataRepository oStationDataRepository, Date oChartsStartDate) {
		return SaveStandardChart(aoInfo, oStationAnag, asOtherLinks, oStationDataRepository, oChartsStartDate, true);
	}
	
	DataChart SaveStandardChart( List<ChartInfo> aoInfo, StationAnag oStationAnag, ArrayList<String> asOtherLinks, StationDataRepository oStationDataRepository, Date oChartsStartDate, boolean bSave) {
		return SaveStandardChart(aoInfo, oStationAnag, asOtherLinks, oStationDataRepository, oChartsStartDate, true, false);
	}
	
	/**
	 * Saves a Standard Chart
	 * @param aoInfo
	 * @param oStationAnag
	 * @param asOtherLinks
	 * @param oStationDataRepository
	 * @param oChartsStartDate
	 * @param bSave
	 * @return
	 */
	DataChart SaveStandardChart( List<ChartInfo> aoInfo, StationAnag oStationAnag, ArrayList<String> asOtherLinks, StationDataRepository oStationDataRepository, Date oChartsStartDate, boolean bSave, boolean bHourlyStep) {
		DataChart oDataChart = new DataChart();		
		DataSerie oDataSerie = new DataSerie();
		
		oDataSerie.setType(aoInfo.get(0).getType());
		List<DataSeriePoint> aoPoints = null; 
		
		if (bHourlyStep) {
			aoPoints = oStationDataRepository.getHourlyDataSerie(oStationAnag.getStation_code(), aoInfo.get(0).getColumnName(), oChartsStartDate);
		}
		else {
			aoPoints = oStationDataRepository.getDataSerie(oStationAnag.getStation_code(), aoInfo.get(0).getColumnName(), oChartsStartDate);
		}
		
		
		DataSeriePointToDataSerie(aoPoints,oDataSerie, aoInfo.get(0).getConversionFactor());
		
		oDataSerie.setName(aoInfo.get(0).getName());

		oDataChart.getDataSeries().add(oDataSerie);
		oDataChart.setTitle(oStationAnag.getMunicipality() + " - " + oStationAnag.getName());
		oDataChart.setSubTitle(aoInfo.get(0).getSubtitle());
		oDataChart.setAxisYMaxValue(aoInfo.get(0).getAxisYMaxValue());
		oDataChart.setAxisYMinValue(aoInfo.get(0).getAxisYMinValue());
		oDataChart.setAxisYTickInterval(aoInfo.get(0).getAxisYTickInterval());
		oDataChart.setAxisYTitle(aoInfo.get(0).getAxisYTitle());
		oDataChart.setTooltipValueSuffix(aoInfo.get(0).getTooltipValueSuffix());
		
		oDataChart.getOtherChart().addAll(asOtherLinks);
		if (bSave) serializeStationChart(oDataChart,m_oConfig, oStationAnag.getStation_code(), aoInfo.get(0).getFolderName(), m_oDateFormat);
		
		return oDataChart;
		
	}

	/**
	 * Peforms tasks that the Daemon has to do daily
	 */
	private void DailyTask() {
		
		System.out.println("AcronetDaemon - DailyTask");
		
		try {
			ClearThread oThread = new ClearThread(m_oConfig.getFileRepositoryPath());
			oThread.run();			
		}
		catch(Exception oEx) {
			System.out.println("AcronetDaemon - Clear Daemon Exception");
			oEx.printStackTrace();
		}
		
		RefreshConfiguration();
		
		RefreshThresholds();
		
		RefreshStationTables();

		//DeleteTask();
	}
	
	/**
	 * Refresh station anag tables
	 */
	public void RefreshStationTables() {
		try {
			
			StationAnagRepository oStationAnagRepository = new StationAnagRepository();
			
			List<AnagTableInfo> aoTables = m_oConfig.getAnagTablesInfo();
			
			for (int iTables=0; iTables<aoTables.size(); iTables++) {
				AnagTableInfo oTableInfo = aoTables.get(iTables);
				
				List<StationAnag> aoStations = oStationAnagRepository.getListByType(oTableInfo.getColumnName());
				
				SensorListTableViewModel oTable = new SensorListTableViewModel();
				oTable.setSensorTye(oTableInfo.getSensorType());
				
				for (int iStations=0; iStations<aoStations.size(); iStations++) {
					SensorListTableRowViewModel oVM = getSensorListTableRowViewModel(aoStations.get(iStations));
					oTable.getTableRows().add(oVM);
				}
				
				String sFullPath = m_oConfig.getFileRepositoryPath()+"/tables/list";
				
				if (sFullPath != null)  {
					String sFileName = oTable.getSensorTye() + ".xml"; 
					SerializationUtils.serializeObjectToXML(sFullPath+"/"+sFileName, oTable);
				}				
			}
			
			
		} catch (Exception e) {
			
			System.out.println("RefreshStationTables Exception");
			e.printStackTrace();
			return;
		}
	}
	
	/**
	 * Converts a Station Anag in a SensorListTableRow View Model
	 * @param oStationAnag
	 * @return
	 */
	public SensorListTableRowViewModel getSensorListTableRowViewModel(StationAnag oStationAnag) {
		SensorListTableRowViewModel oVM = new SensorListTableRowViewModel();
		
		oVM.setArea("");
		oVM.setBasin(oStationAnag.getRiver());
		oVM.setDistrict(oStationAnag.getDistrict());
		oVM.setMunicipality(oStationAnag.getMunicipality());
		oVM.setName(oStationAnag.getName());
		oVM.setNetwork("");
		oVM.setStationCode(oStationAnag.getStation_code());
		
		return oVM;
	}

	/**
	 * Reloads configuration
	 */
	public void RefreshConfiguration() {
		try {
			// Refresh Configuration File
			System.out.println("AcronetDaemon - Reading Configuration File " + m_sConfigurationFile);
			m_oConfig = (AcronetDaemonConfiguration) SerializationUtils.deserializeXMLToObject(m_sConfigurationFile);
		} catch (Exception e) {
			
			System.out.println("RefreshConfiguration Exception");
			e.printStackTrace();
			return;
		}		
	}
	
	/**
	 * Reads thresholds from db
	 */
	public void RefreshThresholds() {
		try {
			
			// Refresh thresholds
			System.out.println("AcronetDaemon - Refreshing Thresholds");
			
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
			
			System.out.println("AcronetDaemon - Error reading Sensor Thresholds.");
			e.printStackTrace();
		}		
	}

	/**
	 * Checks if date is changed
	 * @param oActualDate
	 * @param oLastDate
	 * @return
	 */
	public boolean DayChanged(Date oActualDate, Date oLastDate)
	{
		if (oLastDate==null) return true;
		
		long lActualTime = oActualDate.getTime();
		long lLastTime = oLastDate.getTime();
		
		long lDay = 24L*60L*60L*1000L;
		
		if ((lActualTime-lLastTime)>lDay) return true;
		return false;
	}
	
	/**
	 * Converts Points from db to points for xml exchange format
	 * @param aoPoints
	 * @param oDataSerie
	 */
	public void DataSeriePointToDataSerie(List<DataSeriePoint> aoPoints, DataSerie oDataSerie) {
		DataSeriePointToDataSerie(aoPoints, oDataSerie, 1.0);
	}

	/**
	 * Converts Points from db to points for xml exchange format
	 * @param aoPoints
	 * @param oDataSerie
	 * @param dConversionFactor
	 */
	public void DataSeriePointToDataSerie(List<DataSeriePoint> aoPoints, DataSerie oDataSerie, double dConversionFactor) {
		
		try {
			if (aoPoints != null) {
				for (int iPoints = 0; iPoints<aoPoints.size(); iPoints++) {
					Object [] adPoint = new Object[2];
					adPoint[0] = new Long(aoPoints.get(iPoints).getRefDate().getTime());
					adPoint[1] = new Double(aoPoints.get(iPoints).getVal())*dConversionFactor;
					oDataSerie.getData().add(adPoint);
				}
				
				if (aoPoints.size()>1){
					DateTime oNow = new DateTime();
					long lFirstStep = aoPoints.get(0).getRefDate().getTime();
					long lSecondStep = aoPoints.get(1).getRefDate().getTime();
					long lStep = lSecondStep-lFirstStep;
					
					long lNow = oNow.getMillis();
					
					long lTime = aoPoints.get(aoPoints.size()-1).getRefDate().getTime();
					lTime+=lStep;
					
					for (; lTime<lNow; lTime+=lStep) {
						Object [] adPoint = new Object[2];
						adPoint[0] = new Long(lTime);
						adPoint[1] = null;
						oDataSerie.getData().add(adPoint);
					}
				}
			}					
		}
		catch(Exception oEx) {
			oEx.printStackTrace();
		}
	}
	
	/**
	 * Serializes an XML file with the last observations of all the stations with a specified sensor
	 * @param sName
	 * @param oLastRepo
	 * @param oConfig
	 * @param oDateFormat
	 */
	public void SerializeSensorLast(String sName, StationLastDataRepository oLastRepo) {
		
		
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
				
				String sFullPath = getSubPath(m_oConfig.getFileRepositoryPath()+"/stations/" + sName,oDate);
				
				if (sFullPath != null)  {
					String sFileName = sName+m_oDateFormat.format(oDate)+".xml"; 
					SerializationUtils.serializeObjectToXML(sFullPath+"/"+sFileName, aoSensoViewModel);
				}
			}
			else {
				System.out.println("AcronetDaemon - There was an error reading last values");
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
//		else if (sType == "rain") {
//			for (SensorViewModel oViewModel : aoSensorList) {
//				oViewModel.setValue(oViewModel.getValue()/10.0);
//			}
//		}		
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
	public void getStationDataSerie(String sStationCode, String sColumnName, Date oStartDate) {
		
		StationDataRepository oStationDataRepository = new StationDataRepository();
		
		List<DataSeriePoint> aoPoints = oStationDataRepository.getDataSerie(sStationCode,sColumnName,oStartDate);
		
		if (aoPoints != null) {
			for (DataSeriePoint oDataSeriePoint : aoPoints) {
				System.out.println("Time " + oDataSeriePoint.getRefDate() + ": " + oDataSeriePoint.getVal());
			}
		}
	}
	
	/**
	 * Serializes a station Chart on disk
	 * @param oChart
	 * @param oConfig
	 * @param sStationCode
	 * @param sChartName
	 * @param oDateFormat
	 */
	public void serializeStationChart(DataChart oChart, AcronetDaemonConfiguration oConfig, String sStationCode, String sChartName, DateFormat oDateFormat) {
		try {
			Date oDate = new Date();
			
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
	
	/**
	 * Writes a Sample Configuration
	 */
	public static void WriteSampleConfig() {
		AcronetDaemonConfiguration oConfig = new AcronetDaemonConfiguration();
		oConfig.setFileRepositoryPath("C:\\temp\\Omirl\\Files");
		oConfig.setMinutesPolling(2);
		oConfig.setChartTimeRangeDays(16);
		
		ChartInfo oInfo = new ChartInfo();
		oInfo.setAxisYMaxValue(150.0);
		oInfo.setAxisYMinValue(0.0);
		oInfo.setAxisYTickInterval(5.0);
		oInfo.setAxisYTitle("Pioggia 5m (mm)");
		oInfo.setColumnName("rain_05m");
		oInfo.setConversionFactor(1.0);
		oInfo.setFolderName("rainnative");
		oInfo.setName("Pioggia 5m");
		oInfo.setSensorType("PluvioNative");
		oInfo.setSubtitle("Pioggia Nativa");
		oInfo.setTooltipValueSuffix(" mm");
		oInfo.setType("column");
		
		oConfig.getChartsInfo().add(oInfo);
		
		oInfo = new ChartInfo();
		oInfo.setAxisYMaxValue(600.0);
		oInfo.setAxisYMinValue(0.0);
		oInfo.setAxisYTickInterval(20.0);
		oInfo.setAxisYTitle("Precipitazione Cumulata (mm)");
		oInfo.setColumnName("rain_05m");
		oInfo.setConversionFactor(1.0);
		oInfo.setFolderName("rainnative");
		oInfo.setName("Cumulata");
		oInfo.setSensorType("PluvioNative");
		oInfo.setSubtitle("Pioggia Nativa Cumulata");
		oInfo.setTooltipValueSuffix(" mm");
		oInfo.setType("line");
		
		oConfig.getChartsInfo().add(oInfo);
		
		
		oInfo = new ChartInfo();
		oInfo.setAxisYMaxValue(150.0);
		oInfo.setAxisYMinValue(0.0);
		oInfo.setAxisYTickInterval(5.0);
		oInfo.setAxisYTitle("Pioggia 10m (mm)");
		oInfo.setColumnName("rain_10m");
		oInfo.setConversionFactor(1.0);
		oInfo.setFolderName("rainnative");
		oInfo.setName("Pioggia 10m");
		oInfo.setSensorType("PluvioNative");
		oInfo.setSubtitle("Pioggia Nativa");
		oInfo.setTooltipValueSuffix(" mm");
		oInfo.setType("column");
		
		oConfig.getChartsInfo().add(oInfo);
		
		oInfo = new ChartInfo();
		oInfo.setAxisYMaxValue(600.0);
		oInfo.setAxisYMinValue(0.0);
		oInfo.setAxisYTickInterval(20.0);
		oInfo.setAxisYTitle("Precipitazione Cumulata (mm)");
		oInfo.setColumnName("rain_10m");
		oInfo.setConversionFactor(1.0);
		oInfo.setFolderName("rainnative");
		oInfo.setName("Cumulata");
		oInfo.setSensorType("PluvioNative");
		oInfo.setSubtitle("Pioggia Nativa Cumulata");
		oInfo.setTooltipValueSuffix(" mm");
		oInfo.setType("line");
		
		oConfig.getChartsInfo().add(oInfo);
		
		oInfo = new ChartInfo();
		oInfo.setAxisYMaxValue(150.0);
		oInfo.setAxisYMinValue(0.0);
		oInfo.setAxisYTickInterval(5.0);
		oInfo.setAxisYTitle("Pioggia 15m (mm)");
		oInfo.setColumnName("rain_15m");
		oInfo.setConversionFactor(1.0);
		oInfo.setFolderName("rainnative");
		oInfo.setName("Pioggia 15m");
		oInfo.setSensorType("PluvioNative");
		oInfo.setSubtitle("Pioggia Nativa");
		oInfo.setTooltipValueSuffix(" mm");
		oInfo.setType("column");
		
		oConfig.getChartsInfo().add(oInfo);
		
		oInfo = new ChartInfo();
		oInfo.setAxisYMaxValue(600.0);
		oInfo.setAxisYMinValue(0.0);
		oInfo.setAxisYTickInterval(20.0);
		oInfo.setAxisYTitle("Precipitazione Cumulata (mm)");
		oInfo.setColumnName("rain_15m");
		oInfo.setConversionFactor(1.0);
		oInfo.setFolderName("rainnative");
		oInfo.setName("Cumulata");
		oInfo.setSensorType("PluvioNative");
		oInfo.setSubtitle("Pioggia Nativa Cumulata");
		oInfo.setTooltipValueSuffix(" mm");
		oInfo.setType("line");
		
		oConfig.getChartsInfo().add(oInfo);
		
		oInfo = new ChartInfo();
		oInfo.setAxisYMaxValue(150.0);
		oInfo.setAxisYMinValue(0.0);
		oInfo.setAxisYTickInterval(5.0);
		oInfo.setAxisYTitle("Pioggia 30m (mm)");
		oInfo.setColumnName("rain_30m");
		oInfo.setConversionFactor(1.0);
		oInfo.setFolderName("rainnative");
		oInfo.setName("Pioggia 30m");
		oInfo.setSensorType("PluvioNative");
		oInfo.setSubtitle("Pioggia Nativa");
		oInfo.setTooltipValueSuffix(" mm");
		oInfo.setType("column");
		
		oConfig.getChartsInfo().add(oInfo);
		
		oInfo = new ChartInfo();
		oInfo.setAxisYMaxValue(600.0);
		oInfo.setAxisYMinValue(0.0);
		oInfo.setAxisYTickInterval(20.0);
		oInfo.setAxisYTitle("Precipitazione Cumulata (mm)");
		oInfo.setColumnName("rain_30m");
		oInfo.setConversionFactor(1.0);
		oInfo.setFolderName("rainnative");
		oInfo.setName("Cumulata");
		oInfo.setSensorType("PluvioNative");
		oInfo.setSubtitle("Pioggia Nativa Cumulata");
		oInfo.setTooltipValueSuffix(" mm");
		oInfo.setType("line");
		
		oConfig.getChartsInfo().add(oInfo);
		
		
		oInfo = new ChartInfo();
		oInfo.setAxisYMaxValue(150.0);
		oInfo.setAxisYMinValue(0.0);
		oInfo.setAxisYTickInterval(5.0);
		oInfo.setAxisYTitle("Pioggia Oraria (mm)");
		oInfo.setColumnName("rain_01h");
		oInfo.setConversionFactor(1.0);
		oInfo.setFolderName("rain1h");
		oInfo.setName("Pioggia 1h");
		oInfo.setSensorType("Pluvio");
		oInfo.setSubtitle("Pioggia");
		oInfo.setTooltipValueSuffix(" mm");
		oInfo.setType("column");
		
		oConfig.getChartsInfo().add(oInfo);
		
		oInfo = new ChartInfo();
		oInfo.setAxisYMaxValue(600.0);
		oInfo.setAxisYMinValue(0.0);
		oInfo.setAxisYTickInterval(20.0);
		oInfo.setAxisYTitle("Precipitazione Cumulata (mm)");
		oInfo.setColumnName("rain_01h");
		oInfo.setConversionFactor(1.0);
		oInfo.setFolderName("rain1h");
		oInfo.setName("Cumulata");
		oInfo.setSensorType("PluvioNative");
		oInfo.setSubtitle("Pioggia Nativa Cumulata");
		oInfo.setTooltipValueSuffix(" mm");
		oInfo.setType("line");
		
		oConfig.getChartsInfo().add(oInfo);
		

		oInfo = new ChartInfo();
		oInfo.setAxisYMaxValue(36.0);
		oInfo.setAxisYMinValue(-4.0);
		oInfo.setAxisYTickInterval(2.0);
		oInfo.setAxisYTitle("Temperatura (°C)");
		oInfo.setColumnName("mean_air_temp");
		oInfo.setConversionFactor(1.0);
		oInfo.setFolderName("temp");
		oInfo.setName("Temperatura");
		oInfo.setSensorType("Termo");
		oInfo.setSubtitle("Temperatura");
		oInfo.setTooltipValueSuffix(" °C");
		oInfo.setType("line");
		
		oConfig.getChartsInfo().add(oInfo);


		oInfo = new ChartInfo();
		oInfo.setAxisYMaxValue(10.0);
		oInfo.setAxisYMinValue(-1.0);
		oInfo.setAxisYTickInterval(1.0);
		oInfo.setAxisYTitle("Livello Idrometrico (m)");
		oInfo.setColumnName("mean_creek_level");
		oInfo.setConversionFactor(0.1);
		oInfo.setFolderName("idro");
		oInfo.setName("Livello Idrometrico");
		oInfo.setSensorType("Idro");
		oInfo.setSubtitle("Livello Idrometrico");
		oInfo.setTooltipValueSuffix(" m");
		oInfo.setType("line");
		
		oConfig.getChartsInfo().add(oInfo);
		

		oInfo = new ChartInfo();
		oInfo.setAxisYMaxValue(150.0);
		oInfo.setAxisYMinValue(0.0);
		oInfo.setAxisYTickInterval(10.0);
		oInfo.setAxisYTitle("Velocità (km/h)");
		oInfo.setColumnName("mean_wind_speed");
		oInfo.setConversionFactor(3.6);
		oInfo.setFolderName("wind");
		oInfo.setName("Velocità del Vento");
		oInfo.setSensorType("Vento");
		oInfo.setSubtitle("Vento");
		oInfo.setTooltipValueSuffix(" km/h");
		oInfo.setType("line");
		
		oConfig.getChartsInfo().add(oInfo);		
		
		

		oInfo = new ChartInfo();
		oInfo.setAxisYMaxValue(100.0);
		oInfo.setAxisYMinValue(0.0);
		oInfo.setAxisYTickInterval(10.0);
		oInfo.setAxisYTitle("Umidità Relativa (%)");
		oInfo.setColumnName("humidity");
		oInfo.setConversionFactor(1.0);
		oInfo.setFolderName("igro");
		oInfo.setName("Umidità Relativa");
		oInfo.setSensorType("Igro");
		oInfo.setSubtitle("Umidità");
		oInfo.setTooltipValueSuffix(" %");
		oInfo.setType("line");
		
		oConfig.getChartsInfo().add(oInfo);		
		
		oInfo = new ChartInfo();
		oInfo.setAxisYMaxValue(1200.0);
		oInfo.setAxisYMinValue(0.0);
		oInfo.setAxisYTickInterval(50.0);
		oInfo.setAxisYTitle("Radiazione (W/m2)");
		oInfo.setColumnName("solar_radiation_pwr");
		oInfo.setConversionFactor(10.0);
		oInfo.setFolderName("radio");
		oInfo.setName("Radiazione Solare Media");
		oInfo.setSensorType("Radio");
		oInfo.setSubtitle("Radiometri");
		oInfo.setTooltipValueSuffix(" W/m2");
		oInfo.setType("line");
		
		oConfig.getChartsInfo().add(oInfo);				
		
		oInfo = new ChartInfo();
		oInfo.setAxisYMaxValue(100.0);
		oInfo.setAxisYMinValue(0.0);
		oInfo.setAxisYTickInterval(10.0);
		oInfo.setAxisYTitle("Bagnatura Fogliare (%)");
		oInfo.setColumnName("leaf_wetness");
		oInfo.setConversionFactor(1.0);
		oInfo.setFolderName("leafs");
		oInfo.setName("Bagnatura Fogliare");
		oInfo.setSensorType("Foglie");
		oInfo.setSubtitle("Percentuale");
		oInfo.setTooltipValueSuffix(" %");
		oInfo.setType("line");
		
		oConfig.getChartsInfo().add(oInfo);	
		
	
		oInfo = new ChartInfo();
		oInfo.setAxisYMaxValue(1040.0);
		oInfo.setAxisYMinValue(980.0);
		oInfo.setAxisYTickInterval(5.0);
		oInfo.setAxisYTitle("Pressione al livello del mare (hPa)");
		oInfo.setColumnName("mean_sea_level_press");
		oInfo.setConversionFactor(1.0);
		oInfo.setFolderName("press");
		oInfo.setName("Pressione Atmosferica");
		oInfo.setSensorType("Press");
		oInfo.setSubtitle("Pressione al livello del mare");
		oInfo.setTooltipValueSuffix(" hPa");
		oInfo.setType("line");
		
		oConfig.getChartsInfo().add(oInfo);
		
		
		
		oInfo = new ChartInfo();
		oInfo.setAxisYMaxValue(15.0);
		oInfo.setAxisYMinValue(7.0);
		oInfo.setAxisYTickInterval(1.0);
		oInfo.setAxisYTitle("Tensione Batteria (V)");
		oInfo.setColumnName("battery_voltage");
		oInfo.setConversionFactor(1.0);
		oInfo.setFolderName("batt");
		oInfo.setName("Tensione Batteria");
		oInfo.setSensorType("Batt");
		oInfo.setSubtitle("Tensione Batteria");
		oInfo.setTooltipValueSuffix(" V");
		oInfo.setType("line");
		
		oConfig.getChartsInfo().add(oInfo);
		
		
		oInfo = new ChartInfo();
		oInfo.setAxisYMaxValue(15.0);
		oInfo.setAxisYMinValue(7.0);
		oInfo.setAxisYTickInterval(1.0);
		oInfo.setAxisYTitle("Lunghezza Media Onda(m)");
		oInfo.setColumnName("mean_wave_heigth");
		oInfo.setConversionFactor(1.0);
		oInfo.setFolderName("boa");
		oInfo.setName("Lunghezza d'Onda");
		oInfo.setSensorType("Boa");
		oInfo.setSubtitle("Stato del Mare");
		oInfo.setTooltipValueSuffix(" m");
		oInfo.setType("line");
		
		oConfig.getChartsInfo().add(oInfo);
		
		AnagTableInfo oTableInfo = new AnagTableInfo();
		oTableInfo.setSensorType("Pluvio");
		oTableInfo.setColumnName("rain_01h_every");
		
		oConfig.getAnagTablesInfo().add(oTableInfo);
		
		oTableInfo = new AnagTableInfo();
		oTableInfo.setSensorType("Termo");
		oTableInfo.setColumnName("mean_air_temp_every");
		
		oConfig.getAnagTablesInfo().add(oTableInfo);
		
		oTableInfo = new AnagTableInfo();
		oTableInfo.setSensorType("Idro");
		oTableInfo.setColumnName("mean_creek_level_every");
		
		oConfig.getAnagTablesInfo().add(oTableInfo);
		
		oTableInfo = new AnagTableInfo();
		oTableInfo.setSensorType("Vento");
		oTableInfo.setColumnName("mean_wind_speed_every");
		
		oConfig.getAnagTablesInfo().add(oTableInfo);
		
		oTableInfo = new AnagTableInfo();
		oTableInfo.setSensorType("Igro");
		oTableInfo.setColumnName("humidity_every");
		
		oConfig.getAnagTablesInfo().add(oTableInfo);
		
		oTableInfo = new AnagTableInfo();
		oTableInfo.setSensorType("Radio");
		oTableInfo.setColumnName("solar_radiation_pwr_every");
		
		oConfig.getAnagTablesInfo().add(oTableInfo);
		
		oTableInfo = new AnagTableInfo();
		oTableInfo.setSensorType("Foglie");
		oTableInfo.setColumnName("leaf_wetness_every");
		
		oConfig.getAnagTablesInfo().add(oTableInfo);
		
		oTableInfo = new AnagTableInfo();
		oTableInfo.setSensorType("Press");
		oTableInfo.setColumnName("mean_sea_level_press_every");
		
		oConfig.getAnagTablesInfo().add(oTableInfo);
		
		oTableInfo = new AnagTableInfo();
		oTableInfo.setSensorType("Batt");
		oTableInfo.setColumnName("battery_voltage_every");
		
		oConfig.getAnagTablesInfo().add(oTableInfo);
		
		/*
		oTableInfo = new AnagTableInfo();
		oTableInfo.setColumnName("Boa");
		oTableInfo.setSensorType("");
		
		oConfig.getAnagTablesInfo().add(oTableInfo);
		
		oTableInfo = new AnagTableInfo();
		oTableInfo.setColumnName("Neve");
		oTableInfo.setSensorType("");
		
		oConfig.getAnagTablesInfo().add(oTableInfo);
		*/
		
		
		
		try {
			SerializationUtils.serializeObjectToXML("C:\\temp\\Omirl\\AcronetDaemonConfigSAMPLE.xml", oConfig);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void Test() {
		try {
			StationAnagRepository oRepo = new StationAnagRepository();
			
			List<StationAnag> aoRains = oRepo.getListByType("rain_01h_every");
			
			System.out.println(aoRains.size());
			
			StationAnag oStation = oRepo.selectByStationCode("AGORR");
			
			if (oStation != null) {
				System.out.println(oStation.getName());
			}
			
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
		
		SavedPeriodRepository oSavedPeriodRepository = new SavedPeriodRepository();
		boolean bRet = oSavedPeriodRepository.IsSavedPeriod(new Date(1407799800000l));
		if (bRet) System.out.println("SALVALO ");
		else System.out.println("ELIMINA ");
		
	}


}
