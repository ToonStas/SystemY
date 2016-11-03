package SystemY.NamingServer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
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
		Nodelijst.addNode("floris","192.168.1.2");
		Nodelijst.addNode("Matthias","192.168.1.4");
		Nodelijst.writeJSON();
	}
	
	public static void addNode(String name, String ipaddr)
	{
		Node newnode = new Node(name,ipaddr);
		listofnodes.add(newnode);
	}
	public static void removeNode(int place)
	{
		listofnodes.remove(place);
	}
	
	@SuppressWarnings("unchecked")
	public static void writeJSON()
	{
		//int size = listofnodes.size();
		for(int i=0;i<listofnodes.size();i++)
		{
			System.out.printf("%d , %d\n",i,listofnodes.size());
			listofclients.add(listofnodes.get(i).Node);
		}
		try
		{
			File file=new File("C:/TEMP/JSONFile.json");
			file.createNewFile(); 
			FileWriter fileWriter = new FileWriter(file);
			System.out.println("Writing JSON object to file");
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
		try 
		{ 
			Object obj = parser.parse(new FileReader("C:/TEMP/JSONFile.json")); 
			JSONArray jsonarray = (JSONArray) obj;
			   
			for (int i = 0; i < jsonarray.size(); i++) {
			    JSONObject jsonobject = (JSONObject) jsonarray.get(i);
			    String tempName = (String) jsonobject.get("name");
			    String tempIpadress = (String) jsonobject.get("ipAdress");
			    int tempHash = (int) jsonobject.get("hash");
			    Nodelijst.addNode(tempName, tempIpadress);
			}
					
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
