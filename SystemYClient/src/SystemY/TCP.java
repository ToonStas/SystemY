package SystemY;

import java.io.IOException;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.Semaphore;

// this class handles the tcp sends/receives
public class TCP {
	private final static int SOCKET_PORT = 13267;
	private Thread receiveThread;
	private volatile Semafoor semReceive = new Semafoor(); //only one file can be received at a time
	private volatile Semafoor semSend = new Semafoor(); //only one file can be send at a time
	private volatile HashSet<Integer> sendThreadList;
	private TCPReceiveBuffer tCPReceiveBuffer; //buffer which holds the receive requests
	private NodeClient node;
	
	public TCP(NodeClient nodeClient) {
		node = nodeClient;
		tCPReceiveBuffer = new TCPReceiveBuffer();
		sendThreadList = new HashSet<>();
	}
	
	public void clearThread(int ID){
		if (sendThreadList.contains(ID)){
			sendThreadList.remove(ID);
		}
	}
	
	public void addThread(int ID){
		if (!sendThreadList.contains(ID)){
			sendThreadList.add(ID);
		}
	}
	
	public boolean sendThreadRunning(){
		if (sendThreadList.isEmpty()){
			return false;
		} else {
			return true;
		}
	}
	
	public TCPReceiveBuffer getReceiveBuffer(){
		return tCPReceiveBuffer;
	}
	
	public Semafoor getSemSend(){
		return semSend;
	}
	
	public Semafoor getSemReceive(){
		return semReceive;
	}
	
	//this method uses the sender of a file to know if this receiver is ready to receive a file
	public int checkReceiveAvailable(int fileID){
		if (tCPReceiveBuffer.contains(fileID)){
			if (semReceive.tryAcquire()){
				this.startReceiveFile(tCPReceiveBuffer.get(fileID));
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
	
	public Thread startReceiveFile(TCPReceiveFileRequest request){
		receiveThread = new Thread (new TCPReceiveThread(SOCKET_PORT, this, node, request, node.getFileManager()));
		receiveThread.start();
		return receiveThread;
	}
	
	public Thread startSendFile(TCPSendFileRequest request){
		Thread sendThread = new Thread (new TCPSendThread(SOCKET_PORT, node, request));
		sendThread.start();
		return sendThread;
	}
	
	//Adds the request to the buffer
	public void addReceiveRequest(TCPReceiveFileRequest request) throws IOException {
		tCPReceiveBuffer.add(request);
	}
	
	
	public void sendFile(Bestand fileToSend, int receiverHash, boolean transferOwnerShip, boolean deleteFileAfterSending){
		Random ran = new Random();
		int fileID = ran.nextInt(20000);//The file ID is used in the file receive and send requests, they are compared to know if they are transmitting the right file
		String ip = "";
		try {
			ClientToNamingServerInterface ni = node.makeNI();
			ip = ni.getIPNode(receiverHash);
			ni = null;
		} catch (RemoteException e1) {
			System.out.println("Couldn't fetch IP from Namingserver");
			node.failure(receiverHash); //when we can't fetch te ip it's likely the node shut down unexpectedly
			e1.printStackTrace();
		}
		int fileSize = ((int) fileToSend.getFile().length())+1000;
		
		
		try {
			TCPSendFileRequest sendRequest = new TCPSendFileRequest(fileToSend.getFile(),InetAddress.getByName(ip),fileID,receiverHash,fileToSend.getName(), transferOwnerShip,deleteFileAfterSending);
			TCPReceiveFileRequest receiveRequest = new TCPReceiveFileRequest(InetAddress.getLocalHost(),fileToSend.getName(),fileSize,fileID, fileToSend.getHashLocalOwner(), fileToSend.getNameLocalOwner(), fileToSend.getFiche(), transferOwnerShip);
			ClientToClientInterface ctci = node.makeCTCI(receiverHash);
			ctci.setReceiveRequest(receiveRequest);
			ctci = null;
			startSendFile(sendRequest);
			
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