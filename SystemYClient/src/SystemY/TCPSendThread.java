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
	private TCPSendFileRequest request;
	private InetAddress IPDest;
	private TCP tcp;
	private FileManager fileManager;
	private int ID;
	private String fileName;
	private long sleepTimeMillis = 200;
	private int receiverHash;
	
	//Thread who sends a file
	public TCPSendThread(int SocketPort, NodeClient nodeClient, TCPSendFileRequest sendRequest){
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
		tcp.addThread(ID);
		boolean sendFile = false;
		boolean TOC = false;
		int message = 0;
		ClientToClientInterface ctci;
		int counter = 0;
		//STEP 1: loop for checking if this node and the receiving node are ready to transmit the file
		while (sendFile == false && TOC == false){
			counter++;
			
			//sleeping till new request
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
						tcp.getSemSend().release();
						if (request.checkSemTOC()){
							TOC = true;
							System.out.println("TCP error: semaphore time out counter expired for file "+fileName+", the file could not be send.");
						}
					} else if (message == -2) {
						tcp.getSemSend().release();
						if (request.checkFileTOC()){
							TOC = true;
							System.out.println("TCP error: fileRequest not found on receiver time out counter expired for file "+fileName+", the file could not be send.");
						}
					}
					
				} catch (RemoteException e) {
					System.out.println("Couldn't reach client with RMI");
					ctci = null;
					e.printStackTrace();
				}
			} 
		}
		
		
		if (!TOC){
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
					System.out.println("SendThread message: "+fileName+" was send with TCP.");
					
					if (request.transferOwnerShip()){ //if this node loses the ownership, this will do it
						fileManager.removeOwnerShip(fileName);
					}
					if (request.deleteFileAfterSending()) //if this node must delete the file after sending, this will do it
					{
						fileManager.removeRepFileWithName(fileName);
					}
					tcp.getSemSend().release();
					tcp.addThread(ID);
				
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
					tcp.addThread(ID);
				}
			}
		}
		
	}

}
