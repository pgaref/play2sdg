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
	/root/virtual_share/spark-1.2.0-bin-cdh4/bin/spark-submit --master local[*] --driver-memory 1g --class main.java.uk.ac.imperial.lsds.play2sdg.SparkCollaborativeFiltering /root/virtual_share/play2sdg-Spark-module-1.2-driver.jar local[*]  10.0.3.194
#    child=$! 
#    wait "$child"
	echo "Spark CF Job done! Repeat!"
done

echo "Should never Terminate!!"




