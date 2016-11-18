package SystemY;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServerSocketListener extends Thread{
	
	private Socket sock;
	private ServerSocket serverSock;
	private int port;
	public TCPServerSocketListener(int port, Socket sock, ServerSocket serverSock)
	{
		this.port = port;
		this.sock = sock;
		this.serverSock = serverSock;
	}
	public void run()
	{
		if (this.serverSock.isClosed()||serverSock==null)
		{
			try {
				this.serverSock = new ServerSocket(this.port);
			} catch (IOException e) {
				System.out.println("Could not create serversocket");
			}
		}
		while (this.sock.isConnected()!=true||sock==null)
		{
			try {
				this.sock = serverSock.accept();
				Thread handler = new Thread(new TCPSocketHandler(sock));
				handler.start();
				Thread newListener = new Thread (new TCPServerSocketListener(this.port, this.sock, serverSock));
				newListener.start();
			} catch (IOException e) {
				
			}
		}
	}
}
