package it.fadeout.omirl.data;

import java.util.HashMap;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtils {

	private static SessionFactory sessionFactory;
	
	private static SessionFactory sessionFactorySQLLite;
	
	private static HashMap<String, SessionFactory> m_aoSessionFactorySQLLite = new HashMap<String, SessionFactory>(); 

	private static SessionFactory buildSessionFactory() {
		try {
			// Create the SessionFactory from hibernate.cfg.xml
			return new  Configuration().configure("/hibernate-postgres.cfg.xml").buildSessionFactory();
		} catch (Throwable ex) {
			// Make sure you log the exception, as it might be swallowed
			System.err.println("Initial SessionFactory creation failed." + ex);
			throw new ExceptionInInitializerError(ex);
		}
	}

	private static SessionFactory buildSessionFactorySQLLite(String sConnection) {
		try {
			// Create the SessionFactory from hibernate.cfg.xml
			Configuration oConfiguration = new Configuration();
			oConfiguration.setProperty("hibernate.connection.url", "jdbc:sqlite:/" + sConnection);
			oConfiguration = oConfiguration.configure("/hibernate-sqlite.cfg.xml");
			return oConfiguration.buildSessionFactory();
		} catch (Throwable ex) {
			// Make sure you log the exception, as it might be swallowed
			System.err.println("Initial SessionFactory creation failed." + ex);
			throw new ExceptionInInitializerError(ex);
		}
	}

	public static SessionFactory getSessionFactory() {

		if (sessionFactory == null)
			sessionFactory = buildSessionFactory();
		return sessionFactory;
	}

	public static SessionFactory getSessionFactorySQlLite(String sConnection) {
		
		if (!m_aoSessionFactorySQLLite.containsKey(sConnection))
		{
			m_aoSessionFactorySQLLite.put(sConnection, buildSessionFactorySQLLite(sConnection));
			return m_aoSessionFactorySQLLite.get(sConnection);
		}
		
		 return m_aoSessionFactorySQLLite.get(sConnection);
		//return sessionFactorySQLLite;
	}

	public static void shutdown() {
		// Close caches and connection pools
		getSessionFactory().close();
	}
	
	public static void shutdownSQLite() {
		// Close caches and connection pools
		for(String sConnectionKey : m_aoSessionFactorySQLLite.keySet())
		{
			 m_aoSessionFactorySQLLite.get(sConnectionKey).close();
		}
	}


}
