package it.fadeout.omirl;

import it.fadeout.omirl.business.DataChart;
import it.fadeout.omirl.business.DataSerie;
import it.fadeout.omirl.business.config.OmirlNavigationConfig;
import it.fadeout.omirl.business.config.SensorLinkConfig;
import it.fadeout.omirl.viewmodels.PrimitiveResult;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletConfig;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

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
	public DataChart GetChart(@PathParam("sCode") String sCode, @PathParam("sChart") String sChart, @HeaderParam("x-session-token") String sSessionId, @HeaderParam("x-refdate") String sRefDate) {
		
		System.out.println("ChartService.GetChart: Code = " + sCode + " Chart = " + sChart);
		
		// Create return array List
		DataChart oDataChart = null;
		// Date: will be received from client...
		Date oDate = new Date();
		
		if (sRefDate!=null)
		{
			if (sRefDate.equals("") == false) 
			{
				// TODO: Try e catch per fare il parsing 
				// se è valido sostituire oDate.
			}
		}
		
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
			
			sBasePath += "/charts";
			
			System.out.println("ChartService.GetChart: sBasePath = " + sBasePath);
			
			// Get The path of the right date
			String sPath = Omirl.getSubPath(sBasePath, oDate);
			
			sPath += "/" + sCode + "/" + sChart;
			
			if (sPath != null) {
				
				System.out.println("ChartService.GetChart: searching path " + sPath);
				
				// Get The Last File: TODO: here use also the date and get the last before the date!!
				File oLastFile = Omirl.lastFileModified(sPath, oDate);
				
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
	
	
	/**
	 * Gets sensors data
	 * @return
	 */
	@GET
	@Path("/csv/{sCode}/{sChart}")
	@Produces({"application/octet-stream"})
	public Response ExportCsvChart(@PathParam("sCode") String sCode, @PathParam("sChart") String sChart, @HeaderParam("x-session-token") String sSessionId, @HeaderParam("x-refdate") String sRefDate) {
		
	  	System.out.println("ChartService.GetChart: Code = " + sCode + " Chart = " + sChart);
		
		// Create return array List
		DataChart oDataChart = null;
		// Date: will be received from client...
		Date oDate = new Date();
		
		if (sRefDate!=null)
		{
			if (sRefDate.equals("") == false) 
			{
				// TODO: Try e catch per fare il parsing 
				// se è valido sostituire oDate.
			}
		}
		
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
			
			sBasePath += "/charts";
			
			System.out.println("ChartService.GetChart: sBasePath = " + sBasePath);
			
			// Get The path of the right date
			String sPath = Omirl.getSubPath(sBasePath, oDate);
			
			sPath += "/" + sCode + "/" + sChart;
			
			if (sPath != null) {
				
				System.out.println("ChartService.GetChart: searching path " + sPath);
				
				// Get The Last File: TODO: here use also the date and get the last before the date!!
				File oLastFile = Omirl.lastFileModified(sPath, oDate);
				
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
		
		final DataChart oFinalDataChart = oDataChart;
		
		StreamingOutput stream = new StreamingOutput() {
			  @Override
			  public void write(OutputStream os) throws IOException, WebApplicationException {
				  
				  DecimalFormat oDecimalFormat = new DecimalFormat("#.##");      
				  

				  DateFormat oFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm");
			      Writer writer = new BufferedWriter(new OutputStreamWriter(os));
			      
			      if (oFinalDataChart.getDataSeries()!=null)
			      {
				      if (oFinalDataChart.getDataSeries().size()>0) 
				      {
				    	  DataSerie oDataSerie =oFinalDataChart.getDataSeries().get(0);
				    	  
				    	  if (oDataSerie.getData() != null) {
				    		  for (int iRows=0; iRows<oDataSerie.getData().size(); iRows++) {
				    			  Object [] adValues = oDataSerie.getData().get(iRows);
				    			  
				    			  Date oDate = new Date((long) adValues[0]); 
				    			  
				    			  writer.write("" + oFormat.format(oDate) + ";");
				    			  
				    			  for (int iDataSerie = 0; iDataSerie < oFinalDataChart.getDataSeries().size(); iDataSerie++) {
				    				  DataSerie oActualSerie = oFinalDataChart.getDataSeries().get(iDataSerie);
				    				  
				    				  if (oActualSerie.getData() != null) 
				    				  {
				    					  if (iRows<oActualSerie.getData().size())
				    					  { 
				    						  Object [] adActualValues = oActualSerie.getData().get(iRows);
				    						  
				    						  if (adActualValues!= null){
				    							  if (adActualValues[1]!=null)  {
				    								  Double dValue = (Double) adActualValues[1];
				    								  //String sValue = String.format("%2f",dValue);
				    								  
				    								  
				    								  writer.write("" + Double.valueOf(oDecimalFormat.format(dValue)) + ";");
				    							  }
				    							  else {
				    								  writer.write(";");
				    							  }
				    						  }
			    							  else {
			    								  writer.write(";");
			    							  }				    						  
				    					  }
				    				  }
				    				 
				    			  }
				    			  
				    			  writer.write("\n");
				    			  
				    		  }
				    	  }
				      }
			      }
			      
			      //writer.write("test");
			      writer.flush();
			      
			  }
		};
		
		
	  	return Response.ok(stream).header("Content-Disposition", "attachment;filename="+sCode+sChart+".csv").build();
	}	

}
