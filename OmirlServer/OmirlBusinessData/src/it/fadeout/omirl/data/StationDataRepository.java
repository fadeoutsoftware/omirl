package it.fadeout.omirl.data;

import it.fadeout.omirl.business.DataSeriePoint;
import it.fadeout.omirl.business.StationData;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

public class StationDataRepository extends Repository<StationData>{
	
	public List<DataSeriePoint> getDataSerie(String sStationCode, String sColumnName, Date oStartDate) {
		
		Session oSession = null;
		List<DataSeriePoint> aoLastValues = null;
		try {
			oSession = HibernateUtils.getSessionFactory().openSession();
			
			String sQuery = "select reference_date, "+ sColumnName +" as value from station_data where station_code = '"+sStationCode+"' and "+sColumnName+" is not null and reference_date >= ? order by reference_date";
			
			//oSession.beginTransaction();
			Query oQuery = oSession.createSQLQuery(sQuery).addEntity(DataSeriePoint.class);
			oQuery.setParameter(0, oStartDate);
			if (oQuery.list().size() > 0)
				aoLastValues =  (List<DataSeriePoint>) oQuery.list();

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
	
	public List<DataSeriePoint> getHourlyDataSerie(String sStationCode, String sColumnName, Date oStartDate) {
		
		Session oSession = null;
		List<DataSeriePoint> aoLastValues = null;
		try {
			oSession = HibernateUtils.getSessionFactory().openSession();
			
			String sQuery = "select reference_date, "+ sColumnName +" as value from station_data where station_code = '"+sStationCode+"' and "+sColumnName+" is not null and reference_date >= ? and date_part('minute', reference_date)=0 order by reference_date";
			
			//oSession.beginTransaction();
			Query oQuery = oSession.createSQLQuery(sQuery).addEntity(DataSeriePoint.class);
			oQuery.setParameter(0, oStartDate);
			if (oQuery.list().size() > 0)
				aoLastValues =  (List<DataSeriePoint>) oQuery.list();

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

	
	public List<DataSeriePoint> getDailyDataSerie(String sStationCode, String sColumnName, Date oStartDate) {
		
		Session oSession = null;
		List<DataSeriePoint> aoLastValues = null;
		try {
			oSession = HibernateUtils.getSessionFactory().openSession();
			
			String sQuery = "select reference_date, "+ sColumnName +" as value from station_data where station_code = '"+sStationCode+"' and "+sColumnName+" is not null and reference_date >= ? and date_part('minute', reference_date)=0  and date_part('hour', reference_date)=0 order by reference_date";
			
			//oSession.beginTransaction();
			Query oQuery = oSession.createSQLQuery(sQuery).addEntity(DataSeriePoint.class);
			oQuery.setParameter(0, oStartDate);
			if (oQuery.list().size() > 0)
				aoLastValues =  (List<DataSeriePoint>) oQuery.list();

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
