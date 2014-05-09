package it.fadeout.omirl;

import java.util.ArrayList;
import java.util.List;

import it.fadeout.omirl.business.config.MapLinkConfig;
import it.fadeout.omirl.business.config.OmirlNavigationConfig;
import it.fadeout.omirl.business.config.SensorLinkConfig;
import it.fadeout.omirl.business.config.StaticLinkConfig;
import it.fadeout.omirl.viewmodels.MapLink;
import it.fadeout.omirl.viewmodels.PrimitiveResult;
import it.fadeout.omirl.viewmodels.SensorLink;
import it.fadeout.omirl.viewmodels.StaticLink;

import javax.servlet.ServletConfig;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

@Path("/mapnavigator")
public class MapNavigatorService {
	
	@Context
	ServletConfig m_oServletConfig;
	
	public MapNavigatorService() {
	}
	
	/**
	 * Test Method
	 * @return
	 */
	@GET
	@Path("/test")
	@Produces({"application/xml", "application/json", "text/xml"})
	public PrimitiveResult TestOmirl() {
		// Just a keep alive message
		PrimitiveResult oTest = new PrimitiveResult();
		oTest.StringValue = "Omirl is Working";
		return oTest;
	}
	
	/**
	 * Reloads xml file configuration
	 * @return
	 */
	@GET
	@Path("/reload")
	@Produces({"application/xml", "application/json", "text/xml"})
	public PrimitiveResult ReloadConfig() {
		PrimitiveResult oTest = new PrimitiveResult();
		oTest.StringValue = "Omirl Configuration Updated";
		
		Object oConfig = null;
		
		try {
			String sConfigFilePath = m_oServletConfig.getInitParameter("ConfigFilePath");
			
			oConfig = Omirl.deserializeXMLToObject(sConfigFilePath);
			
			OmirlNavigationConfig oConfiguration = (OmirlNavigationConfig) oConfig;
			m_oServletConfig.getServletContext().setAttribute("Config", oConfiguration);
			
		} catch (Exception e) {
			oTest.StringValue = "Error reloading Configuration";
			//m_oServletConfig.getServletContext().setAttribute("Config", new OmirlNavigationConfig());
			e.printStackTrace();
		}
		
		return oTest;
	}
	
	
	/**
	 * Gets the first level of sensors
	 * @return
	 */
	@GET
	@Path("/sensors")
	@Produces({"application/xml", "application/json", "text/xml"})
	public List<SensorLink> getSensorLinks() {
		
		ArrayList<SensorLink> aoSensorLinks = new ArrayList<>();

		Object oConfObj = m_oServletConfig.getServletContext().getAttribute("Config");
		
		if (oConfObj != null)  {
			OmirlNavigationConfig oConfig = (OmirlNavigationConfig) oConfObj;
			
			for (SensorLinkConfig oLinkConfig : oConfig.getSensorLinks()) {
				aoSensorLinks.add(oLinkConfig.getSensorLink());
			}
		}
		
		
		return aoSensorLinks;
	}
	
	/**
	 * Gets the first level of static layers
	 * @return
	 */
	@GET
	@Path("/statics")
	@Produces({"application/xml", "application/json", "text/xml"})
	public List<StaticLink> getStaticLinks() {
		
		ArrayList<StaticLink> aoStaticLinks = new ArrayList<>();

		Object oConfObj = m_oServletConfig.getServletContext().getAttribute("Config");
		
		if (oConfObj != null)  {
			OmirlNavigationConfig oConfig = (OmirlNavigationConfig) oConfObj;
			
			for (StaticLinkConfig oLinkConfig : oConfig.getStaticLinks()) {
				aoStaticLinks.add(oLinkConfig.getStaticLink());
			}
		}
		
		return aoStaticLinks;
	}	
	
	/**
	 * Gets the first level of dynamic layers
	 * @return
	 */
	@GET
	@Path("/maps")
	@Produces({"application/xml", "application/json", "text/xml"})
	public List<MapLink> getMapsFirstLevelLinks() {
		
		ArrayList<MapLink> aoMapLinks = new ArrayList<>();

		Object oConfObj = m_oServletConfig.getServletContext().getAttribute("Config");
		
		if (oConfObj != null)  {
			OmirlNavigationConfig oConfig = (OmirlNavigationConfig) oConfObj;
			
			for (MapLinkConfig oLinkConfig : oConfig.getMapLinks()) {
				aoMapLinks.add(oLinkConfig.getMapLink());
			}
		}
		
		return aoMapLinks;
	}		
}
