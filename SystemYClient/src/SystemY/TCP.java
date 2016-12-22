package SystemY;

import java.io.IOException;
import java.util.concurrent.Semaphore;

// this class handles the tcp sends/receives
public class TCP {
	private final static int SOCKET_PORT = 13267;
	private Thread receiveThread;
	private Thread sendThread;
	private Semaphore semReceive = new Semaphore(1); //only one file can be received at a time
	private Semaphore semSend = new Semaphore(1); //only one file can be send at a time
	private ReceiveBuffer receiveBuffer; //buffer which holds the receive requests
	private SendBuffer sendBuffer;	//buffer which holds the send requests
	private NodeClient node;
	
	public TCP(NodeClient nodeClient) {
		nodeClient = node;
		receiveBuffer = new ReceiveBuffer();
		sendBuffer = new SendBuffer();
		Thread sendHandler = new TCPSendHandlerThread(this,node); 
		sendHandler.start();	
	}
	
	public SendBuffer getSendBuffer(){
		return sendBuffer;
	}
	
	public ReceiveBuffer getReceiveBuffer(){
		return receiveBuffer;
	}
	
	public Semaphore getSemSend(){
		return semSend;
	}
	
	public Semaphore getSemReceive(){
		return semReceive;
	}
	
	//this method uses the sender of a file to know if this receiver is ready to receive a file
	public int checkReceiveAvailable(int fileID){
		if (receiveBuffer.contains(fileID)){
			if (semReceive.tryAcquire()){
				this.StartReceiveFile(receiveBuffer.get(fileID));
				return fileID;
			}
			else {
				return -1; //semaphore not available message
			}
		}
		else {
			return -2; //file not available message
		}
	}
	
	public Thread StartReceiveFile(ReceiveFileRequest request){
		receiveThread = new Thread (new TCPReceiveThread(SOCKET_PORT, this, node, request));
		receiveThread.start();
		return receiveThread;
	}
	
	public Thread StartSendFile(SendFileRequest request){
		sendThread = new Thread (new TCPSendThread(SOCKET_PORT, this, node, request));
		sendThread.start();
		return sendThread;
	}
	
	//Adds the request to the buffer
	public void addReceiveRequest(ReceiveFileRequest request) throws IOException {
		receiveBuffer.add(request);
	}
	
	
	//Adds the request to the buffer
	public void addSendRequest(SendFileRequest request){
		sendBuffer.add(request);
	}

}