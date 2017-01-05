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
	
	public int removeWithFile(Bestand testFile){
		if (list.contains(testFile)){
			testFile.deleteFile();
			list.remove(testFile);
			return 1;
		} else {
			return -1;
		}
	}
	
	public int removeFileWithHashFromList(int fileHash){
		int index = checkFileExistsWithHash(fileHash);
		if (index == -1){
			return -1;
		} else {
			removeFromList(list.get(index));
			return 1;
		}
	}
	
	public int removeFileWithNameFromList(String fileName){
		int index = checkFileExistsWithName(fileName);
		if (index == -1){
			return -1;
		} else {
			removeFromList(list.get(index));
			return 1;
		}
	}
	
	public int removeFileWithNameWithFile(String fileName){
		int index = checkFileExistsWithName(fileName);
		if (index == -1){
			return -1;
		} else {
			removeFromList(list.get(index));
			return 1;
		}
	}
	
	//returns -1 if the file doesn't exist, otherwise the index number
	public int checkFileExistsWithName(String fileName){
		int index = -1;
		Bestand testFile = null;
		for (int i = 0; i<list.size(); i++){
			testFile = list.get(i);
			if (testFile.getName().equals(fileName)){
				index = i;
			}
		}
		return index;
	}
	
	public Bestand getFile(String fileName){
		Bestand file = null;
		int i = 0;
		while(i<list.size() && file == null){
			if (list.get(i).getName().equals(fileName)){
				file = list.get(i);
			}
			i++;
		}
		return file;
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
			System.out.println("Owner: "+list.get(i).isOwner()+", hash: "+list.get(i).getHash()+", Name: "+list.get(i).getName());
		}
	}
	
		public FileListWithFile getOwnerFiles(){
		FileListWithFile ownerList = new FileListWithFile();
		for (int i=0; i<list.size(); i++){
			if (list.get(i).isOwner()){
				ownerList.add(list.get(i));
			}
		}
		return ownerList;
	}
	
	public void addAll(FileListWithFile newFileList){
		ArrayList<Bestand> newList = newFileList.getList();
		for (int i=0;i<newList.size();i++){
			list.add(newList.get(i));
		}
	}
	
	public void removeAllWithFile(FileListWithFile newFileList){
		ArrayList<Bestand> newList = newFileList.getList();
		for (int i=0;i<newList.size();i++){
			newList.get(i).deleteFile();
			list.remove(newList.get(i));
		}
	}
	
	public void removeAllFromList(FileListWithFile newFileList){
		ArrayList<Bestand> newList = newFileList.getList();
		for (int i=0;i<newList.size();i++){
			list.remove(newList.get(i));
		}
	}
	
	public void clearList(){
		list.clear();
	}
	
	public void clearWithFiles(){
		for (int i=0; i<list.size();i++){
			list.get(i).deleteFile();
		}
		list.clear();
	}
	
	public boolean isEmpty(){
		return list.isEmpty();
	}
	
	public int getSize(){
		return list.size();
	}
	
	public boolean removeFromList(Bestand file){
		if (list.contains(file)){
			list.remove(file);
			return true;
		} else {
			return false;
			
		}
	}
	
	
}
