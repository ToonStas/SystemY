package SystemY.NamingServer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


public class Nodelijst {

	public static JSONArray listofclients = new JSONArray();
	
	public static void main(String[] args) 
	{
		JSONObject client1 = new JSONObject();
		client1.put("Name", "Matthias");					//client name
		client1.put("IPadress", "192.168.1.4");				//client Ipadress
		client1.put("hash", new Integer(32768));			//client integer waarde bekomen door hash functie
		
		listofclients.add(client1);
		
		Nodelijst.writeJSON();
	}
	
	public static void writeJSON()
	{
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
