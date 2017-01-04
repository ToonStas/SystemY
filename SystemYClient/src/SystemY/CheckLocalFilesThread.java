package SystemY;

import java.io.File;

public class CheckLocalFilesThread extends Thread {
	private FileManager fileManager;
	private FileListWithFile localFiles;
	private static long SLEEPTIME = 2000; //Milliseconds
	
	
	public CheckLocalFilesThread(FileManager manager){
		fileManager = manager;
		
	}
	
	public void run(){
		//initial sleep time
		try {
			Thread.sleep(SLEEPTIME);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String name;
		localFiles = fileManager.getLocalFiles();
		File dir = new File("C:/TEMP/LocalFiles/");
		for (File f : dir.listFiles()) {
			name = f.getName();
			if (localFiles.checkFileExistsWithName(name)==-1){
				fileManager.addLocalFile(name);
			}
		}
		run();
	}

}
