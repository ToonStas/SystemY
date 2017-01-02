package SystemY;

import java.io.Serializable;
import java.net.InetAddress;
//class with the information needed to handle a file request
public class ReceiveFileRequest implements Serializable {
	private String path;
	private String name;
	private int size;
	private int ID;
	private InetAddress IP;
	private int hashOwner;
	private int hashReplication;
	public ReceiveFileRequest(InetAddress IPSender,String fileName, String filePath, int fileSize, int fileID, int hashOwnerNode, int hashReplicationNode){
		path = filePath;
		size = fileSize;
		ID = fileID;
		IP = IPSender;
		name = fileName;
		hashOwner = hashOwnerNode;
		hashReplication = hashReplicationNode;
		
	}
	
	public String getPath(){
		return path;
	}
	
	public String getName(){
		return name;
	}
	
	public int getSize(){
		return size;
	}
	
	public int getID(){
		return ID;
	}
	
	public InetAddress getIP(){
		return IP;
	}
	
	public int getHashOwner(){
		return hashOwner;
	}
	
	public int getHashReplication(){
		return hashReplication;
	}

}
