#!/bin/bash
# #############################################
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
# #############################################

LOCAL_HOST=$1
SSH_LOGIN=$2
TESTING_HOST=$3

if [ -z "$3" ];then
  echo "Usage: $0 LOCAL_HOST_IP REMOTE_SSH_LOGIN REMOTE_HOST_IP"
  echo "Script requare installed 'iperf' on both local and remote hosts."
  echo "Also need setting up ssh login without requiring a password. Use for this purpose ssl certificates."
  exit 0;
fi

TEST_LOCAL_IPERF=`iperf 2>&1 | grep "Usage: iperf" |wc -l`
if [ "1" != "$TEST_LOCAL_IPERF" ];then
  echo "Script requare installed 'iperf' on local host."
  exit 0;
fi

TEST_REMOTE_IPERF=`ssh $SSH_LOGIN@$TESTING_HOST "iperf 2>&1 | grep \"Usage: iperf\" |wc -l"`
if [ "1" != "$TEST_REMOTE_IPERF" ];then
  echo "Script requare installed 'iperf' on remote host."
  exit 0;
fi

test_jitter_with_ping (){
## $1 - size of packets in bites
## $2 - count of send packets

COUNT_PACKETS=1000
if [ -z "$1" ]; then
  ping -c 1000 -f -s $1 $TESTING_HOST 2>&1 >${1}b.out.txt
else
  ping -c $2 -f -s $1 $TESTING_HOST 2>&1 >${1}b.out.txt
  COUNT_PACKETS=$2
fi

MIN=`grep "rtt min" ./${1}b.out.txt | cut -d " " -f4 | cut -d "/" -f1 | cut -d "." -f1`
AVG=`grep "rtt min" ./${1}b.out.txt | cut -d " " -f4 | cut -d "/" -f2 | cut -d "." -f1`
MAX=`grep "rtt min" ./${1}b.out.txt | cut -d " " -f4 | cut -d "/" -f3 | cut -d "." -f1`

let "JITTER=($MAX-$AVG)-($AVG-$MIN)"
if [ "$JITTER" -lt "0" ];then
    let "JITTER=-$JITTER"
fi
echo "Jitter = $JITTER for $COUNT_PACKETS packets with size $1 bites (ping)"
echo "Max latency is $MAX"
if [ "$AVG" != "0"  ]; then
  let "JITTERPS=$JITTER*100/$AVG"
  echo "Jitter = $JITTERPS % from $AVG ms"
fi

}

test_opened_ports_with_iperf (){
## Test opened ports
echo "Start testing opened ports"
echo "========================================"
PORTS=$1 #"5080 1935 8088"
AVAIL_PORTS=""
rm server_port_log.txt
for i in $PORTS; do
  #echo "ssh $SSH_LOGIN@$TESTING_HOST \"iperf -s -p $i\""
  ssh $SSH_LOGIN@$TESTING_HOST "iperf -s -p $i" >>server_port_log.txt 2>&1 &
  sleep 2
  #echo "telnet -e q $TESTING_HOST $i <telnet_commands.txt"
  TELNET_OUT=`echo -e "q\nquit"|telnet -e q $TESTING_HOST $i`
  #echo "ssh $SSH_LOGIN@$TESTING_HOST \"ps -C iperf|cut -d \" \" -f1 | xargs kill -9\""
  ssh $SSH_LOGIN@$TESTING_HOST "ps -C iperf|cut -d \" \" -f1 | xargs kill -9" >>server_port_log.txt 2>&1 &
  PORT_IS_AVAILABLE=`echo "$TELNET_OUT" | grep "Connected to"`
  if [ "$PORT_IS_AVAILABLE" != "" ];then
    echo "Port $i available"
    AVAIL_PORTS="$AVAIL_PORTS $i"
  else
    echo "Port $i not available"
  fi
  sleep 2
done

}


test_bandwidth_with_iperf (){
## Test bandwidth of server
echo "Start testing bandwidth of server"
echo "========================================"
  PORT=$1
  TIME_TO_TEST=30
  NUM_THREADS=10
  REPORT_INTERVAL=5
  #Download test
  echo "Start outbound test"
  #setup server
  #echo "Starting server..."
  ssh $SSH_LOGIN@$TESTING_HOST "iperf -s -p $PORT" >server_log.txt 2>&1 &
  #wait when server started
  sleep 2
  #start test
  #echo "Start test"
  iperf -c $TESTING_HOST -p $PORT -i $REPORT_INTERVAL -P $NUM_THREADS -t $TIME_TO_TEST | tee client_log.txt | grep "SUM"
  #stop server
  echo "Stop server"
  ssh $SSH_LOGIN@$TESTING_HOST "ps -C iperf|cut -d \" \" -f1 | xargs kill -9" >>server_log.txt 2>&1 &
  echo "End outbound test"
  sleep 2
  echo
  #Upload test
  #Comment: This we can use any port for testing, I use 12100
  PORT=12100
  echo "Start inbound test"
  #setup server
  #echo "Starting server..."
  iperf -s -p $PORT >local_server_log.txt 2>&1 &
  #wait when server started
  sleep 2
  #start test
  #echo "Start test"
  ssh $SSH_LOGIN@$TESTING_HOST "iperf -c $LOCAL_HOST -p $PORT -i $REPORT_INTERVAL -P $NUM_THREADS -t $TIME_TO_TEST" | tee remote_client_log.txt | grep "SUM"
  #stop server
  echo "Stop server"
  ps -C iperf | cut -d " " -f1 | xargs kill -9 >local_server_log.txt 2>&1 &
  echo "End inbound test"
}

echo "Start testing latency and jitter"
echo "========================================"
test_jitter_with_ping 1000 100
test_jitter_with_ping 1000 1000
test_jitter_with_ping 10000 1000
#Hard test
#test_jitter_with_ping 1024 100000
echo

test_opened_ports_with_iperf "5080 1935 8088"
#echo "$AVAIL_PORTS"
echo

test_bandwidth_with_iperf 1935

## generate file with random content and size 1Mb
#dd if=/dev/urandom of=test.log bs=1k count=1024



