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

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;

import com.briksoftware.updatefx.core.InstallerService;
import com.briksoftware.updatefx.core.UpdateDownloadService;
import com.briksoftware.updatefx.model.Release;

public class UpdateController {
	@FXML
	private Label stepLabel;
	@FXML
	private ProgressBar progressBar;
	@FXML
	private Label progressLabel;
	@FXML
	private Button actionButton;
	@FXML
	private ResourceBundle resources;

	private Release release;
	private UpdateDownloadService service;

	public static void performUpdate(Release release, URL css) {
		try {
			ResourceBundle i18nBundle = ResourceBundle.getBundle("com.briksoftware.updatefx.gui.i18n.UpdateProgressDialog");

			FXMLLoader loader = new FXMLLoader(UpdateController.class.getResource("UpdateProgressDialog.fxml"), i18nBundle);
			loader.setBuilderFactory(new JavaFXBuilderFactory());
			Parent page = loader.load();
			UpdateController controller = loader.getController();
			controller.release = release;
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
		stepLabel.setText(resources.getString("label.downloading"));
		service = new UpdateDownloadService(release);

		service.workDoneProperty().addListener((observable, oldValue, newValue) -> {
			progressBar.setProgress(service.getWorkDone() / service.getTotalWork());
			updateProgressLabel();
		});
		
		service.setOnSucceeded((event) -> Platform.runLater(() -> {
            actionButton.setDefaultButton(true);
            actionButton.setOnAction(this::install);
            actionButton.setText(resources.getString("button.install"));
            actionButton.autosize();
            stepLabel.setText(resources.getString("label.downloaded"));
        }));
		
		service.setOnFailed((event) -> Platform.runLater(() -> {
            actionButton.setDefaultButton(true);
            stepLabel.setText(resources.getString("label.downloadfailed"));
        }));
		
		service.start();
	}
	
	private String byte2Str(double bytes) {
		double unit = 1024.0;
		
		if (bytes < unit) {
			return bytes + " B";
		}
		
		int exp = (int) (Math.log(bytes) / Math.log(unit));
		String pre = "kMGTPE".charAt(exp - 1) + "";
		return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}
	
	private void updateProgressLabel() {
		MessageFormat mf = new MessageFormat(resources.getString("label.progress"), resources.getLocale());
		Object[] params = { byte2Str(service.getWorkDone()), byte2Str(service.getTotalWork())};
		
		progressLabel.setText(mf.format(params));
	}
	
	private void close() {
		((Stage) progressLabel.getScene().getWindow()).close();		
	}

	@FXML
	public void cancel(ActionEvent event) {
		service.cancel();
		close();
	}

	@FXML
	public void install(ActionEvent event) {
		actionButton.setDisable(true);
		progressBar.setProgress(-1.0);
		progressLabel.setText("");
		stepLabel.setText(resources.getString("label.installing"));
		
		InstallerService installService = new InstallerService(service.getValue());
		
		installService.setOnFailed((evt) -> Platform.runLater(() -> {
            actionButton.setDisable(false);
            actionButton.setOnAction((clickEvent) -> close());
            actionButton.setText(resources.getString("button.cancel"));
            actionButton.autosize();
            progressBar.setProgress(1.0);
            stepLabel.setText(resources.getString("label.installfailed"));
        }));
		
		installService.start();
	}
}
