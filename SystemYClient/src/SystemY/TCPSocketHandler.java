package SystemY;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPSocketHandler {
	public int port;
	public ServerSocket servSock = null;
	public TCPSocketHandler(int socketPort){
		port = socketPort;
		
	}
	public Socket getSocket()
	{
		Socket sock = null;
		try {
			servSock = new ServerSocket(port);
			try {
				System.out.println("Attempting connection...");
				
				sock = servSock.accept();
			} catch (IOException e) {
				System.out.println("Could nog connect to socket");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sock;
	}
	

}
