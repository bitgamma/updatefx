package com.briksoftware.updatefx.model;

import java.net.URL;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "href", "size", "platform", "verification" })
public class Binary {
	private URL href;
	private long size;	
	private Platform platform;	
	private Verification verification;

	@XmlAttribute(required = true)
	public URL getHref() {
		return href;
	}

	public void setHref(URL href) {
		this.href = href;
	}

	@XmlAttribute
	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	@XmlAttribute
	public Platform getPlatform() {
		return platform;
	}

	public void setPlatform(Platform platform) {
		this.platform = platform;
	}

	@XmlElement
	public Verification getVerification() {
		return verification;
	}

	public void setVerification(Verification verification) {
		this.verification = verification;
	}
}
