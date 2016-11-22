package SystemY;

import java.io.File;

//klasse voor een bestand in te bewaren.
public class Bestand {
	private String naam;
	private String path;
	private File bestand;
	private int hash;
	
	public Bestand(String naamBestand, String pathBestand){
		this.naam = naamBestand;
		this.path = pathBestand;
		bestand = new File(this.path);
		calculateHash();
	}
	
	//Deze functie berekend de hash voor deze node zelf bij initialisatie
	private int calculateHash(){
		int tempHash = this.naam.hashCode();
		if (tempHash < 0)
			tempHash = tempHash * -1;
		tempHash = tempHash % 32768;
		this.hash = tempHash;
		return 1;
	}
	
	public int getHash(){
		return this.hash;
	}
	
	public String getNaam(){
		return this.naam;
	}
	
	public File getFile(){
		return this.bestand;
	}
	
	public String getPath(){
		return this.path;
	}
	
	//methode voor het veranderen van de bestandsnaam, de hash wordt als parameter teruggegeven.
	public int changeName(String nieuweNaam){ 
		this.naam = nieuweNaam;
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
