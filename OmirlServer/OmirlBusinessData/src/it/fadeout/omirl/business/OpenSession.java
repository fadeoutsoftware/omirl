package it.fadeout.omirl.business;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name="opensessions")
public class OpenSession {
	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy = "increment")
	@Column(name="idopensession")
	int idOpenSession;
	@Column(name="sessionid")
	String sessionId;
	@Column(name="iduser")
	Integer idUser;
	@Column(name="lasttouch")
	Long lastTouch;
	
	public int getIdOpenSession() {
		return idOpenSession;
	}
	public void setIdOpenSession(int idOpenSession) {
		this.idOpenSession = idOpenSession;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public Integer getIdUser() {
		return idUser;
	}
	public void setIdUser(Integer idUser) {
		this.idUser = idUser;
	}
	public Long getLastTouch() {
		return lastTouch;
	}
	public void setLastTouch(Long lastTouch) {
		this.lastTouch = lastTouch;
	}

}
