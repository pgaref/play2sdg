#!/bin/bash
# Author: pg1712@imperial.ac.uk - pgaref.github.io
# Automating the Play-Spark test process
# -------------------------------------------------

## Spark server name ##
_rserver="wombat30.doc.res.ic.ac.uk"
_ruser="pg1712"
#_spark_kill= "/bin/ps -ef | /bin/grep spark | /usr/bin/awk '{print \$2}' | xargs -t -i kill {}"
#_spark_kill_old="for var in $(ps aux | grep spark | awk '{print $2}'); do echo \"Proccess:\"$var; sudo kill -9 $var; done"

#Single Node Case
_spark_run="cd spark-1.1.0/ ; ./bin/spark-submit --master local[*] --driver-memory 1g --class main.java.uk.ac.imperial.lsds.play2sdg.SparkCollaborativeFiltering ../play2sdg-Spark-module-0.0.1-SNAPSHOT-driver.jar"
#Multiple Nodes Manages by Mesos Case
#_spark_run="cd spark-1.1.0/ ; ./bin/spark-submit --master mesos://wombat30.doc.res.ic.ac.uk:5050 --driver-memory 1g --class main.java.uk.ac.imperial.lsds.play2sdg.SparkCollaborativeFiltering ../play2sdg-Spark-module-0.0.1-SNAPSHOT-driver.jar"

_jmeter_run="sh apache-jmeter-2.13/bin/jmeter.sh -n -t apache-jmeter-2.13/bin/play-Login-Recc-Logout-benchmarkMode.jmx -l"
_jmeter_results="./stacked_bars_data/wombat30_single_cpustress_mem_io/"

#Change jmeter Clients number
#sed 'ThreadGroup.num_threads c\<stringProp name="ThreadGroup.num_threads">400</stringProp>' apache-jmeter-2.13/bin/play-Login-Recc-Logout-benchmarkMode.jmx

#for (( COUNTER=5; COUNTER<=300; COUNTER+=5 )); 
#do

clients=("5" "10" "20" "40" "80" "100" "120" "140" "180" "200" "225" "250" "300")

for (( i=0; i<${#clients[@]}; i++ ));
do
        echo 'Running benchmark with' ${clients[$i]} ' clients'

	echo 'Updating Jmeter properties...'
	sed -i '20s/.*/ \t<stringProp name="ThreadGroup.num_threads">'${clients[$i]}'<\/stringProp> /' /home/pg1712/apache-jmeter-2.13/bin/play-Login-Recc-Logout-benchmarkMode.jmx
#	sed -i '453s/.*/ \t<stringProp name="project">Isolated_'$COUNTER'<\/stringProp> /' /home/pg1712/apache-jmeter-2.13/bin/play-Login-Recc-Logout-benchmarkMode.jmx	

#	echo "Killing Spark possible leftovers.."
#	/usr/bin/ssh ${_ruser}@${_rserver} "/bin/ps -ef | /bin/grep spark | /usr/bin/awk '{print \$2}' | xargs -t -i kill {}"

#	echo 'Running Spark CF job in the background...'
#	nohup /usr/bin/ssh ${_ruser}@${_rserver} "$_spark_run" &

	echo 'Now Running Jmeter.. : '$_jmeter_run $_jmeter_results${clients[$i]}'clients.jtl'
	eval $_jmeter_run $_jmeter_results${clients[$i]}'clients.jtl'

#	 echo "Killing Spark possible leftovers.."
#        /usr/bin/ssh ${_ruser}@${_rserver} "/bin/ps -ef | /bin/grep spark | /usr/bin/awk '{print \$2}' | xargs -t -i kill {}"

	echo 'All Done - Sleeping for 1m... '
	sleep 1m
done

echo "Finished all Jmeter tests!!!!"
