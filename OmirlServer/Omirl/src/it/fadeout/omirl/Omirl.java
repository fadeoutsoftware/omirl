package it.fadeout.omirl;

import it.fadeout.omirl.business.OmirlUser;
import it.fadeout.omirl.business.OpenSession;
import it.fadeout.omirl.business.config.HydroLinkConfig;
import it.fadeout.omirl.business.config.LegendConfig;
import it.fadeout.omirl.business.config.LegendStepConfig;
import it.fadeout.omirl.business.config.MapLinkConfig;
import it.fadeout.omirl.business.config.MapThirdLevelLinkConfig;
import it.fadeout.omirl.business.config.OmirlNavigationConfig;
import it.fadeout.omirl.business.config.SensorLinkConfig;
import it.fadeout.omirl.business.config.StaticLinkConfig;
import it.fadeout.omirl.business.config.TableLinkConfig;
import it.fadeout.omirl.data.OmirlUserRepository;
import it.fadeout.omirl.data.OpenSessionRepository;

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
import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.servlet.ServletConfig;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;

public class Omirl extends Application {
	
	@Context
	ServletConfig m_oServletConfig;
	
	public static String s_sDateHeaderFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSXX";

	
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
        
        return classes;
	}
	
	@PostConstruct
	public void initOmirl() {
		// Get Configuration file path
		String sConfigFilePath = m_oServletConfig.getInitParameter("ConfigFilePath");
		
		
		// Read the configuration
		ReadConfiguration(sConfigFilePath);
		
		
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
				return oUser;
			}
		}
		
		return null;
	}
}
