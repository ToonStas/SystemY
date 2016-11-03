package SystemY.NamingServer;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

public class Node {

	public  JSONObject Node = new JSONObject();
	private String name;
	private String ipAdress;
	private int hash;
	private Map<String, String> node = new HashMap<String, String>();
	
	public Node (String name, String ipAdress)
	{
		this.name = name;
		this.ipAdress = ipAdress;
		calculatehash();
		
		node.put("Name", this.name);
		node.put("IpAdress", this.ipAdress);
		node.put("Hash", Integer.toString(this.hash));
	}
	
	public int setName (String name){
		this.name = name;
		if(name == getName())
			return 0;
		else{
			node.put("Name",this.name);
			return 1;
		}
	}
	public String getName (){
		return(name);
	}
	public void setIpAdress (String ipAdress){
		this.ipAdress = ipAdress;
		node.put("IpAdress",this.ipAdress);

	}
	public String getIpAdress (){
		return(ipAdress);
	}
	
	public void calculatehash(){
		int tempHash = this.name.hashCode();
		if (tempHash < 0)
			tempHash = tempHash * -1;
		tempHash = tempHash % 32768;
		this.hash = tempHash;
		Node.put("Hash", Integer.toString(this.hash));
	}
	
	public int gethash(){
		return(hash);
	}
	
	public int generateJSON(HashMap<String, String> node){
		return 1;
	}
}
