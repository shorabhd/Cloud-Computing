import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Client 
{
	public static Queue<String> queue = null;
	public static Queue<String> rqueue = null;
	public static void main(String args[]) throws Exception
	{
		String fileName=null;
		String nthreads=null;
		Options options = new Options();
		CommandLineParser parser = new DefaultParser();
		options.addOption( "s", true, "Queue Name");
		options.addOption( "w", true, "Workload File");
		options.addOption( "t", true, "Threads");
		try 
		{   
			CommandLine line = parser.parse(options,args);
		    if(line.hasOption("s") && line.hasOption("w") && line.hasOption("t")) 
		    {
		    	fileName = line.getOptionValue("w");
		    	nthreads = line.getOptionValue("t");	        
		    }
		    else
		    {
		    	System.out.println("Please Enter the Arguments");
		    }
		}
		catch(ParseException exp) 
		{
		    System.out.println( "Unexpected exception:" + exp.getMessage() );
		}
		
		ExecutorService executor = Executors.newFixedThreadPool(Integer.parseInt(nthreads));
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		queue = new PriorityQueue<String>();
		queue.clear();
		rqueue = new PriorityQueue<String>();
		rqueue.clear();
		
		long start = System.currentTimeMillis();
		String s = br.readLine();
		
		int lines=0;
		while(s!=null)
		{
			lines++;
			queue.add(s);
			s = br.readLine();
		}
		
		for(int j=0;j<lines;j++)
		{
			Runnable worker = new Worker(queue.poll());
			executor.execute(worker);
		}
		executor.shutdown();
		while (!executor.isTerminated()) 
		{
		}
		long end = System.currentTimeMillis();
		long time = (end-start)/1000;
		System.out.println("Time: "+time+" secs.");
		BufferedWriter bw = new BufferedWriter(new FileWriter("Response.txt"));
		while(!rqueue.isEmpty())
		{
			bw.write(rqueue.poll());
		}
		br.close();
		bw.close();
	}
	static class Worker extends Thread
	{
		String task = null;
		public Worker(String task)
		{
			this.task = task;
		}

		public void run()
		{
			try
			{
				String str[] = task.split(" ");
				Thread.sleep(Long.parseLong(str[1]));
				rqueue.add("0");
			}
			catch(Exception e)
			{
				rqueue.add("1");
			}
		}
	}	
}
