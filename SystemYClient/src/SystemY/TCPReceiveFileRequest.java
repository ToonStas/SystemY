package SystemY;

import java.io.Serializable;
import java.net.InetAddress;
//class with the information needed to handle a file request
public class TCPReceiveFileRequest implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
	private int size;
	private int ID;
	private InetAddress IP;
	private int hashOwnerNode;
	private String nameOwnerNode;
	boolean transferOwnerShip;
	private BestandFiche fiche;
	public TCPReceiveFileRequest(InetAddress IPSender,String fileName, int fileSize, int fileID, int hashOwnerNode, String nameOwnerNode, BestandFiche fileFiche, boolean transferOwnerShip){
		size = fileSize;
		ID = fileID;
		IP = IPSender;
		name = fileName;
		this.hashOwnerNode = hashOwnerNode;
		this.nameOwnerNode = nameOwnerNode;
		this.transferOwnerShip = transferOwnerShip;
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
	
	public boolean transferOwnerShip(){
		return transferOwnerShip;
	}
	
	public String getNameOwnerNode(){
		return nameOwnerNode;
	}

	public BestandFiche getFiche(){
		return fiche;
	}
}
