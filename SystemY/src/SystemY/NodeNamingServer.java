package SystemY;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

public class NodeNamingServer {

	public  JSONObject Node = new JSONObject(); 
	private String name;
	private String ipAdress;
	private int hash;
	private Map<String, String> node = new HashMap<String, String>();
	
	public NodeNamingServer (String name, String ipAdress)
	{
		this.name = name;
		this.ipAdress = ipAdress;
		calculateHash();
		
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
	
	private int calculateHash(){ //Deze functie berekend de hash voor deze node zelf bij initialisatie
		int tempHash = this.name.hashCode();
		if (tempHash < 0)
			tempHash = tempHash * -1;
		tempHash = tempHash % 32768;
		this.hash = tempHash;
		node.put("hash",Integer.toString(this.hash));
		return 1;
	}
	
	public boolean compareHash(String nodeNaam){ //Deze functie geeft true terug als de nodenamen dezelfde hashcode hebben.
		int tempHash = nodeNaam.hashCode();
		if (tempHash < 0)
			tempHash = tempHash * -1;
		tempHash = tempHash % 32768;
		if (tempHash == this.hash)
			return true;
		else
			return false;
	}
	
	public int getHash(){
		return(hash);
	}
	
	public int generateJSON(HashMap<String, String> node){
		return 1;
	}
}
