package it.fadeout.omirl.business.config;

import java.util.ArrayList;

public class LegendConfig {
	String type;
	ArrayList<LegendStepConfig> values;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public ArrayList<LegendStepConfig> getValues() {
		return values;
	}
	public void setValues(ArrayList<LegendStepConfig> values) {
		this.values = values;
	}
}
