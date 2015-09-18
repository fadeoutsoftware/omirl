package it.fadeout.omirl.business;

import it.fadeout.omirl.viewmodels.SectionBasinsViewModel;
import it.fadeout.omirl.viewmodels.SectionViewModel;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "sectionbasins")
public class SectionBasins {

	@Id
	@Column(name="id")
	private
	Integer id;
	
	@Column(name="name")
	private
	String name;
	
	@Column(name="ordernumber")
	private
	Integer ordernumber;
	
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "sectionbasins")
	private Set<SectionBasinsCodes> SectionBasinsCodes = new HashSet<SectionBasinsCodes>(0);

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getOrdernumber() {
		return ordernumber;
	}

	public void setOrdernumber(Integer ordernumber) {
		this.ordernumber = ordernumber;
	}

	public Set<SectionBasinsCodes> getSectionBasinsCodes() {
		return SectionBasinsCodes;
	}

	public void setSectionBasinsCodes(Set<SectionBasinsCodes> sectionBasinsCodes) {
		SectionBasinsCodes = sectionBasinsCodes;
	}
	
	
}
