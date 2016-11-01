package SystemY.NamingServer;

import org.json.simple.JSONObject;

public class Node {

	public static JSONObject Node = new JSONObject();
	private String name;
	private String ipAdress;
	private int hash;
	
	public void setName (String name){
		this.name = name;
		Node.putIfAbsent("Name", this.name);
	}
	public String getName (){
		return(name);
	}
	public void setIpAdress (String ipAdress){
		this.ipAdress = ipAdress;
		Node.putIfAbsent("IpAdress", this.ipAdress);
	}
	public String getIpAdress (){
		return(ipAdress);
	}
	public void calculatehash(){
		this.hash = 32768;
		Node.putIfAbsent("Hash", this.hash);
	}
	public int gethash(){
		return(hash);
	}
}
