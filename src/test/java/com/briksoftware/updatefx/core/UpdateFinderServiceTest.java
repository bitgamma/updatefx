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

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import javafx.concurrent.Worker;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.briksoftware.updatefx.model.Application;
import com.briksoftware.updatefx.model.Release;

import de.saxsys.javafx.test.JfxRunner;

@RunWith(JfxRunner.class)
public class UpdateFinderServiceTest {
	private Application app;
	private Release releaseNewerLicense;
	private Release release10002SameLicense;

	@Before
	public void setUp() throws Exception {
		app = new Application();
		releaseNewerLicense = new Release();
		release10002SameLicense = new Release();
		Release release10001SameLicense = new Release();
		Release release10000SameLicense = new Release();
		
		releaseNewerLicense.setApplication(app);
		releaseNewerLicense.setId(20000);
		releaseNewerLicense.setLicenseVersion(2);

		release10002SameLicense.setApplication(app);
		release10002SameLicense.setId(10002);
		release10002SameLicense.setLicenseVersion(1);
		
		release10001SameLicense.setApplication(app);
		release10001SameLicense.setId(10001);
		release10001SameLicense.setLicenseVersion(1);
		
		release10000SameLicense.setApplication(app);
		release10000SameLicense.setId(10000);
		release10000SameLicense.setLicenseVersion(1);
		
		app.getReleases().add(releaseNewerLicense);
		app.getReleases().add(release10002SameLicense);
		app.getReleases().add(release10001SameLicense);
		app.getReleases().add(release10000SameLicense);
	}

	@Test
	public void testServiceUpdateWithSameLicense() throws Exception {
		CompletableFuture<ServiceTestResults<Release>> serviceStateDoneFuture = new CompletableFuture<>();
		UpdateFinderService service = new UpdateFinderService(app, 10001, 1);

		service.stateProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue == Worker.State.FAILED || newValue == Worker.State.SUCCEEDED) {
				serviceStateDoneFuture.complete(new ServiceTestResults<>(service.getState(), service.getValue(), service.getException()));
			}
		});

		service.start();

		ServiceTestResults<Release> result = serviceStateDoneFuture.get(200, TimeUnit.MILLISECONDS);

		assertNull(result.exception);
		assertEquals(Worker.State.SUCCEEDED, result.state);
		assertEquals(release10002SameLicense, result.serviceResult);
	}
	
	@Test
	public void testServiceUpdateWithNewLicense() throws Exception {
		CompletableFuture<ServiceTestResults<Release>> serviceStateDoneFuture = new CompletableFuture<>();
		UpdateFinderService service = new UpdateFinderService(app, 10002, 1);

		service.stateProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue == Worker.State.FAILED || newValue == Worker.State.SUCCEEDED) {
				serviceStateDoneFuture.complete(new ServiceTestResults<>(service.getState(), service.getValue(), service.getException()));
			}
		});

		service.start();

		ServiceTestResults<Release> result = serviceStateDoneFuture.get(200, TimeUnit.MILLISECONDS);

		assertNull(result.exception);
		assertEquals(Worker.State.SUCCEEDED, result.state);
		assertEquals(releaseNewerLicense, result.serviceResult);
	}
	
	@Test
	public void testServiceUpdateNoUpdate() throws Exception {
		CompletableFuture<ServiceTestResults<Release>> serviceStateDoneFuture = new CompletableFuture<>();
		UpdateFinderService service = new UpdateFinderService(app, 20000, 2);

		service.stateProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue == Worker.State.FAILED || newValue == Worker.State.SUCCEEDED) {
				serviceStateDoneFuture.complete(new ServiceTestResults<>(service.getState(), service.getValue(), service.getException()));
			}
		});

		service.start();

		ServiceTestResults<Release> result = serviceStateDoneFuture.get(200, TimeUnit.MILLISECONDS);

		assertThat(result.exception, instanceOf(NoUpdateException.class));
		assertEquals(Worker.State.FAILED, result.state);
		assertNull(result.serviceResult);
	}
}
