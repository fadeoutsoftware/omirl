package it.fadeout.omirl.viewmodels;

import java.util.ArrayList;

public class SensorListTableViewModel {
	String sensorTye;
	ArrayList<SensorListTableRowViewModel> tableRows = new ArrayList<>();
	
	public String getSensorTye() {
		return sensorTye;
	}
	public void setSensorTye(String sensorTye) {
		this.sensorTye = sensorTye;
	}
	public ArrayList<SensorListTableRowViewModel> getTableRows() {
		return tableRows;
	}
	public void setTableRows(ArrayList<SensorListTableRowViewModel> tableRows) {
		this.tableRows = tableRows;
	}
}
