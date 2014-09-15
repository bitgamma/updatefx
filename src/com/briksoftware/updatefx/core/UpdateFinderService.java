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

import java.util.HashMap;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import com.briksoftware.updatefx.model.Application;
import com.briksoftware.updatefx.model.Release;

public class UpdateFinderService extends Service<Release> {

	private Application application;
	private int releaseID;
	private int licenseVersion;

	public UpdateFinderService(Application application, int releaseID, int licenseVersion) {
		this.application = application;
		this.releaseID = releaseID;
		this.licenseVersion = licenseVersion;
	}

	@Override
	protected Task<Release> createTask() {
		return new Task<Release>() {

			@Override
			protected Release call() throws Exception {
				HashMap<Integer, Release> releaseMap = new HashMap<>();
				Release newestVersion = null;
				
				for (Release release : application.getReleases()) {
					Release currentNewest = releaseMap.get(release.getLicenseVersion());
					
					if (currentNewest == null || (release.getId() > currentNewest.getId())) {
						releaseMap.put(release.getLicenseVersion(), release);
					}
					
					if (newestVersion == null || (release.getId() > newestVersion.getId())) {
						newestVersion = release;
					}
				}
				
				Release newestForThisLicense = releaseMap.get(licenseVersion);
				
				if ((newestForThisLicense != null) && (newestForThisLicense.getId() > releaseID)) {
					return newestForThisLicense;
				} else if ((newestVersion != null) && (newestVersion.getId() > releaseID)) {
					return newestVersion;
				}
								
				throw new NoUpdateException();
			}
		};
	}
}
