package testietestie;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

public class TestMainReceive {

	public static void main(String[] args) throws IOException, InterruptedException {
		// TODO Auto-generated method stub
				TCP tcp = new TCP();
				
				while (tcp.ReceiveFile(100000000, "C:/java/lieke2.mp3")==0){
					TimeUnit.SECONDS.sleep(1);
				}
				while (tcp.ReceiveFile(100000000, "C:/java/lieke3.mp3")==0){
					TimeUnit.SECONDS.sleep(1);
				}
				while (tcp.ReceiveFile(100000000, "C:/java/lieke4.mp3")==0){
					TimeUnit.SECONDS.sleep(1);
				}
				while (tcp.ReceiveFile(100000000, "C:/java/lieke5.mp3")==0){
					TimeUnit.SECONDS.sleep(1);
				}
				while (tcp.ReceiveFile(100000000, "C:/java/lieke6.mp3")==0){
					TimeUnit.SECONDS.sleep(1);
				}
				

	}

}
