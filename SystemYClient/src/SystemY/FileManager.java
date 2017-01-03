package SystemY;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

//klasse voor een lijst van bestanden van een node in te bewaren
public class FileManager {
	private FileListWithFile localFiles = null; //files which the node possesses locally
	private FileListWithFile repFiles = null; //files which are replicated to this node or file which are download for any other reason
	private ArrayList<BestandFiche> fileFiches = null; //fiches which hold the locations where a file is stored,, only files where this node is owner of, have a fileFiche
	private NodeClient node = null;		
	private TCP tcp;									
	
	
	public FileManager(NodeClient nodeClient){
		localFiles = new FileListWithFile();
		repFiles = new FileListWithFile();
		fileFiches = new ArrayList<BestandFiche>();
		node = nodeClient;
		tcp = node.getTCP();
		loadLocalFiles();
		startUpReplication();
		
	}
	
	private void startUpReplication() {
		ArrayList<Bestand> list = localFiles.getList();
		for (int i=0; i<list.size();i++){
			replicateFile(list.get(i));
		}
		
	}


	//voegt alle locale bestanden toe bij het opstarten van de node en maakt hun fileFiches aan
	private void loadLocalFiles(){  
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
	
	//methode voor het toevoegen van een bestand aan de lijst
	public int addLocalFile(String nameFile){
		Bestand newFile = new Bestand(nameFile,"C:/TEMP/LocalFiles/",node.getName(),node.getOwnHash());
		if (localFiles.contains(newFile)){
			return -1;
		} else {
			localFiles.add(newFile);
			fileFiches.add(new BestandFiche(newFile.getName(),node.getName()));
			String ip = node.getFileLocation(newFile.getName());
			
			
			return 1;
		}
	}
	
	public int addRepFile(String nameFile, String nameNode, int hashNode, BestandFiche fileFiche){
		Bestand newFile = new Bestand(nameFile,"C:/TEMP/RepFiles/",nameNode,hashNode);
		if (repFiles.contains(newFile)){
			if (fileFiche.isOwner()){
				fileFiches.add(fileFiche);
			}
			return -1;
		} else {
			repFiles.add(newFile);
			return 1;
		}
	}
	
	
	
	//D.m.v. bestandsnaam bestand opvragen als deze voorkomt.
	public Bestand getBestand(String fileName){
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
		fiche.addFileLocation(node.getName());
		if (ownerHash == node.getOwnHash()){
			node.refreshNeighbours();
			int replicationHash = node.getPreviousNode();
			fiche.setNotOwner(); //to indicate this file is not gonna be the owner file
			tcp.sendFile(fileToSend, replicationHash, fiche);
		} else {
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
	
	private void sendFileWithOwnerShip(Bestand fileToSend, int hashDest){
		
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
