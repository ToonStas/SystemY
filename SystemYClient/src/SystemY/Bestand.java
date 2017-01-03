package SystemY;

import java.io.File;

//klasse voor een bestand in te bewaren.
public class Bestand {
	private String name;
	private String path;
	private File bestand;
	private int hash;
	private String localOwner;
	
	
	public Bestand(String naamBestand, String pathBestand, String nameLocalOwner){
		name = naamBestand;
		path = pathBestand;
		bestand = new File(path+"/" + name);
		localOwner = nameLocalOwner;
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
	
	public String getLocalOwner(){
		return localOwner;
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
}
