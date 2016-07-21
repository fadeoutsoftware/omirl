package it.fadeout.omirl.daemon;

import it.fadeout.omirl.business.AnagTableInfo;
import it.fadeout.omirl.business.ChartAxis;
import it.fadeout.omirl.business.ChartInfo;
import it.fadeout.omirl.business.ChartLine;
import it.fadeout.omirl.business.CreekThreshold;
import it.fadeout.omirl.business.DataChart;
import it.fadeout.omirl.business.DataSerie;
import it.fadeout.omirl.business.DataSeriePoint;
import it.fadeout.omirl.business.HydroModelTables;
import it.fadeout.omirl.business.MapInfo;
import it.fadeout.omirl.business.MaxHydroAlertZone;
import it.fadeout.omirl.business.MaxTableInfo;
import it.fadeout.omirl.business.MaxTableRow;
import it.fadeout.omirl.business.ModelGalleryInfo;
import it.fadeout.omirl.business.ModelImageInfo;
import it.fadeout.omirl.business.ModelTable;
import it.fadeout.omirl.business.SectionAnag;
import it.fadeout.omirl.business.SectionBasins;
import it.fadeout.omirl.business.SectionBasinsCodes;
import it.fadeout.omirl.business.SectionLayerInfo;
import it.fadeout.omirl.business.SensorLastData;
import it.fadeout.omirl.business.Sfloc;
import it.fadeout.omirl.business.StationAnag;
import it.fadeout.omirl.business.StationLastData;
import it.fadeout.omirl.business.SummaryInfoEntity;
import it.fadeout.omirl.business.WindDataSeriePoint;
import it.fadeout.omirl.business.WindSummaryConfiguration;
import it.fadeout.omirl.geoserver.GeoServerDataManager2;
import it.fadeout.omirl.data.CreekThresholdRepository;
import it.fadeout.omirl.data.HibernateUtils;
import it.fadeout.omirl.data.OpenSessionRepository;
import it.fadeout.omirl.data.Repository;
import it.fadeout.omirl.data.SavedPeriodRepository;
import it.fadeout.omirl.data.SectionAnagRepository;
import it.fadeout.omirl.data.SflocRepository;
import it.fadeout.omirl.data.StationAnagRepository;
import it.fadeout.omirl.data.StationDataRepository;
import it.fadeout.omirl.data.StationLastDataRepository;
import it.fadeout.omirl.viewmodels.AlertZoneSummaryInfo;
import it.fadeout.omirl.viewmodels.DistrictSummaryInfo;
import it.fadeout.omirl.viewmodels.MapInfoViewModel;
import it.fadeout.omirl.viewmodels.MaxHydroAlertZoneRowViewModel;
import it.fadeout.omirl.viewmodels.MaxHydroAlertZoneViewModel;
import it.fadeout.omirl.viewmodels.MaxTableRowViewModel;
import it.fadeout.omirl.viewmodels.MaxTableViewModel;
import it.fadeout.omirl.viewmodels.ModelGallery;
import it.fadeout.omirl.viewmodels.ModelImage;
import it.fadeout.omirl.viewmodels.SectionBasinsCodesViewModel;
import it.fadeout.omirl.viewmodels.SectionBasinsViewModel;
import it.fadeout.omirl.viewmodels.SectionViewModel;
import it.fadeout.omirl.viewmodels.SensorListTableRowViewModel;
import it.fadeout.omirl.viewmodels.SensorListTableViewModel;
import it.fadeout.omirl.viewmodels.SensorValueRowViewModel;
import it.fadeout.omirl.viewmodels.SensorValueTableViewModel;
import it.fadeout.omirl.viewmodels.SensorViewModel;
import it.fadeout.omirl.viewmodels.SummaryInfo;
import it.fadeout.omirl.viewmodels.WindSummaryInfo;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.io.FileUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;

import sun.dc.path.PathException;

import com.sun.org.apache.xpath.internal.operations.Bool;

public class OmirlDaemon {

	HashMap<String, CreekThreshold> m_aoThresholds = new HashMap<>();
	private OmirlDaemonConfiguration m_oConfig;
	private String m_sConfigurationFile = "";

	// Date Format for File Serialization
	SimpleDateFormat m_oDateFormat = new SimpleDateFormat("HHmm");

	List<StationAnag> m_aoAllStations;

	SensorValueTableViewModel m_oRainValuesTable = new SensorValueTableViewModel();
	SensorValueTableViewModel m_oTempValuesTable = new SensorValueTableViewModel();
	SensorValueTableViewModel m_oHydroValuesTable = new SensorValueTableViewModel();
	SensorValueTableViewModel m_oIgroValuesTable = new SensorValueTableViewModel();
	SensorValueTableViewModel m_oRadioValuesTable = new SensorValueTableViewModel();
	SensorValueTableViewModel m_oLeafsValuesTable = new SensorValueTableViewModel();
	SensorValueTableViewModel m_oBattValuesTable = new SensorValueTableViewModel();
	SensorValueTableViewModel m_oPressValuesTable = new SensorValueTableViewModel();
	SensorValueTableViewModel m_oSnowValuesTable = new SensorValueTableViewModel();
	SensorValueTableViewModel m_oBoaValuesTable = new SensorValueTableViewModel();
	SensorValueTableViewModel m_oWindValuesTable = new SensorValueTableViewModel();


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
		//TestGeoTiff();

		//WriteSampleConfig();

		OmirlDaemon oDaemon = new OmirlDaemon();
		oDaemon.OmirlDaemonCycle(args[0]);
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

		//----------------TEST------------------
		//publishMaps();
		//maxTable();
		//RefreshSectionsLayer();
		//DailyTask();
		//RefreshGallery();
		//if (true) return;
		//RefreshHydroModel();
		//maxHydroAlertZones();
		//SerializeWebCamLayer();
		//----------------TEST------------------


		InitSensorValueTables();

		RefreshSectionsLayer();

		Date oLastDate = null;

		long lReferenceDate = -1; 
		//lReferenceDate = new Date().getTime();

		try {

			// Cycle Forever!
			while (true) {

				Date oActualDate = new Date();

				// Start 
				System.out.println("OmirlDaemon - Cycle Start " + oActualDate);	

				if (DayChanged(oActualDate, oLastDate)) {

					if (oLastDate == null)
					{
						oLastDate= new Date(oActualDate.getTime());
						oLastDate.setHours(0);
						oLastDate.setMinutes(0);
						oLastDate.setSeconds(0);
					}
					else 
					{
						oLastDate=oActualDate;
					}


					if (m_oConfig.isEnableDailyTask())
					{
						DailyTask();
					}

					ClearSensorValueTables();
				}

				try {

					System.out.println("OmirlDaemon - Starting Charts Cycle");

					// CHARTS ***********************************************************************
					StationAnagRepository oStationAnagRepository = new StationAnagRepository();
					StationDataRepository oStationDataRepository = new StationDataRepository();

					// Get Start Date Time Filter
					long lNowTime = new Date().getTime();

					long lInterval = m_oConfig.chartTimeRangeDays * 24 * 60 * 60 * 1000;
					Date oChartsStartDate = new Date(lNowTime-lInterval);				

					// Get all the stations
					m_aoAllStations = oStationAnagRepository.SelectAll(StationAnag.class);
					//ArrayList<StationAnag> aoAllStations = new ArrayList<>();
					//aoAllStations.add(oStationAnagRepository.selectByStationCode("PCERR"));

					// For Each
					for (StationAnag oStationAnag : m_aoAllStations) {

						//System.out.println("OmirDaemon - Station: " + oStationAnag.getStation_code());
						System.out.print(".");

						//if (oStationAnag.getStation_code().equals("CFUNZ")==false) continue;

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
						if (oStationAnag.getMean_wave_height_every() !=null) asOtherLinks.add("Boa");
						if (oStationAnag.getMean_snow_depth_every() != null) asOtherLinks.add("Neve");
						//if (oStationAnag.get != null) asOtherLinks.add("humidity");


						if (m_oConfig.isEnableCharts())
						{

							try {

								// --------------------------------------------------------RAIN CHART
								if (oStationAnag.getRain_01h_every() != null) {

									List<ChartInfo> aoInfo = getChartInfoFromSensorCode("Pluvio");

									if (RefreshChart(aoInfo, lReferenceDate, oStationAnag.getStation_code()))
									{

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
									}
								}
							}
							catch(Exception oChartEx) {
								System.out.println("OmirDaemon - Station: " + oStationAnag.getStation_code());
								oChartEx.printStackTrace();
							}

							// --------------------------------------------------------RAIN 7gg CHART
							try {
								if (asOtherLinks.contains("Pluvio7")) {

									List<ChartInfo> aoInfo = getChartInfoFromSensorCode("Pluvio7");

									if (RefreshChart(aoInfo, lReferenceDate, oStationAnag.getStation_code()))
									{
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

							}
							catch(Exception oChartEx) {
								System.out.println("OmirDaemon - Station: " + oStationAnag.getStation_code());
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

										if (RefreshChart(aoInfo, lReferenceDate, oStationAnag.getStation_code()))
										{

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

							}
							catch(Exception oChartEx) {
								System.out.println("OmirDaemon - Station: " + oStationAnag.getStation_code());
								oChartEx.printStackTrace();
							}

							// --------------------------------------------------------RAIN 30gg CHART
							try {

								String sNativeColumn = "";


								if (asOtherLinks.contains("Pluvio30")) {
									List<ChartInfo> aoInfo = getChartInfoFromSensorCode("Pluvio30");

									if (RefreshChart(aoInfo, lReferenceDate, oStationAnag.getStation_code()))
									{

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
										DateFormat oFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
										oDataChart.setSubTitle(aoInfo.get(0).getSubtitle() + " - " + oFormat.format(new Date()));
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

							}
							catch(Exception oChartEx) {
								System.out.println("OmirDaemon - Station: " + oStationAnag.getStation_code());
								oChartEx.printStackTrace();
							}			


							try {
								// --------------------------------------------------------TEMPERATURE CHART
								if (oStationAnag.getMean_air_temp_every() != null) {

									List<ChartInfo> aoInfo = getChartInfoFromSensorCode("Termo");

									if (RefreshChart(aoInfo, lReferenceDate, oStationAnag.getStation_code()))
									{
										// Initialize Start Date
										Date oStartDate = GetChartStartDate(oChartsStartDate, aoInfo);

										DataChart oTermoChart = SaveStandardChart(aoInfo,oStationAnag,asOtherLinks,oStationDataRepository,oStartDate);

										if (aoInfo.size() > 1) {
											if (oStationAnag.getMin_air_temp_every() != null && oStationAnag.getMax_air_temp_every() != null)
											{
												ChartInfo oMinInfo = aoInfo.get(1);

												DataSerie oMinSerie = new DataSerie();
												// Get Data from the Db: for rain1h only hourly rate
												List<DataSeriePoint> aoPoints = oStationDataRepository.getDataSerie(oStationAnag.getStation_code(), oMinInfo.getColumnName(), oStartDate);

												int iMinuteTimeStep = GetMinutesStep(oMinInfo.getColumnName(),oStationAnag);
												// Convert points to Data Serie
												DataSeriePointToDataSerie(aoPoints,oMinSerie, oMinInfo.getConversionFactor(), iMinuteTimeStep);
												// Set Serie Name
												oMinSerie.setName(oMinInfo.getName());
												// Main Axis Reference
												oMinSerie.setAxisId(0);

												if (aoInfo.get(1).getDashStyle() != null) {
													oMinSerie.setDashStyle(oMinInfo.getDashStyle());
												}

												if (oMinInfo.getLineWidth()>0) oMinSerie.setLineWidth(oMinInfo.getLineWidth());
												if (oMinInfo.getColor()!=null) oMinSerie.setColor(oMinInfo.getColor());

												// Add serie to the chart
												oTermoChart.getDataSeries().add(oMinSerie);


												ChartInfo oMaxInfo = aoInfo.get(2);

												DataSerie oMaxSerie = new DataSerie();
												// Get Data from the Db: for rain1h only hourly rate
												aoPoints = oStationDataRepository.getDataSerie(oStationAnag.getStation_code(), oMaxInfo.getColumnName(), oStartDate);

												iMinuteTimeStep = GetMinutesStep(oMaxInfo.getColumnName(),oStationAnag);
												// Convert points to Data Serie
												DataSeriePointToDataSerie(aoPoints,oMaxSerie, oMaxInfo.getConversionFactor(), iMinuteTimeStep);
												// Set Serie Name
												oMaxSerie.setName(oMaxInfo.getName());
												// Main Axis Reference
												oMaxSerie.setAxisId(0);

												if (oMaxInfo.getDashStyle() != null) {
													oMaxSerie.setDashStyle(oMaxInfo.getDashStyle());
												}

												if (oMaxInfo.getLineWidth()>0) oMaxSerie.setLineWidth(oMaxInfo.getLineWidth());
												if (oMaxInfo.getColor()!=null) oMaxSerie.setColor(oMaxInfo.getColor());

												// Add serie to the chart
												oTermoChart.getDataSeries().add(oMaxSerie);

												serializeStationChart(oTermoChart,m_oConfig, oStationAnag.getStation_code(), aoInfo.get(0).getFolderName(), m_oDateFormat);
											}
										}
									}
								}
							}
							catch(Exception oChartEx) {
								System.out.println("OmirDaemon - Station: " + oStationAnag.getStation_code());
								oChartEx.printStackTrace();
							}

							try {

								// --------------------------------------------------------HYDRO CHART
								if (oStationAnag.getMean_creek_level_every() != null) {

									List<ChartInfo> aoInfo = getChartInfoFromSensorCode("Idro");

									if (RefreshChart(aoInfo, lReferenceDate, oStationAnag.getStation_code()))
									{

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
							}
							catch(Exception oChartEx) {
								System.out.println("OmirDaemon - Station: " + oStationAnag.getStation_code());
								oChartEx.printStackTrace();
							}						



							try {

								// --------------------------------------------------------WIND CHART
								if (oStationAnag.getMean_wind_speed_every() != null) {

									List<ChartInfo> aoInfo = getChartInfoFromSensorCode("Vento");

									Date oStartDate = null;

									if (RefreshChart(aoInfo, lReferenceDate, oStationAnag.getStation_code()))
									{

										// Initialize Start Date
										oStartDate = GetChartStartDate(oChartsStartDate, aoInfo);
										oStartDate.setMinutes(0);
										oStartDate.setSeconds(0);

										Calendar oCalendar = Calendar.getInstance(); // creates calendar
										oCalendar.setTime(oStartDate); // sets calendar time/date
										oCalendar.add(Calendar.HOUR_OF_DAY, 1); // adds one hour
										oStartDate = oCalendar.getTime(); // returns new date object, one hour in the future

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

											//-------------------------WIND DIRECTION
											try
											{
												DataSerie oWindDirSerie = new DataSerie();
												// get points
												List<WindDataSeriePoint> aoWindPoints = oStationDataRepository.getWindDataSerie(oStationAnag.getStation_code(), oStartDate);

												// set minute step
												iMinuteTimeStep = 60;

												// Convert points to Data Serie
												if (aoWindPoints != null && aoWindPoints.size() > 0)
													GetWindDirectionSerie(aoWindPoints, oWindDirSerie, iMinuteTimeStep);
												// name
												oWindDirSerie.setName("Wind Direction");

												// Main Axis Reference
												oWindDirSerie.setAxisId(0);

												// add to wind chart
												oWindChart.getDataSeries().add(oWindDirSerie);
											}
											catch(Exception oChartEx) {
												oChartEx.printStackTrace();
											}

										}

										serializeStationChart(oWindChart,m_oConfig, oStationAnag.getStation_code(), aoInfo.get(0).getFolderName(), m_oDateFormat);
									}


									aoInfo = getChartInfoFromSensorCode("Vento2");

									if (RefreshChart(aoInfo, lReferenceDate, oStationAnag.getStation_code()))
									{

										// Initialize Start Date
										oStartDate = GetChartStartDate(oChartsStartDate, aoInfo);

										DataChart oWind2Chart = SaveStandardChart(aoInfo,oStationAnag,asOtherLinks,oStationDataRepository,oStartDate,false,false);

										if (aoInfo.size()>1) {
											ChartInfo oGustInfo = aoInfo.get(1);

											DataSerie oGustSerie = new DataSerie();
											// Get Data from the Db: for 2 days
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

											//-------------------------WIND DIRECTION 2 GG
											try
											{
												DataSerie oWind2DirSerie = new DataSerie();
												// get points
												List<WindDataSeriePoint> aoWindPoints = oStationDataRepository.getWindDataSerie(oStationAnag.getStation_code(), oStartDate);

												// set minute step
												iMinuteTimeStep = 60;

												// Convert points to Data Serie
												if (aoWindPoints != null && aoWindPoints.size() > 0)
													GetWindDirectionSerie(aoWindPoints, oWind2DirSerie, iMinuteTimeStep);
												// name
												oWind2DirSerie.setName("Wind Direction");

												// Main Axis Reference
												oWind2DirSerie.setAxisId(0);

												// add to wind chart
												oWind2Chart.getDataSeries().add(oWind2DirSerie);
											}
											catch(Exception oChartEx) {
												oChartEx.printStackTrace();
											}
										}

										serializeStationChart(oWind2Chart,m_oConfig, oStationAnag.getStation_code(), aoInfo.get(0).getFolderName(), m_oDateFormat);
									}

								}
							}
							catch(Exception oChartEx) {
								System.out.println("OmirDaemon - Station: " + oStationAnag.getStation_code());
								oChartEx.printStackTrace();
							}

							try {

								// --------------------------------------------------------UMIDITY CHART
								if (oStationAnag.getHumidity_every() != null) {

									List<ChartInfo> aoInfo = getChartInfoFromSensorCode("Igro");

									if (RefreshChart(aoInfo, lReferenceDate, oStationAnag.getStation_code()))
									{

										// Initialize Start Date
										Date oStartDate = GetChartStartDate(oChartsStartDate, aoInfo);

										SaveStandardChart(aoInfo,oStationAnag,asOtherLinks,oStationDataRepository,oStartDate);
									}
								}
							}
							catch(Exception oChartEx) {
								System.out.println("OmirDaemon - Station: " + oStationAnag.getStation_code());
								oChartEx.printStackTrace();
							}							




							try {

								// --------------------------------------------------------RADIATION CHART
								if (oStationAnag.getSolar_radiation_pwr_every() != null) {

									List<ChartInfo> aoInfo = getChartInfoFromSensorCode("Radio");

									if (RefreshChart(aoInfo, lReferenceDate, oStationAnag.getStation_code()))
									{

										// Initialize Start Date
										Date oStartDate = GetChartStartDate(oChartsStartDate, aoInfo);

										SaveStandardChart(aoInfo,oStationAnag,asOtherLinks,oStationDataRepository,oStartDate);
									}
								}
							}
							catch(Exception oChartEx) {
								System.out.println("OmirDaemon - Station: " + oStationAnag.getStation_code());
								oChartEx.printStackTrace();
							}			



							try {

								// --------------------------------------------------------BAGNATURA FOGLIARE CHART
								if (oStationAnag.getLeaf_wetness_every() != null) {

									List<ChartInfo> aoInfo = getChartInfoFromSensorCode("Foglie");

									if (RefreshChart(aoInfo, lReferenceDate, oStationAnag.getStation_code()))
									{

										// Initialize Start Date
										Date oStartDate = GetChartStartDate(oChartsStartDate, aoInfo);

										SaveStandardChart(aoInfo,oStationAnag,asOtherLinks,oStationDataRepository,oStartDate);
									}
								}
							}
							catch(Exception oChartEx) {
								System.out.println("OmirDaemon - Station: " + oStationAnag.getStation_code());
								oChartEx.printStackTrace();
							}	



							try {

								// --------------------------------------------------------PRESSIONE CHART
								if (oStationAnag.getMean_sea_level_press_every() != null) {

									List<ChartInfo> aoInfo = getChartInfoFromSensorCode("Press");

									if (RefreshChart(aoInfo, lReferenceDate, oStationAnag.getStation_code()))
									{

										// Initialize Start Date
										Date oStartDate = GetChartStartDate(oChartsStartDate, aoInfo);

										SaveStandardChart(aoInfo,oStationAnag,asOtherLinks,oStationDataRepository,oStartDate);
									}
								}
							}
							catch(Exception oChartEx) {
								System.out.println("OmirDaemon - Station: " + oStationAnag.getStation_code());
								oChartEx.printStackTrace();
							}	


							try {

								// --------------------------------------------------------BATTERY CHART
								if (oStationAnag.getBattery_voltage_every() != null) {

									List<ChartInfo> aoInfo = getChartInfoFromSensorCode("Batt");

									if (RefreshChart(aoInfo, lReferenceDate, oStationAnag.getStation_code()))
									{

										// Initialize Start Date
										Date oStartDate = GetChartStartDate(oChartsStartDate, aoInfo);

										SaveStandardChart(aoInfo,oStationAnag,asOtherLinks,oStationDataRepository,oStartDate);
									}
								}
							}
							catch(Exception oChartEx) {
								System.out.println("OmirDaemon - Station: " + oStationAnag.getStation_code());
								oChartEx.printStackTrace();
							}	


							try {

								// --------------------------------------------------------MARE CHART
								if (oStationAnag.getMean_wave_height_every() != null) {

									List<ChartInfo> aoInfo = getChartInfoFromSensorCode("Boa");

									if (RefreshChart(aoInfo, lReferenceDate, oStationAnag.getStation_code()))
									{
										// Initialize Start Date
										Date oStartDate = GetChartStartDate(oChartsStartDate, aoInfo);

										SaveStandardChart(aoInfo,oStationAnag,asOtherLinks,oStationDataRepository,oStartDate);
									}
								}
							}
							catch(Exception oChartEx) {
								System.out.println("OmirDaemon - Station: " + oStationAnag.getStation_code());
								oChartEx.printStackTrace();
							}


							try {

								// --------------------------------------------------------SNOW CHART
								if (oStationAnag.getMean_snow_depth_every() != null) {

									List<ChartInfo> aoInfo = getChartInfoFromSensorCode("Neve");

									if (RefreshChart(aoInfo, lReferenceDate, oStationAnag.getStation_code()))
									{
										// Initialize Start Date
										Date oStartDate = GetChartStartDate(oChartsStartDate, aoInfo);

										SaveStandardChart(aoInfo,oStationAnag,asOtherLinks,oStationDataRepository,oStartDate);
									}
								}
							}
							catch(Exception oChartEx) {
								System.out.println("OmirDaemon - Station: " + oStationAnag.getStation_code());
								oChartEx.printStackTrace();
							}

						}
					}

					System.out.println();
					System.out.println("OmirlDaemon - Charts Cycle End");


					System.out.println("OmirlDaemon - Stations Layer Start");


					// Get The stations
					StationLastDataRepository oLastRepo = new StationLastDataRepository();

					if (m_oConfig.isEnableSensorLast())
					{
						System.out.println("OmirlDaemon - Pluvio Layer");
						List<SensorViewModel> aoSensorVMList = SerializeSensorLast("rain1h", oLastRepo);
						SerializeSensorsValuesTable(m_oRainValuesTable, aoSensorVMList, "Pluvio");

						System.out.println("OmirlDaemon - Termo Layer");
						aoSensorVMList = SerializeSensorLast("temp", oLastRepo);
						SerializeSensorsValuesTable(m_oTempValuesTable, aoSensorVMList, "Termo");

						System.out.println("OmirlDaemon - Idro Layer");
						aoSensorVMList = SerializeSensorLast("idro", oLastRepo);
						SerializeSensorsValuesTable(m_oHydroValuesTable, aoSensorVMList, "Idro");

						System.out.println("OmirlDaemon - Igro Layer");
						aoSensorVMList = SerializeSensorLast("igro", oLastRepo);
						SerializeSensorsValuesTable(m_oIgroValuesTable, aoSensorVMList, "Igro");

						System.out.println("OmirlDaemon - Radio Layer");
						aoSensorVMList = SerializeSensorLast("radio", oLastRepo);
						SerializeSensorsValuesTable(m_oRadioValuesTable, aoSensorVMList, "Radio");

						System.out.println("OmirlDaemon - Foglie Layer");
						aoSensorVMList = SerializeSensorLast("leafs", oLastRepo);
						SerializeSensorsValuesTable(m_oLeafsValuesTable, aoSensorVMList, "Foglie");

						System.out.println("OmirlDaemon - Batt Layer");
						aoSensorVMList = SerializeSensorLast("batt", oLastRepo);
						SerializeSensorsValuesTable(m_oBattValuesTable, aoSensorVMList, "Batt");

						System.out.println("OmirlDaemon - Press Layer");
						aoSensorVMList = SerializeSensorLast("press", oLastRepo);
						SerializeSensorsValuesTable(m_oPressValuesTable, aoSensorVMList, "Press");

						System.out.println("OmirlDaemon - Neve Layer");
						aoSensorVMList = SerializeSensorLast("snow", oLastRepo);
						SerializeSensorsValuesTable(m_oSnowValuesTable, aoSensorVMList, "Neve");

						System.out.println("OmirlDaemon - Boa Layer");
						aoSensorVMList = SerializeSensorLast("boa", oLastRepo);
						SerializeSensorsValuesTable(m_oBoaValuesTable, aoSensorVMList, "Boa");

						System.out.println("OmirlDaemon - Vento Layer");
						aoSensorVMList = SerializeSensorLast("wind", oLastRepo);
						SerializeSensorsValuesTable(m_oWindValuesTable, aoSensorVMList, "Vento");
					}

					System.out.println("OmirlDaemon - Stations Layer End");

					System.out.println("OmirlDaemon - Web Cam Layer");

					// Serialize WebCAM Layer
					if (m_oConfig.isEnableWebcam()) SerializeWebCamLayer();

					// Serialize ALL SFLOC
					System.out.println("OmirlDaemon - Sfloc Layer");
					if (m_oConfig.isEnableSfloc()) serializeSfloc();

					// Publish new Maps
					System.out.println("OmirlDaemon - Publish Maps");
					if (m_oConfig.isEnableMaps()) publishMaps();

					// Update Summary Table
					System.out.println("OmirlDaemon - Summary Table");
					if (m_oConfig.isEnableSummaryTable()) summaryTable();

					// Max Table
					System.out.println("OmirlDaemon - Max Table");
					if (m_oConfig.isEnableMaxTable()) maxTable();

					// Max Hydro Alert Zones
					System.out.println("OmirlDaemon - Max Hydro Alert Zones");
					if (m_oConfig.isEnableMaxHydroAlertZones()) maxHydroAlertZones();

					// Sections Layer
					System.out.println("OmirlDaemon - Sections Layer");
					if (m_oConfig.isEnableSectionsLayer()) RefreshSectionsLayer();

					System.out.println("OmirlDaemon - Gallery");
					if (m_oConfig.isEnableGallery()) RefreshGallery();

					System.out.println("OmirlDaemon - HydroModel");
					if (m_oConfig.isEnableHydroModel()) RefreshHydroModel();

					//Delete old session
					System.out.println("OmirlDaemon - Clearing Sessions");
					deleteOldSession();

					//Update Time
					lReferenceDate = new Date().getTime();
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
	 * Indicates if it's necessary refresh chart data
	 * @param aoChartInfo
	 * @param lReferenceDate
	 * @return
	 */
	public Boolean RefreshChart(List<ChartInfo> aoChartInfo, long lReferenceDate, String sStationCode) {

		Boolean bReturn = false;
		long lNow = new Date().getTime();
		if (aoChartInfo != null)
		{
			if (aoChartInfo.size() > 0)
			{
				ChartInfo oInfo = aoChartInfo.get(0);
				if (lReferenceDate <= 0) 
				{
					Calendar now = Calendar.getInstance();
					now.set(Calendar.HOUR, 0);
					now.set(Calendar.MINUTE, 0);
					now.set(Calendar.SECOND, 0);
					now.set(Calendar.HOUR_OF_DAY, 0);
					oInfo.setStationLastRefreshTime(now.getTimeInMillis(),sStationCode);
					return true;
				}

				//Convert time in minutes
				long lRefreshMilliseconds = oInfo.getRefreshTime() * 60 * 1000;

				Calendar oNowCalendar = Calendar.getInstance();
				oNowCalendar.setTimeInMillis(lNow);

				Calendar oLastCalendar = Calendar.getInstance();
				oLastCalendar.setTimeInMillis(oInfo.getStationLastRefreshTime(sStationCode));

				if ((lNow - oInfo.getStationLastRefreshTime(sStationCode)) > lRefreshMilliseconds || oNowCalendar.get(Calendar.DAY_OF_YEAR) != oLastCalendar.get(Calendar.DAY_OF_YEAR))
				{
					oInfo.setStationLastRefreshTime(lNow, sStationCode);
					bReturn = true;
				}
			}
		}

		return bReturn;
	}


	private void GetWindDirectionSerie(List<WindDataSeriePoint> oInputWindDir, DataSerie oSerie, int iMinuteTimeStep)
	{
		List<WindDataSeriePoint> oOutputWindDir = new ArrayList<WindDataSeriePoint>();
		DateTime oNow = new DateTime();
		long lTimeStep = iMinuteTimeStep*60L*1000L;
		long lNow = oNow.getMillis();
		Date oStartDate = new Date( oInputWindDir.get(0).getRefDate().getTime());
		oStartDate.setMinutes(0);
		oStartDate.setSeconds(0);

		long lStart = oStartDate.getTime();

		int iCycleCount = 0;
		for (long lTimeCycle = lStart; lTimeCycle<=lNow; lTimeCycle+=lTimeStep)
		{
			WindDataSeriePoint adPoint = new WindDataSeriePoint();
			adPoint.setWindSpeed(0);
			adPoint.setRefDate(new Date(lStart));
			long lNextStep = lTimeCycle+lTimeStep;
			List<WindDataSeriePoint> oRefWindDirections = new ArrayList<WindDataSeriePoint>();
			for (WindDataSeriePoint windDataSeriePoint : oInputWindDir) {

				if (windDataSeriePoint.getRefDate().getTime() < lNextStep && windDataSeriePoint.getRefDate().getTime() >= lTimeCycle) {
					oRefWindDirections.add(windDataSeriePoint);
				}

				if (windDataSeriePoint.getRefDate().getTime() > lNextStep) break;
			}

			adPoint.setWindDir(GetPrevalentWindDirectionAlgorithm(oRefWindDirections));

			lStart = lNextStep;
			oOutputWindDir.add(adPoint);

			iCycleCount++;
		}

		// convert to data serie
		WindDataSeriePointToDataSerie(oOutputWindDir,oSerie,1.0,iMinuteTimeStep);

	}

	private Double GetPrevalentWindDirectionAlgorithm(List<WindDataSeriePoint> oRefWindDirection)
	{
		if (oRefWindDirection.size() == 0)
			return -1.0;
		Double dNumCalma = 0.0;
		Double dVar = 0.0;
		Double dDeg= null;

		HashMap<Integer, ArrayList<WindDataSeriePoint>> oSectorMap = new HashMap<Integer, ArrayList<WindDataSeriePoint>>();
		HashMap<Integer, Double> oFilteredSectorMap = new HashMap<Integer, Double>();

		//Init Sector and filtered Map
		for (int iSector = 0; iSector < 18; iSector++)
		{
			oSectorMap.put(iSector, new ArrayList<WindDataSeriePoint>());
			oFilteredSectorMap.put(iSector, 0.0);
		}


		for (WindDataSeriePoint windDataSeriePoint : oRefWindDirection) {

			if (windDataSeriePoint.getWindSpeed() <= 0.5)
			{
				dNumCalma++;
				continue;
			}

			if (windDataSeriePoint.getWindDir() == 0)
			{
				dNumCalma++;
				continue;
			}

			if (windDataSeriePoint.getWindDir() == -1)
			{
				dVar++;
				continue;
			}

			//Not calm
			List<Integer> oaSectors = GetSector(windDataSeriePoint.getWindDir());

			for (Integer iSector : oaSectors) {
				if (!oSectorMap.containsKey(iSector))
				{
					ArrayList<WindDataSeriePoint> oList = new ArrayList<WindDataSeriePoint>();
					oList.add(windDataSeriePoint);
					oSectorMap.put(iSector, oList);
				}
				else
				{
					oSectorMap.get(iSector).add(windDataSeriePoint);
				}	
			}
		}

		//sector 0 is calm
		oFilteredSectorMap.put(0, dNumCalma);
		//sector 17 is variable
		oFilteredSectorMap.put(17, dVar);

		//return calm
		if (dNumCalma > oRefWindDirection.size() / 2)
			return 0.0;

		//return var
		if (dVar == oRefWindDirection.size() / 2)
			return -1.0;

		//filter formula wmo
		for (int iCountSector = 1; iCountSector < 17; iCountSector ++)
		{
			int iSectorIndex = iCountSector + 1;	

			Double dValue = (double) (((double)1/9)*(double)oSectorMap.get(GetWmoModifiedIndex(iSectorIndex-2)).size()) + 
					(double) (((double)2/9)*(double)oSectorMap.get(GetWmoModifiedIndex(iSectorIndex-1)).size()) +
					(double) (((double)3/9)*(double)oSectorMap.get(GetWmoModifiedIndex(iSectorIndex)).size()) + 
					(double) (((double)2/9)*(double)oSectorMap.get(GetWmoModifiedIndex(iSectorIndex+1)).size()) +
					(double) (((double)1/9)*(double)oSectorMap.get(GetWmoModifiedIndex(iSectorIndex+2)).size());

			oFilteredSectorMap.put(iCountSector, dValue);
		}

		//Search max
		Double dSectorpr = -1.0;
		ArrayList<Integer> oMaxSectors = new ArrayList<Integer>();

		for (int iSector= 1; iSector < 17; iSector++)
		{
			if (oFilteredSectorMap.get(iSector) > dSectorpr)
			{
				dSectorpr = oFilteredSectorMap.get(iSector);
				if (oMaxSectors.size() == 1)
					oMaxSectors.set(0, iSector);
				else
					oMaxSectors.add(iSector);
			}
			else if (oFilteredSectorMap.get(iSector) == dSectorpr)
				oMaxSectors.add(iSector);
		}


		//One max
		if (oMaxSectors.size() == 1)
		{
			dDeg = GetDeg(oMaxSectors.get(0));
			return dDeg;
		}

		//two max
		if (oMaxSectors.size() == 2 && 
				((oMaxSectors.get(1) == oMaxSectors.get(1) + 1 || (oMaxSectors.get(1) == 16 && oMaxSectors.get(1) == 1)) && 
						oMaxSectors.get(0) != 0 && oMaxSectors.get(1) != 17))
		{
			dDeg = (GetDeg(oMaxSectors.get(0)) + GetDeg(oMaxSectors.get(1))) / 2;
			return dDeg;
		}

		//più di due massimi o due massimi non confinanti
		return -1.0;


	}

	private List<Integer> GetSector(double dDirection)
	{
		List<Integer> oReturnList = new ArrayList<Integer>();

		if (dDirection <= 11.25 || dDirection >= 348.75)
			oReturnList.add(16);
		if (dDirection >= 11.25 && dDirection <= 33.75)
			oReturnList.add(1);
		if (dDirection >= 33.75 && dDirection <= 56.25)
			oReturnList.add(2);
		if (dDirection >= 56.25 && dDirection <= 78.75)
			oReturnList.add(3);
		if (dDirection >= 78.75 && dDirection <= 101.25)
			oReturnList.add(4);
		if (dDirection >= 101.25 && dDirection <= 123.75)
			oReturnList.add(5);
		if (dDirection >= 123.75 && dDirection <= 146.25)
			oReturnList.add(6);
		if (dDirection >= 146.25 && dDirection <= 168.75)
			oReturnList.add(7);
		if (dDirection >= 168.75 && dDirection <= 191.25)
			oReturnList.add(8);
		if (dDirection >= 191.25 && dDirection <= 213.75)
			oReturnList.add(9);
		if (dDirection >= 213.75 && dDirection <= 236.25)
			oReturnList.add(10);
		if (dDirection >= 236.25 && dDirection <= 258.75)
			oReturnList.add(11);
		if (dDirection >= 258.75 && dDirection <= 281.25)
			oReturnList.add(12);
		if (dDirection >= 281.25 && dDirection <= 303.75)
			oReturnList.add(13);
		if (dDirection >= 303.75 && dDirection <= 326.75)
			oReturnList.add(14);
		if (dDirection >= 326.75 && dDirection <= 348.75)
			oReturnList.add(15);

		return oReturnList;

	}

	private int GetWmoModifiedIndex(int iOriginalIndex)
	{
		int iReturnIndex = iOriginalIndex;

		switch (iOriginalIndex) {
		case 0:
			iReturnIndex = 15;
			break;
		case 1:
			iReturnIndex = 16;
			break;
		case 2:
			iReturnIndex = 1;
			break;
		case 3:
			iReturnIndex = 2;
			break;
		case 4:
			iReturnIndex = 3;
			break;
		case 5:
			iReturnIndex = 4;
			break;
		case 6:
			iReturnIndex = 5;
			break;
		case 7:
			iReturnIndex = 6;
			break;
		case 8:
			iReturnIndex = 7;
			break;
		case 9:
			iReturnIndex = 8;
			break;
		case 10:
			iReturnIndex = 9;
			break;
		case 11:
			iReturnIndex = 10;
			break;
		case 12:
			iReturnIndex = 11;
			break;
		case 13:
			iReturnIndex = 12;
			break;
		case 14:
			iReturnIndex = 13;
			break;
		case 15:
			iReturnIndex = 14;
			break;
		case 16:
			iReturnIndex = 15;
			break;
		case 17:
			iReturnIndex = 16;
			break;
		case 18:
			iReturnIndex = 1;
			break;
		case 19:
			iReturnIndex = 2;
			break;
		default:
			break;
		}

		return iReturnIndex;
	}

	private double GetDeg(int iSector)
	{
		switch (iSector) {
		case 16:
			return 360;
		case 15:
			return 348.75 - 11.25;
		case 14:
			return 326.25 - 11.25;
		case 13:
			return 303.75 - 11.25;
		case 12:
			return 281.25 - 11.25;
		case 11:
			return 258.75 - 11.25;
		case 10:
			return 236.25 - 11.25;
		case 9:
			return 213.75 - 11.25;
		case 8:
			return 191.25 - 11.25;
		case 7:
			return 168.75 - 11.25;
		case 6:
			return 146.25 - 11.25;
		case 5:
			return 123.75 - 11.25;
		case 4:
			return 101.25 - 11.25;
		case 3:
			return 78.75 - 11.25;
		case 2:
			return 56.25 - 11.25;
		case 1:
			return 33.75 - 11.25;
		case 0:
			return 0;

		default:
			return -1;
		}

	}

	/**
	 * Clears unused sessions
	 */
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

	/**
	 * Serialzes on disk the sfloc layer
	 */
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

	private String publishGeoTiff(String sFileName, String sNameSpace, String sStyle, String sGeoServerDataDir, String sGeoServerAddress,String sGeoServerUser,String sGeoServerPassword)
	{
		try {
			System.out.println("ENTRO IN publishGeoTiff: publish file " + sFileName);

			File oFile = new File(sFileName);

			if (!oFile.exists()) {
				return "";
			}

			String sLayerId = oFile.getName().substring(0, oFile.getName().length()-4);

			SimpleDateFormat oNameDateFormat = new SimpleDateFormat("yyyyMMdd");

			sLayerId = oNameDateFormat.format(new Date()) + sLayerId;

			String sDestinationFileFolder = sGeoServerDataDir;

			File oGeoServerDataDirFile = new File(sDestinationFileFolder+"/"+sLayerId + ".tif");

			FileUtils.copyFile(oFile, oGeoServerDataDirFile);

			System.out.println("Copy to: " + oGeoServerDataDirFile.getAbsolutePath());

			GeoServerDataManager2 oGeoManager = new GeoServerDataManager2(sGeoServerAddress, "", sGeoServerUser, sGeoServerPassword);

			if (!oGeoManager.ExistsLayer(sNameSpace, sLayerId))
			{
				oGeoManager.addLayer(sLayerId, sNameSpace, oFile.getAbsolutePath(), sStyle);
				System.out.println("File added to geoserver");
			}
			else 
			{
				System.out.println("File already published");
			}			

			return sLayerId;
		}
		catch(Exception oEx) {
			System.out.println("publishMaps Exception " + oEx.toString());
			oEx.printStackTrace();
			return "";
		}				
	}

	/**
	 * Publish maps
	 */
	private void publishMaps() {

		try {
			System.out.println("ENTRO IN PUBLISH MAPS");

			// Creare nella conf un MapInfo
			// 	.codice della mappa
			//	.stile
			//	.tipo tiff o shape
			// Per ognuno di quelli
			//		Andare nelle cartelle
			// 		Prendere ultimo file non ancora pubblicato
			//		Se c'è pubblicarlo
			//		Se c'è aggiornare il riferimento al link id per quel codice (oggetto MapInfoViewModel)
			// Scrivi un xml con per ogni codice il layerId più recente o cmq di riferimento

			List<MapInfo> aoMapsInfo = m_oConfig.getMapsInfo();

			List<MapInfoViewModel> aoOutputInfo = new ArrayList<MapInfoViewModel>();

			String sBasePath = m_oConfig.getFileRepositoryPath();

			SimpleDateFormat oDateFormat = new SimpleDateFormat("yyyy/MM/dd");
			Date oActualDate = new Date();


			for (MapInfo oMapInfo : aoMapsInfo) {
				String sLayerPath = sBasePath + "/maps/" + oMapInfo.getCode();

				// Get Start Date Time Filter
				//long lNowTime = oActualDate.getTime();

				String sFullDir = sLayerPath + "/" + oDateFormat.format(oActualDate);

				File oFile = OmirlDaemon.lastFileByName(sFullDir, ".tif");
				if (oFile == null) 
				{
					System.out.println("Map Code " + oMapInfo.getCode() + " Path Not Available " + sFullDir);
					continue;
				}

				String sFileName = oFile.getAbsolutePath();

				String sLayerId = publishGeoTiff(sFileName,"omirl",oMapInfo.getStyle(), m_oConfig.getGeoServerDataFolder(), m_oConfig.getGeoServerAddress(), m_oConfig.getGeoServerUser(), m_oConfig.getGeoServerPassword());

				if (sLayerId != null)
				{
					if (!sLayerId.isEmpty())
					{
						MapInfoViewModel oMapInfoViewModel = new MapInfoViewModel();
						oMapInfoViewModel.setCode(oMapInfo.getCode());
						oMapInfoViewModel.setLayerId(sLayerId);

						aoOutputInfo.add(oMapInfoViewModel);
					}
				}
			}

			String sIndexPath = sBasePath + "/maps/index/" + oDateFormat.format(oActualDate);

			File oOutPath = new File(sIndexPath);
			if (oOutPath.exists() == false) {
				oOutPath.mkdirs();
				oOutPath.setReadable(true,false);
				oOutPath.setWritable(true, false);
			}

			if (aoOutputInfo.size()>0)
			{
				String sFileName = sIndexPath + "/index"+m_oDateFormat.format(oActualDate)+".xml"; 
				SerializationUtils.serializeObjectToXML(sFileName , aoOutputInfo);
			}

			System.out.println("Finita PUBLISH MAPS");

		}
		catch(Exception oEx) {
			System.out.println("publishMaps Exception " + oEx.toString());
			oEx.printStackTrace();
		}		
	}

	/**
	 * Generates the Values Table
	 */
	private void valuesTable() {
		try {
			System.out.println("Sensor Value Table Start");

			System.out.println("Sensor Value Table End");
		}
		catch(Exception oEx) {
			oEx.printStackTrace();
		}
	}

	private void maxHydroAlertZones()
	{
		System.out.println("Max Hydro Alert Zones Start");

		try{
			// Reference Date: today starting from midnight
			Date oActualDate = new Date();

			// repository
			StationAnagRepository oStationAnagRepository = new StationAnagRepository();
			StationLastDataRepository oStationLastDataRepository = new StationLastDataRepository();
			StationDataRepository oStationDataRepository = new StationDataRepository();

			MaxHydroAlertZoneViewModel oMaxHydroAlertZoneViewModel = new MaxHydroAlertZoneViewModel();

			// select by warning area
			List<StationAnag> oStationList = oStationAnagRepository.SelectAll(StationAnag.class);
			// get last data value
			List<SensorLastData> oStationLastData = oStationLastDataRepository.selectByStationType("lastdataidro");

			// Alert Zone table
			for (int iAlertZone=0; iAlertZone<m_oConfig.getMaxHydroAlertZone().size(); iAlertZone++)
			{

				// search station with warning area
				String sWarningArea = m_oConfig.getMaxHydroAlertZone().get(iAlertZone).getZone();

				// Station List
				for (int iStation=0; iStation< oStationList.size(); iStation++)
				{
					if (oStationList.get(iStation).getWarn_area() == null || !oStationList.get(iStation).getWarn_area().contains(sWarningArea))
						continue;

					// previous 24h 
					Date oDate = new Date();
					oDate.setTime(oDate.getTime() - 24 * 3600 * 1000);

					//max value
					MaxTableRow oMaxTableRow = oStationDataRepository.GetStationAlertZonesMaxTableCell(oStationList.get(iStation).getStation_code(), "mean_creek_level", sWarningArea, oDate);
					if (oMaxTableRow != null)
					{
						// new view model
						MaxHydroAlertZoneRowViewModel oViewModel = new MaxHydroAlertZoneRowViewModel();
						oViewModel.setDate24HMax(oMaxTableRow.getReference_date());
						oViewModel.setValueOnDate24HMax(oMaxTableRow.getValue());
						oViewModel.setStation(oStationList.get(iStation).getName());
						oViewModel.setCode(oStationList.get(iStation).getStation_code());
						oViewModel.setMunicipality(oStationList.get(iStation).getMunicipality());
						oViewModel.setRiver(oStationList.get(iStation).getRiver());
						oViewModel.setWarnArea(sWarningArea);
						oViewModel.setBasin(oStationList.get(iStation).getBasin());
						oViewModel.setDistrict(oStationList.get(iStation).getDistrict());

						// last value
						for (SensorLastData sensorLastData : oStationLastData) {
							if (sensorLastData.getStation_code().equals(oStationList.get(iStation).getStation_code()))
							{
								oViewModel.setValueOnDateRef(sensorLastData.getSensorvalue());
								oViewModel.setDateRef(sensorLastData.getReference_date());
							}
						}

						switch (sWarningArea) {
						case "A":
							oMaxHydroAlertZoneViewModel.getAlertZonesA().add(oViewModel);
							break;
						case "B":
							oMaxHydroAlertZoneViewModel.getAlertZonesB().add(oViewModel);
							break;
						case "C":
							oMaxHydroAlertZoneViewModel.getAlertZonesC().add(oViewModel);
							break;
						case "D":
							oMaxHydroAlertZoneViewModel.getAlertZonesD().add(oViewModel);
							break;
						case "E":
							oMaxHydroAlertZoneViewModel.getAlertZonesE().add(oViewModel);
							break;
						case "M":
							oMaxHydroAlertZoneViewModel.getAlertZonesM().add(oViewModel);
							break;
						case "C+":
							oMaxHydroAlertZoneViewModel.getAlertZonesCPlus().add(oViewModel);
							break;
						case "C-":
							oMaxHydroAlertZoneViewModel.getAlertZonesCLess().add(oViewModel);
							break;
						default:
							break;
						}

					}
				}
			}

			String sBasePath = m_oConfig.getFileRepositoryPath();

			String sOutputPath = sBasePath + "/tables/maxhydrozones";

			SimpleDateFormat oDateFormat = new SimpleDateFormat("yyyy/MM/dd");

			String sFullDir = sOutputPath + "/" + oDateFormat.format(oActualDate);

			File oOutPath = new File(sFullDir);
			if (oOutPath.exists() == false) {
				oOutPath.mkdirs();
				oOutPath.setReadable(true,false);
				oOutPath.setWritable(true, false);
			}

			String sOutputFile = sFullDir + "/maxhydroalertzone" +m_oDateFormat.format(new Date())+".xml"; 

			SerializationUtils.serializeObjectToXML(sOutputFile, oMaxHydroAlertZoneViewModel);

			System.out.println("Max Hydro Alert Zones End");
		}
		catch(Exception oEx)
		{
			oEx.printStackTrace();
		}


	}

	@SuppressWarnings("deprecation")
	private void maxTable() {
		try {
 			System.out.println("Max Table Start");

			// Data Repository
			StationDataRepository oStationDataRepository = new StationDataRepository();

			// Date Format for hour formatting
			SimpleDateFormat oHourFormat = new SimpleDateFormat("HH:mm");

			// Reference Date: today starting from midnight
			Date oActualDate = new Date();

			oActualDate.setHours(0);
			oActualDate.setMinutes(0);
			oActualDate.setSeconds(0);

			// Query result
			List<MaxTableRow> aoMaxTableRows = null;

			// View model to serialize
			MaxTableViewModel oMaxTableViewModel = new MaxTableViewModel();


			// Alert Zone table
			for (int iRows=0; iRows<m_oConfig.getAlertMaxTable().getRows().size(); iRows++)
			{
				// Max Table Row
				MaxTableRowViewModel oRow = new MaxTableRowViewModel();

				String sRow = m_oConfig.getAlertMaxTable().getRows().get(iRows);
				// Set Name
				oRow.setName(sRow);

				String sRowFilter = m_oConfig.getAlertMaxTable().getRowFilters().get(iRows);


				for (int iCols =0; iCols<m_oConfig.getAlertMaxTable().getColumns().size(); iCols++)
				{
					String sMethodCode = m_oConfig.getAlertMaxTable().getMethodCodes().get(iCols);
					String sMethodName = "";
					java.lang.reflect.Method oMethod;					

					// Read Data from Db
					aoMaxTableRows = oStationDataRepository.GetAlertZonesMaxTableCell(m_oConfig.getAlertMaxTable().getColumns().get(iCols), sRowFilter, oActualDate);

					// Any result?
					if (aoMaxTableRows.size()>0) {

						// Get first
						MaxTableRow oTableRow = aoMaxTableRows.get(0);

						// Get Value
						double dValue = oTableRow.getValue();

						// Set hh:mm stationName
						String sText = "["+ oHourFormat.format(oTableRow.getReference_date())+ "] ";
						sText += oTableRow.getStation_name();

						// Set station code
						String sStationCode = oTableRow.getStation_code();

						sMethodName = "set" + sMethodCode + "val";					
						try {
							oMethod = oRow.getClass().getMethod(sMethodName, String.class);
							oMethod.invoke(oRow, "" + dValue);
						} 
						catch (Exception oEx) {
							System.out.println("Exception trying to set max Table values. Method Name = " + sMethodName);
							oEx.printStackTrace();
						}


						sMethodName = "set" + sMethodCode;
						try {
							oMethod = oRow.getClass().getMethod(sMethodName, String.class);
							oMethod.invoke(oRow, sText);
						} 
						catch (Exception oEx) {
							System.out.println("Exception trying to set max Table text. Method Name = " + sMethodName);
							oEx.printStackTrace();
						}


						sMethodName = "set" + sMethodCode + "code";
						try {
							oMethod = oRow.getClass().getMethod(sMethodName, String.class);
							oMethod.invoke(oRow, sStationCode);
						} 
						catch (Exception oEx) {
							System.out.println("Exception trying to set max Table code. Method Name = " + sMethodName);
							oEx.printStackTrace();
						}

						String sStyle = "";

						if (dValue> m_oConfig.getAlertMaxTable().getThreshold2().get(iCols))
						{
							sStyle = m_oConfig.getAlertMaxTable().getThreshold2Style();
						}
						else if (dValue > m_oConfig.getAlertMaxTable().getThreshold1().get(iCols))
						{
							sStyle = m_oConfig.getAlertMaxTable().getThreshold1Style();
						}

						sMethodName = "set" + sMethodCode + "BkColor";
						try {
							oMethod = oRow.getClass().getMethod(sMethodName, String.class);
							oMethod.invoke(oRow, sStyle);
						} 
						catch (Exception oEx) {
							System.out.println("Exception trying to set cell style. Method Name = " + sMethodName);
							oEx.printStackTrace();
						}

					}
					else {
						// No data available

						sMethodName = "set" + sMethodCode + "val";
						try {
							oMethod = oRow.getClass().getMethod(sMethodName, String.class);
							oMethod.invoke(oRow, "-");
						} 
						catch (Exception oEx) {
							System.out.println("Exception trying to set max Table code. Method Name = " + sMethodName);
							oEx.printStackTrace();
						}
					}					
				}

				// Add row
				oMaxTableViewModel.getAlertZones().add(oRow);
			}


			// Districts Table

			for (int iRows=0; iRows<m_oConfig.getDistrictMaxTable().getRows().size(); iRows++)
			{
				// Max Table Row
				MaxTableRowViewModel oRow = new MaxTableRowViewModel();

				String sRow = m_oConfig.getDistrictMaxTable().getRows().get(iRows);
				// Set Name
				oRow.setName(sRow);

				String sRowFilter = m_oConfig.getDistrictMaxTable().getRowFilters().get(iRows);


				for (int iCols =0; iCols<m_oConfig.getDistrictMaxTable().getColumns().size(); iCols++)
				{
					String sMethodCode = m_oConfig.getDistrictMaxTable().getMethodCodes().get(iCols);
					String sMethodName = "";
					java.lang.reflect.Method oMethod;					

					// Read Data from Db
					aoMaxTableRows = oStationDataRepository.GetDistrictMaxTableCell(m_oConfig.getDistrictMaxTable().getColumns().get(iCols), sRowFilter, oActualDate);

					// Any result?
					if (aoMaxTableRows.size()>0) {

						// Get first
						MaxTableRow oTableRow = aoMaxTableRows.get(0);

						// Get Value
						double dValue = oTableRow.getValue();

						// Set hh:mm stationName
						String sText = "["+ oHourFormat.format(oTableRow.getReference_date())+ "] ";
						sText += oTableRow.getStation_name();

						// Set station code
						String sStationCode = oTableRow.getStation_code();

						sMethodName = "set" + sMethodCode + "val";					
						try {
							oMethod = oRow.getClass().getMethod(sMethodName, String.class);
							oMethod.invoke(oRow, "" + dValue);
						} 
						catch (Exception oEx) {
							System.out.println("Exception trying to set max Table values. Method Name = " + sMethodName);
							oEx.printStackTrace();
						}


						sMethodName = "set" + sMethodCode;
						try {
							oMethod = oRow.getClass().getMethod(sMethodName, String.class);
							oMethod.invoke(oRow, sText);
						} 
						catch (Exception oEx) {
							System.out.println("Exception trying to set max Table text. Method Name = " + sMethodName);
							oEx.printStackTrace();
						}


						sMethodName = "set" + sMethodCode + "code";
						try {
							oMethod = oRow.getClass().getMethod(sMethodName, String.class);
							oMethod.invoke(oRow, sStationCode);
						} 
						catch (Exception oEx) {
							System.out.println("Exception trying to set max Table code. Method Name = " + sMethodName);
							oEx.printStackTrace();
						}

						String sStyle = "";

						if (dValue> m_oConfig.getDistrictMaxTable().getThreshold2().get(iCols))
						{
							sStyle = m_oConfig.getDistrictMaxTable().getThreshold2Style();
						}
						else if (dValue > m_oConfig.getDistrictMaxTable().getThreshold1().get(iCols))
						{
							sStyle = m_oConfig.getDistrictMaxTable().getThreshold1Style();
						}

						sMethodName = "set" + sMethodCode + "BkColor";
						try {
							oMethod = oRow.getClass().getMethod(sMethodName, String.class);
							oMethod.invoke(oRow, sStyle);
						} 
						catch (Exception oEx) {
							System.out.println("Exception trying to set cell style. Method Name = " + sMethodName);
							oEx.printStackTrace();
						}
					}
					else {
						// No data available

						sMethodName = "set" + sMethodCode + "val";
						try {
							oMethod = oRow.getClass().getMethod(sMethodName, String.class);
							oMethod.invoke(oRow, "-");
						} 
						catch (Exception oEx) {
							System.out.println("Exception trying to set max Table code. Method Name = " + sMethodName);
							oEx.printStackTrace();
						}
					}					
				}

				// Add row
				oMaxTableViewModel.getDistricts().add(oRow);
			}

			String sBasePath = m_oConfig.getFileRepositoryPath();

			String sOutputPath = sBasePath + "/tables/max";

			SimpleDateFormat oDateFormat = new SimpleDateFormat("yyyy/MM/dd");

			String sFullDir = sOutputPath + "/" + oDateFormat.format(oActualDate);

			File oOutPath = new File(sFullDir);
			if (oOutPath.exists() == false) {
				oOutPath.mkdirs();
				oOutPath.setReadable(true,false);
				oOutPath.setWritable(true, false);
			}

			String sOutputFile = sFullDir + "/maxtable" +m_oDateFormat.format(new Date())+".xml"; 

			SerializationUtils.serializeObjectToXML(sOutputFile, oMaxTableViewModel);

			System.out.println("Max Table End");
		}
		catch(Exception oEx)
		{
			oEx.printStackTrace();
		}
	}

	/**
	 * Generates the summary table
	 */
	private void summaryTable() {
		try {

			System.out.println("Summary Table Start");

			Date oActualDate = new Date();

			SummaryInfo oSummaryInfo = new SummaryInfo();

			StationDataRepository oStationDataRepository = new StationDataRepository();

			// trova il max e min temperatura di oggi x provincia
			SummaryInfoEntity oGeMax = oStationDataRepository.getDistrictMaxTemperatureSummaryInfo("GE", oActualDate, "Genova");
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

			SummaryInfoEntity oGeMin = oStationDataRepository.getDistrictMinTemperatureSummaryInfo("GE", oActualDate, "Genova");

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



			SummaryInfoEntity oSvMax = oStationDataRepository.getDistrictMaxTemperatureSummaryInfo("SV", oActualDate, "Savona");
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

			SummaryInfoEntity oSvMin = oStationDataRepository.getDistrictMinTemperatureSummaryInfo("SV", oActualDate, "Savona");

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



			SummaryInfoEntity oImMax = oStationDataRepository.getDistrictMaxTemperatureSummaryInfo("IM", oActualDate, "Imperia");
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

			SummaryInfoEntity oImMin = oStationDataRepository.getDistrictMinTemperatureSummaryInfo("IM", oActualDate, "Imperia");

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



			SummaryInfoEntity oSpMax = oStationDataRepository.getDistrictMaxTemperatureSummaryInfo("SP", oActualDate, "La Spezia");
			DistrictSummaryInfo oDistrictSummarySp = new DistrictSummaryInfo();
			oDistrictSummarySp.setDescription("La Spezia");

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

			SummaryInfoEntity oSpMin = oStationDataRepository.getDistrictMinTemperatureSummaryInfo("SP", oActualDate, "La Spezia");

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

			SummaryInfoEntity oAMax = oStationDataRepository.getAlertZoneMaxTemperatureSummaryInfo("A", oActualDate);

			if (oAMax!=null)
			{
				oZoneASummary.setMax(oAMax.getValue());
				oZoneASummary.setStationMax(oAMax.getStationName());
				oZoneASummary.setRefDateMax(oAMax.getReferenceDate());
			}
			else
			{
				oZoneASummary.setMax(-9999.0);
				oZoneASummary.setStationMax("N.D.");
				oZoneASummary.setRefDateMax(null);				
			}

			SummaryInfoEntity oAMin = oStationDataRepository.getAlertZoneMinTemperatureSummaryInfo("A", oActualDate);

			if (oAMin!=null)
			{
				oZoneASummary.setMin(oAMin.getValue());
				oZoneASummary.setStationMin(oAMin.getStationName());
				oZoneASummary.setRefDateMin(oAMin.getReferenceDate());
			}
			else
			{
				oZoneASummary.setMin(-9999.0);
				oZoneASummary.setStationMin("N.D.");
				oZoneASummary.setRefDateMin(null);				
			}


			oSummaryInfo.getAlertInfo().add(oZoneASummary);

			AlertZoneSummaryInfo oZoneBSummary = new AlertZoneSummaryInfo();
			oZoneBSummary.setDescription("B");


			SummaryInfoEntity oBMax = oStationDataRepository.getAlertZoneMaxTemperatureSummaryInfo("B", oActualDate);

			if (oBMax!=null)
			{
				oZoneBSummary.setMax(oBMax.getValue());
				oZoneBSummary.setStationMax(oBMax.getStationName());
				oZoneBSummary.setRefDateMax(oBMax.getReferenceDate());
			}
			else
			{
				oZoneBSummary.setMax(-9999.0);
				oZoneBSummary.setStationMax("N.D.");
				oZoneBSummary.setRefDateMax(null);				
			}

			SummaryInfoEntity oBMin = oStationDataRepository.getAlertZoneMinTemperatureSummaryInfo("B", oActualDate);

			if (oBMin!=null)
			{
				oZoneBSummary.setMin(oBMin.getValue());
				oZoneBSummary.setStationMin(oBMin.getStationName());
				oZoneBSummary.setRefDateMin(oBMin.getReferenceDate());
			}
			else
			{
				oZoneBSummary.setMin(-9999.0);
				oZoneBSummary.setStationMin("N.D.");
				oZoneBSummary.setRefDateMin(null);				
			}

			oSummaryInfo.getAlertInfo().add(oZoneBSummary);

			AlertZoneSummaryInfo oZoneCSummary = new AlertZoneSummaryInfo();
			oZoneCSummary.setDescription("C");

			//SummaryInfoEntity oCMax = oStationDataRepository.getAlertZoneMaxTemperatureSummaryInfo("B", oActualDate);
			SummaryInfoEntity oCMax = oStationDataRepository.getAlertZoneMaxTemperatureSummaryInfo("C", oActualDate);

			if (oBMax!=null)
			{
				oZoneCSummary.setMax(oCMax.getValue());
				oZoneCSummary.setStationMax(oCMax.getStationName());
				oZoneCSummary.setRefDateMax(oCMax.getReferenceDate());
			}
			else
			{
				oZoneCSummary.setMax(-9999.0);
				oZoneCSummary.setStationMax("N.D.");
				oZoneCSummary.setRefDateMax(null);				
			}

			//SummaryInfoEntity oCMin = oStationDataRepository.getAlertZoneMinTemperatureSummaryInfo("B", oActualDate);
			SummaryInfoEntity oCMin = oStationDataRepository.getAlertZoneMinTemperatureSummaryInfo("C", oActualDate);

			if (oBMin!=null)
			{
				oZoneCSummary.setMin(oCMin.getValue());
				oZoneCSummary.setStationMin(oCMin.getStationName());
				oZoneCSummary.setRefDateMin(oCMin.getReferenceDate());
			}
			else
			{
				oZoneCSummary.setMin(-9999.0);
				oZoneCSummary.setStationMin("N.D.");
				oZoneCSummary.setRefDateMin(null);				
			}			

			oSummaryInfo.getAlertInfo().add(oZoneCSummary);


			AlertZoneSummaryInfo oZoneCPSummary = new AlertZoneSummaryInfo();
			oZoneCPSummary.setDescription("C+");

			SummaryInfoEntity oCPMax = oStationDataRepository.getAlertZoneMaxTemperatureSummaryInfo("C+", oActualDate);

			if (oCPMax!=null)
			{
				oZoneCPSummary.setMax(oCPMax.getValue());
				oZoneCPSummary.setStationMax(oCPMax.getStationName());
				oZoneCPSummary.setRefDateMax(oCPMax.getReferenceDate());
			}
			else
			{
				oZoneCPSummary.setMax(-9999.0);
				oZoneCPSummary.setStationMax("N.D.");
				oZoneCPSummary.setRefDateMax(null);				
			}

			SummaryInfoEntity oCPMin = oStationDataRepository.getAlertZoneMinTemperatureSummaryInfo("C+", oActualDate);

			if (oCPMin!=null)
			{
				oZoneCPSummary.setMin(oCPMin.getValue());
				oZoneCPSummary.setStationMin(oCPMin.getStationName());
				oZoneCPSummary.setRefDateMin(oCPMin.getReferenceDate());
			}
			else
			{
				oZoneCPSummary.setMin(-9999.0);
				oZoneCPSummary.setStationMin("N.D.");
				oZoneCPSummary.setRefDateMin(null);				
			}			

			oSummaryInfo.getAlertInfo().add(oZoneCPSummary);			


			AlertZoneSummaryInfo oZoneCMSummary = new AlertZoneSummaryInfo();
			oZoneCMSummary.setDescription("C-");

			SummaryInfoEntity oCMMax = oStationDataRepository.getAlertZoneMaxTemperatureSummaryInfo("C-", oActualDate);

			if (oCMMax!=null)
			{
				oZoneCMSummary.setMax(oCMMax.getValue());
				oZoneCMSummary.setStationMax(oCMMax.getStationName());
				oZoneCMSummary.setRefDateMax(oCMMax.getReferenceDate());
			}
			else
			{
				oZoneCMSummary.setMax(-9999.0);
				oZoneCMSummary.setStationMax("N.D.");
				oZoneCMSummary.setRefDateMax(null);				
			}

			SummaryInfoEntity oCMMin = oStationDataRepository.getAlertZoneMinTemperatureSummaryInfo("C-", oActualDate);

			if (oCMMin!=null)
			{
				oZoneCMSummary.setMin(oCMMin.getValue());
				oZoneCMSummary.setStationMin(oCMMin.getStationName());
				oZoneCMSummary.setRefDateMin(oCMMin.getReferenceDate());
			}
			else
			{
				oZoneCMSummary.setMin(-9999.0);
				oZoneCMSummary.setStationMin("N.D.");
				oZoneCMSummary.setRefDateMin(null);				
			}			

			oSummaryInfo.getAlertInfo().add(oZoneCMSummary);			


			AlertZoneSummaryInfo oZoneMSummary = new AlertZoneSummaryInfo();
			oZoneMSummary.setDescription("Magra");

			SummaryInfoEntity oMMax = oStationDataRepository.getAlertZoneMaxTemperatureSummaryInfo("M", oActualDate);

			if (oMMax!=null)
			{
				oZoneMSummary.setMax(oMMax.getValue());
				oZoneMSummary.setStationMax(oMMax.getStationName());
				oZoneMSummary.setRefDateMax(oMMax.getReferenceDate());
			}
			else
			{
				oZoneMSummary.setMax(-9999.0);
				oZoneMSummary.setStationMax("N.D.");
				oZoneMSummary.setRefDateMax(null);				
			}

			SummaryInfoEntity oMMin = oStationDataRepository.getAlertZoneMinTemperatureSummaryInfo("M", oActualDate);

			if (oMMin!=null)
			{
				oZoneMSummary.setMin(oMMin.getValue());
				oZoneMSummary.setStationMin(oMMin.getStationName());
				oZoneMSummary.setRefDateMin(oMMin.getReferenceDate());
			}
			else
			{
				oZoneMSummary.setMin(-9999.0);
				oZoneMSummary.setStationMin("N.D.");
				oZoneMSummary.setRefDateMin(null);				
			}			

			oSummaryInfo.getAlertInfo().add(oZoneMSummary);



			AlertZoneSummaryInfo oZoneDSummary = new AlertZoneSummaryInfo();
			oZoneDSummary.setDescription("D");

			SummaryInfoEntity oDMax = oStationDataRepository.getAlertZoneMaxTemperatureSummaryInfo("D", oActualDate);

			if (oDMax!=null)
			{
				oZoneDSummary.setMax(oDMax.getValue());
				oZoneDSummary.setStationMax(oDMax.getStationName());
				oZoneDSummary.setRefDateMax(oDMax.getReferenceDate());
			}
			else
			{
				oZoneDSummary.setMax(-9999.0);
				oZoneDSummary.setStationMax("N.D.");
				oZoneDSummary.setRefDateMax(null);				
			}

			SummaryInfoEntity oDMin = oStationDataRepository.getAlertZoneMinTemperatureSummaryInfo("D", oActualDate);

			if (oDMin!=null)
			{
				oZoneDSummary.setMin(oDMin.getValue());
				oZoneDSummary.setStationMin(oDMin.getStationName());
				oZoneDSummary.setRefDateMin(oDMin.getReferenceDate());
			}
			else
			{
				oZoneDSummary.setMin(-9999.0);
				oZoneDSummary.setStationMin("N.D.");
				oZoneDSummary.setRefDateMin(null);				
			}			

			oSummaryInfo.getAlertInfo().add(oZoneDSummary);

			AlertZoneSummaryInfo oZoneESummary = new AlertZoneSummaryInfo();
			oZoneESummary.setDescription("E");

			SummaryInfoEntity oEMax = oStationDataRepository.getAlertZoneMaxTemperatureSummaryInfo("E", oActualDate);

			if (oEMax!=null)
			{
				oZoneESummary.setMax(oEMax.getValue());
				oZoneESummary.setStationMax(oEMax.getStationName());
				oZoneESummary.setRefDateMax(oEMax.getReferenceDate());
			}
			else
			{
				oZoneESummary.setMax(-9999.0);
				oZoneESummary.setStationMax("N.D.");
				oZoneESummary.setRefDateMax(null);				
			}

			SummaryInfoEntity oEMin = oStationDataRepository.getAlertZoneMinTemperatureSummaryInfo("E", oActualDate);

			if (oEMin!=null)
			{
				oZoneESummary.setMin(oEMin.getValue());
				oZoneESummary.setStationMin(oEMin.getStationName());
				oZoneESummary.setRefDateMin(oEMin.getReferenceDate());
			}
			else
			{
				oZoneESummary.setMin(-9999.0);
				oZoneESummary.setStationMin("N.D.");
				oZoneESummary.setRefDateMin(null);				
			}			


			oSummaryInfo.getAlertInfo().add(oZoneESummary);

			StationAnagRepository oStationAnagRepository  = new StationAnagRepository();

			List<StationAnag> aoCostalStations = oStationAnagRepository.getCostalWindStations();

			// Trova il max del vento e raffica di oggi per le stazioni che sono in configurazione.
			String sCostalCodes = "";

			/*
			for (int iCodes=0; iCodes<m_oConfig.getWindSummaryInfo().getCostalCodes().size(); iCodes++) {
				sCostalCodes += "'" + m_oConfig.getWindSummaryInfo().getCostalCodes().get(iCodes) + "'";
				if (iCodes != m_oConfig.getWindSummaryInfo().getCostalCodes().size()-1) sCostalCodes += ", ";
			}
			 */
			for (int iCodes=0; iCodes<aoCostalStations.size(); iCodes++) {
				sCostalCodes += "'" + aoCostalStations.get(iCodes).getStation_code() + "'";
				if (iCodes != aoCostalStations.size()-1) sCostalCodes += ", ";
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

			List<StationAnag> aoInternalStations = oStationAnagRepository.getInternalWindStations();
			String sInternalCodes = "";
			/*
			for (int iCodes=0; iCodes<m_oConfig.getWindSummaryInfo().getInternalCodes().size(); iCodes++) {
				sInternalCodes += "'" + m_oConfig.getWindSummaryInfo().getInternalCodes().get(iCodes) + "'";
				if (iCodes != m_oConfig.getWindSummaryInfo().getInternalCodes().size()-1) sInternalCodes += ", ";
			}
			 */
			for (int iCodes=0; iCodes<aoInternalStations.size(); iCodes++) {
				sInternalCodes += "'" + aoInternalStations.get(iCodes).getStation_code() + "'";
				if (iCodes != aoInternalStations.size()-1) sInternalCodes += ", ";
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
			if (oOutPath.exists() == false) {
				oOutPath.mkdirs();
				oOutPath.setReadable(true, false);
				oOutPath.setWritable(true, false);
			}

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
					if (aoPoints.get(iPoints).getVal()*aoInfo.get(0).getConversionFactor()>dYMax)
					{
						// Take the new Max
						dYMax = aoPoints.get(iPoints).getVal()*aoInfo.get(0).getConversionFactor();
					}

					if (aoPoints.get(iPoints).getVal()*aoInfo.get(0).getConversionFactor()<dYMin)
					{
						dYMin = aoPoints.get(iPoints).getVal()*aoInfo.get(0).getConversionFactor();
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
		DateFormat oFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		oDataChart.setSubTitle(aoInfo.get(0).getSubtitle() + " - " + oFormat.format(new Date()));
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
			ClearThread oThread = new ClearThread(m_oConfig.getFileRepositoryPath(), m_oConfig.getCircleBufferDays(), m_oConfig.getGeoServerAddress(), m_oConfig.getGeoServerUser(), m_oConfig.getGeoServerPassword(), m_oConfig.getGeoServerDataFolder());
			oThread.start();			
		}
		catch(Exception oEx) {
			System.out.println("OmirlDaemon - Clear Daemon Exception");
			oEx.printStackTrace();
		}

		try {
			DBClearThread oThread = new DBClearThread(m_oConfig.getDbBufferDataDays());
			oThread.start();			
		}
		catch(Exception oEx) {
			System.out.println("OmirlDaemon - Clear Daemon Exception");
			oEx.printStackTrace();
		}

		RefreshConfiguration();

		if (m_oConfig.isEnableThreshold()) RefreshThresholds();

		if (m_oConfig.isEnableStationsTable()) RefreshStationTables();
	}

	public void RefreshSectionsLayer() {
		try{

			System.out.println("OmirlDaemon - Refresh Sections Layer");

			List<SectionLayerInfo> aoInfos = m_oConfig.getSectionLayersInfo();

			for (SectionLayerInfo oLayerInfo : aoInfos) {

				System.out.println("OmirlDaemon - Model " + oLayerInfo.getModelName() + " Code: " + oLayerInfo.getModelCode() +  " Flag Col " + oLayerInfo.getFlagColumn());

				SerializeSectionsLayer(oLayerInfo.getModelName(), oLayerInfo.getModelCode(), oLayerInfo.getFlagColumn(), oLayerInfo.getHasSubFolders(), oLayerInfo.getDisableOnMap(), oLayerInfo.getTimeRewind(), oLayerInfo.getMaxDelayMinutes());
			}
		}
		catch(Exception oEx)
		{
			System.out.println("OmirlDaemon - Refresh Sections Layer Exception");
			oEx.toString();
		}

	}

	public void RefreshHydroModel() {
		try{

			System.out.println("OmirlDaemon - Refresh Hydro Model Table");

			HydroModelTables aoHydro = m_oConfig.getHydroModelTables();

			//load all section basins
			Repository<SectionBasins> oSectionBasinsRepository = new Repository<SectionBasins>();
			List<SectionBasins> aoSectionsBasins = oSectionBasinsRepository.SelectAll(SectionBasins.class);

			for (ModelTable oModelTable : aoHydro.getModelsTable()) {

				System.out.println("OmirlDaemon - Hydro Model Table - Name" + oModelTable.getModelName() + " Code: " + oModelTable.getModelCode() );

				SerializeHydroModel(oModelTable.getModelName(), oModelTable.getModelCode(), oModelTable.getHasSubFolders(), aoSectionsBasins);
			}
		}
		catch(Exception oEx)
		{
			System.out.println("OmirlDaemon - Refresh Hydro Model Table Exception");
			oEx.toString();
		}

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
					Path oPath = Paths.get(sFullPath);
					if (Files.exists(oPath))
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

					int iLastIndex = aoPoints.size()-1;
					if (iLastIndex>0)
					{
						if (aoPoints.get(iLastIndex).getRefDate().getTime()>lNow)
						{
							//System.out.println("AGGIUSTATO END TIME CICLO CONVERSIONE!!!!!");
							lNow = aoPoints.get(iLastIndex).getRefDate().getTime();
						}
					}

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
	 * Converts Points from db to points for xml exchange format
	 * @param aoPoints
	 * @param oDataSerie
	 * @param dConversionFactor
	 */
	public void WindDataSeriePointToDataSerie(List<WindDataSeriePoint> aoPoints, DataSerie oDataSerie, double dConversionFactor, int iMinutesStep) {

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
							WindDataSeriePoint oDataSeriePoint = aoPoints.get(iPointIndex);

							if (oDataSeriePoint.getRefDate().getTime() == lTimeCycle) {
								adPoint[1] = new Double(oDataSeriePoint.getWindDir())*dConversionFactor;
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
	public List<SensorViewModel> SerializeSensorLast(String sName, StationLastDataRepository oLastRepo) {

		// One List for each sensor type
		List<SensorViewModel> aoSensoViewModel = new ArrayList<>();		

		try {
			List<SensorLastData> aoSensorLast = oLastRepo.selectByStationType("lastdata"+ sName);

			if (aoSensorLast != null) {

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

		return aoSensoViewModel;
	}


	/**
	 *  Serializes an XML file with the last list of all the stations with a webcam
	 */
	public void SerializeWebCamLayer()
	{
		try {
			StationAnagRepository oStationAnagRepository = new StationAnagRepository();

			// Get Webcam list
			List<StationAnag> aoWebCams = oStationAnagRepository.getListByType("webcam_every");

			if (aoWebCams!=null)
			{
				// One List for return
				List<SensorViewModel> aoSensoViewModel = new ArrayList<>();

				// For each station
				for (StationAnag oStation : aoWebCams) {
					try {
						// Create the view model
						SensorViewModel oSensorViewModel = new SensorViewModel();

						// Fill it
						if (oStation.getElevation() != null) oSensorViewModel.setAlt(oStation.getElevation().intValue());
						else oSensorViewModel.setAlt(-1);

						oSensorViewModel.setImgPath(GetLastWebCamImage(oStation.getStation_code()));
						oSensorViewModel.setLat(oStation.getLat()/100000.0);
						oSensorViewModel.setLon(oStation.getLon()/100000.0);
						if (oStation.getName()!=null) oSensorViewModel.setName(oStation.getName());
						if (oStation.getMunicipality()!=null)
						{
							oSensorViewModel.setMunicipality(oStation.getMunicipality());
						}
						else
						{
							oSensorViewModel.setMunicipality("-");
						}

						if (oSensorViewModel.getImgPath() != null && oSensorViewModel.getImgPath() != "")
						{
							Integer iHour = 0;
							Integer iMin = 0;
							try
							{
								oSensorViewModel.setOtherHtml("");
								String sTimeImage = oSensorViewModel.getImgPath().split("_")[2];
								iHour = Integer.valueOf(sTimeImage.substring(0, 2));
								iMin = Integer.valueOf(sTimeImage.substring(2, 4));
							}
							catch(Exception oEx){
	
							}
							Date oRefDate = new Date();
							oRefDate.setHours(iHour);
							oRefDate.setMinutes(iMin);
							oSensorViewModel.setRefDate(oRefDate);
						}
						oSensorViewModel.setShortCode(oStation.getStation_code());
						oSensorViewModel.setStationId(1);
						oSensorViewModel.setValue(0.0);
						oSensorViewModel.setIncrement(0);

						if (oSensorViewModel != null) {
							aoSensoViewModel.add(oSensorViewModel);
						}						
					}
					catch(Exception oInnerEx) {
						oInnerEx.printStackTrace();
					}
				}

				//Serialize Result
				Date oDate = new Date();
				String sName = "webcam";

				String sFullPath = getSubPath(m_oConfig.getFileRepositoryPath()+"/stations/" + sName,oDate);

				if (sFullPath != null)  {
					String sFileName = sName+m_oDateFormat.format(oDate)+".xml"; 
					SerializationUtils.serializeObjectToXML(sFullPath+"/"+sFileName, aoSensoViewModel);
				}				
			}
		}
		catch(Exception oEx) {
			oEx.printStackTrace();
		}
	}

	private String GetLastWebCamImage(String sWebCamCode)
	{
		// Get Repo Path
		String sBasePath = m_oConfig.getFileRepositoryPath();

		SimpleDateFormat oDateFormat = new SimpleDateFormat("yyyy/MM/dd");
		Date oActualDate = new Date();

		String sWebCamPath = sBasePath + "/stations/webcam";

		String sFullDir = sWebCamPath + "/" + oDateFormat.format(oActualDate);

		File oFolder = new File(sFullDir);
		
		// Find Subfolders
		String [] asSubFolders = oFolder.list();

		Boolean bFound = false;
		
		if (asSubFolders!=null)
		{
			for (String sSubFolder : asSubFolders) {
				File oTempFile = new File(sFullDir + "/" + sSubFolder + "/images");
				if (oTempFile.isDirectory())
				{
					if (sSubFolder.equals(sWebCamCode))
					{
						long liLastMod = Long.MIN_VALUE;

						File oLast = null;
						for (File file : oTempFile.listFiles()) {
							if (file.lastModified() > liLastMod) {
								oLast = file;
								liLastMod = file.lastModified();
							}
						}

						sFullDir = "img/webcam/" + oDateFormat.format(oActualDate) + "/" + sWebCamCode + "/images/" + oLast.getName();

						bFound = true;
					}
				}
			}
		}

		if (!bFound)
			sFullDir = "";
		return sFullDir;

	}

	/**
	 * Utility function that extends stations serialization for type-specific work
	 * @param aoSensorList
	 * @param sType
	 */
	public void SensorDataSpecialWork(List<SensorViewModel> aoSensorList, String sType) {
		if (sType == "wind") {

			ArrayList<Double> aoWindLimits = new ArrayList<>();
			aoWindLimits.add(0.555556);
			aoWindLimits.add(1.66667);
			aoWindLimits.add(3.05556);
			aoWindLimits.add(5.0);
			aoWindLimits.add(8.33333);
			aoWindLimits.add(10.8333);
			aoWindLimits.add(13.8889);
			aoWindLimits.add(16.9444);
			aoWindLimits.add(20.5556);
			aoWindLimits.add(24.1667);
			aoWindLimits.add(28.3333);
			aoWindLimits.add(100.0);
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
			//aoWindImages.add("img/sensors/wind_10.png");
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
		else if (sType == "boa")
		{
			for (SensorViewModel oViewModel : aoSensorList) {
				oViewModel.setValue(oViewModel.getValue()/10.0);
			}	
		}
	}


	public Boolean PathExist(String sFullPath)
	{
		File oXmlConfig = new File((String) sFullPath+"/legend.xml");

		return oXmlConfig.exists();
	}

	public HashMap<String, Integer> ReadSectionsLegend(String sFullPath) {
		HashMap<String, Integer> aoRetDictionary = new HashMap<>();

		try {

			File oXmlConfig = new File((String) sFullPath+"/legend.xml");

			if (oXmlConfig.exists()) {

				try {
					SAXBuilder oBuilder = new SAXBuilder();
					Document oDocument = oBuilder.build(oXmlConfig);

					Element oRoot = oDocument.getRootElement();

					if (oRoot != null) {
						List<Element> aoMarkers = (List<Element>) oRoot.getChildren("marker");

						if (aoMarkers != null) {
							for (Element oMarker : aoMarkers) {
								String sCode = oMarker.getAttribute("code").getValue();
								Integer iColor = 1000;

								if (oMarker.getAttribute("ALERT")!= null)
								{
									iColor = oMarker.getAttribute("ALERT").getIntValue();
								}

								if (!aoRetDictionary.containsKey(sCode)){
									aoRetDictionary.put(sCode, iColor);
								}
							}
						}
					}

				} catch (IOException oEx) {
					System.out.println("ReadSectionsLegend: " + oEx.getMessage());
				} 
				catch (JDOMException oEx) {
					System.out.println("ReadSectionsLegend: " + oEx.getMessage());
				} 
				catch (NumberFormatException oEx) {
					System.out.println("ReadSectionsLegend: " + oEx.getMessage());
				} 
				catch (Throwable oEx) {
					System.out.println("ReadSectionsLegend: " + oEx.getMessage());
				}
			}			
		}
		catch(Exception oEx) {
			oEx.toString();
		}

		return aoRetDictionary;
	}

	public String getSectionName(String sFullPath, String sCode) {
		String sSectionName = null;

		try {

			File oXmlConfig = new File((String) sFullPath+"/legend.xml");

			if (oXmlConfig.exists()) {

				try {
					SAXBuilder oBuilder = new SAXBuilder();
					Document oDocument = oBuilder.build(oXmlConfig);

					Element oRoot = oDocument.getRootElement();

					if (oRoot != null) {
						List<Element> aoMarkers = (List<Element>) oRoot.getChildren("marker");

						if (aoMarkers != null) {
							for (Element oMarker : aoMarkers) {
								if (oMarker.getAttribute("code").getValue().equals(sCode))
								{
									sSectionName = oMarker.getAttribute("name").getValue();
								}
							}
						}
					}

				} catch (IOException oEx) {
					System.out.println("getSectionName: " + oEx.getMessage());
				} 
				catch (JDOMException oEx) {
					System.out.println("getSectionName: " + oEx.getMessage());
				} 
				catch (NumberFormatException oEx) {
					System.out.println("getSectionName: " + oEx.getMessage());
				} 
				catch (Throwable oEx) {
					System.out.println("getSectionName: " + oEx.getMessage());
				}
			}			
		}
		catch(Exception oEx) {
			oEx.toString();
		}

		return sSectionName;
	}


	public void SerializeSectionsLayer(String sModelName, String sModelCode, String sFlagColumn, Boolean bHasSubFolders, Boolean bDisableOnMap, Boolean bTimeRewind, int iMaxDelay) {

		List<SectionViewModel> aoSectionsViewModel = new ArrayList<>();

		try {

			List<SectionAnag> aoSections = null;

			if (sFlagColumn.equals("boxplot") == false) {
				SectionAnagRepository oSectionsRepository = new SectionAnagRepository();
				aoSections = oSectionsRepository.selectByModel(sFlagColumn);
			}



			if (aoSections == null && sFlagColumn.equals("boxplot")) {

				System.out.println("BOXPLOT SECTION: ADDING PSEUDO SECTION");

				SectionAnag oSection = new SectionAnag();
				oSection.setCode("SintesiPrevisione");
				oSection.setBasin("BOXPLOT");
				oSection.setDistrict("GE");
				oSection.setLat(44.12);
				oSection.setLon(8.76);
				oSection.setElev(0.0);
				oSection.setName("BOXPLOT");
				oSection.setRiver("");
				oSection.setBasin_area(0.0);
				oSection.setWarn_area("");
				oSection.setBasin_class("");

				aoSections = new ArrayList<>();
				aoSections.add(oSection);
			}

			String sSubFolderVM = "";

			if (aoSections != null) {

				// Read Legend xml file
				Date oDate = new Date();

				String sFullPath = getSubPath(m_oConfig.getFileRepositoryPath()+"/sections/" + sModelCode,oDate);

				if (bHasSubFolders)
				{
					File oParentFolder = new File(sFullPath);

					String [] asSubFolders = oParentFolder.list();

					long lTimestamp = 0;
					String sNewFullPath = sFullPath;

					if (asSubFolders!=null)
					{
						for (String sSubFolder : asSubFolders) {
							File oTempFile = new File(sFullPath+"/"+sSubFolder);
							if (oTempFile.isDirectory())
							{
								if (oTempFile.getName().contains("features")) continue;

								if (oTempFile.lastModified()>lTimestamp)
								{
									lTimestamp = oTempFile.lastModified();
									sNewFullPath = sFullPath+"/"+sSubFolder;
									sSubFolderVM = sSubFolder;
								}
							}
						}
					}

					sFullPath = sNewFullPath;
				}

				boolean b2DaysOld = false;
				// exist today path?
				if (!PathExist(sFullPath))
				{
					// path not exist search on yesterday
					Date oYesterday = new Date( oDate.getTime() - 24 * 3600 * 1000);
					sFullPath = getSubPath(m_oConfig.getFileRepositoryPath()+"/sections/" + sModelCode,oYesterday);
					if (!PathExist(sFullPath))
						// not esists yesterday
						b2DaysOld = true;

				}

				HashMap<String, Integer> aoSectionsMap = ReadSectionsLegend(sFullPath);

				for (SectionAnag oSection : aoSections) {
					try {

						SectionViewModel oSectionViewModel = oSection.getSectionViewModel();

						oSectionViewModel.setModel(sModelName);
						oSectionViewModel.setSubFolder(sSubFolderVM);

						// no symbol
						if (b2DaysOld && bDisableOnMap)
							continue;

						if (aoSectionsMap.containsKey(oSectionViewModel.getCode())) {
							//Grey symbol
							if (b2DaysOld && !bDisableOnMap)
								oSectionViewModel.setColor(-1);
							else
								oSectionViewModel.setColor(aoSectionsMap.get(oSectionViewModel.getCode()));
						}
						else
						{
							oSectionViewModel.setColor(-1);
						}

						aoSectionsViewModel.add(oSectionViewModel);
					}
					catch(Exception oInnerEx) {
						oInnerEx.printStackTrace();
					}
				}

				sFullPath = getSubPath(m_oConfig.getFileRepositoryPath()+"/sections/" + sModelCode,oDate);

				if (sFullPath != null)  {

					sFullPath = sFullPath+"/features";
					File oFile = new File(sFullPath);
					oFile.mkdirs();
					oFile.setWritable(true, false);
					oFile.setReadable(true, false);

					String sFileName = sModelCode+m_oDateFormat.format(oDate)+".xml"; 
					SerializationUtils.serializeObjectToXML(sFullPath+"/"+sFileName, aoSectionsViewModel);
				}
			}
			else {
				System.out.println("OmirlDaemon ");
				System.out.println("OmirlDaemon - There was an error reading sections for " + sFlagColumn);
			}							
		}
		catch(Exception oEx) {
			oEx.printStackTrace();
		}
	}

	public void SerializeHydroModel(String sModelName, String sModelCode, Boolean bHasSubFolders, List<SectionBasins> aoSectionsBasins) {

		List<SectionBasinsViewModel> aoSectionBasinsViewModel = new ArrayList<>();

		//System.out.println("SerializeHydroModel COMINCIO ");

		try {

			String sSubFolderVM = "";

			if (aoSectionsBasins != null) {

				// Read Legend xml file
				Date oDate = new Date();

				String sFullPath = getSubPath(m_oConfig.getFileRepositoryPath()+"/sections/" + sModelCode,oDate);

				//System.out.println("SerializeHydroModel FULL 1 " + sFullPath);

				if (bHasSubFolders)
				{
					File oParentFolder = new File(sFullPath);

					String [] asSubFolders = oParentFolder.list();

					long lTimestamp = 0;
					String sNewFullPath = sFullPath;

					if (asSubFolders!=null)
					{
						for (String sSubFolder : asSubFolders) {
							File oTempFile = new File(sFullPath+"/"+sSubFolder);
							if (oTempFile.isDirectory())
							{
								if (oTempFile.getName().contains("features")) continue;

								if (oTempFile.lastModified()>lTimestamp)
								{
									lTimestamp = oTempFile.lastModified();
									sNewFullPath = sFullPath+"/"+sSubFolder;
									sSubFolderVM = sSubFolder;
								}
							}
						}
					}

					sFullPath = sNewFullPath;
				}

				//System.out.println("SerializeHydroModel FULL 2 " + sFullPath);

				HashMap<String, Integer> aoSectionsMap = ReadSectionsLegend(sFullPath);

				//System.out.println("SerializeHydroModel aoSectionsMap " + aoSectionsMap.size());

				for (SectionBasins oSectionBasins : aoSectionsBasins) {
					try {
						SectionBasinsViewModel oSectionBasinsViewModel = new SectionBasinsViewModel();
						oSectionBasinsViewModel.setBasinName(oSectionBasins.getName());
						oSectionBasinsViewModel.setOrderNumber(oSectionBasins.getOrdernumber());
						for (SectionBasinsCodes oCode : oSectionBasins.getSectionBasinsCodes()) {
							try{
								//new model
								SectionBasinsCodesViewModel oCodeViewModel = new SectionBasinsCodesViewModel(); 
								//get dictionary alert by station
								if (aoSectionsMap.containsKey(oCode.getSectioncode()))
								{
									oCodeViewModel.setColor(aoSectionsMap.get(oCode.getSectioncode()));
								}
								oCodeViewModel.setSectionCode(oCode.getSectioncode());
								oCodeViewModel.setSectionName(getSectionName(sFullPath, oCode.getSectioncode()));
								oCodeViewModel.setOrderNumber(oCode.getOrdernumber());

								oSectionBasinsViewModel.getSectionBasinsCodes().add(oCodeViewModel);
							}
							catch(Exception oInnerEx) {
								oInnerEx.printStackTrace();
							}
						}
						aoSectionBasinsViewModel.add(oSectionBasinsViewModel);			
					}
					catch(Exception oInnerEx) {
						oInnerEx.printStackTrace();
					}
				}

				sFullPath = getSubPath(m_oConfig.getFileRepositoryPath()+"/tables/hydro/" + sModelCode,oDate);

				//System.out.println("SerializeHydroModel sFullPath 3 " + sFullPath);

				if (sFullPath != null)  {

					//sFullPath = sFullPath+"/features";
					File oFile = new File(sFullPath);
					oFile.mkdirs();
					oFile.setWritable(true, false);
					oFile.setReadable(true, false);

					String sFileName = sModelCode+m_oDateFormat.format(oDate)+".xml";

					//System.out.println("SerializeHydroModel sFileName  " + sFileName);
					SerializationUtils.serializeObjectToXML(sFullPath+"/"+sFileName, aoSectionBasinsViewModel);
					//System.out.println("SAVED");
				}
			}
			else {
				System.out.println("OmirlDaemon ");
				System.out.println("OmirlDaemon - There was an error reading sections ");
			}							
		}
		catch(Exception oEx) {
			oEx.printStackTrace();
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
			else {
				oBasePathDir.setReadable(true, false);
				oBasePathDir.setWritable(true,false);
			}
		}

		String sFullDir = sBasePath + "/" + oDateFormat.format(oDate);

		File oFullPathDir = new File(sFullDir);
		if (!oFullPathDir.exists()) {
			if (!oFullPathDir.mkdirs()) return null;
			else {
				oFullPathDir.setReadable(true,false);
				oFullPathDir.setWritable(true,false);
			}
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

	void InitSensorValueTables()
	{
		m_oRainValuesTable.setSensorTye("rain1h");
		m_oTempValuesTable.setSensorTye("temp");
		m_oHydroValuesTable.setSensorTye("idro");
		m_oIgroValuesTable.setSensorTye("igro");
		m_oRadioValuesTable.setSensorTye("radio");
		m_oLeafsValuesTable.setSensorTye("leafs");
		m_oBattValuesTable.setSensorTye("batt");
		m_oPressValuesTable.setSensorTye("press");
		m_oSnowValuesTable.setSensorTye("snow");
		m_oBoaValuesTable.setSensorTye("boa");
		m_oWindValuesTable.setSensorTye("wind");

		ClearSensorValueTables();
	}

	void ClearSensorValueTables()
	{
		m_oRainValuesTable.setTableRows(new ArrayList<SensorValueRowViewModel>());
		m_oTempValuesTable.setTableRows(new ArrayList<SensorValueRowViewModel>());
		m_oHydroValuesTable.setTableRows(new ArrayList<SensorValueRowViewModel>());
		m_oIgroValuesTable.setTableRows(new ArrayList<SensorValueRowViewModel>());
		m_oRadioValuesTable.setTableRows(new ArrayList<SensorValueRowViewModel>());
		m_oLeafsValuesTable.setTableRows(new ArrayList<SensorValueRowViewModel>());
		m_oBattValuesTable.setTableRows(new ArrayList<SensorValueRowViewModel>());
		m_oPressValuesTable.setTableRows(new ArrayList<SensorValueRowViewModel>());
		m_oSnowValuesTable.setTableRows(new ArrayList<SensorValueRowViewModel>());
		m_oBoaValuesTable.setTableRows(new ArrayList<SensorValueRowViewModel>());
		m_oWindValuesTable.setTableRows(new ArrayList<SensorValueRowViewModel>());
	}

	SensorValueRowViewModel GetSensorValueRow(SensorViewModel oSensorVM)
	{
		if (oSensorVM==null) return null;

		SensorValueRowViewModel oValueRow = new SensorValueRowViewModel();
		oValueRow.setCode(oSensorVM.getShortCode());
		oValueRow.setLast(oSensorVM.getValue());
		oValueRow.setMin(oSensorVM.getValue());
		oValueRow.setMax(oSensorVM.getValue());

		for (StationAnag oStation : m_aoAllStations) {
			if (oStation.getStation_code().equals(oValueRow.getCode()) ) {
				oValueRow.setArea(oStation.getWarn_area());
				oValueRow.setBasin(oStation.getBasin());
				oValueRow.setDistrict(oStation.getDistrict());
				oValueRow.setMunicipality(oStation.getMunicipality());
				oValueRow.setName(oStation.getName());
				oValueRow.setUnderbasin(oStation.getRiver());

				break;
			}
		}

		return oValueRow;
	}


	public void SerializeSensorsValuesTable(SensorValueTableViewModel oTable, List<SensorViewModel> oSensorVMList, String sSensorType)
	{
		try  {
			if (oTable.getTableRows().size()==0)
			{
				for (SensorViewModel oSensorVM : oSensorVMList) {
					SensorValueRowViewModel oValueRow = GetSensorValueRow(oSensorVM);

					oTable.getTableRows().add(oValueRow);
				}
			}
			else 
			{
				for (SensorViewModel oSensorVM : oSensorVMList) {
					SensorValueRowViewModel oValueRow = oTable.getTableRowByCode(oSensorVM.getShortCode());

					if (oValueRow!=null)
					{
						oValueRow.setLast(oSensorVM.getValue());
						if (oSensorVM.getValue()<oValueRow.getMin()) oValueRow.setMin(oSensorVM.getValue());
						if (oSensorVM.getValue()>oValueRow.getMax()) oValueRow.setMax(oSensorVM.getValue());									
					}
				}
			}


			Date oDate = new Date();

			String sFullPath = getSubPath(m_oConfig.getFileRepositoryPath()+"/tables/sensorvalues/" + sSensorType,oDate);

			if (sFullPath != null)  {
				String sFileName = sSensorType+m_oDateFormat.format(oDate)+".xml"; 
				SerializationUtils.serializeObjectToXML(sFullPath+"/"+sFileName, oTable);
			}

		}
		catch(Exception oEx)
		{

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



	public void RefreshGallery() {

		// Get configured models
		List<ModelGalleryInfo> aoModels = m_oConfig.getModelsGallery();

		// Get Repo Path
		String sBasePath = m_oConfig.getFileRepositoryPath();

		SimpleDateFormat oDateFormat = new SimpleDateFormat("yyyy/MM/dd");
		Date oActualDate = new Date();

		String sGalleryPath = sBasePath + "/gallery";

		String sFullDir = sGalleryPath + "/" + oDateFormat.format(oActualDate);

		File oFolder = new File(sFullDir);

		// Find Subfolders
		String [] asSubFolders = oFolder.list();

		long lTimestamp = 0;
		String sNewFullPath = sFullDir;
		String sHourFolder = "";

		if (asSubFolders!=null)
		{
			for (String sSubFolder : asSubFolders) {
				File oTempFile = new File(sFullDir+"/"+sSubFolder);
				if (oTempFile.isDirectory())
				{
					if (oTempFile.lastModified()>lTimestamp)
					{
						// This is the last one!
						lTimestamp = oTempFile.lastModified();
						sNewFullPath = sFullDir+"/"+sSubFolder;
						sHourFolder = sSubFolder;
					}
				}
			}
		}

		sFullDir = sNewFullPath;

		int iHourFolder = 0;

		try {
			iHourFolder = Integer.parseInt(sHourFolder);
		}
		catch(Exception oEx)
		{
			oEx.printStackTrace();
		}

		// For each model
		for (ModelGalleryInfo oGalleryInfo : aoModels) {

			//  For each variable
			for (ModelImageInfo oVariable : oGalleryInfo.getVariables()) {

				// Generate file name 
				String sFileFilter = oVariable.getCodeNumber();
				sFileFilter += "_";
				sFileFilter += oGalleryInfo.getCodeModel();
				sFileFilter += "_";
				sFileFilter+=oVariable.getCodeVariable();

				if (oVariable.isUseAt())
				{
					sFileFilter+="@";
				}
				else 
				{
					sFileFilter+="_";
				}
				sFileFilter+=oVariable.getCodeSubVariable();
				// Get Images from folder
				File [] aoImages = OmirlDaemon.listFilesStartingWith(sFullDir, sFileFilter);
				if (aoImages == null) 
				{
					System.out.println("Gallery Code " + oGalleryInfo.getModel() +" Varialbe " + oVariable.getCodeVariable() + " Sub " + oVariable.getSubVarialbe() + ": no images");
					continue;
				}			
				// Create View Model
				ModelGallery oGalleryVM = new ModelGallery();
				oGalleryVM.setModel(oGalleryInfo.getModel());
				oGalleryVM.setVariable(oVariable.getVariable());
				oGalleryVM.setSubVarialbe(oVariable.getSubVarialbe());
				DateTime oDate = new DateTime(oActualDate.getYear(), oActualDate.getMonth()+1, oActualDate.getDate(),iHourFolder , 0);
				oGalleryVM.setRefDateMin(oDate.toDate());



				String sImagesPath = "img/gallery/" + oDateFormat.format(oActualDate);

				if (sHourFolder.isEmpty() == false)
				{
					sImagesPath+="/" + sHourFolder;
				}

				for (File oImage : aoImages) {
					ModelImage oImageVM = new ModelImage();
					oImageVM.setDescription("");
					oImageVM.setImageLink(sImagesPath+"/" + oImage.getName());
					oGalleryVM.getImages().add(oImageVM);
				}

				String sOutFileName = oGalleryInfo.getCodeModel()+oVariable.getCodeVariable()+oVariable.getCodeSubVariable()+".xml";

				try {
					SerializationUtils.serializeObjectToXML(sFullDir+"/"+sOutFileName, oGalleryVM);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
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

		MaxHydroAlertZone oMaxHydroAlertZone = new MaxHydroAlertZone();
		oMaxHydroAlertZone.setName("Marittimi di ponente");
		oMaxHydroAlertZone.setDescription("MARITTIMI_PONENTE");
		oMaxHydroAlertZone.setZone("A");
		oConfig.getMaxHydroAlertZone().add(oMaxHydroAlertZone);
		oMaxHydroAlertZone = new MaxHydroAlertZone();
		oMaxHydroAlertZone.setName("Marittimi di centro");
		oMaxHydroAlertZone.setDescription("MARITTIMI_CENTRO");
		oMaxHydroAlertZone.setZone("B");
		oConfig.getMaxHydroAlertZone().add(oMaxHydroAlertZone);
		oMaxHydroAlertZone = new MaxHydroAlertZone();
		oMaxHydroAlertZone.setName("Marittimi di levante");
		oMaxHydroAlertZone.setDescription("MARITTIMI_LEVANTE");
		oMaxHydroAlertZone.setZone("C");
		oConfig.getMaxHydroAlertZone().add(oMaxHydroAlertZone);
		oMaxHydroAlertZone = new MaxHydroAlertZone();
		oMaxHydroAlertZone.setName("Padani di ponente");
		oMaxHydroAlertZone.setDescription("PADANI_PONENTE");
		oMaxHydroAlertZone.setZone("D");
		oConfig.getMaxHydroAlertZone().add(oMaxHydroAlertZone);
		oMaxHydroAlertZone = new MaxHydroAlertZone();
		oMaxHydroAlertZone.setName("Padani di levante");
		oMaxHydroAlertZone.setDescription("PADANI_LEVANTE");
		oMaxHydroAlertZone.setZone("E");
		oConfig.getMaxHydroAlertZone().add(oMaxHydroAlertZone);
		oMaxHydroAlertZone = new MaxHydroAlertZone();
		oMaxHydroAlertZone.setName("C+ Magra");
		oMaxHydroAlertZone.setDescription("C+_MAGRA");
		oMaxHydroAlertZone.setZone("C+");
		oConfig.getMaxHydroAlertZone().add(oMaxHydroAlertZone);
		oMaxHydroAlertZone = new MaxHydroAlertZone();
		oMaxHydroAlertZone.setName("C- Magra");
		oMaxHydroAlertZone.setDescription("C-_MAGRA");
		oMaxHydroAlertZone.setZone("C-");
		oConfig.getMaxHydroAlertZone().add(oMaxHydroAlertZone);
		oMaxHydroAlertZone = new MaxHydroAlertZone();
		oMaxHydroAlertZone.setName("Magra");
		oMaxHydroAlertZone.setDescription("M_MAGRA");
		oMaxHydroAlertZone.setZone("M");
		oConfig.getMaxHydroAlertZone().add(oMaxHydroAlertZone);

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


		SectionLayerInfo oSectionLayerInfo = new SectionLayerInfo();
		oSectionLayerInfo.setModelCode("piccolibacini");
		oSectionLayerInfo.setModelName("Piccoli Bacini");
		oSectionLayerInfo.setFlagColumn("piccoli_bacini");
		oSectionLayerInfo.setHasSubFolders(true);

		oConfig.getSectionLayersInfo().add(oSectionLayerInfo);

		oSectionLayerInfo = new SectionLayerInfo();
		oSectionLayerInfo.setModelCode("monitmagraq");
		oSectionLayerInfo.setModelName("Monitoraggio Magra Q");
		oSectionLayerInfo.setFlagColumn("catena_magra");
		oSectionLayerInfo.setHasSubFolders(false);

		oConfig.getSectionLayersInfo().add(oSectionLayerInfo);


		oConfig.setEnableCharts(true);
		oConfig.setEnableMaps(true);
		oConfig.setEnableSensorLast(true);
		oConfig.setEnableSfloc(true);
		oConfig.setEnableSummaryTable(true);
		oConfig.setEnableValueTable(true);
		oConfig.setEnableWebcam(true);
		oConfig.setEnableMaxTable(true);

		MaxTableInfo oAlertMaxTableInfo  = new MaxTableInfo();

		oAlertMaxTableInfo.setTableName("Zone di Allerta");
		oAlertMaxTableInfo.getRows().add("A");
		oAlertMaxTableInfo.getRows().add("B");
		oAlertMaxTableInfo.getRows().add("C");
		oAlertMaxTableInfo.getRows().add("C+");
		oAlertMaxTableInfo.getRows().add("C-");
		oAlertMaxTableInfo.getRows().add("D");
		oAlertMaxTableInfo.getRows().add("E");

		oAlertMaxTableInfo.getRowFilters().add("A");
		oAlertMaxTableInfo.getRowFilters().add("B");
		oAlertMaxTableInfo.getRowFilters().add("C");
		oAlertMaxTableInfo.getRowFilters().add("C+");
		oAlertMaxTableInfo.getRowFilters().add("C-");
		oAlertMaxTableInfo.getRowFilters().add("D");
		oAlertMaxTableInfo.getRowFilters().add("E");

		oAlertMaxTableInfo.getColumns().add("rain_05m");
		oAlertMaxTableInfo.getColumns().add("rain_15m");
		oAlertMaxTableInfo.getColumns().add("rain_30m");
		oAlertMaxTableInfo.getColumns().add("rain_1h");
		oAlertMaxTableInfo.getColumns().add("rain_3h");
		oAlertMaxTableInfo.getColumns().add("rain_6h");
		oAlertMaxTableInfo.getColumns().add("rain_12h");
		oAlertMaxTableInfo.getColumns().add("rain_24h");

		oAlertMaxTableInfo.getMethodCodes().add("M5");
		oAlertMaxTableInfo.getMethodCodes().add("M15");
		oAlertMaxTableInfo.getMethodCodes().add("M30");
		oAlertMaxTableInfo.getMethodCodes().add("H1");
		oAlertMaxTableInfo.getMethodCodes().add("H3");
		oAlertMaxTableInfo.getMethodCodes().add("H6");
		oAlertMaxTableInfo.getMethodCodes().add("H12");
		oAlertMaxTableInfo.getMethodCodes().add("H24");

		oAlertMaxTableInfo.getThreshold1().add(4.0);
		oAlertMaxTableInfo.getThreshold1().add(6.0);
		oAlertMaxTableInfo.getThreshold1().add(10.0);
		oAlertMaxTableInfo.getThreshold1().add(15.0);
		oAlertMaxTableInfo.getThreshold1().add(30.0);
		oAlertMaxTableInfo.getThreshold1().add(40.0);
		oAlertMaxTableInfo.getThreshold1().add(50.0);
		oAlertMaxTableInfo.getThreshold1().add(60.0);


		oAlertMaxTableInfo.getThreshold2().add(8.0);
		oAlertMaxTableInfo.getThreshold2().add(12.0);
		oAlertMaxTableInfo.getThreshold2().add(20.0);
		oAlertMaxTableInfo.getThreshold2().add(30.0);
		oAlertMaxTableInfo.getThreshold2().add(60.0);
		oAlertMaxTableInfo.getThreshold2().add(80.0);
		oAlertMaxTableInfo.getThreshold2().add(100.0);
		oAlertMaxTableInfo.getThreshold2().add(120.0);

		oAlertMaxTableInfo.setThreshold1Style("max-table-yellow-cell");
		oAlertMaxTableInfo.setThreshold2Style("max-table-red-cell");

		oConfig.setAlertMaxTable(oAlertMaxTableInfo);

		MaxTableInfo oDistrictMaxTableInfo  = new MaxTableInfo();

		oDistrictMaxTableInfo.setTableName("Province");
		oDistrictMaxTableInfo.getRows().add("Genova");
		oDistrictMaxTableInfo.getRows().add("La Spezia");
		oDistrictMaxTableInfo.getRows().add("Savona");
		oDistrictMaxTableInfo.getRows().add("Imperia");

		oDistrictMaxTableInfo.getRowFilters().add("GE");
		oDistrictMaxTableInfo.getRowFilters().add("SP");
		oDistrictMaxTableInfo.getRowFilters().add("SV");
		oDistrictMaxTableInfo.getRowFilters().add("IM");		

		oDistrictMaxTableInfo.getColumns().add("rain_05m");
		oDistrictMaxTableInfo.getColumns().add("rain_15m");
		oDistrictMaxTableInfo.getColumns().add("rain_30m");
		oDistrictMaxTableInfo.getColumns().add("rain_1h");
		oDistrictMaxTableInfo.getColumns().add("rain_3h");
		oDistrictMaxTableInfo.getColumns().add("rain_6h");
		oDistrictMaxTableInfo.getColumns().add("rain_12h");
		oDistrictMaxTableInfo.getColumns().add("rain_24h");

		oDistrictMaxTableInfo.getMethodCodes().add("M5");
		oDistrictMaxTableInfo.getMethodCodes().add("M15");
		oDistrictMaxTableInfo.getMethodCodes().add("M30");
		oDistrictMaxTableInfo.getMethodCodes().add("H1");
		oDistrictMaxTableInfo.getMethodCodes().add("H3");
		oDistrictMaxTableInfo.getMethodCodes().add("H6");
		oDistrictMaxTableInfo.getMethodCodes().add("H12");
		oDistrictMaxTableInfo.getMethodCodes().add("H24");

		oDistrictMaxTableInfo.getThreshold1().add(4.0);
		oDistrictMaxTableInfo.getThreshold1().add(6.0);
		oDistrictMaxTableInfo.getThreshold1().add(10.0);
		oDistrictMaxTableInfo.getThreshold1().add(15.0);
		oDistrictMaxTableInfo.getThreshold1().add(30.0);
		oDistrictMaxTableInfo.getThreshold1().add(40.0);
		oDistrictMaxTableInfo.getThreshold1().add(50.0);
		oDistrictMaxTableInfo.getThreshold1().add(60.0);


		oDistrictMaxTableInfo.getThreshold2().add(8.0);
		oDistrictMaxTableInfo.getThreshold2().add(12.0);
		oDistrictMaxTableInfo.getThreshold2().add(20.0);
		oDistrictMaxTableInfo.getThreshold2().add(30.0);
		oDistrictMaxTableInfo.getThreshold2().add(60.0);
		oDistrictMaxTableInfo.getThreshold2().add(80.0);
		oDistrictMaxTableInfo.getThreshold2().add(100.0);
		oDistrictMaxTableInfo.getThreshold2().add(120.0);

		oDistrictMaxTableInfo.setThreshold1Style("max-table-yellow-cell");
		oDistrictMaxTableInfo.setThreshold2Style("max-table-red-cell");


		oConfig.setDistrictMaxTable(oDistrictMaxTableInfo);

		oConfig.setGeoServerAddress("http://127.0.0.1:8888/geoserver/");
		oConfig.setGeoServerDataFolder("C:\\Program Files (x86)\\GeoServer 2.3.2\\data_dir\\data");
		oConfig.setGeoServerPassword("geoserver");
		oConfig.setGeoServerUser("admin");


		MapInfo oMapInfo = new MapInfo();
		oMapInfo.setCode("rainfall10m");
		oMapInfo.setStyle("raster");
		oMapInfo.setTiff(true);

		oConfig.getMapsInfo().add(oMapInfo);


		oMapInfo = new MapInfo();
		oMapInfo.setCode("rainfall1d");
		oMapInfo.setStyle("raster");
		oMapInfo.setTiff(true);

		oConfig.getMapsInfo().add(oMapInfo);


		oMapInfo = new MapInfo();
		oMapInfo.setCode("rainfall30m");
		oMapInfo.setStyle("raster");
		oMapInfo.setTiff(true);

		oConfig.getMapsInfo().add(oMapInfo);

		oMapInfo = new MapInfo();
		oMapInfo.setCode("rainfall6h");
		oMapInfo.setStyle("raster");
		oMapInfo.setTiff(true);

		oConfig.getMapsInfo().add(oMapInfo);

		oMapInfo = new MapInfo();
		oMapInfo.setCode("rainfall12h");
		oMapInfo.setStyle("raster");
		oMapInfo.setTiff(true);

		oConfig.getMapsInfo().add(oMapInfo);

		oMapInfo = new MapInfo();
		oMapInfo.setCode("rainfall1h");
		oMapInfo.setStyle("raster");
		oMapInfo.setTiff(true);

		oConfig.getMapsInfo().add(oMapInfo);


		oMapInfo = new MapInfo();
		oMapInfo.setCode("rainfall3h");
		oMapInfo.setStyle("raster");
		oMapInfo.setTiff(true);

		oConfig.getMapsInfo().add(oMapInfo);

		oMapInfo = new MapInfo();
		oMapInfo.setCode("tempMean");
		oMapInfo.setStyle("raster");
		oMapInfo.setTiff(true);

		oConfig.getMapsInfo().add(oMapInfo);

		oMapInfo = new MapInfo();
		oMapInfo.setCode("tempTheta");
		oMapInfo.setStyle("raster");
		oMapInfo.setTiff(true);

		oConfig.getMapsInfo().add(oMapInfo);

		oMapInfo = new MapInfo();
		oMapInfo.setCode("tempMax");
		oMapInfo.setStyle("raster");
		oMapInfo.setTiff(true);

		oConfig.getMapsInfo().add(oMapInfo);

		oMapInfo = new MapInfo();
		oMapInfo.setCode("tempMin");
		oMapInfo.setStyle("raster");
		oMapInfo.setTiff(true);

		oConfig.getMapsInfo().add(oMapInfo);

		ModelGalleryInfo oGalleryInfo = new ModelGalleryInfo();
		oGalleryInfo.setCodeModel("bo10ar");
		oGalleryInfo.setModel("Bolam Europe");

		ModelImageInfo oImageInfo = new ModelImageInfo();
		oImageInfo.setCodeNumber("08");
		oImageInfo.setCodeVariable("TPrec12");
		oImageInfo.setVariable("12h Total Precipitation");
		oImageInfo.setUseAt(false);
		oImageInfo.setCodeSubVariable("GH_TCK_Europe");

		oGalleryInfo.getVariables().add(oImageInfo);


		oImageInfo = new ModelImageInfo();
		oImageInfo.setCodeNumber("09");
		oImageInfo.setCodeVariable("MSLP");
		oImageInfo.setVariable("Mean Sea Level Pressure");
		oImageInfo.setUseAt(false);
		oImageInfo.setCodeSubVariable("_Europe");

		oGalleryInfo.getVariables().add(oImageInfo);

		oConfig.getModelsGallery().add(oGalleryInfo);

		//HydroModelTables
		ModelTable oModelTable = new ModelTable();
		oModelTable.setHasSubFolders(true);
		oModelTable.setModelCode("CODE 1");
		ModelTable oModelTable1 = new ModelTable();
		oModelTable1.setHasSubFolders(false);
		oModelTable1.setModelCode("CODE 2");
		oConfig.getHydroModelTables().getModelsTable().add(oModelTable);
		oConfig.getHydroModelTables().getModelsTable().add(oModelTable1);

		try {
			SerializationUtils.serializeObjectToXML("C:\\temp\\Omirl\\OmirlDaemonConfigSAMPLE.xml", oConfig);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void Test() {
		try {


			File oFileTest = lastFileByName("C:\\temp\\Omirl\\Files\\maps\\rainfall1d\\2016\\01\\07", "tif");

			if (oFileTest != null) System.out.println(oFileTest.getAbsolutePath()); 


			StationDataRepository oStationDataRepositorySum = new StationDataRepository();

			SummaryInfoEntity oZoneTest = oStationDataRepositorySum.getAlertZoneMaxTemperatureSummaryInfo("A", new Date());
			SummaryInfoEntity oZoneMin =  oStationDataRepositorySum.getAlertZoneMinTemperatureSummaryInfo("A", new Date());

			SummaryInfoEntity oZoneTest2 = oStationDataRepositorySum.getAlertZoneMaxTemperatureSummaryInfo("C+", new Date());
			SummaryInfoEntity oZoneMin2 =  oStationDataRepositorySum.getAlertZoneMinTemperatureSummaryInfo("C+", new Date());


			SummaryInfoEntity oSummaryTest = oStationDataRepositorySum.getAlertZoneMaxTemperatureSummaryInfo("GE", new Date());
			SummaryInfoEntity oSummaryMin =  oStationDataRepositorySum.getAlertZoneMinTemperatureSummaryInfo("GE", new Date());


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

	public static File lastFileModified(String dir, String sExtension) {
		File oDir = new File(dir);

		if (!oDir.exists()) {
			System.out.println("OMIRL.lastFileModified: folder does not exists " + dir);
			return null;
		}

		final String sExt = sExtension;

		File[] aoFiles = oDir.listFiles(new FileFilter() {			
			public boolean accept(File file) {

				if (file.isFile())
				{
					if (file.getName().endsWith(sExt))
					{
						return true;
					}
				}
				return false;
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


	public static File lastFileByName(String dir, String sExtension) {
		File oDir = new File(dir);

		if (!oDir.exists()) {
			System.out.println("OMIRL.lastFileModified: folder does not exists " + dir);
			return null;
		}

		final String sExt = sExtension;

		File[] aoFiles = oDir.listFiles(new FileFilter() {			
			public boolean accept(File file) {

				if (file.isFile())
				{
					if (file.getName().endsWith(sExt))
					{
						return true;
					}
				}
				return false;
			}
		});

		Arrays.sort(aoFiles);

		File oChoise = null;

		if (aoFiles!=null)
		{
			if (aoFiles.length>0)
			{
				oChoise = aoFiles[aoFiles.length-1];
			}
		}

		return oChoise;
	}

	public static File [] listFilesStartingWith(String dir, String sStart) {
		File oDir = new File(dir);

		if (!oDir.exists()) {
			System.out.println("OMIRL.lastFileModified: folder does not exists " + dir);
			return null;
		}

		final String sFilter = sStart;

		File[] aoFiles = oDir.listFiles(new FileFilter() {			
			public boolean accept(File file) {

				if (file.isFile())
				{
					if (file.getName().startsWith(sFilter))
					{
						return true;
					}
				}
				return false;
			}
		});

		return aoFiles;
	}


	private static void TestGeoTiff() {
		GeoServerDataManager2 oManager = new GeoServerDataManager2("http://93.62.155.217:8080/geoserver/", "", "admin", "geo4Omirl");
		try {
			oManager.AggregateLayer("c:\\temp\\Omirl\\aggrega\\rainfall30d_std_0000.tiff", "C:\\temp\\Omirl\\aggrega\\comuni_wgs84\\comuni_wgs84.shp", "C:\\temp\\Omirl\\aggrega\\Output\\output.shp", "NOME_COM", "VALUE", -999000000.0f);
			oManager.addShapeLayer("output", "omirl", "OmirlRain30d","omirlaggregations");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
}
