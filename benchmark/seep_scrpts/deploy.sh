#!/bin/bash 
. nodes.sh

USER="pg1712"

if [[ -z "$1" || -z "$2" || -z "$3" ]]; then 
  echo usage: $0 trender_num sloper_num selector_num
  exit
fi

if [ -e "deploy.log" ]; then
  echo deploy.log file exists, exiting 
  exit
fi

touch deploy.log

function ip_for {
  echo `dig +short $1`
}

function get_source_worker_cmd {
  echo "$1 worker-$2" >> deploy.log
  echo "tmux new -d -s worker-$2 '/home/vlopezmo/software/jdk1.8.0_51/bin/java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=1044 -Xmx8g -Xms8g -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=9010 -Dcom.sun.management.jmxremote.local.only=false -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false  -jar seep-worker-0.1.jar --master.ip $(ip_for $MASTER) --data.port 460$2 --worker.port 360$2 --wait.time.inputadapter.ms 0 --metrics.report.console.period $REPORT_PERIOD --metrics.report.jmx 1 --batch.size 5000 --app.batch.size 1'"
}

QUERY_FILE="detectTrendyVms-1.0-SNAPSHOT.jar"
BASECLASS_NAME="Base"
#DATA_DIR="/data/datasets/vmperf"

#CLUSTER="fakeCluster4"

#PREDICTIONATTNAME="cpu.ready.sum_"
#NORMALIZATION="yes"
#FEATURETRANSFORMATION="none"
#SMOOTHING=1
#FEATURESET="pred-only"
#PREDICTION="SMO"

#PREDICTION="ph-vm"
TRENDER_OPS=$1
SLOPER_OPS=$2
SELECTOR_OPS=$3
REPORT_PERIOD=1800
#RETRAIN_FREQUENCY=2160
#MVIMPUTATION_SIZE=30
#IGNORE_EVALUATION=4
#NUM_ATTRIBUTES=3
#WINDOW_SIZE=4320
#KNN=11
#SVM_OPTIONS=11
#LAG=4

MASTER_CMD="tmux new -d -s master 'software/jdk1.8.0_51/bin/java -jar seep-master-0.1.jar --query.file $QUERY_FILE --baseclass.name $BASECLASS_NAME'"

#MASTER_CMD="tmux new -d -s master 'software/jdk1.8.0_51/bin/java -jar seep-master-0.1.jar --query.file $QUERY_FILE --baseclass.name $BASECLASS_NAME --data.dir $DATA_DIR --predictionAtt.name $PREDICTIONATTNAME --prediction $PREDICTION --featureSet $FEATURESET --normalization $NORMALIZATION --featureTransformation $FEATURETRANSFORMATION --smoothing $SMOOTHING --window.size $WINDOW_SIZE --mvimputation.size $MVIMPUTATION_SIZE --k $KNN --lag $LAG --vmToHost.path $VM_TO_HOST_PATH --lineSplitter.ops $LINE_SPLITTER_OPS --preprocessor.ops $PRE_PROCESSOR_OPS --processor.ops $PROCESSOR_OPS --evaluator.ops $EVALUATOR_OPS --ignore.evaluation $IGNORE_EVALUATION --svm.parameters $SVM_OPTIONS --retrain.frequency $RETRAIN_FREQUENCY --num.attributes $NUM_ATTRIBUTES --cluster $CLUSTER'"

NUM_REQUIRED_WORKERS=$(($TRENDER_OPS + $SLOPER_OPS + $SELECTOR_OPS + 1))
NUM_WORKERS=${#WORKERS[@]}

#if [ "$NUM_REQUIRED_WORKERS" -gt "$NUM_WORKERS" ]; then
#  echo Not enough workers available: $NUM_REQUIRED_WORKERS needed, $NUM_WORKERS available
#  exit
#else
#  if [ "$NUM_REQUIRED_WORKERS" -lt "$NUM_WORKERS" ]; then
#    echo Too many workers available, some of them are going to be wasted: $NUM_REQUIRED_WORKERS needed, $NUM_WORKERS available. Please try to call this script with a different number of operators.
#    exit
#  fi
#fi

echo "$MASTER master" >> deploy.log
ssh $USER@$MASTER "$MASTER_CMD"

sleep 1

# For one worker per machine
for i in `seq 0 $((NUM_WORKERS - 1))`; do
  worker=${WORKERS[i]}
  ssh $USER@$worker "$(get_worker_cmd $worker 1)"
  echo $worker `ssh $USER@$worker "tmux ls"`
  sleep 5
done

sleep 10

ssh $USER@$SOURCE "$(get_source_worker_cmd $SOURCE 1)"
echo $SOURCE `ssh $USER@$SOURCE "tmux ls"`

sleep 5

ssh $USER@$MASTER "tmux send-keys -t master '1' Enter"

sleep 5

ssh $USER@$MASTER "tmux send-keys -t master '2' Enter"


