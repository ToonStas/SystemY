package SystemY;

import java.io.File;
import java.rmi.RemoteException;
import java.util.ArrayList;

//Class which manages all files and the lists it has
public class FileManager {
	private FileListWithFile localFiles = null; //files which the node possesses locally
	private FileListWithFile repFiles = null; //files which are replicated to this node or file which are download for any other reason
	private FileListWithFile ownerFiles = null;
	private FileListWithFile filesToReplicate = null; //list of files which are not yet replicated because this node was the first one
	private NodeClient node = null;		
	private TCP tcp;	
	private Thread fileChecker;
	public volatile boolean runThread = false;
	
	public FileManager(NodeClient nodeClient){
		//initializing the private parameters
		localFiles = new FileListWithFile();
		repFiles = new FileListWithFile();
		node = nodeClient;
		tcp = node.getTCP();
		ownerFiles = new FileListWithFile();
		filesToReplicate = new FileListWithFile();
		
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
	
	public void updateOwnerFiles(){
		ownerFiles.clearList();
		ownerFiles.addAll(localFiles.getOwnerFiles());
		ownerFiles.addAll(repFiles.getOwnerFiles());
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
		System.out.println("Printing all the files on this node: ");
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
		Bestand newFile = new Bestand(nameFile,"C:/TEMP/LocalFiles/",node.getName(),node.getOwnHash());
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
		ArrayList<Bestand> replicateList = filesToReplicate.getList();
		while (replicateList.isEmpty()!=true){
			Bestand file = replicateList.get(0);
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
			updateOwnerFiles();
			node.refreshNeighbours();
			int hashNext = node.getNextNode();
			int testHash;
			Bestand file;
			ArrayList<Bestand> ownerFileList = ownerFiles.getList();
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
		ArrayList<Bestand> repList = repFiles.getList();
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
			Bestand file;
			for (int i=0;i<repList.size();i++){
				file = repList.get(i);
				if (file.isOwner()){ //if this node is the owner of the file
					file.removeLocation(node.getName());
					if (file.getHashLocalOwner()==hashPrevious){// if the previous node has the file locally
						node.transferOwnerShipToNode(hashPrevious, file.getFiche());
						tcp.sendFile(file, hashPreviousPrevious, false, false);
					} else { // if the previous node doesn't have the file locally
						tcp.sendFile(file, hashPrevious, true, false);
					}
				} else { //if this node isn't the owner of the file
					if (file.getHashLocalOwner()==hashPrevious){
						
					} else {
						tcp.sendFile(file, hashPreviousPrevious, false, false);
						node.removeLocationFromFileFromOwnerNode(file.getName(), node.getName());
						node.addLocationToFileFromOwnerNode(file.getName(), hashPreviousPrevious);
					}
				}
			}
			
			long sleepTime = 100;
			while(!tcp.checkSendThreadList()){
				try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		} else if ( numberOfNodes == 1) {
			//do nothing
		} else if ( numberOfNodes == 2) {
			//We don't need to send files but we need to remove this node from the download locations and make that node owner
			Bestand file;
			BestandFiche fiche;
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
		System.out.println("All the files are replicated correctly.");
		
		
		//STEP 2: removing all the local files from this node in the network
		System.out.println("Removing this nodes local files from the network...");
		ArrayList<Bestand> localList = localFiles.getList();
		Bestand file;
		String name;
		for (int i=0;i<localList.size();i++){
			file = localList.get(i);
			node.removeFileFromNetwork(file.getName());
		}
		System.out.println("All the nodes local files are removed from the network.");
		
	}
	
	public boolean addRepFile(String nameFile, String nameNode, int hashNode, BestandFiche fileFiche, boolean transferOwnerShip){
		Bestand newFile = new Bestand(nameFile,"C:/TEMP/RepFiles/",nameNode,hashNode);
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
		Bestand file = getFileByName(fileName);
		file.removeLocation(nodeName);
	}
	
	//D.m.v. bestandsnaam bestand opvragen als deze voorkomt.
	public Bestand getFileByName(String fileName){
		Bestand testFile = localFiles.getFile(fileName);
		if (testFile == null){
			testFile = repFiles.getFile(fileName);
		}
		return testFile;
	}
	
	//meegegeven bestand verwijderen uit lijst
	public int removeRepFile(Bestand toRemove){
		int check = repFiles.removeWithFile(toRemove);
		return check;
	}
	
	public int removeLocalFile(Bestand toRemove){
		int check = localFiles.removeWithFile(toRemove);
		return check;
	}
	
	
	//bestand verwijderen op basis van naam
	public boolean removeRepFile(String fileName){
		return repFiles.removeFileWithFile(fileName);
		
	}
	
	//method for replicating a file to the right node
	public void replicateFile(Bestand fileToSend){
		System.out.println("replicating file "+fileToSend.getName());
		int ownerHash = node.getHashLocation(fileToSend.getName());
		if (ownerHash == node.getOwnHash()){ //if the ownerhash == this node, the ownership should not be transferred
			node.refreshNeighbours();
			int replicationHash = node.getPreviousNode();
			tcp.sendFile(fileToSend, replicationHash, false, false); //first boolean: do not transfer ownerhip, second boolean: do not delete file after sending
		} else { //transfer ownership
			tcp.sendFile(fileToSend, ownerHash, true, false);
		}
	}
	
	
	public FileListWithFile getLocalFiles(){
		return localFiles;
	}
	
	public boolean removeOwnerShip(String fileName){
		Bestand file = getFileByName(fileName);
		boolean isRemoved = file.removeOwnership();
		updateOwnerFiles();
		return isRemoved;
	}

	public void transferOwnerShip(BestandFiche fiche) {
		System.out.println("transfer ownership message: searching file for fiche: "+fiche.getFileName());
		Bestand file = getFileByName(fiche.getFileName());
		fiche.addFileLocation(node.getName()); //to be sure
		if (file == null){
			System.out.println("transfer ownership error: file = null, fiche: "+fiche.getFileName());
		}
		file.replaceFiche(fiche);
	}

	public void addFileLocation(String fileName, String nodeNameToAdd) {
		
		if ()
		
	}


}
