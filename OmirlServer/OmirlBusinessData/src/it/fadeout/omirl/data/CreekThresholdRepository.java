package it.fadeout.omirl.data;

import it.fadeout.omirl.business.CreekThreshold;

import org.hibernate.Query;
import org.hibernate.Session;

public class CreekThresholdRepository extends Repository<CreekThreshold> {
	
	public CreekThreshold selectByStationCode(String sCode) {
		
		Session oSession = null;
		CreekThreshold oThreshold = null;
		try {
			oSession = HibernateUtils.getSessionFactory().openSession();
			Query oQuery = oSession.createQuery("from CreekThreshold where code = '" + sCode+ "'");
			if (oQuery.list().size() > 0)
				oThreshold =  (CreekThreshold) oQuery.list().get(0);

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
		return oThreshold;	
	}
}

