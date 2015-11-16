#!/bin/bash

############################################################
# Author: pg1712@imperial.ac.uk - pgaref.github.io	   #
# Runs Jmeter Bechmark with different number of client	   #
# Also collects CPU and bandwidth statistics from workers  #
############################################################

############################################################
# Dependencies so far: pssh, pip install psutil, ..	   #
# Script needs to be pushed to all workers before launch   #
############################################################
#
# Start Cassandra and Play Manually!!!
#

RUN='NO-HT-w16-PlaySpark-NoIsolation-v2'
#Stats collection variable
STATS='1'
#Variable to control Spark initialisation
SPARK='1'
#Variable to clear caches
CACHE_CLEAR='0'

PLAY_WORKERS=("wombat16")
SPARK_WORKERS=("wombat16")


stop_generic_services(){
	#Pass the argument array by name
	name=$1[@]
	echo "Stopping system services.."    
	ssh localhost "sudo service memcached stop"
	ssh localhost "sudo service apache2 stop"
    	for worker in ${argument_array[@]}; do
		ssh $worker "sudo service memcached stop"
		ssh $worker "sudo service apache2 stop"
	printf "done\n"
	done
}

clear_cache() {
    #Pass the argument array by name
	name=$1[@]
    	argument_array=("${!name}")
    	echo "Clearing caches on servers: ${argument_array[@]}"    
	for worker in ${argument_array[@]}; do
		printf "Clearing system cache on $worker..."
		ssh $worker "sudo sync"
		ssh $worker "sudo sh -c 'echo 3 >/proc/sys/vm/drop_caches'"
		printf "done\n"
	done
	sleep 30
}

start_play() {
    	#Pass the argument array by name
	name=$1[@]
    	argument_array=("${!name}")
    	echo "Starting Play on servers: ${argument_array[@]}"
    	for worker in ${argument_array[@]}; do
    		printf "Starting play APP on $worker..."
		ssh $worker "cd $SCRIPTS_HOME/ && screen -dm -S 'play_run' ./run_play.sh > run_play.out"
		printf "done\n"
	done
}


stop_play() {
    	#Pass the argument array by name
	name=$1[@]
    	argument_array=("${!name}")
    	echo "Stopping Play on servers: ${argument_array[@]}"
    	for worker in ${argument_array[@]}; do
    		printf "Stopping play APP on $worker..."
		ssh $worker "killall run_play.sh"
		printf "done\n"
	done
}

start_spark() {
    	#Pass the argument array by name
	name=$1[@]
   	argument_array=("${!name}")
	echo "Starting Spark on servers: ${argument_array[@]}"
	for worker in ${argument_array[@]}; do
		printf "Starting Spark on $worker..."
		ssh $worker "cd $SCRIPTS_HOME/ && screen -dm -S 'spark_run' ./run_spark_cf_continously.sh > run_spark.out"
		printf "done\n"
	done
}

stop_spark() {
    	#Pass the argument array by name
	name=$1[@]
	argument_array=("${!name}")
	echo "Stopping Spark Container on servers: ${argument_array[@]}"
    	for worker in ${argument_array[@]}; do
    		printf "Stopping Spark on $worker..."
		ssh $worker "killall -u $USER run_spark_cf_continously.sh"
		printf "done\n"
	done
}

start_cassandra() {
    	#Pass the argument array by name
	name=$1[@]
    	argument_array=("${!name}")
    	echo "Starting Cassandra on servers: ${argument_array[@]}"
    	for worker in ${argument_array[@]}; do
    		printf "Starting Cassandra on $worker..."
		ssh $worker "cd $BASE_DIR/ && ./apache-cassandra-2.1.9/bin/cassandra -p ./apache-cassandra-2.1.9/casspid"
		printf "done\n"
	done
}

stop_cassandra() {
    	#Pass the argument array by name
	name=$1[@]
    	argument_array=("${!name}")
    	echo "Starting Play on servers: ${argument_array[@]}"
    	for worker in ${argument_array[@]}; do
    		printf "Starting play APP on $worker..."
		ssh $worker "killall -u $USER java"
		printf "done\n"
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


# create folder to store results
RESULTS=${RESULTS_FOLDER}/${FILE_BASE}
echo "Creating: $RESULTS - folder to store results"
mkdir -p ${RESULTS}

_jmeter_run="sh apache-jmeter-2.13/bin/jmeter.sh -n -t apache-jmeter-2.13/bin/play2sdg-datastax-benchmark.jmx -l"
_jmeter_results=${RESULTS}/jmeter_results
echo "Creating Jmeter Results Path: $_jmeter_results"
mkdir -p ${_jmeter_results}
touch ${RESULTS}/${RUN}

#JMeter Project Name (to upload)
echo "Updating Jmeters Upload Project Name: $RUN "
sed -i '3254s/.*/ \t<stringProp name="projects">'$RUN'<\/stringProp> /' /home/pg1712/apache-jmeter-2.13/bin/play2sdg-datastax-benchmark.jmx


#For more powerfull servers
clients=("5" "10" "50" "100" "200" "300" "400" "500" "600" "700" "800" "900" "1000" "1200" "1500")

#Make sure no services are running!!
stop_generic_services PLAY_WORKERS
if [ "$SPARK" == "1" ]; then
	stop_generic_services SPARK_WORKERS
fi



for (( i=0; i<${#clients[@]}; i++ ));
do
    
	echo 'Updating Jmeter properties to: '${clients[$i]}' clients'
	sed -i '20s/.*/ \t<stringProp name="ThreadGroup.num_threads">'${clients[$i]}'<\/stringProp> /' /home/pg1712/apache-jmeter-2.13/bin/play2sdg-datastax-benchmark.jmx

	start_cassandra PLAY_WORKERS
	sleep 20
	start_play PLAY_WORKERS
	sleep 10
	if [ "$SPARK" == "1" ]; then
		start_spark SPARK_WORKERS
	fi
	
	# run the gathering statistics script on the play workers
	if [ "$STATS" == "1" ]; then
		# parallel-ssh -H "${PLAY_WORKERS[@]}" "cd $SCRIPTS_HOME/ && screen -dm -S 'stats' python $STATS_SCRIPT -i ${IFACE} -f ${STATS_FILENAME}"
		for worker in ${PLAY_WORKERS[@]}; do
			echo "Starting $STATS_SCRIPT on workers: "$worker"..."
			ssh $worker "cd $SCRIPTS_HOME/ && screen -dm -S 'stats' python $STATS_SCRIPT -i ${IFACE} -f ${STATS_FILENAME}"
		done
	fi
	sleep 1

	echo 'Now Running Jmeter with params : '$_jmeter_run $_jmeter_results/SummaryReport_${clients[$i]}'clients.csv'
	eval $_jmeter_run $_jmeter_results/SummaryReport_${clients[$i]}'clients.csv'

	for worker in ${PLAY_WORKERS[@]}; do
	#	parallel-ssh -H "${PLAY_WORKERS[@]}" "killall -u $USER -SIGINT python"
		echo "Stopping $STATS_SCRIPT on workers: "$worker"..."
		ssh $worker "killall -u $USER -SIGINT python"
	done
	# collect statistics
	for worker in ${PLAY_WORKERS[@]}; do
		printf "Retrieving stats from $worker..."
		scp $worker:${SCRIPTS_HOME}/${STATS_FILENAME} ${RESULTS}/${worker}_${clients[$i]}'clients'_${STATS_FILENAME}
		ssh $worker "rm ${SCRIPTS_HOME}/${STATS_FILENAME}"
        printf "done\n"
	done

	########################################
	#And finally stop the running processes
	########################################
	stop_play PLAY_WORKERS
	stop_cassandra PLAY_WORKERS

	if [ "$SPARK" == "1" ]; then
		stop_spark  SPARK_WORKERS
		sleep 1
	fi

	if [ "$CACHE_CLEAR" == "1" ]; then
		clear_cache PLAY_WORKERS
		if [ "$SPARK" == "1" ]; then
			clear_cache SPARK_WORKERS
		fi
	fi
    
	echo 'All Done - Sleeping for 10s... '
	sleep 10s
done


echo 'Finished all Jmeter tests!!!!'
