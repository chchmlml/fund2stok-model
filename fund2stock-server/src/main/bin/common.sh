#!/bin/bash

LOG_DIR="${BASEDIR}/logs"

mkdir -p ${LOG_DIR}

nowday=`date +%Y%m%d_%H%M%S`
GC_LOG_DIR=${BASEDIR}/logs/gclogs
test -d $GC_LOG_DIR || mkdir $GC_LOG_DIR
NUM=`ls -latr $GC_LOG_DIR | wc -l`
if [ ${NUM} -gt 31 ]; then
    cd $GC_LOG_DIR
    DELETENUM=$((NUM-31))
    ls -latr | awk '{print $9}' | head -n ${DELETENUM} | xargs rm -rf {}
    cd -
fi

GC_OPTS="-verbosegc -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintGCDateStamps -XX:+UseG1GC -Xloggc:${GC_LOG_DIR}/gc.log.${nowday}"
JAVA_OPTS="-server -d64 -Dfile.encoding=UTF8 -Djava.net.preferIPv4Stack=true"

if [ -z "$JAVA_HOME" ]; then
  JAVA="java"
else
  JAVA="$JAVA_HOME/bin/java"
fi

