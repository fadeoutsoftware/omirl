package it.fadeout.omirl.data;

import it.fadeout.omirl.business.OpenSession;

import org.hibernate.Query;
import org.hibernate.Session;

public class OpenSessionRepository extends Repository<OpenSession> {
	
	public OpenSession selectBySessionId(String sSessionId) {
		Session oSession = null;
		OpenSession oOpenSession = null;
		try {
			oSession = HibernateUtils.getSessionFactory().openSession();
			//oSession.beginTransaction();
			Query oQuery = oSession.createQuery("from OpenSession where sessionId = '" + sSessionId+ "'");
			if (oQuery.list().size() > 0)
				oOpenSession =  (OpenSession) oQuery.list().get(0);

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
		return oOpenSession;
	}

}
