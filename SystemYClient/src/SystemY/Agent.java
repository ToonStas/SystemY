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
		FileManager fileManager = node.getFileManager();
		FileWithoutFileList nodeList = fileManager.getLockRequestList();
		
		
		
		
	}
	
	
}
