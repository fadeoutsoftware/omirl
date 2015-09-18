package it.fadeout.omirl.viewmodels;

import java.util.ArrayList;
import java.util.List;

public class SectionBasinsViewModel {

	private String basinName;
	
	private Integer orderNumber;
	
	private List<SectionBasinsCodesViewModel> sectionBasinsCodes = new ArrayList<SectionBasinsCodesViewModel>();
	
	public String getBasinName() {
		return basinName;
	}
	public void setBasinName(String basinName) {
		this.basinName = basinName;
	}
	
	public Integer getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}
	public List<SectionBasinsCodesViewModel> getSectionBasinsCodes() {
		return sectionBasinsCodes;
	}
	public void setSectionBasinsCodes(List<SectionBasinsCodesViewModel> sectionBasinsCodes) {
		this.sectionBasinsCodes = sectionBasinsCodes;
	}
}
