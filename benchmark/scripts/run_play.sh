#!/bin/bash
# Author: pg1712@imperial.ac.uk - pgaref.github.io
# Automating the Play-Spark test process
# -------------------------------------------------

_PORT=9000


_term() { 
  echo "Caught SIGTERM signal!" 
  kill -TERM "$child" 2>/dev/null
}

trap _term SIGTERM

rm /home/pg1712/play2sdg-1.0-SNAPSHOT/RUNNING_PID
exec /bin/sh /home/pg1712/play2sdg-1.0-SNAPSHOT/start -Dlogger.file=/home/pg1712/scripts/play_conf/logger.xml -Dhttp.port=$_PORT &

child=$! 
wait "$child"

echo "Should never Terminate!!"
