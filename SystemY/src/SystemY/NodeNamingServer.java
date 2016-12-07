package SystemY;

import java.rmi.Remote;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

public class NodeNamingServer implements Remote {

	public  JSONObject Node = new JSONObject(); 
	private String name;
	private String ipAdress;
	private int hash;
	private Map<String, String> node = new HashMap<String, String>();
	private NamingServerToClientInterface clientInterface;
	
	public NodeNamingServer (String name, String ipAdress)
	{
		this.name = name;
		this.ipAdress = ipAdress;
		calculateHash();
		
		node.put("Name", this.name);
		node.put("IpAdress", this.ipAdress);
		node.put("Hash", Integer.toString(this.hash));
		re_generateJSONobject();
	}
	
	public int setName (String name){
		this.name = name;
		if(name == getName())
			return 0;
		else{
			node.put("Name",this.name);
			re_generateJSONobject();
			return 1;
		}
	}
	public String getName (){
		return(name);
	}
	public void setIpAdress (String ipAdress){
		this.ipAdress = ipAdress;
		node.put("IpAdress",this.ipAdress);
		re_generateJSONobject();

	}
	public String getIpAdress (){
		return(ipAdress);
	}
	
	//Deze functie berekend de hash voor deze node zelf bij initialisatie
	private int calculateHash(){
		int tempHash = this.name.hashCode();
		if (tempHash < 0)
			tempHash = tempHash * -1;
		tempHash = tempHash % 32768;
		this.hash = tempHash;
		node.put("hash",Integer.toString(this.hash));
		re_generateJSONobject();
		return 1;
	}
	
	//Deze functie geeft true terug als de nodenamen dezelfde hashcode hebben.
	public boolean compareHash(String nodeNaam){
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
	
	@SuppressWarnings("unchecked")
	public int re_generateJSONobject(){
		Node.put("Name", this.name);
		Node.put("IpAdress", this.ipAdress);
		Node.put("Hash", Integer.toString(this.hash));
		return 1;
	}

	public void addInterface(NamingServerToClientInterface ntci) {
		clientInterface = ntci;
	}

	public NamingServerToClientInterface getInterface() {
		return clientInterface;
	} 
}
