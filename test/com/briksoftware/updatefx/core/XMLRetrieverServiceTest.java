package com.briksoftware.updatefx.core;

import static org.junit.Assert.*;

import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import javafx.concurrent.Worker;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.briksoftware.updatefx.model.Application;

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
		
		ServiceTestResults<Application> result = serviceStateDoneFuture.get(100, TimeUnit.MILLISECONDS); 

		assertNull(result.exception);
		assertEquals(result.state, Worker.State.SUCCEEDED);
		assertNotNull(result.serviceResult);
		assertEquals(result.serviceResult.getName(), "Example App");
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
		
		ServiceTestResults<Application> result = serviceStateDoneFuture.get(100, TimeUnit.MILLISECONDS); 

		assertNotNull(result.exception);
		assertEquals(result.state, Worker.State.FAILED);
		assertNull(result.serviceResult);
	}
}
