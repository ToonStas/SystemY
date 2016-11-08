package SystemY;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class NamingServer extends UnicastRemoteObject implements NamingServerInterface {
	private static final long serialVersionUID = 1L;

	public NamingServer() throws RemoteException{
		Nodelijst nodeLijst = new Nodelijst();
		
		nodeLijst.addNode("Matthias", "192.168.1.4");
		nodeLijst.addNode("Floris", "192.168.1.2");
		nodeLijst.addNode("Matthias", "192.168.1.4");
		nodeLijst.writeJSON();
		nodeLijst.readJSON();

	}
	
	public String getFileLocation(String fileName){
		String location = null;
		
		return location;
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