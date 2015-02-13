package it.fadeout.omirl.data;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import it.fadeout.omirl.business.Sfloc;

public class SflocRepository extends Repository<Sfloc>{

@SuppressWarnings("unchecked")
public List<Sfloc> selectLastHour(int iHour) {
		
		Session oSession = null;
		List<Sfloc> aoLastValues = null;
		try {
			oSession = HibernateUtils.getSessionFactory().openSession();
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date()); // sets calendar time/date
		    cal.add(Calendar.HOUR_OF_DAY, -iHour); // adds six hour
			Criteria oCriteria = oSession.createCriteria(Sfloc.class);
			oCriteria.add(Restrictions.ge("dtrfsec", cal.getTime()));
			if (oCriteria.list().size() > 0)
				aoLastValues =  (List<Sfloc>) oCriteria.list();

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
