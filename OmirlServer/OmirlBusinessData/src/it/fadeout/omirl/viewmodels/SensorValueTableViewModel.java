package it.fadeout.omirl.viewmodels;

import java.util.ArrayList;

import javax.persistence.Transient;
import javax.swing.text.TabExpander;

public class SensorValueTableViewModel {
	String sensorTye;
	ArrayList<SensorValueRowViewModel> tableRows = new ArrayList<>();
	
	@Transient
	private
	String updateDateTime;
	
	public String getSensorTye() {
		return sensorTye;
	}
	public void setSensorTye(String sensorTye) {
		this.sensorTye = sensorTye;
	}
	public ArrayList<SensorValueRowViewModel> getTableRows() {
		return tableRows;
	}
	public void setTableRows(ArrayList<SensorValueRowViewModel> tableRows) {
		this.tableRows = tableRows;
	}
	
	public SensorValueRowViewModel getTableRowByCode(String sCode)
	{
		if (tableRows==null) return null;
		
		for (SensorValueRowViewModel oRow : tableRows) {
			if (oRow.getCode().equals(sCode))
			{
				return oRow;
			}
		}
		
		return null;
	}
	public String getUpdateDateTime() {
		return updateDateTime;
	}
	public void setUpdateDateTime(String updateDateTime) {
		this.updateDateTime = updateDateTime;
	}
}
