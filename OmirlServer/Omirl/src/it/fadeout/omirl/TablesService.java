package it.fadeout.omirl;

import it.fadeout.omirl.business.StationAnag;
import it.fadeout.omirl.business.config.OmirlNavigationConfig;
import it.fadeout.omirl.business.config.TableLinkConfig;
import it.fadeout.omirl.data.StationAnagRepository;
import it.fadeout.omirl.viewmodels.MaxTableRowViewModel;
import it.fadeout.omirl.viewmodels.MaxTableViewModel;
import it.fadeout.omirl.viewmodels.PrimitiveResult;
import it.fadeout.omirl.viewmodels.SensorViewModel;
import it.fadeout.omirl.viewmodels.SummaryInfo;
import it.fadeout.omirl.viewmodels.TableLink;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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

@Path("/tables")
public class TablesService {

	@Context
	ServletConfig m_oServletConfig;


	@GET
	@Path("/test")
	@Produces({"application/xml", "application/json", "text/xml"})
	public PrimitiveResult TestOmirl() {
		// Just a keep alive message
		PrimitiveResult oTest = new PrimitiveResult();
		oTest.StringValue = "Table Service is Working";
		return oTest;
	}

	@GET
	@Path("/tablelinks")
	@Produces({"application/xml", "application/json", "text/xml"})	
	public ArrayList<TableLink> getTableLinks(@HeaderParam("x-session-token") String sSessionId) {

		ArrayList<TableLink> aoRet = new ArrayList<>();

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

				for (int iConfigured =0 ; iConfigured< oConfig.getTableLinks().size(); iConfigured++ )
				{
					TableLinkConfig oTableLink = oConfig.getTableLinks().get(iConfigured);

					boolean bAdd = false;

					if (bShowPrivate==true) bAdd = true;
					else {
						if (oTableLink.isPrivate() == false) bAdd = true;
					}

					if (bAdd){
						aoRet.add(oTableLink.getTableLink());
					}
				}
			}


		}
		catch(Exception oEx) {
			oEx.printStackTrace();
		}

		return aoRet;
	}


	@GET
	@Path("/datatablelinks")
	@Produces({"application/xml", "application/json", "text/xml"})	
	public ArrayList<TableLink> getDataTableLinks(@HeaderParam("x-session-token") String sSessionId) {

		ArrayList<TableLink> aoRet = new ArrayList<>();

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

				for (int iConfigured =0 ; iConfigured< oConfig.getDataTableLinks().size(); iConfigured++ )
				{
					TableLinkConfig oTableLink = oConfig.getDataTableLinks().get(iConfigured);

					boolean bAdd = false;

					if (bShowPrivate==true) bAdd = true;
					else {
						if (oTableLink.isPrivate() == false) bAdd = true;
					}

					if (bAdd){
						aoRet.add(oTableLink.getTableLink());
					}
				}
			}			
		}
		catch(Exception oEx) {
			oEx.printStackTrace();
		}

		return aoRet;
	}	

	@GET
	@Path("/summary")
	@Produces({"application/xml", "application/json", "text/xml"})
	public SummaryInfo GetSummaryTable(@HeaderParam("x-session-token") String sSessionId, @HeaderParam("x-refdate") String sRefDate) {
		System.out.println("TablesService.GetSummaryTable");

		// Create return array List
		SummaryInfo oSummaryInfo = null;
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

			String sBasePath = oConfig.getFilesBasePath();

			sBasePath += "/tables/summary";

			System.out.println("TablesService.GetSummaryTable = " + sBasePath);

			// Get The path of the right date
			String sPath = Omirl.getSubPath(sBasePath, oDate);

			if (sPath != null) {

				System.out.println("TablesService.GetSummaryTable: searching path " + sPath);

				// Get The Last File: TODO: here use also the date and get the last before the date!!
				File oLastFile = Omirl.lastFileModified(sPath, oDate);

				// Found?
				if (oLastFile != null) {

					System.out.println("TablesService.GetSummaryTable: Opening File " + oLastFile.getAbsolutePath());

					try {
						// Ok read sensors 
						oSummaryInfo = (SummaryInfo) Omirl.deserializeXMLToObject(oLastFile.getAbsolutePath());
					} catch (Exception e) {
						e.printStackTrace();
					}							
				}
			}
		}

		// Return the list of sensors
		return oSummaryInfo;
	}




	@GET
	@Path("/max")
	@Produces({"application/xml", "application/json", "text/xml"})
	public MaxTableViewModel GetMaxTable(@HeaderParam("x-session-token") String sSessionId, @HeaderParam("x-refdate") String sRefDate) {
		System.out.println("TablesService.GetMaxTable");

		// Create return array List
		MaxTableViewModel oMaxTable = null;
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

			String sBasePath = oConfig.getFilesBasePath();

			sBasePath += "/tables/max";

			System.out.println("TablesService.GetMaxTable = " + sBasePath);

			// Get The path of the right date
			String sPath = Omirl.getSubPath(sBasePath, oDate);

			if (sPath != null) {

				System.out.println("TablesService.GetSummaryTable: searching path " + sPath);

				// Get The Last File:
				File oLastFile = Omirl.lastFileModified(sPath, oDate);

				// Found?
				if (oLastFile != null) {

					System.out.println("TablesService.GetSummaryTable: Opening File " + oLastFile.getAbsolutePath());

					try {
						// Ok read sensors 
						oMaxTable = (MaxTableViewModel) Omirl.deserializeXMLToObject(oLastFile.getAbsolutePath());
					} catch (Exception e) {
						e.printStackTrace();
					}							
				}
			}
		}

		// Return the list of sensors
		return oMaxTable;
	}
	
	
	/**
	 * Get a list of stations near the device position
	 * @param dLat
	 * @param dLat
	 * @return A list with all the station around the mobile device position
	 */
	@GET
	@Path("/anag/{sCode}")
	@Produces({"application/xml", "application/json", "text/xml"})
	@Consumes({"application/xml", "application/json", "text/xml"})	
	public SensorViewModel getStationsAnag(@HeaderParam("x-session-token") String sSessionId, @HeaderParam("x-refdate") String sRefDate, @PathParam("sCode") String sCode)
	{
		SensorViewModel oViewModel = new SensorViewModel();
		StationAnagRepository oRepo = new StationAnagRepository();
		StationAnag oAnag = oRepo.selectByStationCode(sCode);
		if (oAnag != null)
		{
			oViewModel.setMunicipality(oAnag.getMunicipality());
			oViewModel.setName(oAnag.getName());
			oViewModel.setShortCode(oAnag.getStation_code());
		}
		
		return oViewModel;
		
		
	}
	
	@GET
	@Path("/anagByName/{sName}")
	@Produces({"application/xml", "application/json", "text/xml"})
	@Consumes({"application/xml", "application/json", "text/xml"})	
	public SensorViewModel getStationsAnagByName(@HeaderParam("x-session-token") String sSessionId, @HeaderParam("x-refdate") String sRefDate, @PathParam("sName") String sName)
	{
		System.out.println("TableService.getStationsAnagByName: start");
		
		SensorViewModel oViewModel = new SensorViewModel();
		
		try
		{
			StationAnagRepository oRepo = new StationAnagRepository();
			StationAnag oAnag = oRepo.selectByName(sName);
			if (oAnag != null)
			{
				
				oViewModel.setMunicipality(oAnag.getMunicipality());
				oViewModel.setName(oAnag.getName());
				oViewModel.setShortCode(oAnag.getStation_code());
				
				System.out.println("TableService.getStationsAnagByName: Name = " + sName + " Code = " + oAnag.getStation_code());
			}
			else
			{
				System.out.println("TableService.getStationsAnagByName: Name = " + sName + " Not Found");
			}
			
		}
		catch(Exception oEx)
		{
			System.out.println("TableService.getStationsAnagByName: Eccezione");
			oEx.printStackTrace();
		}
		
		return oViewModel;
		
		
	}


	/**
	 * Gets sensors data
	 * @return
	 */
	@GET
	@Path("/exportmaxvalues")
	@Produces({"application/octet-stream"})
	public Response ExportSensorValuesTable(@HeaderParam("x-session-token") String sSessionId, @HeaderParam("x-refdate") String sRefDate) {


		// Create return array List
		MaxTableViewModel oMaxTable = null;
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

			String sBasePath = oConfig.getFilesBasePath();

			sBasePath += "/tables/max";

			System.out.println("TablesService.GetMaxTable = " + sBasePath);

			// Get The path of the right date
			String sPath = Omirl.getSubPath(sBasePath, oDate);

			if (sPath != null) {

				System.out.println("TablesService.GetSummaryTable: searching path " + sPath);

				// Get The Last File:
				File oLastFile = Omirl.lastFileModified(sPath, oDate);

				// Found?
				if (oLastFile != null) {

					System.out.println("TablesService.GetSummaryTable: Opening File " + oLastFile.getAbsolutePath());

					try {
						// Ok read sensors 
						oMaxTable = (MaxTableViewModel) Omirl.deserializeXMLToObject(oLastFile.getAbsolutePath());
					} catch (Exception e) {
						e.printStackTrace();
					}							
				}
			}
		}


		final MaxTableViewModel oFinalTable = oMaxTable;

		StreamingOutput stream = new StreamingOutput() {
			@Override
			public void write(OutputStream os) throws IOException, WebApplicationException {
				Writer writer = new BufferedWriter(new OutputStreamWriter(os));

				if (oFinalTable.getAlertZones()!=null)
				{
					if (oFinalTable.getAlertZones().size()>0) 
					{
						writer.write("Zona;5m;15m;30m;1h;3h;6h;12h;24h;\n");

						for (int iTableRows=0; iTableRows<oFinalTable.getAlertZones().size(); iTableRows++) {

							MaxTableRowViewModel oRow = oFinalTable.getAlertZones().get(iTableRows);

							String sZona = oRow.getName();
							String s5m = oRow.getM5val() + " " + oRow.getM5();
							String s15m = oRow.getM15val() + " " + oRow.getM15();
							String s30m = oRow.getM30val() + " " + oRow.getM30();
							String s1h = oRow.getH1val() + " " + oRow.getH1();
							String s3h = oRow.getH3val() + " " + oRow.getH3();
							String s6h = oRow.getH6val() + " " + oRow.getH6();
							String s12h = oRow.getH12val() + " " + oRow.getH12();
							String s24h = oRow.getH24val() + " " + oRow.getH24();

							if (sZona == null) sZona ="";
							if (s5m == null) s5m ="";
							if (s15m == null) s15m ="";
							if (s30m == null) s30m ="";
							if (s1h == null) s1h ="";
							if (s3h == null) s3h ="";
							if (s6h == null) s6h ="";
							if (s12h == null) s12h ="";
							if (s24h == null) s24h ="";

							writer.write(sZona+";");
							writer.write(s5m+";");
							writer.write(s15m+";");
							writer.write(s30m+";");
							writer.write(s1h+";");
							writer.write(s3h+";");
							writer.write(s6h+";");
							writer.write(s12h+";");
							writer.write(s24h+";");
							writer.write("\n");

						}
					}
				}

				if (oFinalTable.getDistricts().size()>0) 
				{
					writer.write("Provincia;5m;15m;30m;1h;3h;6h;12h;24h;\n");

					for (int iTableRows=0; iTableRows<oFinalTable.getDistricts().size(); iTableRows++) {

						MaxTableRowViewModel oRow = oFinalTable.getDistricts().get(iTableRows);

						String sZona = oRow.getName();
						String s5m = oRow.getM5val() + " " + oRow.getM5();
						String s15m = oRow.getM15val() + " " + oRow.getM15();
						String s30m = oRow.getM30val() + " " + oRow.getM30();
						String s1h = oRow.getH1val() + " " + oRow.getH1();
						String s3h = oRow.getH3val() + " " + oRow.getH3();
						String s6h = oRow.getH6val() + " " + oRow.getH6();
						String s12h = oRow.getH12val() + " " + oRow.getH12();
						String s24h = oRow.getH24val() + " " + oRow.getH24();

						if (sZona == null) sZona ="";
						if (s5m == null) s5m ="";
						if (s15m == null) s15m ="";
						if (s30m == null) s30m ="";
						if (s1h == null) s1h ="";
						if (s3h == null) s3h ="";
						if (s6h == null) s6h ="";
						if (s12h == null) s12h ="";
						if (s24h == null) s24h ="";

						writer.write(sZona+";");
						writer.write(s5m+";");
						writer.write(s15m+";");
						writer.write(s30m+";");
						writer.write(s1h+";");
						writer.write(s3h+";");
						writer.write(s6h+";");
						writer.write(s12h+";");
						writer.write(s24h+";");
						writer.write("\n");

					}
				}

				//writer.write("test");
				writer.flush();

			}
		};


		return Response.ok(stream).header("Content-Disposition", "attachment;filename=Max_Values.csv").build();
	}	

}
