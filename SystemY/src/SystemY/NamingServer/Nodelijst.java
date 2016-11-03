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

	public static JSONArray listofclients = new JSONArray();
	public static List<Node> listofnodes = new ArrayList<Node>();
	
	public static void main(String[] args)
	{
		Nodelijst.addNode("Matthias","192.168.1.4"); 
		Nodelijst.addNode("Floris","192.168.1.2");
		Nodelijst.writeJSON();
		Nodelijst.readJSON();
		Nodelijst.removeNode(0);
		Nodelijst.addNode("Matthias","192.168.1.4");
		Nodelijst.writeJSON();
		Nodelijst.readJSON();
		
	}
	
	public static void addNode(String name, String ipaddr)
	{
		Node newnode = new Node(name,ipaddr);
		listofnodes.add(newnode);
		Nodelijst.updateJSON(Integer.MAX_VALUE);
	}
	public static void removeNode(int place)
	{
		listofnodes.remove(place);
		Nodelijst.updateJSON(place);
	}
	
	public static void updateJSON(int index) // als je integermax value doorgeeft voeg je een node toe anders remove je de node op de meegegeven index.
	{
		if(index == Integer.MAX_VALUE)
		{
			listofclients.add(listofnodes.get(listofnodes.size()-1).Node);
		}
		else
		{
			listofclients.remove(index);
		}
		
	}
	
	public static void writeJSON()
	{
		try
		{
			File file=new File("C:/TEMP/JSONFile.json");
			file.createNewFile(); 
			FileWriter fileWriter = new FileWriter(file);
			System.out.println("\nWriting JSON object to file");
			System.out.println("-----------------------");
			System.out.print(listofclients);
			fileWriter.write(listofclients.toJSONString());
			fileWriter.flush();
			fileWriter.close();
		
		
		}
		catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	public static void readJSON()
	{
		JSONParser parser = new JSONParser();
		listofnodes.clear();
		listofclients.clear();
		try 
		{ 
			Object obj = parser.parse(new FileReader("C:/TEMP/JSONFile.json")); 
			JSONArray jsonarray = (JSONArray) obj;
			   
			for (int i = 0; i < jsonarray.size(); i++) {
			    JSONObject jsonobject = (JSONObject) jsonarray.get(i);
			    String tempName = (String) jsonobject.get("Name");
			    String tempIpadress = (String) jsonobject.get("IpAdress");
			    Nodelijst.addNode(tempName, tempIpadress);
			}
			System.out.println("\nreading JSON object from file");
			System.out.println("-----------------------");
			System.out.print(listofclients);
					
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
