# Mirth Configuration #

To get started, you need the Mirth files. You can download them [here](http://crossriver-openhds.googlecode.com/files/mirth-exported-files.zip). Alternatively, you can checkout the following the [Mobile Projects](MobileProjects.md) page

  * [First Time Login](#First_Time_Login.md)
  * [Mirth HTTP Helper](#Mirth_HTTP_Helper.md)
  * [Configuration](#Configuration.md)
  * [Channel Deployment](#Deployment.md)

## First Time Login ##

The first time you login to Mirth, you need to set up the administrative account. Right click the Mirth Server Manager and select Launch Administrator

http://wiki.crossriver-openhds.googlecode.com/git/images/mirth/configure-1.PNG

  * The default username and password are: **admin**

http://wiki.crossriver-openhds.googlecode.com/git/images/mirth/configure-2.PNG

http://wiki.crossriver-openhds.googlecode.com/git/images/mirth/configure-3.PNG

## Mirth HTTP Helper ##

You will see the Mirth HTTP Helper jar. You can download it [here](http://crossriver-openhds.googlecode.com/files/mirth-http-helper-1.0-jar-with-dependencies.jar) or build it from source (see build section.)

Copy the jar **mirth-http-helper-1.0-jar-with-dependencies.jar** to C:\Program Files\Mirth Connect\custom-lib folder

http://wiki.crossriver-openhds.googlecode.com/git/images/mobileprojects/mirth-build-3.PNG

Finally, you need to restart Mirth

http://wiki.crossriver-openhds.googlecode.com/git/images/mobileprojects/mirth-build-4.PNG

## Configuration ##

To configure Mirth, launch the Mirth Administrator application

http://wiki.crossriver-openhds.googlecode.com/git/images/mirth/configure-4.PNG

Login using your username and password

http://wiki.crossriver-openhds.googlecode.com/git/images/mirth/configure-5.PNG

When the Mirth Administrator application starts, select 'Channels' from the left context menu. Under 'Channel Tasks' select **Edit Global Scripts**

http://wiki.crossriver-openhds.googlecode.com/git/images/mirth/configure-6.PNG

Select 'Import Scripts'

http://wiki.crossriver-openhds.googlecode.com/git/images/mirth/configure-7.PNG

Navigate to the directory where you have the Mirth files. You will see a directory **scripts**. Select the file **global-script.xml** and hit **Open**

http://wiki.crossriver-openhds.googlecode.com/git/images/mirth/configure-8.PNG

Next, you need to configure the top portions of the global scripts section. Specifically, you need to configure:

  * ODK Database information
  * OpenHDS Application Information
  * Mobile Interop Server Information

Once you have configured this information, you need to select **Save Scripts**. Then you can hit **Channels** to go back to the Channels screen

http://wiki.crossriver-openhds.googlecode.com/git/images/mirth/configure-9.PNG

Next, select **Edit Code Templates**

http://wiki.crossriver-openhds.googlecode.com/git/images/mirth/configure-10.PNG

Select **Import Code Templates**. From Mirth files directory, go to the **scripts** folder and select **code-templates.xml**

http://wiki.crossriver-openhds.googlecode.com/git/images/mirth/configure-11.PNG

Be sure to **Save Code Templates** and then hit **Channels**

http://wiki.crossriver-openhds.googlecode.com/git/images/mirth/configure-12.PNG

Finally, you will need to import all Channels. Select **Import Channel**

http://wiki.crossriver-openhds.googlecode.com/git/images/mirth/configure-13.PNG

Browse to your Mirth folder, and go to the **Channels** folder. Import each of the XML files. After you select a file, be sure to hit **Save Changes**, then hit Channels

http://wiki.crossriver-openhds.googlecode.com/git/images/mirth/configure-15.PNG

Repeat the same process for each of the remaining channels. There are a total of 26 channels. Unfortunately, there is no way to do a bulk import with Mirth at this time, so each channel must be individually imported. When you are finished importing, you should see 26 channels listed

http://wiki.crossriver-openhds.googlecode.com/git/images/mirth/configure-14.PNG

## Deployment ##

Finally, you need to deploy the channels.

**NOTE: If you try to deploy the channels before setting up ODK Aggregate, you will see errors occurring inside Mirth. To avoid seeing these errors, you should completely configure ODK Aggregate before you deploy the Mirth Channels**

Select **Channels** and click **Redeploy All**

http://wiki.crossriver-openhds.googlecode.com/git/images/mirth/configure-16.PNG

On the **Dashboard**, you should now see all channels listed

http://wiki.crossriver-openhds.googlecode.com/git/images/mirth/configure-17.PNG