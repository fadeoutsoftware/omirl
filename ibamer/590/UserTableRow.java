package it.acrotec.rest.api.admin;

import it.acrotec.lib.settings.entities.CircleEntity;
import it.acrotec.lib.settings.entities.HatEntity;
import it.acrotec.lib.settings.entities.OrganizationEntity;
import it.acrotec.lib.settings.entities.SmartUserEntity;
import it.acrotec.lib.settings.repositories.CircleRepository;
import it.acrotec.lib.settings.repositories.HatRepository;
import it.acrotec.rest.api.Upload;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UserTableRow {
	
	String id;
	String name;
	String avatar;
	OrganizationEntity organization;
	String email;
	String phone;
	ArrayList<Integer> hats = new ArrayList<Integer>();
	ArrayList<Integer> circles = new ArrayList<Integer>();
	
	public UserTableRow() {
	}
	
//	public UserTableRow(UserEntitySmart ud) {
//		id = ud.getId();
//		name = ud.getName();
//		avatar = "img/avatar/" + id;
//		email = ud.getEmail();
//		phone = ud.getPhone();
//		organization = ud.getOrganization();
//		for (HatSimple h : ud.getHats()) {
//			hats.add(h.getId());
//		}
//		for (Circle c : ud.getCircles()) {
//			circles.add(c.getId());
//		}
//	}
	
	public UserTableRow(SmartUserEntity ud) {
		id = ud.getId();
		name = ud.getName();
		avatar = Upload.getAvatarBaseUrl() + id;
		email = ud.getEmail();
		phone = ud.getPhone();
		organization = ud.getOrganization();
		for (HatEntity h : ud.getHats()) {
			hats.add(h.getId());
		}
		for (CircleEntity c : ud.getCircles()) {
			circles.add(c.getId());
		}		
	}
	
//	public UserEntitySmart toUserEntitySmart() {
//		UserEntitySmart u = new UserEntitySmart();
//		u.setId(getId());
//		u.setName(getName());
//		u.setEmail(getEmail());
//		u.setPhone(getPhone());
//		u.setOrganization(getOrganization());
//		u.setHats(new ArrayList<HatSimple>());
//		HatSimpleRepository hatRepo = new HatSimpleRepository();
//		for (String h : getHats()) {
//			u.getHats().add(hatRepo.select(h, HatSimple.class));
//		}
//		CircleRepository circleRepo = new CircleRepository();
//		u.setCircles(new ArrayList<Circle>());
//		for (String c : getCircles()) {
//			u.getCircles().add(circleRepo.select(c, Circle.class));
//		}
//		return u ;
//	}
	
	public SmartUserEntity toSmartUserEntity() {
		SmartUserEntity u = new SmartUserEntity();
		u.setId(getId());
		u.setName(getName());
		u.setEmail(getEmail());
		u.setPhone(getPhone());
		u.setOrganization(getOrganization());
		u.setHats(new ArrayList<HatEntity>());
		HatRepository hatRepo = new HatRepository();
		for (Integer h : getHats()) {
			u.getHats().add(hatRepo.select(h, HatEntity.class));
		}
		CircleRepository circleRepo = new CircleRepository();
		u.setCircles(new ArrayList<CircleEntity>());
		for (Integer c : getCircles()) {
			u.getCircles().add(circleRepo.select(c, CircleEntity.class));
		}
		return u;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public OrganizationEntity getOrganization() {
		return organization;
	}

	public void setOrganization(OrganizationEntity organization) {
		this.organization = organization;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public ArrayList<Integer> getHats() {
		return hats;
	}

	public void setHats(ArrayList<Integer> hats) {
		this.hats = hats;
	}

	public ArrayList<Integer> getCircles() {
		return circles;
	}

	public void setCircles(ArrayList<Integer> circles) {
		this.circles = circles;
	}

}
