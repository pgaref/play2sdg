#!/bin/bash
# Author: pg1712@imperial.ac.uk - pgaref.github.io
# Automating the Play-Spark test process
# -------------------------------------------------

while :
do
	./spark-1.1.0/bin/spark-submit --master local[*] --driver-memory 1g --class main.java.uk.ac.imperial.lsds.play2sdg.SparkCollaborativeFiltering ./play2sdg-Spark-module-0.0.1-SNAPSHOT-driver.jar
	echo "Spark CF Job done! Repeat!"
done

echo "Should never Terminate!!"




