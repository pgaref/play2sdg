#!/bin/bash
MASTER="wombat02.doc.res.ic.ac.uk"
SOURCE="possum.doc.res.ic.ac.uk"
WORKERS=( "wombat04.doc.res.ic.ac.uk"
          "wombat03.doc.res.ic.ac.uk"
          "wombat02.doc.res.ic.ac.uk"
          "wombat01.doc.res.ic.ac.uk"
)

function get_worker_cmd {
  echo "$1 worker-$2" >> deploy.log
  echo "tmux set-option -g history-limit 100000 \\; new -d -s worker-$2 'software/jdk1.8.0_51/bin/java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=1044 $(jvm_args $1) -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=9010 -Dcom.sun.management.jmxremote.port=9010 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -jar seep-worker-0.1.jar --master.ip $(ip_for $MASTER) --data.port 460$2 --worker.port 360$2 --wait.time.inputadapter.ms 0 --metrics.report.console.period $REPORT_PERIOD --app.batch.size 1 --batch.size 5000'"

}

function jvm_args {
  if [ "$1" == "wombat11.doc.res.ic.ac.uk" ] || [ "$1" == "wombat12.doc.res.ic.ac.uk" ]  || [ "$1" == "wombat13.doc.res.ic.ac.uk" ] || [ "$1" == "wombat14.doc.res.ic.ac.uk" ] || [ "$1" == "wombat15.doc.res.ic.ac.uk" ] || [ "$1" == "wombat01.doc.res.ic.ac.uk" ] || [ "$1" == "wombat02.doc.res.ic.ac.uk" ] || [ "$1" == "wombat03.doc.res.ic.ac.uk" ] || [ "$1" == "wombat04.doc.res.ic.ac.uk" ] || [ "$1" == "wombat05.doc.res.ic.ac.uk" ]; then
    echo "-Xmx2g -Xms2g"
  elif [ "$1" == "wombat06.doc.res.ic.ac.uk" ] || [ "$1" == "wombat07.doc.res.ic.ac.uk" ] || [ "$1" == "wombat08.doc.res.ic.ac.uk" ] || [ "$1" == "wombat09.doc.res.ic.ac.uk" ] || [ "$1" == "wombat10.doc.res.ic.ac.uk" ]; then
    echo "-d64 -Xmx3g -Xms3g"
  else
    echo "-d64 -Xmx7g -Xms7g"
  fi
}



