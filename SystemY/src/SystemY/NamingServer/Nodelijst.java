package SystemY.NamingServer;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Nodelijst {

	private static JSONArray listOfClients;
	private static Map<Integer, String> listOfNodes;
	public static Nodelijst nodeLijst;
	
	public static void main(String[] args) {
		nodeLijst = new Nodelijst();
		listOfClients = new JSONArray();
		listOfNodes = new TreeMap<>();
		nodeLijst.addNode("Matthias", "192.168.1.4");
		nodeLijst.addNode("Floris", "192.168.1.2");
		nodeLijst.addNode("Matthias", "192.168.1.4");
		nodeLijst.writeJSON();
		nodeLijst.readJSON();
	}

	public Nodelijst() {
		
	}

	public int addNode(String name, String ipaddr) {
		int val = 0;
		int hash = calculatehash(name);
		System.out.println(hash);
		
			if (listOfNodes.containsKey(hash)) {
				System.out.println("bestaat al");
			} else {
				System.out.println("test");
				Node node = new Node(name, ipaddr);
				listOfNodes.put(node.getHash(), node.getIpAdress());
				updateJSON(Integer.MAX_VALUE, node);
			}
		
		return val;
	}

	public void removeNode(int place) {
		listOfNodes.remove(place);
		updateJSON(place, null);
	}

	public void updateJSON(int index, Node node) // als je integermax value doorgeeft voeg
										// je een node toe anders verwijder je
										// de node op de meegegeven index.
	{
		if (index == Integer.MAX_VALUE) {
			listOfClients.add(null);
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

	public int calculatehash(String name) {
		int tempHash = this.hashCode();
		if (tempHash < 0)
			tempHash = tempHash * -1;
		tempHash = tempHash % 32768;
		return tempHash;
	}
}
