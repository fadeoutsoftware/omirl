package it.fadeout.omirl.data;

import java.util.ArrayList;
import java.util.List;

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
	
	public List<SectionAnag> selectByModel(String sFlagColumn)
	{
		Session oSession = null;
		List<SectionAnag> aoSections = new ArrayList<>();
		
		try {
			oSession = HibernateUtils.getSessionFactory().openSession();
			Query oQuery = oSession.createQuery("from SectionAnag where " + sFlagColumn +" = 1");
			aoSections =  (List<SectionAnag>) oQuery.list();
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
		return aoSections;		
	}

}
