package com.briksoftware.updatefx.core;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import javafx.concurrent.Worker;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.briksoftware.updatefx.model.Binary;
import com.briksoftware.updatefx.model.Platform;
import com.briksoftware.updatefx.model.Release;

import de.saxsys.javafx.test.JfxRunner;

@RunWith(JfxRunner.class)
public class UpdateDownloadServiceTest {

	private Release releaseNoVerification;
	private Release emptyRelease;
	
	@Before
	public void setUp() throws Exception {
		releaseNoVerification = new Release();
		
		Binary mac = new Binary();
		mac.setPlatform(Platform.mac);
		mac.setHref(this.getClass().getResource("updatefx.xml"));
		
		Binary windows = new Binary();
		windows.setPlatform(Platform.windows);
		windows.setHref(this.getClass().getResource("updatefx.xml"));
		
		releaseNoVerification.getBinaries().add(mac);
		releaseNoVerification.getBinaries().add(windows);
		
		emptyRelease = new Release();
	}

	@Test
	public void testServiceBinaryFoundNoVerification() throws Exception {
		CompletableFuture<ServiceTestResults<File>> serviceStateDoneFuture = new CompletableFuture<>();
		UpdateDownloadService service = new UpdateDownloadService(releaseNoVerification);

		service.stateProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue == Worker.State.FAILED || newValue == Worker.State.SUCCEEDED) {
				serviceStateDoneFuture.complete(new ServiceTestResults<>(service.getState(), service.getValue(), service.getException()));
			}
		});
		
		service.start();

		ServiceTestResults<File> result = serviceStateDoneFuture.get(200, TimeUnit.MILLISECONDS);

		assertNull(result.exception);
		assertEquals(result.state, Worker.State.SUCCEEDED);
		assertEquals(result.serviceResult, new File(new File(System.getProperty("java.io.tmpdir")), "updatefx.xml"));
		result.serviceResult.delete();
	}
	
	@Test
	public void testServiceBinaryNotFound() throws Exception {
		CompletableFuture<ServiceTestResults<File>> serviceStateDoneFuture = new CompletableFuture<>();
		UpdateDownloadService service = new UpdateDownloadService(emptyRelease);

		service.stateProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue == Worker.State.FAILED || newValue == Worker.State.SUCCEEDED) {
				serviceStateDoneFuture.complete(new ServiceTestResults<>(service.getState(), service.getValue(), service.getException()));
			}
		});
		
		service.start();

		ServiceTestResults<File> result = serviceStateDoneFuture.get(200, TimeUnit.MILLISECONDS);

		assertThat(result.exception, instanceOf(IllegalArgumentException.class));
		assertEquals(result.state, Worker.State.FAILED);
		assertNull(result.serviceResult);
	}
}
