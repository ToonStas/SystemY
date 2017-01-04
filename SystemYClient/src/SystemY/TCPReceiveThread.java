package SystemY;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPReceiveThread extends Thread {
	
	private static int SOCKET_PORT;
	private int size;
	private TCP tcp;
	private int ID;
	private NodeClient node;
	private ReceiveFileRequest request;
	private FileManager fileManager;
	
	//Thread who receives a file
	public TCPReceiveThread(int Socket_Port,TCP thisTcp, NodeClient nodeClient, ReceiveFileRequest receiveRequest, FileManager theFileManager){
		SOCKET_PORT = Socket_Port;
		request = receiveRequest;
		size = request.getSize();
		tcp = thisTcp;
		ID = request.getID();
		node = nodeClient;
		fileManager = theFileManager;
	}
	
	public void run(){
		int bytesRead;
		int current;
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		Socket sock = null;
		ServerSocket servSock = null;
		try {
			servSock = new ServerSocket(SOCKET_PORT);
			
				try {
					sock = servSock.accept();
					//System.out.println("Succesful TCP connection with " + sock.getInetAddress().toString() + ", ready for receiving file.");
					byte[] byteArray = new byte[size];
					InputStream in = sock.getInputStream();
					fos = new FileOutputStream("C:/TEMP/RepFiles/"+request.getName());
					bos = new BufferedOutputStream(fos);
					bytesRead = in.read(byteArray, 0, byteArray.length);
					current = bytesRead;
					do {
						bytesRead = in.read(byteArray, current, (byteArray.length - current));
						if (bytesRead >= 0)
							current += bytesRead;
					} while (bytesRead > -1);
					bos.write(byteArray, 0, current);
					bos.flush();
					File file = new File("C:/TEMP/RepFiles/"+request.getName());
					if (file.exists()){
						System.out.println("File " +file.getName()+ " was succesfull received.");
						fileManager.addRepFile(request.getName(), request.getNameOwnerNode(), request.getHashOwnerNode(), request.getFiche());
						
					}
					else {
						System.out.println("Could not receive file.");
					}
					tcp.getSemReceive().release();
					tcp.getReceiveBuffer().remove(request.getID());

				} finally {
					if (fos != null)
						fos.close();
					if (bos != null)
						bos.close();
					if (sock != null)
						sock.close();
					tcp.getSemReceive().release();
					tcp.getReceiveBuffer().remove(request.getID());
					
				}
			
		} catch (IOException e) {
			tcp.getSemReceive().release();
			tcp.getReceiveBuffer().remove(ID);
			e.printStackTrace();
		} finally {
			if (servSock != null){
				try {
					servSock.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			tcp.getSemReceive().release();
			tcp.getReceiveBuffer().remove(ID);
		}
		
	}

}
