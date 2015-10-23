#!/bin/bash

############################################################
# Author: pg1712@imperial.ac.uk - pgaref.github.io	   #
# Runs Jmeter Bechmark with different number of client	   #
# Also collects CPU and bandwidth statistics from workers  #
############################################################

############################################################
# Dependencies so far: pssh, pip install psutil, ..	   #
# Script needs to be pushed to all workers to launche      #
############################################################
RUN='play_isolated_w16'

clear_cache() {
	echo "Clearing caches..."
	for worker in $1; do
		printf "Clearing system cache on $worker..."
		ssh $worker "sudo sync"
		ssh $worker "sudo sh -c 'echo 3 >/proc/sys/vm/drop_caches'"
		printf "done\n"
	done
}

stop_play_app() {
	echo 'Stopping Play application on... {$1}'
	parallel-ssh -H "$1" "killall -u $USER run_play.sh"
}

stop_spark() {
	echo 'Stopping Spark application on... {$1}'
	parallel-ssh -H "$1" "killall -u $USER run_spark_cf_continously.sh"
}

stop_cassandra() {
	local PID_File='/home/pg1712/apache-cassandra-2.1.9/casspid'
	for worker in ${1}; do
		echo "Stopping Cassandra application with pid: $PID_File on worker $worker"
#	parallel-ssh -H "$1" "kill -9 $(ps aux | grep cassandra | awk '{print $2}')"
		ssh $worker "pkill -9 java"
	done

}

BASE_DIR=/home/$USER
SCRIPTS_HOME=$BASE_DIR/scripts
RESULTS_FOLDER=$BASE_DIR/scripts/results
FILE_BASE=`date +%Y-%m-%d-%H-%M-%S`
STATS_FILENAME=stats.csv
STATS_SCRIPT=system-stats_sys.py
PLAY_SCRIPT=run_play.sh
#Thats the _Workers Interface!
IFACE='eth1'

PLAY_WORKERS=("wombat16")
SPARK_WORKERS=("wombat17")

#Stats collection always enabled!
STATS='1'


# create folder to store results
RESULTS=${RESULTS_FOLDER}/${FILE_BASE}
echo "Created $RESULTS folder to store results"
mkdir -p ${RESULTS}

_jmeter_run="sh apache-jmeter-2.13/bin/jmeter.sh -n -t apache-jmeter-2.13/bin/play2sdg-datastax-benchmark.jmx -l"
_jmeter_results=${RESULTS}/jmeter_results
echo "Creating Jmeter Results Path $_jmeter_results"
mkdir -p ${_jmeter_results}
touch ${RESULTS}/${RUN}

#JMeter Project Name (to upload)
echo "Updating Jmeters Upload Project Name "
sed -i '3349s/.*/ \t<stringProp name="projects">'$RUN'<\/stringProp> /' /home/pg1712/apache-jmeter-2.13/bin/play2sdg-datastax-benchmark.jmx


#For more powerfull servers
clients=("5" "10" "50" "100" "200" "300" "400" "500" "600" "700" "800" "900" "1000" "1200" "1500")
#clients=("5" "10" "50" "100" "200" "300" "400" "500")



for (( i=0; i<${#clients[@]}; i++ ));
do

	echo 'Updating Jmeter properties to '${clients[$i]}' clients'
	sed -i '20s/.*/ \t<stringProp name="ThreadGroup.num_threads">'${clients[$i]}'<\/stringProp> /' /home/pg1712/apache-jmeter-2.13/bin/play2sdg-datastax-benchmark.jmx

	echo "Starting Cassandra"
	parallel-ssh -H "$PLAY_WORKERS" "cd $BASE_DIR/ && ./apache-cassandra-2.1.9/bin/cassandra -p ./apache-cassandra-2.1.9/casspid"
	printf "Wait for Cassandra to settle..."
	sleep 30
	printf "done\n"

	# Start Spark
	echo "Starting Spark"
	parallel-ssh -H "$SPARK_WORKERS" "cd $SCRIPTS_HOME/ && ./run_spark_cf_continously.sh"
	printf "Wait for Spark to settle..."
	sleep 5
	printf "done\n"
	
	echo "Starting Play"
	parallel-ssh -H "$PLAY_WORKERS" "cd $SCRIPTS_HOME/ && screen -dm -S 'play_run' ./run_play.sh"
	printf "Wait for Play to settle..."
	sleep 5
	printf "done\n"


	# run the gathering statistics script on the play workers
	if [ "$STATS" == "1" ]; then
		echo "Starting $STATS_SCRIPT on workers: "$PLAY_WORKERS""
		parallel-ssh -H "$PLAY_WORKERS" "cd $SCRIPTS_HOME/ && screen -dm -S 'stats' python $STATS_SCRIPT -i ${IFACE} -f ${STATS_FILENAME}"
	fi
	
	sleep 1
	

	echo 'Now Running Jmeter with params : '$_jmeter_run $_jmeter_results${clients[$i]}'clients.jtl'
	eval $_jmeter_run $_jmeter_results/${clients[$i]}'clients.jtl'


	echo "Stopping $STATS_SCRIPT on workers"
	parallel-ssh -H "$PLAY_WORKERS" "killall -u $USER -SIGINT python"
	
	stop_spark	$SPARK_WORKERS
	stop_play_app	$PLAY_WORKERS
	stop_cassandra	$PLAY_WORKERS


	# collect statistics
	for worker in ${PLAY_WORKERS}; do
		echo "Retrieving stats from $worker"
		scp $worker:${SCRIPTS_HOME}/${STATS_FILENAME} ${RESULTS}/${STATS_FILENAME}_$worker_${clients[$i]}'clients'
#		scp $worker:${_jmeter_results} ${_jmeter_results}_$worker
		ssh $worker "rm ${SCRIPTS_HOME}/${STATS_FILENAME}"
#		ssh $worker "rm ${_jmeter_results}"
		ssh $worker 'rm /home/pg1712/play2sdg-1.0-SNAPSHOT/RUNNING_PID'
	done

	clear_cache $PLAY_WORKERS
	echo 'All Done - Sleeping for 1m... '
	sleep 1m
done

echo 'Finished all Jmeter tests!!!!'
