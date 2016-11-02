package SystemY.NamingServer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;



public class Nodelijst {

	public static JSONArray listofclients = new JSONArray();
	public static List<Node> listofnodes = new ArrayList<Node>();
	
	public static void main(String[] args)
	{
		/*
		JSONObject client1 = new JSONObject();
		client1.put("Name", "Matthias");					//client name
		client1.put("IPadress", "192.168.1.4");				//client Ipadress
		client1.put("Hash", new Integer(32768));			//client integer waarde bekomen door hash functie
		
		listofclients.add(client1);
		*/
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
		
	}
}
