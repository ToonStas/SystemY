package SystemY;

import java.io.File;
import java.net.InetAddress;
// class with the information needed for a file send request
public class SendFileRequest {
	private File file;
	private InetAddress IP;
	private String fileName;
	private int ID;
	private int semTOC; //time out counter for busy semaphore
	private int fileTOC; // time out counter for no such file in receive request buffer
	private int hashReceiver;
	private boolean removeFiche;
	private boolean deleteFileAfterSending;
	
	public SendFileRequest(File fileToSend, InetAddress IPDestination, int fileID, int hashReceiverNode, String fileName, boolean removeFiche, boolean deleteFileAfterSending){
		file = fileToSend;
		IP = IPDestination;
		ID = fileID;
		semTOC = 5000;
		fileTOC = 50;
		hashReceiver = hashReceiverNode;
		this.fileName = fileName;
		this.removeFiche = removeFiche;
		this.deleteFileAfterSending = deleteFileAfterSending;
		
	}
	
	public String getFileName(){
		return fileName;
	}
	
	public boolean isRemoveFiche(){
		return removeFiche;
	}
	
	public boolean deleteFileAfterSending(){
		return deleteFileAfterSending;
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
