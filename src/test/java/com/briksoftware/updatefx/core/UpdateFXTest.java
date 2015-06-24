/*
 * Copyright (C) 2014 Michele Balistreri
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
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
		prop.setProperty("app.release", "10000");
		prop.setProperty("app.licenseVersion", "1");
		prop.setProperty("app.updatefx.url", this.getClass().getResource("updatefx.xml").toString());
	}

	@Test
	public void testUpdateFXWithProperties() throws IOException {
		UpdateFX update = new UpdateFX(prop, null);
		assertEquals("1.0.0", update.getVersion());
		assertEquals(10000, update.getReleaseID());
		assertEquals(1, update.getLicenseVersion());
		assertEquals(this.getClass().getResource("updatefx.xml"), update.getUpdateXML());
	}
	
	@Test
	public void testUpdateFXWithClass() throws IOException {
		UpdateFX update = new UpdateFX(this.getClass());
		assertEquals("1.0.0", update.getVersion());
		assertEquals(10000, update.getReleaseID());
		assertEquals(1, update.getLicenseVersion());
		assertEquals("http://example.com/updatefx.xml", update.getUpdateXML().toString());
	}

}
