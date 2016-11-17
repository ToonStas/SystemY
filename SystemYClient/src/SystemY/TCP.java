package SystemY;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class TCP {
	public final static int SOCKET_PORT = 13267;
	private Socket sock;
	private ServerSocket serverSock;
	private InetAddress ipPrevious;
	private InetAddress ipNext;
	private InetAddress ipNamingServer;
	public TCP() throws IOException{
		this.sock = new Socket();
		try {
			serverSock = new ServerSocket(SOCKET_PORT);
		} catch (IOException e) {
			System.out.println("Couldn't create serversocket");
		}
		this.listen();
	}
	
	public void notifyNextAdd(int previousHash, int nextHash, InetAddress ipNext)
	{
		checkSocket();
	}
	
	public void notifyPreviousAdd(int previousHash, int nextHash, InetAddress ipPrevious)
	{
		
	}
	
	public void notifyNextShutdown(int previousHash, InetAddress ipPrevious)
	{
		
	}
	
	public void notifyPreviousShutdown(int nextHash, InetAddress ipNext)
	{
		
	}
	
	private void checkSocket()
	{
		if (sock.isClosed())
		{
			
		}
	}
	
	public void checkMessages()
	{
		
	}
	
	public void listen() throws IOException
	{
		Thread listener = new Thread(new TCPServerSocketListener(SOCKET_PORT, this.sock, this.serverSock));
		listener.start();
	}

}
