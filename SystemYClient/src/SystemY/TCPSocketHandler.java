package SystemY;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPSocketHandler extends Thread {
	
	private Socket sock;
	public TCPSocketHandler (Socket sock)
	{
		this.sock = sock;
	}
	
	public void run()
	{
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(this.sock.getInputStream()));
			String nextLine;
			nextLine = in.readLine();
			if (nextLine ==  "notifyNextAdd")
			{
				int previousHash = Integer.parseInt(in.readLine());
				int nextHash = Integer.parseInt(in.readLine());
				InetAddress ipNext = InetAddress.getByName(in.readLine());
				
				//action
				System.out.println("notifyNextAdd message received: ");
				System.out.println("------------------------------------");
				System.out.println("Previous Hash: "+previousHash);
				System.out.println("Next Hash: "+nextHash);
				System.out.println("IP previous: "+ipNext.toString());
			} 
			else if (nextLine == "notifyPreviousAdd")
			{
				int previousHash = Integer.parseInt(in.readLine());
				int nextHash = Integer.parseInt(in.readLine());
				InetAddress ipPrevious = InetAddress.getByName(in.readLine());
				
				//action
				System.out.println("notifyPreviousAdd message received: ");
				System.out.println("------------------------------------");
				System.out.println("Previous Hash: "+previousHash);
				System.out.println("Next Hash: "+nextHash);
				System.out.println("IP previous: "+ipPrevious.toString());
				
			}
			else if (nextLine == "notifyNextShutdown")
			{
				int previousHash = Integer.parseInt(in.readLine());
				InetAddress ipPrevious = InetAddress.getByName(in.readLine());
				
				//action
				System.out.println("notifyNextShutdown message received: ");
				System.out.println("------------------------------------");
				System.out.println("Previous Hash: "+previousHash);
				System.out.println("IP previous: "+ipPrevious.toString());
			}
			else if (nextLine == "notifyPreviousShutdown")
			{
				int nextHash = Integer.parseInt(in.readLine());
				InetAddress ipNext = InetAddress.getByName(in.readLine());
				
				//action
				System.out.println("notifyPreviousShutdown message received: ");
				System.out.println("------------------------------------");
				System.out.println("Next Hash: "+nextHash);
				System.out.println("IP next: "+ipNext.toString());
			} 
			else if (nextLine == "notifyNextFailure")
			{
				int previousHash = Integer.parseInt(in.readLine());
				InetAddress ipPrevious = InetAddress.getByName(in.readLine());
				
				//action
				System.out.println("notifyNextFailure message received: ");
				System.out.println("------------------------------------");
				System.out.println("Previous Hash: "+previousHash);
				System.out.println("IP previous: "+ipPrevious.toString());
			}
			else if (nextLine == "notifyPreviousFailure")
			{
				int nextHash = Integer.parseInt(in.readLine());
				InetAddress ipNext = InetAddress.getByName(in.readLine());
				
				//action
				System.out.println("notifyPreviousFailure message received: ");
				System.out.println("------------------------------------");
				System.out.println("Previous Hash: "+nextHash);
				System.out.println("IP previous: "+ipNext.toString());
			}
			else
			{
				System.out.println("Unsupported TCP-message");
			}
			in.close();
			sock.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}

}
