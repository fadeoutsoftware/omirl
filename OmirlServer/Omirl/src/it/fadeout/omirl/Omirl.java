package it.fadeout.omirl;

import it.fadeout.omirl.business.config.MapLinkConfig;
import it.fadeout.omirl.business.config.MapThirdLevelLinkConfig;
import it.fadeout.omirl.business.config.OmirlNavigationConfig;
import it.fadeout.omirl.business.config.SensorLinkConfig;
import it.fadeout.omirl.business.config.StaticLinkConfig;
import it.fadeout.omirl.viewmodels.PrimitiveResult;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.servlet.ServletConfig;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;

public class Omirl extends Application {
	
	@Context
	ServletConfig m_oServletConfig;

	
	@Override
    public Set<Class<?>> getClasses() {
        final Set<Class<?>> classes = new HashSet<Class<?>>();
        // register resources and features
        classes.add(MapNavigatorService.class);
        classes.add(StationsService.class);
        classes.add(GensonProvider.class);
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
		
		StaticLinkConfig oStatic = new StaticLinkConfig();
		oStatic.setDescription("Comuni");
		oStatic.setLayerID("Municipalities_ISTAT12010");
		oStatic.setLayerWMS("http://geoserver.cimafoundation.org/geoserver/dew/wms");
		
		OmirlNavigationConfig oConfig = new OmirlNavigationConfig();
		oConfig.getMapLinks().add(oMapLink);
		oConfig.getStaticLinks().add(oStatic);
		oConfig.getSensorLinks().add(oSensorLinkConfig);
		
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
 	
	
	public static File lastFileModified(String dir) {
		File oDir = new File(dir);
		
		
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
