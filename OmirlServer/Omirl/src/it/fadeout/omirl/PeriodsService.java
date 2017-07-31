package it.fadeout.omirl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import it.fadeout.omirl.business.OmirlUser;
import it.fadeout.omirl.business.SavedPeriod;
import it.fadeout.omirl.data.OmirlUserRepository;
import it.fadeout.omirl.data.SavedPeriodRepository;
import it.fadeout.omirl.viewmodels.PeriodViewModel;

@Path("/periods")
public class PeriodsService {

	@GET
	@Path("/load")
	@Produces({"application/xml", "application/json", "text/xml"})	
	public ArrayList<PeriodViewModel> getSavedPeriods(@HeaderParam("x-session-token") String sSessionId) {

		ArrayList<SavedPeriod> aoSavedPeriods = new ArrayList<SavedPeriod>();
		ArrayList<PeriodViewModel> aoRet = new ArrayList<PeriodViewModel>();
		SavedPeriodRepository oRepo = new SavedPeriodRepository(); 
		OmirlUser oUser = Omirl.getUserFromSession(sSessionId);
		if (oUser != null) {
			try {
				DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
				if (Omirl.getUserFromSession(sSessionId) != null) {
					aoSavedPeriods = (ArrayList<SavedPeriod>) oRepo.SelectAll(SavedPeriod.class);
					if (aoSavedPeriods != null && aoSavedPeriods.size() > 0)
					{
						for (SavedPeriod oSavedPeriod : aoSavedPeriods) {
							Date oDateStart = new Date(oSavedPeriod.getTimestampStart());
							Date oDateEnd = new Date(oSavedPeriod.getTimestampEnd());
							formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
							PeriodViewModel oViewModel = new PeriodViewModel();
							oViewModel.setIdPeriod(oSavedPeriod.getIdSavedPeriod());
							oViewModel.setTimestampStart(formatter.format(oDateStart));
							oViewModel.setTimestampEnd(formatter.format(oDateEnd));
							aoRet.add(oViewModel);
						}
					}
				}
			}
			catch(Exception oEx) {
				oEx.printStackTrace();
			}
		}

		return aoRet;
	}

	@POST
	@Path("/save")
	@Produces({"application/xml", "application/json", "text/xml"})	
	public Boolean SaveOrUpdate(@HeaderParam("x-session-token") String sSessionId,  PeriodViewModel oPeriodViewModel) {

		Boolean aoRet = true;
		SavedPeriodRepository oRepo = new SavedPeriodRepository();
		OmirlUser oUser = Omirl.getUserFromSession(sSessionId);
		try {
			if (oUser != null) {
				try
				{

					DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
					formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
					//check if user exists
					SavedPeriod oDBSavedPeriod = null;
					if (oPeriodViewModel.getIdPeriod() != null)
						oDBSavedPeriod = oRepo.selectByPeriodId(oPeriodViewModel.getIdPeriod());
					if (oDBSavedPeriod == null)
						oDBSavedPeriod = new SavedPeriod();

					Date oDateStart = formatter.parse(oPeriodViewModel.getTimestampStart());
					Date oDateEnd = formatter.parse(oPeriodViewModel.getTimestampEnd());
					
					oDBSavedPeriod.setTimestampStart(oDateStart.getTime());
					oDBSavedPeriod.setTimestampEnd(oDateEnd.getTime());
					oDBSavedPeriod.setIdUser(oUser.getIdUser());
					oRepo.Save(oDBSavedPeriod);

				}
				catch(Exception oEx)
				{
					oEx.printStackTrace();
					aoRet = false;
				}
			}
		}
		catch(Exception oEx) {
			oEx.printStackTrace();
		}
		return aoRet;
	}

	@POST
	@Path("/delete/{id}")
	@Produces({"application/xml", "application/json", "text/xml"})	
	@Consumes({"application/xml", "application/json", "text/xml"})
	public Boolean Delete(@HeaderParam("x-session-token") String sSessionId, @PathParam("id") Integer id) {

		Boolean aoRet = true;
		SavedPeriodRepository oRepo = new SavedPeriodRepository();

		try {
			if (Omirl.getUserFromSession(sSessionId) != null) {
				SavedPeriod oSavedPeriod = oRepo.Select(id, SavedPeriod.class);
				if (oSavedPeriod != null)
					aoRet = oRepo.Delete(oSavedPeriod);
			}
		}
		catch(Exception oEx) {
			oEx.printStackTrace();
		}

		return aoRet;
	}

}
