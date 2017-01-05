package SystemY;

import java.io.Serializable;
import java.util.ArrayList;

public class FileListWithoutFile implements Serializable{
	private ArrayList<FileWithoutFile> list;
	
	public FileListWithoutFile(){
		list = new ArrayList<>();
	}
	
	public boolean addNewFile(String fileName, boolean isLocked){
		FileWithoutFile file = new FileWithoutFile(fileName,isLocked);
		if (list.contains(file)){
			return false;
		} else {
			list.add(file);
			return true;
		}
	}
	
	public boolean addFile(FileWithoutFile newFile){
		if (list.contains(newFile)){
			return false;
		} else {
			list.add(newFile);
			return true;
		}
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
	
	public FileListWithoutFile getFileListLockedFiles(){
		FileListWithoutFile fileList = new FileListWithoutFile();
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
	
	public void addAll(FileListWithoutFile newFileList){
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
}
