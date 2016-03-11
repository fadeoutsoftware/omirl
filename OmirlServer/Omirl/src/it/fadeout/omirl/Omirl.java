package it.fadeout.omirl;

import it.fadeout.omirl.business.OmirlUser;
import it.fadeout.omirl.business.OpenSession;
import it.fadeout.omirl.business.config.GalleryLinkConfig;
import it.fadeout.omirl.business.config.HydroLinkConfig;
import it.fadeout.omirl.business.config.HydroModelLinkConfig;
import it.fadeout.omirl.business.config.LegendConfig;
import it.fadeout.omirl.business.config.LegendStepConfig;
import it.fadeout.omirl.business.config.MapInfoAggregationConfig;
import it.fadeout.omirl.business.config.MapLinkConfig;
import it.fadeout.omirl.business.config.MapThirdLevelLinkConfig;
import it.fadeout.omirl.business.config.OmirlNavigationConfig;
import it.fadeout.omirl.business.config.RadarLinkConfig;
import it.fadeout.omirl.business.config.SatelliteLinkConfig;
import it.fadeout.omirl.business.config.SensorLinkConfig;
import it.fadeout.omirl.business.config.StaticLinkConfig;
import it.fadeout.omirl.business.config.TableLinkConfig;
import it.fadeout.omirl.data.OmirlUserRepository;
import it.fadeout.omirl.data.OpenSessionRepository;
import it.fadeout.omirl.viewmodels.SensorViewModel;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletSecurityElement;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;

public class Omirl extends Application {

	@Context
	ServletConfig m_oServletConfig;
	
	@Context
	ServletContext m_oContext;

	public static String s_sDateHeaderFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSXX";

	public static String s_sDateQueryParam = "dd/MM/yyyy HH:mm";

	@Override
	public Set<Class<?>> getClasses() {
		final Set<Class<?>> classes = new HashSet<Class<?>>();
		// register resources and features
		classes.add(MapNavigatorService.class);
		classes.add(StationsService.class);
		classes.add(GensonProvider.class);
		classes.add(ChartService.class);
		classes.add(AuthService.class);
		classes.add(TablesService.class);
		classes.add(SectionsService.class);
		classes.add(MapService.class);
		classes.add(GalleryService.class);
		classes.add(OmirlUserService.class);
		classes.add(AnimationService.class);

		return classes;
	}

	@PostConstruct
	public void initOmirl() {
		// Get Configuration file path
		String sConfigFilePath = m_oServletConfig.getInitParameter("ConfigFilePath");


		// Read the configuration
		ReadConfiguration(sConfigFilePath);

		
		//Init cache
		HashMap<String, CacheObject> oCacheDictionary = new HashMap<>();
		m_oContext.setAttribute("Cache", oCacheDictionary);
		
		// Uncomment to write a test configuration!
		//WriteTestConfiguration("C:\\temp\\OmirlTEST.xml");
	}

	/**
	 * Reads the Omirl Configuration from xml file
	 * @param sFilePath	Configuration File Path
	 */
	void ReadConfiguration(String sFilePath) {

		// Create a stub object
		Object oConfig = null;

		try {

			// Deserialize XML File
			oConfig = deserializeXMLToObject(sFilePath);

			// Cast to Config
			OmirlNavigationConfig oConfiguration = (OmirlNavigationConfig) oConfig;

			if (oConfiguration != null)
			{
				if (oConfiguration.getHydroLinks()!=null)
				{
					if (oConfiguration.getFlattedHydroLinks()==null)
					{
						oConfiguration.setFlattedHydroLinks(new ArrayList<HydroLinkConfig>());
					}

					for (HydroLinkConfig oHLinkConfig : oConfiguration.getHydroLinks()) {
						oConfiguration.getFlattedHydroLinks().add(oHLinkConfig);

						if (oHLinkConfig.getChildren()!=null)
						{
							for (HydroLinkConfig oChild1 : oHLinkConfig.getChildren()) {
								oConfiguration.getFlattedHydroLinks().add(oChild1);

								if (oChild1.getChildren()!=null)
								{
									for (HydroLinkConfig oChild2 : oChild1.getChildren()) {
										oConfiguration.getFlattedHydroLinks().add(oChild2);
									}	
								}								
							}	
						}
					}
				}

				/*
				//Flatted radar link
				if (oConfiguration.getRadarLinks()!= null)
				{
					if (oConfiguration.getFlattedRadarLinks() == null)
					{
						oConfiguration.setFlattedRadarLinks(new ArrayList<RadarLinkConfig>());
					}

					for (RadarLinkConfig oRadarLinkConfig : oConfiguration.getRadarLinks()) {
						oConfiguration.getFlattedRadarLinks().add(oRadarLinkConfig);

						if (oRadarLinkConfig.getChildren()!=null)
						{
							for (RadarLinkConfig oChild1 : oRadarLinkConfig.getChildren()) {
								oConfiguration.getFlattedRadarLinks().add(oChild1);

								if (oChild1.getChildren()!=null)
								{
									for (RadarLinkConfig oChild2 : oChild1.getChildren()) {
										oConfiguration.getFlattedRadarLinks().add(oChild2);
									}	
								}								
							}	
						}
					}
				}
				
				//Flatted Satellite link
				if (oConfiguration.getSatelliteLinks()!= null)
				{
					if (oConfiguration.getFlattedSatelliteLinks() == null)
					{
						oConfiguration.setFlattedSatelliteLinks(new ArrayList<SatelliteLinkConfig>());
					}

					for (SatelliteLinkConfig oSatelliteLinkConfig : oConfiguration.getSatelliteLinks()) {
						oConfiguration.getFlattedSatelliteLinks().add(oSatelliteLinkConfig);

						if (oSatelliteLinkConfig.getChildren()!=null)
						{
							for (SatelliteLinkConfig oChild1 : oSatelliteLinkConfig.getChildren()) {
								oConfiguration.getFlattedSatelliteLinks().add(oChild1);

								if (oChild1.getChildren()!=null)
								{
									for (SatelliteLinkConfig oChild2 : oChild1.getChildren()) {
										oConfiguration.getFlattedSatelliteLinks().add(oChild2);
									}	
								}								
							}	
						}
					}
				}
				*/

			}

			// Save the config
			m_oServletConfig.getServletContext().setAttribute("Config", oConfiguration);

		} catch (Exception e) {
			m_oServletConfig.getServletContext().setAttribute("Config", new OmirlNavigationConfig());
			e.printStackTrace();
		}
	}


	/**
	 * Writes a test configuration file
	 * @param sFilePath
	 */
	void WriteTestConfiguration(String sFilePath) {

		LegendStepConfig aoLegendStepConfig = new LegendStepConfig();
		aoLegendStepConfig.setClr("#A000C8");
		aoLegendStepConfig.setLmt(120);
		LegendStepConfig aoLegendStepConfig2 = new LegendStepConfig();
		aoLegendStepConfig2.setClr("#8200DC");
		aoLegendStepConfig2.setLmt(240);
		ArrayList<LegendStepConfig> oList = new ArrayList<LegendStepConfig>();
		oList.add(aoLegendStepConfig);
		oList.add(aoLegendStepConfig2);

		MapThirdLevelLinkConfig oThird = new MapThirdLevelLinkConfig();
		oThird.setDefault(true);
		oThird.setDescription("Interpolata");
		oThird.setLayerIDModifier("");

		MapThirdLevelLinkConfig oThird2 = new MapThirdLevelLinkConfig();
		oThird2.setDefault(false);
		oThird2.setDescription("Comuni");
		oThird2.setLayerIDModifier("Com");

		MapLinkConfig oMapLink2 = new MapLinkConfig();
		oMapLink2.getThirdLevels().add(oThird);
		oMapLink2.getThirdLevels().add(oThird2);
		oMapLink2.setDescription("Pioggia - Ultimi 15'");
		oMapLink2.setHasThirdLevel(true);
		oMapLink2.setLayerID("OMIRL:rainfall15m");
		oMapLink2.setLayerWMS("http://www.nfsproject.com/geoserver/OMIRL/wms");
		oMapLink2.setLegendLink("img/mapLegend.jpg");
		oMapLink2.setLink("img/15m.png");
		oMapLink2.setLinkId(1);		

		MapLinkConfig oMapLink = new MapLinkConfig();
		oMapLink.setDescription("Pioggia");
		oMapLink.setHasThirdLevel(false);
		oMapLink.setLayerID("");
		oMapLink.setLayerWMS("http://www.nfsproject.com/geoserver/OMIRL/wms");
		oMapLink.setLegendLink("img/mapLegend.jpg");
		oMapLink.setLink("img/rain_drops.png");
		oMapLink.setLinkId(1);
		oMapLink.getSecondLevels().add(oMapLink2);

		SensorLinkConfig oSensorLinkConfig = new SensorLinkConfig();
		oSensorLinkConfig.setCode("Pluvio");
		oSensorLinkConfig.setDescription("Precipitazione");
		oSensorLinkConfig.setCount(50);
		oSensorLinkConfig.setImageLinkInv("img/sensors/pluviometriInv.png");
		oSensorLinkConfig.setImageLinkOff("img/sensors/pluviometriOff.png");
		oSensorLinkConfig.setImageLinkOn("img/sensors/pluviometriOn.png");
		oSensorLinkConfig.setLegendLink("img/sensors/sensorsLegend.jpg");
		oSensorLinkConfig.setMesUnit("mm");
		oSensorLinkConfig.setFilePath("c:\\temp\\omirl");
		oSensorLinkConfig.setColumnName("rain05m");
		oSensorLinkConfig.setLegends(oList);

		StaticLinkConfig oStatic = new StaticLinkConfig();
		oStatic.setDescription("Comuni");
		oStatic.setLayerID("Municipalities_ISTAT12010");
		oStatic.setLayerWMS("http://geoserver.cimafoundation.org/geoserver/dew/wms");

		HydroLinkConfig oHydro = new HydroLinkConfig();
		oHydro.setDescription("Modelli Idro Monitoraggio");
		oHydro.setFilePath("c:\\temp\\omirl\\files");
		oHydro.setHasThirdLevel(false);
		oHydro.setLegendLink("img/sensors/sensorsLegend.jpg");
		oHydro.setLink("img/sensors/hydroMonitoraggio.jpg");
		oHydro.setLinkCode("idromonitoraggio");

		HydroLinkConfig oHydroChild = new HydroLinkConfig();  
		oHydroChild.setDescription("Magra");
		oHydroChild.setFilePath("c:\\temp\\omirl\\files");
		oHydroChild.setHasThirdLevel(true);
		oHydroChild.setLegendLink("img/sensors/sensorsLegend.jpg");
		oHydroChild.setLink("img/sensors/magra.jpg");
		oHydroChild.setLinkCode("magra");		

		HydroLinkConfig oHydro3 = new HydroLinkConfig();  
		oHydro3.setDescription("Magra Q");
		oHydro3.setFilePath("c:\\temp\\omirl\\files");
		oHydro3.setHasThirdLevel(false);
		oHydro3.setLegendLink("img/sensors/sensorsLegend.jpg");
		oHydro3.setLink("img/sensors/magra.jpg");
		oHydro3.setLinkCode("magraq");			

		oHydroChild.getChildren().add(oHydro3);
		oHydro.getChildren().add(oHydroChild);

		TableLinkConfig oDataTable1 = new TableLinkConfig();
		oDataTable1.setActive(true);
		oDataTable1.setCode("Stations");
		oDataTable1.setDescription("Tabella Stazioni");
		oDataTable1.setImageLinkOff("img/tables/max.png");
		oDataTable1.setLocation("/stationstable");
		oDataTable1.setPrivate(false);

		TableLinkConfig oDataTable2 = new TableLinkConfig();
		oDataTable2.setActive(false);
		oDataTable2.setCode("Models");
		oDataTable2.setDescription("Modelli Idrologici");
		oDataTable2.setImageLinkOff("img/tables/sintesi.png");
		oDataTable2.setLocation("/modelstable");
		oDataTable2.setPrivate(true);

		TableLinkConfig oTable1 = new TableLinkConfig();
		oTable1.setActive(true);
		oTable1.setCode("StationValues");
		oTable1.setDescription("Valori Stazioni");
		oTable1.setImageLinkOff("img/tables/stationvalues.png");
		oTable1.setLocation("/sensorstable");
		oTable1.setPrivate(false);

		TableLinkConfig oTable2 = new TableLinkConfig();
		oTable2.setActive(false);
		oTable2.setCode("Max");
		oTable2.setDescription("Massimi Puntuali");
		oTable2.setImageLinkOff("img/tables/max.png");
		oTable2.setLocation("/maxtable");
		oTable2.setPrivate(false);

		TableLinkConfig oTable3 = new TableLinkConfig();
		oTable3.setActive(false);
		oTable3.setCode("Sintesi");
		oTable3.setDescription("Sintesi");
		oTable3.setImageLinkOff("img/tables/sintesi.png");
		oTable3.setLocation("/summarytable");
		oTable3.setPrivate(true);

		GalleryLinkConfig oGallery1 = new GalleryLinkConfig();
		oGallery1.setActive(false);
		oGallery1.setCode("bo10ar");
		oGallery1.setDescription("Sintesi");
		oGallery1.setImageLinkOff("img/gallery/sintesi.png");
		oGallery1.setLocation("/summarytable");
		oGallery1.setPrivate(true);
		GalleryLinkConfig oGallery1_1 = new GalleryLinkConfig();
		oGallery1_1.setActive(false);
		oGallery1_1.setCode("GH_TCK_Europe");
		oGallery1_1.setDescription("12h Total Precipitation");
		oGallery1_1.setImageLinkOff("img/tables/sintesi.png");
		oGallery1_1.setLocation("/summarytable");
		oGallery1_1.setPrivate(true);
		oGallery1_1.setCodeVariable("TPrec12");
		oGallery1_1.setCodeParent(oGallery1.getCode());
		GalleryLinkConfig oGallery1_2 = new GalleryLinkConfig();
		oGallery1_2.setActive(false);
		oGallery1_2.setCode("_Europe");
		oGallery1_2.setDescription("Mean Sea Level Pressure");
		oGallery1_2.setImageLinkOff("img/tables/sintesi.png");
		oGallery1_2.setLocation("/summarytable");
		oGallery1_2.setPrivate(true);
		oGallery1_2.setCodeVariable("MSLP");
		oGallery1_2.setCodeParent(oGallery1.getCode());
		oGallery1.getSublevelGalleryLinkConfig().add(oGallery1_1);
		oGallery1.getSublevelGalleryLinkConfig().add(oGallery1_2);

		GalleryLinkConfig oGallery2 = new GalleryLinkConfig();
		oGallery2.setActive(false);
		oGallery2.setCode("bo10ac");
		oGallery2.setDescription("Sintesi");
		oGallery2.setImageLinkOff("img/tables/sintesi.png");
		oGallery2.setLocation("/summarytable");
		oGallery2.setPrivate(true);
		GalleryLinkConfig oGallery2_1 = new GalleryLinkConfig();
		oGallery2_1.setActive(false);
		oGallery2_1.setCode("Sintesi");
		oGallery2_1.setDescription("Sintesi");
		oGallery2_1.setImageLinkOff("img/tables/sintesi.png");
		oGallery2_1.setLocation("/summarytable");
		oGallery2_1.setPrivate(true);
		oGallery2_1.setCodeVariable("TPrec12");
		oGallery2_1.setCodeParent(oGallery2.getCode());
		GalleryLinkConfig oGallery2_2 = new GalleryLinkConfig();
		oGallery2_2.setActive(false);
		oGallery2_2.setCode("Sintesi");
		oGallery2_2.setDescription("Sintesi");
		oGallery2_2.setImageLinkOff("img/tables/sintesi.png");
		oGallery2_2.setLocation("/summarytable");
		oGallery2_2.setPrivate(true);
		oGallery2_2.setCodeVariable("MSLP");
		oGallery2_2.setCodeParent(oGallery2.getCode());
		oGallery2.getSublevelGalleryLinkConfig().add(oGallery2_1);
		oGallery2.getSublevelGalleryLinkConfig().add(oGallery2_2);


		HydroModelLinkConfig oModelChild = new HydroModelLinkConfig();  
		oModelChild.setDescription("Rainfarmbo10ar+06");
		oModelChild.setIconLink("/img/tables/sintesi.png");
		oModelChild.setLinkCode("Rainfarmbo10ar+06");
		oModelChild.setIsDefault(true);
		
		MapInfoAggregationConfig oMapConfig1 = new MapInfoAggregationConfig();
		oMapConfig1.setModifier("Com");
		oMapConfig1.setPath("/var/omirl/files/aggregations/com");
		oMapConfig1.setShapeFile("comuni_wgs84");
		
		MapInfoAggregationConfig oMapConfig2 = new MapInfoAggregationConfig();
		oMapConfig2.setModifier("Bac");
		oMapConfig2.setPath("/var/omirl/files/aggregations/bac");
		oMapConfig2.setShapeFile("bacini_wgs84");
		
		MapInfoAggregationConfig oMapConfig3 = new MapInfoAggregationConfig();
		oMapConfig3.setModifier("AA");
		oMapConfig3.setPath("/var/omirl/files/aggregations/aa");
		oMapConfig3.setShapeFile("aree_wgs84");
		

		OmirlNavigationConfig oConfig = new OmirlNavigationConfig();
		oConfig.setFilesBasePath("C:/temp/omirl/files");
		oConfig.getMapLinks().add(oMapLink);
		oConfig.getStaticLinks().add(oStatic);
		oConfig.getSensorLinks().add(oSensorLinkConfig);
		oConfig.getHydroLinks().add(oHydro);
		oConfig.getDataTableLinks().add(oDataTable1);
		oConfig.getDataTableLinks().add(oDataTable2);
		oConfig.getTableLinks().add(oTable1);
		oConfig.getTableLinks().add(oTable2);
		oConfig.getTableLinks().add(oTable3);
		oConfig.getGalleryLinks().add(oGallery1);
		oConfig.getGalleryLinks().add(oGallery2);
		oConfig.getHydroModelLinks().add(oModelChild);
		oConfig.getMapInfoAggregationConfigs().add(oMapConfig1);
		oConfig.getMapInfoAggregationConfigs().add(oMapConfig2);
		oConfig.getMapInfoAggregationConfigs().add(oMapConfig3);


		try {
			serializeObjectToXML(sFilePath, oConfig);
		} catch (Exception e) {
			e.printStackTrace();
		}


	}

	/**
	 * This method saves (serializes) any java bean object into xml file
	 */
	public static void serializeObjectToXML(String xmlFileLocation, Object objectToSerialize) throws Exception {
		FileOutputStream os = new FileOutputStream(xmlFileLocation);
		XMLEncoder encoder = new XMLEncoder(os);
		encoder.writeObject(objectToSerialize);
		encoder.close();
	}

	/**
	 * Reads Java Bean Object From XML File
	 */
	public static Object deserializeXMLToObject(String xmlFileLocation) throws Exception {
		FileInputStream os = new FileInputStream(xmlFileLocation);
		XMLDecoder decoder = new XMLDecoder(os);
		Object deSerializedObject = decoder.readObject();
		decoder.close();

		return deSerializedObject;
	}

	/**
	 * Gets a full path starting from the Base Path appending oDate
	 * @param sBasePath
	 * @param oDate
	 * @return
	 */
	public static String getSubPath(String sBasePath, Date oDate) {
		SimpleDateFormat oDateFormat = new SimpleDateFormat("yyyy/MM/dd");

		String sFullDir = sBasePath + "/" + oDateFormat.format(oDate);

		File oFullPathDir = new File(sFullDir);
		if (!oFullPathDir.exists()) {
			return null;
		}

		return sFullDir;
	}


	/**
	 * Gets a full path starting from the Base Path appending oDate
	 * @param sBasePath
	 * @param oDate
	 * @return
	 */
	public static String getSubPathWithoutCheck(String sBasePath, Date oDate) {
		SimpleDateFormat oDateFormat = new SimpleDateFormat("yyyy/MM/dd");

		String sFullDir = sBasePath + "/" + oDateFormat.format(oDate);

		return sFullDir;
	}

	public static File lastFileModified(String dir, Date oRefDate) {
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

		//se è presente una data allora prendiamo il file immediatamente più vicino in base ad ora e minuti
		if (oRefDate != null)
		{
			long longRefDate = Long.MAX_VALUE;
			SimpleDateFormat oFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
			try {
				Date oParsed = oFormat.parse(oFormat.format(oRefDate));
				longRefDate = oParsed.getTime();
			} catch (ParseException e) {
				e.printStackTrace();
			}

			long lDiff = Long.MAX_VALUE;
			for (File file : aoFiles) {
				Date oDateFile = new Date(file.lastModified());
				try {
					Date oLastDateFile = oFormat.parse(oFormat.format(oDateFile));
					long lTicksLastDateFile = oLastDateFile.getTime();

					//se per fortuna la differenza è uguale a 0 allora abbiamo trovato il file candidato
					if (lTicksLastDateFile - longRefDate == 0)
					{
						oChoise = file;
						break;
					}

					//se la differenza è maggiore di 0 allora è un file precedente alla data
					if (longRefDate - lTicksLastDateFile  > 0)
					{
						//vediamo se è il più vicino alla data selezionata
						if ((longRefDate - lTicksLastDateFile) < lDiff)
						{
							oChoise = file;
							lDiff = longRefDate - lTicksLastDateFile;
						}
					}

				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}

		return oChoise;
	}

	public static File nearestHourSubFolder(String dir, Date oRefDate) {

		File oDir = new File(dir);

		if (!oDir.exists()) {
			System.out.println("OMIRL.nearestHourSubFolder: folder does not exists " + dir);
			return null;
		}

		File[] aoFiles = oDir.listFiles(new FileFilter() {			
			public boolean accept(File file) {
				return !file.isFile();
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

		//se è presente una data allora prendiamo il file immediatamente più vicino in base ad ora e minuti
		if (oRefDate != null)
		{
			long longRefDate = Long.MAX_VALUE;
			SimpleDateFormat oFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
			try {
				Date oParsed = oFormat.parse(oFormat.format(oRefDate));
				longRefDate = oParsed.getTime();
			} catch (ParseException e) {
				e.printStackTrace();
			}

			long lDiff = Long.MAX_VALUE;
			for (File file : aoFiles) {
				Date oDateFile = new Date(file.lastModified());
				try {
					Date oLastDateFile = oFormat.parse(oFormat.format(oDateFile));
					long lTicksLastDateFile = oLastDateFile.getTime();

					//se per fortuna la differenza è uguale a 0 allora abbiamo trovato il file candidato
					if (lTicksLastDateFile - longRefDate == 0)
					{
						oChoise = file;
						break;
					}

					//se la differenza è maggiore di 0 allora è un file precedente alla data
					if (longRefDate - lTicksLastDateFile  > 0)
					{
						//vediamo se è il più vicino alla data selezionata
						if ((longRefDate - lTicksLastDateFile) < lDiff)
						{
							oChoise = file;
							lDiff = longRefDate - lTicksLastDateFile;
						}
					}

				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}

		return oChoise;
	}

	public static OmirlUser getUserFromSession(String sSessionId) {
		if (sSessionId == null) {
			return null;
		}
		if (sSessionId.isEmpty()) {
			return null;
		}		

		OpenSessionRepository oOpenSessionRepository = new OpenSessionRepository();
		OpenSession oSession = oOpenSessionRepository.selectBySessionId(sSessionId);

		if(oSession != null) {

			// TODO: Check also session age ?!

			OmirlUserRepository oOmirlUserRepository = new OmirlUserRepository();
			OmirlUser oUser = oOmirlUserRepository.Select(oSession.getIdUser(), OmirlUser.class);

			if (oUser != null) {
				oOpenSessionRepository.updateBySessionId(sSessionId);
				return oUser;
			}
		}

		return null;
	}
	
	public synchronized  static Object getCacheValues(String sResourcesPath, ServletContext oContext)
	{
		try
		{
			HashMap<String, CacheObject> oCacheDictionary = (HashMap<String, CacheObject>) oContext.getAttribute("Cache");
			if (oCacheDictionary == null)
				oCacheDictionary = new HashMap<String, CacheObject>();
			if (oCacheDictionary.containsKey(sResourcesPath))
			{
				CacheObject oCache = oCacheDictionary.get(sResourcesPath);
				if (oCache == null)
				{
					return null;
				}
				
				long now = new Date().getTime();
				long cacheTime = oCache.getTimestamp();
				long minuteMs = 1000*60*1;
				if (now - cacheTime < minuteMs)
				{
					return oCache.getData();
				}
			}
			
		}
		catch(Exception oEx)
		{
			oEx.printStackTrace();
		}
		
		return null;
	}
	
	public synchronized  static void setCacheValues(String sResourcesPath, Object oData, ServletContext oContext)
	{
		try
		{
			//create new cache object
			CacheObject oCache = new CacheObject();
			//set timestamp
			oCache.setTimestamp(new Date().getTime());
			//set data
			oCache.setData(oData);
			//put in dictionary
			((HashMap<String, CacheObject>)oContext.getAttribute("Cache")).put(sResourcesPath, oCache);
			
		}
		catch(Exception oEx)
		{
			oEx.printStackTrace();
		}
	}
}
