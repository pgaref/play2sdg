# play2sdg


###Create eclipse project
Use play or sbt and run:  **eclipse with-source=true**

###To run tests inside eclispe -- For Ebean only!
Install [Avaje Ebean Eclipse Plugin] (http://www.avaje.org/eclipseupdate/site.xml)
After the installation, activate Ebean enhancement by right clicking on your project and selecting Enable Ebean Enhancement.



###Spark Java application - Packaging
using maven => maven_spark_magic: https://gist.github.com/prb/d776a47bd164f704eecb


###Running Jar in a cluster using spark-submit
./bin/spark-submit --class main.java.uk.ac.imperial.lsds.play2sdg.SimpleApp ../play2sdg-Spark-module-0.0.1-SNAPSHOT-driver.jar 


###Play development issues encountered:
my play folder: **/development/play-2.1.5/repository/**
http://dinezhshetty.blogspot.co.uk/2013/07/solved-sbtresolveexception-download.html
##Play development version: play-2.1.5

###Kundera models integration with play
* cd target/scala-2.10/classes
* jar -cvf myEntities.jar models/
* cp myEntities.jar ../../../lib/

###Play Project Packaging
* play dist

###Launching Marathon at Mesos cluster
* nohup ./bin/start --master zk://localhost:3181/mesos --zk_hosts localhost:3181 &

###Launching play.zip at Mesos-Marathon
* Command: sh ./play2sdg-*/start -Dhttp.port=$PORT -Xms1900m -Xmx6144m
* URL: http://wombat30.doc.res.ic.ac.uk/play2sdg-1.0-SNAPSHOT.zip

###Terminating Mesos framework
* /usr/lib/zookeeper/bin/zkCli.sh -server wombat30.doc.res.ic.ac.uk:3181
*  curl -d "frameworkId=20150517-162701-2877535122-5050-28705-0238" -X POST http://wombat30.doc.res.ic.ac.uk:5050/master/shutdown

### JMeter Metrics Agent###
* wget http://jmeter-plugins.org/downloads/file/ServerAgent-2.2.1.zip
* unzip ServerAgent-2.2.1.zip 
* nohup sh ./startAgent.sh  & > /dev/null 


### JMeter Run from console Command ###
sh apache-jmeter-2.13/bin/jmeter.sh -n -t apache-jmeter-2.13/bin/play-Login-Recc-Logout-benchmarkMode.jmx -l ./play_stress_results/play_mesos_colocate_0.5k-with-Monitoring-v1.jtl

### Nginx limits conf for benchmarking ###
* sudo nano /etc/security/limits.conf

 ```
 # Panagiotis Extra Configuration

 pg1712          soft    nofile          65536

 pg1712          hard    nofile          65536

 * - nofile 120000 
```

