
SUMMARY
=======
This is a simple cloud connector for Mule (specifically version 3.3) that allows integration with Couchbase. 

BUILDING
========
Run the Maven command "mvn package -Ddevkit.studio.package.skip=false" to build the connector.

INSTALLING IN MULE STUDIO
=========================
After you've succesfully built the connector, you need to follow these steps to install in Mule Studio:

1. In Mule Studio, access the Help menu, then select Install Software. 
2. Click the Add button to add an update site.
3. In the Name field, enter the name, "Couchbase".
4. Enter the full path to your connector, prepended with file:/", then click OK.
5. Select your connector from the update site.  
6. Follow the steps to accept the license, then restart Studio.
7. Studio makes your new cloud connector available for selection in the Studio palette.

(Steps taken from http://www.mulesoft.org/documentation/display/current/Your+First+Cloud+Connector)