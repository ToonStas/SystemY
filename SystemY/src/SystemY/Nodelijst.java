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
	private Map<Integer, String> listOfNodes;

	public Nodelijst() {
		listOfClients = new JSONArray();
		listOfNodes = new TreeMap<>();
	}

	public int addNode(String name, String ipaddr) {
		int val = 0;
		int hash = calculateHash(name);
		
			if (listOfNodes.containsKey(hash)) {
				val = 0;
			} else {
				NodeNamingServer node = new NodeNamingServer(name, ipaddr);
				listOfNodes.put(node.getHash(), node.getIpAdress());
				updateJSON(Integer.MAX_VALUE, node);
				val = 1;
			}
		
		return val;
	}

	public void removeNode(int place) {
		listOfNodes.remove(place);
		updateJSON(place, null);
	}

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

	public void writeJSON() {
		try {
			File file = new File("C:/TEMP/JSONFile.json");
			file.createNewFile();
			FileWriter fileWriter = new FileWriter(file);
			System.out.println("\nWriting JSON object to file");
			System.out.println("-----------------------");
			System.out.print(listOfClients);
			fileWriter.write(listOfClients.toJSONString());
			fileWriter.flush();
			fileWriter.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void readJSON() {
		JSONParser parser = new JSONParser();
		listOfNodes.clear();
		listOfClients.clear();
		try {
			Object obj = parser.parse(new FileReader("C:/TEMP/JSONFile.json"));
			JSONArray jsonarray = (JSONArray) obj;

			for (int i = 0; i < jsonarray.size(); i++) {
				JSONObject jsonobject = (JSONObject) jsonarray.get(i);
				String tempName = (String) jsonobject.get("Name");
				String tempIpadress = (String) jsonobject.get("IpAdress");
				addNode(tempName, tempIpadress);
			}
			System.out.println("\nreading JSON object from file");
			System.out.println("-----------------------");
			System.out.print(listOfClients);

		} catch (ParseException | IOException e) {
			e.printStackTrace();
		}

	}

    public int calculateHash(String nodeNaam){ //Deze functie berekend de hash van een String als parameter.
        int tempHash = nodeNaam.hashCode();
        if (tempHash < 0)
            tempHash = tempHash * -1;
        tempHash = tempHash % 32768;
        return tempHash;
    }

}
