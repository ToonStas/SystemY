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

	private static final long serialVersionUID = 1L;
	private FileWithoutFileList allFiles;
	private NodeClient node;
	
	public Agent(){
		allFiles = new FileWithoutFileList();
	}
	
	public void setNode(NodeClient newNode){
		node = newNode;
	}
	
	public void run(){
		//add the nodes new owned files
		FileManager fileManager = node.getFileManager();
		FileWithoutFileList nodeFileList = fileManager.getAllNodeOwnedFiles();
		allFiles.addAllFilesNotAlreadyAdded(nodeFileList);
		
		//set the lock request from the node
		ArrayList<String> lockRequests = nodeFileList.getNameListLockedFiles();
		allFiles.lockAllFilesInThisNameList(lockRequests);
		
		//remove the lock request from the nodes list
		nodeFileList.removeAllLocksInThisNameList(lockRequests);
		
		//return the nodes lock request list:
		fileManager.setAllNodeOwnedFiles(nodeFileList);
		
		//now we set the new allFile list in the node
		fileManager.setAllFileList(allFiles);
		
		
		
	}
	
	
}
