import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

public class DiskBench 
{
	public static long nano = 1000000000;
	public static int bsize[] = {1,1024,1024*1024};
	public static double rwrite,rread,swrite,sread;
	public static void main(String[] args) throws Exception
	{
		int nthreads = Integer.parseInt(args[0]);
		Thread th[] = new Thread[nthreads];
		for(int i=0;i<bsize.length;i++)
		{
			for(int j=0;j<nthreads;j++)
			{
				th[j] = new Nthread(bsize[i]);
				th[j].start();
			}
		}
		
	}
	
	static class Nthread extends Thread 
	{
		double start,end;
		int bsize;
		public Nthread(int bsize)
		{
			this.bsize = bsize;
		}
		public void run()  
		{
			String fileName = "disk_bench.txt"; //To read and write data
			File fileObject = new File(fileName);
			try
			{
				randWrite(fileName,bsize);	//randRead() is called from inside
				seqWrite(fileObject,bsize);	//seqRead() is called from inside
			}
			catch(Exception e)
			{
				System.out.println(e);
			}
		}

		//Random Write
		public void randWrite(String fileName, int bsize) throws IOException 
		{
			byte b[] = new byte[bsize];
			for(int i=0;i<bsize;i++)
				b[i]='a';
			start = System.nanoTime();
			RandomAccessFile raf = new RandomAccessFile(fileName, "rw");
			raf.seek(raf.length());
			raf.write(b);
			end = System.nanoTime();
			rwrite = (end-start)/nano;
			System.out.println("Random Write for block size "+bsize+" is: "+(bsize/rwrite)/(1024*1024)+" MBps");
			System.out.println("Latency of Random Write for block size "+bsize+" is: "+rwrite*1000+" ms");
			randRead(raf,bsize);
			raf.close();
		}

		//Random Read
		public void randRead(RandomAccessFile raf, int bsize) throws IOException 
		{
			start = System.nanoTime();
			for(int i=0;i<bsize;i++) 
			{
				raf.seek(1000+i);
				raf.read();
			}
			end = System.nanoTime();
			rread = (end-start)/nano;
			System.out.println("Random Read for block size "+bsize+" is: "+(bsize/rread)/(1024*1024)+" MBps");
			System.out.println("Latency of Random Read for block size "+bsize+" is: "+rread*1000+" ms");
			raf.close();
		}

		//Sequential Write
		public void seqWrite(File fileName, int bsize) throws IOException 
		{
			byte b[] = new byte[bsize];
			for(int i=0;i<bsize;i++)
				b[i]='a';
			start = System.nanoTime();
			FileOutputStream br = new FileOutputStream(fileName,true);
			br.write(b);
			end = System.nanoTime();
			swrite = (end-start)/nano;
			System.out.println("Sequential Write for block size "+bsize+" is: "+(bsize/swrite)/(1024*1024)+" MBps");
			System.out.println("Latency of Sequential Write for block size "+bsize+" is: "+swrite*1000+" ms");
			seqRead(fileName,bsize);
		}

		//Sequential Read
		public void seqRead(File fileName,int bsize) throws IOException 
		{
			start = System.nanoTime();
			byte b[] = new byte[bsize];
			FileInputStream br = new FileInputStream(fileName);
			br.read(b);
			end = System.nanoTime();
			sread = (end-start)/nano;
			System.out.println("Sequential Read for block size "+bsize+" is: "+(bsize/sread)/(1024*1024)+" MBps");
			System.out.println("Latency of Sequential Read for block size "+bsize+" is: "+sread*1000+" ms");
		}
	}
}