package SystemY;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class NamingServer extends UnicastRemoteObject implements NamingServerInterface {
	private static final long serialVersionUID = 1L;
	private Nodelijst nodeLijst;

	public NamingServer() throws RemoteException{
		super();
		nodeLijst = new Nodelijst();
		
		nodeLijst.addNode("Matthias", "192.168.1.4");
		nodeLijst.addNode("Floris", "192.168.1.2");
		nodeLijst.addNode("Matthias", "192.168.1.4");
		//nodeLijst.writeJSON();
		//nodeLijst.readJSON();

	}
	
	public String getFileLocation(String fileName){
		//TODO itereer door lijst met bestanden voor gekozen fileName en return dan het ipadres van de eigenaar
		String location = "ipadres";
		int hash = nodeLijst.calculateHash(fileName);
		TreeMap<Integer, String> listOfNodes = new TreeMap<>();
		listOfNodes = nodeLijst.getListOfNodes();
		
		location = listOfNodes.floorEntry(hash).getValue();
		
		
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