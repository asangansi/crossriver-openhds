# Configuring the OpenHDS Mobile Application #

This guide will explain the required steps to get up and running with the mobile application.

## Requirements ##

  * This application will work for Android tablet devices running Android 3.0 or higher.
  * The OpenHDS Web Application must be running.
  * Open Data Kit Collect must be installed on the tablet device and properly configured.

## About the OpenHDS Mobile Application ##

One of the primary responsibilities of the OpenHDS Mobile application is to perform update rounds. After the baseline census is completed, the study population is established and the various regions are defined. The application allows the entire study population to be downloaded onto the mobile device. The application supports a step-by-step workflow of events in which individuals can be selected based on the location hierarchy they reside in and the household they may be part of. Once the individual of interest is located, various events can be assigned to them (i.e. Out Migration) in which the ODK Collect application will be launched and a partially completed Xform will be presented and ready to fill out. In often cases, referencing an individual on one of these Xforms tends to be a complicated task since each individual has a unique id. If one of these id's is entered incorrectly, the form will fail validation on the server side when it's processed. The OpenHDS Mobile application solves key issues where the id's of particular entities and various other fields can be automatically filled and thus error rates can potentially be significantly reduced.

## Setting up the Mobile Application Components ##

This demo will provide a step by step guide to get started using an existing instance of Aggregate and existing XForm definitions.

  * Open up a web browser on your tablet and download the OpenHDS Mobile application by proceeding to the following URL: http://code.google.com/p/crossriver-openhds/downloads/detail?name=openhds-mobile.apk.
  * Proceed to the following URL to download Open Data Kit Collect, http://code.google.com/p/opendatakit/downloads/detail?name=ODK%20Collect%20v1.1.7.apk
  * Open ODK Collect and configure it to point to the Aggregate Server location. Press **menu** -> **Server Preferences**.
  * Enter the following URL for the Server location: http://openhds.rcg.usm.maine.edu/ODKAggregate
  * Go back to the main menu of ODK Collect and press **Get Blank Form** and download all of the forms.
  * ODK Collect is now configured.

## Step by Step guide for the OpenHDS Mobile Application ##

When the application is opened for the first time, it will need to be configured to point to the OpenHDS Web Application.

  * Press the **Configure Server** tab on the action bar in the upper right corner of the screen.
  * You will be prompted to enter the URL of the OpenHDS Web Application's Web Services as well as the username and password for authentication. We have a demo instance set up if you would like to use it. Enter http://openhds.rcg.usm.maine.edu/crossriver/api/rest/corewebservice with a username of **admin** and password of **test**. If you don't wish to use the test data that we have provided, you will have to point it to the location of your OpenHDS instance.

  * Go back to the login screen. The first thing that must be done is to register a new user on the device. You will not be able to log in successfully if a user is not registered. The new user must reference a valid Field Worker that exists in the OpenHDS Web Application. The picture below demonstrates an example:

![http://wiki.crossriver-openhds.googlecode.com/git/images/mobileprojects/mobile1.png](http://wiki.crossriver-openhds.googlecode.com/git/images/mobileprojects/mobile1.png)

  * When the **Register** button is pressed, the Field Worker and password will be saved to the database. Next, uncheck the **Register Field Worker on Device** box and press the **Login** button.

![http://wiki.crossriver-openhds.googlecode.com/git/images/mobileprojects/mobile2.png](http://wiki.crossriver-openhds.googlecode.com/git/images/mobileprojects/mobile2.png)

  * You will be presented with the following screen. This is where most of the Update Data Collection Routine will occur. The user interface is split into three distinct parts. The column on the left is called the _Selection Column_. This is where selections will be made based on where in the hierarchy you wish to assign events for individuals. The column in the center is called the _Value Column_. This is where you will select the particular entity of interest. Based on what is selected in the Selection Column will determine what is produced in the Value Column. The column on the right is called the _Event Column_. This is where various events can be assigned to individuals. The buttons in this column will typically become enabled after an individual has been chosen.

![http://wiki.crossriver-openhds.googlecode.com/git/images/mobileprojects/mobile3.png](http://wiki.crossriver-openhds.googlecode.com/git/images/mobileprojects/mobile3.png)

  * The first thing we must do is download the study population onto the device. Pressing the **Select Region** button will not do anything until the study population database is present. Press the **Sync Database** tab on the top right of the screen. You will be presented with the following:

![http://wiki.crossriver-openhds.googlecode.com/git/images/mobileprojects/mobile4.png](http://wiki.crossriver-openhds.googlecode.com/git/images/mobileprojects/mobile4.png)

  * This is the procedure to download the study population onto the device. We highly recommend that you let this process finish and do not interrupt it. The study population will not be present on the device if this process is cancelled prematurely. Press the **Sync Database** button to start the process.

![http://wiki.crossriver-openhds.googlecode.com/git/images/mobileprojects/mobile5.png](http://wiki.crossriver-openhds.googlecode.com/git/images/mobileprojects/mobile5.png)

  * Depending on your database size, it may take a varying time to process. Please be patient. Once completed successfully, proceed back to the Main menu and press the **Select Region** button. You'll notice that the Location Hierarchy name and associated External Id can be seen in the Value Column.

![http://wiki.crossriver-openhds.googlecode.com/git/images/mobileprojects/mobile6.png](http://wiki.crossriver-openhds.googlecode.com/git/images/mobileprojects/mobile6.png)

  * Select AKP from the Value Column. The value that was selected can be seen under the Region button. Also, the **Select Sub Region** button became enabled. Select the button and the region of IDU.

![http://wiki.crossriver-openhds.googlecode.com/git/images/mobileprojects/mobile7.png](http://wiki.crossriver-openhds.googlecode.com/git/images/mobileprojects/mobile7.png)

  * The **Village** button became enabled. There are many villages to choose from. For the purposes of this tutorial, 'EBU' was the chosen village.

![http://wiki.crossriver-openhds.googlecode.com/git/images/mobileprojects/mobile8.png](http://wiki.crossriver-openhds.googlecode.com/git/images/mobileprojects/mobile8.png)

  * The next step is to select a round for the Visit.

![http://wiki.crossriver-openhds.googlecode.com/git/images/mobileprojects/mobile9.png](http://wiki.crossriver-openhds.googlecode.com/git/images/mobileprojects/mobile9.png)

  * At this point, two buttons became enabled, **Select Location** and **Create Location**. Select Location will allow you to select an existing Location within the specified village of 'EBU'. Creating a Location is more interesting, so for the purposes of this tutorial, we'll do that.

![http://wiki.crossriver-openhds.googlecode.com/git/images/mobileprojects/mobile10.png](http://wiki.crossriver-openhds.googlecode.com/git/images/mobileprojects/mobile10.png)

  * You will be shown a new screen to search for an Individual. In this case, we need to identify who is the Location Head. The Region, Sub Region and Village will already be prefilled for you based on what you selected. Everything else will be left blank. If you double tap Region, Sub Region, Village, or Location, you will be able to filter. For example, by double tapping the Location text field, the following dialog will be displayed:

![http://wiki.crossriver-openhds.googlecode.com/git/images/mobileprojects/mobile19.png](http://wiki.crossriver-openhds.googlecode.com/git/images/mobileprojects/mobile19.png)

  * If you happen to know where the Individual lives you can choose to narrow down the Location. Additionally, you can further filter the result down to first name, last name, and gender. When the **Search** button is pressed, the results will show up on the right.

![http://wiki.crossriver-openhds.googlecode.com/git/images/mobileprojects/mobile11.png](http://wiki.crossriver-openhds.googlecode.com/git/images/mobileprojects/mobile11.png)

  * Selecting the individual will transfer control to the associated Location XForm definition stored in ODK Collect. In this example, 'Micah Epke' is selected as the Location Head.

![http://wiki.crossriver-openhds.googlecode.com/git/images/mobileprojects/mobile12.png](http://wiki.crossriver-openhds.googlecode.com/git/images/mobileprojects/mobile12.png)

  * The Location XForm is launched and the fields are prefilled. However, there are some fields that were not able to be prefilled such as 'Location Type' and 'Geo Point'. Proceed to the Geo Point question. Collecting Geo Point coordinates is a feature from ODK Collect that will use your device GPS settings to pinpoint your approximate location. By pressing the button will start a process to pinpoint your coordinates.

![http://wiki.crossriver-openhds.googlecode.com/git/images/mobileprojects/mobile13.png](http://wiki.crossriver-openhds.googlecode.com/git/images/mobileprojects/mobile13.png)

  * Complete the Location XForm and you will be transitioned back to the OpenHDS Update menu. The Location information chosen will be displayed under the Select Location button. Notice how the Latitude/Longitude fields are blank. This is because a new Location was created and the OpenHDS Web Application isn't aware of it yet. This is normal behavior. If we chose the Select Location option instead of the Create Location option, they would be shown.

![http://wiki.crossriver-openhds.googlecode.com/git/images/mobileprojects/mobile14.png](http://wiki.crossriver-openhds.googlecode.com/git/images/mobileprojects/mobile14.png)

  * The **Create Visit** button became enabled. Press it and you will be presented with another XForm for filling out a new Visit.

![http://wiki.crossriver-openhds.googlecode.com/git/images/mobileprojects/mobile15.png](http://wiki.crossriver-openhds.googlecode.com/git/images/mobileprojects/mobile15.png)

  * Every field for a new visit has been prefilled automatically for us. Complete the visit Xform. You will be brought back to the OpenHDS Update screen. Next, an individual can be selected. Press the **Select Individual** button.

![http://wiki.crossriver-openhds.googlecode.com/git/images/mobileprojects/mobile16.png](http://wiki.crossriver-openhds.googlecode.com/git/images/mobileprojects/mobile16.png)

  * For the sake of this tutorial, 'Eamana Bassey' is the chosen individual selected. Once the individual is selected, events can be assigned to them. Notice all of the event buttons in the Event Column became enabled.

![http://wiki.crossriver-openhds.googlecode.com/git/images/mobileprojects/mobile17.png](http://wiki.crossriver-openhds.googlecode.com/git/images/mobileprojects/mobile17.png)

  * Each event selected will launch a new XForm in ODK Collect and prefill any fields that it can. For the sake of this tutorial, the **Birth Registration** event will be selected.

![http://wiki.crossriver-openhds.googlecode.com/git/images/mobileprojects/mobile18.png](http://wiki.crossriver-openhds.googlecode.com/git/images/mobileprojects/mobile18.png)

  * Complete the XForm and you will be brought back to the OpenHDS update screen. From here you can assign more events to individuals or even finish the visit when you're done.

## GPS Functionality ##

This is the newest addition to the tablet. If latitude/longitude coordinate data is being stored for Locations, this will allow you to select the Location nearest to you without having to go through the entire Location Hierarchy manually. The dataset used for this demo includes some Locations that I created myself.

A button labelled **Find Location with Geo Point** is enabled from the very beginning, as shown in the screen below:

![http://wiki.crossriver-openhds.googlecode.com/git/images/mobileprojects/mobile20.png](http://wiki.crossriver-openhds.googlecode.com/git/images/mobileprojects/mobile20.png)

**Make sure that you have GPS functionality enabled on your tablet (Check Settings -> Location Services)**

If you do not have GPS functionality enabled, a message will appear telling you to enable it. If you do have GPS enabled, you should be presented with a similar screen:

![http://wiki.crossriver-openhds.googlecode.com/git/images/mobileprojects/mobile21.png](http://wiki.crossriver-openhds.googlecode.com/git/images/mobileprojects/mobile21.png)

The bottom part of the screen shows a map with your current location and the top part of the screen lists all registered locations within 25miles. Selecting one of the Locations in the list will bring you back to the main application with the Location Hierarchy already specified.

![http://wiki.crossriver-openhds.googlecode.com/git/images/mobileprojects/mobile22.png](http://wiki.crossriver-openhds.googlecode.com/git/images/mobileprojects/mobile22.png)

## Configuring XForm Definitions ##

The mobile application will function appropriately if common naming strategies are used. If you would like the application to automatically fill in fields on the XForm, standard naming conventions must be applied when the XForm definitions are created.

The following is a sample of an XForm instance.

```
 <instance>
   <data id="visit_registration_v3">
     <visitId/>
     <fieldWorkerId/>
     <houseId/>
     <visitDate/>
     <roundNumber/> 
     <derivedFromUri />
     <supervisorStatus />
     <processedByMirth />
     <validationFailed />
   </data>
 </instance>
```

The `<instance>` element has one child, `<data>` which is critical and must follow that convention. Additionally, the id attribute of the `<data>` element is required. The id must start with one of the core update events of the OpenHDS.

The id attribute must start with one of the following prefixes:

  * visit
  * location
  * household
  * membership
  * relationship
  * inmigration
  * outmigration
  * pregnancyobservation
  * birth
  * socialgroup
  * death

Whatever is included after the prefix doesn't matter. What matters is that the id of the Xform definition starts with one of the core update event names. The reason for this is because the Mobile Application will dynamically search for a valid XForm definition with the corresponding name of the selected update event. Once found, the definition will be inspected and fields will be automatically filled in.

The `<data>` element can have an infinitely deep hierarchy. Meaning, it can have many child elements, which also may have have their own child elements. What's important though, is that if you would like some of these elements to be automatically filled, they must be named one of the following:

  * visitId : The external id of the visit
  * roundNumber : The round number for the visit
  * visitDate : The date of the visit (also known as date of interview)
  * individualId : The external id of the individual
  * motherId : The mother id of the individual
  * fatherId : The father id of the individual
  * firstName : The first name of the individual
  * lastName : The last name of the individual
  * gender : The gender of the individual
  * dob : The date of birth of the individual
  * houseId : The external id of the house (or location)
  * houseName : The house name
  * hierarchyId : The external id of the hierarchy
  * latlong: The latitude/longitude of the house
  * householdId : The external id of the household (or social group)
  * householdName : The household name
  * fieldWorkerId : The external id of the field worker
  * child1Id : The external id of the first child in a birth event
  * child2Id : The potential external id of the second child in a birth event
  * childFatherId : The external id of the child's father
  * childFatherFirstName : The first name of the child's father
  * childFatherLastName : The last name of the child's father
  * womanId : The external id of a woman in a relationship event
  * manId : The external id of a man in a relationship event

Any elements that are children of the `<data>` element that are not named one of the following as mentioned above will simply be ignored and will not be automatically filled in.