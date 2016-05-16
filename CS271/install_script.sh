#!/bin/bash
sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv EA312927
echo "deb http://repo.mongodb.org/apt/ubuntu precise/mongodb-org/3.2 multiverse" | sudo tee /etc/apt/sources.list.d/mongodb-org-3.2.list
sudo apt-get update
sudo apt-get install -y mongodb-org
export LC_ALL=C

apt-get install software-properties-common
sudo apt-get install python-software-properties
sudo add-apt-repository ppa:openjdk-r/ppa
sudo apt-get update 
sudo apt-get install openjdk-8-jdk openjdk-8-jre