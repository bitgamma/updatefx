package com.briksoftware.updatefx.gui;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.web.WebView;

import com.briksoftware.updatefx.model.Release;

public class UpdateDialogController implements Initializable {
	@FXML
	private Label infoLabel;
	@FXML
	private WebView changeView;
	
	public static void showUpdateDialog(Release release, int currentReleaseID, String currentVersion, int currentLicenseVersion) {
		
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
	}
	
	@FXML
	public void performUpdate(ActionEvent event) {
		
	}

	@FXML
	public void cancel(ActionEvent event) {
		
	}
	
	@FXML
	public void ignoreVersion(ActionEvent event) {
		
	}
}
