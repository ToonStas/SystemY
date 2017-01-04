package SystemY;

import java.io.File;

//klasse voor een bestand in te bewaren.
public class Bestand {
	private String name;
	private String path;
	private File bestand;
	private int hash;
	private int hashLocalOwner;
	private String nameLocalOwner;
	private BestandFiche fiche;
	private boolean isLocked;
	private boolean isOwner;
	
	
	public Bestand(String naamBestand, String pathBestand, String nameLocalOwner, int hashLocalOwner){
		name = naamBestand;
		path = pathBestand;
		bestand = new File(path+"/" + name);
		this.nameLocalOwner = nameLocalOwner;
		this.hashLocalOwner = hashLocalOwner;
		calculateHash();

	}
	
	//Deze functie berekent de hash voor deze node zelf bij initialisatie
	private int calculateHash(){
		int tempHash = name.hashCode();
		if (tempHash < 0)
			tempHash = tempHash * -1;
		tempHash = tempHash % 32768;
		hash = tempHash;
		return 1;
	}
	
	public int getHash(){
		return hash;
	}
	
	public String getNameLocalOwner(){
		return nameLocalOwner;
	}
	
	public int getHashLocalOwner(){
		return hashLocalOwner;
	}
	
	public String getName(){
		return name;
	}
	
	public File getFile(){
		return bestand;
	}
	
	public String getPath(){
		return path;
	}
	
	public String getFullPath(){
		String fullPath = path + name;
		return fullPath;
	}
	
	//methode voor het veranderen van de bestandsnaam, de hash wordt als parameter teruggegeven.
	public int changeName(String nieuweNaam){ 
		this.name = nieuweNaam;
		calculateHash();
		return this.hash;
	}
	
	//methode voor het cotroleren of de hash al bestaat, true als deze al bestaat
	public boolean checkName(String checkNaam){
		int tempHash = checkNaam.hashCode();
		if (tempHash < 0)
			tempHash = tempHash * -1;
		tempHash = tempHash % 32768;
		if (tempHash == this.hash)
			return true;
		else
			return false;
	}
	
	public void deleteFile(){
		bestand.delete();
	}
	
	public boolean addOwnerFiche(String ownerNodeName){ //returns true if Bestand doesn't have a fiche yet
		if (isOwner){
			fiche = new BestandFiche(bestand.getName(),ownerNodeName);
			return false;
		} else {
			fiche = new BestandFiche(bestand.getName(),ownerNodeName);
			isOwner = true;
			return true;
		}
	}
	
	public boolean isOwner(){
		return isOwner;
	}
	
	public boolean removeOwnership(){
		fiche = null;
		if (isOwner){
			isOwner = false;
			return true; //because he was an owner and the ownership is removed now
		} else {
			return false; //because he wasn't an owner
		}
	}
	
	public boolean addLocation(String newLocationNodeName){
		if (isOwner){
			fiche.addFileLocation(newLocationNodeName);
			return true;
		} else {
			return false; //no fiche existing
		}
		
	}
	
	public String getRandomFileLocation(){ //return null if there is no fiche
		String nodeName = null;
		if (isOwner){
			nodeName = fiche.getRandomLocation();
		}
		return nodeName;
	}
	
	public boolean lock(){
		if (isLocked){
			return false;
		} else {
			isLocked = true;
			return true;
		}
	}
	
	public BestandFiche getFiche(){
		if (isOwner){
			return fiche;
		} else {
			return new BestandFiche("deleteFiche","deleteFiche"); //for TCP layer, it needs a fiche in its method, so we give it a false fiche
		}
	}
	
	public void replaceFiche(BestandFiche newFiche){
		isOwner = true;
		fiche = newFiche;
		
	}
	
	public boolean isLocked(){
		return isLocked;
	}
	
	public boolean unlock(){
		if (isLocked){
			isLocked = false;
			return true;
		} else {
			return false;
		}
	}
}
