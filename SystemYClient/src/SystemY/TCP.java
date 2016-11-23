package SystemY;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class TCP {
	public final static int SOCKET_PORT = 13267;

	public TCP() {

	}

	public int ReceiveFile(InetAddress IPSender, int fileSize, String fileName) throws IOException {
		InetAddress IPSend = IPSender;
		int size = fileSize;
		int bytesRead;
		int current;
		String name = fileName;
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		Socket sock = null;
		ServerSocket servSock = null;
		try {
			servSock = new ServerSocket(SOCKET_PORT);
			try {
				sock = servSock.accept();
				System.out.println("Succesful TCP connection with " + IPSend.toString() + " .");
				byte[] byteArray = new byte[size];
				InputStream in = sock.getInputStream();
				fos = new FileOutputStream(name);
				bos = new BufferedOutputStream(fos);
				bytesRead = in.read(byteArray, 0, byteArray.length);
				current = bytesRead;
				do {
					bytesRead = in.read(byteArray, current, (byteArray.length - current));
					if (bytesRead >= 0)
						current += bytesRead;
				} while (bytesRead > -1);
				bos.write(byteArray, 0, current);
				bos.flush();

			} finally {
				if (fos != null)
					fos.close();
				if (bos != null)
					bos.close();
				if (sock != null)
					sock.close();
			}
		} finally {
			if (servSock != null)
				servSock.close();
		}
		File file = new File(name);
		if (file.exists())
			return 1;
		else
			return 0;
	}

	
	public void SendFile(File fileToSend, InetAddress IPDestination) throws IOException {
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


	//public void listen() throws IOException {
	//	Thread listener = new Thread(new TCPServerSocketListener(SOCKET_PORT, this.sock, this.serverSock));
	//	listener.start();
	//}

}