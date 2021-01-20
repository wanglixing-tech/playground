#!/usr/bin/env bash
###############################################################################
## Created by Richard Wang
## Created on Sep. 14, 2018
## ###########################################################################
#set -x
finalRet=0;
QM_NAME=$1
if [ -z $QM_NAME ] 
then
        echo "A Queue Manager name is manadatory."
	exit 1
 else
        echo "Your target Queue Manager Name is: $QM_NAME"
fi

export LD_LIBRARY_PATH=/opt/mqm/java/lib64:$LD_LIBRARY_PATH

RESULT=$( dspmq -m $QM_NAME | sed 's/QMNAME(.*)\s*STATUS(\(.*\))/\1/' )
if [[ ! $RESULT =~ Running ]] 
then
	echo "WebSphere MQ queue manager not available."
	finalRet=1
else
	RESULT=$( java -jar mqbrowse3.jar $QM_NAME | grep -A 1 -B 2 "MQRC_OBJECT_DAMAGED") 
        #echo $RESULT
	if [[ $RESULT =~ MQRC_OBJECT_DAMAGED ]] 
	then
		echo $RESULT | sed -e 's/\!\!\!\!\!\s/\n/g' 
		finalRet=1
	else
		echo "Did not find damaged queue objects under the queue manager $QM_NAME"
		finalRet=0
	fi
fi
exit $finalRet

