package it.fadeout.omirl.data;

import it.fadeout.omirl.business.SectionAnag;
import it.fadeout.omirl.business.StationAnag;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

public class StationAnagRepository extends Repository<StationAnag> {
	
	
	public ArrayList<StationAnag> getListByLatLon(double dLat, double dLon, int radiusInKm)
	{
		Session oSession = null;
		ArrayList<StationAnag> aoLastValues = null;
		
		// Longitude (X axis):
		// +/- 1.0 = +/- 79.529Km
		// 1km = 0.0125740296
		double ONE_KM_AS_LON = 0.0125740296;
		
		// Latitude (Y axis)
		// +/- 1.0 = +/- 114.119Km
		// 1Km = 0.00000876278 (corrected: 0.0089627827)
		double ONE_KM_AS_LAT = 0.0089627827;
		
		// Calculate a square as search bounding box
		double[] latRange = {dLat - (ONE_KM_AS_LAT * radiusInKm),  dLat + (ONE_KM_AS_LAT * radiusInKm)};
		double[] lonRange = {dLon - (ONE_KM_AS_LON * radiusInKm),  dLon + (ONE_KM_AS_LON * radiusInKm)};
		
		
		
		// Execute the query
		try {
			oSession = HibernateUtils.getSessionFactory().openSession();
			Query oQuery = oSession.createSQLQuery(
					"SELECT * FROM station_anag WHERE lat >= " + latRange[0]  +" AND lat <= " + latRange[1] 
							+ " AND lon >= "+ lonRange[0] +" AND lon <= "+ lonRange[1])
				.addEntity(StationAnag.class);
			if (oQuery.list().size() > 0)
				aoLastValues =  new ArrayList<StationAnag>(oQuery.list());

		}
		catch(Throwable oEx) {
			System.err.println(oEx.toString());
			oEx.printStackTrace();
		}
		finally {
			if (oSession!=null) {
				oSession.flush();
				oSession.clear();
				oSession.close();
			}

		}
		return aoLastValues;	
	}
	
	
	public StationAnag selectByStationCode(String sCode) {
		
		Session oSession = null;
		StationAnag oStation = null;
		try {
			oSession = HibernateUtils.getSessionFactory().openSession();
			Query oQuery = oSession.createQuery("from StationAnag where station_code = '" + sCode+ "'");
			if (oQuery.list().size() > 0)
				oStation =  (StationAnag) oQuery.list().get(0);

		}
		catch(Throwable oEx) {
			System.err.println(oEx.toString());
			oEx.printStackTrace();
		}
		finally {
			if (oSession!=null) {
				oSession.flush();
				oSession.clear();
				oSession.close();
			}

		}
		return oStation;		
	}
	
	public List<StationAnag> getListByType(String sColumnToCheck) {
		Session oSession = null;
		List<StationAnag> aoLastValues = null;
		try {
			oSession = HibernateUtils.getSessionFactory().openSession();
			Query oQuery = oSession.createSQLQuery("select * from station_anag where " + sColumnToCheck + " is not null order by name").addEntity(StationAnag.class);
			if (oQuery.list().size() > 0)
				aoLastValues =  (List<StationAnag>) oQuery.list();

		}
		catch(Throwable oEx) {
			System.err.println(oEx.toString());
			oEx.printStackTrace();
		}
		finally {
			if (oSession!=null) {
				oSession.flush();
				oSession.clear();
				oSession.close();
			}

		}
		return aoLastValues;	
	}
	
	
	
	public List<StationAnag> getCostalWindStations() {
		
		Session oSession = null;
		List<StationAnag> aoLastValues = null;
		try {
			oSession = HibernateUtils.getSessionFactory().openSession();
			Query oQuery = oSession.createSQLQuery("select * from station_anag where near_sea=1 order by name").addEntity(StationAnag.class);
			if (oQuery.list().size() > 0)
				aoLastValues =  (List<StationAnag>) oQuery.list();

		}
		catch(Throwable oEx) {
			System.err.println(oEx.toString());
			oEx.printStackTrace();
		}
		finally {
			if (oSession!=null) {
				oSession.flush();
				oSession.clear();
				oSession.close();
			}

		}
		return aoLastValues;		
	}

	
	public List<StationAnag> getInternalWindStations() {
		
		Session oSession = null;
		List<StationAnag> aoLastValues = null;
		try {
			oSession = HibernateUtils.getSessionFactory().openSession();
			Query oQuery = oSession.createSQLQuery("select * from station_anag where near_sea=0 order by name").addEntity(StationAnag.class);
			if (oQuery.list().size() > 0)
				aoLastValues =  (List<StationAnag>) oQuery.list();

		}
		catch(Throwable oEx) {
			System.err.println(oEx.toString());
			oEx.printStackTrace();
		}
		finally {
			if (oSession!=null) {
				oSession.flush();
				oSession.clear();
				oSession.close();
			}

		}
		return aoLastValues;			
	}
}
