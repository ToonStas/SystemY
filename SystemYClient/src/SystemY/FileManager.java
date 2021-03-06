package SystemY;

import java.io.File;
import java.rmi.RemoteException;
import java.util.ArrayList;

//Class which manages all files and the lists it has
public class FileManager {
	private FileWithFileList localFiles = null; //files which the node possesses locally
	private FileWithFileList repFiles = null; //files which are replicated to this node or file which are download for any other reason
	private FileWithFileList ownedFiles = null; //files which are owned and located locally
	private FileWithFileList filesToReplicate = null; //list of files which are not yet replicated because this node was the first one
	private volatile FileWithoutFileList allNetworkFiles = null; //list of all the files in the network
	private volatile FileWithoutFileList allNodeOwnedFiles = null; //ist of the owned files by this node
	private volatile FileWithoutFileList lockRequests = null;//list with the files on the network for the lock requests on this node
	private volatile ArrayList<String> unlockList = null; //list of all the unlocks this node has, contains the names of the files that may by unlocked
	private volatile ArrayList<String> deletedFiles = null; //list for the agent that holds the deleted files
	private NodeClient node = null;		
	private TCP tcp;	
	private Thread fileChecker;
	public volatile boolean runThread = false;
	
	public FileManager(NodeClient nodeClient){
		//initializing the private parameters
		localFiles = new FileWithFileList();
		repFiles = new FileWithFileList();
		node = nodeClient;
		tcp = node.getTCP();
		ownedFiles = new FileWithFileList();
		filesToReplicate = new FileWithFileList();
		allNetworkFiles = new FileWithoutFileList();
		allNodeOwnedFiles = new FileWithoutFileList();
		lockRequests = new FileWithoutFileList();
		unlockList = new ArrayList<>();
		deletedFiles = new ArrayList<>();
		
		//create the file directories if they not already exist
		File dir = new File("C:/TEMP/LocalFiles/");
	    if (!dir.exists()) {
	       	dir.mkdir();
	    }
	    dir = new File("C:/TEMP/RepFiles/");
	    if (!dir.exists()){
	    	dir.mkdir();
	    } else {
	    	for (File f : dir.listFiles()) {
	    		f.delete();
	    	}
	    }
	}
	
	
	
	public void updateOwnedFiles(){
		ownedFiles.clearList();
		ownedFiles.addAll(localFiles.getOwnerFiles());
		ownedFiles.addAll(repFiles.getOwnerFiles());
		allNodeOwnedFiles.addAllFilesNotAlreadyAdded(ownedFiles); //add the new files
		allNodeOwnedFiles.removeFilesNotContaining(ownedFiles); //remove the deleted files
		
	}


	//voegt alle locale bestanden toe bij het opstarten van de node en maakt hun fileFiches aan
	public void loadLocalFiles(){  
		File dir = new File("C:/TEMP/LocalFiles/");
		for (File f : dir.listFiles()) {
			String name = f.getName();
			addLocalFile(name);
			
		}
	}
	
	public void startCheckLocalFilesThread(){
		runThread = true;
		fileChecker = new CheckLocalFilesThread(this);
		fileChecker.start();
	}
	
	
	public void stopCheckLocalFilesThread(){
		runThread = false;
	}
	
	
	public void printAllFiles(){
		System.out.println("");
		System.out.println("Printing all the files on this node: (Hash: "+node.getOwnHash()+" )");
		System.out.println("");
		System.out.println("The local files are: ");
		localFiles.printFiles();
		System.out.println("");
		System.out.println("The replication and downloaded files are: ");
		repFiles.printFiles();
		System.out.println("");
		System.out.println("");
	}
	
	//adds a file correctly to the list, they are replicated afterward
	public void addLocalFile(String nameFile){
		FileWithFile newFile = new FileWithFile(nameFile,"C:/TEMP/LocalFiles/",node.getName(),node.getOwnHash());
		newFile.addOwnerFiche(node.getName());
		localFiles.add(newFile);
		
		//System.out.println("file fiche added for file "+newFile.getName()+", fiche: "+fiche.toString());
		ClientToNamingServerInterface ni = node.makeNI();
		try {
			if(ni.amIFirst()!=1){
				replicateFile(newFile);
			}
			else{
				filesToReplicate.add(newFile);
			}
			ni = null;
		} catch (RemoteException e) {
			ni = null;
			System.out.println("Couldn't reach namingserver via RMI.");
			e.printStackTrace();
		}
		
	}
	
	//method which checks if all files are replicated correctly, invoked when a new node enters the network
	public void checkReplication(){
		//checking if this node still has files that need to be replicated
		//if the node was the first node, it may not have replicated it files when it was the only node
		//System.out.println("Checking Replication...");
		ArrayList<FileWithFile> replicateList = filesToReplicate.getList();
		while (replicateList.isEmpty()!=true){
			FileWithFile file = replicateList.get(0);
			replicateFile(file);
			filesToReplicate.removeFromList(file);
		}
		
		//checking if the replicated files on this node are replicated to the right node, only if this node isn't first or second
		ClientToNamingServerInterface ni = node.makeNI();
		int numberOfClients = 0;
		try {
			 numberOfClients = ni.amIFirst();
		} catch (RemoteException e) {
			System.out.println("Couldn't reach NamingServer with RMI.");
		}
		ni = null;
		if (numberOfClients == 0){ 
			updateOwnedFiles();
			node.refreshNeighbours();
			int hashNext = node.getNextNode();
			int testHash;
			FileWithFile file;
			ArrayList<FileWithFile> ownerFileList = ownedFiles.getList();
			for (int i=0; i<ownerFileList.size();i++){
				file = ownerFileList.get(i);
				testHash = node.getHashLocation(file.getName());
				if (testHash == hashNext){
					tcp.sendFile(file, hashNext, true, false);
				}
			}
		}
		//System.out.println("replication finished");
		
	}
	
	public void shutDownReplication() {
		System.out.println("The files are be�ng replicated to other nodes. Please wait...");
		
		//STEP 1: sending all the replicated files to the right nodes and adjusting the file fiches
		ArrayList<FileWithFile> repList = repFiles.getList();
		node.refreshNeighbours();
		int hashPrevious = node.getPreviousNode();
		int numberOfNodes = 0;
		ClientToNamingServerInterface ni = node.makeNI();
		try {
			numberOfNodes = ni.amIFirst();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ni = null;
		//for more then two nodes:
		if (numberOfNodes == 0){
			int hashPreviousPrevious = node.getPreviousPreviousNode();
			FileWithFile file;
			for (int i=0;i<repList.size();i++){
				file = repList.get(i);
				if (file.getHashLocalOwner()==hashPrevious){ //if the previous node has the file locally
					if (file.isOwner()){// if this node is the owner
						//do:
						//- remove this node from the fiche
						//- add previousPrevious to the fiche
						//- send file to previous previous with no ownership
						file.removeLocation(node.getName());
						ni = node.makeNI();
						try {
							String nodeName = ni.getNameNode(hashPreviousPrevious);
							file.addLocation(nodeName);
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						ni = null;
						tcp.sendFile(file, hashPreviousPrevious, true, false);
					} else { // if this node isn't the owner
						//do:
						//- remove this node from the fiche from the owner
						//- add previousPrevious to the fiche from the owner
						//- send file to previous previous with no ownership
						node.removeLocationFromFileFromOwnerNode(file.getName(), node.getName());
						node.addLocationToFileFromOwnerNodeByHash(file.getName(), hashPreviousPrevious);
						tcp.sendFile(file, hashPreviousPrevious, false, false);
					}
				} else { //if the previous node doesn't have the file lovally
					if (file.isOwner()){ //if this node is the owner
						//do:
						//- remove this node from the fiche
						//- add previous to the fiche
						//- send file to previous with ownership
						file.removeLocation(node.getName());
						ni = node.makeNI();
						try {
							String nodeName = ni.getNameNode(hashPrevious);
							file.addLocation(nodeName);
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						ni = null;
						tcp.sendFile(file, hashPrevious, true, false);
					} else { //if this node isn't the owner
						//do:
						//- remove this node from the fiche from the owner
						//- add previous to the fiche from the owner
						//- send file to previous with no ownership
						node.removeLocationFromFileFromOwnerNode(file.getName(), node.getName());
						node.addLocationToFileFromOwnerNodeByHash(file.getName(), hashPrevious);
						tcp.sendFile(file, hashPrevious, false, false);
					}
				}
			}
			
			
			
		} else if ( numberOfNodes == 1) {
			//do nothing
		} else if ( numberOfNodes == 2) {
			//We don't need to send files but we need to remove this node from the download locations and make that node owner
			FileWithFile file;
			FileFiche fiche;
			for (int i=0;i<repList.size();i++){
				file = repList.get(i);
				if (file.isOwner()){
					fiche = file.getFiche();
					fiche.removeFileLocation(node.getName());
					node.transferOwnerShipToNode(node.getPreviousNode(), fiche);
				} else {
					node.removeLocationFromFileFromOwnerNode(file.getName(), node.getName());
				}
			}
		}
		
		//STEP 2: removing all the local files from this node in the network
		System.out.println("Removing this nodes local files from the network...");
		ArrayList<FileWithFile> localList = localFiles.getList();
		FileWithFile file;
		for (int i=0;i<localList.size();i++){
			file = localList.get(i);
			node.removeRepFileFromNetwork(file.getName());
		}

		//waiting till all files are send
		long sleepTime = 100;
		while(tcp.threadRunning()){
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("All the files are replicated correctly.");
	}
	
	
	public boolean addRepFile(String nameFile, String nameNode, int hashNode, FileFiche fileFiche, boolean transferOwnerShip){
		FileWithFile newFile = new FileWithFile(nameFile,"C:/TEMP/RepFiles/",nameNode,hashNode);
		//when the node has the file already but it needs to be the owner of the file
		if (repFiles.checkFileExists(nameFile)){
			if (transferOwnerShip){ 
				fileFiche.addFileLocation(node.getName());
				repFiles.getFile(nameFile).replaceFiche(fileFiche);
			}
			return false;
		
		//when the node doesn't have the file	
		} else {
			if (transferOwnerShip){ //if the node is gonna be the new owner, the fileFiche should be added
				fileFiche.addFileLocation(node.getName());
				newFile.replaceFiche(fileFiche);
				repFiles.add(newFile);
			} else {
				newFile.removeOwnership(); //for safety
				repFiles.add(newFile);
			}
			return true;
		}
	}
	
	public void removeLocationFromFile(String fileName, String nodeName){
		FileWithFile file = getFileByName(fileName);
		file.removeLocation(nodeName);
	}
	
	//D.m.v. bestandsnaam fileWithFile opvragen als deze voorkomt.
	public FileWithFile getFileByName(String fileName){
		FileWithFile testFile = localFiles.getFile(fileName);
		if (testFile == null){
			testFile = repFiles.getFile(fileName);
		}
		return testFile;
	}
	
	//meegegeven fileWithFile verwijderen uit lijst
	public int removeRepFile(FileWithFile toRemove){
		ownedFiles.removeFileFromList(toRemove);
		int check = repFiles.removeWithFile(toRemove);
		addToDeleteList(toRemove.getName());
		return check;
	}
	
	public int removeLocalFile(FileWithFile toRemove){
		ownedFiles.removeFileFromList(toRemove);
		int check = localFiles.removeWithFile(toRemove);
		addToDeleteList(toRemove.getName());
		return check;
	}
	
	
	//fileWithFile verwijderen op basis van naam
	public boolean removeRepFile(String fileName){
		ownedFiles.removeFileFromList(fileName);
		addToDeleteList(fileName);
		return repFiles.removeFileWithFile(fileName);
		
	}
	
	//method for replicating a file to the right node
	public void replicateFile(FileWithFile fileToSend){
		int ownerHash = node.getHashLocation(fileToSend.getName());
		if (ownerHash == node.getOwnHash()){ //if the ownerhash == this node, the ownership should not be transferred
			node.refreshNeighbours();
			int replicationHash = node.getPreviousNode();
			ClientToNamingServerInterface ni = node.makeNI();
			try {
				String nodeName;
				nodeName = ni.getNameNode(replicationHash);
				fileToSend.addLocation(nodeName);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			tcp.sendFile(fileToSend, replicationHash, false, false); //first boolean: do not transfer ownerhip, second boolean: do not delete file after sending
		} else { //transfer ownership
			tcp.sendFile(fileToSend, ownerHash, true, false);
		}
	}
	
	
	public FileWithFileList getLocalFiles(){
		return localFiles;
	}
	
	public boolean removeOwnerShip(String fileName){
		FileWithFile file = getFileByName(fileName);
		boolean isRemoved = file.removeOwnership();
		updateOwnedFiles();
		return isRemoved;
	}

	public void transferOwnerShip(FileFiche fiche) {
		FileWithFile file = getFileByName(fiche.getFileName());
		fiche.addFileLocation(node.getName()); //to be sure
		if (file == null){
			System.out.println("transfer ownership error: file = null, fiche: "+fiche.getFileName());
		}
		file.replaceFiche(fiche);
	}

	public void addFileLocation(String fileName, String nodeNameToAdd) {
		FileWithFile file = getFileByName(fileName);
		if (file != null){
			file.addLocation(nodeNameToAdd);
		} else {
			System.out.println("Node was trying to add this node to the locations of file "+fileName+", but this node doesn't have the file.");
		}
	}
	
	public void setLockRequestList(FileWithoutFileList newLockRequestList){
		lockRequests = newLockRequestList;
	}
	
	public FileWithoutFileList getAllNodeOwnedFiles(){
		updateOwnedFiles();
		return allNodeOwnedFiles;
	}
	
	public void setAllFileList(FileWithoutFileList newAllFileList){
		allNetworkFiles = newAllFileList;
	}
	
	public FileWithoutFileList getLockRequests(){
		return lockRequests;
	}
	
	public boolean isLockRequest(){
		updateOwnedFiles();
		return lockRequests.isLock();
	}
	
	public ArrayList<String> getNameListLockRequests(){
		return lockRequests.getNameListLockedFiles();
	}
	
	
	public void addToUnlockList(String fileNameToUnlock){
		unlockList.add(fileNameToUnlock);
	}
	
	public void clearUnlockList(){
		unlockList.clear();
	}
	
	public ArrayList<String> getUnlockList(){
		return unlockList;
	}
	
	public ArrayList<String> getDeletedList(){
		return deletedFiles;
	}

	public void printAllFilesInTheNetwork() {
		allNetworkFiles.printAllFiles();
	}
	
	public boolean isFileLocked(String fileName){
		if (allNetworkFiles.isLockOnFile(fileName)){
			return true;
		} else {
			return false;
		}
	}
	
	//method for deleting a file in the whole network
	public void deleteFileFromNetwork(String fileName){
		int numberOfClients = 4;
		ClientToNamingServerInterface ni = node.makeNI();
		try {
			numberOfClients = ni.amIFirst();
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		ni = null;
		if (numberOfClients == 1){
			if (localFiles.checkFileExists(fileName)){
				localFiles.removeFileWithFile(fileName);
			} else {
				System.out.println("The file wasn't found on this node.");
			}
		} else {
			if (allNetworkFiles.existsWithName(fileName)){
				if (allNetworkFiles.isLockOnFile(fileName)){
					System.out.println("The file can't be deleted because there is a lock on it.");
				} else {
					System.out.println("Setting the lock request: ");
					lockRequests.lockFileWithName(fileName);
					long sleepTime = 100;
					while (!allNetworkFiles.isLockOnFile(fileName)){
						try {
							Thread.sleep(sleepTime);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					System.out.println("The lock was set by the agent.");
					node.removeFileFromNetwork(fileName);
					addToDeleteList(fileName);
					unlockList.add(fileName);
					allNetworkFiles.unlockFile(fileName);
					removeFileWithFile(fileName);
					System.out.println("The file was deleted succesfully from all nodes.");
				}
			} else {
				System.out.println("The file doesn't exist.");
			}
		}
	}
	
	//method for deleting a file locally, can only be done if this node isn't the owner and there are still two copies in the network
	public void deleteFileLocally(String fileName){
		int numberOfClients = 4;
		ClientToNamingServerInterface ni = node.makeNI();
		try {
			numberOfClients = ni.amIFirst();
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		ni = null;
		if (numberOfClients == 1){
			System.out.println("The file cannot be deleted locally because this node is the only owner.");
		} else {
			if (hasFile(fileName)){
				if (ownedFiles.checkFileExists(fileName) && localFiles.checkFileExists(fileName)){
					System.out.println("This node is the (local) owner, cannot delete this file.");
				} else {
					int hashOwner = -1;
					ni = node.makeNI();
					try {
						hashOwner = ni.getHashFileLocation(fileName);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					ni = null;
					boolean canBeDeleted = false;
					ClientToClientInterface ctci = node.makeCTCI(hashOwner);
					try {
						canBeDeleted = ctci.canFileBeDeleted(fileName);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (canBeDeleted){
						if (allNetworkFiles.isLockOnFile(fileName)){
							System.out.println("The file can't be deleted because there is a lock on it.");
						} else {
							lockRequests.lockFileWithName(fileName);
							System.out.println("The lock request is set.");
							long sleepTime = 100;
							while (!allNetworkFiles.isLockOnFile(fileName)){
								//sleep a bit if the lock is not yet set 
								try {
									Thread.sleep(sleepTime);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							System.out.println("The file was locked by the agent. Now we can delete the file.");
							node.removeLocationFromFileFromOwnerNode(fileName, node.getName());
							//fileWithFile verwijderen op basis van naam
							removeRepFile(fileName);
							System.out.println("The file was deleted.");
							unlockList.add(fileName);
							allNetworkFiles.unlockFile(fileName);
						}
						
						
					} else {
						System.out.println("The file can't be deleted because there are only 2 copies.");
					}
				}
				
			} else {
				System.out.println("This node doesn't have that file");
			}
		}
		
		
	}
	
	//method for opening a file, if this node doesn't have this file, it will be downloaded.
	public void openFile(String fileName){
		//STEP 1: check if the agent is active by checking the number of clients
		int numberOfClients = 4;
		ClientToNamingServerInterface ni = node.makeNI();
		try {
			numberOfClients = ni.amIFirst();
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		ni = null;
		// if the agent isn't active yet
		if (numberOfClients == 1){
			if (localFiles.checkFileExists(fileName)){
				System.out.println("The file "+fileName+" was located on this node. ");
				System.out.println("Opening the file: ");
				FileWithFile file = localFiles.getFile(fileName);
				file.open();
			} else {
				System.out.println("The file wasn't found.");
			}
		//if the agent is started
		} else {
			// if the file is not locked
			if (!isFileLocked(fileName)){

				lockRequests.lockFileWithName(fileName);
				System.out.println("The lock request is set.");
				long sleepTime = 100;
				while (!allNetworkFiles.isLockOnFile(fileName)){
					//sleep a bit if the lock is not yet set 
					try {
						Thread.sleep(sleepTime);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				System.out.println("The file was locked by the agent. Now we search the file: ");
				
				//if the file is is in the local files:
				if (localFiles.checkFileExists(fileName)){
					System.out.println("The file "+fileName+" was located on this node. ");
					System.out.println("Opening the file: ");
					FileWithFile file = localFiles.getFile(fileName);
					file.open();
					
				//checking the replication files on this node	
				} else if (repFiles.checkFileExists(fileName)){
					System.out.println("The file "+fileName+" was replicated on this node. ");
					System.out.println("Opening the file: ");
					FileWithFile file = repFiles.getFile(fileName);
					file.open();
					
				//checking the network	
				} else if (allNetworkFiles.existsWithName(fileName)){
					ni = node.makeNI();
					try {
						int hashOwner = ni.getHashFileLocation(fileName);
						ni = null;
						ClientToClientInterface ctci = node.makeCTCI(hashOwner);
						boolean isFound = ctci.sendFileTo(fileName, node.getOwnHash());
						// if the file is found
						if (isFound){
							System.out.println("The file "+fileName+" is being send to this node...");
							sleepTime = 100;
							while (tcp.threadRunning()){
							//sleep a bit if the file is not yet been received. 
								try {
									Thread.sleep(sleepTime);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							System.out.println("Opening the file: ");
							FileWithFile file = getFileWithFile(fileName);
							file.open();
							
						// if the file wasn't found
						} else {
							System.out.println("The file couldn't be found on the network.");
						}
						
					} catch (RemoteException e) {
						e.printStackTrace();
						ni = null;
					}
					
					
				} else {
					System.out.println("The file couldn't be found on the network.");
				}
				allNetworkFiles.unlockFile(fileName);
				unlockList.add(fileName);
				while (allNetworkFiles.isLockOnFile(fileName)){
					try {
						Thread.sleep(sleepTime);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				System.out.println("The lock was removed.");
			} else {
				System.out.println("The file is locked and can not be opened/downloaded");
			}
		}
		
		
	}
		
	
	public boolean hasFile(String fileName){
		boolean exists = false;
		if (localFiles.checkFileExists(fileName)){
			exists = true;
			if (!exists){
				if (repFiles.checkFileExists(fileName)){
					exists = true;
				}
			}
		}
		return exists;
	}
	
	//method invoked by an RMI call from a node who wishes to send a file to a certain hash
	public void downloadRequestTo(String fileName, int hashNodeToSend) {
		//getting the file
		updateOwnedFiles();
		FileWithFile file = null;
		if (localFiles.checkFileExists(fileName)){
			file = localFiles.getFile(fileName);
		} else if (repFiles.checkFileExists(fileName)){
			file = repFiles.getFile(fileName);
		} else {
			System.out.println("Download request of file "+fileName+" that doesn't exist on this node to node "+hashNodeToSend);
		}
		
		//sending the file correctly to the node
		//if this node is the owner, we should add the next location to the fiche
		if (file.isOwner()){
			ClientToNamingServerInterface ni = node.makeNI();
			String nodeName;
			try {
				nodeName = ni.getNameNode(hashNodeToSend);
				file.addLocation(nodeName);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		//if this node isn't the owner, we should add te new location to the owners file filefiche.
		} else {
			node.addLocationToFileFromOwnerNodeByHash(fileName, hashNodeToSend);
		}
		//actually sending the file:
		tcp.sendFile(file, hashNodeToSend, false, false);
		
	}
	
	public FileWithFile getFileWithFile (String fileName){
		FileWithFile file = null;
		if (localFiles.checkFileExists(fileName)){
			file = localFiles.getFile(fileName);
		} else {
			file = repFiles.getFile(fileName);
		}
		return file;
	}
	
	public boolean canFileBeDeleted(String fileName){
		boolean can = false;
		if (ownedFiles.checkFileExists(fileName)){
			can = ownedFiles.canFileBeDeleted(fileName);
		}
		return can;
	}
	
	public void removeFileWithFile(String fileName){
		if (ownedFiles.checkFileExists(fileName)){
			ownedFiles.removeFileFromList(fileName);
		}
		if (localFiles.checkFileExists(fileName)){
			localFiles.removeFileWithFile(fileName);
		}
		if (repFiles.checkFileExists(fileName)){
			repFiles.removeFileWithFile(fileName);
		}
	}
	
	public ArrayList<String> getListAllFiles(){
		ClientToNamingServerInterface ni = node.makeNI();
		int numberOfClients = 0;
		try {
			numberOfClients = ni.amIFirst();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (numberOfClients == 1){
			allNetworkFiles.addAllFilesNotAlreadyAdded(localFiles);
			allNetworkFiles.addAllFilesNotAlreadyAdded(repFiles);
		} 
		return allNetworkFiles.getNameList();
	}
	
	public ArrayList<String> getListAllFilesThatCanBeDeletedLocally(){
		ArrayList<String> list = getListAllFiles();
		ArrayList<String> canDeleteList = new ArrayList<>();
		ClientToClientInterface ctci;
		ClientToNamingServerInterface ni;
		int hashOwner;
		boolean canBeDeleted;
		for(int i=0;i<list.size();i++){
			if (!ownedFiles.checkFileExists(list.get(i))){
				ni = node.makeNI();
				try {
					hashOwner = ni.getHashFileLocation(list.get(i));
					ni = null;
					ctci = node.makeCTCI(hashOwner);
					canBeDeleted = ctci.canFileBeDeleted(list.get(i));
					ctci = null;
					if (canBeDeleted){
						canDeleteList.add(list.get(i));
					}
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					ni = null;
					ctci = null;
				}
			}
		}
		return canDeleteList;
	}
	
	//add files to the list that the agent should remove
	public void addToDeleteList(String fileName){
		if (!deletedFiles.contains(fileName))
		{
			node.addFileNameToDeleteListAllNodes(fileName);
			deletedFiles.add(fileName);
			for (int i=0;i<deletedFiles.size();i++){
				allNetworkFiles.removeFileWithName(deletedFiles.get(i));
			}
		}
	}
	
	public void addToDeleteListByNodeClient(String fileName){
		deletedFiles.add(fileName);
		for (int i=0;i<deletedFiles.size();i++){
			allNetworkFiles.removeFileWithName(deletedFiles.get(i));
		}
	}
}
