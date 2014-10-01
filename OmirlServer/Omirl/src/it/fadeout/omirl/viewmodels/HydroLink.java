package it.fadeout.omirl.viewmodels;

public class HydroLink {
	// Unique link id
	String linkCode;	
	// Link to the icon image
	String link;
	// Flag to detect if exists a third level
	boolean hasThirdLevel;
	// Flag to know if has childs or not
	boolean hasChilds;
	// Tooltip
	String description;
	// Link to the legend
	String legendLink;
	// Flag to know if it is selected or not
	boolean selected;
	// Parent Code
	String parentLinkCode;
	// Parent Code
	String parentDescription;
	
	public String getLinkCode() {
		return linkCode;
	}
	public void setLinkCode(String linkCode) {
		this.linkCode = linkCode;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public boolean isHasThirdLevel() {
		return hasThirdLevel;
	}
	public void setHasThirdLevel(boolean hasThirdLevel) {
		this.hasThirdLevel = hasThirdLevel;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getLegendLink() {
		return legendLink;
	}
	public void setLegendLink(String legendLink) {
		this.legendLink = legendLink;
	}
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	public String getParentLinkCode() {
		return parentLinkCode;
	}
	public void setParentLinkCode(String parentLinkCode) {
		this.parentLinkCode = parentLinkCode;
	}
	public String getParentDescription() {
		return parentDescription;
	}
	public void setParentDescription(String parentDescription) {
		this.parentDescription = parentDescription;
	}
	public boolean isHasChilds() {
		return hasChilds;
	}
	public void setHasChilds(boolean hasChilds) {
		this.hasChilds = hasChilds;
	}
}
