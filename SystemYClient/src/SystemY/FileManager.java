package SystemY;

import java.io.File;
import java.rmi.RemoteException;
import java.util.ArrayList;

//Class which manages all files and the lists it has
public class FileManager {
	private FileListWithFile localFiles = null; //files which the node possesses locally
	private FileListWithFile repFiles = null; //files which are replicated to this node or file which are download for any other reason
	private ArrayList<BestandFiche> fileFiches = null; //fiches which hold the locations where a file is stored,, only files where this node is owner of, have a fileFiche
	private ArrayList<String> filesToReplicate = null; //list of files which are not yet replicated because this node was the first one
	private ArrayList<String> ownerFiles = null;
	private NodeClient node = null;		
	private TCP tcp;									
	
	
	public FileManager(NodeClient nodeClient){
		//initializing the private parameters
		localFiles = new FileListWithFile();
		repFiles = new FileListWithFile();
		fileFiches = new ArrayList<BestandFiche>();
		node = nodeClient;
		tcp = node.getTCP();
		ownerFiles = new ArrayList<String>();
		filesToReplicate = new ArrayList<String>();
		
		//create the file directories
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
		ownerFiles.clear();
		ownerFiles.addAll(localFiles.getOwnerFiles(fileFiches));
		ownerFiles.addAll(repFiles.getOwnerFiles(fileFiches));
	}
	
	


	//voegt alle locale bestanden toe bij het opstarten van de node en maakt hun fileFiches aan
	public void loadLocalFiles(){  
		File dir = new File("C:/TEMP/LocalFiles/");
		for (File f : dir.listFiles()) {
			String name = f.getName();
			addLocalFile(name);
			
		}
	}
	
	
	public void printAllFiles(){
		System.out.println("The local files are: ");
		localFiles.printFiles();
		System.out.println("The replication and downloaded files are: ");
		repFiles.printFiles();
	}
	
	//adds a file correctly to the list, they are replicated afterward
	public int addLocalFile(String nameFile){
		Bestand newFile = new Bestand(nameFile,"C:/TEMP/LocalFiles/",node.getName(),node.getOwnHash());
		if (localFiles.contains(newFile)){
			return -1;
		} else {
			localFiles.add(newFile);
			fileFiches.add(new BestandFiche(newFile.getName(),node.getName()));
			ClientToNamingServerInterface ni = node.makeNI();
			try {
				if(ni.amIFirst()!=1){
					replicateFile(newFile);
				}
				else{
					filesToReplicate.add(newFile.getName());
				}
			} catch (RemoteException e) {
				System.out.println("Couldn't reach namingserver via RMI.");
				e.printStackTrace();
			}
			ni = null;
			return 1;
		}
	}
	
	//method which checks if all files are replicated correctly, invoked when a new node enters the network
	public void checkReplication(){
		//checking if this node still has files that need to be replicated
		//if the node was the first node, it may not have replicated it files when it was the only node
		System.out.println("Checking Replication...");
		while (filesToReplicate.isEmpty()!=true){
			Bestand file = getFileByName(filesToReplicate.get(0));
			replicateFile(file);
			filesToReplicate.remove(0);
		}
		
		//checking if the replicated files on this node are replicated to the right node
		updateOwnerFiles();
		node.refreshNeighbours();
		int hashNext = node.getNextNode();
		int testHash;
		Bestand file;
		BestandFiche fiche;
		for (int i=0; i<ownerFiles.size();i++){
			testHash = node.getHashLocation(ownerFiles.get(i));
			if (testHash == hashNext){
				file = getFileByName(ownerFiles.get(i));
				fiche = getFicheByName(ownerFiles.get(i));
				fiche.setNewOwner();
				tcp.sendFile(file, hashNext, fiche);
			}
		}
		System.out.println("replication finished");
		
	}
	
	public int addRepFile(String nameFile, String nameNode, int hashNode, BestandFiche fileFiche){
		Bestand newFile = new Bestand(nameFile,"C:/TEMP/RepFiles/",nameNode,hashNode);
		//when the node has the file already but it needs to be the owner of the file
		if (repFiles.contains(newFile)){
			if (fileFiche.isNewOwner()){ 
				fileFiche.addFileLocation(node.getName());
				removeFicheByName(newFile.getName());
				fileFiches.add(fileFiche);
			}
			return -1;
		
		//when the node doesn't have the file	
		} else {
			if (fileFiche.isNewOwner()){ //if the node is gonna be the new owner, the fileFiche should be added
				fileFiche.addFileLocation(node.getName());
				fileFiches.add(fileFiche);
			}
			repFiles.add(newFile);
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
		int check = repFiles.removeFile(toRemove);
		return check;
	}
	
	public int removeLocalFile(Bestand toRemove){
		int check = localFiles.removeFile(toRemove);
		return check;
	}
	
	
	//bestand verwijderen op basis van naam
	public int removeRepFileWithName(String fileName){
		return repFiles.removeFileWithName(fileName);
		
	}
	
	public void replicateFile(Bestand fileToSend){
		int ownerHash = node.getHashLocation(fileToSend.getName());
		BestandFiche fiche = getFicheByName(fileToSend.getName());
		if (ownerHash == node.getOwnHash()){
			node.refreshNeighbours();
			int replicationHash = node.getPreviousNode();
			fiche.setNotNewOwner(); //to indicate this file is not gonna be the owner file
			
			tcp.sendFile(fileToSend, replicationHash, fiche);
		} else {
			System.out.println("Replicating file "+fileToSend.getName()+" to hash "+ownerHash);
			tcp.sendFile(fileToSend, ownerHash, fiche);
		}
	}
	
	private BestandFiche getFicheByName(String fileName){
		BestandFiche fiche = null;
		for (int i = 0; i<fileFiches.size();i++){
			if (fileFiches.get(i).getFileName() == fileName){
				fiche = fileFiches.get(i);
			}
		}
		return fiche;
	}
	
	public void removeFicheByName(String fileName){
		int index = -1;
		for (int i = 0; i<fileFiches.size();i++){
			if (fileFiches.get(i).getFileName() == fileName){
				index = i;
			}
		}
		if (index != -1){
			fileFiches.remove(index);
		}
	}
	
	public void deleteFileBySendThread(String fileName){
		repFiles.removeFileWithName(fileName);
		//updateAllFiles();
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
