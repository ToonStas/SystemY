package SystemY;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;

public class Agent implements Serializable,Runnable {

	private static final long serialVersionUID = 1L;
	private static long SLEEPTIME = 50;
	private FileWithoutFileList allFiles;
	private NodeClient node;
	
	public Agent(NodeClient nodeClient, FileWithoutFileList allFilesNetwork){
		allFiles = allFilesNetwork;
		node = nodeClient;
	}
	
	
	
	public void run(){
		//update the allFiles list with the new files of the node
		FileManager fileManager = node.getFileManager();
		FileWithoutFileList nodeFileList = fileManager.getAllNodeOwnedFiles();
		allFiles.addAllFilesNotAlreadyAdded(nodeFileList);
		
		//delete files in allFiles list who are in the deleteList from this node:
		ArrayList<String> deleteList = fileManager.getDeletedList();
		for (int i=0;i<deleteList.size();i++){
			allFiles.removeFileWithName(deleteList.get(i));
		}
		
		//set the lock request from the node
		ArrayList<String> lockRequests = fileManager.getNameListLockRequests();
		allFiles.lockAllFilesInThisNameList(lockRequests);
		
		//unlock the files with the unlocks this node has
		ArrayList<String> unlockList = fileManager.getUnlockList();
		allFiles.removeAllLocksInThisNameList(unlockList);
		fileManager.clearUnlockList();
		
		//now we set the new allFile and the lockRequest list in the node, in the lock request list, the locks are deleted first
		fileManager.setAllFileList(allFiles);
		FileWithoutFileList newLockRequestList = allFiles;
		newLockRequestList.removeAllLocks();
		fileManager.setLockRequestList(newLockRequestList);
		
		
		//sleeping a bit
		try {
			Thread.sleep(SLEEPTIME);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		node.passAgent(allFiles);
	}
	
	
}
