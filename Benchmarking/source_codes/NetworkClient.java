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

public class NetworkClient
{
        public static double tcp,udp,start,end;
        public static int nthreads;
        public static DatagramSocket clientSocket = null;
        public static String fileName = "output.txt";	//To Store data from Server
        public static ServerSocket server = null;
        public static Socket client = null;
        public static FileInputStream fis = null;
        public static FileOutputStream fos = null;
        public static DataInputStream is = null;
        public static DataOutputStream out = null;
        public static void main(String args[]) throws Exception
        {
                File file = new File("input.txt");	//To Send Data to Server
                fis = new FileInputStream(file);
                clientSocket = new DatagramSocket(30000);
                nthreads = Integer.parseInt(args[0]);
                Thread th[] = new Thread[nthreads];
                System.out.println("Attempt Connection");
                client = new Socket("52.36.43.210",20000); //Give IP of Server
                fos = new FileOutputStream(fileName);
                is = new DataInputStream(client.getInputStream());
                out = new DataOutputStream(client.getOutputStream());
                int[] bsize = {1,1024,65507};	 
                for(int i=0;i<bsize.length;i++)		//Block Size Loop.
                {
                        for(int j=0;j<nthreads;j++)		//Thread Loop
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
                        try

                        {
                        		//UPLOAD
                                byte [] buffer  = new byte[bsize];
                                int count,total=0;
                                double start = System.nanoTime();
                                while ((count = fis.read(buffer)) != -1)
                                {
                                        out.write(buffer,0,count);
                                        total += count;
                                        if(total==bsize)
                                                break;
                                }
                                System.out.println("File Sent");

                                Thread.sleep(1000);
                                
                                //DOWNLOAD
                                buffer = new byte[bsize];
                                count=0;total=0;
                                while ((count = is.read(buffer)) != -1)
                                {
                                        fos.write(buffer);
                                        total += count;
                                        if(total==bsize)
                                                break;
                                }
                                double end = System.nanoTime();
                                System.out.println("File " + fileName + " downloaded of size " + total + " bytes");
                                tcp = ((end-start)/1000000000)-1;
                                System.out.println("TCP Throughput for Block size "+bsize+" Byte(s) is "+(2*(bsize/tcp)/(1024*1024))+" Mbps");
                                System.out.println("TCP Latency for Block size "+bsize+" Byte(s) is "+(tcp*1000)/2+" ms");

                                //UDP
                                DatagramPacket send = null;
                                start = System.nanoTime();
                                buffer = new byte[bsize];
                                fis.read(buffer);
                                send = new DatagramPacket(buffer,buffer.length,InetAddress.getByName("52.36.43.210"),40000); //Give IP of Server
                                clientSocket.send(send);	//Send Packet to Server
                                send = new DatagramPacket(buffer, buffer.length);
                                clientSocket.receive(send); //Receive Packet from Server
                                end = System.nanoTime();
                                udp = (end-start)/1000000000;
                                System.out.println("UDP Throughput for Block size "+bsize+" Byte(s) is "+2*((bsize/udp)/(1024*1024))+" Mbps");
                                System.out.println("UDP Latency for Block size "+bsize+" Byte(s) is "+(udp*1000)/2+" ms");
                        }
                        catch (Exception e)
                        {
                                e.printStackTrace();
                        }
                }
        }
}
