package SystemY;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

public class TCP {
	private final static int SOCKET_PORT = 13267;
	private Thread receiveThread;
	private Thread sendThread;
	private Semaphore semReceive = new Semaphore(1);
	private Semaphore semSend = new Semaphore(1);
	private TreeMap receiveList = new TreeMap<Integer,ListedReceiveFile>();
	private TreeMap sendList = new TreeMap<Integer,ListedSendFile>();
	
	public TCP() {
		
	}
	
	public Semaphore getSemSend(){
		return semSend;
	}
	
	public Semaphore getSemReceive(){
		return semReceive;
	}
	
	public TreeMap<Integer,ListedReceiveFile> getReceiveList(){
		return receiveList;
	}
	
	public TreeMap<Integer,ListedSendFile> getSendList(){
		return sendList;
	}
	
	public Thread StartReceiveFile(String filePath, int size){
		receiveThread = new Thread (new TCPReceiveThread(SOCKET_PORT, size, filePath));
		receiveThread.start();
		return receiveThread;
	}
	
	public Thread StartSendFile(File fileToSend, InetAddress IPDestination){
		sendThread = new Thread (new TCPSendThread(SOCKET_PORT, fileToSend, IPDestination));
		sendThread.start();
		return sendThread;
	}
	
	//Starts a thread who receives a file if the thread is not busy handling another receive request.
	public void ReceiveFile(clientToClientInterface ctci, String filePath, int size, int fileID) throws IOException {
		receiveList.put(fileID,new ListedReceiveFile(filePath, size, fileID));
		
		
		
	}
	
	
	//Starts a thread who sends a file if the thread is not busy handling another send request.
	public void SendFile(clientToClientInterface ctci, File fileToSend, InetAddress IPDestination, int fileID) throws IOException {
		sendList.put(fileID,new ListedSendFile(fileToSend,IPDestination,fileID));
	}

}