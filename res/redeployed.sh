#!/bin/bash


# First make sure to clean and build both ImageUpdater and ImageViewer
# Double check that DaContextListener has the right path in it
# Then go to http://208.113.132.241:8080/manager/html and undeploy and redeploy
# Then run this project...

cat /var/lib/tomcat8/webapps/ImageViewer/idkWhereThisGoes/settings.props

#sudo nano /var/lib/tomcat8/webapps/ImageViewer/idkWhereThisGoes/settings.props
#change hostname to the ip address (which you can find with ifconfig) 208.113.132.241
#change url.location to /var/lib/tomcat8/webapps/ImageViewer/idkWhereThisGoes/urls.txt

sudo sed -i 's?hostname=.*?hostname=208.113.132.241?' /var/lib/tomcat8/webapps/ImageViewer/idkWhereThisGoes/settings.props
sudo sed -i 's?url.location=.*?url.location=/var/lib/tomcat8/webapps/ImageViewer/idkWhereThisGoes/urls.txt?' /var/lib/tomcat8/webapps/ImageViewer/idkWhereThisGoes/settings.props


# sudo nano /var/lib/tomcat8/webapps/ImageViewer/idkWhereThisGoes/urls.txt
#add the following line:
#/home/ubuntu/webimgs:static
sudo bash -c 'echo "/home/ubuntu/webimgs:static" > /var/lib/tomcat8/webapps/ImageViewer/idkWhereThisGoes/urls.txt'

sudo chown tomcat8:tomcat8 /var/lib/tomcat8/webapps/ImageViewer/idkWhereThisGoes/urls.txt

java -cp "/home/ubuntu/otherlibs/commons-imaging-1.0-20161011.170907-98.jar:$(find /var/lib/tomcat8/webapps/ImageViewer/WEB-INF/lib/ | tr '\n' ':')" org.hallock.images.App --settingsFile /var/lib/tomcat8/webapps/ImageViewer/idkWhereThisGoes/settings.props --action update --root /home/ubuntu/webimgs


sudo service tomcat8 restart


#Test by viewing:
#http://208.113.132.241:8080/ImageViewer/

