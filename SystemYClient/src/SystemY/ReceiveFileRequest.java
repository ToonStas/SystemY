package SystemY;

import java.io.Serializable;
import java.net.InetAddress;
//class with the information needed to handle a file request
public class ReceiveFileRequest implements Serializable {
	private String name;
	private int size;
	private int ID;
	private InetAddress IP;
	private int hashOwnerNode;
	private String nameOwnerNode;
	private BestandFiche fiche;
	public ReceiveFileRequest(InetAddress IPSender,String fileName, int fileSize, int fileID, int hashOwnerNode, String nameOwnerNode, BestandFiche fileFiche){
		size = fileSize;
		ID = fileID;
		IP = IPSender;
		name = fileName;
		this.hashOwnerNode = hashOwnerNode;
		this.nameOwnerNode = nameOwnerNode;
		fiche = fileFiche;
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
	
	public int getHashOwnerNode(){
		return hashOwnerNode;
	}
	
	public String getNameOwnerNode(){
		return nameOwnerNode;
	}

	public BestandFiche getFiche(){
		return fiche;
	}
}
