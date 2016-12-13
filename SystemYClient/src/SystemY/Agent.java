package SystemY;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class Agent implements Runnable, Serializable{
	TreeMap<String, Boolean> allFiles = new TreeMap<>(); //name, isLocked; all files in the system to provide to the clients
	private NodeClient nodeClient;
	HashSet<String> owned = new HashSet<>(); //contains all files this node is the owner of
	HashSet<String> locked = new HashSet<>(); //contains all files that should be locked
	HashSet<String> unLocked = new HashSet<>(); //contains all files that should be unlocked
	
	public Agent(NodeClient nodeClient){
		this.nodeClient = nodeClient;
	}
	
	public void run(){
		TreeMap<String, Integer> temp = nodeClient.getFileList();
		
		//iterate through the localfiles to determine, which files are owned by the node
		//also check if the agent already has this file in his list or if it should be locked
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
			  
			  //check for every file if it should be locked
			  //alternative is to iterate through, allfiles and see if there are files here, that should be locked, but that takes longer
			  if(allFiles.get(name)==true){
				  locked.add(name);
			  }else if(allFiles.get(name)==false){
				  locked.remove(name);
			  }
			  
		}
		
		//also if a file is now unlocked, unlock it for all nodes
		unLocked = nodeClient.getUnlocked();
		
		//iterate throught the set and remove the lock
		for (String s : unLocked) {
		    allFiles.put(s, false);
		}
		  
		//update this nodes list of all files
		nodeClient.setAllFiles(allFiles);	
		
		//update this nodes owned files
		nodeClient.setOwned(owned);
		
		//update this nodes locked files
		nodeClient.setLocked(locked);
	}
}
