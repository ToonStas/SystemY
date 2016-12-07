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
	public int ReceiveFile(int fileSize, String filePath) throws IOException {
		if (receiveThread.isAlive()){
			System.out.println("The thread is still busy with receiving another file.");
			return 0;
		}
		else {
			receiveThread = new Thread (new TCPReceiveThread(SOCKET_PORT, fileSize, filePath));
			receiveThread.start();
			return 1;
		}
	}
	
	//Starts a thread who sends a file.
	public int SendFile(File fileToSend, InetAddress IPDestination) throws IOException {
		if (sendThread.isAlive()){
			System.out.println("The thread is still busy with sending another file.");
			return 0;
		}
		else {
			sendThread = new Thread (new TCPSendThread(SOCKET_PORT, fileToSend, IPDestination));
			sendThread.start();
			return 1;
		}
	}



}