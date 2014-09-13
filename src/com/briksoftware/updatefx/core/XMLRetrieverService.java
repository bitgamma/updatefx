package com.briksoftware.updatefx.core;

import java.net.URL;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import com.briksoftware.updatefx.model.Application;
import com.briksoftware.updatefx.model.Release;

public class XMLRetrieverService extends Service<Application> {
	private URL xmlURL;
	
	public XMLRetrieverService(URL xmlURL) {
		this.xmlURL = xmlURL;
	}

	@Override
	protected Task<Application> createTask() {
		return new Task<Application>() {

			@Override
			protected Application call() throws Exception {
				JAXBContext ctx = JAXBContext.newInstance(Application.class);
				Unmarshaller um = ctx.createUnmarshaller();
				Application app = (Application) um.unmarshal(xmlURL);
				
				for (Release release : app.getReleases()) {
					release.setApplication(app);
				}
				
				return app;
			}
		};
	}

}
