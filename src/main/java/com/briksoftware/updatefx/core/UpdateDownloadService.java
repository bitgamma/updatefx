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

import java.io.BufferedInputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import com.briksoftware.updatefx.model.Binary;
import com.briksoftware.updatefx.model.Platform;
import com.briksoftware.updatefx.model.Release;

public class UpdateDownloadService extends Service<Path> {
    private Release release;

    public UpdateDownloadService(Release release) {
        this.release = release;
    }

    @Override
    protected Task<Path> createTask() {
        return new Task<Path>() {
            @Override
            protected Path call() throws Exception {
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
                if (fileName != null && fileName.contains("=")) {
                    fileName = fileName.split("=")[1];
                } else {
                    String url = toDownload.getHref().getPath();
                    int lastSlashIdx = url.lastIndexOf('/');

                    if (lastSlashIdx >= 0) {
                        fileName = url.substring(lastSlashIdx + 1, url.length());
                    } else {
                        fileName = url;
                    }
                }

                Path downloadFile = Paths.get(System.getProperty("java.io.tmpdir"), fileName);

                try (OutputStream fos = Files.newOutputStream(downloadFile, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
                     BufferedInputStream is = new BufferedInputStream(connection.getInputStream())) {
                    byte[] buffer = new byte[512];
                    int n;
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
        if (platform == Platform.independent) {
            return true;
        }

        String currentPlatform = System.getProperty("os.name").toLowerCase();

        if (currentPlatform.startsWith("mac")) {
            return platform == Platform.mac;
        } else if (currentPlatform.startsWith("windows")) {
            if (System.getProperty("os.arch").contains("64")) {
                return platform == Platform.win_x64;
            } else {
                return platform == Platform.win_x86;
            }
        } else {
            throw new IllegalStateException("UpdateFX does not support this platform unless application is platform independent");
        }
    }
}
