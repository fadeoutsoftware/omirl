package it.fadeout.omirl.viewmodels;

public class PeriodViewModel {
	
	private String timestampStart ;
	private String timestampEnd ;
	private Integer idPeriod;
	
	public String getTimestampStart() {
		return timestampStart;
	}
	public void setTimestampStart(String timestampStart) {
		this.timestampStart = timestampStart;
	}
	public String getTimestampEnd() {
		return timestampEnd;
	}
	public void setTimestampEnd(String timestampEnd) {
		this.timestampEnd = timestampEnd;
	}
	
	public Integer getIdPeriod() {
		return idPeriod;
	}
	public void setIdPeriod(Integer idPeriod) {
		this.idPeriod = idPeriod;
	}
	
}
