package it.fadeout.omirl.viewmodels;

import javax.persistence.Transient;

public class MapInfoViewModel {
	String code;
	String layerId;
	@Transient
	private
	String updateDateTime;
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getLayerId() {
		return layerId;
	}
	public void setLayerId(String layerId) {
		this.layerId = layerId;
	}
	public String getUpdateDateTime() {
		return updateDateTime;
	}
	public void setUpdateDateTime(String updateDateTime) {
		this.updateDateTime = updateDateTime;
	}

}
