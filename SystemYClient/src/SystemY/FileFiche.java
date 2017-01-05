package SystemY;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class FileFiche implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name; //name of the file
	private ArrayList<String> fileLocations; 
	private String localOwner; //name of the local owner of the file
	private boolean lockRequest;
	
	public FileFiche(String fileName, String localOwnerFile){
		name = fileName;
		localOwner = localOwnerFile;
		fileLocations = new ArrayList<String>();
		fileLocations.add(localOwner);
		lockRequest = false;
	}
	
	public void setLockRequest(){
		lockRequest = true;
	}
	
	public boolean isLockRequest(){
		return lockRequest;
	}
	
	public void releaseLockRequest(){
		lockRequest = false;
	}
	
	public boolean addFileLocation(String nodeName){
		if (fileLocations.contains(nodeName)){
			return false;
		} else {
			fileLocations.add(nodeName);
			return true;
		}
	}
	
	public boolean removeFileLocation(String nodeName){
		if (fileLocations.contains(nodeName)){
			fileLocations.remove(nodeName);
			return true;
		} else {
			return false;
		}
	}
	
	public String getLocalOwner(){
		return localOwner;
	}
	
	public String getFileName(){
		return name;
	}
	
	public ArrayList<String> getfileLocations(){
		return fileLocations;
	}
	
	public String getRandomLocation(){
		Random ran = new Random();
		int index = ran.nextInt(fileLocations.size());
		return fileLocations.get(index);
	}
	
	

}
