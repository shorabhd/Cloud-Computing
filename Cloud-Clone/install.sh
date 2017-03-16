#!/bin/bash

#JAVA
sudo add-apt-repository ppa:webupd8team/java
sudo apt-get update
sudo apt-get install oracle-java8-installer

#AWSCLI
sudo apt-get install awscli

#AWS_SDK_JAVA
sudo wget http://sdk-for-java.amazonwebservices.com/latest/aws-java-sdk.zip
sudo apt-get install zip
unzip aws-java-sdk.zip

#Apache Common CLI's Jar
sudo wget http://www-us.apache.org/dist//commons/cli/binaries/commons-cli-1.3.1-bin.tar.gz
sudo tar -xzvf commons-cli-1.3.1-bin.tar

#EXPORT .bashrc
sudo echo export CLASSPATH=.:/home/ubuntu/aws-java-sdk-1.10.74/lib/aws-java-sdk-1.10.74.jar:/home/ubuntu/aws-java-sdk-1.10.74/third-party/lib/*:/home/ubuntu/commons-cli-1.3.1/commons-cli-1.3.1.jar >> ~/.bashrc
