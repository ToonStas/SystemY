package SystemY;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class TCP {
	private final static int SOCKET_PORT = 13267;
	private Thread receiveThread;
	private Thread sendThread;
	public TCP() {
		;
	}

	public void ReceiveFile(int fileSize, String filePath) throws IOException {
		receiveThread = new Thread (new TCPReceiveThread(SOCKET_PORT, fileSize, filePath));
		receiveThread.start();
	}
	


	public void SendFile(File fileToSend, InetAddress IPDestination) throws IOException {
		sendThread = new Thread (new TCPSendThread(SOCKET_PORT, fileToSend, IPDestination));
		sendThread.start();
	}



}