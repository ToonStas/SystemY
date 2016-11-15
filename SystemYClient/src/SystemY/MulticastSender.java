package SystemY;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.Arrays;

public class MulticastSender {

	private int port = 8769; // Poort naar waar we verzenden
	private String group = "224.1.1.1";
	private MulticastSocket s;
	private int ownHash;

	public MulticastSender(int ownHash) throws UnsupportedEncodingException {
		this.ownHash = ownHash;
		
		String Name;
		InetAddress address=null;
		String ipAdres="";
		try {
			address = InetAddress.getLocalHost();
			ipAdres = address.getHostAddress() ;
		} catch (UnknownHostException e1) {
			e1.printStackTrace(); 
		} 
	   
		
		Name = ReadConsoleNaam();
		try {
			s = new MulticastSocket();
		} catch (IOException e) {
			e.printStackTrace();
		}
		sending(Name, ipAdres);
		close();
	}

	private String ReadConsoleNaam() {
		String naam = null;
		BufferedReader br = null;

		try {
			br = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("Naam Node: ");
			naam = br.readLine();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (naam == "\n") {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		ownHash = calculateHash(naam);
		return naam;
	}

	//niet meer nodig we halen het i-adres nu zelf op
	private String ReadConsoleIP() {
		String IP = null;
		BufferedReader br = null;

		try {
			br = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("IP Node: ");
			IP = br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (IP == "\n") {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return IP;
	}

	private void sending(String naam, String Ip) throws UnsupportedEncodingException {
		// Hier moeten we de n; het IP adres verzenden

		String Naamip;

		Naamip = naam + " " + Ip;

		byte[] sendData = Naamip.getBytes(); // Naam in sendData1 zetten

		/*
		 * for(int i=0 ; i<sendData.length; i++){ 
		 * sendData[i] = (byte)i;
		 * }
		 */
		try {
			DatagramPacket pack = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(group), port);
			s.send(pack);
			System.out.println("Pakket is verzonden.");
		} catch (IOException e1) {
			e1.printStackTrace();
			System.out.println("Pakket is niet verzonden");
		}

	}

	private void close() {
		s.close();
	}
	
	private int calculateHash(String nodeNaam){ //Deze functie berekent de hash van een String als parameter.
        int tempHash = nodeNaam.hashCode();
        if (tempHash < 0)
            tempHash = tempHash * -1;
        tempHash = tempHash % 32768;
        return tempHash;
    }
}
