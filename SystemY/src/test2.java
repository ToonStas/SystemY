import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

public class test2 {
	
	private int port = 8769; //Poort naarwaar we verzenden
	private String group = "224.1.1.1";
	private MulticastSocket s;
	
	public test2(){
		String Name;
		String Ipadres;
		
		Name = ReadConsoleNaam();
		Ipadres =ReadConsoleIP();
		try {
			s = new MulticastSocket();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sending(Name, Ipadres);
		close();
	}

	public String ReadConsoleNaam(){
		String naam = null;
		BufferedReader br = null;
		
		try{ 
		br = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Naam Node: ");
		naam = br.readLine();
		
		} catch(IOException e){
			e.printStackTrace();
		} finally{
			if(naam == "\n"){
				try{
					br.close();
				} catch(IOException e){
				e.printStackTrace();
				}
			}
		}
		return naam;
	}
	
	public String ReadConsoleIP(){
		String IP = null;
		BufferedReader br = null;
		
		try{ 
		br = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("IP Node: ");
		IP = br.readLine();
		} catch(IOException e){
			e.printStackTrace();
		} finally{
			if(IP == "\n"){
				try{
					br.close();
				} catch(IOException e){
					e.printStackTrace();
				}
			}
		}
		return IP;
	}
	
	
	public void sending(String naam, String Ip){
		//Hier moeten we de n; het IP adres verzenden

		String Naamip;
		
		Naamip = naam + Ip;
		
		byte[] sendData = Naamip.getBytes();	//Naam in sendData1 zetten
		
		for(int i=0 ; i<sendData.length; i++){
			sendData[i] = (byte)i;
		}
		DatagramPacket pack = new DatagramPacket(sendData, 10);
		try {
			pack = new DatagramPacket(sendData, sendData.length,InetAddress.getByName(group), port);
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			s.send(pack); 
			System.out.println("Pakket is verzonden.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public void close(){
		s.close();
	}	
}
