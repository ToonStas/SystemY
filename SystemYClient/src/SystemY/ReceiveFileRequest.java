package SystemY;

import java.net.InetAddress;

public class ReceiveFileRequest {
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
