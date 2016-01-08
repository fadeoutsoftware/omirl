package it.fadeout.omirl;

import java.util.Date;
import java.util.UUID;

import it.fadeout.omirl.business.OmirlUser;
import it.fadeout.omirl.business.OpenSession;
import it.fadeout.omirl.data.OmirlUserRepository;
import it.fadeout.omirl.data.OpenSessionRepository;
import it.fadeout.omirl.viewmodels.LoginInfo;
import it.fadeout.omirl.viewmodels.PrimitiveResult;
import it.fadeout.omirl.viewmodels.UserSettingsViewModel;
import it.fadeout.omirl.viewmodels.UserViewModel;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/auth")
public class AuthService {

	
	@GET
	@Path("/test")
	@Produces({"application/xml", "application/json", "text/xml"})
	public PrimitiveResult TestOmirl() {
		// Just a keep alive message
		PrimitiveResult oTest = new PrimitiveResult();
		oTest.StringValue = "Omirl AuthService is Working";
		return oTest;
	}
	
	@POST
	@Path("/login")
	@Produces({"application/xml", "application/json", "text/xml"})
	public UserViewModel Login(LoginInfo oLoginInfo) {
		UserViewModel oUserVM = new UserViewModel();
		oUserVM.setLogged(false);
		
		if (oLoginInfo == null) {
			return oUserVM;
		}
		
		if (oLoginInfo.getUserId() == null) {
			return oUserVM;
		}
		
		if (oLoginInfo.getUserId().equals("")) {
			return oUserVM;
		}
		
		if (oLoginInfo.getUserPassword() == null) {
			return oUserVM;
		}
		
		if (oLoginInfo.getUserPassword().equals("")) {
			return oUserVM;
		}
		
		System.out.println("AuthService.Login: requested access from " + oLoginInfo.getUserId());
		
		OmirlUserRepository oOmirlUserRepository = new OmirlUserRepository();
		
		boolean bLogged = oOmirlUserRepository.login(oLoginInfo.getUserId(), oLoginInfo.getUserPassword());
		
		if (bLogged)
		{
			OmirlUser oUser = oOmirlUserRepository.selectByUserId(oLoginInfo.getUserId());
			
			if (oUser != null)
			{
				oUserVM.setLogged(bLogged);
				oUserVM.setDefaultLat(oUser.getDefaultLat());
				oUserVM.setDefaultLon(oUser.getDefaultLon());
				oUserVM.setDefaultSensorType(oUser.getDefaultSensorType());
				oUserVM.setDefaultZoom(oUser.getDefaultZoom());
				oUserVM.setName(oUser.getName());
				oUserVM.setRole(oUser.getRole());
				oUserVM.setMail(oUser.getUserId());
				oUserVM.setDefaultStatics(oUser.getDefaultStatics());
				oUserVM.setDefaultMap(oUser.getDefaultMap());
				
				
				OpenSession oUserSession = new OpenSession();
				oUserSession.setIdUser(oUser.getIdUser());
				oUserSession.setLastTouch(new Date().getTime());
				
				String sSessionId = UUID.randomUUID().toString();
				
				oUserSession.setSessionId(sSessionId);
				
				OpenSessionRepository oOpenSessionRepository = new OpenSessionRepository();
				oOpenSessionRepository.Save(oUserSession);
				
				oUserVM.setSessionId(sSessionId);
				
				System.out.println("AuthService.Login: access succeeded");
			}
			else
			{
				System.out.println("AuthService.Login: access failed");
			}
		}
		else
		{
			System.out.println("AuthService.Login: access failed");
		}
		
		return oUserVM;
	}

	@GET
	@Path("/logout")
	@Produces({"application/xml", "application/json", "text/xml"})
	public PrimitiveResult Logout(@HeaderParam("x-session-token") String sSessionId) {
		PrimitiveResult oResult = new PrimitiveResult();
		oResult.BoolValue = false;
		
		if (sSessionId == null) return oResult;
		if (sSessionId.isEmpty()) return oResult;
		
		OpenSessionRepository oOpenSessionRepository = new OpenSessionRepository();
		OpenSession oSession = oOpenSessionRepository.selectBySessionId(sSessionId);
		if(oSession != null) {
			oResult.BoolValue = true;
			oOpenSessionRepository.Delete(oSession);
		}
		
		return oResult;
	}
	
	@POST
	@Path("/settings")
	@Produces({"application/xml", "application/json", "text/xml"})
	public PrimitiveResult SaveSettings(UserSettingsViewModel oSettings, @HeaderParam("x-session-token") String sSessionId) {
		PrimitiveResult oResult = new PrimitiveResult();
		oResult.BoolValue = false;

		if (sSessionId == null) {
			oResult.StringValue = "Sessione non valida";
			return oResult;
		}
		if (sSessionId.isEmpty()) {
			oResult.StringValue = "Sessione non valida";
			return oResult;
		}
		if (oSettings == null) {
			oResult.StringValue = "Si è verificato un errore si prega di riprovare";
			return oResult;
		}
		
		
		OpenSessionRepository oOpenSessionRepository = new OpenSessionRepository();
		OpenSession oSession = oOpenSessionRepository.selectBySessionId(sSessionId);
		
		if(oSession != null) {
			OmirlUserRepository oOmirlUserRepository = new OmirlUserRepository();
			OmirlUser oUser = oOmirlUserRepository.Select(oSession.getIdUser(), OmirlUser.class);
			
			if (oUser != null) {
				
				if (oSettings.isChangePassword())
				{
					if (oUser.getPassword().equals(oSettings.getOldPassword()) == false)
					{
						oResult.StringValue = "Password errata";
						return oResult;
					}
					
					if (oSettings.getNewPassword().equals(oSettings.getConfirmPassword()) == false)
					{
						oResult.StringValue = "Password non corrispondenti";
						return oResult;					
					}
					
					if (oSettings.getNewPassword().isEmpty())
					{
						oResult.StringValue = "Password vuota";
						return oResult;					
					}
					
					
					oUser.setPassword(oSettings.getNewPassword());
				}
				
				oUser.setName(oSettings.getUserName());
				
				oOmirlUserRepository.Save(oUser);
				
				oResult.BoolValue = true;
			}
		}
		else {
			oResult.StringValue = "Sessione non valida o scaduta";
		}
		
		return oResult;
	}
	
	
	@POST
	@Path("/mapsettings")
	@Produces({"application/xml", "application/json", "text/xml"})
	public PrimitiveResult SaveMapSettings(UserViewModel oSettings, @HeaderParam("x-session-token") String sSessionId) {
		PrimitiveResult oResult = new PrimitiveResult();
		oResult.BoolValue = false;

		if (sSessionId == null) {
			oResult.StringValue = "Sessione non valida";
			return oResult;
		}
		if (sSessionId.isEmpty()) {
			oResult.StringValue = "Sessione non valida";
			return oResult;
		}
		if (oSettings == null) {
			oResult.StringValue = "Si è verificato un errore si prega di riprovare";
			return oResult;
		}
		
		
		OpenSessionRepository oOpenSessionRepository = new OpenSessionRepository();
		OpenSession oSession = oOpenSessionRepository.selectBySessionId(sSessionId);
		
		if(oSession != null) {
			OmirlUserRepository oOmirlUserRepository = new OmirlUserRepository();
			OmirlUser oUser = oOmirlUserRepository.Select(oSession.getIdUser(), OmirlUser.class);
			
			if (oUser != null) {
				
				oUser.setDefaultLat(oSettings.getDefaultLat());
				oUser.setDefaultLon(oSettings.getDefaultLon());
				oUser.setDefaultZoom(oSettings.getDefaultZoom());
				oUser.setDefaultSensorType(oSettings.getDefaultSensorType());
				oUser.setDefaultStatics(oSettings.getDefaultStatics());
				oUser.setDefaultMap(oSettings.getDefaultMap());
				
				oOmirlUserRepository.Save(oUser);
				
				oResult.BoolValue = true;
			}
		}
		else {
			oResult.StringValue = "Sessione non valida o scaduta";
		}
		
		return oResult;
	}
	
	@GET
	@Path("/sessionCheck")
	@Produces({"application/xml", "application/json", "text/xml"})
	@Consumes({"application/xml", "application/json", "text/xml"})
	public UserViewModel SessionCheck(@HeaderParam("x-session-token") String sSessionId) {
		
		UserViewModel oUserVM = null;
		
		OmirlUser oUser = Omirl.getUserFromSession(sSessionId);
		
		if (oUser != null)
		{
			oUserVM = new UserViewModel();
			
			oUserVM.setLogged(true);
			oUserVM.setDefaultLat(oUser.getDefaultLat());
			oUserVM.setDefaultLon(oUser.getDefaultLon());
			oUserVM.setDefaultSensorType(oUser.getDefaultSensorType());
			oUserVM.setDefaultZoom(oUser.getDefaultZoom());
			oUserVM.setName(oUser.getName());
			oUserVM.setRole(oUser.getRole());
			oUserVM.setMail(oUser.getUserId());
			oUserVM.setDefaultStatics(oUser.getDefaultStatics());
			oUserVM.setDefaultMap(oUser.getDefaultMap());
						
			oUserVM.setSessionId(sSessionId);
			
			System.out.println("AuthService.SessionCheck: access succeeded");
		}
		else
		{
			System.out.println("AuthService.SessionCheck: access failed");
		}
		
		return oUserVM;
	}

	
	@GET
	@Path("/cookieCheck/{sessionId}")
	@Produces({"application/xml", "application/json", "text/xml"})
	@Consumes({"application/xml", "application/json", "text/xml"})
	public UserViewModel CookieCheck(@PathParam("sessionId") String sSessionId) {
		
		UserViewModel oUserVM = null;
		
		OmirlUser oUser = Omirl.getUserFromSession(sSessionId);
		
		if (oUser != null)
		{
			oUserVM = new UserViewModel();
			
			oUserVM.setLogged(true);
			oUserVM.setDefaultLat(oUser.getDefaultLat());
			oUserVM.setDefaultLon(oUser.getDefaultLon());
			oUserVM.setDefaultSensorType(oUser.getDefaultSensorType());
			oUserVM.setDefaultZoom(oUser.getDefaultZoom());
			oUserVM.setName(oUser.getName());
			oUserVM.setRole(oUser.getRole());
			oUserVM.setMail(oUser.getUserId());
			oUserVM.setDefaultStatics(oUser.getDefaultStatics());
			oUserVM.setDefaultMap(oUser.getDefaultMap());
						
			oUserVM.setSessionId(sSessionId);
			
			System.out.println("AuthService.SessionCheck: access succeeded");
		}
		else
		{
			System.out.println("AuthService.SessionCheck: access failed");
		}
		
		return oUserVM;
	}
}
