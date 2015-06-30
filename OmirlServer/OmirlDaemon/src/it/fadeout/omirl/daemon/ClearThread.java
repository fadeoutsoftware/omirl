package it.fadeout.omirl.daemon;

import it.fadeout.omirl.business.SavedPeriod;
import it.fadeout.omirl.daemon.geoserver.GeoServerDataManager2;
import it.fadeout.omirl.data.SavedPeriodRepository;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class ClearThread extends Thread {
	String m_sFileRepoPath = "";
	List<SavedPeriod> m_aoSavedPeriods = new ArrayList<>();
	int m_iSavedDays = 1;
	String m_sGeoServerAddress;
	String m_sGeoServerUser;
	String m_sGeoServerPassword;
	String m_sGeoServerDataFolder;
	
	public ClearThread(String arg, int iDays,String sGeoServerAddress,String sGeoServerUser,String sGeoServerPassword,String sGeoServerDataFolder) {
		super(arg);
		m_sFileRepoPath = arg;
		m_iSavedDays = iDays;
		
		m_sGeoServerAddress = sGeoServerAddress;
		m_sGeoServerUser = sGeoServerUser;
		m_sGeoServerPassword = sGeoServerPassword;
		m_sGeoServerDataFolder = sGeoServerDataFolder;
	}
	
	
	public void run() {
		
		try {
			
			System.out.println("OmirlDaemon ClearThread - Start clean operations");
			
			// Set UTC as default
			DateTimeZone.setDefault(DateTimeZone.UTC);
			
			// Create Now and get yesterday
			DateTime oDateTime = new DateTime();
			DateTime oYesterday = oDateTime.minusDays(m_iSavedDays);
			
			// Read all saved periods
			SavedPeriodRepository oSavedPeriodRepository = new SavedPeriodRepository();
			m_aoSavedPeriods = oSavedPeriodRepository.SelectAll(SavedPeriod.class);
			
			
			
			/////////////////////////////////////////////////////////////////////////////////////////////////////
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
						
						// Keep the last day and Saved periods
						if (oFileDateTime.isAfter(oYesterday) || IsSavedPeriod(oFileDateTime)) {
							continue;
						}
						
						System.out.println("Clear Thread - charts: Deleted folder " + oDayFolder.getAbsolutePath());
						
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
							
							
							
							// Keep the last day and Saved periods
							if (oFileDateTime.isAfter(oYesterday) || IsSavedPeriod(oFileDateTime)) {
								continue;
							}
							
							System.out.println("Clear Thread - stations: Deleted folder " + oDayFolder.getAbsolutePath());
							
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
			
			
			
			/////////////////////////////////////////////////////////////////////////////////////////////////////
			System.out.println("OmirlDaemon ClearThread - clearing sections");
			
			// Sections Folder
			File oSectionsFolder = new File(m_sFileRepoPath+"/sections");
			
			String [] asModels = oSectionsFolder.list();
			
			Collection<File> aoModels = new ArrayList<File> ();
			
			for (String sModel : asModels) {
				aoModels.add(new File(oSectionsFolder.getAbsolutePath()+"/" + sModel));
			}
			
			
			// For each model
			for (File oModelFolder : aoModels) {
				
				//System.out.println("OmirlDaemon ClearThread - clearing sections PATH " + oModelFolder.getAbsolutePath());
				
				asYears = oModelFolder.list();				
				aoYears = new ArrayList<File> ();
				
				for (String sYear : asYears) {
					aoYears.add(new File(oModelFolder.getAbsolutePath()+"/" + sYear));
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
							
							// Keep the last day and Saved periods
							if (oFileDateTime.isAfter(oYesterday) || IsSavedPeriod(oFileDateTime)) {
								continue;
							}
							
							System.out.println("Clear Thread - sections: Deleted folder " + oDayFolder.getAbsolutePath());
							
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
			
			
			
			
			/////////////////////////////////////////////////////////////////////////////////////////////////////
			System.out.println("OmirlDaemon ClearThread - clearing maps");
			
			// Sections Folder
			File oMapsFolder = new File(m_sFileRepoPath+"/maps");
			
			String [] asMapTypes = oMapsFolder.list();
			
			Collection<File> aoMapTypes = new ArrayList<File> ();
			
			for (String sMap : asMapTypes) {
				aoMapTypes.add(new File(oMapsFolder.getAbsolutePath()+"/" + sMap));
			}
			
			
			// For each folder
			for (File oMapFolder : aoMapTypes) {
				
				//System.out.println("OmirlDaemon ClearThread - clearing sections PATH " + oModelFolder.getAbsolutePath());
				
				asYears = oMapFolder.list();				
				aoYears = new ArrayList<File> ();
				
				for (String sYear : asYears) {
					aoYears.add(new File(oMapFolder.getAbsolutePath()+"/" + sYear));
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
							
							// Keep the last day and Saved periods
							if (oFileDateTime.isAfter(oYesterday) || IsSavedPeriod(oFileDateTime)) {
								continue;
							}

							System.out.println("Clear Thread - maps: Deleted folder " + oDayFolder.getAbsolutePath());
							
							// Delete otherwise!
							for (File oSubFolder : aoSubFolders) {
								
								if (oSubFolder.isFile())
								{
									String sName = oSubFolder.getName();
									if (sName.length()>4)
									{
										sName = sName.substring(0, sName.length()-4);
									}
									
									if (!sName.startsWith("index"))
									{
																			
										String sPrefix ="" + iYear;
										if (iMonth<10) sPrefix += "0";
										sPrefix += iMonth;
										if (iDay<10) sPrefix +="0";
										sPrefix +=iDay;
										
										sName =  sPrefix + sName;
	
										
										//System.out.println("CLEAR THREAD DELETE LAYER "+sName);
										deleteLayer(sName, "omirl", m_sGeoServerAddress, m_sGeoServerUser, m_sGeoServerPassword);
										
										try {
											File oFile = new File(m_sGeoServerDataFolder+"/"+sName+".tif");
											//System.out.println("CLEAR THREAD DELETE FILE "+oFile.getAbsolutePath());
											FileUtils.deleteQuietly(oFile);										
										}
										catch(Exception oEx)
										{
											oEx.printStackTrace();
										}
									}
								}
								
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
			
			
			/////////////////////////////////////////////////////////////////////////////////////////////////////
			System.out.println("OmirlDaemon ClearThread - clearing gallery");
			
			// Sections Folder
			File oGalleryFolder = new File(m_sFileRepoPath+"/gallery");
			
			asYears = oGalleryFolder.list();				
			aoYears = new ArrayList<File> ();
			
			for (String sYear : asYears) {
				aoYears.add(new File(oGalleryFolder.getAbsolutePath()+"/" + sYear));
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
						
						// Keep the last day and Saved periods
						if (oFileDateTime.isAfter(oYesterday) || IsSavedPeriod(oFileDateTime)) {
							continue;
						}

						System.out.println("Clear Thread - gallery: Deleted folder " + oDayFolder.getAbsolutePath());
						
//						// Delete otherwise!
//						for (File oSubFolder : aoSubFolders) {
//							FileUtils.deleteQuietly(oSubFolder);
//						}
						
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
			
			System.out.println("OmirlDaemon ClearThread - clear done");
			
		}
		catch(Exception oEx) {
			System.out.println("OmirlDaemon - Clear Thread Exception");
			oEx.printStackTrace();
		}
		
	}
	
	private boolean deleteLayer(String sLayerName, String sNameSpace, String sGeoServerAddress,String sGeoServerUser,String sGeoServerPassword)
	{
		try {
			GeoServerDataManager2 oGeoManager = new GeoServerDataManager2(sGeoServerAddress, "", sGeoServerUser, sGeoServerPassword);
			oGeoManager.removeLayer(sLayerName, sNameSpace);
			
			return true;
		}
		catch(Exception oEx) {
			oEx.printStackTrace();
		}
		
		return false;
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
