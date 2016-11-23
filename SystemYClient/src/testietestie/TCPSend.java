package testietestie;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPSend {
	
	public static int SOCKET_PORT = 13267;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		File file = new File("C:/java/lieke.mp3");
		try {
			SendFile(file,InetAddress.getLocalHost());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void SendFile(File fileToSend, InetAddress IPDestination) throws IOException {
		File file = fileToSend;
		InetAddress IPDest = IPDestination;
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		OutputStream os = null;
		Socket sock = null;

		
		System.out.println("Waiting...");
		try {
			while (true){
				sock = new Socket(IPDest, SOCKET_PORT);
				System.out.println("Accepted connection : " + sock);
				// send file
				byte[] mybytearray = new byte[(int) file.length()];
				fis = new FileInputStream(file);
				bis = new BufferedInputStream(fis);
				bis.read(mybytearray, 0, mybytearray.length);
				os = sock.getOutputStream();
				System.out.println("Sending " + file.toString() + "(" + mybytearray.length + " bytes)");
				os.write(mybytearray, 0, mybytearray.length);
				os.flush();
				System.out.println("Done.");
			}
		} finally {
			if (bis != null)
				bis.close();
			if (os != null)
				os.close();
			if (sock != null)
				sock.close();
		}
	}

}
