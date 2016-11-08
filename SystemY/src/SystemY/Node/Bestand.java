package SystemY.Node;

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
	
	private int calculateHash(){ //Deze functie berekend de hash voor deze node zelf bij initialisatie
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
	
	public int changeName(String nieuweNaam){ //methode voor het veranderen van de bestandsnaam, de hash wordt als parameter teruggegeven.
		this.naam = nieuweNaam;
		calculateHash();
		return this.hash;
	}
	
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