package it.fadeout.omirl;

import it.fadeout.omirl.business.DataChart;
import it.fadeout.omirl.business.DataSerie;
import it.fadeout.omirl.business.config.HydroLinkConfig;
import it.fadeout.omirl.business.config.OmirlNavigationConfig;
import it.fadeout.omirl.business.config.SensorLinkConfig;
import it.fadeout.omirl.viewmodels.PrimitiveResult;
import it.fadeout.omirl.viewmodels.SectionChartViewModel;
import it.fadeout.omirl.viewmodels.SectionViewModel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.media.jai.operator.MinDescriptor;
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

			System.out.println("ChartService.GetChart: Config Found");
			
			// Call get user from session to update last touch if user is logged. Don't care about return here that is free access
			Omirl.getUserFromSession(sSessionId);

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

				// Get The Last File
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
				else
				{
					System.out.println("ChartService.GetChart: File Not Found with date " + oDate);
				}
			}
		}
		else
		{
			System.out.println("ChartService.GetChart: Config NOT Found");
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

		System.out.println("ChartService.ExportCsvChart: Code = " + sCode + " Chart = " + sChart);

		// Create return array List
		DataChart oDataChart = null;
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

			System.out.println("ChartService.GetChart: Config Found");

			// Cast Config
			OmirlNavigationConfig oConfig = (OmirlNavigationConfig) oConfObj;			

			// Find the right Sensor Link Configuration
			for (SensorLinkConfig oLinkConfig : oConfig.getSensorLinks()) {

				if (oLinkConfig.getCode().equals(sChart)) {
					sChart = oLinkConfig.getColumnName();
					System.out.println("ChartService.ExportCsvChart: Column Name = " +sChart);
					break;
				}
			}

			String sBasePath = oConfig.getFilesBasePath();

			sBasePath += "/charts";

			//System.out.println("ChartService.ExportCsvChart: sBasePath = " + sBasePath);

			// Get The path of the right date
			String sPath = Omirl.getSubPath(sBasePath, oDate);

			sPath += "/" + sCode + "/" + sChart;

			if (sPath != null) {

				System.out.println("ChartService.ExportCsvChart: searching path " + sPath);

				// Get The Last File
				File oLastFile = Omirl.lastFileModified(sPath, oDate);

				// Found?
				if (oLastFile != null) {

					System.out.println("ChartService.ExportCsvChart: Opening File " + oLastFile.getAbsolutePath());

					try {
						// Ok read sensors 
						oDataChart = (DataChart) Omirl.deserializeXMLToObject(oLastFile.getAbsolutePath());
					} catch (Exception e) {
						e.printStackTrace();
					}							
				}
				else
				{
					System.out.println("ChartService.ExportCsvChart: File Not Found with date " + oDate);
				}				
			}
		}
		else
		{
			System.out.println("ChartService.ExportCsvChart: Config NOT Found");
		}
		final DataChart oFinalDataChart = oDataChart;

		StreamingOutput stream = new StreamingOutput() {
			@Override
			public void write(OutputStream os) throws IOException, WebApplicationException {

				DecimalFormat oDecimalFormat = new DecimalFormat("#.##");      
				List<Integer> minIdxs = new ArrayList<Integer>();;
				ArrayList<Long> refs = new ArrayList<Long>();
				ArrayList<Integer> dataSeriesIdx = new ArrayList<Integer>();



				DateFormat oFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
				Writer writer = new BufferedWriter(new OutputStreamWriter(os));

				if (oFinalDataChart.getDataSeries()!=null 
						&& oFinalDataChart.getDataSeries().size()>0){
					//Init idxs for data series
					for (int i=0; i < oFinalDataChart.getDataSeries().size();i++){
						dataSeriesIdx.add(i,0);
					}
					
					Object[] lTmp;
					int numActiveDs = dataSeriesIdx.size();
					System.out.println("Actives idxs :"+numActiveDs);
					System.out.println("dateSerie size :"+dataSeriesIdx.size());
					System.out.println("numbers of data series" +oFinalDataChart.getDataSeries().size());
					
					
					while(numActiveDs > 0 )	{
						
						for (int i=0; i < dataSeriesIdx.size() ;  i++){
							//TODO add safety checks
							if ( Integer.MAX_VALUE != dataSeriesIdx.get(i)){
								lTmp = oFinalDataChart.getDataSeries().get(i).getData().get(dataSeriesIdx.get(i));
								refs.add((long)(lTmp[0]));
							}
						}
						
						if (refs.size() == 1 && minIdxs.isEmpty()){
							minIdxs.add(0);
						}
						else{
							minIdxs = ChartService.getMinIdxs(refs);
						}

						Date oDate = new Date((long) refs.get(0));
						writer.write("" + oFormat.format(oDate) + ";");
						DataSerie oDataSerie;
						Double dValue;
						for (int idx:minIdxs){
							oDataSerie= oFinalDataChart.getDataSeries().get(idx);
							if (oDataSerie.getData().get(dataSeriesIdx.get(idx))[1] != null){
								dValue = (Double) oDataSerie.getData().get(dataSeriesIdx.get(idx))[1];
								writer.write("" + Double.valueOf(oDecimalFormat.format(dValue)) + ";");
							}
						}
						writer.write("\n");
						
						for (Integer idx:minIdxs){
							dataSeriesIdx.set(idx,dataSeriesIdx.get(idx) + 1);
							if (! (dataSeriesIdx.get(idx) < oFinalDataChart.getDataSeries().get(idx).getData().size()) ){
								dataSeriesIdx.set(idx,Integer.MAX_VALUE);
								numActiveDs--;
							}
						}
						refs.clear();
					}

					//writer.write("test");
					

				}
				writer.flush();
			}
			
		};


		return Response.ok(stream).header("Content-Disposition", "attachment;filename="+sCode+sChart+".csv").build();
	}
	
	
	private static List<Integer> getMinIdxs(List<Long> epochs){
		
		  Long min = Collections.min(epochs);
		  
		  return IntStream.range(0, epochs.size())
		                         .filter(i -> epochs.get(i).equals(min))
		                         .mapToObj(i-> new Integer(i))
		                         .collect( Collectors.toList());
	}

	/**
	 * Gets sensors data
	 * @return
	 */
	@GET
	@Path("/sections/{sSection}/{sModel}")
	@Produces({"application/xml", "application/json", "text/xml"})
	public SectionChartViewModel GetSectionChart(@PathParam("sSection") String sSection, @PathParam("sModel") String sModel, @HeaderParam("x-session-token") String sSessionId, @HeaderParam("x-refdate") String sRefDate) {


		System.out.println("ChartService.GetSectionChart: Section = " + sSection + " Model = " + sModel);

		// Create return object
		SectionChartViewModel oDataChart = null;

		try {			

			if (Omirl.getUserFromSession(sSessionId)!=null) {
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
					System.out.println("ChartService.GetSectionChart: Config Found");

					// Cast Config
					OmirlNavigationConfig oConfig = (OmirlNavigationConfig) oConfObj;

					String sBasePath = oConfig.getFilesBasePath();

					sBasePath += "/sections/"+sModel;

					//System.out.println("ChartService.GetSectionChart: sBasePath = " + sBasePath);

					// Get The path of the right date
					String sPath = Omirl.getSubPath(sBasePath, oDate);
					
					// Create return object
					oDataChart = new SectionChartViewModel();
					
					// Find the config object
					HydroLinkConfig oSelectedHydroConfig = null;
					
					//System.out.println("ChartService.GetSectionChart:Cerco " + sModel);
					
					for (HydroLinkConfig oHydroConfig : oConfig.getFlattedHydroLinks()) {
						if (oHydroConfig.getLinkCode().equals(sModel)) {
							// This is my model
							oSelectedHydroConfig=oHydroConfig;
							System.out.println("ChartService.GetSectionChart: found " + sModel);
							break;
						}
					}
					
					//System.out.println("SectionsService.ChartService: Cerco i fratelli di " + sModel);
					
					for (HydroLinkConfig oHydroConfig : oConfig.getFlattedHydroLinks()) {
						
						if (oHydroConfig.getColFlag()!=null)
						{
							if (oHydroConfig.getColFlag().equals(oSelectedHydroConfig.getColFlag())) {
								oDataChart.getOtherChart().add(oHydroConfig.getLinkCode());
								//System.out.println("ChartService.GetSectionChart: Aggiunto " + oHydroConfig.getLinkCode());
							}
						}
					}		
					
					if (sPath != null) {

						String sSubPath = "";

						String sFeaturesPath = sPath + "/features";

						System.out.println("ChartService.GetSectionChart: Opening Path = " + sFeaturesPath);

						// Get The Last File
						File oLastFile = Omirl.lastFileModified(sFeaturesPath, oDate);

						if (oLastFile != null) {

							System.out.println("SectionsService.GetSectionChart: Opening Sections File " + oLastFile.getAbsolutePath());

							try {
								// Ok read sections 
								List<SectionViewModel> aoSections = (List<SectionViewModel>) Omirl.deserializeXMLToObject(oLastFile.getAbsolutePath());
								if (aoSections !=null) {
									if (aoSections.size()>0) {
										sSubPath = aoSections.get(0).getSubFolder();
										System.out.println("SectionsService.GetSectionChart: SubPath found [" + sSubPath + "]");
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
							}							
						}
						else
						{
							System.out.println("ChartService.GetSectionChart: File Not Found with date " + oDate);
						}							


						String sFile = "";

						if (sSubPath.equals("")){
							sFile = sPath+"/"+sSection+".png";
						}
						else {
							sFile = sPath+"/"+sSubPath + "/" + sSection+".png";
						}

						System.out.println("ChartService.GetSectionChart: searching file " + sFile);
						

						// Check if the file exists
						File oChartFile = new File(sFile);

						// Found?					
						if (oChartFile.exists()) {

							
							// Compose the img relative path
							String sRelativePath = Omirl.getSubPathWithoutCheck("img/sections/" + sModel, oDate);


							if (sSubPath.equals("")){
								sRelativePath+="/"+sSection+".png";
							}
							else {
								sRelativePath+="/"+sSubPath + "/" + sSection+".png";
							}
							// Save it
							oDataChart.setImageLink(sRelativePath);
							// and log
							System.out.println("ChartService.GetSectionChart: return path " + sRelativePath);

													

						}
						else {

							System.out.println("ChartService.GetSectionChart: File does not exists, try in the previous days");
							
							Integer oDays = 0;

							//Search back
							oDays = oConfig.getBackDaysSearch();
							
							if (oDays != null)
							{
								for(int iDay = 1; iDay <= oDays; iDay ++)
								{
									oDate = new Date( oDate.getTime() - (long)( 24 * 3600 * 1000));
									
									System.out.println("ChartService.GetSectionChart: NEW DATE " + oDate.toString());
									
									
									sPath = Omirl.getSubPath(sBasePath, oDate);
									
									if (sPath == null)
									{
										System.out.println("ChartService.GetSectionChart: no folder on this date ");
										continue;
									}
									
									sFeaturesPath = sPath + "/features";
									
									// Get The Last File
									oLastFile = Omirl.lastFileModified(sFeaturesPath, oDate);
									
									if (oLastFile != null) {

										System.out.println("ChartService.GetSectionChart: Opening Sections File " + oLastFile.getAbsolutePath());

										try {
											// Ok read sections 
											List<SectionViewModel> aoSections = (List<SectionViewModel>) Omirl.deserializeXMLToObject(oLastFile.getAbsolutePath());
											if (aoSections !=null) {
												if (aoSections.size()>0) {
													sSubPath = aoSections.get(0).getSubFolder();
													System.out.println("SectionsService.GetSectionChart: SubPath found [" + sSubPath + "]");
												}
											}
										} catch (Exception e) {
											e.printStackTrace();
										}							
									}

									String sBackFile = "";

									if (sSubPath.equals("")){
										sBackFile = sPath+"/"+sSection+".png";
									}
									else {
										sBackFile = sPath+"/"+sSubPath + "/" + sSection+".png";
									}

									System.out.println("ChartService.GetSectionChart: searching file " + sBackFile);

									// Check if the file exists
									File oBackChartFile = new File(sBackFile);

									// Found?					
									if (oBackChartFile.exists()) {
										
										System.out.println("ChartService.GetSectionChart: file exists");
										
										// Compose the img relative path
										String sRelativePath = Omirl.getSubPathWithoutCheck("img/sections/" + sModel, oDate);

										System.out.println("ChartService.GetSectionChart: relative Path: " + sRelativePath);

										if (sSubPath.equals("")){
											sRelativePath+="/"+sSection+".png";
										}
										else {
											sRelativePath+="/"+sSubPath + "/" + sSection+".png";
										}

										// Save it
										oDataChart.setImageLink(sRelativePath);
										// and log
										System.out.println("ChartService.GetSectionChart: return path " + sRelativePath);
										
										//Chart Found - break;
										break;
									}
									else
									{
										System.out.println("ChartService.GetSectionChart: file does not exists");
									}
								}
							}
							System.out.println("ChartService.GetSectionChart: cannot find file " + sFile);
						}

					}
				}
				else
				{
					System.out.println("ChartService.GetSectionChart: Config NOT Found");
				}
			}
		}
		catch(Exception oEx) {
			oEx.printStackTrace();
		}


		// Return the list of sensors
		return oDataChart;
	}	


}
