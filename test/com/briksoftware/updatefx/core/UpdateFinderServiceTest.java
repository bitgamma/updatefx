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
	private Release release10001SameLicense;
	private Release release10000SameLicense;
	
	@Before
	public void setUp() throws Exception {
		app = new Application();
		releaseNewerLicense = new Release();
		release10002SameLicense = new Release();
		release10001SameLicense = new Release();
		release10000SameLicense = new Release();
		
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
		assertEquals(result.state, Worker.State.SUCCEEDED);
		assertEquals(result.serviceResult, release10002SameLicense);
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
		assertEquals(result.state, Worker.State.SUCCEEDED);
		assertEquals(result.serviceResult, releaseNewerLicense);
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
		assertEquals(result.state, Worker.State.FAILED);
		assertNull(result.serviceResult);
	}
}
