Steps to perform before running the code:

1. Please run "install.sh" first, which is a script to setup Java and AWS and add the required Jars. 
   Since Jars are big in size, I have used "wget" in the above script to fetch online.

2. Configure AWS Credentials by giving the following command and Enter the required information when prompted,

	aws configure
	
Steps to run the code:

1.	Complete the environment setup as mentioned above.

2.	Create the 17 workload files for the experiments (3 files for each thread (15), Local Throughput file (15,000K ops), Remote throughput file (10,000 ops)).

3.	Run the Client code for local throughput by varying number of threads (1,2,4,8 and 16)

Javac Client.java (in local folder)
Java Client -s LOCAL -w <WORKLOAD FILE> -t <THREADS> 

4.	Now, run the client code for local efficiency (10ms) with varying number of threads (1,2,4,8 and 16)

Java Client -s LOCAL -w <WORKLOAD FILE for Efficiency> -t 1
Java Client -s LOCAL -w <WORKLOAD FILE for Efficiency> -t 2
Java Client -s LOCAL -w <WORKLOAD FILE for Efficiency> -t 4
Java Client -s LOCAL -w <WORKLOAD FILE for Efficiency> -t 8 
Java Client -s LOCAL -w <WORKLOAD FILE for Efficiency> -t 16

5.	Repeat step 4 for other two experiments (1000ms and 10000ms)
6.	Now for remote, start instances for workers
7.	Run Client on original instance and Worker on worker instances simultaneously. Perform throughput experiment by varying no. of threads 

Javac Client.java (in Remote folder)
Java Client -s <QNAME> -w<WORKLOAD FILE>

Javac Worker.java
Java Worker -s <QNAME> -t <THREADS>

<THREADS> will be 1 for remote experiments.

8.	Run the above experiments for efficiency by changing the workload file



