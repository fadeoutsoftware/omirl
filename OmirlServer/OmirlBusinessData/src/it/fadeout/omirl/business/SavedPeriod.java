package it.fadeout.omirl.business;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name="savedperiods")
public class SavedPeriod {
	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy = "increment")
	@Column(name="idsavedperiod")		
	int idSavedPeriod;
	
	@Column(name="timestampstart")	
	long timestampStart;
	
	@Column(name="timestampend")
	long timestampEnd;
	
	@Column(name="iduser")
	Integer idUser;

	public int getIdSavedPeriod() {
		return idSavedPeriod;
	}

	public void setIdSavedPeriod(int idSavedPeriod) {
		this.idSavedPeriod = idSavedPeriod;
	}

	public long getTimestampStart() {
		return timestampStart;
	}

	public void setTimestampStart(long timestampStart) {
		this.timestampStart = timestampStart;
	}

	public long getTimestampEnd() {
		return timestampEnd;
	}

	public void setTimestampEnd(long timestampEnd) {
		this.timestampEnd = timestampEnd;
	}

	public Integer getIdUser() {
		return idUser;
	}

	public void setIdUser(Integer idUser) {
		this.idUser = idUser;
	}
	
	
}
