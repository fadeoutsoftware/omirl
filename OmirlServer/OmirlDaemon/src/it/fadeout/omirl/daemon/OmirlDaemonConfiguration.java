package it.fadeout.omirl.daemon;

import it.fadeout.omirl.business.AnagTableInfo;
import it.fadeout.omirl.business.ChartInfo;
import it.fadeout.omirl.business.DynamicLayerInfo;

import java.util.ArrayList;

public class OmirlDaemonConfiguration {
	String fileRepositoryPath;
	
	int minutesPolling = 1;
	
	int chartTimeRangeDays = 15;

	ArrayList<ChartInfo> chartsInfo = new ArrayList<>();
	
	ArrayList<AnagTableInfo> anagTablesInfo = new ArrayList<>();
	
	ArrayList<DynamicLayerInfo> dynamicLayersInfo = new ArrayList<>();


	public String getFileRepositoryPath() {
		return fileRepositoryPath;
	}

	public void setFileRepositoryPath(String fileRepositoryPath) {
		this.fileRepositoryPath = fileRepositoryPath;
	}

	public int getMinutesPolling() {
		return minutesPolling;
	}

	public void setMinutesPolling(int minutesPolling) {
		this.minutesPolling = minutesPolling;
	}


	public int getChartTimeRangeDays() {
		return chartTimeRangeDays;
	}

	public void setChartTimeRangeDays(int chartTimeRangeDays) {
		this.chartTimeRangeDays = chartTimeRangeDays;
	}

	public ArrayList<ChartInfo> getChartsInfo() {
		return chartsInfo;
	}

	public void setChartsInfo(ArrayList<ChartInfo> chartsInfo) {
		this.chartsInfo = chartsInfo;
	}

	public ArrayList<AnagTableInfo> getAnagTablesInfo() {
		return anagTablesInfo;
	}

	public void setAnagTablesInfo(ArrayList<AnagTableInfo> anagTablesInfo) {
		this.anagTablesInfo = anagTablesInfo;
	}

	public ArrayList<DynamicLayerInfo> getDynamicLayersInfo() {
		return dynamicLayersInfo;
	}

	public void setDynamicLayersInfo(ArrayList<DynamicLayerInfo> dynamicLayersInfo) {
		this.dynamicLayersInfo = dynamicLayersInfo;
	}	
}
