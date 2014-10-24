package it.fadeout.omirl;

import it.fadeout.omirl.business.config.OmirlNavigationConfig;
import it.fadeout.omirl.business.config.SensorLinkConfig;
import it.fadeout.omirl.viewmodels.SensorListTableRowViewModel;
import it.fadeout.omirl.viewmodels.SensorListTableViewModel;
import it.fadeout.omirl.viewmodels.SensorViewModel;
import it.fadeout.omirl.viewmodels.StationTypeViewModel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

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
	
	@GET
	@Path("/types")
	@Produces({"application/xml", "application/json", "text/xml"})
	@Consumes({"application/xml", "application/json", "text/xml"})	
	public List<StationTypeViewModel> GetStationTypes(@HeaderParam("x-session-token") String sSessionId) {
		ArrayList<StationTypeViewModel> aoTypes = new ArrayList<>();
		
		// Get Config
		Object oConfObj = m_oServletConfig.getServletContext().getAttribute("Config");

		if (oConfObj != null)  {
			
			System.out.println("StationsService.GetStationTypes: Config Found");
			
			// Cast Config
			OmirlNavigationConfig oConfig = (OmirlNavigationConfig) oConfObj;
			
			// Find the right Sensor Link Configuration
			for (SensorLinkConfig oLinkConfig : oConfig.getSensorLinks()) {
				if (oLinkConfig.getIsClickable() && oLinkConfig.getIsVisible()) {
					StationTypeViewModel oType = new StationTypeViewModel();
					oType.setCode(oLinkConfig.getCode());
					oType.setDescription(oLinkConfig.getDescription());
					aoTypes.add(oType);
				}
			}
		}
		
		return aoTypes;
	}
	
	@GET
	@Path("/stationlist/{sCode}")
	@Produces({"application/xml", "application/json", "text/xml"})
	@Consumes({"application/xml", "application/json", "text/xml"})	
	public SensorListTableViewModel GetStationsListTable(@PathParam("sCode") String sCode, @HeaderParam("x-session-token") String sSessionId) {
		
		System.out.println("StationsService.GetSensors: GetStationsListTable = " + sCode);
		
		// Create return array List
		SensorListTableViewModel oTable = new SensorListTableViewModel();
		
		// Get Config
		Object oConfObj = m_oServletConfig.getServletContext().getAttribute("Config");
		
		if (oConfObj != null)  {
			
			System.out.println("StationsService.GetStationsListTable: Config Found");
			
			// Cast Config
			OmirlNavigationConfig oConfig = (OmirlNavigationConfig) oConfObj;
			
			String sPath = oConfig.getFilesBasePath()+"/tables/list/";
			sPath+=sCode+".xml";
			
			System.out.println("StationsService.GetStationsListTable: Opening File " + sPath);

			try {
				// Ok read sensors 
				oTable = (SensorListTableViewModel) Omirl.deserializeXMLToObject(sPath);
			} catch (Exception e) {
				e.printStackTrace();
			}							
		}
		
		
		// Return the list of sensors
		return oTable;
	}
	
	
	
	
	
	/**
	 * Gets sensors data
	 * @return
	 */
	@GET
	@Path("/exportlist/{sCode}")
	@Produces({"application/octet-stream"})
	public Response ExportStationsListTable(@PathParam("sCode") String sCode, @HeaderParam("x-session-token") String sSessionId) {
		
	  	System.out.println("StaionsService.ExportStationsListTable: Code = " + sCode);
		
		// Create return array List
		SensorListTableViewModel oTable = new SensorListTableViewModel();
		
		// Get Config
		Object oConfObj = m_oServletConfig.getServletContext().getAttribute("Config");
		
		if (oConfObj != null)  {
			
			System.out.println("StationsService.GetStationsListTable: Config Found");
			
			// Cast Config
			OmirlNavigationConfig oConfig = (OmirlNavigationConfig) oConfObj;
			
			String sPath = oConfig.getFilesBasePath()+"/tables/list/";
			sPath+=sCode+".xml";
			
			System.out.println("StationsService.GetStationsListTable: Opening File " + sPath);

			try {
				// Ok read sensors 
				oTable = (SensorListTableViewModel) Omirl.deserializeXMLToObject(sPath);
			} catch (Exception e) {
				e.printStackTrace();
			}							
		}
		
		final SensorListTableViewModel oFinalTable = oTable;
		
		StreamingOutput stream = new StreamingOutput() {
			  @Override
			  public void write(OutputStream os) throws IOException, WebApplicationException {
			      Writer writer = new BufferedWriter(new OutputStreamWriter(os));
			      
			      if (oFinalTable.getTableRows()!=null)
			      {
				      if (oFinalTable.getTableRows().size()>0) 
				      {
				    	  
				    	  writer.write("Codice;Nome;Provincia;Area;Bacino;Comune\n");
				    	  
				    	  for (int iTableRows=0; iTableRows<oFinalTable.getTableRows().size(); iTableRows++) {
				    		  
				    		  SensorListTableRowViewModel oRow = oFinalTable.getTableRows().get(iTableRows);
				    		  
				    		  String sStationCode = oRow.getStationCode();
				    		  String sName = oRow.getName();
				    		  String sDistrict = oRow.getDistrict();
				    		  String sArea = oRow.getArea();
				    		  String sBasin = oRow.getBasin();
				    		  String sMunicipality = oRow.getMunicipality();
				    		  
				    		  if (sStationCode == null) sStationCode ="";
				    		  if (sName == null) sName ="";
				    		  if (sDistrict == null) sDistrict ="";
				    		  if (sArea == null) sArea ="";
				    		  if (sBasin == null) sBasin ="";
				    		  if (sMunicipality == null) sMunicipality ="";

				    		  writer.write(sStationCode+";");
				    		  writer.write(sName+";");
				    		  writer.write(sDistrict+";");
				    		  writer.write(sArea+";");
				    		  writer.write(sBasin+";");
				    		  writer.write(sMunicipality);
				    		  writer.write("\n");
				    	  }
				      }
			      }
			      
			      //writer.write("test");
			      writer.flush();
			      
			  }
		};
		
		
	  	return Response.ok(stream).header("Content-Disposition", "attachment;filename="+sCode+"_List.csv").build();
	}	
	
}
