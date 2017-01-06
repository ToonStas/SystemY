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
	private FileWithoutFileList allNetworkFiles = null; //list of all the files in the network
	private FileWithoutFileList allNodeOwnedFiles = null; //list of all owned files on this node, we use the lock function to implement lock requests
	private ArrayList<String> unlockList = null; //list of all the unlocks this node has, contains the names of the files that may by unlocked
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
		unlockList = new ArrayList<>();
		
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
	
	public void setAllFileList(FileWithoutFileList newAllFileList){
		allNetworkFiles = newAllFileList;
	}
	
	public void updateOwnedFiles(){
		ownedFiles.clearList();
		ownedFiles.addAll(localFiles.getOwnerFiles());
		ownedFiles.addAll(repFiles.getOwnerFiles());
		allNodeOwnedFiles.addAllFilesNotAlreadyAdded(ownedFiles); //add the new files
		allNodeOwnedFiles.removeFilesNotContaining(ownedFiles); //remove the deleted files
	}
	
	public FileWithoutFileList getAllNodeOwnedFiles(){
		updateOwnedFiles();
		return allNodeOwnedFiles;
	}
	
	public void setAllNodeOwnedFiles(FileWithoutFileList newAllNodeOwnedFiles){
		allNodeOwnedFiles = newAllNodeOwnedFiles;
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
		System.out.println("The files are beïng replicated to other nodes. Please wait...");
		
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
			node.removeFileFromNetwork(file.getName());
		}

		//waiting till all files are send
		long sleepTime = 100;
		while(tcp.sendThreadRunning()){
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
		int check = repFiles.removeWithFile(toRemove);
		return check;
	}
	
	public int removeLocalFile(FileWithFile toRemove){
		int check = localFiles.removeWithFile(toRemove);
		return check;
	}
	
	
	//fileWithFile verwijderen op basis van naam
	public boolean removeRepFile(String fileName){
		return repFiles.removeFileWithFile(fileName);
		
	}
	
	//method for replicating a file to the right node
	public void replicateFile(FileWithFile fileToSend){
		System.out.println("replicating file "+fileToSend.getName());
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
	
	public boolean isLockRequest(){
		updateOwnedFiles();
		return ownedFiles.isLockRequest();
	}
	
	public ArrayList<String> getNameListLockRequests(){
		return ownedFiles.getNameListLockRequests();
	}
	
	public FileWithFileList getFileListLockRequests(){
		return ownedFiles.getFileListLockRequests();
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

	public void printAllFilesInTheNetwork() {
		allNetworkFiles.printAllFiles();
	}

}
