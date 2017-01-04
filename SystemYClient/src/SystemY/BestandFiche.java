package SystemY;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class BestandFiche implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name; //name of the file
	private ArrayList<String> fileLocations; 
	private String localOwner; //name of the local owner of the file
	private boolean isNewOwner = true; //this determines if this file will be an ownerfile when it gets send to a another node
	private boolean delete = false; //this determines if this file will be deleted after being send to another node
	
	public BestandFiche(String fileName, String localOwnerFile){
		name = fileName;
		localOwner = localOwnerFile;
		fileLocations = new ArrayList<String>();
		fileLocations.add(localOwner);
	}
	
	public boolean addFileLocation(String nodeName){
		if (fileLocations.contains(nodeName)){
			return false;
		} else {
			fileLocations.add(nodeName);
			return true;
		}
	}
	
	public int removeFileLocation(String nodeName){
		if (fileLocations.contains(nodeName)){
			return -1;
		} else {
			fileLocations.remove(nodeName);
			return 1;
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
	
	public void setNotNewOwner(){
		isNewOwner = false;
	}
	
	public void setNewOwner(){
		isNewOwner = true;
	}
	
	public boolean isNewOwner(){
		return isNewOwner;
	}
	
	public void setDeleteFileAfterSending(){
		delete = true;
	}
	
	public boolean deleteFileAfterSending(){
		return delete;
	}
	
	public String getRandomLocation(){
		Random ran = new Random();
		int index = ran.nextInt(fileLocations.size());
		return fileLocations.get(index);
	}
	
	

}
