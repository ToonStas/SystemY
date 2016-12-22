package SystemY;

import java.util.ArrayList;

// FIFO buffer wich holds the different file send requests
public class SendBuffer {
	
	private ArrayList<SendFileRequest> buffer;
	
	public SendBuffer()
	{
		buffer = new ArrayList<SendFileRequest>();
	}
	
	public void add(SendFileRequest fileRequest){
		buffer.add(fileRequest);
	}
	
	public boolean remove(int fileID){
		boolean inList;
		if (buffer.get(0).ID == fileID){
			buffer.remove(0); //if the file is in the list
			inList = true;
		}
		else {
			inList = false;
		}
		return inList;
	}
	
	public boolean isNext(){
		if (buffer.isEmpty()){
			return false;
		}
		else {
			return true;
		}
	}
	
	public SendFileRequest getNext(){
		return buffer.get(0);
	}
	
	public boolean contains(int fileID){
		boolean check = false;
		for (int i=0;i<buffer.size();i++){
			if (buffer.get(i).ID == fileID){
				check = true;
			}
		}
		return check;
		
	}

}
