# Installing ODK Aggregate #

  * First, download the ODK Aggregate server application for Windows:

**You should download a version in the 1.0.X series (e.g ODK Aggregate v1.0.9 windows-installer.exe)**

http://code.google.com/p/opendatakit/downloads/list

  * Next, run the installation file

http://wiki.crossriver-openhds.googlecode.com/git/images/aggregate/install-1.PNG

http://wiki.crossriver-openhds.googlecode.com/git/images/aggregate/install-2.PNG

  * The installation will create a folder and place the configured WAR file there. You'll need to remember this location

http://wiki.crossriver-openhds.googlecode.com/git/images/aggregate/install-3.PNG

http://wiki.crossriver-openhds.googlecode.com/git/images/aggregate/install-4.PNG

http://wiki.crossriver-openhds.googlecode.com/git/images/aggregate/install-5.PNG


http://wiki.crossriver-openhds.googlecode.com/git/images/aggregate/install-6.PNG

http://wiki.crossriver-openhds.googlecode.com/git/images/aggregate/install-7.PNG

  * **This next step is very important**. ODK will not function properly if you do not configure this properly. You **must** provide the IP Address of the host that will be hosting ODK Aggregate. It can be either a local or public IP address. If you want ODK to be accessible from the internet, then you must provide a public IP address

http://wiki.crossriver-openhds.googlecode.com/git/images/aggregate/install-8.PNG

http://wiki.crossriver-openhds.googlecode.com/git/images/aggregate/install-9.PNG


http://wiki.crossriver-openhds.googlecode.com/git/images/aggregate/install-10.PNG

http://wiki.crossriver-openhds.googlecode.com/git/images/aggregate/install-11.PNG

http://wiki.crossriver-openhds.googlecode.com/git/images/aggregate/install-12.PNG

http://wiki.crossriver-openhds.googlecode.com/git/images/aggregate/install-13.PNG

http://wiki.crossriver-openhds.googlecode.com/git/images/aggregate/install-14.PNG

  * You must provide a valid Google account. **This is critical because on the first sign in, you will need to use the Google account**

http://wiki.crossriver-openhds.googlecode.com/git/images/aggregate/install-15.PNG

http://wiki.crossriver-openhds.googlecode.com/git/images/aggregate/install-16.PNG

http://wiki.crossriver-openhds.googlecode.com/git/images/aggregate/install-17.PNG

  * Once the installation finishes, you will find the following files in the output directory:

http://wiki.crossriver-openhds.googlecode.com/git/images/aggregate/install-18.PNG

  * Now, you need to run the database script for ODK (create\_db\_and\_user.sql). From the command line, you can follow:

http://wiki.crossriver-openhds.googlecode.com/git/images/aggregate/install-19.PNG

http://wiki.crossriver-openhds.googlecode.com/git/images/aggregate/install-20.PNG

http://wiki.crossriver-openhds.googlecode.com/git/images/aggregate/install-21.PNG

  * Before you can deploy the ODK WAR file, You will need to install the MySQL Java Connector:

Download the connector from http://cdn.mysql.com/Downloads/Connector-J/mysql-connector-java-5.1.20.zip

This will download a zip file. In the zip file, you will find the MySQL Connector jar file (mysql-connector-java-5.1.20-bin.jar). Place this file in the tomcat/lib folder:

http://wiki.crossriver-openhds.googlecode.com/git/images/aggregate/install-22.PNG

  * Finally, you can deploy the ODKAggregate.war file to Tomcat. On your first login, you will need to select, **Sign in with Google**. After you sign in for the first time, you can create a local user to sign in with.

http://wiki.crossriver-openhds.googlecode.com/git/images/aggregate/install-23.PNG