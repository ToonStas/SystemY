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
	private int sendMessage;
	private int receiveMessage;
	private Semaphore semReceive = new Semaphore(1);
	private Semaphore semSend = new Semaphore(1);
	private TreeMap receiveList = new TreeMap<Integer,ListedReceiveFile>();
	private TreeMap sendList = new TreeMap<Integer,ListedSendFile>();
	
	public TCP() {
		Thread receiveHandler = new TCPReceiveHandlerThread(this);
		receiveHandler.start();
		Thread sendHandler = new TCPSendHandlerThread(this);
		sendHandler.start();
		sendMessage = -1;
		receiveMessage = -1;
		
	}
	
	public int getSendMessage(){
		return sendMessage;
	}
	
	public int getReceiveMessage(){
		return receiveMessage;
	}
	
	public void setSendMessage(int message){
		sendMessage = message;
	}
	
	public void setReceiveMessage(int message){
		receiveMessage = message;
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
	
	public Thread StartReceiveFile(String filePath, int size, int fileID){
		receiveThread = new Thread (new TCPReceiveThread(SOCKET_PORT, size, filePath, this, fileID));
		receiveThread.start();
		return receiveThread;
	}
	
	public Thread StartSendFile(File fileToSend, InetAddress IPDestination, int fileID){
		sendThread = new Thread (new TCPSendThread(SOCKET_PORT, fileToSend, IPDestination, this, fileID));
		sendThread.start();
		return sendThread;
	}
	
	//Starts a thread who receives a file if the thread is not busy handling another receive request.
	public void ReceiveFile(clientToClientInterface ctci, String filePath, int size, int fileID) throws IOException {
		receiveList.put(fileID,new ListedReceiveFile(ctci,filePath, size, fileID));
		
		
		
	}
	
	
	//Starts a thread who sends a file if the thread is not busy handling another send request.
	public void SendFile(clientToClientInterface ctci, File fileToSend, InetAddress IPDestination, int fileID) throws IOException {
		sendList.put(fileID,new ListedSendFile(ctci,fileToSend,IPDestination,fileID));
	}

}