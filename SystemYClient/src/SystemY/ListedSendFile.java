package SystemY;

import java.io.File;
import java.net.InetAddress;

public class ListedSendFile {
	public File file;
	public InetAddress IP;
	public int ID;
	
	public ListedSendFile(File fileToSend, InetAddress IPDestination, int fileID){
		file = fileToSend;
		IP = IPDestination;
		ID = fileID;
	}

}
