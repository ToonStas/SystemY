package SystemY;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

public class TCP {
	private final static int SOCKET_PORT = 13267;
	private Thread receiveThread;
	private Thread sendThread;
	public TCP() {
		
	}
	
	//Starts a thread who receives a file.
	public void ReceiveFile(int fileSize, String filePath) throws IOException {
		receiveThread = new Thread (new TCPReceiveThread(SOCKET_PORT, fileSize, filePath));
		receiveThread.start();
	}
	
	//Starts a thread who sends a file.
	public void SendFile(File fileToSend, InetAddress IPDestination) throws IOException {
		sendThread = new Thread (new TCPSendThread(SOCKET_PORT, fileToSend, IPDestination));
		sendThread.start();
	}



}