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

import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import javafx.concurrent.Worker;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.briksoftware.updatefx.model.Application;
import com.briksoftware.updatefx.model.Release;

import de.saxsys.javafx.test.JfxRunner;

@RunWith(JfxRunner.class)
public class XMLRetrieverServiceTest {
	
	@Test
	public void testService() throws Throwable {
		CompletableFuture<ServiceTestResults<Application>> serviceStateDoneFuture = new CompletableFuture<>();
		
		XMLRetrieverService service = new XMLRetrieverService(getClass().getResource("updatefx.xml"));
		service.stateProperty().addListener((observable, oldValue, newValue) -> {
				if (newValue == Worker.State.FAILED || newValue == Worker.State.SUCCEEDED) {
					serviceStateDoneFuture.complete(new ServiceTestResults<>(service.getState(), service.getValue(), service.getException()));
				}
		});
		
		service.start();
		
		ServiceTestResults<Application> result = serviceStateDoneFuture.get(1000, TimeUnit.MILLISECONDS); 

		assertNull(result.exception);
		assertEquals(Worker.State.SUCCEEDED, result.state);
		assertNotNull(result.serviceResult);
		assertEquals("Example App", result.serviceResult.getName());
		assertEquals(2, result.serviceResult.getReleases().size());
		
		for (Release release : result.serviceResult.getReleases()) {
			assertEquals(result.serviceResult, release.getApplication());
		}
	}
	
	@Test
	public void testServiceMissingXML() throws Throwable {
		CompletableFuture<ServiceTestResults<Application>> serviceStateDoneFuture = new CompletableFuture<>();
		
		XMLRetrieverService service = new XMLRetrieverService(new URL("file:///i_really_hope_this_file_is_missing.xml"));
		service.stateProperty().addListener((observable, oldValue, newValue) -> {
				if (newValue == Worker.State.FAILED || newValue == Worker.State.SUCCEEDED) {
					serviceStateDoneFuture.complete(new ServiceTestResults<>(service.getState(), service.getValue(), service.getException()));
				}
		});
		
		service.start();
		
		ServiceTestResults<Application> result = serviceStateDoneFuture.get(1000, TimeUnit.MILLISECONDS); 

		assertNotNull(result.exception);
		assertEquals(Worker.State.FAILED, result.state);
		assertNull(result.serviceResult);
	}
}
