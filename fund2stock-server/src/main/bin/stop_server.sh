#!/bin/bash

bin=`dirname "$0"`
BASEDIR=`cd "$bin/.."; pwd`
export BASEDIR

cd ${BASEDIR}/bin

function killpid()
{
  if [ ! -z "$*" ] ; then
	echo "kill $*"
    kill $*
  fi
}

function forceKillPid(){
   if [ ! -z "$*" ] ; then
      echo "force kill $*"
      kill -9 $*
    fi
}

cd ${BASEDIR}

#PID=`jps -l | grep "${PROCESS_NAME}"| awk '{print $1}'`
PID=`cat ${BASEDIR}/logs/app.pid`

if ps -p ${PID} > /dev/null
then
    killpid ${PID}
    sleep 10
    if ps -p ${PID} > /dev/null
    then
       forceKillPid ${PID}
    fi
fi

#remove app.pid
rm -f ${BASEDIR}/logs/app.pid

exit 0