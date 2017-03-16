public class CPUBench1
{
	public static void main(String[] args)
	{
		int nthreads = Integer.parseInt(args[0]);
		Thread t[] = new Thread[nthreads];
		for(int i=0;i<nthreads;i++)
		{
			t[i] = new Thread(new Nthreads());
			t[i].start();
		}
	}
	
	static class Nthreads extends Thread
	{
		double w=0.4, x = 1.4, y = 2.46, z = 3.84, f=1.3, g=1.3, h=1.3;
		int a = 4, b = 1, c = 2, d = 5, e = 1;
		public void run()
		{
			double start,end=0;
			long niters=1000000000,nano = 1000000000;
			
			//IOPS
			start = System.nanoTime();
			for(int i=0;i<niters;i++)
			{
				a+=b;a+=b;a+=b;a+=b;a-=b;
				a+=b;a*=b;a+=b;a+=b;a-=b;
			}
			end = System.nanoTime();
			System.out.println("IOPS is "+(22/((end-start)/nano)));
			
			//FLOPS
			start = System.nanoTime();
			for(int i=0;i<niters;i++)
			{		
				f+=z+w+x;x+=y+g+w;x+=f+y+g;x-=y+z+h;g+=f+g+y;
				x-=g+w+y;z+=g+x+y;h+=y+x+z;f-=z+y+x;h+=y+z+x;
				x-=g+w+y;z+=g+x+y;h+=y+x+z;f-=z+y+x;h+=y+z+x;	
			}
			end = System.nanoTime();
			System.out.println("FLOPS is "+(77/((end-start)/nano)));
		}
	}
}
