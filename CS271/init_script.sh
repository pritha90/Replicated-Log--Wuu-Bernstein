#!/bin/bash
java -jar clean.jar
kill -9 $(ps aux | grep "java -jar trial.jar" | awk 'NR==1{print $2}')
nohup java -jar trial.jar > log.log 2>&1 &