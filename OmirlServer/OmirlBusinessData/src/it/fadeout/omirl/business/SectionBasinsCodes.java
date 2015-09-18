package it.fadeout.omirl.business;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "sectionbasinscodes")
public class SectionBasinsCodes {

	@Id
	@Column(name="id")
	private
	Integer id;
	
	@Column(name="sectioncode")
	private
	String sectioncode;
	
	@Column(name="ordernumber")
	private
	Integer ordernumber;
	
	@Column(name="sectionbasinid", insertable= false, updatable= false)
	private
	Integer sectionbasinid;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "sectionbasinid", nullable = false)
	private SectionBasins sectionbasins = new SectionBasins();
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getSectioncode() {
		return sectioncode;
	}

	public void setSectioncode(String sectioncode) {
		this.sectioncode = sectioncode;
	}

	public Integer getOrdernumber() {
		return ordernumber;
	}

	public void setOrdernumber(Integer ordernumber) {
		this.ordernumber = ordernumber;
	}

	public SectionBasins getSectionBasins() {
		return sectionbasins;
	}

	public void setSectionBasins(SectionBasins sectionBasins) {
		sectionbasins = sectionBasins;
	}
}
