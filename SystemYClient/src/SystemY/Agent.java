package SystemY;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class Agent implements Runnable, Serializable{
	TreeMap<String, Boolean> allFiles = new TreeMap<>(); //name, isLocked; all files in the system to provide to the clients
	private NodeClient nodeClient;
	ArrayList<String> owned = new ArrayList<>(); //contains all files this node is the owner of
	ArrayList<String> locked = new ArrayList<>(); //contains all files that should be locked
	
	public Agent(NodeClient nodeClient){
		this.nodeClient = nodeClient;
	}
	
	public void run(){
		TreeMap<String, Integer> temp = nodeClient.getFileList();
		
		//iterate through the localfiles to determine, which files are owned by the node
		//also check if the agent already has this file in his list
		for(Map.Entry<String,Integer> entry : temp.entrySet()) {
			  String name = entry.getKey();
			  Integer hash = entry.getValue();
			  
			  //check for ownership
			  try {
				if(nodeClient.ni.getFileLocation(name)==nodeClient.ni.getIP(nodeClient.getOwnHash())){
					//finally add the name, if this node owns it
					owned.add(name);
					

				}
			  } catch (RemoteException e) {
				System.out.println("Agent: Couldn't fetch filelocation/ip");
				e.printStackTrace();
			  }	
			  
			  //check if it's already in the list
			  //if it isn't, add it
			  if(!allFiles.containsKey(name)){
				  allFiles.put(name, false); //by default a file isn't locked
			  }
		}
		
		//update this nodes list of all files
		nodeClient.setAllFiles(allFiles);	
		
		
	}
}
