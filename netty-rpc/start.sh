#!/bin/sh
#ADATE=`date +%Y%m%d%H%M%S`
PRGDIR=`pwd`
dirname $0 | grep "^/" >/dev/null

LOGDIR=$PRGDIR/logs
if [ ! -d "$LOGDIR" ]; then
        mkdir "$LOGDIR"
fi

# SIGTERM-handler  graceful-shutdown
pid=0
process_exit() {
 if [ $pid -ne 0 ]; then
  kill -SIGTERM "$pid"
  wait "$pid"
 fi

for fluentPid in $(pgrep -f fluent-bit)
 do
    kill -SIGTERM "$fluentPid"
    wait "$fluentPid"
 done

 exit 143; # 128 + 15 -- SIGTERM
}


trap 'kill ${!};process_exit' SIGTERM

echo $JAVA_OPTS > $LOGDIR/console.log

nohup java -server  -jar $PRGDIR/app.jar >> $LOGDIR/console.log 2>&1 &
pid="$!"

#echo $pid > $LOGDIR/pid.txt

wait $pid
