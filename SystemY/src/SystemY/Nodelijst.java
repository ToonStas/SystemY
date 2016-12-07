package SystemY;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Map;
import java.util.TreeMap;

import org.json.simple.*;
import org.json.simple.parser.*;

public class Nodelijst {

	private JSONArray listOfClients;
	private Map<Integer, NodeNamingServer> listOfNodes; //hash, node

	public Nodelijst() {
		listOfClients = new JSONArray();
		listOfNodes = new TreeMap<>();
	}

	//het toevoegen van een node aan de nodelijst
	public int addNode(String nameIp) {
		String[] parts = nameIp.split(" ");
		String name = parts[0];
		String ipaddr = parts[1];
		
		int val = 0;
		int hash = calculateHash(name);
		
		if (listOfNodes.containsKey(hash)) {
			val = 0;//node already exists
		} else if(hash == 25757){
			//if it is the server himself
			val = 2;
		} else if(hash != 25757) {  //25757 is hash van de naam "Server"
			NodeNamingServer node = new NodeNamingServer(name, ipaddr);
			listOfNodes.put(node.getHash(), node);
			updateJSON(Integer.MAX_VALUE, node);
			
			//make an RMI interface for the server to RMI with the node
			makeRMI(node);
			NamingServerToClientInterface nodeInterface = node.getInterface();
			String serverIP;
			try {
				serverIP = InetAddress.getLocalHost().getHostAddress();
				nodeInterface.setServerIP(serverIP);
			} catch (UnknownHostException | RemoteException e) {
				System.out.println("Couldn't set serverIP address: ");
				e.printStackTrace();
			}
			
			val = 1;
		}
		return val;
	}

	private void makeRMI(NodeNamingServer node) {
		String location = "//"+node.getIpAdress()+":1200/Client"+node.getName();
		try {
			NamingServerToClientInterface ntci = (NamingServerToClientInterface) Naming.lookup(location);
			node.addInterface(ntci);
		} catch (Exception e) {
			System.err.println("Exception: Couldn't make NamingServerToClientInterface: " + e.getMessage());
			e.printStackTrace();
		}
	}

	//het verwijderen van een node uit de nodelijst
	public void removeNode(int hash) {
		listOfNodes.remove(hash);
		updateJSON(hash, null);
	}

	//Het up tot date houden van de nodelijst in de JSON array.
	public void updateJSON(int index, NodeNamingServer node) // als je integermax value doorgeeft voeg
										// je een node toe anders verwijder je
										// de node op de meegegeven index.
	{
		if (index == Integer.MAX_VALUE) {
			listOfClients.add(node.Node);
		} else {
			listOfClients.remove(index);
		}
	}

	//het wegschrijven van de JSONArray naar een file
	public void writeJSON() {
		try {
			File file = new File("C:/TEMP/JSONFile.json"); //file waar de informatie word weggeschreven.
			file.createNewFile();
			FileWriter fileWriter = new FileWriter(file);
			fileWriter.write(listOfClients.toJSONString());
			fileWriter.flush();
			fileWriter.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//het inlezen van de JSON file
	public void readJSON() {
		JSONParser parser = new JSONParser();
		listOfNodes.clear();			// eerst de huidige lijsten leeg maken zodat de ingelezen informatie hier niet dubbel in gezet word
		listOfClients.clear();
		try {
			Object obj = parser.parse(new FileReader("C:/TEMP/JSONFile.json"));
			JSONArray jsonarray = (JSONArray) obj;

			for (int i = 0; i < jsonarray.size(); i++) {
				JSONObject jsonobject = (JSONObject) jsonarray.get(i);
				String tempName = (String) jsonobject.get("Name");
				String tempIpadress = (String) jsonobject.get("IpAdress");
				//addNode(tempName, tempIpadress);
			}
			System.out.println("\nreading JSON object from file");
			System.out.println("-----------------------");
			System.out.println(listOfClients);

		} catch (ParseException | IOException e) {
			e.printStackTrace();
		}

	}
	
	//Deze functie berekent de hash van een String als parameter.
    public int calculateHash(String nodeNaam){
        int tempHash = nodeNaam.hashCode();
        if (tempHash < 0)
            tempHash = tempHash * -1;
        tempHash = tempHash % 32768;
        return tempHash;
    }
    
    public TreeMap<Integer, NodeNamingServer> getListOfNodes(){
		return (TreeMap<Integer, NodeNamingServer>) listOfNodes;
    }
    
    public NodeNamingServer getNode(int hash){
		NodeNamingServer node = listOfNodes.get(hash);
    	return node;
    }
    
    public void listAllNodes(){
    	for (Map.Entry<Integer, NodeNamingServer> entry : listOfNodes.entrySet()) {
    	     System.out.println("Hash: " + entry.getKey() + ", Ipadres: " + entry.getValue().getIpAdress() + ", Naam: " + entry.getValue().getName());
    	}
    }

}
