package com.briksoftware.updatefx.core;

import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import com.briksoftware.updatefx.model.Application;

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
				return (Application) um.unmarshal(xmlURL);
			}
		};
	}

}
