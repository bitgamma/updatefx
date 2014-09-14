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
import javafx.stage.Modality;
import javafx.stage.Stage;

import com.briksoftware.updatefx.core.UpdateDownloadService;
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
	
	public static void showUpdateDialog(Release release, int currentReleaseID, String currentVersion, int currentLicenseVersion) {
		try {
			ResourceBundle i18nBundle = ResourceBundle.getBundle("com.briksoftware.updatefx.gui.UpdateDialog");
			
			FXMLLoader loader = new FXMLLoader(UpdateDialogController.class.getResource("UpdateDialog.fxml"), i18nBundle);
			loader.setBuilderFactory(new JavaFXBuilderFactory());
			Parent page = (Parent) loader.load();
			UpdateDialogController controller = (UpdateDialogController) loader.getController();
			controller.release = release;
			controller.currentReleaseID = currentReleaseID;
			controller.currentVersion = currentVersion;
			controller.currentLicenseVersion = currentLicenseVersion;
			controller.initialize();
			
			Scene scene = new Scene(page);
			final Stage modalStage = new Stage();
			modalStage.setScene(scene);
			modalStage.initModality(Modality.APPLICATION_MODAL);	
			modalStage.show();
			modalStage.toFront();
			
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
		UpdateDownloadService service = new UpdateDownloadService(release);
		
		service.start();
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
