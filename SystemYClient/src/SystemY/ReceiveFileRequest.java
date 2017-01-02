package SystemY;

import java.io.Serializable;
import java.net.InetAddress;
//class with the information needed to handle a file request
public class ReceiveFileRequest implements Serializable {
	public String path;
	public String name;
	public int size;
	public int ID;
	public InetAddress IP;
	public int hashOwner;
	public int hashReplication;
	public ReceiveFileRequest(InetAddress IPSender,String fileName, String filePath, int fileSize, int fileID, int hashOwnerNode, int hashReplicationNode){
		path = filePath;
		size = fileSize;
		ID = fileID;
		IP = IPSender;
		name = fileName;
		hashOwner = hashOwnerNode;
		hashReplication = hashReplicationNode;
		
	}

}
