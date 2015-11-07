# play2sdg


##Create eclipse project
Use play or sbt and run:  **eclipse with-source=true**

##To run tests inside eclispe
Install [Avaje Ebean Eclipse Plugin] (http://www.avaje.org/eclipseupdate/site.xml)

After the installation, activate Ebean enhancement by right clicking on your project and selecting Enable Ebean Enhancement.

##Now working on the deployment

## Packaging
try play dist


##Mesos-Marathon Deployment Commands

http://wombat30.doc.res.ic.ac.uk/play2sdg-1.0-SNAPSHOT.zip
sh ./play2sdg-*/start -Dhttp.port=$PORT

For custom logging: 
-Dlogger.file=/home/pg1712/scripts/play_conf/logger.xml


## Spark Java application - Packaging
using maven => maven_spark_magic: https://gist.github.com/prb/d776a47bd164f704eecb


## Running Jar in a cluster using spark-submit
./bin/spark-submit --class main.java.uk.ac.imperial.lsds.play2sdg.SimpleApp ../play2sdg-Spark-module-0.0.1-SNAPSHOT-driver.jar 


