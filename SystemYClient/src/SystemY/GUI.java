package SystemY;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class GUI extends JFrame implements ActionListener {
	JFrame frame;
	NodeClient node;
	FileManager fileManager;
	int lengthList;
	ArrayList<String> fileList;
	ArrayList<String> fileListDeleteLocally;
	JButton logout = new JButton("LOG OUT");
	JButton openButtons[];
	JButton deleteButtons[];
	JButton deleteLocallyButtons[];
	
	public GUI(NodeClient nodeClient){
		node = nodeClient;
		this.fileManager = node.getFileManager();
		
		//getting the lists
		fileList = fileManager.getListAllFiles();
		fileListDeleteLocally = fileManager.getListAllFilesThatCanBeDeletedLocally();
		lengthList = fileList.size();
		openButtons = new JButton[lengthList];
		deleteButtons = new JButton[lengthList];
		deleteLocallyButtons = new JButton[lengthList];
		
		frame = new JFrame("Filelist");
		JFrame.setDefaultLookAndFeelDecorated(true);
		frame.setBackground(Color.LIGHT_GRAY);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new GridLayout(lengthList,4));
		
		//making the buttons
		for(int i=0; i< lengthList;i++){
			JButton btn = new JButton("OPEN");
			btn.addActionListener(this);
			add(btn);
			openButtons[i] = btn;
		}
		for(int i=0;i<lengthList;i++)
		{
			JButton btn = new JButton("DELETE");
			btn.addActionListener(this);
			add(btn);
			deleteButtons[i] = btn;
		}
		for (int i=0;i<lengthList;i++){
			JButton btn = new JButton ("DELETE LOCALLY");
			btn.addActionListener(this);
			add(btn);
			deleteLocallyButtons[i] = btn;
		}
		
		for(int i = 0; i< lengthList; i++)	
		{
			JPanel p = new JPanel();
			p.setLayout(new GridLayout(1, 4));
			p.add(new JTextField(fileList.get(i)));	//get filename
			p.add(openButtons[i]);
			p.add(deleteButtons[i]);
			if (fileListDeleteLocally.contains(fileList.get(i))){
				p.add(deleteLocallyButtons[i]);
			}
			frame.add(p);
			frame.pack();
		}
		
		JPanel p5 = new JPanel(new GridLayout(lengthList+1,3));
		p5.add(logout);
		frame.add(p5);
		frame.pack();
	    frame.setVisible(true);
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		JButton b = (JButton) e.getSource();
		String name;
		for (int i=0;i<lengthList;i++){
			if(b.equals(openButtons[i])){
				name = fileList.get(i);
				fileManager.openFile(name);
			}
			if(b.equals(deleteButtons[i])){
				name = fileList.get(i);
				fileManager.deleteFileFromNetwork(name);
			}
			if(b.equals(deleteLocallyButtons[i])){
				name = fileList.get(i);
				fileManager.deleteFileLocally(name);
			}
		}
		
		
	}

}