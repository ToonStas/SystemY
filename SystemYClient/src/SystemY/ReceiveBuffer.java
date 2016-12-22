package SystemY;

import java.util.TreeMap;
//buffer which holds the different file requests
public class ReceiveBuffer {

	private TreeMap<Integer,ReceiveFileRequest> map;
	public ReceiveBuffer(){
		map = new TreeMap<Integer,ReceiveFileRequest>();
	}
	
	public void add(ReceiveFileRequest fileRequest){
		map.put(fileRequest.ID, fileRequest);
	}
	
	public ReceiveFileRequest get(int fileID){
		return map.get(fileID);
	}
	
	public boolean contains(int fileID){
		return map.containsKey(fileID);
	}
	
	public boolean remove(int fileID){
		if (this.contains(fileID)){
			map.remove(fileID);
			return true;
		}
		else
			return false;
	}
}
