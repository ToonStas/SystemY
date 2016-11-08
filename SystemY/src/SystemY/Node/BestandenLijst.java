package SystemY.Node;

import java.util.ArrayList;

//klasse voor een lijst van bestanden van een node in te bewaren
public class BestandenLijst {
	private ArrayList<Bestand> lijst = null; 
	
	public BestandenLijst(){
		lijst = new ArrayList<Bestand>();
		
	}
	
	public int addBestand(String naamBestand, String pathBestand){
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
			lijst.add(new Bestand(naamBestand, pathBestand));
			System.out.println("bestand is toegevoegd");
			return 1;
		}
	}
	
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

}
