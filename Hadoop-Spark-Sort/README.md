# CS553-Hadoop-Spark-Sort
Cloud Assignment 2

Steps to run the code:

1.Create dataset file using Gensort

sudo time ./gensort -a [FILE SIZE] New1

2.Edit SharedMemorySort.java to change the no. of Files and Threads. Also change the path of input and output file.(Line no. 17,33,92)

vi SharedMemorySort.java 

3.Compile and run SharedMemorySort.java

Javac SharedMemorySort.java

Java SharedMemorySort

4.After Creating the Hadoop Cluster, follow below commands to format namenode(On all nodes if EBS is mounted) and start all nodes (only on Master Node).

/hadoop/bin/ 

./hdfs namenode -format

/hadoop/sbin/

./start-dfs.sh

./start-yarn.sh

jps --> To Check whether all nodes are running

4.Create jar file for HadoopSort.java.(HadoopSort.jar given)

5.Put the dataset in HDFS

hadoop fs -put [Source] [HDFS Location]

6.Run Jar

hadoop jar HadoopSort.jar HadoopSort [DATASET HDFS SOURCE] [DATASET HDFS DEST] 

7.Get the dataset to local system

hadoop fs -get [HDFS SOURCE] [LOCAL DEST]

8.Validate DATASET

/64/

sudo time ./valsort DATASET

9.After Creating SPARK Cluster, Goto Spark Shell of Master Node

/spark/bin/

./spark-shell

Run below commands one by one in Shell:

val input = sc.textFile("[HDFS DATASET SOURCE]")

val data = input.flatMap(line => line.split("\n")).map(line => (line.substring(0,10),line.substring(10,98))).sortByKey().map{case(k,v)=>k+v+" "}

data.saveAsTextFile("HDFS DATASET LOCATION")

10.For Spark in Java, run this command from Master node:

/spark/bin/

./spark-submit --class [Main Class in Jar] --master [master-ip] [Jar File] [HDFS INPUT DATASET] [HDFS OUTPUT DATASET]

11.To get the First 10 and Last 10 Lines of Output File.

head -10 [Output File]

tail -10 [Output File]

Note: install.sh for installation.
