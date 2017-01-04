package SystemY;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.Random;

public class TCPSendThread extends Thread {
	private static int SOCKET_PORT;
	private NodeClient node;
	private File file;
	private SendFileRequest request;
	private InetAddress IPDest;
	private TCP tcp;
	private FileManager fileManager;
	private int ID;
	private String fileName;
	private long sleepTimeMillis = 500;
	private int receiverHash;
	
	//Thread who sends a file
	public TCPSendThread(int SocketPort, NodeClient nodeClient, SendFileRequest sendRequest){
		SOCKET_PORT = SocketPort;
		request = sendRequest;
		file = request.getFile();
		IPDest = request.getIP();
		node = nodeClient;
		tcp = node.getTCP();
		ID = request.getID();
		fileManager = node.getFileManager();
		fileName = request.getFileName();
		receiverHash = request.getHashReceiver();
	}
	
	public void run(){
		boolean sendFile = false;
		boolean TOC = false;
		int message = 0;
		ClientToClientInterface ctci;
		int counter = 0;
		Random ran = new Random();
		//loop for checking if this node and the receiving node are ready to transmit the file
		while (sendFile == false){
			counter++;
			System.out.println("Waiting in thread for the "+counter+" time for file "+fileName+" with message "+message);
			//sleeping till new request
			sleepTimeMillis = (long) (ran.nextInt(500)+100);
			try {
				Thread.sleep(sleepTimeMillis);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//checking if this node is available for sending a file
			if (tcp.getSemSend().tryAcquire()){
				//checking if receiver is available
				ctci = node.makeCTCI(receiverHash);
				try {
					message = ctci.checkReceiveAvailable(ID);
					ctci = null;
					if (message == ID){
						sendFile = true; //now we can send the file
					} else if (message == -1) {
						
						
					} else if (message == -2) {
						
					}
					
				} catch (RemoteException e) {
					System.out.println("Couldn't reach client with RMI");
					ctci = null;
					e.printStackTrace();
				}
			} 
		}
		
		
		if (true){
			//Sending the file:
			FileInputStream fis = null;
			BufferedInputStream bis = null;
			OutputStream os = null;
			Socket sock = null;
			try {
				
					sock = new Socket(IPDest, SOCKET_PORT);
					//System.out.println("Accepted connection : " + sock);
					// send file
					byte[] mybytearray = new byte[(int) file.length()];
					fis = new FileInputStream(file);
					bis = new BufferedInputStream(fis);
					bis.read(mybytearray, 0, mybytearray.length);
					os = sock.getOutputStream();
					//System.out.println("Sending " + file.toString() + "(" + mybytearray.length + " bytes)");
					os.write(mybytearray, 0, mybytearray.length);
					os.flush();
					System.out.println("File "+fileName+" was send using TCP.");
					
					if (request.isRemoveFiche()){ //if this node loses the ownership
						fileManager.removeFicheByName(fileName);
					}
					if (request.deleteFileAfterSending()) //if this node must delete the file after sending
					{
						fileManager.deleteFileBySendThread(fileName);
					}
					tcp.getSemSend().release();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				
				try {
					if (bis != null)
						bis.close();
					if (os != null)
						os.close();
					if (sock != null)
						sock.close();
					tcp.getSemSend().release();
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					tcp.getSemSend().release();
				}
			}
		}
		
	}

}
