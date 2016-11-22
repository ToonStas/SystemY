package SystemY;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import org.json.simple.*;
import org.json.simple.parser.*;

public class Nodelijst {

	private JSONArray listOfClients;
	private Map<Integer, NodeNamingServer> listOfNodes;

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
			val = 0;
		} else {
			NodeNamingServer node = new NodeNamingServer(name, ipaddr);
			listOfNodes.put(node.getHash(), node);
			updateJSON(Integer.MAX_VALUE, node);
			val = 1;
		}
		return val;
	}

	//het verwijderen van een node uit de nodelijst
	public void removeNode(int place) {
		listOfNodes.remove(place);
		updateJSON(place, null);
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
			System.out.println("\nWriting JSON object to file");
			System.out.println("-----------------------");
			System.out.println(listOfClients);
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
