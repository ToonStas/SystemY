package SystemY.NamingServer;

import org.json.simple.JSONObject;

public class Node {

	public  JSONObject Node = new JSONObject();
	private String name;
	private String ipAdress;
	private int hash;
	
	public Node (String name, String ipAdress)
	{
		this.name = name;
		this.ipAdress = ipAdress;
		calculatehash();
		
		Node.put("Name", this.name);
		Node.put("IpAdress", this.ipAdress);
		Node.put("hash", this.hash);
	}
	
	public void setName (String name){
		this.name = name;
		Node.replace("name",this.name);
	}
	public String getName (){
		return(name);
	}
	public void setIpAdress (String ipAdress){
		this.ipAdress = ipAdress;
		Node.replace("ipAdress",this.ipAdress);

	}
	public String getIpAdress (){
		return(ipAdress);
	}
	public void calculatehash(){
		this.hash = 32768;
		Node.replace("hash", this.hash);
	}
	public int gethash(){
		return(hash);
	}
}
