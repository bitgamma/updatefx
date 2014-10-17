# UpdateFX
Update framework for JavaFX packaged applications.

UpdateFX is inspired by Sparkle and provides an all-in-one solution to update application based on the JavaFX framework and packaged as standalone desktop applications. It has both a beautiful GUI with the ability to view HTML-formatted changelogs and the mechanism needed to actually discover and perform the updates.

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
Updates are described using an XML file, which is downloaded and read by the framework using JAXB. A typical file would look like this

```xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<application xmlns="http://www.briksoftware.com/updatefx" name="Example App" changelog="http://example.com/changelog">
  <release id="20000" version="2.0.0" releaseDate="2014-10-11T00:00:00.000+02:00" licenseVersion="2">
    <binary href="http://example.com/ExampleApp2.dmg" size="100000" platform="mac" />
    <binary href="http://example.com/ExampleAppSetup2.exe" size="10000" platform="win_x86" />
    <binary href="http://example.com/ExampleAppSetup2_64.exe" size="10000" platform="win_x64" />
  </release>
  <release id="10002" version="1.0.2" releaseDate="2014-10-10T00:00:00.000+02:00" licenseVersion="1">
    <binary href="http://example.com/ExampleApp.dmg" size="100000" platform="mac" />
    <binary href="http://example.com/ExampleAppSetup.exe" size="10000" platform="win_x86" />
    <binary href="http://example.com/ExampleAppSetup64.exe" size="10000" platform="win_x64" />
  </release>
</application>
```

As you can see, multiple releases can be active at the same time. This allows you for example to support more than one major revision and deliver security updates.

```application``` has two attributes: ```name``` and ```changelog```. The URL to the changelog will be used to display changes from the previous version. UpdateFX will send as parameter ```from``` and ```to``` indicating respectively the current version of the application and the version it wish to update to. The server responding can use these parameters to show only relevant changes

```release``` describes each release and can appear multiple times. It has the following attributes:
  * ```id```corresponds to the ```app.release``` from the info file. This will be used to determine if there is a newer version
  * ```name``` is used to display the name of this version
  * ```releaseDate``` is the release date and must be formatted as seen above
  * ```licenseVersion``` is the version of the license. This will be used to determine if the update is free or paid

```binary``` can be found in releases. A release can have multiple binaries, one for each supported OS. Its attributes are:
  * ```href``` the URL to download the binary
  * ```size``` the size, in bytes, of the file. If your webserver gives this information instead, this attribute will be ignored
  * ```platform``` the platform on which this binary runs. Currently supported values are: *mac*, *win_x86*, *win_x64*

## Update process
When invoking the ```checkUpdates``` method, the framework will download the XML file (on a background thread) and look for updates. The algorithm to determine if an update is available is quite simple: if a release with a bigger number is present, then there is an update available. In case there is more than one possible update, the one with biggest number and compatible license version is selected.

If no update is present, nothing happens. If an update is found a dialog is shown informing about the update, showing the changelog and informing the user if the update is a free or paid one. The user can decide to ignore or install the update. If the user choose to install the update, the framework will start the installation procedure and terminate the application. At the end of the update, the application will be restarted. No user intervention is required during install.

## TODO
* Support for signature/hashes is defined in the XML format but is currently unimplemented (it is correctly read but is ignored)
* Localize in more languages
* Support MSI for Windows and PKG Installer for OS X

# Who is using this?
* [PDFKey Pro](http://pdfkey.com) - Mac and Windows utility to lock and unlock PDF files.
