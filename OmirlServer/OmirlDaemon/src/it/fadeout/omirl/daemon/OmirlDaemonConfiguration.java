package it.fadeout.omirl.daemon;

import it.fadeout.omirl.business.AnagTableInfo;
import it.fadeout.omirl.business.ChartInfo;
import it.fadeout.omirl.business.HydroModelTables;
import it.fadeout.omirl.business.MapInfo;
import it.fadeout.omirl.business.MaxHydroAlertZone;
import it.fadeout.omirl.business.MaxTableInfo;
import it.fadeout.omirl.business.ModelGalleryInfo;
import it.fadeout.omirl.business.SectionLayerInfo;
import it.fadeout.omirl.business.WindSummaryConfiguration;

import java.util.ArrayList;

public class OmirlDaemonConfiguration {
	String fileRepositoryPath;
	
	int minutesPolling = 1;
	
	int chartTimeRangeDays = 15;
	
	int sflocTimeRangeDays = 6;
	
	int sessioneTimeout = 20;

	ArrayList<ChartInfo> chartsInfo = new ArrayList<>();
	
	ArrayList<AnagTableInfo> anagTablesInfo = new ArrayList<>();
	
	private ArrayList<MaxHydroAlertZone> maxHydroAlertZone = new ArrayList<>();
	
	WindSummaryConfiguration windSummaryInfo = new WindSummaryConfiguration();
	
	ArrayList<SectionLayerInfo> sectionLayersInfo = new ArrayList<>();
	
	ArrayList<MapInfo> mapsInfo = new ArrayList<>();
	
	ArrayList<ModelGalleryInfo> modelsGallery = new ArrayList<ModelGalleryInfo>();
	
	private HydroModelTables HydroModelTables = new HydroModelTables();
	
	boolean enableCharts;
	boolean enableSensorLast;
	boolean enableWebcam;
	boolean enableSfloc;
	boolean enableMaps;
	boolean enableSummaryTable;
	boolean enableValueTable;
	boolean enableGallery;
	
	boolean enableThreshold;
	boolean enableStationsTable;
	boolean enableSectionsLayer;
	boolean enableDailyTask;
	boolean enableMaxTable;
	private boolean enableHydroModel;
	private boolean enableMaxHydroAlertZones;
	
	MaxTableInfo alertMaxTable;
	MaxTableInfo districtMaxTable;
	
	int circleBufferDays = 1;
	
	private int dbBufferDataDays = 60;
	
	String geoServerAddress;
	String geoServerUser;
	String geoServerPassword;
	String geoServerDataFolder;
	String geoServerWorkspace;

	public String getFileRepositoryPath() {
		return fileRepositoryPath;
	}

	public void setFileRepositoryPath(String fileRepositoryPath) {
		this.fileRepositoryPath = fileRepositoryPath;
	}

	public int getMinutesPolling() {
		return minutesPolling;
	}

	public void setMinutesPolling(int minutesPolling) {
		this.minutesPolling = minutesPolling;
	}


	public int getChartTimeRangeDays() {
		return chartTimeRangeDays;
	}

	public void setChartTimeRangeDays(int chartTimeRangeDays) {
		this.chartTimeRangeDays = chartTimeRangeDays;
	}

	public ArrayList<ChartInfo> getChartsInfo() {
		return chartsInfo;
	}

	public void setChartsInfo(ArrayList<ChartInfo> chartsInfo) {
		this.chartsInfo = chartsInfo;
	}

	public ArrayList<AnagTableInfo> getAnagTablesInfo() {
		return anagTablesInfo;
	}

	public void setAnagTablesInfo(ArrayList<AnagTableInfo> anagTablesInfo) {
		this.anagTablesInfo = anagTablesInfo;
	}

	public WindSummaryConfiguration getWindSummaryInfo() {
		return windSummaryInfo;
	}

	public void setWindSummaryInfo(WindSummaryConfiguration windSummaryInfo) {
		this.windSummaryInfo = windSummaryInfo;
	}

	public int getSflocTimeRangeDays() {
		return sflocTimeRangeDays;
	}

	public void setSflocTimeRangeDays(int sflocTimeRangeDays) {
		this.sflocTimeRangeDays = sflocTimeRangeDays;
	}

	public int getSessioneTimeout() {
		return sessioneTimeout;
	}

	public void setSessioneTimeout(int sessioneTimeout) {
		this.sessioneTimeout = sessioneTimeout;
	}

	public ArrayList<SectionLayerInfo> getSectionLayersInfo() {
		return sectionLayersInfo;
	}

	public void setSectionLayersInfo(ArrayList<SectionLayerInfo> sectionLayersInfo) {
		this.sectionLayersInfo = sectionLayersInfo;
	}

	public boolean isEnableCharts() {
		return enableCharts;
	}

	public void setEnableCharts(boolean enableCharts) {
		this.enableCharts = enableCharts;
	}

	public boolean isEnableSensorLast() {
		return enableSensorLast;
	}

	public void setEnableSensorLast(boolean enableSensorLast) {
		this.enableSensorLast = enableSensorLast;
	}

	public boolean isEnableWebcam() {
		return enableWebcam;
	}

	public void setEnableWebcam(boolean enableWebcam) {
		this.enableWebcam = enableWebcam;
	}

	public boolean isEnableSfloc() {
		return enableSfloc;
	}

	public void setEnableSfloc(boolean enableSfloc) {
		this.enableSfloc = enableSfloc;
	}

	public boolean isEnableMaps() {
		return enableMaps;
	}

	public void setEnableMaps(boolean enableMaps) {
		this.enableMaps = enableMaps;
	}

	public boolean isEnableSummaryTable() {
		return enableSummaryTable;
	}

	public void setEnableSummaryTable(boolean enableSummaryTable) {
		this.enableSummaryTable = enableSummaryTable;
	}

	public boolean isEnableValueTable() {
		return enableValueTable;
	}

	public void setEnableValueTable(boolean enableValueTable) {
		this.enableValueTable = enableValueTable;
	}

	public boolean isEnableThreshold() {
		return enableThreshold;
	}

	public void setEnableThreshold(boolean enableThreshold) {
		this.enableThreshold = enableThreshold;
	}

	public boolean isEnableStationsTable() {
		return enableStationsTable;
	}

	public void setEnableStationsTable(boolean enableStationsTable) {
		this.enableStationsTable = enableStationsTable;
	}

	public boolean isEnableSectionsLayer() {
		return enableSectionsLayer;
	}

	public void setEnableSectionsLayer(boolean enableSectionsLayer) {
		this.enableSectionsLayer = enableSectionsLayer;
	}

	public boolean isEnableDailyTask() {
		return enableDailyTask;
	}

	public void setEnableDailyTask(boolean enableDailyTask) {
		this.enableDailyTask = enableDailyTask;
	}

	public int getCircleBufferDays() {
		return circleBufferDays;
	}

	public void setCircleBufferDays(int circleBufferDays) {
		this.circleBufferDays = circleBufferDays;
	}

	public boolean isEnableMaxTable() {
		return enableMaxTable;
	}

	public void setEnableMaxTable(boolean enableMaxTable) {
		this.enableMaxTable = enableMaxTable;
	}

	public MaxTableInfo getAlertMaxTable() {
		return alertMaxTable;
	}

	public void setAlertMaxTable(MaxTableInfo alertMaxTable) {
		this.alertMaxTable = alertMaxTable;
	}

	public MaxTableInfo getDistrictMaxTable() {
		return districtMaxTable;
	}

	public void setDistrictMaxTable(MaxTableInfo districtMaxTable) {
		this.districtMaxTable = districtMaxTable;
	}

	public String getGeoServerAddress() {
		return geoServerAddress;
	}

	public void setGeoServerAddress(String geoServerAddress) {
		this.geoServerAddress = geoServerAddress;
	}

	public String getGeoServerUser() {
		return geoServerUser;
	}

	public void setGeoServerUser(String geoServerUser) {
		this.geoServerUser = geoServerUser;
	}

	public String getGeoServerPassword() {
		return geoServerPassword;
	}

	public void setGeoServerPassword(String geoServerPassword) {
		this.geoServerPassword = geoServerPassword;
	}

	public String getGeoServerDataFolder() {
		return geoServerDataFolder;
	}

	public void setGeoServerDataFolder(String geoServerDataFolder) {
		this.geoServerDataFolder = geoServerDataFolder;
	}

	public ArrayList<MapInfo> getMapsInfo() {
		return mapsInfo;
	}

	public void setMapsInfo(ArrayList<MapInfo> mapsInfo) {
		this.mapsInfo = mapsInfo;
	}

	public String getGeoServerWorkspace() {
		return geoServerWorkspace;
	}

	public void setGeoServerWorkspace(String geoServerWorkspace) {
		this.geoServerWorkspace = geoServerWorkspace;
	}

	public ArrayList<ModelGalleryInfo> getModelsGallery() {
		return modelsGallery;
	}

	public void setModelsGallery(ArrayList<ModelGalleryInfo> modelsGallery) {
		this.modelsGallery = modelsGallery;
	}

	public boolean isEnableGallery() {
		return enableGallery;
	}

	public void setEnableGallery(boolean enableGallery) {
		this.enableGallery = enableGallery;
	}

	public HydroModelTables getHydroModelTables() {
		return HydroModelTables;
	}

	public void setHydroModelTables(HydroModelTables hydroModelTables) {
		HydroModelTables = hydroModelTables;
	}

	public boolean isEnableHydroModel() {
		return enableHydroModel;
	}

	public void setEnableHydroModel(boolean enableHydroModel) {
		this.enableHydroModel = enableHydroModel;
	}

	public ArrayList<MaxHydroAlertZone> getMaxHydroAlertZone() {
		return maxHydroAlertZone;
	}

	public void setMaxHydroAlertZone(ArrayList<MaxHydroAlertZone> maxHydroAlertZone) {
		this.maxHydroAlertZone = maxHydroAlertZone;
	}

	public boolean isEnableMaxHydroAlertZones() {
		return enableMaxHydroAlertZones;
	}

	public void setEnableMaxHydroAlertZones(boolean enableMaxHydroAlertZones) {
		this.enableMaxHydroAlertZones = enableMaxHydroAlertZones;
	}

	public int getDbBufferDataDays() {
		return dbBufferDataDays;
	}

	public void setDbBufferDataDays(int dbBufferDataDays) {
		dbBufferDataDays = dbBufferDataDays;
	}

	}
