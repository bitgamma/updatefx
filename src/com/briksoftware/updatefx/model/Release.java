package com.briksoftware.updatefx.model;

import java.util.ArrayList;
import java.util.Date;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


@XmlType(propOrder = { "id", "version", "licenseVersion", "releaseDate", "binaries" })
public class Release {
	private String id;	
	private String version;	
	private String licenseVersion;	
	private Date releaseDate;
	private ArrayList<Binary> binaries;

	@XmlAttribute(required = true)
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@XmlAttribute(required = true)
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@XmlAttribute
	public String getLicenseVersion() {
		return licenseVersion;
	}

	public void setLicenseVersion(String licenseVersion) {
		this.licenseVersion = licenseVersion;
	}

	@XmlAttribute
	public Date getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(Date releaseDate) {
		this.releaseDate = releaseDate;
	}

	@XmlElement(name = "binary")
	public ArrayList<Binary> getBinaries() {
		return binaries;
	}

	public void setBinaries(ArrayList<Binary> binaries) {
		this.binaries = binaries;
	}
}
