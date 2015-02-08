package it.fadeout.omirl.daemon;

import it.fadeout.omirl.business.AnagTableInfo;
import it.fadeout.omirl.business.ChartAxis;
import it.fadeout.omirl.business.ChartInfo;
import it.fadeout.omirl.business.ChartLine;
import it.fadeout.omirl.business.CreekThreshold;
import it.fadeout.omirl.business.DataChart;
import it.fadeout.omirl.business.DataSerie;
import it.fadeout.omirl.business.DataSeriePoint;
import it.fadeout.omirl.business.DynamicLayerInfo;
import it.fadeout.omirl.business.SensorLastData;
import it.fadeout.omirl.business.Sfloc;
import it.fadeout.omirl.business.StationAnag;
import it.fadeout.omirl.business.StationLastData;
import it.fadeout.omirl.business.SummaryInfoEntity;
import it.fadeout.omirl.business.WindSummaryConfiguration;
import it.fadeout.omirl.daemon.geoserver.GeoServerDataManager2;
import it.fadeout.omirl.data.CreekThresholdRepository;
import it.fadeout.omirl.data.HibernateUtils;
import it.fadeout.omirl.data.OpenSessionRepository;
import it.fadeout.omirl.data.SavedPeriodRepository;
import it.fadeout.omirl.data.SflocRepository;
import it.fadeout.omirl.data.StationAnagRepository;
import it.fadeout.omirl.data.StationDataRepository;
import it.fadeout.omirl.data.StationLastDataRepository;
import it.fadeout.omirl.viewmodels.AlertZoneSummaryInfo;
import it.fadeout.omirl.viewmodels.DistrictSummaryInfo;
import it.fadeout.omirl.viewmodels.SensorListTableRowViewModel;
import it.fadeout.omirl.viewmodels.SensorListTableViewModel;
import it.fadeout.omirl.viewmodels.SensorViewModel;
import it.fadeout.omirl.viewmodels.SummaryInfo;
import it.fadeout.omirl.viewmodels.WindSummaryInfo;

import java.io.File;
import java.io.FileFilter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;

public class OmirlDaemon {

	HashMap<String, CreekThreshold> m_aoThresholds = new HashMap<>();
	private OmirlDaemonConfiguration m_oConfig;
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
			System.out.println("OmirlDaemon - Missing Configuration File Config");
			System.out.println("Usage OmirlDaemon \"FILEPATH\". Closing now");
			return;
		}

		//Test();
		//testDate();

		//WriteSampleConfig();

		OmirlDaemon oDaemon = new OmirlDaemon();
		oDaemon.OmirlDaemonCycle(args[0]);
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

		//TEST
		//publishMaps();
		//if (true) return;

		//summaryTable();

		Date oLastDate = null;

		try {

			// Cycle Forever!
			while (true) {

				Date oActualDate = new Date();

				// Start 
				System.out.println("OmirlDaemon - Cycle Start " + oActualDate);	

				System.out.println("OmirlDaemon - DATE TEST FIXED " + GetChartStartDate(2, true));
				System.out.println("OmirlDaemon - DATE TEST MOBILE " + GetChartStartDate(2, false));

				if (DayChanged(oActualDate, oLastDate)) {
					oLastDate=oActualDate;
					DailyTask();
				}

				try {
					
					// CHARTS ***********************************************************************
					StationAnagRepository oStationAnagRepository = new StationAnagRepository();
					StationDataRepository oStationDataRepository = new StationDataRepository();

					// Get Start Date Time Filter
					long lNowTime = new Date().getTime();

					long lInterval = m_oConfig.chartTimeRangeDays * 24 * 60 * 60 * 1000;
					Date oChartsStartDate = new Date(lNowTime-lInterval);				

					// Get all the stations
					List<StationAnag> aoAllStations = oStationAnagRepository.SelectAll(StationAnag.class);
					//ArrayList<StationAnag> aoAllStations = new ArrayList<>();
					//aoAllStations.add(oStationAnagRepository.selectByStationCode("PCERR"));

					// For Each
					for (StationAnag oStationAnag : aoAllStations) {
						
						ArrayList<String> asOtherLinks = new ArrayList<>();

						// Find other sensors links
						if (oStationAnag.getRain_01h_every() != null) {

							// If there is rain add std 3gg chart
							asOtherLinks.add("Pluvio");

							// Check for Native Rain (1d chart)
							String sNativeColumn = getRainColumnNameFromNative(oStationAnag.getRain_01h_every());

							if (sNativeColumn!=null) {
								if (!sNativeColumn.isEmpty()) {
									asOtherLinks.add("PluvioNative");
								}
							}

							// Add 7 gg chart
							asOtherLinks.add("Pluvio7");

							// Check 1d column for 30gg chart
							if (oStationAnag.getRain_24h_every() != null) {
								asOtherLinks.add("Pluvio30");
							}
						}
						if (oStationAnag.getMean_air_temp_every() != null) asOtherLinks.add("Termo");
						if (oStationAnag.getMean_creek_level_every() != null) asOtherLinks.add("Idro");
						if (oStationAnag.getMean_wind_speed_every() != null) {
							asOtherLinks.add("Vento");
							asOtherLinks.add("Vento2");
						}
						if (oStationAnag.getHumidity_every() != null) asOtherLinks.add("Igro");
						if (oStationAnag.getSolar_radiation_pwr_every() != null) asOtherLinks.add("Radio");
						if (oStationAnag.getLeaf_wetness_every() != null) asOtherLinks.add("Foglie");
						if (oStationAnag.getMean_sea_level_press_every() != null)  asOtherLinks.add("Press");
						if (oStationAnag.getBattery_voltage_every() != null) asOtherLinks.add("Batt");
						//if (oStationAnag.get != null) asOtherLinks.add("humidity");


						try {
							// --------------------------------------------------------RAIN CHART
							if (oStationAnag.getRain_01h_every() != null) {

								List<ChartInfo> aoInfo = getChartInfoFromSensorCode("Pluvio");

								// Initialize Start Date
								Date oStartDate = GetChartStartDate(oChartsStartDate, aoInfo);

								DataChart oDataChart = SaveStandardChart(aoInfo,oStationAnag,asOtherLinks,oStationDataRepository,oStartDate, false, true);
								DataSerie oDataSerie = oDataChart.getDataSeries().get(0); 

								// Create Additional Axes
								ChartAxis oAdditionalAxis = new ChartAxis();
								oAdditionalAxis.setAxisYMaxValue(aoInfo.get(1).getAxisYMaxValue());
								oAdditionalAxis.setAxisYMinValue(aoInfo.get(1).getAxisYMinValue());
								oAdditionalAxis.setAxisYTickInterval(aoInfo.get(1).getAxisYTickInterval());
								oAdditionalAxis.setAxisYTitle(aoInfo.get(1).getAxisYTitle());
								oAdditionalAxis.setIsOpposite(true);

								oDataChart.getVerticalAxes().add(oAdditionalAxis);

								// Add Cumulated Serie
								DataSerie oCumulatedSerie = new DataSerie();
								oCumulatedSerie.setName(aoInfo.get(1).getName());
								oCumulatedSerie.setType(aoInfo.get(1).getType());
								// Refer to other axes
								oCumulatedSerie.setAxisId(1);

								FillCumulatedSerie(oDataSerie,oCumulatedSerie);

								oDataChart.getDataSeries().add(oCumulatedSerie);

								// Set dash style if in configuration
								if (aoInfo.get(1).getDashStyle() != null) {
									oCumulatedSerie.setDashStyle(aoInfo.get(1).getDashStyle());
								}

								// Set color if in configuration
								if (aoInfo.get(1).getColor() != null) {
									oCumulatedSerie.setColor(aoInfo.get(1).getColor());
								}

								// Set line width if in configuration
								if (aoInfo.get(1).getLineWidth()>0) oCumulatedSerie.setLineWidth(aoInfo.get(1).getLineWidth());

								// Check for autorange on max exceed
								CheckCumulateAutorange(oCumulatedSerie,oAdditionalAxis);

								// Save
								serializeStationChart(oDataChart,m_oConfig, oStationAnag.getStation_code(), aoInfo.get(1).getFolderName(), m_oDateFormat);

								aoInfo = getChartInfoFromSensorCode("Pluvio7");

								// Initialize Start Date
								oStartDate = GetChartStartDate(oChartsStartDate, aoInfo);

								oDataChart = SaveStandardChart(aoInfo,oStationAnag,asOtherLinks,oStationDataRepository,oStartDate, false, true);
								oDataSerie = oDataChart.getDataSeries().get(0); 

								// Create Additional Axes
								oAdditionalAxis = new ChartAxis();
								oAdditionalAxis.setAxisYMaxValue(aoInfo.get(1).getAxisYMaxValue());
								oAdditionalAxis.setAxisYMinValue(aoInfo.get(1).getAxisYMinValue());
								oAdditionalAxis.setAxisYTickInterval(aoInfo.get(1).getAxisYTickInterval());
								oAdditionalAxis.setAxisYTitle(aoInfo.get(1).getAxisYTitle());
								oAdditionalAxis.setIsOpposite(true);

								oDataChart.getVerticalAxes().add(oAdditionalAxis);

								// Add Cumulated Serie
								oCumulatedSerie = new DataSerie();
								oCumulatedSerie.setName(aoInfo.get(1).getName());
								oCumulatedSerie.setType(aoInfo.get(1).getType());
								// Refer to other axes
								oCumulatedSerie.setAxisId(1);

								FillCumulatedSerie(oDataSerie,oCumulatedSerie);

								oDataChart.getDataSeries().add(oCumulatedSerie);

								if (aoInfo.get(1).getDashStyle() != null) {
									oCumulatedSerie.setDashStyle(aoInfo.get(1).getDashStyle());
								}

								if (aoInfo.get(1).getColor() != null) {
									oCumulatedSerie.setColor(aoInfo.get(1).getColor());
								}

								if (aoInfo.get(1).getLineWidth()>0) oCumulatedSerie.setLineWidth(aoInfo.get(1).getLineWidth());

								// Check for autorange on max exceed
								CheckCumulateAutorange(oCumulatedSerie,oAdditionalAxis);
								serializeStationChart(oDataChart,m_oConfig, oStationAnag.getStation_code(), aoInfo.get(1).getFolderName(), m_oDateFormat);
							}
						}
						catch(Exception oChartEx) {
							oChartEx.printStackTrace();
						}


						// --------------------------------------------------------RAIN NATIVE CHART
						try {

							String sNativeColumn = "";

							if (oStationAnag.getRain_01h_every() != null) {
								sNativeColumn = getRainColumnNameFromNative(oStationAnag.getRain_01h_every());
							}

							if (sNativeColumn!=null) {
								if (!sNativeColumn.isEmpty()) {

									List<ChartInfo> aoInfo = getChartInfoFromSensorColumn(sNativeColumn);

									// Initialize Start Date
									Date oStartDate = GetChartStartDate(oChartsStartDate, aoInfo);

									// Create the Chart
									DataChart oDataChart = SaveStandardChart(aoInfo,oStationAnag,asOtherLinks,oStationDataRepository,oStartDate, false, false);
									DataSerie oDataSerie = oDataChart.getDataSeries().get(0); 

									// Create Additional Axes
									ChartAxis oAdditionalAxis = new ChartAxis();
									oAdditionalAxis.setAxisYMaxValue(aoInfo.get(1).getAxisYMaxValue());
									oAdditionalAxis.setAxisYMinValue(aoInfo.get(1).getAxisYMinValue());
									oAdditionalAxis.setAxisYTickInterval(aoInfo.get(1).getAxisYTickInterval());
									oAdditionalAxis.setAxisYTitle(aoInfo.get(1).getAxisYTitle());
									oAdditionalAxis.setIsOpposite(true);

									oDataChart.getVerticalAxes().add(oAdditionalAxis);

									// Add Cumulated Serie
									DataSerie oCumulatedSerie = new DataSerie();
									oCumulatedSerie.setName(aoInfo.get(1).getName());
									oCumulatedSerie.setType(aoInfo.get(1).getType());
									// Refer to other axes
									oCumulatedSerie.setAxisId(1);

									FillCumulatedSerie(oDataSerie,oCumulatedSerie);

									oDataChart.getDataSeries().add(oCumulatedSerie);

									if (aoInfo.get(1).getDashStyle() != null) {
										oCumulatedSerie.setDashStyle(aoInfo.get(1).getDashStyle());
									}

									if (aoInfo.get(1).getColor() != null) {
										oCumulatedSerie.setColor(aoInfo.get(1).getColor());
									}

									if (aoInfo.get(1).getLineWidth()>0) oCumulatedSerie.setLineWidth(aoInfo.get(1).getLineWidth());

									// Check for autorange on max exceed
									CheckCumulateAutorange(oCumulatedSerie,oAdditionalAxis);									
									serializeStationChart(oDataChart,m_oConfig, oStationAnag.getStation_code(), aoInfo.get(0).getFolderName(), m_oDateFormat);
								}
							}

						}
						catch(Exception oChartEx) {
							oChartEx.printStackTrace();
						}

						// --------------------------------------------------------RAIN 30gg CHART
						try {

							String sNativeColumn = "";


							if (asOtherLinks.contains("Pluvio30")) {
								List<ChartInfo> aoInfo = getChartInfoFromSensorCode("Pluvio30");

								// Initialize Start Date
								Date oStartDate = GetChartStartDate(oChartsStartDate, aoInfo);

								// TODO: Controllo che ci siano almeno i 2 ChartInfo che mi aspetto o lascio gestire l'eccezione?!?

								// Create the Chart
								DataChart oDataChart = new DataChart();

								// Create Main Data Serie
								DataSerie oDataSerie = new DataSerie();
								oDataSerie.setType(aoInfo.get(0).getType());

								// Get Data from the Db: for rain1h only hourly rate
								List<DataSeriePoint> aoPoints = oStationDataRepository.getDailyDataSerie(oStationAnag.getStation_code(), aoInfo.get(0).getColumnName(), oStartDate);

								int iMinuteTimeStep = 24*60;

								// Convert points to Data Serie
								DataSeriePointToDataSerie(aoPoints,oDataSerie, aoInfo.get(0).getConversionFactor(), iMinuteTimeStep);
								// Set Serie Name
								oDataSerie.setName(aoInfo.get(0).getName());
								// Main Axis Reference
								oDataSerie.setAxisId(0);
								// Add serie to the chart
								oDataChart.getDataSeries().add(oDataSerie);
								// Set title
								oDataChart.setTitle(oStationAnag.getMunicipality() + " - " + oStationAnag.getName());
								// Subtitle
								oDataChart.setSubTitle(aoInfo.get(0).getSubtitle());
								// Main Axes Values
								oDataChart.setAxisYMaxValue(aoInfo.get(0).getAxisYMaxValue());
								oDataChart.setAxisYMinValue(aoInfo.get(0).getAxisYMinValue());
								oDataChart.setAxisYTickInterval(aoInfo.get(0).getAxisYTickInterval());
								oDataChart.setAxisYTitle(aoInfo.get(0).getAxisYTitle());
								oDataChart.setTooltipValueSuffix(aoInfo.get(0).getTooltipValueSuffix());

								// Create Additional Axes
								ChartAxis oAdditionalAxis = new ChartAxis();
								oAdditionalAxis.setAxisYMaxValue(aoInfo.get(1).getAxisYMaxValue());
								oAdditionalAxis.setAxisYMinValue(aoInfo.get(1).getAxisYMinValue());
								oAdditionalAxis.setAxisYTickInterval(aoInfo.get(1).getAxisYTickInterval());
								oAdditionalAxis.setAxisYTitle(aoInfo.get(1).getAxisYTitle());
								oAdditionalAxis.setIsOpposite(true);

								oDataChart.getVerticalAxes().add(oAdditionalAxis);

								// Add Cumulated Serie
								DataSerie oCumulatedSerie = new DataSerie();
								oCumulatedSerie.setName(aoInfo.get(1).getName());
								oCumulatedSerie.setType(aoInfo.get(1).getType());
								// Refer to other axes
								oCumulatedSerie.setAxisId(1);

								FillCumulatedSerie(oDataSerie,oCumulatedSerie);

								oDataChart.getDataSeries().add(oCumulatedSerie);
								oDataChart.getOtherChart().addAll(asOtherLinks);

								if (aoInfo.get(0).getDashStyle() != null) {
									oDataSerie.setDashStyle(aoInfo.get(0).getDashStyle());
								}

								if (aoInfo.get(1).getDashStyle() != null) {
									oCumulatedSerie.setDashStyle(aoInfo.get(1).getDashStyle());
								}

								if (aoInfo.get(0).getColor() != null) {
									oDataSerie.setColor(aoInfo.get(0).getColor());
								}

								if (aoInfo.get(1).getColor() != null) {
									oCumulatedSerie.setColor(aoInfo.get(1).getColor());
								}

								if (aoInfo.get(0).getLineWidth()>0) oDataSerie.setLineWidth(aoInfo.get(0).getLineWidth());
								if (aoInfo.get(1).getLineWidth()>0) oCumulatedSerie.setLineWidth(aoInfo.get(1).getLineWidth());

								// Check for autorange on max exceed
								CheckCumulateAutorange(oCumulatedSerie,oAdditionalAxis);								
								serializeStationChart(oDataChart,m_oConfig, oStationAnag.getStation_code(), aoInfo.get(0).getFolderName(), m_oDateFormat);
							}

						}
						catch(Exception oChartEx) {
							oChartEx.printStackTrace();
						}			


						try {
							// --------------------------------------------------------TEMPERATURE CHART
							if (oStationAnag.getMean_air_temp_every() != null) {

								List<ChartInfo> aoInfo = getChartInfoFromSensorCode("Termo");

								// Initialize Start Date
								Date oStartDate = GetChartStartDate(oChartsStartDate, aoInfo);

								SaveStandardChart(aoInfo,oStationAnag,asOtherLinks,oStationDataRepository,oStartDate);
							}
						}
						catch(Exception oChartEx) {
							oChartEx.printStackTrace();
						}

						try {

							// --------------------------------------------------------HYDRO CHART
							if (oStationAnag.getMean_creek_level_every() != null) {

								List<ChartInfo> aoInfo = getChartInfoFromSensorCode("Idro");

								// Initialize Start Date
								Date oStartDate = GetChartStartDate(oChartsStartDate, aoInfo);

								DataChart oDataChart = SaveStandardChart(aoInfo,oStationAnag,asOtherLinks,oStationDataRepository,oStartDate, false);

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
									oOrange.setColor("#FFC020");
									oOrange.setName("Soglia Arancione");
									oOrange.setValue(oThreshold.getOrange());

									ChartLine oRed = new ChartLine();
									oRed.setColor("#FF0000");
									oRed.setName("Soglia Rossa");
									oRed.setValue(oThreshold.getRed());

									oDataChart.getHorizontalLines().add(oOrange);
									oDataChart.getHorizontalLines().add(oRed);
								}

								serializeStationChart(oDataChart,m_oConfig, oStationAnag.getStation_code(), aoInfo.get(0).getFolderName(), m_oDateFormat);
							}
						}
						catch(Exception oChartEx) {
							oChartEx.printStackTrace();
						}						



						try {

							// --------------------------------------------------------WIND CHART
							if (oStationAnag.getMean_wind_speed_every() != null) {

								List<ChartInfo> aoInfo = getChartInfoFromSensorCode("Vento");

								// Initialize Start Date
								Date oStartDate = GetChartStartDate(oChartsStartDate, aoInfo);

								DataChart oWindChart = SaveStandardChart(aoInfo,oStationAnag,asOtherLinks,oStationDataRepository,oStartDate,false,false);

								if (aoInfo.size()>1) {
									ChartInfo oGustInfo = aoInfo.get(1);

									DataSerie oGustSerie = new DataSerie();
									// Get Data from the Db: for rain1h only hourly rate
									List<DataSeriePoint> aoPoints = oStationDataRepository.getDataSerie(oStationAnag.getStation_code(), oGustInfo.getColumnName(), oStartDate);

									int iMinuteTimeStep = GetMinutesStep(oGustInfo.getColumnName(),oStationAnag);
									// Convert points to Data Serie
									DataSeriePointToDataSerie(aoPoints,oGustSerie, oGustInfo.getConversionFactor(), iMinuteTimeStep);
									// Set Serie Name
									oGustSerie.setName(oGustInfo.getName());
									// Main Axis Reference
									oGustSerie.setAxisId(0);

									if (aoInfo.get(1).getDashStyle() != null) {
										oGustSerie.setDashStyle(aoInfo.get(1).getDashStyle());
									}

									if (aoInfo.get(1).getLineWidth()>0) oGustSerie.setLineWidth(aoInfo.get(1).getLineWidth());
									if (aoInfo.get(1).getColor()!=null) oGustSerie.setColor(aoInfo.get(1).getColor());

									// Add serie to the chart
									oWindChart.getDataSeries().add(oGustSerie);
								}

								serializeStationChart(oWindChart,m_oConfig, oStationAnag.getStation_code(), aoInfo.get(0).getFolderName(), m_oDateFormat);


								aoInfo = getChartInfoFromSensorCode("Vento2");

								// Initialize Start Date
								oStartDate = GetChartStartDate(oChartsStartDate, aoInfo);

								DataChart oWind2Chart = SaveStandardChart(aoInfo,oStationAnag,asOtherLinks,oStationDataRepository,oStartDate,false,false);

								if (aoInfo.size()>1) {
									ChartInfo oGustInfo = aoInfo.get(1);

									DataSerie oGustSerie = new DataSerie();
									// Get Data from the Db: for rain1h only hourly rate
									List<DataSeriePoint> aoPoints = oStationDataRepository.getDataSerie(oStationAnag.getStation_code(), oGustInfo.getColumnName(), oStartDate);

									int iMinuteTimeStep = GetMinutesStep(oGustInfo.getColumnName(),oStationAnag);
									// Convert points to Data Serie
									DataSeriePointToDataSerie(aoPoints,oGustSerie, oGustInfo.getConversionFactor(), iMinuteTimeStep);

									// Set Serie Name
									oGustSerie.setName(oGustInfo.getName());
									// Main Axis Reference
									oGustSerie.setAxisId(0);

									if (aoInfo.get(1).getDashStyle() != null) {
										oGustSerie.setDashStyle(aoInfo.get(1).getDashStyle());
									}

									if (aoInfo.get(1).getLineWidth()>0) oGustSerie.setLineWidth(aoInfo.get(1).getLineWidth());
									if (aoInfo.get(1).getColor()!=null) oGustSerie.setColor(aoInfo.get(1).getColor());

									// Add serie to the chart
									oWind2Chart.getDataSeries().add(oGustSerie);
								}

								serializeStationChart(oWind2Chart,m_oConfig, oStationAnag.getStation_code(), aoInfo.get(0).getFolderName(), m_oDateFormat);
							}
						}
						catch(Exception oChartEx) {
							oChartEx.printStackTrace();
						}							


						try {

							// --------------------------------------------------------UMIDITY CHART
							if (oStationAnag.getHumidity_every() != null) {

								List<ChartInfo> aoInfo = getChartInfoFromSensorCode("Igro");

								// Initialize Start Date
								Date oStartDate = GetChartStartDate(oChartsStartDate, aoInfo);

								SaveStandardChart(aoInfo,oStationAnag,asOtherLinks,oStationDataRepository,oStartDate);
							}
						}
						catch(Exception oChartEx) {
							oChartEx.printStackTrace();
						}							




						try {

							// --------------------------------------------------------RADIATION CHART
							if (oStationAnag.getSolar_radiation_pwr_every() != null) {

								List<ChartInfo> aoInfo = getChartInfoFromSensorCode("Radio");

								// Initialize Start Date
								Date oStartDate = GetChartStartDate(oChartsStartDate, aoInfo);

								SaveStandardChart(aoInfo,oStationAnag,asOtherLinks,oStationDataRepository,oStartDate);
							}
						}
						catch(Exception oChartEx) {
							oChartEx.printStackTrace();
						}			



						try {

							// --------------------------------------------------------BAGNATURA FOGLIARE CHART
							if (oStationAnag.getLeaf_wetness_every() != null) {

								List<ChartInfo> aoInfo = getChartInfoFromSensorCode("Foglie");

								// Initialize Start Date
								Date oStartDate = GetChartStartDate(oChartsStartDate, aoInfo);

								SaveStandardChart(aoInfo,oStationAnag,asOtherLinks,oStationDataRepository,oStartDate);
							}
						}
						catch(Exception oChartEx) {
							oChartEx.printStackTrace();
						}	

						try {

							// --------------------------------------------------------PRESSIONE CHART
							if (oStationAnag.getMean_sea_level_press_every() != null) {

								List<ChartInfo> aoInfo = getChartInfoFromSensorCode("Press");

								// Initialize Start Date
								Date oStartDate = GetChartStartDate(oChartsStartDate, aoInfo);

								SaveStandardChart(aoInfo,oStationAnag,asOtherLinks,oStationDataRepository,oStartDate);
							}
						}
						catch(Exception oChartEx) {
							oChartEx.printStackTrace();
						}	


						try {

							// --------------------------------------------------------BATTERY CHART
							if (oStationAnag.getBattery_voltage_every() != null) {

								List<ChartInfo> aoInfo = getChartInfoFromSensorCode("Batt");

								// Initialize Start Date
								Date oStartDate = GetChartStartDate(oChartsStartDate, aoInfo);

								SaveStandardChart(aoInfo,oStationAnag,asOtherLinks,oStationDataRepository,oStartDate);
							}
						}
						catch(Exception oChartEx) {
							oChartEx.printStackTrace();
						}	


						try {

							// --------------------------------------------------------MARE CHART
							if (oStationAnag.getBattery_voltage_every() != null) {

								List<ChartInfo> aoInfo = getChartInfoFromSensorCode("Boa");

								// Initialize Start Date
								Date oStartDate = GetChartStartDate(oChartsStartDate, aoInfo);

								SaveStandardChart(aoInfo,oStationAnag,asOtherLinks,oStationDataRepository,oStartDate);
							}
						}
						catch(Exception oChartEx) {
							oChartEx.printStackTrace();
						}	
					}

					// Get The stations
					StationLastDataRepository oLastRepo = new StationLastDataRepository();

					SerializeSensorLast("rain1h", oLastRepo);
					SerializeSensorLast("temp", oLastRepo);
					SerializeSensorLast("idro", oLastRepo);
					SerializeSensorLast("igro", oLastRepo);
					SerializeSensorLast("radio", oLastRepo);
					SerializeSensorLast("leafs", oLastRepo);
					SerializeSensorLast("batt", oLastRepo);
					SerializeSensorLast("press", oLastRepo);
					SerializeSensorLast("snow", oLastRepo);
					SerializeSensorLast("boa", oLastRepo);
					SerializeSensorLast("wind", oLastRepo);


					// Serialize ALL SFLOC
					serializeSfloc();

					// Publish new Maps
					publishMaps();

					// Update Summary Table
					summaryTable();
					
					//Delete old session
					deleteOldSession();

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
	
	private void deleteOldSession()
	{
		OpenSessionRepository oRepository = new OpenSessionRepository();
		try{
			int iRowDeleted = oRepository.deleteOldSessionId(m_oConfig.getSessioneTimeout());
			System.out.println("OmirlDaemon - Deleted " + iRowDeleted + " rows");
		}
		catch(Exception oEx){
			oEx.printStackTrace();
		}
	}

	private void serializeSfloc()
	{
		try
		{

			//New repository
			SflocRepository oRepository = new SflocRepository();
			//select last hour
			List<Sfloc> oList = oRepository.selectLastHour(m_oConfig.getSflocTimeRangeDays());
			if (oList != null)
			{
				//base path
				String sBasePath = m_oConfig.getFileRepositoryPath();
				ArrayList<SensorViewModel> oSflocViewModelList = new ArrayList<SensorViewModel>();
				for (Sfloc oSfloc : oList) {
					try {
						SensorViewModel oSensorViewModel = oSfloc.GetSflocViewModel();
						if (oSensorViewModel != null) {
							oSflocViewModelList.add(oSensorViewModel);
						}						
					}
					catch(Exception oInnerEx) {
						oInnerEx.printStackTrace();
					}
				}

				Date oDate = new Date();

				String sFullPath = getSubPath(sBasePath + "/stations/sfloc", oDate);

				if (sFullPath != null)  {
					String sFileName = "sfloc" + m_oDateFormat.format(oDate)+".xml"; 
					SerializationUtils.serializeObjectToXML(sFullPath + "/" + sFileName, oSflocViewModelList);
				}
			}
			else
			{
				System.out.println("OmirlDaemon ");
				System.out.println("OmirlDaemon - There was an error reading last values");
			}
		}
		catch(Exception oEx) {
			oEx.printStackTrace();
		}

	}


	/**
	 * Publish maps
	 */
	private void publishMaps() {
		try {
			System.out.println("ENTRO IN PUBLISH MAPS");

			Date oActualDate = new Date();
			// Get Start Date Time Filter
			long lNowTime = oActualDate.getTime();

			DynamicLayerInfo oLayerInfo = new DynamicLayerInfo();
			oLayerInfo.setLayerId("rainfall12h");
			oLayerInfo.setShapeFile(true);
			oLayerInfo.setStyleId("polygon");

			String sBasePath = m_oConfig.getFileRepositoryPath();

			String sLayerPath = sBasePath + "/maps/" + oLayerInfo.getLayerId();

			SimpleDateFormat oDateFormat = new SimpleDateFormat("yyyy/MM/dd");

			String sFullDir = sLayerPath + "/" + oDateFormat.format(oActualDate);

			File oFullPathDir = new File(sFullDir);

			/*if (!oFullPathDir.exists()) {
				return ;
			}*/

			File oFile = OmirlDaemon.lastFileModified(sFullDir);
			if (oFile == null) return;

			String sFileShp = oFile.getAbsolutePath();
			sFileShp = sFileShp.substring(0, sFileShp.length()-4);
			sFileShp += ".shp";
			oFile = new File(sFileShp);

			if (!oFile.exists()) {
				return;
			}

			String sLayerId = oFile.getName().substring(0, oFile.getName().length()-4);
			//String sShpStore = sLayerId;
			String sShpStore = "omirl_shp";

			String sGeoServerDataDir = "/var/lib/tomcat6/webapps/geoserver/data";
			//sGeoServerDataDir = "C:\\Program Files (x86)\\GeoServer 2.3.2\\data_dir\\data";
			String sDestinationFileFolder = sGeoServerDataDir + "/" + "omirl"; //sLayerId

			File oGeoServerDataDir = new File(sDestinationFileFolder);

			FileUtils.copyDirectory(oFullPathDir, oGeoServerDataDir);

			String sFile = sDestinationFileFolder + "/" + sLayerId + ".shp";

			System.out.println("FILE: " + sFile);

			GeoServerDataManager2 oGeoManager = new GeoServerDataManager2("http://127.0.0.1:8080/geoserver/", "", "admin", "geo4Omirl");

			System.out.println("GeoServerDataManager2 creato ");

			oGeoManager.addShapeLayer(sLayerId, "OMIRL",sFile, "polygon", sShpStore);

			System.out.println("Finita PUBLISH MAPS");

		}
		catch(Exception oEx) {
			System.out.println("publishMaps Exception " + oEx.toString());
		}		
	}

	private void summaryTable() {
		try {

			System.out.println("Summary Table Start");

			Date oActualDate = new Date();

			SummaryInfo oSummaryInfo = new SummaryInfo();

			StationDataRepository oStationDataRepository = new StationDataRepository();

			// trova il max e min temperatura di oggi x provincia
			SummaryInfoEntity oGeMax = oStationDataRepository.getDistrictMaxTemperatureSummaryInfo("GE", oActualDate);
			DistrictSummaryInfo oDistrictSummaryGe = new DistrictSummaryInfo();
			oDistrictSummaryGe.setDescription("Genova");

			if (oGeMax != null)
			{
				oDistrictSummaryGe.setMax(oGeMax.getValue());
				oDistrictSummaryGe.setStationMax(oGeMax.getStationName());
				oDistrictSummaryGe.setRefDateMax(oGeMax.getReferenceDate());
			}
			else 
			{
				oDistrictSummaryGe.setMax(-9999.0);
				oDistrictSummaryGe.setStationMax("N.D.");
				oDistrictSummaryGe.setRefDateMax(null);
			}

			SummaryInfoEntity oGeMin = oStationDataRepository.getDistrictMinTemperatureSummaryInfo("GE", oActualDate);

			if (oGeMin != null)
			{
				oDistrictSummaryGe.setMin(oGeMin.getValue());
				oDistrictSummaryGe.setStationMin(oGeMin.getStationName());
				oDistrictSummaryGe.setRefDateMin(oGeMin.getReferenceDate());
			}
			else 
			{
				oDistrictSummaryGe.setMin(-9999.0);
				oDistrictSummaryGe.setStationMin("N.D.");
				oDistrictSummaryGe.setRefDateMin(null);
			}

			oSummaryInfo.getDistrictInfo().add(oDistrictSummaryGe);
			


			SummaryInfoEntity oSvMax = oStationDataRepository.getDistrictMaxTemperatureSummaryInfo("SV", oActualDate);
			DistrictSummaryInfo oDistrictSummarySv = new DistrictSummaryInfo();
			oDistrictSummarySv.setDescription("Savona");

			if (oSvMax != null)
			{
				oDistrictSummarySv.setMax(oSvMax.getValue());
				oDistrictSummarySv.setStationMax(oSvMax.getStationName());	
				oDistrictSummarySv.setRefDateMax(oSvMax.getReferenceDate());	
			}
			else 
			{
				oDistrictSummarySv.setMax(-9999.0);
				oDistrictSummarySv.setStationMax("N.D.");
				oDistrictSummarySv.setRefDateMax(null);
			}

			SummaryInfoEntity oSvMin = oStationDataRepository.getDistrictMinTemperatureSummaryInfo("SV", oActualDate);

			if (oSvMin != null)
			{
				oDistrictSummarySv.setMin(oSvMin.getValue());
				oDistrictSummarySv.setStationMin(oSvMin.getStationName());
				oDistrictSummarySv.setRefDateMin(oSvMin.getReferenceDate());
			}
			else 
			{
				oDistrictSummarySv.setMin(-9999.0);
				oDistrictSummarySv.setStationMin("N.D.");	
				oDistrictSummarySv.setRefDateMin(null);
			}

			oSummaryInfo.getDistrictInfo().add(oDistrictSummarySv);



			SummaryInfoEntity oImMax = oStationDataRepository.getDistrictMaxTemperatureSummaryInfo("IM", oActualDate);
			DistrictSummaryInfo oDistrictSummaryIm = new DistrictSummaryInfo();
			oDistrictSummaryIm.setDescription("Imperia");

			if (oImMax != null)
			{
				oDistrictSummaryIm.setMax(oImMax.getValue());
				oDistrictSummaryIm.setStationMax(oImMax.getStationName());
				oDistrictSummaryIm.setRefDateMax(oImMax.getReferenceDate());
			}
			else 
			{
				oDistrictSummaryIm.setMax(-9999.0);
				oDistrictSummaryIm.setStationMax("N.D.");
				oDistrictSummaryIm.setRefDateMax(null);
			}

			SummaryInfoEntity oImMin = oStationDataRepository.getDistrictMinTemperatureSummaryInfo("IM", oActualDate);

			if (oImMin != null)
			{
				oDistrictSummaryIm.setMin(oImMin.getValue());
				oDistrictSummaryIm.setStationMin(oImMin.getStationName());
				oDistrictSummaryIm.setRefDateMin(oImMin.getReferenceDate());
			}
			else 
			{
				oDistrictSummaryIm.setMin(-9999.0);
				oDistrictSummaryIm.setStationMin("N.D.");
				oDistrictSummaryIm.setRefDateMin(null);
			}

			oSummaryInfo.getDistrictInfo().add(oDistrictSummaryIm);



			SummaryInfoEntity oSpMax = oStationDataRepository.getDistrictMaxTemperatureSummaryInfo("SP", oActualDate);
			DistrictSummaryInfo oDistrictSummarySp = new DistrictSummaryInfo();
			oDistrictSummarySp.setDescription("Spezia");

			if (oSpMax!=null)
			{
				oDistrictSummarySp.setMax(oSpMax.getValue());
				oDistrictSummarySp.setStationMax(oSpMax.getStationName());
				oDistrictSummarySp.setRefDateMax(oSpMax.getReferenceDate());
			}
			else 
			{				
				oDistrictSummarySp.setMax(-9999.0);
				oDistrictSummarySp.setStationMax("N.D.");
				oDistrictSummarySp.setRefDateMax(null);
			}

			SummaryInfoEntity oSpMin = oStationDataRepository.getDistrictMinTemperatureSummaryInfo("SP", oActualDate);

			if (oSpMin!=null)
			{
				oDistrictSummarySp.setMin(oSpMin.getValue());
				oDistrictSummarySp.setStationMin(oSpMin.getStationName());	
				oDistrictSummarySp.setRefDateMin(oSpMin.getReferenceDate());
			}
			else
			{
				oDistrictSummarySp.setMin(-9999.0);
				oDistrictSummarySp.setStationMin("N.D.");
				oDistrictSummarySp.setRefDateMin(null);
			}

			oSummaryInfo.getDistrictInfo().add(oDistrictSummarySp);

			// trova il max e min temperatura di oggi x zona allertamento
			AlertZoneSummaryInfo oZoneASummary = new AlertZoneSummaryInfo();
			oZoneASummary.setDescription("A");
			oZoneASummary.setMax(0);
			oZoneASummary.setMin(0);
			oZoneASummary.setStationMax("ND");
			oZoneASummary.setStationMin("ND");

			oSummaryInfo.getAlertInfo().add(oZoneASummary);

			AlertZoneSummaryInfo oZoneBSummary = new AlertZoneSummaryInfo();
			oZoneBSummary.setDescription("B");
			oZoneBSummary.setMax(0);
			oZoneBSummary.setMin(0);
			oZoneBSummary.setStationMax("ND");
			oZoneBSummary.setStationMin("ND");

			oSummaryInfo.getAlertInfo().add(oZoneBSummary);

			AlertZoneSummaryInfo oZoneCSummary = new AlertZoneSummaryInfo();
			oZoneCSummary.setDescription("C");
			oZoneCSummary.setMax(0);
			oZoneCSummary.setMin(0);
			oZoneCSummary.setStationMax("ND");
			oZoneCSummary.setStationMin("ND");

			oSummaryInfo.getAlertInfo().add(oZoneCSummary);

			AlertZoneSummaryInfo oZoneDSummary = new AlertZoneSummaryInfo();
			oZoneDSummary.setDescription("D");
			oZoneDSummary.setMax(0);
			oZoneDSummary.setMin(0);
			oZoneDSummary.setStationMax("ND");
			oZoneDSummary.setStationMin("ND");

			oSummaryInfo.getAlertInfo().add(oZoneDSummary);

			AlertZoneSummaryInfo oZoneESummary = new AlertZoneSummaryInfo();
			oZoneESummary.setDescription("E");
			oZoneESummary.setMax(0);
			oZoneESummary.setMin(0);
			oZoneESummary.setStationMax("ND");
			oZoneESummary.setStationMin("ND");

			oSummaryInfo.getAlertInfo().add(oZoneESummary);


			// Trova il max del vento e raffica di oggi per le stazioni che sono in configurazione.
			String sCostalCodes = "";
			for (int iCodes=0; iCodes<m_oConfig.getWindSummaryInfo().getCostalCodes().size(); iCodes++) {
				sCostalCodes += "'" + m_oConfig.getWindSummaryInfo().getCostalCodes().get(iCodes) + "'";
				if (iCodes != m_oConfig.getWindSummaryInfo().getCostalCodes().size()-1) sCostalCodes += ", ";
			}

			SummaryInfoEntity oCostalWind = oStationDataRepository.getWindMaxSummaryInfo(sCostalCodes, oActualDate);
			SummaryInfoEntity oCostalGust = oStationDataRepository.getWindGustSummaryInfo(sCostalCodes, oActualDate);

			WindSummaryInfo oCostalSummaryInfo = new WindSummaryInfo();
			oCostalSummaryInfo.setDescription("Costa");

			if (oCostalWind!=null)
			{
				oCostalSummaryInfo.setMax(oCostalWind.getValue());
				oCostalSummaryInfo.setStationMax(oCostalWind.getStationName());
				oCostalSummaryInfo.setRefDateWind(oCostalWind.getReferenceDate());
			}
			else
			{
				oCostalSummaryInfo.setMax(-9999.0);
				oCostalSummaryInfo.setStationMax("N.D.");
				oCostalSummaryInfo.setRefDateWind(null);
			}

			if (oCostalGust != null)
			{
				oCostalSummaryInfo.setGust(oCostalGust.getValue());
				oCostalSummaryInfo.setStationGust(oCostalGust.getStationName());
				oCostalSummaryInfo.setRefDateGust(oCostalGust.getReferenceDate());
			}
			else 
			{
				oCostalSummaryInfo.setGust(-9999.0);
				oCostalSummaryInfo.setStationGust("N.D.");
				oCostalSummaryInfo.setRefDateGust(null);
			}

			oSummaryInfo.getWindInfo().add(oCostalSummaryInfo);


			String sInternalCodes = "";
			for (int iCodes=0; iCodes<m_oConfig.getWindSummaryInfo().getInternalCodes().size(); iCodes++) {
				sInternalCodes += "'" + m_oConfig.getWindSummaryInfo().getInternalCodes().get(iCodes) + "'";
				if (iCodes != m_oConfig.getWindSummaryInfo().getInternalCodes().size()-1) sInternalCodes += ", ";
			}

			SummaryInfoEntity oInternalWind = oStationDataRepository.getWindMaxSummaryInfo(sInternalCodes, oActualDate);
			SummaryInfoEntity oInternalGust = oStationDataRepository.getWindGustSummaryInfo(sInternalCodes, oActualDate);

			WindSummaryInfo oInternalSummaryInfo = new WindSummaryInfo();
			oInternalSummaryInfo.setDescription("Rilievi");
			if (oInternalWind != null)
			{
				oInternalSummaryInfo.setMax(oInternalWind.getValue());
				oInternalSummaryInfo.setStationMax(oInternalWind.getStationName());
				oInternalSummaryInfo.setRefDateWind(oInternalWind.getReferenceDate());
			}
			else
			{
				oInternalSummaryInfo.setMax(-9999.0);
				oInternalSummaryInfo.setStationMax("N.D.");				
				oInternalSummaryInfo.setRefDateWind(null);
			}

			if (oInternalGust!=null)
			{
				oInternalSummaryInfo.setGust(oInternalGust.getValue());
				oInternalSummaryInfo.setStationGust(oInternalGust.getStationName());
				oInternalSummaryInfo.setRefDateGust(oInternalGust.getReferenceDate());
			}
			else
			{
				oInternalSummaryInfo.setGust(-9999.0);
				oInternalSummaryInfo.setStationGust("N.D.");
				oInternalSummaryInfo.setRefDateGust(null);
			}

			oSummaryInfo.getWindInfo().add(oInternalSummaryInfo);

			String sBasePath = m_oConfig.getFileRepositoryPath();

			String sOutputPath = sBasePath + "/tables/summary";

			SimpleDateFormat oDateFormat = new SimpleDateFormat("yyyy/MM/dd");

			String sFullDir = sOutputPath + "/" + oDateFormat.format(oActualDate);

			File oOutPath = new File(sFullDir);
			if (oOutPath.exists() == false) oOutPath.mkdirs();

			String sOutputFile = sFullDir + "/summary.xml"; 

			SerializationUtils.serializeObjectToXML(sOutputFile, oSummaryInfo);

			System.out.println("Summary Table End");
		}
		catch(Exception oEx) {
			System.out.println("summaryTable Exception " + oEx.toString());
			oEx.printStackTrace();
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
		long lInterval = ((long)iDays) * 24L * 60L * 60L * 1000L;

		// Create now and get ms
		Date oNow = new Date();
		long lNow = oNow.getTime();

		// Compute start date
		long lStartDate = lNow - lInterval;

		DateTimeZone.setDefault(DateTimeZone.forID("Europe/Rome"));

		// Create date
		DateTime oRetDate = new DateTime(lStartDate);
		LocalDateTime oLocalRetDate = new LocalDateTime(lStartDate);
		//DateTime oRetDate = new LocalDateTime(lStartDate).toDateTime();  

		// If is fixed
		if (bFixedWindow) {
			// Starts at 00:00
			oLocalRetDate = oLocalRetDate.withHourOfDay(0);
			oLocalRetDate = oLocalRetDate.withMinuteOfHour(0);
			oLocalRetDate = oLocalRetDate.withSecondOfMinute(0);
			oLocalRetDate = oLocalRetDate.withMillisOfSecond(0);

			oRetDate = oRetDate.withHourOfDay(0);
			oRetDate = oRetDate.withMinuteOfHour(0);
			oRetDate = oRetDate.withSecondOfMinute(0);
			oRetDate = oRetDate.withMillisOfSecond(0);
		}

		if (bFixedWindow) return oRetDate.toDate();
		else return oLocalRetDate.toDate();
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
	 * Get the time step in minutes for the given variable (column) in the actual station 
	 * @param sColumnName
	 * @param oStationAnag
	 * @return
	 */
	int GetMinutesStep(String sColumnName, StationAnag oStationAnag)
	{
		int iMinutesEvery = 5;

		String sUpperCaseColumn = sColumnName.substring(0,1).toUpperCase() + sColumnName.substring(1);

		String sMethodName = "get" + sUpperCaseColumn + "_every";

		java.lang.reflect.Method oGetMinutesMethod;

		try {
			oGetMinutesMethod = oStationAnag.getClass().getMethod(sMethodName);
			iMinutesEvery = (int) oGetMinutesMethod.invoke(oStationAnag);
		} 
		catch (Exception oEx) {
			System.out.println("Exception trying to read data frequency. Method Name = " + sMethodName);
			oEx.printStackTrace();
		}

		return iMinutesEvery;
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

		if (aoInfo == null) {
			// TODO
			System.out.println("SaveStandardChart aoInfo is null");
			return oDataChart;
		}
		if (aoInfo.size()==0) {
			System.out.println("SaveStandardChart aoInfo is null");
			return oDataChart;
		}

		oDataSerie.setType(aoInfo.get(0).getType());
		List<DataSeriePoint> aoPoints = null;

		String sColumnName = aoInfo.get(0).getColumnName();

		int iMinuteTimeStep = GetMinutesStep(sColumnName,oStationAnag);

		if (bHourlyStep) {
			aoPoints = oStationDataRepository.getHourlyDataSerie(oStationAnag.getStation_code(), sColumnName, oChartsStartDate);
			iMinuteTimeStep = 60;
		}
		else {
			aoPoints = oStationDataRepository.getDataSerie(oStationAnag.getStation_code(), sColumnName, oChartsStartDate);
		}

		DataSeriePointToDataSerie(aoPoints,oDataSerie, aoInfo.get(0).getConversionFactor(),iMinuteTimeStep);

		oDataSerie.setName(aoInfo.get(0).getName());

		if (aoInfo.get(0).getDashStyle() != null) {
			oDataSerie.setDashStyle(aoInfo.get(0).getDashStyle());
		}

		if (aoInfo.get(0).getColor() != null) {
			oDataSerie.setColor(aoInfo.get(0).getColor());
		}

		if (aoInfo.get(0).getLineWidth()>0) oDataSerie.setLineWidth(aoInfo.get(0).getLineWidth());

		// Autorange if the serie exceeds default limits
		double dYMax = aoInfo.get(0).getAxisYMaxValue();
		double dYMin = aoInfo.get(0).getAxisYMinValue();


		if (aoPoints != null)
		{
			// Check all the points
			for (int iPoints = 0; iPoints<aoPoints.size(); iPoints++) {
				if (aoPoints.get(iPoints)!=null) {
					// Is over the top?
					if (aoPoints.get(iPoints).getVal()>dYMax)
					{
						// Take the new Max
						dYMax = aoPoints.get(iPoints).getVal();
					}
					
					if (aoPoints.get(iPoints).getVal()<dYMin)
					{
						dYMin = aoPoints.get(iPoints).getVal();
					}
				}
			}

			// Check if max was changed
			if (dYMax>aoInfo.get(0).getAxisYMaxValue()) {

				// Ok, round it
				if (dYMax > 0)
					dYMax *= 1.1;
				else
					dYMax *= 0.9;
				try{
					System.out.println("Chart Max value autorange for " + aoInfo.get(0).getSensorType() + " in " + oStationAnag.getName());
				}
				catch(Exception oEx) {
					oEx.printStackTrace();
				}
			}

			// Check if max was changed
			if (dYMin<aoInfo.get(0).getAxisYMinValue()) {

				// Ok, round it
				if (dYMin > 0)
					dYMin *= 0.9;
				else
					dYMin *= 1.1;
				try{
					System.out.println("Chart Min value autorange for " + aoInfo.get(0).getSensorType() + " in " + oStationAnag.getName());
				}
				catch(Exception oEx) {
					oEx.printStackTrace();
				}
			}

		}
		oDataChart.getDataSeries().add(oDataSerie);
		oDataChart.setTitle(oStationAnag.getMunicipality() + " - " + oStationAnag.getName());
		oDataChart.setSubTitle(aoInfo.get(0).getSubtitle());
		oDataChart.setAxisYMaxValue(dYMax);
		oDataChart.setAxisYMinValue(dYMin);
		oDataChart.setAxisYTickInterval(aoInfo.get(0).getAxisYTickInterval());
		oDataChart.setAxisYTitle(aoInfo.get(0).getAxisYTitle());
		oDataChart.setTooltipValueSuffix(aoInfo.get(0).getTooltipValueSuffix());

		oDataChart.getOtherChart().addAll(asOtherLinks);
		if (bSave) serializeStationChart(oDataChart,m_oConfig, oStationAnag.getStation_code(), aoInfo.get(0).getFolderName(), m_oDateFormat);

		return oDataChart;

	}

	/**
	 * Checks and calulate autorange for cumulated data serie
	 * @param oCumulatedSerie
	 * @param oAdditionalAxis
	 */
	private void CheckCumulateAutorange(DataSerie oCumulatedSerie, ChartAxis oAdditionalAxis)
	{
		// Check for autorange on max exceed
		if (oCumulatedSerie.getData()!=null)
		{
			double dYMax = oAdditionalAxis.getAxisYMaxValue();

			for (int iPoints=oCumulatedSerie.getData().size()-1; iPoints>0; iPoints--){
				Object [] aoPoint = oCumulatedSerie.getData().get(iPoints);

				if (aoPoint.length>1)
				{
					if (aoPoint[1]!=null) {
						Double dValue = (Double) aoPoint[1];
						if (dValue>dYMax) {
							dYMax = dValue*1.1;
							oAdditionalAxis.setAxisYMaxValue(dYMax);
						}

						break;
					}
				}
			}			
		}
	}

	/**
	 * Peforms tasks that the Daemon has to do daily
	 */
	private void DailyTask() {

		System.out.println("OmirlDaemon - DailyTask");

		try {
			ClearThread oThread = new ClearThread(m_oConfig.getFileRepositoryPath());
			oThread.run();			
		}
		catch(Exception oEx) {
			System.out.println("OmirlDaemon - Clear Daemon Exception");
			oEx.printStackTrace();
		}

		RefreshConfiguration();

		RefreshThresholds();

		RefreshStationTables();
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
				
				if (aoStations==null) continue;

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

		oVM.setArea(oStationAnag.getWarn_area());
		oVM.setBasin(oStationAnag.getBasin());
		oVM.setSubBasin(oStationAnag.getRiver());
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
			System.out.println("OmirlDaemon - Reading Configuration File " + m_sConfigurationFile);
			m_oConfig = (OmirlDaemonConfiguration) SerializationUtils.deserializeXMLToObject(m_sConfigurationFile);
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
	public void DataSeriePointToDataSerie(List<DataSeriePoint> aoPoints, DataSerie oDataSerie, int iMinutesStep) {
		DataSeriePointToDataSerie(aoPoints, oDataSerie, 1.0, iMinutesStep);
	}

	/**
	 * Converts Points from db to points for xml exchange format
	 * @param aoPoints
	 * @param oDataSerie
	 * @param dConversionFactor
	 */
	public void DataSeriePointToDataSerie(List<DataSeriePoint> aoPoints, DataSerie oDataSerie, double dConversionFactor, int iMinutesStep) {

		try {
			if (aoPoints != null) {

				if (aoPoints.size()>0)
				{			
					DateTime oNow = new DateTime();
					long lTimeStep = iMinutesStep*60L*1000L;
					long lNow = oNow.getMillis();

					long lStart = aoPoints.get(0).getRefDate().getTime();

					int iPointIndex = 0;

					for (long lTimeCycle = lStart; lTimeCycle<=lNow; lTimeCycle+=lTimeStep)
					{
						Object [] adPoint = new Object[2];
						adPoint[0] = new Long(lTimeCycle);

						if (iPointIndex<aoPoints.size())
						{
							DataSeriePoint oDataSeriePoint = aoPoints.get(iPointIndex);

							if (oDataSeriePoint.getRefDate().getTime() == lTimeCycle) {
								adPoint[1] = new Double(oDataSeriePoint.getVal())*dConversionFactor;
								iPointIndex++;
							}
							else if (oDataSeriePoint.getRefDate().getTime() < lTimeCycle)
							{
								iPointIndex++;
								lTimeCycle-=lTimeStep;
								continue;
							}
							else {
								adPoint[1] = null;
							}
						}

						oDataSerie.getData().add(adPoint);

					}
				}

				/*
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
				 */



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

	public Date GetChartStartDate(Date oChartsStartDate, List<ChartInfo> aoInfo) {
		// Initialize Start Date and Fixed
		Date oStartDate = oChartsStartDate;
		boolean bFixed = false;

		// If I have Info
		if (aoInfo!=null) {
			if (aoInfo.size()>0) {
				// Take info from config
				if (aoInfo.get(0).getDaysLength() >0) {
					bFixed = aoInfo.get(0).isHasFixedWindow();
					oStartDate = GetChartStartDate(aoInfo.get(0).getDaysLength(),bFixed);
				}
			}
		}

		return oStartDate;
	}


	public void FillCumulatedSerie(DataSerie oDataSerie, DataSerie oCumulatedSerie) {

		Double oLastCumulated = new Double(0.0);

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
				adPoint[1] = new Double(0.0);
			}
			else 
			{
				// Sum to the previos value

				if (oHistoPoint[1]!=null){
					adPoint[1] = new Double((oLastCumulated + (Double)oHistoPoint[1]));
					oLastCumulated = (Double) adPoint[1];
				}
				else {
					adPoint[1] = null;
				}

			}

			// Add the point to the serie
			oCumulatedSerie.getData().add(adPoint);
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
	public void serializeStationChart(DataChart oChart, OmirlDaemonConfiguration oConfig, String sStationCode, String sChartName, DateFormat oDateFormat) {
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
		OmirlDaemonConfiguration oConfig = new OmirlDaemonConfiguration();
		oConfig.setFileRepositoryPath("C:\\temp\\Omirl\\Files");
		oConfig.setMinutesPolling(2);
		oConfig.setChartTimeRangeDays(16);
		oConfig.setSflocTimeRangeDays(5);
		oConfig.setSessioneTimeout(30);

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

		//DynamicLayerInfo oMapInfo = new DynamicLayerInfo();

		WindSummaryConfiguration oWindInfo = new WindSummaryConfiguration();
		oWindInfo.getInternalCodes().add("PFEAR");
		oWindInfo.getInternalCodes().add("MSETT");
		oWindInfo.getInternalCodes().add("CCADB");
		oWindInfo.getInternalCodes().add("PTURC");
		oWindInfo.getInternalCodes().add("MOCAP");
		oWindInfo.getInternalCodes().add("SALBE");
		oWindInfo.getInternalCodes().add("GIACO");
		oWindInfo.getInternalCodes().add("CASON");
		oWindInfo.getInternalCodes().add("TAGLT");

		oWindInfo.getCostalCodes().add("SREMO");
		oWindInfo.getCostalCodes().add("IMPER");
		oWindInfo.getCostalCodes().add("MMAUR");
		oWindInfo.getCostalCodes().add("INASV");
		oWindInfo.getCostalCodes().add("GEPVA");
		oWindInfo.getCostalCodes().add("FFRES");
		oWindInfo.getCostalCodes().add("MTPOR");
		oWindInfo.getCostalCodes().add("CRNLO");
		oWindInfo.getCostalCodes().add("SPZIA");
		oWindInfo.getCostalCodes().add("MROCC");
		oWindInfo.getCostalCodes().add("LUNIS");
		oWindInfo.getCostalCodes().add("FRAMU");
		oWindInfo.getCostalCodes().add("LEVAN");

		oConfig.setWindSummaryInfo(oWindInfo);


		try {
			SerializationUtils.serializeObjectToXML("C:\\temp\\Omirl\\OmirlDaemonConfigSAMPLE.xml", oConfig);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void Test() {
		try {


			StationDataRepository oStationDataRepositorySum = new StationDataRepository();
			SummaryInfoEntity oSummaryTest = oStationDataRepositorySum.getDistrictMaxTemperatureSummaryInfo("GE", new Date());
			SummaryInfoEntity oSummaryMin =  oStationDataRepositorySum.getDistrictMinTemperatureSummaryInfo("GE", new Date());


			Date oNow = new Date();
			long lTime = oNow.getTime() - 1000*60*60*24*30;
			Date oStartDate = new  Date(lTime);

			StationDataRepository oStationDataRepository = new StationDataRepository();
			oStationDataRepository.getDailyDataSerie("TAVRN", "rain_24h", oStartDate);

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


	public static File lastFileModified(String dir) {
		File oDir = new File(dir);

		if (!oDir.exists()) {
			System.out.println("OMIRL.lastFileModified: folder does not exists " + dir);
			return null;
		}

		File[] aoFiles = oDir.listFiles(new FileFilter() {			
			public boolean accept(File file) {
				return file.isFile();
			}
		});

		long liLastMod = Long.MIN_VALUE;

		File oChoise = null;
		for (File file : aoFiles) {
			if (file.lastModified() > liLastMod) {
				oChoise = file;
				liLastMod = file.lastModified();
			}
		}

		return oChoise;
	}
}
