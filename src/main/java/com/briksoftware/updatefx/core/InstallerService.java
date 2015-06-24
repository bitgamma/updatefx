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

import java.nio.file.Path;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import com.briksoftware.updatefx.util.PIDUtil;
import com.briksoftware.updatefx.util.ScriptUtil;

public class InstallerService extends Service<Void> {
	private Path installer;
	public InstallerService(Path installer) {
		this.installer = installer;
	}

	@Override
	protected Task<Void> createTask() {
		return new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				String[] nameComponents = installer.getFileName().toString().split("\\.");
				
				if (nameComponents.length < 2) {
					throw new IllegalArgumentException("Files without extensions are not supported yet");
				}
				
				String extension = nameComponents[nameComponents.length - 1].toLowerCase();
				
				switch(extension) {
				case "dmg":
					handleDMGInstallation();
					break;
				case "exe":
					handleEXEInstallation();
					break;
				case "msi":
					handleMSIInstallation();
					break;
				default:
					throw new IllegalArgumentException(String.format("installers with extension %s are not supported", extension));
				}
				
				Platform.exit();
				return null;
			}
		};
	}

	private void handleDMGInstallation() throws Exception {
		Path tmpScript = ScriptUtil.copyScript("installdmg.sh");
		new ProcessBuilder("/bin/sh", tmpScript.toAbsolutePath().toString(), installer.toAbsolutePath().toString(), String.format("%d", PIDUtil.getPID())).start();
	}
	
	private void handleEXEInstallation() throws Exception {
		new ProcessBuilder(installer.toAbsolutePath().toString(), "/SILENT", "/SP-", "/SUPPRESSMSGBOXES").start();
	}
	
	private void handleMSIInstallation() throws Exception {
		// TODO Auto-generated method stub
		
	}
}
