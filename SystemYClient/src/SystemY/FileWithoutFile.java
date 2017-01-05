package SystemY;

import java.io.Serializable;

public class FileWithoutFile implements Serializable{
	private String fileName;
	private boolean isLocked;
	
	public FileWithoutFile(String fileNameFile, boolean isLockedFile){
		fileName = fileNameFile;
		isLocked = isLockedFile;
	}
	
	public String getName(){
		return fileName;
	}
	
	public void lock(){
		isLocked = true;
	}
	
	public void unlock(){
		isLocked = false;
	}
	
	public boolean isLocked(){
		return isLocked;
	}
}
