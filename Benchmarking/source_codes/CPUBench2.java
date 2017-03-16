public class CPUBench2
{
	public static void main(String[] args)
	{
		Thread t[] = new Thread[4];
		for(int i=0;i<4;i++)
		{
			t[i] = new Nthreads();
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
		
			//IOPS
			System.out.println("IOPS");
			long nops=0;
			int secs=0;
			while(secs<=600)
			{
				start = System.currentTimeMillis()/1000;
				if(start!=end)
				{
					end=start;
					System.out.println(secs+":"+nops);
					nops=0;
					secs++;
				}
				a+=b;a+=b;a+=b;a+=b;a-=b;
				a+=b;a*=b;a+=b;a+=b;a-=b;
				nops+=22;
			}
			
			//FLOPS
			System.out.println("FLOPS");
			nops=0;secs=0;end=0;
			while(secs<=600)
			{
				start = System.currentTimeMillis()/1000;
				if(start!=end)
				{
					end=start;
					System.out.println(secs+":"+nops);
					nops=0;
					secs++;
				}
				f+=z+w+x;x+=y+g+w;x+=f+y+g;x-=y+z+h;g+=f+g+y;
				x-=g+w+y;z+=g+x+y;h+=y+x+z;f-=z+y+x;h+=y+z+x;
				x-=g+w+y;z+=g+x+y;h+=y+x+z;f-=z+y+x;h+=y+z+x;
				nops+=77;
			}
		}
	}
}
