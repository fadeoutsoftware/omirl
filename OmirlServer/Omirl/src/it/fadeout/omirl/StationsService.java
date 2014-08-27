package it.fadeout.omirl;

import it.fadeout.omirl.business.config.OmirlNavigationConfig;
import it.fadeout.omirl.business.config.SensorLinkConfig;
import it.fadeout.omirl.viewmodels.SensorViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

@Path("/stations")
public class StationsService {
	@Context
	ServletConfig m_oServletConfig;
	
	
	/**
	 * Gets sensors data
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@GET
	@Path("/{sCode}")
	@Produces({"application/xml", "application/json", "text/xml"})
	@Consumes({"application/xml", "application/json", "text/xml"})
	public List<SensorViewModel> GetSensors(@PathParam("sCode") String sCode, @HeaderParam("x-session-token") String sSessionId) {
		
		System.out.println("StationsService.GetSensors: Code = " + sCode);
		
		// Create return array List
		List<SensorViewModel> aoSensors = new ArrayList<>();
		// Date: will be received from client...
		Date oDate = new Date();
		
		// Get Config
		Object oConfObj = m_oServletConfig.getServletContext().getAttribute("Config");
		
		if (oConfObj != null)  {
			
			System.out.println("StationsService.GetSensors: Config Found");
			
			// Cast Config
			OmirlNavigationConfig oConfig = (OmirlNavigationConfig) oConfObj;
			
			// Find the right Sensor Link Configuration
			for (SensorLinkConfig oLinkConfig : oConfig.getSensorLinks()) {
				
				if (oLinkConfig.getCode().equals(sCode)) {
					
					System.out.println("StationsService.GetSensors: Sensor Code Config Found");
					
					// Get The path of the right date
					String sPath = Omirl.getSubPath(oLinkConfig.getFilePath(), oDate);
					
					if (sPath != null) {
						
						System.out.println("StationsService.GetSensors: searching path " + sPath);
						
						// Get The Last File: TODO: here use also the date and get the last before the date!!
						File oLastFile = Omirl.lastFileModified(sPath);
						
						// Found?
						if (oLastFile != null) {
							
							System.out.println("StationsService.GetSensors: Opening File " + oLastFile.getAbsolutePath());

							try {
								// Ok read sensors 
								aoSensors = (List<SensorViewModel>) Omirl.deserializeXMLToObject(oLastFile.getAbsolutePath());
							} catch (Exception e) {
								e.printStackTrace();
							}							
						}
					}
					
					// We are done
					break;
				}
				
			}
		}
		
		
		// Return the list of sensors
		return aoSensors;
	}
}
