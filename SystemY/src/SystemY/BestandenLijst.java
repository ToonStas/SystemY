package SystemY;

import java.util.ArrayList;

//klasse voor een lijst van bestanden van een node in te bewaren
public class BestandenLijst {
	private ArrayList<Bestand> lijst = null;
	
	public BestandenLijst(){
		lijst = new ArrayList<Bestand>();
		
	}
	
	//methode voor het tovoegen van een bestand aan de lijst
	public int addBestand(String naamBestand, String pathBestand, int hashOwner, int hashReplicationNode){
		boolean flag = false;
		for (int i = 0;i<lijst.size();i++){
			if (lijst.get(i).checkName(naamBestand)){
				flag = true;
			}
		}
		if (flag){ 
			System.out.println("De bestandsnaam heeft dezelfde hash als een bestaand bestand op deze node.");
			return 0;
		}
		else {	
			lijst.add(new Bestand(naamBestand, pathBestand, hashOwner, hashReplicationNode));
			System.out.println("bestand is toegevoegd");
			return 1;
		}
	}
	
	//D.m.v. bestandsnaam bestand opvragen als deze voorkomt.
	public Bestand getBestand(String naamBestand){
		Bestand testBestand = null;
		for (int i = 0; i < lijst.size(); i++){
			if (lijst.get(i).getNaam()==naamBestand){
				testBestand = lijst.get(i);
			}
		}
		if (testBestand != null){
			System.out.println("bestand succesvol opgehaald.");
			return testBestand;
		}
		else {
			System.out.println("Bestandsnaam komt niet voor in lijst.");
			return testBestand;
		}
			
	}
	
	//meegegeven bestand verwijderen uit lijst
	public int verwijderBestand(Bestand teVerwijderen){
		if (lijst.contains(teVerwijderen)){
			lijst.remove(teVerwijderen);
			return 1;
		}
		else
			return 0;
	}
	
	//bestand verwijderen op basis van naam
	public int verwijderBestandMetNaam(String naamBestand){
		boolean flag = false;
		for (int i = 0; i < lijst.size(); i++){
			if (lijst.get(i).getNaam()==naamBestand){
				lijst.remove(i);
				flag = true;
			}
		} 
		if (flag){
			System.out.println("Bestand succesvol verwijderd");
			return 1;
		}
		else{
			System.out.println("Bestandsnaam bestaat niet op deze node.");
			return 0;
		}
		
	}
	
	public int getSize(){
		return lijst.size();
	}
}
