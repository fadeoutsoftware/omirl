package it.fadeout.omirl;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletConfig;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import it.fadeout.omirl.business.config.OmirlNavigationConfig;
import it.fadeout.omirl.viewmodels.ModelGallery;
import it.fadeout.omirl.viewmodels.PrimitiveResult;

@Path("/animation")
public class AnimationService {
	@Context
	ServletConfig m_oServletConfig;
	
	
	/**
	 * Gets Gallery
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@GET
	@Path("/animation/{sCode}")
	@Produces({"application/xml", "application/json", "text/xml"})
	@Consumes({"application/xml", "application/json", "text/xml"})
	public PrimitiveResult GetAnimation(@PathParam("sCode") String sCode, @HeaderParam("x-session-token") String sSessionId, @HeaderParam("x-refdate") String sRefDate) {
		
		System.out.println("AnimationService.GetAnimation: Code = " + sCode);
		
		PrimitiveResult oResult = new PrimitiveResult();
				
		// Date: will be received from client...
		Date oDate = new Date();
		
		if (sRefDate!=null)
		{
			if (sRefDate.equals("") == false) 
			{
				// Try e catch per fare il parsing 
				// se � valido sostituire oDate.
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
			
			
			//System.out.println("GalleryService.GetGallery: Section Code Config Found");
			
			// Get The path of the right date
			String sPath = Omirl.getSubPath(oConfig.getFilesBasePath()+"/radarsat/" + sCode , oDate);
			
			if (sPath != null) {
				
				sPath = sPath +"/animations";
				
				System.out.println("AnimationService.GetAnimation: searching path " + sPath);
				
				File oLastFile = Omirl.lastFileModified(sPath, oDate);
				
				// Found?
				if (oLastFile != null) {
					
					System.out.println("AnimationService.GetAnimation: Opening File " + oLastFile.getAbsolutePath());

					try {
						// Ok read sections 
						oResult.StringValue = "img" + oLastFile.getAbsolutePath().substring(oConfig.getFilesBasePath().length(), oLastFile.getAbsolutePath().length());
						System.out.println("AnimationService.GetAnimation: Out Result =  " + oResult.StringValue);
					} catch (Exception e) {
						e.printStackTrace();
					}							
				}
			}
			else {
				System.out.println("AnimationService.GetAnimation: WARNING path is null");
			}
		}
		else
		{
			System.out.println("AnimationService.GetAnimation: Config NOT Found");
		}
		
		
		// Return the list of sensors
		return oResult;
	}
	
	@SuppressWarnings("unchecked")
	@GET
	@Path("/image/{sCode}")
	@Produces({"application/xml", "application/json", "text/xml"})
	@Consumes({"application/xml", "application/json", "text/xml"})
	public PrimitiveResult GetImage(@PathParam("sCode") String sCode, @HeaderParam("x-session-token") String sSessionId, @HeaderParam("x-refdate") String sRefDate) {
		
		System.out.println("AnimationService.GetImage: Code = " + sCode);
		
		PrimitiveResult oResult = new PrimitiveResult();
				
		// Date: will be received from client...
		Date oDate = new Date();
		
		if (sRefDate!=null)
		{
			if (sRefDate.equals("") == false) 
			{
				// Try e catch per fare il parsing 
				// se � valido sostituire oDate.
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
			
			
			//System.out.println("GalleryService.GetGallery: Section Code Config Found");
			
			// Get The path of the right date
			String sPath = Omirl.getSubPath(oConfig.getFilesBasePath()+"/radarsat/" + sCode , oDate);
			
			if (sPath != null) {
				
				sPath = sPath +"/images";
				
				System.out.println("AnimationService.GetImage: searching path " + sPath);
				
				//File oLastFile = Omirl.lastFileModified(sPath, oDate);
				File oLastFile = Omirl.lastFileByName(sPath, oDate);
				// Found?
				if (oLastFile != null) {
					
					System.out.println("AnimationService.GetImage: Opening File " + oLastFile.getAbsolutePath());

					try {
						// Ok read sections 
						oResult.StringValue = "img" + oLastFile.getAbsolutePath().substring(oConfig.getFilesBasePath().length(), oLastFile.getAbsolutePath().length());
						System.out.println("AnimationService.GetImage: Out Result =  " + oResult.StringValue);
					} catch (Exception e) {
						e.printStackTrace();
					}							
				}
			}
			else {
				System.out.println("AnimationService.GetImage: WARNING path is null");
			}
		}
		else
		{
			System.out.println("AnimationService.GetImage: Config NOT Found");
		}
		
		
		// Return the list of sensors
		return oResult;
	}	
}
