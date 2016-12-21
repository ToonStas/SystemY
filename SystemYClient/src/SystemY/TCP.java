package SystemY;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.Semaphore;

public class TCP {
	private final static int SOCKET_PORT = 13267;
	private Thread receiveThread;
	private Thread sendThread;
	private Semaphore semReceive = new Semaphore(1);
	private Semaphore semSend = new Semaphore(1);
	
	
	public TCP() {
		
	}
	
	//Starts a thread who receives a file if the thread is not busy handling another receive request.
	public int ReceiveFile(String filePath, int size, int fileID) throws IOException {
		if (receiveThread != null && receiveThread.isAlive()){
			System.out.println("The thread is still busy with receiving another file.");
			return 0;
		}
		else {
			receiveThread = new Thread (new TCPReceiveThread(SOCKET_PORT, size, filePath));
			receiveThread.start();
			return 1;
		}
	}
	
	//Starts a thread who sends a file if the thread is not busy handling another send request.
	public int SendFile(File fileToSend, InetAddress IPDestination, int fileID) throws IOException {
		if (sendThread != null && sendThread.isAlive()){
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