package it.fadeout.omirl.data;

import java.util.HashMap;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

public class HibernateUtils {

	private static SessionFactory sessionFactory;

	private static SessionFactory buildSessionFactory() {
		try {
			// Create the SessionFactory from hibernate.cfg.xml			
			Configuration oConfiguration = new Configuration().configure();
			StandardServiceRegistryBuilder oBuilder = new StandardServiceRegistryBuilder().applySettings(oConfiguration.getProperties());
			SessionFactory oFactory = oConfiguration.buildSessionFactory(oBuilder.build());
			return oFactory;
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

	public static void shutdown() {
		// Close caches and connection pools
		getSessionFactory().close();
	}
	
}
