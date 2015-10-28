package it.fadeout.omirl.data;

import java.util.ArrayList;

import it.fadeout.omirl.business.OmirlUser;

import org.hibernate.Query;
import org.hibernate.Session;

public class OmirlUserRepository  extends Repository<OmirlUser> {
	
	public OmirlUser selectByUserId(String sUserId) {
		
		Session oSession = null;
		OmirlUser oUser = null;
		try {
			oSession = HibernateUtils.getSessionFactory().openSession();
			Query oQuery = oSession.createQuery("from OmirlUser where userid = '" + sUserId+ "'");
			if (oQuery.list().size() > 0)
				oUser =  (OmirlUser) oQuery.list().get(0);

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
		return oUser;		
	}
	
	public boolean login(String sUserId, String sPassword) {
		
		Session oSession = null;
		OmirlUser oUser = null;
		boolean bLogged=false;
		
		try {
			oSession = HibernateUtils.getSessionFactory().openSession();
			Query oQuery = oSession.createQuery("from OmirlUser where userid = '" + sUserId+ "' and password = '"+ sPassword + "'");
			if (oQuery.list().size() > 0)
			{
				oUser =  (OmirlUser) oQuery.list().get(0);
				if (oUser != null) bLogged = true;
			}

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
		return bLogged;		
	}
	
	public ArrayList<OmirlUser> selectUsersNotAdmin() {
		
		Session oSession = null;
		ArrayList<OmirlUser> oUsers = null;
		try {
			oSession = HibernateUtils.getSessionFactory().openSession();
			Query oQuery = oSession.createQuery("from OmirlUser where role = 2 order by name");
			if (oQuery.list().size() > 0)
				oUsers =  (ArrayList<OmirlUser>) oQuery.list();

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
		return oUsers;		
	}
	
}
