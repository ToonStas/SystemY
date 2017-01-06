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
	private static long SLEEPTIME = 100;
	private FileWithoutFileList allFiles;
	private NodeClient node;
	private boolean start = false;
	
	public Agent(){
		allFiles = new FileWithoutFileList();
	}
	
	public void setNode(NodeClient nodeClient){
		node = nodeClient;
		start = true;
	}
	
	
	public void run(){
		
		while (true){
			//waiting till the thread may start and the node is set
			while (start == false){
				try {
					Thread.sleep(SLEEPTIME);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
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
			
			//sleeping a bit
			try {
				Thread.sleep(SLEEPTIME);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			start = false;
			node.passAgent(this);
		}
		
	}
	
	
}
