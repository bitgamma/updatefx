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

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
		
		Binary win_x86 = new Binary();
		win_x86.setPlatform(Platform.win_x86);
		win_x86.setHref(this.getClass().getResource("updatefx.xml"));
		
		Binary win_x64 = new Binary();
		win_x64.setPlatform(Platform.win_x64);
		win_x64.setHref(this.getClass().getResource("updatefx.xml"));
		
		releaseNoVerification.getBinaries().add(mac);
		releaseNoVerification.getBinaries().add(win_x86);
		releaseNoVerification.getBinaries().add(win_x64);
		
		emptyRelease = new Release();
	}

	@Test
	public void testServiceBinaryFoundNoVerification() throws Exception {
		CompletableFuture<ServiceTestResults<Path>> serviceStateDoneFuture = new CompletableFuture<>();
		UpdateDownloadService service = new UpdateDownloadService(releaseNoVerification);

		service.stateProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue == Worker.State.FAILED || newValue == Worker.State.SUCCEEDED) {
				serviceStateDoneFuture.complete(new ServiceTestResults<>(service.getState(), service.getValue(), service.getException()));
			}
		});
		
		service.start();

		ServiceTestResults<Path> result = serviceStateDoneFuture.get(1000, TimeUnit.MILLISECONDS);

		assertNull(result.exception);
		assertEquals(Worker.State.SUCCEEDED, result.state);
		assertEquals(Paths.get(System.getProperty("java.io.tmpdir"), "updatefx.xml"), result.serviceResult);
		Files.delete(result.serviceResult);
	}
	
	@Test
	public void testServiceBinaryNotFound() throws Exception {
		CompletableFuture<ServiceTestResults<Path>> serviceStateDoneFuture = new CompletableFuture<>();
		UpdateDownloadService service = new UpdateDownloadService(emptyRelease);

		service.stateProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue == Worker.State.FAILED || newValue == Worker.State.SUCCEEDED) {
				serviceStateDoneFuture.complete(new ServiceTestResults<>(service.getState(), service.getValue(), service.getException()));
			}
		});
		
		service.start();

		ServiceTestResults<Path> result = serviceStateDoneFuture.get(1000, TimeUnit.MILLISECONDS);

		assertThat(result.exception, instanceOf(IllegalArgumentException.class));
		assertEquals(Worker.State.FAILED, result.state);
		assertNull(result.serviceResult);
	}
}
