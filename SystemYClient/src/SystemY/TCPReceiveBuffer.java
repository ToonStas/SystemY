package SystemY;

import java.util.TreeMap;
//buffer which holds the different file requests
public class TCPReceiveBuffer {

	private TreeMap<Integer,TCPReceiveFileRequest> map;
	public TCPReceiveBuffer(){
		map = new TreeMap<Integer,TCPReceiveFileRequest>();
	}
	
	public void add(TCPReceiveFileRequest fileRequest){
		map.put(fileRequest.getID(), fileRequest);
	}
	
	public TCPReceiveFileRequest get(int fileID){
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
	
	public boolean isEmpty(){
		return map.isEmpty();
	}
}
