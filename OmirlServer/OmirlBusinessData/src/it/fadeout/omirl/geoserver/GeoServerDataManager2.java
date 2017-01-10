package it.fadeout.omirl.geoserver;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.UUID;
import java.util.Vector;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.FeatureStore;
import org.geotools.data.FeatureWriter;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.feature.FeatureIterator;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.opengis.feature.Feature;
import org.opengis.feature.type.PropertyDescriptor;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

import sun.misc.BASE64Encoder;

public class GeoServerDataManager2 implements IGeoServerDataManager {

	private  String m_GsUrl= null;
	private  String m_cookieString = null;
	private  String m_GsUser = null;
	private  String m_GsPsw = null;
	private static String SHAPE_DSTORE_NAME = null;
	//private static String COVERAGE_DSTORE_NAME = "dew_coverage";

	private Object m_oGeoServerCriticalSection = new Object();

	public GeoServerDataManager2(String gsUrl, String cookieStr, String gsUser, String gsPsw) {

		m_GsUrl= gsUrl;
		m_cookieString = cookieStr;
		m_GsUser = gsUser;
		m_GsPsw = gsPsw;
		SHAPE_DSTORE_NAME = "nfs_shp";
	}


	public void addLayer(String sLayerName, String sNameSpace, String gsFile, String styleId) throws MalformedURLException, IOException {

		System.out.println("addLayer: Name: " + sLayerName + " NameSpace: " + sNameSpace + " File " + gsFile + " Style " + styleId);

		synchronized(m_oGeoServerCriticalSection) {

			//crea il coveragestores
			URL oUrlCoverageStore = new URL(m_GsUrl + "rest/workspaces/" + sNameSpace + "/coveragestores/");

			System.out.println("COVERAGE STORE: " + oUrlCoverageStore.toString());

			String sInDataStore =  "<coverageStore>"
					+ "<name>" + sLayerName + "</name>"
					+ "<enabled>true</enabled>"
					+ "<workspace><name>" + sNameSpace + "</name></workspace>"
					+ "<type>GeoTIFF</type>"
					+ "<url>file:" + gsFile + "</url>" 
					+ "</coverageStore>";
			geoserverAction("POST", oUrlCoverageStore, m_GsUser, m_GsPsw, sInDataStore);

			//crea il coverage
			URL oUrlCoverages = new URL(m_GsUrl + "rest/workspaces/" + sNameSpace + "/coveragestores/" + sLayerName + "/coverages/");

			System.out.println("COVERAGE: " + oUrlCoverages.toString());

			String sInCoverages = "<coverage>"
					+ "<name>" + sLayerName + "</name>"
					+ "<nativeName>" + sLayerName + "</nativeName>"
					+ "<namespace><name>" + sNameSpace + "</name></namespace>"
					+ "<nativeCRS>EPSG:4326</nativeCRS>"
					+ "<srs>EPSG:4326</srs>"
					+ "<nativeBoundingBox>"
					+ "<minx>5.0</minx><maxx>19.0</maxx><miny>37.0</miny><maxy>50.0</maxy>"
					+ "<crs>EPSG:4326</crs>"
					+ "</nativeBoundingBox>"
					+ "<latLonBoundingBox>"
					+ "<minx>5.0</minx><maxx>19.0</maxx><miny>37.0</miny><maxy>50.0</maxy>"
					+ "<crs>EPSG:4326</crs>"
					+ "</latLonBoundingBox>"
					+ "<projectionPolicy>FORCE_DECLARED</projectionPolicy>"
					+ "<enabled>true</enabled>"
					+ "<store><name>" + sLayerName + "</name></store>"
					+ "<grid><range><low>0 0</low><high>0 0</high></range><transform><scaleX>0.5</scaleX><scaleY>-0.5</scaleY><shearX>0.0</shearX><shearX>0.0</shearX>"
					+ "<translateX>0.0</translateX><translateY>0.0</translateY></transform><crs>EPSG:4326</crs></grid>"
					+ "</coverage>";

			geoserverAction("POST", oUrlCoverages, m_GsUser, m_GsPsw, sInCoverages);


			//definisce lo stile del layer

			URL oUrlLayerStyle = new URL(m_GsUrl + "rest/layers/"+ sNameSpace + ":" + sLayerName);

			System.out.println("LAYER: " + oUrlLayerStyle.toString());

			String sInStyle = "<layer>"
					+ "<name>" + sLayerName + "</name>"
					+ "<defaultStyle><name>" + styleId + "</name></defaultStyle>"
					+ "<enabled>true</enabled>"
					+ "</layer>";

			geoserverAction("PUT", oUrlLayerStyle, m_GsUser, m_GsPsw, sInStyle);

		}

	}


	public void addShapeLayer(String sLayerName, String sNameSpace, String styleId, String sShpStore) throws MalformedURLException, IOException {

		if(sShpStore == null) sShpStore = SHAPE_DSTORE_NAME;

		synchronized(m_oGeoServerCriticalSection) {

			//crea il featureType
			URL oUrlFeaturetypes = new URL(m_GsUrl + "rest/workspaces/" + sNameSpace + "/datastores/" + sShpStore + "/featuretypes/");

			String sInFeatureType = "<featureType>"
					+ "<name>" + sLayerName + "</name>"
					+ "<nativeName>" + sLayerName + "</nativeName>"
					+ "<namespace><name>" + sNameSpace + "</name></namespace>"
					+ "<nativeCRS>EPSG:4326</nativeCRS>"
					+ "<srs>EPSG:4326</srs>"
					+ "<nativeBoundingBox>"
					+ "<minx>5.0</minx><maxx>19.0</maxx><miny>37.0</miny><maxy>50.0</maxy>"
					+ "<crs>EPSG:4326</crs>"
					+ "</nativeBoundingBox>"
					+ "<latLonBoundingBox>"
					+ "<minx>5.0</minx><maxx>19.0</maxx><miny>37.0</miny><maxy>50.0</maxy>"
					+ "<crs>EPSG:4326</crs>"
					+ "</latLonBoundingBox>"
					+ "<projectionPolicy>FORCE_DECLARED</projectionPolicy>"
					+ "<enabled>true</enabled>"
					+ "<store><name>" + sShpStore + "</name></store>"
					+ "</featureType>";


			geoserverAction("POST", oUrlFeaturetypes, m_GsUser, m_GsPsw, sInFeatureType);

			//definisce lo stile del layer
			URL oUrlLayerStyle = new URL(m_GsUrl + "rest/layers/"+ sNameSpace + ":" + sLayerName);

			String sInStyle = "<layer>"
					+ "<name>" + sLayerName + "</name>"
					+ "<defaultStyle><name>" + styleId + "</name></defaultStyle>"
					+ "<enabled>true</enabled>"
					+ "</layer>";

			geoserverAction("PUT", oUrlLayerStyle, m_GsUser, m_GsPsw, sInStyle);

		}
	}




	public void addShapeLayer(String sLayerName, String sNameSpace,
			String styleId) throws MalformedURLException,
	IOException {

		addShapeLayer(sLayerName, sNameSpace, styleId, null);

		//		synchronized(m_oGeoServerCriticalSection) {
		//
		//			//crea il featureType
		//			URL oUrlFeaturetypes = new URL(m_GsUrl + "rest/workspaces/" + sNameSpace + "/datastores/" + SHAPE_DSTORE_NAME + "/featuretypes/");
		//		
		//			String sInFeatureType = "<featureType>"
		//				+ "<name>" + sLayerName + "</name>"
		//				+ "<nativeName>" + sLayerName + "</nativeName>"
		//				+ "<namespace><name>" + sNameSpace + "</name></namespace>"
		//				+ "<nativeCRS>EPSG:4326</nativeCRS>"
		//				+ "<srs>EPSG:4326</srs>"
		//				+ "<nativeBoundingBox>"
		//				+ "<minx>5.0</minx><maxx>19.0</maxx><miny>37.0</miny><maxy>50.0</maxy>"
		//				+ "<crs>EPSG:4326</crs>"
		//				+ "</nativeBoundingBox>"
		//				+ "<latLonBoundingBox>"
		//				+ "<minx>5.0</minx><maxx>19.0</maxx><miny>37.0</miny><maxy>50.0</maxy>"
		//				+ "<crs>EPSG:4326</crs>"
		//				+ "</latLonBoundingBox>"
		//				+ "<projectionPolicy>FORCE_DECLARED</projectionPolicy>"
		//				+ "<enabled>true</enabled>"
		//				+ "<store><name>" + SHAPE_DSTORE_NAME + "</name></store>"
		//				+ "</featureType>";
		//
		//			
		//			geoserverAction("POST", oUrlFeaturetypes, m_GsUser, m_GsPsw, sInFeatureType);
		//
		//			//definisce lo stile del layer
		//			URL oUrlLayerStyle = new URL(m_GsUrl + "rest/layers/"+ sNameSpace + ":" + sLayerName);
		//
		//			String sInStyle = "<layer>"
		//				+ "<name>" + sLayerName + "</name>"
		//				+ "<defaultStyle><name>" + styleId + "</name></defaultStyle>"
		//				+ "<enabled>true</enabled>"
		//				+ "</layer>";
		//
		//			geoserverAction("PUT", oUrlLayerStyle, m_GsUser, m_GsPsw, sInStyle);
		//
		//		}
	}

	public void addPostgisLayer(String sGsPostgisStore, String sLayerName, String sNameSpace, String styleId) throws MalformedURLException,
	IOException {

		synchronized(m_oGeoServerCriticalSection) {

			//crea il featureType
			URL oUrlFeaturetypes = new URL(m_GsUrl + "rest/workspaces/" + sNameSpace + "/datastores/" + sGsPostgisStore + "/featuretypes/");

			String sInFeatureType = "<featureType>"
					+ "<name>" + sLayerName + "</name>"
					+ "<nativeName>" + sLayerName + "</nativeName>"
					+ "<namespace><name>" + sNameSpace + "</name></namespace>"
					+ "<nativeCRS>EPSG:4326</nativeCRS>"
					+ "<srs>EPSG:4326</srs>"
					+ "<nativeBoundingBox>"
					+ "<minx>5.0</minx><maxx>19.0</maxx><miny>37.0</miny><maxy>50.0</maxy>"
					+ "<crs>EPSG:4326</crs>"
					+ "</nativeBoundingBox>"
					+ "<latLonBoundingBox>"
					+ "<minx>5.0</minx><maxx>19.0</maxx><miny>37.0</miny><maxy>50.0</maxy>"
					+ "<crs>EPSG:4326</crs>"
					+ "</latLonBoundingBox>"
					+ "<projectionPolicy>FORCE_DECLARED</projectionPolicy>"
					+ "<enabled>true</enabled>"
					+ "<store><name>" + sGsPostgisStore + "</name></store>"
					+ "</featureType>";


			geoserverAction("POST", oUrlFeaturetypes, m_GsUser, m_GsPsw, sInFeatureType);

			//definisce lo stile del layer
			URL oUrlLayerStyle = new URL(m_GsUrl + "rest/layers/"+ sNameSpace + ":" + sLayerName);

			String sInStyle = "<layer>"
					+ "<name>" + sLayerName + "</name>"
					+ "<defaultStyle><name>" + styleId + "</name></defaultStyle>"
					+ "<enabled>true</enabled>"
					+ "</layer>";

			geoserverAction("PUT", oUrlLayerStyle, m_GsUser, m_GsPsw, sInStyle);

		}
	}

	public void addLayerGroup(String sLayerGroupId, String[] asLayerName) throws MalformedURLException,
	IOException {

		synchronized(m_oGeoServerCriticalSection) {

			URL oUrlSetLayersGroup = new URL(m_GsUrl + "rest/layergroups");

			String sXmlLayerName = "";
			for (int i = 0; i < asLayerName.length; i++) {
				sXmlLayerName =  sXmlLayerName + "<layer>" + asLayerName[i] + "</layer>";
			}

			String sInFeatureType = "<layerGroup>"
					+ "<name>" + sLayerGroupId + "</name>"
					+ "<layers>" + sXmlLayerName +  "</layers>"
					+ "</layerGroup>";

			geoserverAction("POST", oUrlSetLayersGroup, m_GsUser, m_GsPsw, sInFeatureType);

		}
	}

	public void removeLayer(String sDelGSLayerName, String sNameSpace)
			throws MalformedURLException, IOException {

		synchronized(m_oGeoServerCriticalSection) {


			if (sDelGSLayerName.startsWith(SHP_LAYER_ID_PREFIX)) {

				//elimina il Layer
				URL oUrlLayerDelete = new URL(m_GsUrl + "rest/layers/"+ sNameSpace + ":" + sDelGSLayerName);	
				geoserverAction("DELETE", oUrlLayerDelete, m_GsUser, m_GsPsw, null);
				//elimina il Featuretype
				URL oUrlDelFeaturetypes = new URL(m_GsUrl + "rest/workspaces/" + sNameSpace + "/datastores/" + SHAPE_DSTORE_NAME + "/featuretypes/" + sDelGSLayerName);
				geoserverAction("DELETE", oUrlDelFeaturetypes, m_GsUser, m_GsPsw, null);


			} else {

				//elimina il Layer
				URL oUrlLayerDelete = new URL(m_GsUrl + "rest/layers/"+ sNameSpace + ":" + sDelGSLayerName);	
				geoserverAction("DELETE", oUrlLayerDelete, m_GsUser, m_GsPsw, null);

				//elimina il Coverage
				URL oUrlDelCoverages = new URL(m_GsUrl + "rest/workspaces/" + sNameSpace + "/coveragestores/" + sDelGSLayerName + "/coverages/" + sDelGSLayerName);
				geoserverAction("DELETE", oUrlDelCoverages, m_GsUser, m_GsPsw, null);

				//elimina il CoverageStore
				URL oUrlDelCoverageStore = new URL(m_GsUrl + "rest/workspaces/" + sNameSpace + "/coveragestores/" + sDelGSLayerName);
				geoserverAction("DELETE", oUrlDelCoverageStore, m_GsUser, m_GsPsw, null);


			}

		}

	}


	public void DeleteFile(String oFileName) {

		File oFile = new File(oFileName);

		//Verifico l'esistenza del file
		if (!oFile.exists()) throw new IllegalArgumentException("Il file non esiste " + oFileName);
		//verifico la cancellabilita del file
		if (!oFile.canWrite()) throw new IllegalArgumentException("Il file non puo essere eliminato " + oFileName);
		boolean success = oFile.delete();
		if (!success) throw new IllegalArgumentException("Cancellazione del file fallita");

	}


	public boolean ExistsLayer(String sNameSpace, String sLayerName) throws IOException
	{
		try {
			synchronized(m_oGeoServerCriticalSection) {

				URL oUrlLayer = new URL(m_GsUrl + "rest/layers/"+ sNameSpace + ":" + sLayerName);

				String sResponse = geoserverGETAction(oUrlLayer, m_GsUser, m_GsPsw);

				if (sResponse!=null)
				{
					if (!sResponse.isEmpty())
					{
						if (sResponse.startsWith("<layer>"))
						{
							return true;
						}
					}
				}

				return false;
			}			
		}
		catch (Exception oEx) {
			oEx.printStackTrace();
		}

		return false;
	}


	private void geoserverAction(String sMethod, URL oUrlToSand, String sUser, String sPsw, String sInputAction) throws IOException
	{

		HttpURLConnection oGeoServerConn = (HttpURLConnection)oUrlToSand.openConnection();
		oGeoServerConn.setRequestMethod(sMethod);

		BASE64Encoder oEncoder = new BASE64Encoder();
		String encodedCredential = oEncoder.encode((sUser + ":" + sPsw).getBytes());
		oGeoServerConn.setRequestProperty("Authorization", "Basic " + encodedCredential);
		oGeoServerConn.setRequestProperty("Content-Type","text/xml");
		oGeoServerConn.setRequestProperty("Accept","text/xml");

		if(!sMethod.equals("DELETE")){

			oGeoServerConn.setDoOutput(true);
			OutputStream output = oGeoServerConn.getOutputStream();
			if(sInputAction != null) output.write(sInputAction.getBytes("UTF-8"));

		}

		oGeoServerConn.connect();
		byte abBuffer[] = new byte[8192];
		int read = 0;

		//risposta 
		InputStream responseBodyStream = oGeoServerConn.getInputStream();
		StringBuffer responseBody = new StringBuffer();
		while ((read = responseBodyStream.read(abBuffer)) != -1)
		{
			responseBody.append(new String(abBuffer, 0, read));
		}
		oGeoServerConn.disconnect();


	}


	private String geoserverGETAction(URL oUrlToSand, String sUser, String sPsw) throws IOException
	{

		HttpURLConnection oGeoServerConn = (HttpURLConnection)oUrlToSand.openConnection();
		oGeoServerConn.setRequestMethod("GET");

		BASE64Encoder oEncoder = new BASE64Encoder();
		String encodedCredential = oEncoder.encode((sUser + ":" + sPsw).getBytes());
		oGeoServerConn.setRequestProperty("Authorization", "Basic " + encodedCredential);
		//oGeoServerConn.setRequestProperty("Content-Type","text/xml");
		oGeoServerConn.setRequestProperty("Accept","text/xml");

		//oGeoServerConn.setDoOutput(true);
		//OutputStream output = oGeoServerConn.getOutputStream();

		oGeoServerConn.connect();
		byte abBuffer[] = new byte[8192];
		int read = 0;

		//risposta 
		InputStream responseBodyStream = oGeoServerConn.getInputStream();
		StringBuffer responseBody = new StringBuffer();
		while ((read = responseBodyStream.read(abBuffer)) != -1)
		{
			responseBody.append(new String(abBuffer, 0, read));
		}
		oGeoServerConn.disconnect();

		return responseBody.toString();
	}


	public void AggregateLayer(String sSourceFile, String sShpLocalFile, String sOutFile, String sIdField, String sPaletteField, float fUndef) {
		try {
			
			if (sSourceFile==null) throw new Exception("Input file not found"); 
			if (sShpLocalFile == null) throw new Exception("File locale per l'aggregazione non trovato nelle proprieta' del layer");
			File oShapeFile = new File(sShpLocalFile);
			if (!oShapeFile.isFile()) throw new Exception("Shape file non trovato: " + oShapeFile.getAbsolutePath());
			if (sIdField == null) throw new Exception("field identificativo non trovato nelle proprieta' del layer");
			if (sPaletteField == null) throw new Exception("field palette non trovato nelle proprieta' del layer");


			File oSouceFile = new File(sSourceFile);
			if (!oSouceFile.isFile()) throw new Exception("Input file not found");
			
			GeoTiffReader oFileReader = new GeoTiffReader(oSouceFile);

			GridCoverage2D oGridCoverage = (GridCoverage2D) oFileReader.read(null);

			String sGeoProp = "";
			
			float fX1 = (float) oGridCoverage.getEnvelope2D().x;
			float fY1 = (float) oGridCoverage.getEnvelope2D().y;
			float fX2 = (float) (oGridCoverage.getEnvelope2D().x + oGridCoverage.getEnvelope2D().width);
			float fY2 = (float) (oGridCoverage.getEnvelope2D().y + oGridCoverage.getEnvelope2D().height);
			
			
			int iDimX = oGridCoverage.getRenderedImage().getWidth();
			int iDimY = oGridCoverage.getRenderedImage().getHeight();
			
			sGeoProp += fX1 + ";" + fY1 + ";" + fX2 + ";" + fY2 + ";" + iDimX + ";" + iDimY;

			float afMap[] = new float [iDimX*iDimY];
			GeoImageProperty oGeoProp = new GeoImageProperty(sGeoProp);
			float fLonStep = (float) (oGridCoverage.getEnvelope2D().width/((double)iDimX));
			float fLatStep = (float) (oGridCoverage.getEnvelope2D().height/((double)iDimY));
			
			afMap = oGridCoverage.getRenderedImage().getData().getPixels(0, 0, iDimX, iDimY, afMap);

			File oShapeFileDst = new File(sOutFile);

			Transaction t = new DefaultTransaction(UUID.randomUUID().toString());		

			try {

				ShapefileDataStore oSrcData = new ShapefileDataStore(oShapeFile.toURI().toURL());
				FeatureStore oFeatureStore = (FeatureStore)oSrcData.getFeatureSource();

				//verifico che ci sia l'attributo PALETTE e sIdField
				Collection<PropertyDescriptor> oPropDescriptors = oFeatureStore.getSchema().getDescriptors();
				boolean bFoundPalette = false; 
				boolean bFoundIdField = false;
				for (PropertyDescriptor oPropDescr : oPropDescriptors) {
					if (oPropDescr.getName().getLocalPart().equals(sPaletteField)) bFoundPalette = true;
					if (oPropDescr.getName().getLocalPart().equals(sIdField)) bFoundIdField = true;
				}
				if (!bFoundPalette || !bFoundIdField) throw new Exception("Shape file per l'aggregazione non valido");			

				//calcolo la "mappa di assegnazione" delle feature sullo shape			
				VectorialMapBuilder oMapBuilder = new VectorialMapBuilder(oFeatureStore.getFeatures(), false);
				oMapBuilder.Build(oGeoProp, fLatStep, fLonStep, iDimX, iDimY);
				int aiMap[] = oMapBuilder.aiFeatureIdxMap;			
				Vector<String> vsFeatureMap = oMapBuilder.m_vsFeatureMap;
				//			Vector<String> vsFeatureMap = new Vector<String>();
				//			int aiMap[] = CalculateAlertAreaMapVectorial(oMyData.oImgProp, oMyData.fLatStep, oMyData.fLonStep, oMyData.iDimX, oMyData.iDimY, oFeatureStore, vsFeatureMap);			

				//calcolo la somma ed il numero di celle per ogni feature

				float afValues[] = new float[vsFeatureMap.size()];
				int aiCount[] = new int[vsFeatureMap.size()];			
				AggregateMap(afMap, fUndef, aiMap, afValues, aiCount);

				//applico il valore alle feature
				ShapefileDataStore oDstData = new ShapefileDataStore(oShapeFileDst.toURI().toURL());			
				oDstData.createSchema(oSrcData.getSchema());

				FeatureWriter oWriter = oDstData.getFeatureWriter(t);
				t.addAuthorization("lockId");

				for (FeatureIterator fit = oFeatureStore.getFeatures().features(); fit.hasNext();) {
					Feature oSrcFeature = fit.next();
					Feature oNewFeature = oWriter.next();				
					oNewFeature.setDefaultGeometryProperty(oSrcFeature.getDefaultGeometryProperty());
					for (PropertyDescriptor oPropDescr : oPropDescriptors) {
						oNewFeature.getProperty(oPropDescr.getName()).setValue(oSrcFeature.getProperty(oPropDescr.getName()).getValue());
					}
					String sIdFieldValue = oSrcFeature.getProperty(sIdField).getValue().toString();				

					int iIdx = vsFeatureMap.indexOf(oSrcFeature.getIdentifier().getID());

					float fValue = fUndef;
					if (iIdx>=0) {
						if (aiCount[iIdx] > 0) {
							fValue = afValues[iIdx] /(float)aiCount[iIdx];
						} 
						else {
							//cerco il punto griglia + vicino al baricentro della feature
							Object oObj = oSrcFeature.getDefaultGeometryProperty().getValue();
							if (oObj instanceof Geometry) {
								Geometry oGeom = (Geometry)oObj;
								Point oCentroid = oGeom.getCentroid();
								
								// P.Campanella 15/12/2016: dal confronto con il codice di DDS mettiamo il * al posto del /
								
								int iCol = (int)Math.round((oCentroid.getX() - oGeoProp.m_oSW.m_dX) / (double)fLonStep);
								int iRow = (int)Math.round((oCentroid.getY() - oGeoProp.m_oSW.m_dY) / (double)fLatStep);
								
								//int iCol = (int)Math.round((oCentroid.getX() - oGeoProp.m_oSW.m_dX) * (double)fLonStep);
								//int iRow = (int)Math.round((oCentroid.getY() - oGeoProp.m_oSW.m_dY) * (double)fLatStep);								

								if (iCol>=0 && iCol<iDimX && iRow>=0 && iRow<iDimY) {
									fValue = afMap[iRow*iDimX + iCol];
								}
							}
						}
					}
					
					if (Float.isNaN(fValue)) fValue = fUndef;

					oNewFeature.getProperty(sPaletteField).setValue(fValue);
					oWriter.write();
				}

				oWriter.close();
				t.commit();

			} catch (MalformedURLException e) {
				e.printStackTrace();
				throw new Exception("Errore durante la creazione dello shape file");
			} catch (IOException e) {
				e.printStackTrace();
				throw new Exception("Errore di I/O durante la creazione dello shape file");
			} catch (Throwable e) {
				e.printStackTrace();
			} finally {
				try {
					t.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	protected void AggregateMap(float[] afMap, float fUndef, int[] aiMap, float[] afValues, int[] aiCount) {
		for (int i = 0; i < aiMap.length; i++) {
			if (aiMap[i]>=0 && afMap[i] != fUndef && !Float.isNaN(afMap[i]) && afMap[i] >= 0) {
				afValues[aiMap[i]] += afMap[i];
				aiCount[aiMap[i]]++;
			}
		}
	}
}
