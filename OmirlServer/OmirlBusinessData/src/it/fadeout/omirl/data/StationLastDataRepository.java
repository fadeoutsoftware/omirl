package it.fadeout.omirl.data;

import it.fadeout.omirl.business.StationLastData;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

public class StationLastDataRepository extends Repository<StationLastData> {
	
	public List<StationLastData> selectByStationType(String sValueColumn) {
		
		Session oSession = null;
		List<StationLastData> aoLastValues = null;
		try {
			oSession = HibernateUtils.getSessionFactory().openSession();
			//oSession.beginTransaction();
			Query oQuery = oSession.createQuery("from StationLastData where " + sValueColumn + " is not null");
			if (oQuery.list().size() > 0)
				aoLastValues =  (List<StationLastData>) oQuery.list();

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
