# UpdateFX
Update framework for JavaFX packaged applications.

UpdateFX is inspired by Sparkle and provides an all-in-one solution to update application based on the JavaFX framework and packaged as standalone desktop applications.

## Usage
Usage is very simple, basically you just have to add the following lines in your application:

```java
UpdateFX updater = new UpdateFX(MyApplication.class);
updater.checkUpdates();
```

In the package where "MyApplication.class" is located, you should have a file named app-info.properties with the following structure:

```
app.version = 1.0.1
app.release = 10001
app.licenseVersion = 1
app.updatefx.url = http://example.com/ExampleAppUpdateFX.xml
```

* ```app.version``` is the current application version in a human readable form.
* ```app.release``` is the internal version number and must contain only digits. It can either be a sequential number for every build (1, 2, 3) or encoded in any way you see fit like in the example above. This number is used to decide if an update exists or not, so it must be increased for every release.
* ```app.licenseVersion``` is the version of the license accepted by this application. This is used to differentiate between paid and free updates.
* ```app.updatefx.url``` is the URL where the XML file describing available updates is located. The format of this file is documented below.

## XML format
Updates are described using an XML file, which is downloaded and read by the framework using JAXB

## TODO
* Support for signature/hashes is defined in the XML format but is currently unimplemented (it is correctly read but is ignored)
* Localize in more languages
* Support MSI for Windows and PKG Installer for OS X

# Who is using this?
* [PDFKey Pro](http://pdfkey.com) - Mac and Windows utility to lock and unlock PDF files.
