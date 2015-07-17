package it.fadeout.omirl;

import it.fadeout.omirl.business.config.GalleryLinkConfig;
import it.fadeout.omirl.business.config.HydroLinkConfig;
import it.fadeout.omirl.business.config.OmirlNavigationConfig;
import it.fadeout.omirl.business.config.TableLinkConfig;
import it.fadeout.omirl.viewmodels.GalleryLink;
import it.fadeout.omirl.viewmodels.ModelGallery;
import it.fadeout.omirl.viewmodels.TableLink;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

@Path("/gallery")
public class GalleryService {
	@Context
	ServletConfig m_oServletConfig;
	
	
	/**
	 * Gets Gallery
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@GET
	@Path("/{sCode}")
	@Produces({"application/xml", "application/json", "text/xml"})
	@Consumes({"application/xml", "application/json", "text/xml"})
	public ModelGallery GetGallery(@PathParam("sCode") String sCode, @HeaderParam("x-session-token") String sSessionId, @HeaderParam("x-refdate") String sRefDate) {
		
		System.out.println("GalleryService.GetGallery: Code = " + sCode);
		
		// Create return array List
		ModelGallery oGallery = new ModelGallery();
		
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
			
			System.out.println("GalleryService.GetGallery: Config Found");
			
			// Cast Config
			OmirlNavigationConfig oConfig = (OmirlNavigationConfig) oConfObj;
			
			
			System.out.println("GalleryService.GetGallery: Section Code Config Found");
			
			// Get The path of the right date
			String sPath = Omirl.getSubPath(oConfig.getFilesBasePath()+"/gallery", oDate);
			
			if (sPath != null) {
				
				File oHourFolder = Omirl.nearestHourSubFolder(sPath, oDate);
				
				if (oHourFolder!=null)
				{
					sPath = sPath + "/" + oHourFolder.getName();
				}
				
				
				
				System.out.println("GalleryService.GetGallery: searching path " + sPath);
				
				// Get The Last File
				File oLastFile = new File(sPath+"/"+sCode+".xml");
				
				
				// Found?
				if (oLastFile != null) {
					
					System.out.println("GalleryService.GetGallery: Opening File " + oLastFile.getAbsolutePath());

					try {
						// Ok read sections 
						oGallery = (ModelGallery) Omirl.deserializeXMLToObject(oLastFile.getAbsolutePath());
					} catch (Exception e) {
						e.printStackTrace();
					}							
				}
			}
			else {
				System.out.println("GalleryService.GetGallery: WARNING path is null");
			}
		}
		
		
		// Return the list of sensors
		return oGallery;
	}
	
	@GET
	@Path("/gallerylinks")
	@Produces({"application/xml", "application/json", "text/xml"})	
	public ArrayList<GalleryLink> getGalleryLinks(@HeaderParam("x-session-token") String sSessionId) {
		
		ArrayList<GalleryLink> aoRet = new ArrayList<>();
		
		try {
			boolean bShowPrivate = false;
			if (Omirl.getUserFromSession(sSessionId) != null) {
				bShowPrivate = true;
			}
			
			// Get Config
			Object oConfObj = m_oServletConfig.getServletContext().getAttribute("Config");
			
			if (oConfObj != null)  {
				// Cast Config
				OmirlNavigationConfig oConfig = (OmirlNavigationConfig) oConfObj;			
				
				for (int iConfigured =0 ; iConfigured< oConfig.getGalleryLinks().size(); iConfigured++ )
				{
					GalleryLinkConfig oGalleryLink = oConfig.getGalleryLinks().get(iConfigured);
					
					boolean bAdd = false;
					
					if (bShowPrivate==true) bAdd = true;
					else {
						if (oGalleryLink.isPrivate() == false) bAdd = true;
					}
					
					if (bAdd){
						aoRet.add(oGalleryLink.getGalleryLink());
					}
				}
			}
			
			
		}
		catch(Exception oEx) {
			oEx.printStackTrace();
		}
		
		return aoRet;
	}
}
