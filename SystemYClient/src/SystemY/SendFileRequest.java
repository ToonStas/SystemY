package SystemY;

import java.io.File;
import java.net.InetAddress;
// class with the information needed for a file send request
public class SendFileRequest {
	public File file;
	public InetAddress IP;
	public int ID;
	public int semTOC; //time out counter for busy semaphore
	public int fileTOC; // time out counter for no such file in receive request buffer
	public int hashReceiver;
	
	public SendFileRequest(File fileToSend, InetAddress IPDestination, int fileID, int hashReceiverNode){
		file = fileToSend;
		IP = IPDestination;
		ID = fileID;
		semTOC = 50;
		fileTOC = 50;
		hashReceiver = hashReceiverNode;
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
