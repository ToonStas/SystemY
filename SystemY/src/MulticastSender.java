import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.Arrays;

public class MulticastSender {
	
	private int port = 8769; //Poort naar waar we verzenden
	private String group = "224.1.1.1";
	private MulticastSocket s;
	
	public MulticastSender() throws UnsupportedEncodingException{
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
	
	
	public void sending(String naam, String Ip) throws UnsupportedEncodingException{
		//Hier moeten we de n; het IP adres verzenden

		String Naamip;
		
		Naamip = naam + " " + Ip;
		
		byte[] sendData = Naamip.getBytes();	//Naam in sendData1 zetten
		
		/*for(int i=0 ; i<sendData.length; i++){
			sendData[i] = (byte)i;
		}*/
		try {
			DatagramPacket pack = new DatagramPacket(sendData, sendData.length,InetAddress.getByName(group), port);
			s.send(pack); 
			System.out.println("Pakket is verzonden.");
		} catch (IOException e1) {
			e1.printStackTrace();
			System.out.println("Pakket is niet verzonden");
		}
		
	}
	
	public void close(){
		s.close();
	}	
}
