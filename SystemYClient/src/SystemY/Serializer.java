package SystemY;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import testietestie.agent;

public class Serializer {
	File file = new File("C:/TEMP/Agentfile");
	File file2 = new File("C:/TEMP/Agentfile/agent.ser");
	private Thread agent;

	public static void main(String[] args) {
		Serializer s = new Serializer();
	}

	public Serializer() {
		try {
			file.mkdir();
			file2.createNewFile();
		} catch (IOException e) {
			System.out.println("Couldn't create file");
			e.printStackTrace();
		}
	}

	public void serialize(Agent agent) {
		try {
			FileOutputStream fileOut = new FileOutputStream("C:/TEMP/Agentfile/agent.ser");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(agent);
			out.close();
			fileOut.close();
			System.out.printf("Serialized agent is saved in /Agentfile/agent.ser \n");
		} catch (IOException i) {
			i.printStackTrace();
		}
	}

	public Thread deserialize() {
		try {
			FileInputStream fileIn = new FileInputStream("C:/TEMP/Agentfile/agent.ser");
			ObjectInputStream in = new ObjectInputStream(fileIn);
			agent = (Thread) in.readObject();
			in.close();
			fileIn.close();
		} catch (IOException i) {
			i.printStackTrace();
		} catch (ClassNotFoundException c) {
			System.out.println("agent class not found");
			c.printStackTrace();
		}
		
		return agent;

	}

}