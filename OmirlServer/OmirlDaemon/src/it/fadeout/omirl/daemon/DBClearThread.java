package it.fadeout.omirl.daemon;

import java.util.Calendar;
import it.fadeout.omirl.data.StationDataRepository;

public class DBClearThread  extends Thread{
	
	int m_iSavedDays = 1;
	
	public DBClearThread(int iDays) {
		super();
		m_iSavedDays = iDays;
		
	}
	
	public void run() {

		try {
			
			// Create a Date Time with the created date
			Calendar cal = Calendar.getInstance();    
			cal.set(Calendar.HOUR_OF_DAY, 12);
			cal.set(Calendar.MINUTE, 0);
			cal.add(Calendar.DAY_OF_MONTH, -1*m_iSavedDays);
			
			StationDataRepository oRepository = new StationDataRepository();
			oRepository.DeleteOldData(m_iSavedDays, cal.getTime());
			
		}
		catch(Exception oEx) {
			System.out.println("OmirlDaemon - Clear Thread Exception");
			oEx.printStackTrace();
		}
	}
	
}
