package SystemY;

public class ListedReceiveFile {
	public String path;
	public int size;
	public int ID;
	public clientToClientInterface ctci;
	public ListedReceiveFile(clientToClientInterface fileCtci, String filePath, int fileSize, int fileID){
		path = filePath;
		size = fileSize;
		ID = fileID;
		ctci = fileCtci;
		
	}

}
