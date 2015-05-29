package it.fadeout.omirl.business;

import java.util.ArrayList;

public class MaxTableInfo {
	String tableName;
	
	String threshold1Style;
	String threshold2Style;
	
	ArrayList<String> rows = new ArrayList<>();
	
	ArrayList<String> rowFilters = new ArrayList<>();
	
	ArrayList<String> columns = new ArrayList<>();
	
	ArrayList<String> methodCodes = new ArrayList<>();
	
	ArrayList<Double> threshold1 = new ArrayList<>();
	
	ArrayList<Double> threshold2 = new ArrayList<>();

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public ArrayList<String> getRows() {
		return rows;
	}

	public void setRows(ArrayList<String> rows) {
		this.rows = rows;
	}

	public ArrayList<String> getColumns() {
		return columns;
	}

	public void setColumns(ArrayList<String> columns) {
		this.columns = columns;
	}

	public ArrayList<Double> getThreshold1() {
		return threshold1;
	}

	public void setThreshold1(ArrayList<Double> threshold1) {
		this.threshold1 = threshold1;
	}

	public ArrayList<Double> getThreshold2() {
		return threshold2;
	}

	public void setThreshold2(ArrayList<Double> threshold2) {
		this.threshold2 = threshold2;
	}

	public ArrayList<String> getRowFilters() {
		return rowFilters;
	}

	public void setRowFilters(ArrayList<String> rowFilters) {
		this.rowFilters = rowFilters;
	}

	public ArrayList<String> getMethodCodes() {
		return methodCodes;
	}

	public void setMethodCodes(ArrayList<String> methodCodes) {
		this.methodCodes = methodCodes;
	}

	public String getThreshold1Style() {
		return threshold1Style;
	}

	public void setThreshold1Style(String threshold1Style) {
		this.threshold1Style = threshold1Style;
	}

	public String getThreshold2Style() {
		return threshold2Style;
	}

	public void setThreshold2Style(String threshold2Style) {
		this.threshold2Style = threshold2Style;
	}
}
