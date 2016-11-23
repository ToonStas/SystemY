package SystemY;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MulticastSenderNamingServer {
	private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
	private int port = 8769; // Poort naar waar we verzenden
	private String group = "224.1.1.1";
	private MulticastSocket s;
	String name="Server";
	InetAddress address=null;
	String ipAddress="";

	public MulticastSenderNamingServer(){
		sendEveryXSeconds();
	}
	
	public void sendEveryXSeconds(){
		scheduler.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				try {
					address = InetAddress.getLocalHost();
					ipAddress = address.getHostAddress() ;
				} catch (UnknownHostException e1) {
					e1.printStackTrace(); 
				} 
			 
				try {
					s = new MulticastSocket();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				//versturen van het pakket
				try {
					sending(name, ipAddress);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}, 10, 15, TimeUnit.SECONDS); //initialDelay, period, timeunit
		

	}

	private void sending(String naam, String Ip) throws UnsupportedEncodingException {
		// Hier moeten we de n; het IP adres verzenden

		String Naamip;

		Naamip = naam + " " + Ip;

		byte[] sendData = Naamip.getBytes(); // Naam in sendData zetten
		
		try {
			DatagramPacket pack = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(group), port);
			s.send(pack);
			System.out.println("Pakket is verzonden.");
		} catch (IOException e1) {
			e1.printStackTrace();
			System.out.println("Pakket is niet verzonden");
		}

	}
}
