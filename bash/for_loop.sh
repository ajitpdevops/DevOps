#!/bin/bash

cd ./input

PICTURES=$(ls *jpg)
DATE=$(date +%F)

for PICTURE in $PICTURES
do 
	echo "Renaming ${PICTURE} to ${DATE}-${PICTURE}"
	mv ${PICTURE} ${DATE}-${PICTURE}
done


for COLOR in red gree blue orange
do
	echo "COLOR : ${COLOR}"
done

