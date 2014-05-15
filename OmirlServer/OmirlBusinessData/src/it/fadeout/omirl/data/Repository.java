package it.fadeout.omirl.data;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.LockOptions;
import org.hibernate.Query;
import org.hibernate.Session;

public class Repository<T> {

	public boolean Save(T oEntity) {
		Session oSession = null;
		try {
			oSession = HibernateUtils.getSessionFactory().openSession();
			oSession.beginTransaction();
			oSession.saveOrUpdate(oEntity);
			oSession.getTransaction().commit();
			return true;
		}
		catch(Throwable oEx) {
			System.err.println(oEx.toString());
			oEx.printStackTrace();
			
			try {
				oSession.getTransaction().rollback();
			}
			catch(Throwable oEx2) {
				System.err.println(oEx2.toString());
				oEx2.printStackTrace();					
			}
			
			return false;
		}
		finally {
			if (oSession!=null)  oSession.close();
		}
		
	}
	
	public boolean Update(T oEntity) {
		Session oSession = null;
		try {		
			oSession = HibernateUtils.getSessionFactory().openSession();
			oSession.beginTransaction();
			oSession.saveOrUpdate(oEntity);
			oSession.getTransaction().commit();
			return true;
		}
		catch(Throwable oEx) {
			System.err.println(oEx.toString());
			oEx.printStackTrace();
			
			
			try {
				oSession.getTransaction().rollback();
			}
			catch(Throwable oEx2) {
				System.err.println(oEx2.toString());
				oEx2.printStackTrace();					
			}			
			
			return false;
		}		
		finally {
			if (oSession!=null) {
				oSession.flush();
				oSession.clear();
				oSession.close();
			}
		}

		
	}
	
	@SuppressWarnings("unchecked")
	public List<T> SelectAll(Class<T> oClass) {
		Session oSession = null;
		
		List<T> aoList = new ArrayList<T>();
		
		try {
			oSession = HibernateUtils.getSessionFactory().openSession();
			oSession.beginTransaction();
			Query oQuery = oSession.createQuery("from " + oClass.getSimpleName());
			aoList = oQuery.list();
			oSession.getTransaction().commit();			
		}
		catch(Throwable oEx) {
			System.err.println(oEx.toString());
			oEx.printStackTrace();
			
			
			try {
				oSession.getTransaction().rollback();
			}
			catch(Throwable oEx2) {
				System.err.println(oEx2.toString());
				oEx2.printStackTrace();					
			}			
		}
		finally {
			if (oSession!=null) {
				oSession.flush();
				oSession.clear();
				oSession.close();
			}

		}

		
		
		return aoList;
	}
	
	@SuppressWarnings("unchecked")
	public T Select(int iId, Class<T> oClass) {
		
		Session oSession = null;
		
		T oEntity = null;
		
		try {				
			oSession = HibernateUtils.getSessionFactory().openSession();
			oSession.beginTransaction();
			oEntity = (T) oSession.load(oClass, new Integer(iId),LockOptions.UPGRADE);
			oSession.getTransaction().commit();
		}
		catch(Throwable oEx) {
			System.err.println(oEx.toString());
			oEx.printStackTrace();
			
			
			try {
				oSession.getTransaction().rollback();
			}
			catch(Throwable oEx2) {
				System.err.println(oEx2.toString());
				oEx2.printStackTrace();					
			}			
		}		
		finally {
			if (oSession!=null) {
				oSession.flush();
				oSession.clear();
				oSession.close();
			}

		}
		
		return oEntity;
	}
	
	public boolean Delete(T oEntity) {
		Session oSession = null;
		try {				
			oSession = HibernateUtils.getSessionFactory().openSession();
			oSession.beginTransaction();
			oSession.delete(oEntity);
			oSession.getTransaction().commit();
			return true;
		}
		catch(Throwable oEx) {
			System.err.println(oEx.toString());
			oEx.printStackTrace();
			
			
			try {
				oSession.getTransaction().rollback();
			}
			catch(Throwable oEx2) {
				System.err.println(oEx2.toString());
				oEx2.printStackTrace();					
			}			
		}	
		finally {
			if (oSession!=null) {
				oSession.flush();
				oSession.clear();
				oSession.close();
			}
		}

		return false;
	}
}
