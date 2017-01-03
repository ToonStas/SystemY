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
	private ArrayList<BestandFiche> fileFiches = null; //fiches which hold the locations where a file is stored
	private NodeClient node = null;					   //only files where this node is owner of, have a fileFiche
	
	
	public FileManager(NodeClient nodeClient){
		localFiles = new FileListWithFile();
		repFiles = new FileListWithFile();
		fileFiches = new ArrayList<BestandFiche>();
		node = nodeClient;
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
		if ()
		if (repFiles.contains(newFile)){
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
		if (ownerHash == node.getOwnHash()){
			node.refreshNeighbours();
			int replicationHash = node.getPreviousNode();
			sendFileWithoutOwnerShip(fileToSend,replicationHash);
		} else {
			sendFileWithOwnerShip(fileToSend,ownerHash);
		}
	}
	
	private void sendFileWithoutOwnerShip(Bestand fileToSend, int hashDest){
		
	}
	
	private void sendFileWithOwnerShip(Bestand fileToSend, int hashDest){
		
	}
	
	
	/*public void sendFile(Bestand fileToSend, int receiverHash){
		
		String ip="";
		Random ran = new Random();
		int fileID = ran.nextInt(20000); //The file ID is used in the file receive and send requests, they are compared to know if they are transmitting the right file
		try {
			ip = ni.getIP(receiverHash);
		} catch (RemoteException e1) {
			System.out.println("Couldn't fetch IP from Namingserver");
			failure(receiverHash); //when we can't fetch te ip it's likely the node shut down unexpectedly
			e1.printStackTrace();
		}
		int fileSize = ((int) fileToSend.getFile().length())+1000;
		
		
		try {
			SendFileRequest sendRequest = new SendFileRequest(fileToSend.getFile(),InetAddress.getByName(ip),fileID,receiverHash);
			ReceiveFileRequest receiveRequest = new ReceiveFileRequest(InetAddress.getLocalHost(),fileToSend.getName(), fileToSend.getFullPath(), fileSize, fileID, fileToSend.getHashOwner(), fileToSend.getHashReplicationNode());
			ClientToClientInterface ctci = makeCTCI(receiverHash);
			ctci.setReceiveRequest(receiveRequest);
			ctci = null;
			tcp.addSendRequest(sendRequest);
			
		} catch (RemoteException e) {
			System.err.println("NamingServer exception: " + e.getMessage());
			failure(receiverHash); //when we can't connect to the node we assume it failed.
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
	
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
