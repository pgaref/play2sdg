#!/bin/bash

############################################################
# Author: pg1712@imperial.ac.uk - pgaref.github.io	       #
# Runs Jmeter Bechmark with different number of client	   #
# Also collects CPU and bandwidth statistics from workers  #
############################################################

############################################################
# Dependencies so far: pssh, pip install psutil, ..	       #
# Script needs to be pushed to all workers before launch   #
############################################################
RUN='w16_lxc_colocated_play'
#Stats collection variable
STATS='1'
#Variable to control Spark initialisation
SPARK='1'
#Variable to clear caches
CACHE_CLEAR='1'

PLAY_WORKERS=("wombat16")
SPARK_WORKERS=("wombat16")

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

stop_play_lxc() {
    #Pass the argument array by name
	name=$1[@]
    argument_array=("${!name}")
	echo "Stopping Play Container on servers: ${argument_array[@]}"
    for worker in ${argument_array[@]}; do
    	printf "Stopping play APP on $worker..."
		ssh $worker "sudo lxc-stop -n play-container"
		printf "done\n"
	done
}

stop_spark_lxc() {
    #Pass the argument array by name
	name=$1[@]
    argument_array=("${!name}")
	echo "Stopping Spark Container on servers: ${argument_array[@]}"
    for worker in ${argument_array[@]}; do
    	printf "Stopping Spark on $worker..."
		ssh $worker "sudo lxc-stop -n spark-container"
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
sed -i '3349s/.*/ \t<stringProp name="projects">'$RUN'<\/stringProp> /' /home/pg1712/apache-jmeter-2.13/bin/play2sdg-datastax-benchmark.jmx


#For more powerfull servers
clients=("5" "10" "50" "100" "200" "300" "400" "500" "600" "700" "800" "900" "1000" "1200" "1500")
#clients=("5" "10" "50" "100" "200" "300" "400" "500")



for (( i=0; i<${#clients[@]}; i++ ));
do
    
	echo 'Updating Jmeter properties to: '${clients[$i]}' clients'
	sed -i '20s/.*/ \t<stringProp name="ThreadGroup.num_threads">'${clients[$i]}'<\/stringProp> /' /home/pg1712/apache-jmeter-2.13/bin/play2sdg-datastax-benchmark.jmx

	echo "Starting Play-Cassandra LXC"
	parallel-ssh -H "$PLAY_WORKERS" "sudo lxc-start -d -n play-container"
	printf "Wait to settle..."
	sleep 40
	printf "done\n"
	
	# Start Spark
	if [ "$SPARK" == "1" ]; then
	    echo "Starting Spark"
	    parallel-ssh -H "$SPARK_WORKERS" "sudo lxc-start -d -n spark-container"
	    printf "Wait Spark to settle..."
	    sleep 60
	    printf "done\n"
	fi

	# run the gathering statistics script on the play workers
	if [ "$STATS" == "1" ]; then
		echo "Starting $STATS_SCRIPT on workers: "$PLAY_WORKERS"..."
		parallel-ssh -H "$PLAY_WORKERS" "cd $SCRIPTS_HOME/ && screen -dm -S 'stats' python $STATS_SCRIPT -i ${IFACE} -f ${STATS_FILENAME}"
	fi
	sleep 1
	

	echo 'Now Running Jmeter with params : '$_jmeter_run $_jmeter_results${clients[$i]}'clients.jtl'
	eval $_jmeter_run $_jmeter_results/${clients[$i]}'clients.jtl'


	echo "Stopping $STATS_SCRIPT on workers: "$PLAY_WORKERS"..."
	parallel-ssh -H "$PLAY_WORKERS" "killall -u $USER -SIGINT python"
	
    #Trick to pass the array by name
    if [ "$SPARK" == "1" ]; then
    	stop_spark_lxc  SPARK_WORKERS
	    sleep 1
	fi
	
	stop_play_lxc   PLAY_WORKERS
	sleep 1


	# collect statistics
	for worker in ${PLAY_WORKERS}; do
		printf "Retrieving stats from $worker..."
		scp $worker:${SCRIPTS_HOME}/${STATS_FILENAME} ${RESULTS}/${STATS_FILENAME}_$worker_${clients[$i]}'clients'
#		scp $worker:${_jmeter_results} ${_jmeter_results}_$worker
		ssh $worker "rm ${SCRIPTS_HOME}/${STATS_FILENAME}"
#		ssh $worker "rm ${_jmeter_results}"
#		ssh $worker 'rm /home/pg1712/play2sdg-1.0-SNAPSHOT/RUNNING_PID'
        printf "done\n"
	done
    
    if [ "$CACHE_CLEAR" == "1" ]; then
	    clear_cache PLAY_WORKERS
	    if [ "$SPARK" == "1" ]; then
    	    clear_cache SPARK_WORKERS
        fi
    fi
    
	echo 'All Done - Sleeping for 1m... '
	sleep 1m
done

echo 'Finished all Jmeter tests!!!!'
