package com.briksoftware.updatefx.core;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

public class UpdateFXTest {
	private Properties prop;
	
	@Before
	public void setUp() {
		prop = new Properties();
		prop.setProperty("app.version", "1.0.0");
		prop.setProperty("app.release", "201409121916");
		prop.setProperty("app.updatefx.url", this.getClass().getResource("updatefx.xml").toString());
	}

	@Test
	public void testUpdateFXWithProperties() throws IOException {
		UpdateFX update = new UpdateFX(prop);
		assertEquals(update.getVersion(), "1.0.0");
		assertEquals(update.getReleaseID(), "201409121916");
		assertEquals(update.getUpdateXML(), this.getClass().getResource("updatefx.xml"));
	}
	
	@Test
	public void testUpdateFXWithClass() throws IOException {
		UpdateFX update = new UpdateFX(this.getClass());
		assertEquals(update.getVersion(), "1.0.0");
		assertEquals(update.getReleaseID(), "201409121916");
		assertEquals(update.getUpdateXML().toString(), "http://example.com/updatefx.xml");
	}

}
