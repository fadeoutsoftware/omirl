package it.fadeout.omirl.data;

import it.fadeout.omirl.business.SectionAnag;

import org.hibernate.Query;
import org.hibernate.Session;

public class SectionAnagRepository  extends Repository<SectionAnag> {
	
	public SectionAnag selectBySectionCode(String sCode) {
		
		Session oSession = null;
		SectionAnag oSection = null;
		try {
			oSession = HibernateUtils.getSessionFactory().openSession();
			Query oQuery = oSession.createQuery("from SectionAnag where code = '" + sCode+ "'");
			if (oQuery.list().size() > 0)
				oSection =  (SectionAnag) oQuery.list().get(0);

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
		return oSection;		
	}

}
