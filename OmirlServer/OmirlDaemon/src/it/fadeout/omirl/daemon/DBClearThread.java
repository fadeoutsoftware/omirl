package it.fadeout.omirl.daemon;

import java.util.Calendar;
import java.util.Date;

import it.fadeout.omirl.data.StationDataRepository;

public class DBClearThread  extends Thread{
	
	int m_iSavedDays = 1;
	
	public DBClearThread(int iDays) {
		super();
		m_iSavedDays = iDays;
		
	}
	
	public void run() {

		try {
			System.out.println("In DeleteOldData " + new Date());
			// Create a Date Time with the created date
			Calendar cal = Calendar.getInstance();    
			cal.set(Calendar.HOUR_OF_DAY, 12);
			cal.set(Calendar.MINUTE, 0);
			cal.add(Calendar.DAY_OF_MONTH, -1*m_iSavedDays);
			
			StationDataRepository oRepository = new StationDataRepository();
			 
			oRepository.DeleteOldData(m_iSavedDays, cal.getTime());
			System.out.println("Out DeleteOldData " + new Date());
		}
		catch(Exception oEx) {
			System.out.println("Exception in DeleteOldData " + new Date());
			System.out.println("OmirlDaemon - Clear Thread Exception");
			oEx.printStackTrace();
		}
	}
	
}
