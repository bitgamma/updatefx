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
