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
					
					if (newestVersion == null || release.getId() > newestVersion.getId()) {
						newestVersion = release;
					}
				}
				
				Release newestForThisLicense = releaseMap.get(licenseVersion);
				
				if (newestForThisLicense != null && newestForThisLicense.getId() > releaseID) {
					return newestForThisLicense;
				} else if (newestVersion != null && newestVersion.getId() > releaseID) {
					return newestVersion;
				}
								
				throw new NoUpdateException();
			}
		};
	}

}
