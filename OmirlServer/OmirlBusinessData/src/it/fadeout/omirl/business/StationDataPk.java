package it.fadeout.omirl.business;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Embeddable;

@Embeddable
public class StationDataPk implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4802701424617038177L;
	
	String station_code;
	Date reference_date;
	
	public StationDataPk(String sStationCode, Date dtRefDate) {
		station_code = sStationCode;
		reference_date = dtRefDate;
	}
	
	@Override
	public int hashCode() {
		int iHashCode = station_code.hashCode() + reference_date.hashCode();
		return iHashCode;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		try {
			StationDataPk oOther = (StationDataPk) obj;
			if (station_code.equals(oOther.getStation_code())) {
				if (reference_date.equals(oOther.getReference_date())) {
					return true;
				}
			}
			
			return false;
			
		}
		catch(Throwable oEx) {
			oEx.printStackTrace();
			return super.equals(obj);
		}
	}
	
	
	public String getStation_code() {
		return station_code;
	}

	public void setStation_code(String station_code) {
		this.station_code = station_code;
	}

	public Date getReference_date() {
		return reference_date;
	}

	public void setReference_date(Date reference_date) {
		this.reference_date = reference_date;
	}

}
