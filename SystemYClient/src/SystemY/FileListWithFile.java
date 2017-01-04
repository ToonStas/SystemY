package SystemY;

import java.util.ArrayList;
import java.util.HashMap;

public class FileListWithFile {
	private ArrayList<Bestand> list;
	
	public FileListWithFile(){
		list = new ArrayList<Bestand>();
	}
	
	public int add(Bestand newFile){
		if (list.contains(newFile)){
			return -1;
		} else {
			list.add(newFile);
			return 1;
		}
	}
	
	public ArrayList<Bestand> getList(){
		return list;
	}
	
	public boolean contains(Bestand testFile){
		boolean exists = false;
		if (list.contains(testFile))
			exists = true;
		return exists;
	}
	
	public int removeFile(Bestand testFile){
		if (list.contains(testFile)){
			testFile.deleteFile();
			list.remove(testFile);
			return 1;
		} else {
			return -1;
		}
	}
	
	public int removeFileWithHash(int fileHash){
		int index = checkFileExistsWithHash(fileHash);
		if (index == -1){
			return -1;
		} else {
			removeFile(list.get(index));
			return 1;
		}
	}
	
	public int removeFileWithName(String fileName){
		int index = checkFileExistsWithName(fileName);
		if (index == -1){
			return -1;
		} else {
			removeFile(list.get(index));
			return 1;
		}
	}
	
	//returns -1 if the file doesn't exist, otherwise the index number
	public int checkFileExistsWithName(String fileName){
		int index = -1;
		Bestand testFile = null;
		for (int i = 0; i<list.size(); i++){
			testFile = list.get(i);
			if (testFile.getName()==fileName){
				index = i;
			}
		}
		return index;
	}
	
	public Bestand getFile(String fileName){
		int index = checkFileExistsWithName(fileName);
		Bestand testFile = null;
		if (index == -1){
			return testFile;
		} else {
			return list.get(index);
		}
	}
	
	public int checkFileExistsWithHash(int fileHash){
		int index = -1;
		Bestand testFile = null;
		for (int i = 0; i<list.size(); i++){
			testFile = list.get(i);
			if (testFile.getHash()==fileHash){
				index = i;
			}
		}
		return index;
	}
	
	public Bestand getFileWithHash(int fileHash){
		int index = checkFileExistsWithHash(fileHash);
		Bestand testFile = null;
		if (index == -1){
			return testFile;
		} else {
			return list.get(index);
		}
	}
	
	public void printFiles(){
		for (int i = 0; i<list.size(); i++){
			System.out.println("Name: "+list.get(i).getName() +" hash: "+list.get(i).getHash());
		}
	}
	
	public ArrayList<String> getOwnerFiles(ArrayList<BestandFiche> fileFiches){
	ArrayList<String> ownerFiles = new ArrayList<>();
	String testFileName = null;
	int indexFileList = -1;
	for(int i=0; i<fileFiches.size();i++){
		testFileName = fileFiches.get(i).getFileName();
		indexFileList = checkFileExistsWithName(testFileName);
		if (indexFileList != -1){
			ownerFiles.add(testFileName);
		} else {
			fileFiches.remove(i);
		}
	}
	return ownerFiles;
	}
}
