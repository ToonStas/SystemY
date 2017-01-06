package SystemY;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class Agent extends Thread implements Serializable {

	private static final long serialVersionUID = 1L;
	private FileWithoutFileList allFiles;
	private NodeClient node;
	
	public Agent(FileWithoutFileList allFileList, NodeClient nodeClient){
		allFiles = allFileList;
		node = nodeClient;
	}
	
	
	public void run(){
		//add the nodes new owned files
		FileManager fileManager = node.getFileManager();
		FileWithoutFileList nodeFileList = fileManager.getAllNodeOwnedFiles();
		allFiles.addAllFilesNotAlreadyAdded(nodeFileList);
		
		//unlock the files with the unlocks this node has
		ArrayList<String> unlockList = fileManager.getUnlockList();
		allFiles.removeAllLocksInThisNameList(unlockList);
		fileManager.clearUnlockList();
		
		//set the lock request from the node
		ArrayList<String> lockRequests = nodeFileList.getNameListLockedFiles();
		allFiles.lockAllFilesInThisNameList(lockRequests);
		
		//remove the lock request from the nodes list
		nodeFileList.removeAllLocksInThisNameList(lockRequests);
		
		//return the nodes lock request list:
		fileManager.setAllNodeOwnedFiles(nodeFileList);
		
		//now we set the new allFile list in the node
		fileManager.setAllFileList(allFiles);
		
		node.passAgent(allFiles);
	}
	
	
}
