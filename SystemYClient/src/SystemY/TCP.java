package SystemY;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class TCP {
	public final static int SOCKET_PORT = 13267;
	private Socket sock;
	private ServerSocket serverSock;
	private InetAddress ipThisClient;
	public TCP() throws IOException{
		ipThisClient = InetAddress.getLocalHost();
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
		try {
			if (sock.isClosed()!=true)
			{
				sock.close();
			}
			sock = new Socket(ipNext,SOCKET_PORT);
			System.out.println("Connecting to server");
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
			out.write("notifyNextAdd");
			out.newLine();
			out.write(String.valueOf(previousHash));
			out.newLine();
			out.write(String.valueOf(nextHash));
			out.newLine();
			out.write(ipNext.toString());
			out.flush();
			out.close();
			sock.close();
			
			
		} catch (IOException e) {
			
		}
	}
	
	public void notifyPreviousAdd(int previousHash, int nextHash, InetAddress ipPrevious)
	{
		try {
			if (sock.isClosed()!=true)
			{
				sock.close();
			}
			sock = new Socket(ipPrevious,SOCKET_PORT);
			System.out.println("Connecting to server");
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
			out.write("notifyPreviousAdd");
			out.newLine();
			out.write(String.valueOf(previousHash));
			out.newLine();
			out.write(String.valueOf(nextHash));
			out.newLine();
			out.write(ipPrevious.toString());
			out.flush();
			out.close();
			sock.close();
			
			
		} catch (IOException e) {
			
		}
	}
	
	public void notifyNextShutdown(int previousHash, InetAddress ipPrevious)
	{
		try {
			if (sock.isClosed()!=true)
			{
				sock.close();
			}
			sock = new Socket(ipPrevious,SOCKET_PORT);
			System.out.println("Connecting to server");
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
			out.write("notifyNextShutdown");
			out.newLine();
			out.write(String.valueOf(previousHash));
			out.newLine();
			out.write(ipPrevious.toString());
			out.flush();
			out.close();
			sock.close();
			
			
		} catch (IOException e) {
			
		}
	}
	
	public void notifyPreviousShutdown(int nextHash, InetAddress ipNext)
	{
		try {
			if (sock.isClosed()!=true)
			{
				sock.close();
			}
			sock = new Socket(ipNext,SOCKET_PORT);
			System.out.println("Connecting to server");
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
			out.write("notifyPreviousShutdown");
			out.newLine();
			out.write(String.valueOf(nextHash));
			out.newLine();
			out.write(ipNext.toString());
			out.flush();
			out.close();
			sock.close();
			
			
		} catch (IOException e) {
			
		}
	}
	
	
	public void listen() throws IOException
	{
		Thread listener = new Thread(new TCPServerSocketListener(SOCKET_PORT, this.sock, this.serverSock));
		listener.start();
	}

}
