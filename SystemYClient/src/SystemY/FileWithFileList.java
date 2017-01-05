package SystemY;

import java.util.ArrayList;
import java.util.HashMap;

public class FileWithFileList {
	private ArrayList<FileWithFile> list;
	
	public FileWithFileList(){
		list = new ArrayList<FileWithFile>();
	}
	
	public int add(FileWithFile newFile){
		if (list.contains(newFile)){
			return -1;
		} else {
			list.add(newFile);
			return 1;
		}
	}
	
	public ArrayList<FileWithFile> getList(){
		return list;
	}
	
	public boolean contains(FileWithFile testFile){
		boolean exists = false;
		if (list.contains(testFile))
			exists = true;
		return exists;
	}
	
	public int removeWithFile(FileWithFile testFile){
		if (list.contains(testFile)){
			testFile.deleteFile();
			list.remove(testFile);
			return 1;
		} else {
			return -1;
		}
	}
	
	public int removeFileFromList(int fileHash){
		int index = checkFileExists(fileHash);
		if (index == -1){
			return -1;
		} else {
			removeFromList(list.get(index));
			return 1;
		}
	}
	
	public int getIndexFile(String fileName){
		int index = -1;
		for (int i=0;i<list.size();i++){
			if (list.get(i).getName().equals(fileName)){
				index = i;
			}
		}
		return index;
	}
	
	public boolean removeFileFromList(String fileName){
		int index = getIndexFile(fileName);
		int minOne = -1;
		if (index == minOne){
			return false;
		} else {
			list.remove(index);
			return true;
		}
	}
	
	public boolean removeFileFromList(FileWithFile file){
		return list.remove(file);
	}
	
	public boolean removeFileWithFile(String fileName){
		if (checkFileExists(fileName)){
			FileWithFile file = getFile(fileName);
			file.deleteFile();
			removeFromList(file);
			return true;
		} else {
			return false;
		}
	}
	
	//returns -1 if the file doesn't exist, otherwise the index number
	public boolean checkFileExists(String fileName){
		boolean exists = false;
		FileWithFile testFile = null;
		for (int i = 0; i<list.size(); i++){
			testFile = list.get(i);
			if (testFile.getName().equals(fileName)){
				exists = true;
			}
		}
		return exists;
	}
	
	public FileWithFile getFile(String fileName){
		FileWithFile file = null;
		int i = 0;
		while(i<list.size() && file == null){
			if (list.get(i).getName().equals(fileName)){
				file = list.get(i);
			}
			i++;
		}
		return file;
	}
	
	public int checkFileExists(int fileHash){
		int index = -1;
		FileWithFile testFile = null;
		for (int i = 0; i<list.size(); i++){
			testFile = list.get(i);
			if (testFile.getHash()==fileHash){
				index = i;
			}
		}
		return index;
	}
	
	public FileWithFile getFileWithHash(int fileHash){
		int index = checkFileExists(fileHash);
		FileWithFile testFile = null;
		if (index == -1){
			return testFile;
		} else {
			return list.get(index);
		}
	}
	
	public void printFiles(){
		String str;
		Integer hash;
		for (int i = 0; i<list.size(); i++){
			System.out.print("Filename: ");
			//making name exact 30 characters wide
			str = list.get(i).getName();
			if (str.length()<=40){
				while (str.length()<40){
					str = str + " ";
				}
			} else {
				str = str.substring(0, 37);
				str = str + "...";
			}
			System.out.print(str+"   Hash: ");
			hash = list.get(i).getHash();
			str = hash.toString();
			while (str.length()<5){
				str = " " + str;
			}
			System.out.print(str+"    ");
			if (list.get(i).isOwner()){
				System.out.print("This node is the owner and the file locations are: ");
				ArrayList<String> locations = list.get(i).getFiche().getfileLocations();
				for (int j=0;j<locations.size();j++){
					System.out.print(locations.get(j)+" ");
				}
			} else {
				System.out.print("This node is NOT the owner.");
			}
			
			System.out.print("\n");
		}
	}
	
	public FileWithFileList getOwnerFiles(){
		FileWithFileList ownerList = new FileWithFileList();
		for (int i=0; i<list.size(); i++){
			if (list.get(i).isOwner()){
				ownerList.add(list.get(i));
			}
		}
		return ownerList;
	}
	
	public void addAll(FileWithFileList newFileList){
		ArrayList<FileWithFile> newList = newFileList.getList();
		for (int i=0;i<newList.size();i++){
			list.add(newList.get(i));
		}
	}
	
	public void removeAllWithFile(FileWithFileList newFileList){
		ArrayList<FileWithFile> newList = newFileList.getList();
		for (int i=0;i<newList.size();i++){
			newList.get(i).deleteFile();
			list.remove(newList.get(i));
		}
	}
	
	public void removeAllFromList(FileWithFileList newFileList){
		ArrayList<FileWithFile> newList = newFileList.getList();
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
	
	public boolean removeFromList(FileWithFile file){
		if (list.contains(file)){
			list.remove(file);
			return true;
		} else {
			return false;
			
		}
	}
	
	public boolean isLockRequest(){
		boolean isRequest = false;
		int i = 0;
		while (i<list.size() && isRequest == false){
			if (list.get(i).isLockRequest()){
				isRequest = true;
			}
			i++;
		}
		return isRequest;
	}
	
	public ArrayList<String> getNameListLockRequests(){
		ArrayList<String> requestList = new ArrayList<>();
		for (int i=0;i<list.size();i++){
			if (list.get(i).isLockRequest()){
				requestList.add(list.get(i).getName());
			}
		}
		return requestList;
	}
	
	public FileWithFileList getFileListLockRequests(){
		FileWithFileList requestList = new FileWithFileList();
		for (int i=0;i<list.size();i++){
			if (list.get(i).isLockRequest()){
				requestList.add(list.get(i));
			}
		}
		return requestList;
	}
}
