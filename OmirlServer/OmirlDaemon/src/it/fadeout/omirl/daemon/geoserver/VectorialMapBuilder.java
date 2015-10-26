package it.fadeout.omirl.daemon.geoserver;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.Vector;

import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;



import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

public class VectorialMapBuilder {
	
	//private FeatureStore m_oFeatureStore;
	private FeatureCollection m_oFeatureCollection;
	
	public int[] aiFeatureIdxMap;
	public Vector<String> m_vsFeatureMap = new Vector<String>();
	private boolean m_bRevertY = true;
	
	
//	public VectorialMapBuilder(FeatureStore oFeatureStore) {
//		m_oFeatureStore = oFeatureStore;
//	}
//
//	public VectorialMapBuilder(FeatureStore oFeatureStore, boolean bRevertY) {
//		m_oFeatureStore = oFeatureStore;
//		m_bRevertY = bRevertY;
//	}
	
	public VectorialMapBuilder(FeatureCollection oFeatureCollection) {
		m_oFeatureCollection = oFeatureCollection;
	}

	public VectorialMapBuilder(FeatureCollection oFeatureCollection, boolean bRevertY) {
		m_oFeatureCollection = oFeatureCollection;
		m_bRevertY = bRevertY;
	}
	
	public void Build(GeoImageProperty oGeoWin,float fLatStep, float fLonStep, final int iXDim, final int iYDim) throws Exception {
		Build(null, oGeoWin, fLatStep, fLonStep, iXDim, iYDim);
	}
	
	public void Build(String IDField, GeoImageProperty oGeoWin,float fLatStep, float fLonStep, final int iXDim, final int iYDim) throws Exception {
		
		int iDemSizeX = iXDim;
		int iDemSizeY = iYDim;

		aiFeatureIdxMap = new int[iDemSizeX*iDemSizeY];
		BufferedImage image = new BufferedImage(iDemSizeX,iDemSizeY,BufferedImage.TYPE_INT_RGB );

		Graphics2D g2 = image.createGraphics();
		
		String sFeatureId;
		for (FeatureIterator fit = m_oFeatureCollection.features(); fit.hasNext();) {
			
			Feature oFeature = (SimpleFeature) fit.next();
			
			sFeatureId = ((IDField == null)? oFeature.getIdentifier().getID() : oFeature.getProperty(IDField).getValue().toString());
			
			int iPos = m_vsFeatureMap.indexOf(sFeatureId);
			if (iPos < 0) {
				m_vsFeatureMap.add(sFeatureId);
				iPos = m_vsFeatureMap.size()-1;
			}				

			Object o = oFeature.getDefaultGeometryProperty().getValue();				
			if (o instanceof MultiPolygon) {

				MultiPolygon mpoli = (MultiPolygon) o;

				int iNumGeo = mpoli.getNumGeometries();
				
				g2.setPaint(new Color((iPos+1)/65536,(iPos+1)/256,(iPos+1)%256));
				
				for (int iGeo = 0; iGeo < iNumGeo; iGeo++) {
					Geometry oGeo = mpoli.getGeometryN(iGeo);
					if (oGeo instanceof Polygon) {
						Polygon oPolygon = (Polygon) oGeo;
						
						LineString oLS = oPolygon.getExteriorRing();
						
						Coordinate[] points = oLS.getCoordinates(); //oPolygon.getCoordinates();
						GeneralPath path = new GeneralPath(GeneralPath.WIND_EVEN_ODD, points.length);
						path.moveTo((points[0].x-oGeoWin.m_oSW.m_dX)/fLonStep, (iDemSizeY-1)-(points[0].y-oGeoWin.m_oSW.m_dY)/fLatStep);
						for (int i = 1; i < points.length; i++) {
							path.lineTo((points[i].x-oGeoWin.m_oSW.m_dX)/fLonStep,(iDemSizeY-1)-(points[i].y-oGeoWin.m_oSW.m_dY)/fLatStep);
						}
						g2.fill(path);							
					}
				}

			}

		}
		
//		try {
//			ImageIO.write(image, "png", new File("C:\\Dev\\mapImage.png"));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

		int k = 0;

		for (int y = (m_bRevertY? (iDemSizeY - 1) : 0); (m_bRevertY? y >= 0 : y < iDemSizeY); y = (m_bRevertY? (y-1) : (y+1))) {
			
			for (int x = 0; x < iDemSizeX; x++, k++) {
				int iColo = image.getRGB(x, y);
				int iAlertZone = -1 + (iColo & 0xff) + ((iColo & 0xff00) >> 8) * 256 + ((iColo & 0xff0000) >> 16) * 65536;
				if (iAlertZone < 0) {
					aiFeatureIdxMap[k] = -9998;
				} else {
					aiFeatureIdxMap[k] = iAlertZone;
				}
			}
			
		}
		
	}
	
}
