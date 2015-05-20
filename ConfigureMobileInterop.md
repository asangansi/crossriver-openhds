# Configure Mobile Interop Server #

  * [Database Configuration](#Database_Configuration.md)
  * [User Management](#User_Management.md)
  * [Application Settings](#Application_Settings.md)

## Database Configuration ##

When you first deploy the Mobile Interop WAR, you will be asked to set the administrator password

http://wiki.crossriver-openhds.googlecode.com/git/images/mobileprojects/interop-configure-1.PNG

After you click **Create Admin User** you will be prompted to login. The username is **admin** and the password will be whatever you chose

Next you will want to configure the database properties, click **Database Configuration**

Fill in required information. If this is the first time configuring the mobile interop server, you will want to click the checkbox to create a fresh database.

http://wiki.crossriver-openhds.googlecode.com/git/images/mobileprojects/interop-configure-2.PNG

After you hit **Save Database Configuration** you will be prompted to restart the application. You should restart Tomcat once you see this screen.

http://wiki.crossriver-openhds.googlecode.com/git/images/mobileprojects/interop-configure-3.PNG

Once you restart Tomcat, you can go back to the Mobile Interop application. Since database configuration was changed, you will need to set the administrator password again

## User Management ##

In order for the Mobile Helper application to work with the Mobile Interop server, you must set up user accounts. The user accounts consist of:

  * A username - This is used to log in to the Mobile Helper Application
  * A password - Used in Mobile Helper Application
  * A set of Supervised Field Workers

The field workers represent the forms a supervisor will download. These values are taken from the mobile forms when they are completed. All forms require a field worker id.

  * Start by selecting **Manage Server Users** from the main menu

http://wiki.crossriver-openhds.googlecode.com/git/images/mobileprojects/interop-configure-4.PNG

  * Initially, there will be no users in the system. Select **Add a new user**

http://wiki.crossriver-openhds.googlecode.com/git/images/mobileprojects/interop-configure-5.PNG

  * Fill in required fields, and then hit **Create User**

http://wiki.crossriver-openhds.googlecode.com/git/images/mobileprojects/interop-configure-6.PNG

  * You should see the new user in the table of users.

http://wiki.crossriver-openhds.googlecode.com/git/images/mobileprojects/interop-configure-7.PNG

## Application Settings ##

You should configure the Mobile Interop application settings

  * Select **Application Settings** from the main menu

http://wiki.crossriver-openhds.googlecode.com/git/images/mobileprojects/interop-configure-8.PNG

  * Supply the URL to the OpenHDS Application, and click **Save OpenHDS Settings**

http://wiki.crossriver-openhds.googlecode.com/git/images/mobileprojects/interop-configure-9.PNG