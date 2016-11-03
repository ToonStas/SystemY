package SystemY.NamingServer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;



public class Nodelijst {

	private JSONArray listOfClients = new JSONArray();
	private List<Node> listOfNodes = new ArrayList<Node>();
	private Nodelijst nodeLijst = new Nodelijst();
	
	public void main(String[] args)
	{
		nodeLijst.addNode("Matthias","192.168.1.4"); 
		nodeLijst.addNode("Floris","192.168.1.2");
		nodeLijst.writeJSON();
		nodeLijst.readJSON();
		nodeLijst.removeNode(0);
		nodeLijst.addNode("Matthias","192.168.1.4");
		nodeLijst.writeJSON();
		nodeLijst.readJSON();
	}
	
	public int addNode(String name, String ipaddr)
	{
		for(int i=0; i<listOfNodes.size(); i++){
			if(listOfNodes.get(i).getName()==name){
				System.out.println("testopesto");
				return 0;
			}else{
				
			}
		}
		if(listOfNodes.contains(name)==false){
			Node newnode = new Node(name,ipaddr);
			listOfNodes.add(newnode);
			nodeLijst.updateJSON(Integer.MAX_VALUE);
			return 1;
		}else{
			return 0;
		}
	}
	
	public void removeNode(int place)
	{
		listOfNodes.remove(place);
		nodeLijst.updateJSON(place);
	}
	
	public void updateJSON(int index) // als je integermax value doorgeeft voeg je een node toe anders remove je de node op de meegegeven index.
	{
		if(index == Integer.MAX_VALUE)
		{
			listOfClients.add(listOfNodes.get(listOfNodes.size()-1).Node);
		}
		else
		{
			listOfClients.remove(index);
		}
	}
	
	public void writeJSON()
	{
		try
		{
			File file=new File("C:/TEMP/JSONFile.json");
			file.createNewFile(); 
			FileWriter fileWriter = new FileWriter(file);
			System.out.println("\nWriting JSON object to file");
			System.out.println("-----------------------");
			System.out.print(listOfClients);
			fileWriter.write(listOfClients.toJSONString());
			fileWriter.flush();
			fileWriter.close();
		
		
		}
		catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	public void readJSON()
	{
		JSONParser parser = new JSONParser();
		listOfNodes.clear();
		listOfClients.clear();
		try 
		{ 
			Object obj = parser.parse(new FileReader("C:/TEMP/JSONFile.json")); 
			JSONArray jsonarray = (JSONArray) obj;
			   
			for (int i = 0; i < jsonarray.size(); i++) {
			    JSONObject jsonobject = (JSONObject) jsonarray.get(i);
			    String tempName = (String) jsonobject.get("Name");
			    String tempIpadress = (String) jsonobject.get("IpAdress");
			    nodeLijst.addNode(tempName, tempIpadress);
			}
			System.out.println("\nreading JSON object from file");
			System.out.println("-----------------------");
			System.out.print(listOfClients);
					
		} 
		catch (FileNotFoundException e) { 
			e.printStackTrace(); 
		} 
		catch (IOException e) { 
			e.printStackTrace(); 
		} 
		catch (ParseException e) { 
			e.printStackTrace(); 
		}
		
	}
}
