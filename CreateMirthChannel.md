# Create a channel in Mirth #

## Overview ##

In Mirth Connect, a **Channel** is an abstract, unidirectional pipe to transfer data between applications. Data are read into the channel through a **Source** using a **Source Connector**. Then the data pass through the channel, where operations can be performed on the data, for example transforming it's representation. Finally, the data is sent to a **Destination** using a **Destination Connector**. A channel may have 1 or more destinations to send the data to. In this way, a channel can connect two separate applications

http://wiki.crossriver-openhds.googlecode.com/git/images/mirth/channel.PNG

## Requirements ##

To create a channel, you must define the following:

  * What application will you read data from (source)?
  * Where do you want to send data to (destination)?
  * How will you read data (source connector)?
  * How will you send data (destination connector)?
  * How will you map the data between the 2 applications (transformations)?

To provide a concrete example, we assume the following answers to these questions in this tutorial:

  * Read data from ODK Aggregate
  * Send the data to the OpenHDS
  * Read data from the ODK Aggregate database (MySQL)
  * Send data to the OpenHDS through web services
  * Mapping will be done with XML

## Create new Channel ##

First, create a new channel in Mirth by selecting the **Channels** link from the left context menu

http://wiki.crossriver-openhds.googlecode.com/git/images/mirth/create-1.PNG

Select **New Channel** link from the Channel Tasks menu in the left context menu

http://wiki.crossriver-openhds.googlecode.com/git/images/mirth/create-2.PNG

Provide a **Channel Name**. This is the name you will see in the Channel list.

Select **Set Data Types**, and change the data type for the **Source Connector** and **Destination Connectors** to XML

http://wiki.crossriver-openhds.googlecode.com/git/images/mirth/create-3.PNG

http://wiki.crossriver-openhds.googlecode.com/git/images/mirth/create-4.PNG

Optionally, provide a **Description** for the channel.

## Source Configuration ##

As mentioned previously, the source identifies where data will be read from. In this example, we assume you want to read from a database table.

Select the **Source** tab. There are different types of Connectors, but we will use the **Database Reader**. Select this from the list of Connector Types

http://wiki.crossriver-openhds.googlecode.com/git/images/mirth/create-5.PNG

You need to select a **Driver**, which indicates the type of database you want to connect to. Select **MySQL**. Then click **Insert URL Template** to generate a URL for the database

http://wiki.crossriver-openhds.googlecode.com/git/images/mirth/create-6.PNG

Replace the **host**, **port**, and **dbname** with the database you want to read from. In this case, we'll select a table from ODK  Aggregate database on the localhost. (If you are using the default MySQL port 3306, you do not need to explicitly put this in the URL). Supply a username and password that can access the database


http://wiki.crossriver-openhds.googlecode.com/git/images/mirth/create-7.PNG

You can select the polling type you prefer. The polling type dictates how often the channel will look for new data in the database. Interval will occur in a repeating fashion, for example, every 10 minutes. By contrast, Time will happen only once per day.

Click **Select**


http://wiki.crossriver-openhds.googlecode.com/git/images/mirth/create-8.PNG

Click **Get Tables**. This will retrieve all tables in the database. Find which table you want to read data from, and select the columns for the table by checking the boxes. Note: Only the values from the columns you select will be available. If a column if left unchecked, it's value will not be available. This will become more clear in the Transformation section. Finally, hit **Generate**

http://wiki.crossriver-openhds.googlecode.com/git/images/mirth/create-9.PNG

You will notice an SQL statement has been generated in the SQL text box.


http://wiki.crossriver-openhds.googlecode.com/git/images/mirth/create-10.PNG

You may also notice the SELECT statement Mirth generated does not have a filter clause (WHERE statement). This means that when Mirth runs this SQL statement, it will always grab all rows in the database. In most cases, this is not desirable. Instead, you will like to only retrieve new rows that haven't yet been seen before. Currently, we handle this by altering the the MySQL table with a new column (processed\_by\_mirth), which is used only by Mirth. It is a flag, with either the value 0 or 1, that indicates whether a row has already been processed. By default, the row starts with the value 0. After processing, it's value is updated to a 1 (discussed next.) There may be alternative methods for handling this case, but this is what we recommend currently.

Add a filter clause to the SQL statement

http://wiki.crossriver-openhds.googlecode.com/git/images/mirth/create-11.PNG

We want to update this column after a row has been processed. Select the **Yes** radio button for **Run On-Update Statement**. This will enable the **Update** button. Click **Update**

http://wiki.crossriver-openhds.googlecode.com/git/images/mirth/create-12.PNG

Click **Get Tables** button, and scroll to the table you selected in the previous step. Look for the flag column (processed\_by\_mirth) in the table, and check it. Then hit **Generate**

http://wiki.crossriver-openhds.googlecode.com/git/images/mirth/create-13.PNG

Mirth will generate an incomplete SQL UPDATE statement.

http://wiki.crossriver-openhds.googlecode.com/git/images/mirth/create-14.PNG

You are required to complete the update statement. In this case, we'll update the column with the value 1. Also, you must provide a SQL filter so only the row being processed is updated. To identify the row to update you can use the primary key of the table. In ODK Aggregate, the _URI column is the primary key of the table._

This statement is run in the **context of a single row**, which means we can retrieve the current rows' primary key value. Add a WHERE clause to the SQL statement. The value for the URI can be obtained by **clicking and dragging** the **uri item in the list box to the right onto the SQL text area. The _${_uri}** is a **variable** or **placeholder**, and will be replaced with the current rows' value during processing.

http://wiki.crossriver-openhds.googlecode.com/git/images/mirth/create-29.PNG

That completes the Source Connector configuration

## Transformer ##

Usually, the data you read in will not be in the format required for a destination. In this scenario, we must use a transformer. Transformers provide a way to change the data representation or format of the data. From the **Source** tab, select **Edit Transformer**.

http://wiki.crossriver-openhds.googlecode.com/git/images/mirth/create-20.PNG

From the right tab bar, select **Message Templates**

http://wiki.crossriver-openhds.googlecode.com/git/images/mirth/create-30.PNG

You will notice 2 text areas: Inbound Message Template and Outbound Message Template. The Inbound Message Template is generated automatically by Mirth based on the SQL SELECT statement from the previous step. You should not ever have to modify this field. The Outbound Message Template must be manually populated. In our scenario, we know the Outbound Message Template must be an XML document that adheres to the OpenHDS XML structure for a Visit. Simply paste in the XML template

http://wiki.crossriver-openhds.googlecode.com/git/images/mirth/create-22.PNG

Now, we must create a **mapping** between the inbound and outbound templates. We will map an inbound XML elements to corresponding outbound XML elements. You accomplish the mappings through a sequence of **steps**.

First, select **Message Trees** from the right tab bar.

http://wiki.crossriver-openhds.googlecode.com/git/images/mirth/create-23.PNG

You will notice that both the inbound and outbound text areas turn into expandable tree lists. Start by expanding the **visitLocation** in the outbound message template tree

http://wiki.crossriver-openhds.googlecode.com/git/images/mirth/create-24.PNG

We want the value to go in the extId element, so expand **extId** as well

Now, drag the item labeled as **empty** into middle panel and drop it. This should create a new step

http://wiki.crossriver-openhds.googlecode.com/git/images/mirth/create-25.PNG

You need to provide a mapping for the step. From the Inbound Message Template Tree, find the corresponding element you want to populate the value with.

Drag the **value** element to the **Mapping** text box

http://wiki.crossriver-openhds.googlecode.com/git/images/mirth/create-26.PNG

You have now mapped the inbound element to it's corresponding outbound element. Repeat the same process for each element you need to map.

http://wiki.crossriver-openhds.googlecode.com/git/images/mirth/create-27.PNG

When you are done, hit **Back to Channel**

http://wiki.crossriver-openhds.googlecode.com/git/images/mirth/create-28.PNG

## Destination Connector ##

Click the **Destinations** tab. In this tutorial, we are sending the data to the OpenHDS through a web service call. Select **HTTP Sender** from the **Connector Type**.

http://wiki.crossriver-openhds.googlecode.com/git/images/mirth/create-15.PNG

Enter the URL to the Visit web service on your host

Select **POST** as the Method, and **No** for Multipart. Optionally set the Send Timeout (the time the connector will wait before it stops trying to send the data to the web service.)

http://wiki.crossriver-openhds.googlecode.com/git/images/mirth/create-16.PNG

Choose **Yes** for Authentication, **Basic** for Authentication Type and provide a username and password for your OpenHDS application.

http://wiki.crossriver-openhds.googlecode.com/git/images/mirth/create-17.PNG

Change the Content Type to **application/xml**. Finally, drag-and-drop the **Transformed Data** list item into the Content text area. It should look like the following:

http://wiki.crossriver-openhds.googlecode.com/git/images/mirth/create-18.PNG

Finally, hit **Save Changes** from the left context menu.

http://wiki.crossriver-openhds.googlecode.com/git/images/mirth/create-19.PNG

That completes the tutorial for creating a channel in Mirth.