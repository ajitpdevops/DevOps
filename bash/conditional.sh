#!/bin/bash

read -p "Enter you age: " AGE

echo "Your age is ${AGE}"

if [ ${AGE} -gt 18 ]
then
	echo "You are an adult and welcome inside"
elif [ ${AGE} -eq 18 ]
then
	echo "You are almost there, come back to celebrate your next birthday"
else
	echo "Go to mother's lap kiddo!"
fi

HOST="google.com"

ping -c 1 ${HOST}

if [ "$?" -eq "0" ]
then
	echo "${HOST} is reachable"
else
	echo "${HOST} is not reachable"
fi