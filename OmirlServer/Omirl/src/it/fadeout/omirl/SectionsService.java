package it.fadeout.omirl;

import it.fadeout.omirl.business.config.HydroLinkConfig;
import it.fadeout.omirl.business.config.OmirlNavigationConfig;
import it.fadeout.omirl.viewmodels.SectionViewModel;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.ServletConfig;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

@Path("/sections")
public class SectionsService {
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
	public List<SectionViewModel> GetSection(@PathParam("sCode") String sCode, @HeaderParam("x-session-token") String sSessionId, @HeaderParam("x-refdate") String sRefDate) {
		
		System.out.println("SectionsService.GetSection: Code = " + sCode);
		// Create return array List
		List<SectionViewModel> aoSections = new ArrayList<>();		
		
		if (Omirl.getUserFromSession(sSessionId)!= null) 
		{
			// Date: will be received from client...
			Date oDate = new Date();
			
			if (sRefDate!=null)
			{
				if (sRefDate.equals("") == false) 
				{
					// Try e catch per fare il parsing 
					// se è valido sostituire oDate.
					SimpleDateFormat dtFormat = new SimpleDateFormat(Omirl.s_sDateHeaderFormat);
					try {
						
						oDate = dtFormat.parse(sRefDate);
						
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
			}
			
			// Get Config
			Object oConfObj = m_oServletConfig.getServletContext().getAttribute("Config");
			
			if (oConfObj != null)  {
				
				// Cast Config
				OmirlNavigationConfig oConfig = (OmirlNavigationConfig) oConfObj;
				
				// Find the right Sensor Link Configuration
				for (HydroLinkConfig oLinkConfig : oConfig.getFlattedHydroLinks()) {
					
					if (oLinkConfig.getLinkCode().equals(sCode)) {
						
						//System.out.println("SectionsService.GetSection: Section Code Config Found");
						
						// Get The path of the right date
						String sPath = Omirl.getSubPath(oLinkConfig.getFilePath(), oDate);
						
						if (sPath != null) {
							
							sPath = sPath + "/features";
							
							System.out.println("SectionsService.GetSection: searching path " + sPath);
							
							// Get The Last File
							File oLastFile = Omirl.lastFileModified(sPath, oDate);
							
							// Found?
							if (oLastFile != null) {
								
								System.out.println("SectionsService.GetSection: Opening File " + oLastFile.getAbsolutePath());
	
								try {
									// Ok read sections 
									aoSections = (List<SectionViewModel>) Omirl.deserializeXMLToObject(oLastFile.getAbsolutePath());
									if (aoSections.size() > 0)
									{
										Date oLastDate = new Date(oLastFile.lastModified()); 
										SimpleDateFormat oFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa");
										oFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
										aoSections.get(0).setUpdateDateTime(oFormat.format(oLastDate));
									}
								} catch (Exception e) {
									e.printStackTrace();
								}							
							}
						}
						else {
							System.out.println("SectionsService.GetSection: WARNING path is null");
						}
						
						// We are done
						break;
					}
					
				}
			}
			else
			{
				System.out.println("SectionsService.GetSection: Config NOT Found");
			}
		}
		
		// Return the list of sensors
		return aoSections;
	}	
}
