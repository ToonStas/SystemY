package SystemY;

import java.io.Serializable;
import java.util.ArrayList;

public class FileWithoutFileList implements Serializable{

	private static final long serialVersionUID = 1L;
	private ArrayList<FileWithoutFile> list;
	
	public FileWithoutFileList(){
		list = new ArrayList<>();
	}
	
	public boolean addNewFile(String fileName, boolean isLocked){
		if (existsWithName(fileName)){
			return false;
		} else {
			list.add(new FileWithoutFile(fileName,isLocked));
			return true;
		}
	}
	
	public boolean existsWithName(String fileName){
		boolean exists = false;
		for (int i=0;i<list.size();i++){
			if (list.get(i).getName().equals(fileName)){
				exists = true;
			}
		}
		
		return exists;
	}
	
	
	public boolean addFile(FileWithoutFile newFile){
		if (list.contains(newFile)){
			return false;
		} else {
			list.add(newFile);
			return true;
		}
	}
	
	public boolean removeFileWithName(String fileName){
		boolean removed = false;
		for (int i=0;i<list.size();i++){
			if (list.get(i).getName().equals(fileName)){
				list.remove(i);
				removed = true;
				i--;
			}
		}
		return removed;
	}
	
	public ArrayList<String> getNameListLockedFiles(){
		ArrayList<String> lockList = new ArrayList<>();
		for (int i=0;i<list.size();i++){
			if (list.get(i).isLocked()){
				lockList.add(list.get(i).getName());
			}
		}
		return lockList;
	}
	
	public FileWithoutFileList getFileListLockedFiles(){
		FileWithoutFileList fileList = new FileWithoutFileList();
		for (int i=0;i<list.size();i++){
			if(list.get(i).isLocked()){
				fileList.addFile(list.get(i));
			}
		}
		return fileList;
	}
	
	public boolean isLock(){
		boolean isLock = false;
		for (int i=0;i<list.size();i++){
			if (list.get(i).isLocked()){
				isLock = true;
			}
		}
		return isLock;
	}
	
	public ArrayList<FileWithoutFile> getList(){
		return list;
	}
	
	public void addAll(FileWithoutFileList newFileList){
		if (newFileList != null){
			ArrayList<FileWithoutFile> newList = newFileList.getList();
			for (int i=0;i<newList.size();i++){
				if (!list.contains(newList.get(i))){
					list.add(newList.get(i));
				}
			}
		}
	}
	
	public void addList(ArrayList<FileWithoutFile> newList){
		if (newList != null){
			for (int i=0;i<newList.size();i++){
				if (!list.contains(newList.get(i))){
					list.add(newList.get(i));
				}
			}
		}
	}
	
	public void addAllFilesNotAlreadyAdded(FileWithFileList fileList){
		ArrayList<FileWithFile> newList = fileList.getList();
		for (int i=0;i<newList.size();i++){
			addNewFile(newList.get(i).getName(),false);
		}
	}
	
	public void addAllFilesNotAlreadyAdded(FileWithoutFileList fileList){
		ArrayList<FileWithoutFile> newList = fileList.getList();
		for (int i=0;i<newList.size();i++){
			addNewFile(newList.get(i).getName(),false);
		}
	}
	
	//this function removes the files which are not found in the file list with files
	public void removeFilesNotContaining(FileWithFileList fileListWithFiles){
		ArrayList<String> nameList = new ArrayList<>();
		ArrayList<FileWithFile> fileList = fileListWithFiles.getList();
		//getting a list of all the names which this should contain
		for (int i=0;i<fileList.size();i++){
			nameList.add(fileList.get(i).getName());
		}
		ArrayList<String> nameListThis = new ArrayList<>();
		//getting a list of all the names this list contains
		for (int i=0;i<list.size();i++){
			nameListThis.add(list.get(i).getName());
		}
		
		//removing the names which they both contain, the files which this list contains and the other doesn't, remain
		for (int i=0;i<nameList.size();i++){
			if (nameListThis.contains(nameList.get(i))){
				nameListThis.remove(nameList.get(i));
			}
		}
		//removing the remaining files by name
		for (int i=0;i<nameListThis.size();i++){
			removeFileWithName(nameListThis.get(i));
		}
	}
	
	public void lockAllFilesInThisNameList(ArrayList<String> lockList){
		for (int i=0;i<lockList.size();i++){
			lockFileWithName(lockList.get(i));
		}
	}
	
	public boolean lockFileWithName(String name){
		boolean isLocked = false;
		int i=0;
		while (!isLocked && i<list.size()){
			if (list.get(i).getName().equals(name)){
				list.get(i).lock();
				isLocked = true;
			}
			i++;
		}
		return isLocked;
	}
	
	public void removeAllLocksInThisNameList(ArrayList<String> unlockList){
		for (int i=0;i<unlockList.size();i++){
			unlockFile(unlockList.get(i));
		}
	}
	
	public boolean unlockFile(String name){
		boolean unlocked = false;
		int i=0;
		while (!unlocked && i<list.size()){
			if (list.get(i).getName().equals(name)){
				list.get(i).unlock();
				unlocked = true;
			}
			i++;
		}
		return unlocked;
	}
	
	public void printAllFiles(){
		String str;
		for (int i=0;i<list.size();i++){
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
			System.out.print(str+"    ");
			if (list.get(i).isLocked()){
				System.out.print("    [locked]");
			}
			System.out.print("\n");
		}
		
	}
	
	public boolean isLockOnFile(String fileName){
		boolean isLocked = false;
		for (int i=0;i<list.size();i++){
			if (list.get(i).getName().equals(fileName)&&list.get(i).isLocked()){
				isLocked = true;
			}
		}
		return isLocked;
	}
	
	public ArrayList<String> getNameList(){
		ArrayList<String> nameList = new ArrayList<>();
		for (int i=0;i<list.size();i++){
			nameList.add(list.get(i).getName());
		}
		return nameList;
	}
}
