#!/bin/bash
# Author: pg1712@imperial.ac.uk - pgaref.github.io
# Automating the Play-Spark test process
# -------------------------------------------------

_PORT=9000


_term() { 
  echo "Caught SIGTERM signal!" 
  kill -TERM "$child" 2>/dev/null
}

#trap _term SIGTERM
/root/virtual_share/apache-cassandra-2.1.9/bin/cassandra -p /root/virtual_share/apache-cassandra-2.1.9/casspid
sleep 20
rm /root/virtual_share/play2sdg-1.0-SNAPSHOT/RUNNING_PID
nohup /bin/sh /root/virtual_share/play2sdg-1.0-SNAPSHOT/start -Dhttp.port=$_PORT -Dlogger.file=/root/virtual_share/scripts/play_conf/logger.xml &

#child=$! 
#wait "$child"

#echo "Should never Terminate!!"
