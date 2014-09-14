package com.briksoftware.updatefx.core;

import java.io.File;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import com.briksoftware.updatefx.model.Release;

public class UpdateDownloadService extends Service<File> {
	private Release release;
	
	public UpdateDownloadService(Release release) {
		this.release = release;
	}

	@Override
	protected Task<File> createTask() {
		return new Task<File>() {
			@Override
			protected File call() throws Exception {
				// TODO Auto-generated method stub
				return null;
			}
			
		};
	}
}
