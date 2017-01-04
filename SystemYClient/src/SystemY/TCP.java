package SystemY;

import java.io.IOException;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.util.Random;
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
		node = nodeClient;
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
		receiveThread = new Thread (new TCPReceiveThread(SOCKET_PORT, this, node, request, node.getFileManager()));
		receiveThread.start();
		return receiveThread;
	}
	
	public Thread StartSendFile(SendFileRequest request){
		sendThread = new Thread (new TCPSendThread(SOCKET_PORT, this, node, request, node.getFileManager()));
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
		System.out.println("A send request was set: "+request.getFile().getName()+" to IP address: "+request.getIP());
	}
	
	public void sendFile(Bestand fileToSend, int receiverHash, BestandFiche fileFiche){
		Random ran = new Random();
		int fileID = ran.nextInt(20000);//The file ID is used in the file receive and send requests, they are compared to know if they are transmitting the right file
		String ip = "";
		try {
			ClientToNamingServerInterface ni = node.makeNI();
			ip = ni.getIP(receiverHash);
			ni = null;
		} catch (RemoteException e1) {
			System.out.println("Couldn't fetch IP from Namingserver");
			node.failure(receiverHash); //when we can't fetch te ip it's likely the node shut down unexpectedly
			e1.printStackTrace();
		}
		int fileSize = ((int) fileToSend.getFile().length())+1000;
		
		
		try {
			SendFileRequest sendRequest = new SendFileRequest(fileToSend.getFile(),InetAddress.getByName(ip),fileID,receiverHash,fileToSend.getName(),fileFiche.isNewOwner(),fileFiche.deleteFileAfterSending());
			ReceiveFileRequest receiveRequest = new ReceiveFileRequest(InetAddress.getLocalHost(),fileToSend.getName(),fileSize,fileID, fileToSend.getHashLocalOwner(), fileToSend.getNameLocalOwner(), fileFiche);

			ClientToClientInterface ctci = node.makeCTCI(receiverHash);
			ctci.setReceiveRequest(receiveRequest);
			ctci = null;
			addSendRequest(sendRequest);
			
		} catch (RemoteException e) {
			System.err.println("NamingServer exception: " + e.getMessage());
			node.failure(receiverHash); //when we can't connect to the node we assume it failed.
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	

}