#!/bin/bash

#JAVA
sudo add-apt-repository ppa:webupd8team/java
sudo apt-get update
sudo apt-get install oracle-java8-installer

#GENSORT
sudo wget http://www.ordinal.com/try.cgi/gensort-linux-1.5.tar.gz
sudo tar -xzvf gensort-linux-1.5.tar.gz

#HADOOP
sudo wget http://apache.mirror.gtcomm.net/hadoop/common/hadoop-2.7.2/hadoop-2.7.2.tar.gz 
sudo tar -xzvf hadoop-2.7.2.tar.gz
mv hadoop-2.7.2 hadoop

#SPARK
sudo wget http://www-eu.apache.org/dist/spark/spark-1.6.1/spark-1.6.1-bin-hadoop2.6.tgz
sudo tar -xzvf spark-1.6.1-bin-hadoop2.6.tgz
mv spark-1.6.1-bin-hadoop2.6 spark

#PERMISSIONS
sudo chown -R ubuntu 64
sudo chown -R ubuntu /hadoop
sudo chown -R ubuntu /spark

#PEM FILE
eval `ssh-agent -s`
chmod 600 NewHadoop.pem
ssh-add NewHadoop.pem

