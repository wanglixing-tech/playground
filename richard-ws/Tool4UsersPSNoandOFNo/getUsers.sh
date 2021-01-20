#!/usr/bin/bash

#set -x

PWD=$(pwd)

while IFS='' read -r line || [[ -n "$line" ]]; do
	userId="$(echo -e "${line}" | sed -e 's/^[[:space:]]*//' -e 's/[[:space:]]*$//')"
	if [[ ! "$userId" =~ ^\#.* ]]; then 
    		echo "Text read from file: $userId"
		fi
done < "$PWD/userList.properties"
