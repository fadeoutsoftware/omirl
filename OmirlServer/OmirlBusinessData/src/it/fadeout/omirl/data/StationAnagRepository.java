package it.fadeout.omirl.data;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import it.fadeout.omirl.business.SensorLastData;
import it.fadeout.omirl.business.StationAnag;

public class StationAnagRepository extends Repository<StationAnag> {
	
	public StationAnag selectByStationCode(String sCode) {
		
		Session oSession = null;
		StationAnag oStation = null;
		try {
			oSession = HibernateUtils.getSessionFactory().openSession();
			//oSession.beginTransaction();
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
			//oSession.beginTransaction();
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
}
