package com.briksoftware.updatefx.core;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLConnection;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import com.briksoftware.updatefx.model.Binary;
import com.briksoftware.updatefx.model.Platform;
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
				Binary toDownload = null;

				for (Binary binary : release.getBinaries()) {
					if (isCurrentPlatform(binary.getPlatform())) {
						toDownload = binary;
						break;
					}
				}

				if (toDownload == null) {
					throw new IllegalArgumentException("This release does not contain binaries for this platform");
				}

				URL fileURL = toDownload.getHref();
				URLConnection connection = fileURL.openConnection();
				connection.connect();

				long len = connection.getContentLengthLong();

				if (len == -1) {
					len = toDownload.getSize();
				}

				updateProgress(0, len);

				String fileName = connection.getHeaderField("Content-Disposition");
				if (fileName != null && fileName.indexOf("=") != -1) {
					fileName = fileName.split("=")[1];
				} else {
					fileName = toDownload.getHref().getPath().substring(1);
				}

				File downloadFile = new File(new File(System.getProperty("java.io.tmpdir")), fileName);
				
				try(FileOutputStream fos = new FileOutputStream(downloadFile); 
						BufferedInputStream is = new BufferedInputStream(connection.getInputStream())) {
					byte[] buffer = new byte[512];
					int n = -1;
					long totalDownload = 0;
					
					while ((n = is.read(buffer)) != -1) {
						fos.write(buffer, 0, n);
						totalDownload += n;
						updateProgress(totalDownload, len);
					}
				}
				
				return downloadFile;
			}
		};
	}

	private boolean isCurrentPlatform(Platform platform) {
		String currentPlatform = System.getProperty("os.name");
		
		if (currentPlatform.startsWith("Mac")) {
			return platform == Platform.mac;
		} else if (currentPlatform.startsWith("Windows")) {
			return platform == Platform.windows;
		} else {
			throw new IllegalStateException("UpdateFX does not support this platform");
		}
	}
}
