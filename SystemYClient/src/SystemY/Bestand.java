package SystemY;

import java.io.File;

//klasse voor een bestand in te bewaren.
public class Bestand {
	private String naam;
	private String path;
	private File bestand;
	private int hash;
	private int hashOwner;
	private int hashReplicationNode;
	
	public Bestand(String naamBestand, String pathBestand, int hashOwner, int hashReplicationNode){
		naam = naamBestand;
		path = pathBestand;
		bestand = new File(path+"/" + naam);
		calculateHash();
		this.hashOwner = hashOwner;
		this.hashReplicationNode = hashReplicationNode;
	}
	
	//Deze functie berekent de hash voor deze node zelf bij initialisatie
	private int calculateHash(){
		int tempHash = naam.hashCode();
		if (tempHash < 0)
			tempHash = tempHash * -1;
		tempHash = tempHash % 32768;
		hash = tempHash;
		return 1;
	}
	
	public int getHash(){
		return hash;
	}
	
	public int getHashOwner(){
		return hashOwner;
	}
	
	public int getHashReplicationNode(){
		return hashReplicationNode;
	}
	
	public String getNaam(){
		return naam;
	}
	
	public File getFile(){
		return bestand;
	}
	
	public String getPath(){
		return path;
	}
	
	public String getFullPath(){
		String fullPath = path + naam;
		return fullPath;
	}
	
	//methode voor het veranderen van de bestandsnaam, de hash wordt als parameter teruggegeven.
	public int changeName(String nieuweNaam){ 
		this.naam = nieuweNaam;
		calculateHash();
		return this.hash;
	}
	public void setReplicationNode(int hashNewReplicationNode){
		this.hashReplicationNode = hashNewReplicationNode;
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
}
