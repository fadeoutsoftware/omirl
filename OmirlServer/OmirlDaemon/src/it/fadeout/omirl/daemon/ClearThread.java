package it.fadeout.omirl.daemon;

import it.fadeout.omirl.business.SavedPeriod;
import it.fadeout.omirl.data.SavedPeriodRepository;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class ClearThread extends Thread {
	String m_sFileRepoPath = "";
	List<SavedPeriod> m_aoSavedPeriods = new ArrayList<>();
	
	public ClearThread(String arg) {
		super(arg);
		m_sFileRepoPath = arg;
	}
	
	
	public void run() {
		
		try {
			
			System.out.println("OmirlDaemon ClearThread - Start clean operations");
			
			// Set UTC as default
			DateTimeZone.setDefault(DateTimeZone.UTC);
			
			// Create Now and get yesterday
			DateTime oDateTime = new DateTime();
			DateTime oYesterday = oDateTime.minusDays(1);
			
			// Read all saved periods
			SavedPeriodRepository oSavedPeriodRepository = new SavedPeriodRepository();
			m_aoSavedPeriods = oSavedPeriodRepository.SelectAll(SavedPeriod.class);
			
			System.out.println("OmirlDaemon ClearThread - clearing charts");
			
			// Start from Charts Folder
			File oChartFolder = new File(m_sFileRepoPath+"/charts");
			
			String [] asYears = oChartFolder.list();
			// Get All Files and dirs
			//Collection<File> aoYears = FileUtils.listFiles(oChartFolder,TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
			
			Collection<File> aoYears = new ArrayList<File> ();
			
			for (String sYear : asYears) {
				aoYears.add(new File(oChartFolder.getAbsolutePath()+"/" + sYear));
			}
			
			
			// For each year
			for (File oYearFolder : aoYears) {
				
				// Initialize Year
				int iYear = oYesterday.getYear();
				
				// Parse folder name
				try{
					iYear = Integer.parseInt(oYearFolder.getName());
				}
				catch(Exception oEx) {
					System.out.println("OmirlDaemon Clear Thread: excpetion reading year folder " + oEx.toString());
					oEx.printStackTrace();
				}
								
				String [] asMonths = oYearFolder.list();				
				Collection<File> aoMonths = new ArrayList<File> ();
				
				for (String sMonth : asMonths) {
					aoMonths.add(new File(oYearFolder.getAbsolutePath()+"/" + sMonth));
				}
				
				// For each month
				for (File oMonthFolder : aoMonths) {
					
					// Initialize month
					int iMonth = oYesterday.getMonthOfYear();
					
					// Parse the name 
					try{
						iMonth = Integer.parseInt(oMonthFolder.getName());
					}
					catch(Exception oEx) {
						System.out.println("OmirlDaemon Clear Thread: excpetion reading month folder " + oEx.toString());
						oEx.printStackTrace();					
					}				
										
					String [] asDays = oMonthFolder.list();				
					Collection<File> aoDays = new ArrayList<File> ();
					
					for (String sDay : asDays) {
						aoDays.add(new File(oMonthFolder.getAbsolutePath()+"/" + sDay));
					}					
					
					// For each day
					for (File oDayFolder : aoDays) {
						
						// Initialize day
						int iDay = oYesterday.getDayOfMonth();
						
						// Parse the name
						try{
							iDay = Integer.parseInt(oDayFolder.getName());
						}
						catch(Exception oEx) {
							System.out.println("OmirlDaemon Clear Thread: excpetion reading day folder " + oEx.toString());
							oEx.printStackTrace();					
						}				
						
						// Get all subfolders and files
						Collection<File> aoSubFolders = FileUtils.listFiles(oDayFolder,TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
						
						// Create a Date Time with the created date
						DateTime oFileDateTime = new DateTime(iYear, iMonth, iDay, 12, 00);
						
						// Keep the last day
						if (oFileDateTime.isAfter(oYesterday)) continue;
						// Keep Saved periods
						if (IsSavedPeriod(oFileDateTime)) continue;
						
						// Delete otherwise!
						for (File oSubFolder : aoSubFolders) {
							FileUtils.deleteQuietly(oSubFolder);
						}
						
						// Delete the day folder
						FileUtils.deleteQuietly(oDayFolder);
					}
					
					
					File oCheckMonth = new File(oMonthFolder.getAbsolutePath());
					if (oCheckMonth.list().length==0) {
						// Delete the day folder
						FileUtils.deleteQuietly(oCheckMonth);						
					}
				}
				
				File oCheckYear = new File(oYearFolder.getAbsolutePath());
				if (oCheckYear.list().length==0) {
					// Delete the day folder
					FileUtils.deleteQuietly(oCheckYear);						
				}
			}	
			

			
			/////////////////////////////////////////////////////////////////////////////////////////////////////
			System.out.println("OmirlDaemon ClearThread - clearing stations");
			
			// Stations Folder
			File oStationsFolder = new File(m_sFileRepoPath+"/stations");
			
			String [] asTypes = oStationsFolder.list();
			
			Collection<File> aoTypes = new ArrayList<File> ();
			
			for (String sType : asTypes) {
				aoTypes.add(new File(oStationsFolder.getAbsolutePath()+"/" + sType));
			}
			
			
			// For each type
			for (File oTypeFolder : aoTypes) {
				
				asYears = oTypeFolder.list();				
				aoYears = new ArrayList<File> ();
				
				for (String sYear : asYears) {
					aoYears.add(new File(oTypeFolder.getAbsolutePath()+"/" + sYear));
				}
				
				
				// For each year
				for (File oYearFolder : aoYears) {
					
					// Initialize Year
					int iYear = oYesterday.getYear();
					
					// Parse folder name
					try{
						iYear = Integer.parseInt(oYearFolder.getName());
					}
					catch(Exception oEx) {
						System.out.println("OmirlDaemon Clear Thread: excpetion reading year folder " + oEx.toString());
						oEx.printStackTrace();
					}
									
					String [] asMonths = oYearFolder.list();				
					Collection<File> aoMonths = new ArrayList<File> ();
					
					for (String sMonth : asMonths) {
						aoMonths.add(new File(oYearFolder.getAbsolutePath()+"/" + sMonth));
					}
					
					// For each month
					for (File oMonthFolder : aoMonths) {
						
						// Initialize month
						int iMonth = oYesterday.getMonthOfYear();
						
						// Parse the name 
						try{
							iMonth = Integer.parseInt(oMonthFolder.getName());
						}
						catch(Exception oEx) {
							System.out.println("OmirlDaemon Clear Thread: excpetion reading month folder " + oEx.toString());
							oEx.printStackTrace();					
						}				
											
						String [] asDays = oMonthFolder.list();				
						Collection<File> aoDays = new ArrayList<File> ();
						
						for (String sDay : asDays) {
							aoDays.add(new File(oMonthFolder.getAbsolutePath()+"/" + sDay));
						}					
						
						// For each day
						for (File oDayFolder : aoDays) {
							
							// Initialize day
							int iDay = oYesterday.getDayOfMonth();
							
							// Parse the name
							try{
								iDay = Integer.parseInt(oDayFolder.getName());
							}
							catch(Exception oEx) {
								System.out.println("OmirlDaemon Clear Thread: excpetion reading day folder " + oEx.toString());
								oEx.printStackTrace();					
							}				
							
							// Get all subfolders and files
							Collection<File> aoSubFolders = FileUtils.listFiles(oDayFolder,TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
							
							// Create a Date Time with the created date
							DateTime oFileDateTime = new DateTime(iYear, iMonth, iDay, 12, 00);
							
							// Keep the last day
							if (oFileDateTime.isAfter(oYesterday)) continue;
							// Keep Saved periods
							if (IsSavedPeriod(oFileDateTime)) continue;
							
							// Delete otherwise!
							for (File oSubFolder : aoSubFolders) {
								FileUtils.deleteQuietly(oSubFolder);
							}
							
							// Delete the day folder
							FileUtils.deleteQuietly(oDayFolder);
						}
						
						
						File oCheckMonth = new File(oMonthFolder.getAbsolutePath());
						if (oCheckMonth.list().length==0) {
							// Delete the day folder
							FileUtils.deleteQuietly(oCheckMonth);						
						}
					}
					
					File oCheckYear = new File(oYearFolder.getAbsolutePath());
					if (oCheckYear.list().length==0) {
						// Delete the day folder
						FileUtils.deleteQuietly(oCheckYear);						
					}
				}	
			}
			
			
			System.out.println("OmirlDaemon ClearThread - clear done");
			
		}
		catch(Exception oEx) {
			System.out.println("OmirlDaemon - Clear Thread Exception");
			oEx.printStackTrace();
		}
		
	}
	
	public boolean IsSavedPeriod(DateTime oDateTime) {
		
		boolean bRet = false;
		long lCheckTime = oDateTime.toDate().getTime();
		
		for (int iSaved = 0; iSaved < m_aoSavedPeriods.size(); iSaved++) {
			SavedPeriod oPeriod = m_aoSavedPeriods.get(iSaved);
			
			// Check if the date is exactly in the save pattern
			if (oPeriod.getTimestampStart()<= lCheckTime && oPeriod.getTimestampEnd() >= lCheckTime) {
				bRet = true;
				break;
			}
			
			
			DateTime oOnlyDay = new DateTime(oPeriod.getTimestampStart());
			if (oOnlyDay.getDayOfMonth()==oDateTime.getDayOfMonth() && oOnlyDay.getMonthOfYear() == oDateTime.getMonthOfYear() && oOnlyDay.getYear() == oDateTime.getYear()) {
				bRet = true;
				break;				
			}
		}
		
		return bRet;
	}

}
