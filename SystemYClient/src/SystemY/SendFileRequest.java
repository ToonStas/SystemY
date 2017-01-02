package SystemY;

import java.io.File;
import java.net.InetAddress;
// class with the information needed for a file send request
public class SendFileRequest {
	private File file;
	private InetAddress IP;
	private int ID;
	private int semTOC; //time out counter for busy semaphore
	private int fileTOC; // time out counter for no such file in receive request buffer
	private int hashReceiver;
	
	public SendFileRequest(File fileToSend, InetAddress IPDestination, int fileID, int hashReceiverNode){
		file = fileToSend;
		IP = IPDestination;
		ID = fileID;
		semTOC = 50;
		fileTOC = 50;
		hashReceiver = hashReceiverNode;
	}
	
	public File getFile(){
		return file;
	}
	
	public InetAddress getIP(){
		return IP;
	}
	
	public int getID(){
		return ID;
	}
	
	public int getSemTOC(){
		return semTOC;
	}
	
	public int getFileTOC(){
		return fileTOC;
	}
	
	public int getHashReceiver(){
		return hashReceiver;
	}

	public boolean checkSemTOC(){
		semTOC--;
		if (semTOC<0)
			return true;
		else
			return false;
	}
	
	public boolean checkFileTOC(){
		fileTOC--;
		if (fileTOC<0)
			return true;
		else
			return false;
	}

}
