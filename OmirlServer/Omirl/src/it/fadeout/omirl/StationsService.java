package it.fadeout.omirl;

import it.fadeout.omirl.business.StationAnag;
import it.fadeout.omirl.business.config.OmirlNavigationConfig;
import it.fadeout.omirl.business.config.SensorLinkConfig;
import it.fadeout.omirl.data.StationAnagRepository;
import it.fadeout.omirl.viewmodels.MobileStation;
import it.fadeout.omirl.viewmodels.SensorListTableRowViewModel;
import it.fadeout.omirl.viewmodels.SensorListTableViewModel;
import it.fadeout.omirl.viewmodels.SensorValueRowViewModel;
import it.fadeout.omirl.viewmodels.SensorValueTableViewModel;
import it.fadeout.omirl.viewmodels.SensorViewModel;
import it.fadeout.omirl.viewmodels.StationTypeViewModel;

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
import java.util.List;

import javax.print.attribute.standard.DateTimeAtCompleted;
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
	public List<SensorViewModel> GetSensors(@PathParam("sCode") String sCode, @HeaderParam("x-session-token") String sSessionId, @HeaderParam("x-refdate") String sRefDate) {

		System.out.println("StationsService.GetSensors: Code = " + sCode);

		// Create return array List
		List<SensorViewModel> aoSensors = new ArrayList<>();
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
			for (SensorLinkConfig oLinkConfig : oConfig.getSensorLinks()) {

				if (oLinkConfig.getCode().equals(sCode)) {

					//System.out.println("StationsService.GetSensors: Sensor Code Config Found");

					// Get The path of the right date
					String sPath = Omirl.getSubPath(oLinkConfig.getFilePath(), oDate);

					if (sPath != null) {

						System.out.println("StationsService.GetSensors: searching path " + sPath);

						// Get The Last File: here use also the date and get the last before the date!!
						File oLastFile = Omirl.lastFileModified(sPath, oDate);

						// Found?
						if (oLastFile != null) {

							System.out.println("StationsService.GetSensors: Opening File " + oLastFile.getAbsolutePath());

							try {
								// Ok read sensors 
								aoSensors = (List<SensorViewModel>) Omirl.deserializeXMLToObject(oLastFile.getAbsolutePath());
								//Set Updated Date
								if (aoSensors.size() > 0)
								{
									Date oLastDate = new Date(oLastFile.lastModified()); 
									SimpleDateFormat oFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
									aoSensors.get(0).setUpdateDateTime(oFormat.format(oLastDate));
								}
							} catch (Exception e) {
								e.printStackTrace();
							}							
						}
					}
					else {
						System.out.println("StationsService.GetSensors: WARNING path is null");
					}

					// We are done
					break;
				}

			}
		}
		else
		{
			System.out.println("StationsService.GetSensors: Config NOT Found");
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

			//System.out.println("StationsService.GetStationTypes: Config Found");

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


	/**
	 * Get a list of stations near the device position
	 * @param dLat
	 * @param dLat
	 * @return A list with all the station around the mobile device position
	 */
	@GET
	@Path("/mobile/stationlist/{dLat}/{dLon}")
	@Produces({"application/xml", "application/json", "text/xml"})
	@Consumes({"application/xml", "application/json", "text/xml"})	
	public List<MobileStation> getStationsByLatLon(
			@PathParam("dLat") String dLat,
			@PathParam("dLon") String dLon
			//@HeaderParam("x-session-token") String sSessionId
			)
			{

		System.out.println("StationsService.GetSensors: GetStationsByLatLon = [" + dLat + ", " + dLon + "]");

		// Create return array List
		ArrayList<MobileStation> iStationsList = new ArrayList<MobileStation>();

		try {
			// perform query
			ArrayList<StationAnag> oStationList = new StationAnagRepository().getListByLatLon( Double.parseDouble(dLat), Double.parseDouble(dLon), 5);
			// and build the retval array
			for(StationAnag stationAnag : oStationList)
			{
				iStationsList.add( new MobileStation(stationAnag) );
			}

		} 
		catch (Exception e) {
			e.printStackTrace();
		}	


		// Return the list of stations
		return iStationsList;
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

			//System.out.println("StationsService.GetStationsListTable: Config Found");

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

			//System.out.println("StationsService.GetStationsListTable: Config Found");

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






	@GET
	@Path("/sensorvalues/{sCode}")
	@Produces({"application/xml", "application/json", "text/xml"})
	@Consumes({"application/xml", "application/json", "text/xml"})	
	public SensorValueTableViewModel GetSensorValuesTable(@PathParam("sCode") String sCode, @HeaderParam("x-session-token") String sSessionId, @HeaderParam("x-refdate") String sRefDate) {

		System.out.println("StationsService.GetSensorValuesTable: Code = " + sCode);

		// Create return array List
		SensorValueTableViewModel oTable = new SensorValueTableViewModel();
		// Date: received from client
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

			//System.out.println("StationsService.GetSensorValuesTable: Config Found");

			// Cast Config
			OmirlNavigationConfig oConfig = (OmirlNavigationConfig) oConfObj;

			String sPath = oConfig.getFilesBasePath()+"/tables/sensorvalues/"+sCode;

			// Get The path of the right date
			sPath = Omirl.getSubPath(sPath, oDate);

			if (sPath != null) {

				System.out.println("StationsService.ExportSensorValuesTable: searching path " + sPath);

				// Get The Last File
				File oLastFile = Omirl.lastFileModified(sPath, oDate);

				// Found?
				if (oLastFile != null) {

					System.out.println("StationsService.ExportSensorValuesTable: Opening File " + oLastFile.getAbsolutePath());

					try {
						// Ok read sensors 
						oTable = (SensorValueTableViewModel) Omirl.deserializeXMLToObject(oLastFile.getAbsolutePath());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
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
	@Path("/exportsensorvalues/{sCode}")
	@Produces({"application/octet-stream"})
	public Response ExportSensorValuesTable(@PathParam("sCode") String sCode, @HeaderParam("x-session-token") String sSessionId, @HeaderParam("x-refdate") String sRefDate) {

		System.out.println("StationsService.ExportSensorValuesTable: Code = " + sCode);

		// Create return array List
		SensorValueTableViewModel oTable = new SensorValueTableViewModel();
		// Date: received from client
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

			//System.out.println("StationsService.ExportSensorValuesTable: Config Found");

			// Cast Config
			OmirlNavigationConfig oConfig = (OmirlNavigationConfig) oConfObj;

			String sPath = oConfig.getFilesBasePath()+"/tables/sensorvalues/"+sCode;

			// Get The path of the right date
			sPath = Omirl.getSubPath(sPath, oDate);

			if (sPath != null) {

				System.out.println("StationsService.ExportSensorValuesTable: searching path " + sPath);

				// Get The Last File
				File oLastFile = Omirl.lastFileModified(sPath, oDate);

				// Found?
				if (oLastFile != null) {

					System.out.println("StationsService.ExportSensorValuesTable: Opening File " + oLastFile.getAbsolutePath());

					try {
						// Ok read sensors 
						oTable = (SensorValueTableViewModel) Omirl.deserializeXMLToObject(oLastFile.getAbsolutePath());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}

		final SensorValueTableViewModel oFinalTable = oTable;

		StreamingOutput stream = new StreamingOutput() {
			@Override
			public void write(OutputStream os) throws IOException, WebApplicationException {
				Writer writer = new BufferedWriter(new OutputStreamWriter(os));

				if (oFinalTable.getTableRows()!=null)
				{
					if (oFinalTable.getTableRows().size()>0) 
					{

						writer.write("Codice;Nome;Provincia;Area;Bacino;Comune;Ultimo;Min;Max\n");

						for (int iTableRows=0; iTableRows<oFinalTable.getTableRows().size(); iTableRows++) {

							SensorValueRowViewModel oRow = oFinalTable.getTableRows().get(iTableRows);

							String sStationCode = oRow.getCode();
							String sName = oRow.getName();
							String sDistrict = oRow.getDistrict();
							String sArea = oRow.getArea();
							String sBasin = oRow.getBasin();
							String sMunicipality = oRow.getMunicipality();

							Double dLast = oRow.getLast();
							Double dMin = oRow.getMin();
							Double dMax = oRow.getMax();

							String sLast = "";
							String sMin = "";
							String sMax = "";

							if (sStationCode == null) sStationCode ="";
							if (sName == null) sName ="";
							if (sDistrict == null) sDistrict ="";
							if (sArea == null) sArea ="";
							if (sBasin == null) sBasin ="";
							if (sMunicipality == null) sMunicipality ="";

							if (dLast!=null) sLast=dLast.toString();
							if (dMin!=null) sMin=dMin.toString();
							if (dMax!=null) sMax=dMax.toString();

							writer.write(sStationCode+";");
							writer.write(sName+";");
							writer.write(sDistrict+";");
							writer.write(sArea+";");
							writer.write(sBasin+";");
							writer.write(sMunicipality+";");
							writer.write(sLast+";");
							writer.write(sMin+";");
							writer.write(sMax);
							writer.write("\n");
						}
					}
				}

				//writer.write("test");
				writer.flush();

			}
		};


		return Response.ok(stream).header("Content-Disposition", "attachment;filename="+sCode+"_Values.csv").build();
	}	

}
