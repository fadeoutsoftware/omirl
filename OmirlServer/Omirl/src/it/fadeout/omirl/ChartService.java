package it.fadeout.omirl;

import it.fadeout.omirl.business.DataChart;
import it.fadeout.omirl.business.config.OmirlNavigationConfig;
import it.fadeout.omirl.business.config.SensorLinkConfig;
import it.fadeout.omirl.viewmodels.PrimitiveResult;

import java.io.File;
import java.util.Date;

import javax.servlet.ServletConfig;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

@Path("/charts")
public class ChartService {
	@Context
	ServletConfig m_oServletConfig;
	
	@GET
	@Path("/test")
	@Produces({"application/xml", "application/json", "text/xml"})
	public PrimitiveResult TestOmirl() {
		// Just a keep alive message
		PrimitiveResult oTest = new PrimitiveResult();
		oTest.StringValue = "Omirl ChartService is Working";
		return oTest;
	}
	
	/**
	 * Gets sensors data
	 * @return
	 */
	@GET
	@Path("/{sCode}/{sChart}")
	@Produces({"application/xml", "application/json", "text/xml"})
	public DataChart GetChart(@PathParam("sCode") String sCode, @PathParam("sChart") String sChart) {
		
		System.out.println("ChartService.GetChart: Code = " + sCode + " Chart = " + sChart);
		
		// Create return array List
		DataChart oDataChart = null;
		// Date: will be received from client...
		Date oDate = new Date();
		
		// Get Config
		Object oConfObj = m_oServletConfig.getServletContext().getAttribute("Config");
		
		if (oConfObj != null)  {
			
			System.out.println("ChartService.GetChart: Config Found");
			
			// Cast Config
			OmirlNavigationConfig oConfig = (OmirlNavigationConfig) oConfObj;			
			
			// Find the right Sensor Link Configuration
			for (SensorLinkConfig oLinkConfig : oConfig.getSensorLinks()) {
				
				if (oLinkConfig.getCode().equals(sChart)) {
					sChart = oLinkConfig.getColumnName();
					System.out.println("ChartService.GetChart: Column Name = " +sChart);
					break;
				}
			}
			
			String sBasePath = oConfig.getFilesBasePath();
			
			sBasePath += "/charts/" + sCode + "/" + sChart;
			
			System.out.println("ChartService.GetChart: sBasePath = " + sBasePath);
			
			// Get The path of the right date
			String sPath = Omirl.getSubPath(sBasePath, oDate);
			
			if (sPath != null) {
				
				System.out.println("ChartService.GetChart: searching path " + sPath);
				
				// Get The Last File: TODO: here use also the date and get the last before the date!!
				File oLastFile = Omirl.lastFileModified(sPath);
				
				// Found?
				if (oLastFile != null) {
					
					System.out.println("ChartService.GetChart: Opening File " + oLastFile.getAbsolutePath());
					
					try {
						// Ok read sensors 
						oDataChart = (DataChart) Omirl.deserializeXMLToObject(oLastFile.getAbsolutePath());
					} catch (Exception e) {
						e.printStackTrace();
					}							
				}
			}
		}
		
		// Return the list of sensors
		return oDataChart;
	}	

}
