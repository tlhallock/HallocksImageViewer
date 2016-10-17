# HallocksImageViewer
A simple war file for showing images.



In order to build this, follow the following instructions:

import ImageUpdater into eclipse
run mvn package once within ImageUpdater
import ImageUpdater in Netbeans as an eclipse project
open ImageViewer in netbeans
fix the ImageViewer library to point at ImageUpdater/target/ImageUpdater.jar
fix DaContextListener.java by changing the path of the settings file

in order to run this on a server the first time, you need to create the database, like in res/CreateDB.sql


after that, you will need to run the script redeployed.sh after every time you redeploy the jar.
before you run the redeployed.sh, also make sure to changed the ip address if it has changed.
(Right now I have it in the home folder under otherLibs/.)
just do ./otherLibs/redeplyed.sh at the command line.
