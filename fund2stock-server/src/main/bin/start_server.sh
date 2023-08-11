#!/bin/bash

ENVIRONMENT="sep"
DEBUG_OPTS=""
JAR_NAME="fundx-superconversion-server.jar"

function usage() {
  echo ""
  echo "./start_server.sh"
  echo "\t-h --help"
  echo "\t-e --env sep or production"
  echo ""
}

if [ $# = 0 ]; then
  echo "no arguments provided."
  usage
  exit 1
fi

while [ "$1" != "" ]; do
  PARAM=$(echo $1 | awk -F= '{print $1}')
  VALUE=$(echo $1 | awk -F= '{print $2}')
  case ${PARAM} in
  -h | --help)
    usage
    exit
    ;;
  -e | --env)
    ENVIRONMENT=${VALUE}
    ;;
  --debug)
    #加入 debug 模式
    DEBUG_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005"
    ;;
  *)
    BYPASS_PARAMS="$BYPASS_PARAMS $PARAM"
    ;;
  esac
  shift
done

bin=$(dirname "$0")
BASEDIR=$(
  cd "$bin/.."
  pwd
)

export BASEDIR

cd ${BASEDIR}/bin

source ./common.sh

cd ${BASEDIR}

#CLASSPATH=${BASEDIR}/conf/${ENVIRONMENT}/:${CLASSPATH}

RPC_HEAP_OPTS="-Xmx4g -Xms4g"

if [ ${ENVIRONMENT} = "release" ] || [ ${ENVIRONMENT} = "production" ]; then
  RPC_HEAP_OPTS="-Xms8g -Xmx8g"
fi

if [ ${ENVIRONMENT} = "sep" ] || [ ${ENVIRONMENT} = "sit" ] || [ ${ENVIRONMENT} = "staging" ]; then
  DEBUG_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8000"
fi

SPRING_PROFILE_EVN=" --spring.profiles.active="${ENVIRONMENT}

#APM
AGENT_ENV="sep"
if [[ ${CONTAINER_ENV} == "production" ||  ${CONTAINER_ENV} == "rc" || ${CONTAINER_ENV} == "release" || ${CONTAINER_ENV} == "staging" ]];then
    AGENT_ENV="production"
    SENTRY_OPTS=" -Dsentry.dsn=http://f2896b53316c41b9a9b09187f77fdbdf@sentry.snowballfinance.com/8 -Dsentry.environment=${CONTAINER_ENV}"

fi

if [  -f "/data/deploy/agent/apm-agent.jar" ];then
  AGENT_OPTIONS=" -javaagent:/data/deploy/agent/apm-agent.jar=agent.service_name=danjuan-uc-${CONTAINER_ENV}::${CONTAINER_PROJ}  -Dapm_config=/config/agent-${AGENT_ENV}.properties"
fi

echo "starting server..."

JAR_NAME=${BASEDIR}"/"${JAR_NAME}

nohup ${JAVA} ${SENTRY_OPTS} ${AGENT_OPTIONS} ${JAVA_OPTS} ${RPC_HEAP_OPTS} ${GC_OPTS} ${DEBUG_OPTS} -jar ${JAR_NAME} ${SPRING_PROFILE_EVN} -Dlog.home=${LOG_DIR} ${BYPASS_PARAMS} > ${LOG_DIR}/console.log 2>&1 &

if [ $? -eq 0 ]; then
  echo $! >${BASEDIR}/logs/app.pid
else
  echo "failed to start server"
fi
