package SystemY;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

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

	private void replicateFile(Bestand bestand) {
				
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
		Bestand newFile = new Bestand(nameFile,"C:/TEMP/LocalFiles/",node.getName());
		if (localFiles.contains(newFile)){
			return -1;
		} else {
			localFiles.add(newFile);
			fileFiches.add(new BestandFiche(newFile.getName(),node.getName()));
			String ip = node.getFileLocation(newFile.getName());
			
			return 1;
		}
	}
	
	public int addRepFile(String nameFile){
		Bestand newFile = new Bestand(nameFile,"C:/RepFiles/",node.getName());
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
