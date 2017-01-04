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
	
	
	public FileManager(NodeClient nodeClient){
		//initializing the private parameters
		localFiles = new FileListWithFile();
		repFiles = new FileListWithFile();
		node = nodeClient;
		tcp = node.getTCP();
		ownerFiles = new FileListWithFile();
		filesToReplicate = new FileListWithFile();
		
		//create the file directories if they not already exist
		File file = new File("C:/TEMP/LocalFiles/");
	    if (!file.exists()) {
	       	file.mkdir();
	    }
	    file = new File("C:/TEMP/RepFiles/");
	    if (!file.exists()){
	    	file.mkdir();
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
		Thread fileChecker = new CheckLocalFilesThread(this);
		fileChecker.start();
	}
	
	
	public void printAllFiles(){
		System.out.println("The local files are: ");
		localFiles.printFiles();
		System.out.println("The replication and downloaded files are: ");
		repFiles.printFiles();
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
		int test = 3;
		try {
			 test = ni.amIFirst();
		} catch (RemoteException e) {
			System.out.println("Couldn't reach NamingServer with RMI.");
		}
		ni = null;
		if (test > 2){
			updateOwnerFiles();
			node.refreshNeighbours();
			int hashNext = node.getNextNode();
			int testHash;
			Bestand file;
			BestandFiche fiche;
			String fileName;
			for (int i=0; i<ownerFiles.size();i++){
				fileName = ownerFiles.get(i);
				testHash = node.getHashLocation(fileName);
				if (testHash == hashNext){
					file = getFileByName(fileName);
					fiche = getFicheByName(fileName);
					System.out.println("requested file fiche for file with name: "+fileName+", "+fiche.toString());
					fiche.setNewOwner();
					tcp.sendFile(file, hashNext, fiche);
				}
				updateOwnerFiles();
			}
		}
		//System.out.println("replication finished");
		
	}
	
	public int addRepFile(String nameFile, String nameNode, int hashNode, BestandFiche fileFiche, boolean transferOwnerShip){
		Bestand newFile = new Bestand(nameFile,"C:/TEMP/RepFiles/",nameNode,hashNode);
		//when the node has the file already but it needs to be the owner of the file
		if (repFiles.checkFileExistsWithName(nameFile)!=-1){
			if (transferOwnerShip){ 
				fileFiche.addFileLocation(node.getName());
				repFiles.getFile(nameFile).replaceFiche(fileFiche);
			}
			return -1;
		
		//when the node doesn't have the file	
		} else {
			if (transferOwnerShip){ //if the node is gonna be the new owner, the fileFiche should be added
				fileFiche.addFileLocation(node.getName());
				newFile.replaceFiche(fileFiche);
			} else {
				newFile.removeOwnership(); //for safety
				repFiles.add(newFile);
			}
			return 1;
		}
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
	public int removeRepFileWithName(String fileName){
		return repFiles.removeFileWithNameWithFile(fileName);
		
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
		return file.removeOwnership();
	}
	
	
	
	
	
	
	/*public ArrayList<Bestand> getFilesWithSmallerHash(int hashNewNode){
		int size = lijst.size();
		ArrayList<Bestand> temp = new ArrayList<>();
		for (int i=0; size>i; i++){
			if(lijst.get(i).getHash() >= hashNewNode){
				lijst.get(i).setReplicationNode(hashNewNode);
				temp.add(lijst.get(i));
			}	
		}
		return temp;
	}
	public Bestand getIndex(int index){
		return lijst.get(index);
	}
	
	public void listAllFiles(){
		Iterator<Bestand> iter = lijst.iterator();
		while (iter.hasNext()){
			System.out.println(iter.next().getName());			
		}
	}*/

}
