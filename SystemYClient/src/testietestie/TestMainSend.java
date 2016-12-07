package testietestie;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

public class TestMainSend {

	public static void main(String[] args) throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		TCP tcp = new TCP();
		File file = new File("C:/java/lieke.mp3");
		InetAddress address = InetAddress.getByName("192.168.0.4");
		
		;
		for (int i = 0;i<5;i++){
			while (tcp.SendFile(file, address)==0){
				TimeUnit.SECONDS.sleep(1);
			}
		}
		
	}

}
