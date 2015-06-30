package it.fadeout.omirl.data;

import it.fadeout.omirl.business.DataSeriePoint;
import it.fadeout.omirl.business.MaxTableRow;
import it.fadeout.omirl.business.StationData;
import it.fadeout.omirl.business.SummaryInfoEntity;
import it.fadeout.omirl.business.WindDataSeriePoint;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
	
	public List<WindDataSeriePoint> getWindDataSerie(String sStationCode, Date oStartDate) {
		
		Session oSession = null;
		List<WindDataSeriePoint> aoLastValues = null;
		try {
			oSession = HibernateUtils.getSessionFactory().openSession();
			
			String sQuery = "select reference_date, mean_wind_speed, prevailing_wind_dir from station_data where station_code = '"+sStationCode+"' and mean_wind_speed is not null and prevailing_wind_dir is not null and reference_date >= ? order by reference_date desc";
			
			Query oQuery = oSession.createSQLQuery(sQuery).addEntity(WindDataSeriePoint.class);
			oQuery.setParameter(0, oStartDate);
			if (oQuery.list().size() > 0)
				aoLastValues =  (List<WindDataSeriePoint>) oQuery.list();

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
	
	public SummaryInfoEntity getDistrictMaxTemperatureSummaryInfo(String sDistrict, Date oDate)
	{
		Session oSession = null;
		SummaryInfoEntity oSummaryInfoEntity = null;
		
		try {
			
			Calendar calStart = new GregorianCalendar();
			calStart.setTime(oDate);
			calStart.set(Calendar.HOUR_OF_DAY, 0);
			calStart.set(Calendar.MINUTE, 0);
			calStart.set(Calendar.SECOND, 0);
			calStart.set(Calendar.MILLISECOND, 0);
			oDate = calStart.getTime();
			
			oSession = HibernateUtils.getSessionFactory().openSession();
			
			String sQuery = "select station_data.max_air_temp as value, station_data.reference_date, station_anag.name as station_name from station_data inner join station_anag on station_data.station_code = station_anag.station_code where district = '"+sDistrict+"' and station_data.max_air_temp is not null and reference_date >= ? order by value desc limit 1";
			
			Query oQuery = oSession.createSQLQuery(sQuery).addEntity(SummaryInfoEntity.class);
			oQuery.setParameter(0, oDate);
			oSummaryInfoEntity =  (SummaryInfoEntity) oQuery.uniqueResult();
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
		return oSummaryInfoEntity;		
		
	}
	
	
	public SummaryInfoEntity getDistrictMinTemperatureSummaryInfo(String sDistrict, Date oDate)
	{
		Session oSession = null;
		SummaryInfoEntity oSummaryInfoEntity = null;
		
		try {
			
			Calendar calStart = new GregorianCalendar();
			calStart.setTime(oDate);
			calStart.set(Calendar.HOUR_OF_DAY, 0);
			calStart.set(Calendar.MINUTE, 0);
			calStart.set(Calendar.SECOND, 0);
			calStart.set(Calendar.MILLISECOND, 0);
			oDate = calStart.getTime();
			
			oSession = HibernateUtils.getSessionFactory().openSession();
			
			String sQuery = "select station_data.min_air_temp as value, station_data.reference_date, station_anag.name as station_name from station_data inner join station_anag on station_data.station_code = station_anag.station_code where district = '"+sDistrict+"' and station_data.min_air_temp is not null and reference_date >= ? order by value limit 1";
			
			Query oQuery = oSession.createSQLQuery(sQuery).addEntity(SummaryInfoEntity.class);
			oQuery.setParameter(0, oDate);
			oSummaryInfoEntity =  (SummaryInfoEntity) oQuery.uniqueResult();
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
		return oSummaryInfoEntity;		
		
	}
	
	public SummaryInfoEntity getWindMaxSummaryInfo(String sCodes, Date oDate)
	{
		Session oSession = null;
		SummaryInfoEntity oSummaryInfoEntity = null;
		
		try {
			
			Calendar calStart = new GregorianCalendar();
			calStart.setTime(oDate);
			calStart.set(Calendar.HOUR_OF_DAY, 0);
			calStart.set(Calendar.MINUTE, 0);
			calStart.set(Calendar.SECOND, 0);
			calStart.set(Calendar.MILLISECOND, 0);
			oDate = calStart.getTime();
			
			oSession = HibernateUtils.getSessionFactory().openSession();
			
			String sQuery = "select station_data.mean_wind_speed as value, station_data.reference_date, station_anag.name as station_name from station_data inner join station_anag on station_data.station_code = station_anag.station_code where station_data.station_code in (" + sCodes + ") and station_data.mean_wind_speed is not null and reference_date >= ? order by value desc limit 1";
			
			Query oQuery = oSession.createSQLQuery(sQuery).addEntity(SummaryInfoEntity.class);
			oQuery.setParameter(0, oDate);
			oSummaryInfoEntity =  (SummaryInfoEntity) oQuery.uniqueResult();
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
		return oSummaryInfoEntity;		
		
	}
	
	
	public SummaryInfoEntity getWindGustSummaryInfo(String sCodes, Date oDate)
	{
		Session oSession = null;
		SummaryInfoEntity oSummaryInfoEntity = null;
		
		try {
			
			Calendar calStart = new GregorianCalendar();
			calStart.setTime(oDate);
			calStart.set(Calendar.HOUR_OF_DAY, 0);
			calStart.set(Calendar.MINUTE, 0);
			calStart.set(Calendar.SECOND, 0);
			calStart.set(Calendar.MILLISECOND, 0);
			oDate = calStart.getTime();
			
			oSession = HibernateUtils.getSessionFactory().openSession();
			
			String sQuery = "select station_data.wind_gust as value, station_data.reference_date, station_anag.name as station_name from station_data inner join station_anag on station_data.station_code = station_anag.station_code where station_data.station_code in (" + sCodes + ") and station_data.wind_gust is not null and reference_date >= ? order by value desc limit 1";
			
			Query oQuery = oSession.createSQLQuery(sQuery).addEntity(SummaryInfoEntity.class);
			oQuery.setParameter(0, oDate);
			oSummaryInfoEntity =  (SummaryInfoEntity) oQuery.uniqueResult();
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
		return oSummaryInfoEntity;		
		
	}
	
	
	
	
	public SummaryInfoEntity getAlertZoneMaxTemperatureSummaryInfo(String sAlertZone, Date oDate)
	{
		Session oSession = null;
		SummaryInfoEntity oSummaryInfoEntity = null;
		
		try {
			
			Calendar calStart = new GregorianCalendar();
			calStart.setTime(oDate);
			calStart.set(Calendar.HOUR_OF_DAY, 0);
			calStart.set(Calendar.MINUTE, 0);
			calStart.set(Calendar.SECOND, 0);
			calStart.set(Calendar.MILLISECOND, 0);
			oDate = calStart.getTime();
			
			oSession = HibernateUtils.getSessionFactory().openSession();
			
			String sQuery = "select station_data.max_air_temp as value, station_data.reference_date, station_anag.name as station_name from station_data inner join station_anag on station_data.station_code = station_anag.station_code where station_anag.warn_area LIKE '%"+sAlertZone+"%' and station_data.max_air_temp is not null and reference_date >= ? order by value desc limit 1";
			
			Query oQuery = oSession.createSQLQuery(sQuery).addEntity(SummaryInfoEntity.class);
			oQuery.setParameter(0, oDate);
			oSummaryInfoEntity =  (SummaryInfoEntity) oQuery.uniqueResult();
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
		return oSummaryInfoEntity;		
		
	}
	
	
	public SummaryInfoEntity getAlertZoneMinTemperatureSummaryInfo(String sAlertZone, Date oDate)
	{
		Session oSession = null;
		SummaryInfoEntity oSummaryInfoEntity = null;
		
		try {
			
			Calendar calStart = new GregorianCalendar();
			calStart.setTime(oDate);
			calStart.set(Calendar.HOUR_OF_DAY, 0);
			calStart.set(Calendar.MINUTE, 0);
			calStart.set(Calendar.SECOND, 0);
			calStart.set(Calendar.MILLISECOND, 0);
			oDate = calStart.getTime();
			
			oSession = HibernateUtils.getSessionFactory().openSession();
			
			String sQuery = "select station_data.min_air_temp as value, station_data.reference_date, station_anag.name as station_name from station_data inner join station_anag on station_data.station_code = station_anag.station_code where station_anag.warn_area LIKE '%"+sAlertZone+"%' and station_data.min_air_temp is not null and reference_date >= ? order by value limit 1";
			
			Query oQuery = oSession.createSQLQuery(sQuery).addEntity(SummaryInfoEntity.class);
			oQuery.setParameter(0, oDate);
			oSummaryInfoEntity =  (SummaryInfoEntity) oQuery.uniqueResult();
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
		return oSummaryInfoEntity;		
		
	}
	
	public List<MaxTableRow> GetDistrictMaxTableCell(String sColumn, String sFilter, Date oDate)
	{
		Session oSession = null;
		List<MaxTableRow> aoMaxList = new ArrayList<>();
		
		try {
			
			Calendar calStart = new GregorianCalendar();
			calStart.setTime(oDate);
			calStart.set(Calendar.HOUR_OF_DAY, 0);
			calStart.set(Calendar.MINUTE, 0);
			calStart.set(Calendar.SECOND, 0);
			calStart.set(Calendar.MILLISECOND, 0);
			oDate = calStart.getTime();
			
			oSession = HibernateUtils.getSessionFactory().openSession();
			
			String sQuery = "select station_data." + sColumn +" as value, station_data.reference_date, station_anag.name as station_name, station_anag.station_code as station_code from station_data inner join station_anag on station_data.station_code = station_anag.station_code where district = '"+sFilter+"' and station_data." + sColumn + " is not null and reference_date >= ? order by value desc";
			
			Query oQuery = oSession.createSQLQuery(sQuery).addEntity(MaxTableRow.class);
			oQuery.setParameter(0, oDate);
			aoMaxList =  (List<MaxTableRow>) oQuery.list();
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
		return aoMaxList;		
		
	}
	
	public List<MaxTableRow> GetAlertZonesMaxTableCell(String sColumn, String sFilter, Date oDate)
	{
		Session oSession = null;
		List<MaxTableRow> aoMaxList = new ArrayList<>();
		
		try {
			
			Calendar calStart = new GregorianCalendar();
			calStart.setTime(oDate);
			calStart.set(Calendar.HOUR_OF_DAY, 0);
			calStart.set(Calendar.MINUTE, 0);
			calStart.set(Calendar.SECOND, 0);
			calStart.set(Calendar.MILLISECOND, 0);
			oDate = calStart.getTime();
			
			oSession = HibernateUtils.getSessionFactory().openSession();
			
			String sQuery = "select station_data." + sColumn +" as value, station_data.reference_date, station_anag.name as station_name, station_anag.station_code as station_code from station_data inner join station_anag on station_data.station_code = station_anag.station_code where warn_area like '%"+sFilter+"%' and station_data." + sColumn + " is not null and reference_date >= ? order by value desc";
			
			Query oQuery = oSession.createSQLQuery(sQuery).addEntity(MaxTableRow.class);
			oQuery.setParameter(0, oDate);
			aoMaxList =  (List<MaxTableRow>) oQuery.list();
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
		return aoMaxList;		
		
	}
	
	
}
