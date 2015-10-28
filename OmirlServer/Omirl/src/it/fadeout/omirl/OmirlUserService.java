package it.fadeout.omirl;

import it.fadeout.omirl.business.OmirlUser;
import it.fadeout.omirl.business.config.OmirlNavigationConfig;
import it.fadeout.omirl.business.config.TableLinkConfig;
import it.fadeout.omirl.data.OmirlUserRepository;
import it.fadeout.omirl.viewmodels.TableLink;

import java.util.ArrayList;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Path("/users")
public class OmirlUserService {

	@GET
	@Path("/load")
	@Produces({"application/xml", "application/json", "text/xml"})	
	public ArrayList<OmirlUser> getOmirlUsers(@HeaderParam("x-session-token") String sSessionId) {

		ArrayList<OmirlUser> aoRet = new ArrayList<OmirlUser>();
		OmirlUserRepository oRepo = new OmirlUserRepository();

		try {
			boolean bShowPrivate = false;
			if (Omirl.getUserFromSession(sSessionId) != null) {
				aoRet = oRepo.selectUsersNotAdmin();
			}

		}
		catch(Exception oEx) {
			oEx.printStackTrace();
		}

		return aoRet;
	}

	@POST
	@Path("/save")
	@Produces({"application/xml", "application/json", "text/xml"})	
	public Boolean SaveOrUpdate(@HeaderParam("x-session-token") String sSessionId, OmirlUser oUser) {

		Boolean aoRet = true;
		OmirlUserRepository oRepo = new OmirlUserRepository();

		try {
			if (Omirl.getUserFromSession(sSessionId) != null) {
				try
				{
					//check if user exists
					OmirlUser oDBuser = oRepo.selectByUserId(oUser.getUserId());
					if (oDBuser != null)
					{
						//change name and password
						oDBuser.setName(oUser.getName());
						oDBuser.setPassword(oUser.getPassword());
						oRepo.Save(oDBuser);
					}
					else
					{
						//new user
						oRepo.Save(oUser);
					}
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
		OmirlUserRepository oRepo = new OmirlUserRepository();

		try {
			if (Omirl.getUserFromSession(sSessionId) != null) {
				OmirlUser oUser = oRepo.Select(id, OmirlUser.class);
				if (oUser != null)
					aoRet = oRepo.Delete(oUser);
			}
		}
		catch(Exception oEx) {
			oEx.printStackTrace();
		}

		return aoRet;
	}

}
