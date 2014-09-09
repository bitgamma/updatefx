package com.briksoftware.updatefx.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlType(propOrder = { "type", "algorithm", "value" })
public class Verification {
	private VerificationType type;
	private String algorithm;	
	private byte[] value;
	
	@XmlAttribute
	public VerificationType getType() {
		return type;
	}

	public void setType(VerificationType type) {
		this.type = type;
	}

	
	@XmlAttribute
	public String getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}
	
	@XmlValue
	public byte[] getValue() {
		return value;
	}

	public void setValue(byte[] value) {
		this.value = value;
	}
}
