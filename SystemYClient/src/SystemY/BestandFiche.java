package SystemY;

import java.util.ArrayList;

public class BestandFiche {
	private String name; //name of the file
	private ArrayList<String> fileLocations; 
	private String localOwner; //name of the local owner of the file
	private boolean isOwner = true;
	private boolean delete = false;
	
	public BestandFiche(String fileName, String localOwnerFile){
		name = fileName;
		localOwner = localOwnerFile;
		fileLocations = new ArrayList<String>();
		fileLocations.add(localOwner);
	}
	
	public int addFileLocation(String nodeName){
		if (fileLocations.contains(nodeName)){
			return -1;
		} else {
			fileLocations.add(nodeName);
			return 1;
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
	
	public void setNotOwner(){
		isOwner = false;
	}
	
	public boolean isOwner(){
		return isOwner;
	}
	
	public void setDeleteFileAfterSending(){
		delete = true;
	}
	
	public boolean deleteFileAfterSending(){
		return delete;
	}
	
	

}
