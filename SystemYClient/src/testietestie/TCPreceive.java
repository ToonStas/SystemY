package testietestie;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPreceive {

	public static int SOCKET_PORT = 13267;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			int i = ReceiveFile(InetAddress.getLocalHost(), 12000000, "C:/Temp/eenliedje.mp3");
			if (i == 0) {
				System.out.println("file doesnt exist");
			} else {
				System.out.println("succesfull transmission");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static int ReceiveFile(InetAddress IPSender, int fileSize, String fileName) throws IOException {
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
			while (true) {
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
			}
		} finally {
			if (servSock != null)
				servSock.close();
		}

	}

}
