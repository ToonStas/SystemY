package testietestie;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import testietestie.agent;

public class Serializer {
	
	public static agent theAgent;

	
	public static void main (String [] args ){
		theAgent = new agent("naam",5); //Zie bij floris voor parameters
		serialize(theAgent);
		deserialize(theAgent);
	}
	
	public static void serialize(agent anAgent)
	{
		try{
			FileOutputStream fileOut = new FileOutputStream("/Agentfile/agent.ser");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(anAgent);
			out.close();
			fileOut.close();
			System.out.printf("Serialized agent is saved in /Agentfile/agent.ser \n");
		}catch(IOException i){
			i.printStackTrace();
		}
	}
	
	public static void deserialize(agent anAgent)
	{
		try {
	        FileInputStream fileIn = new FileInputStream("/Agentfile/agent.ser");
	        ObjectInputStream in = new ObjectInputStream(fileIn);
	        anAgent = (agent) in.readObject();
	        in.close();
	        fileIn.close();
	     }catch(IOException i) {
	        i.printStackTrace();
	        return;
	     }catch(ClassNotFoundException c) {
	        System.out.println("agent class not found");
	        c.printStackTrace();
	        return;
	     }

		
		//Must be adjusted to given parameters
	    System.out.println("Deserialized Agent:");
	    System.out.println("Name: " + anAgent.name);
	    System.out.println("bestandlijst: " + anAgent.bestandlijst);
		
	}	

}

