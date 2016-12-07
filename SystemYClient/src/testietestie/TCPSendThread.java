package testietestie;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class TCPSendThread extends Thread {
	private static int SOCKET_PORT;
	private File file;
	private InetAddress IPDest;
	
	//Thread who sends a file
	public TCPSendThread(int SocketPort, File fileToSend, InetAddress IPDestination){
		SOCKET_PORT = SocketPort;
		file = fileToSend;
		IPDest = IPDestination;
	}
	
	public void run(){
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		OutputStream os = null;
		Socket sock = null;

		
		System.out.println("Waiting...");
		try {
			
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
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (bis != null)
					bis.close();
				if (os != null)
					os.close();
				if (sock != null)
					sock.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
