#!/bin/bash
# Author: pg1712@imperial.ac.uk - pgaref.github.io
# Automating the Play-Spark test process
# -------------------------------------------------

_term() { 
  echo "Caught SIGTERM signal!" 
  kill -TERM "$child" 2>/dev/null
}

while :
do
#    trap _term SIGTERM
	/home/pg1712/spark-1.2.0-bin-cdh4/bin/spark-submit --master local[*] --driver-memory 1g --class main.java.uk.ac.imperial.lsds.play2sdg.SparkCollaborativeFiltering /home/pg1712/play2sdg-Spark-module-1.2-driver.jar local[*] 192.168.0.16
#    child=$! 
#    wait "$child"
	echo "Spark CF Job done! Repeat!"
done

echo "Should never Terminate!!"




