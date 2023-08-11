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

PID=`cat ${BASEDIR}/logs/app.pid`

if ps -p ${PID} > /dev/null
then
    forceKillPid ${PID}
fi

rm -f ${BASEDIR}/logs/app.pid

exit 0