package it.fadeout.omirl;

import it.fadeout.omirl.business.config.OmirlNavigationConfig;
import it.fadeout.omirl.viewmodels.MapInfoViewModel;
import it.fadeout.omirl.viewmodels.PrimitiveResult;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.ServletConfig;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

@Path("/maps")
public class MapService {
	
	@Context
	ServletConfig m_oServletConfig;
	
	public MapService() {
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
		oTest.StringValue = "Map Service is Working";
		return oTest;
	}
	
	@SuppressWarnings("unchecked")
	@GET
	@Path("/layer/{sCode}/{sModifier}")
	@Produces({"application/xml", "application/json", "text/xml"})
	public MapInfoViewModel GetLayerId(@PathParam("sCode") String sCode, @PathParam("sModifier") String sModifier, @HeaderParam("x-session-token") String sSessionId, @HeaderParam("x-refdate") String sRefDate) {
		MapInfoViewModel oResult = new MapInfoViewModel();
		
		System.out.println("MapService.GetLayerId: Code = " + sCode);

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
			
			// Call get user from session to update last touch if user is logged. Don't care about return here that is free access
			Omirl.getUserFromSession(sSessionId);

			// Cast Config
			OmirlNavigationConfig oConfig = (OmirlNavigationConfig) oConfObj;
			
			SimpleDateFormat oDateFormat = new SimpleDateFormat("yyyy/MM/dd");
			
			String sPath = oConfig.getFilesBasePath();
			sPath = sPath + "/maps/index/" + oDateFormat.format(oDate);
			
			System.out.println("MapService.GetLayerId: searching path " + sPath);
			
			// Get The Last File
			File oLastFile = Omirl.lastFileModified(sPath, oDate);
			
			// Found?
			if (oLastFile != null) {
				
				System.out.println("MapService.GetLayerId: Opening File " + oLastFile.getAbsolutePath());
				
				List<MapInfoViewModel> aoOutputInfo = new ArrayList<MapInfoViewModel>();

				try {
					
					// Ok read sections 
					aoOutputInfo = (List<MapInfoViewModel>) Omirl.deserializeXMLToObject(oLastFile.getAbsolutePath());
				} catch (Exception e) {
					e.printStackTrace();
				}	
				
				for (MapInfoViewModel oInfo : aoOutputInfo) {
					if (oInfo.getCode().equals(sCode)) {
						oResult = oInfo;
						//set ref date
						Date oLastDate = new Date(oLastFile.lastModified()); 
						SimpleDateFormat oFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa");
						oFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
						oInfo.setUpdateDateTime(oFormat.format(oLastDate));
						System.out.println("MapService.GetLayerId: Layer ID Found " + oResult.getLayerId());
						
						break;
					}
				}
				
			}
		}
		else
		{
			System.out.println("MapService.GetLayerId: Config NOT Found");
		}
		
		return oResult;
	}
}
