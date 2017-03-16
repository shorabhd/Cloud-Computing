# CS553-Benchmarking
Cloud Assignment 1

Steps to run the code:
1.Create 30MB file “disk_bench.txt” for disk benchmarking 

2.Place all the Java files at the same place from where the script is running

3.Run the script.sh file for CPU and Disk Benchmark.

4.Compile and run CPUBench2.java to perform second experiment of CPU

Javac CPUBench2.java
Java CPUBench2 >>"output_filename.txt"

5.Create 30MB file “input.txt” for network benchmarking and two blank files server.txt and output.txt for Network read write.
6.Give the server ipaddresses in NetworkClient.java(Line 32 & 96) and client’s ipaddress in NetworkServer.java(Line 99)
7.Compile NetworkServer.java and NetworkClient.java in two separate instances

Javac NetworkServer.java
Javac NetworkClient.java

8.Run the NetworkServer.java and NetworkClient.java in two separate instances by varying no. of threads

Java NetworkServer 1
Java NetworkClient 1
Java NetworkServer 2
Java NetworkClient 2
