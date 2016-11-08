package SystemY.NamingServer;

public class NamingServer {

	public static void main(String[] args) {
		Nodelijst nodeLijst = new Nodelijst();
		
		nodeLijst.addNode("Matthias", "192.168.1.4");
		nodeLijst.addNode("Floris", "192.168.1.2");
		nodeLijst.addNode("Matthias", "192.168.1.4");
		nodeLijst.writeJSON();
		nodeLijst.readJSON();

	}

}


//Namingserver:
//--> klasse nodelijst (bevat nodes)
//==--> Methode JSON serialisatie
//==--> Methode voor toevoegen nodes (hashing)
//--> klasse node (bevat ip's, bestandsnamen en adressen, + attributen)
//==--> 
//--> abstracte klasse voor communicatie

//Node:
//--> klasse bestandenlijst
//--> abstracte klasse voor communicatie


//Toon: 192.168.1.1 (server)
//Thijs: 192.168.1.2 (node)
//Floris: 192.168.1.3 (node)
//Matthias: 192.168.1.4 (node)