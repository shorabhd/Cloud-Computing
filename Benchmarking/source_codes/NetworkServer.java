import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class NetworkServer 
{
	public static DatagramSocket serverSocket = null;
	
	public static String fileName = "server.txt"; //To Save Data from the Client
	public static FileInputStream fis = null;
	public static DataInputStream is = null;
	
	public static ServerSocket server = null;
	public static Socket client = null;
	
	public static FileOutputStream fos = null;
	public static DataOutputStream os = null;
	
	public static void main(String args[]) throws Exception
	{
		serverSocket = new DatagramSocket(40000);
		File file = new File(fileName);
		fis = new FileInputStream(file);
		
		fos = new FileOutputStream(fileName);
		int nthreads = Integer.parseInt(args[0]);
		Thread th[] = new Thread[nthreads];
		System.out.println("Server Up!");
		server = new ServerSocket(20000);	
		client = server.accept();
		
		os = new DataOutputStream(client.getOutputStream());
		is = new DataInputStream(client.getInputStream());
		
		int[] bsize = {1,1024,65507};	//65507 since UDP packet's maximum size is that much
		
		for(int i=0;i<bsize.length;i++)	//Block Size Loop
		{
			for(int j=0;j<nthreads;j++) //Thread Loop
			{
				th[j] = new Nthread(bsize[i]);
				th[j].start();
				Thread.sleep(1000);
			}
		}
	}

	static class Nthread extends Thread
	{
		int bsize;

		public Nthread(int bsize)
		{
			this.bsize = bsize;
		}

		public void run()
		{	
			byte [] buffer = new byte [bsize];
			try
			{
				int count=0,total=0;
				
				//DOWNLOAD
				while ((count = is.read(buffer)) != -1) 
				{
					fos.write(buffer);
					total += count;
					if(total==bsize)
						break;
				}
				System.out.println("File of size "+bsize+" Byte(s) Downloaded!");
				Thread.sleep(1000);
				
				//UPLOAD
				buffer = new byte[bsize];
				count=0;total=0;
				while ((count = fis.read(buffer)) != -1) 
				{
					os.write(buffer,0,count);
					total += count;
					if(total==bsize)
						break;
				}
				System.out.println("File " + fileName + " of " + total + " Byte(s) Sent.");
				
				//UDP
				DatagramPacket recv = null;
				buffer = new byte[bsize];
				recv = new DatagramPacket(buffer, buffer.length);
				serverSocket.receive(recv);		//Receive Packet from Client
				recv = new DatagramPacket(buffer, buffer.length, InetAddress.getByName("localhost"),30000);  //Give IP of Client
				serverSocket.send(recv);		//Send Packet to Client	
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}



