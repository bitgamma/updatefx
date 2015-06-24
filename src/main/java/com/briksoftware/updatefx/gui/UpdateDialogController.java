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
package com.briksoftware.updatefx.gui;

import java.net.URL;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import com.briksoftware.updatefx.model.Release;

public class UpdateDialogController {
	@FXML
	private Label infoLabel;
	@FXML
	private WebView changeView;
	@FXML
	private ResourceBundle resources;
	
	private Release release;
	private int currentReleaseID;
	private String currentVersion;
	private int currentLicenseVersion;
	private URL css;
	
	public static void showUpdateDialog(Release release, int currentReleaseID, String currentVersion, int currentLicenseVersion, URL css) {
		try {
			ResourceBundle i18nBundle = ResourceBundle.getBundle("com.briksoftware.updatefx.gui.i18n.UpdateDialog");
			
			FXMLLoader loader = new FXMLLoader(UpdateDialogController.class.getResource("UpdateDialog.fxml"), i18nBundle);
			loader.setBuilderFactory(new JavaFXBuilderFactory());
			Parent page = loader.load();
			UpdateDialogController controller = loader.getController();
			controller.release = release;
			controller.currentReleaseID = currentReleaseID;
			controller.currentVersion = currentVersion;
			controller.currentLicenseVersion = currentLicenseVersion;
			controller.css = css;
			controller.initialize();
			
			Scene scene = new Scene(page);
			if (css != null) {
				scene.getStylesheets().add(css.toExternalForm());
			}
			
			final Stage stage = new Stage();
			stage.setScene(scene);
			stage.show();
			stage.toFront();
			
		} catch (Throwable ex) {
			ex.printStackTrace();
		}		
	}

	private void initialize() {
		URL changelog = release.getApplication().getChangelog();
		
		if (changelog != null) {
			WebEngine engine = changeView.getEngine();
			String finalURL = String.format("%s?from=%d&to=%d", changelog, currentReleaseID, release.getId());
			engine.load(finalURL);
		} else {
			changeView.setVisible(false);
			changeView.setManaged(false);
		}
		
    Object[] messageArguments = { release.getApplication().getName(), currentVersion, release.getVersion() };
    MessageFormat formatter = new MessageFormat("");
    formatter.setLocale(resources.getLocale());
    
		if (release.getLicenseVersion() != currentLicenseVersion) {
			formatter.applyPattern(resources.getString("infotext.paidupgrade"));
		} else {
			formatter.applyPattern(resources.getString("infotext.freeupgrade"));			
		}
		
		infoLabel.setText(formatter.format(messageArguments));
		infoLabel.autosize();
	}
	
	private void close() {
		((Stage) infoLabel.getScene().getWindow()).close();		
	}
	
	@FXML
	public void performUpdate(ActionEvent event) {
		UpdateController.performUpdate(release, css);
		close();
	}

	@FXML
	public void cancel(ActionEvent event) {
		close();
	}
	
	@FXML
	public void ignoreVersion(ActionEvent event) {
		//TODO: implement this feature
		close();
	}
}
