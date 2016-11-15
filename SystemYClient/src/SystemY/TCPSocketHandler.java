package SystemY;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPSocketHandler {
	private int port;
	private ServerSocket servSock = null;
	private Socket sock = null;
	
	public TCPSocketHandler(int socketPort){
		port = socketPort;
	}
	public Socket getSocket()
	{
		if (sock.isClosed()){
			try {
				servSock = new ServerSocket(port);
				try {
					System.out.println("Attempting connection...");
					
					this.sock = servSock.accept();
				} catch (IOException e) {
					System.out.println("Could nog connect to socket.");
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Serversocket fail.");
			}
		}
		return sock;
		
		
		
	}
	

}
