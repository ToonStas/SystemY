package SystemY;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class GUI extends JFrame implements ActionListener {
	NodeClient nodeClient;
	BestandenLijst bestandenLijst;
	int length;
	JButton logout = new JButton("LOG OUT");
	JButton openbuttons[] = new JButton[length];
	JButton deletebuttons[] = new JButton[length];
	Bestand bestand; //een bestand
	
	public GUI(NodeClient nodeClient){
		this.nodeClient = nodeClient;
		bestandenLijst = nodeClient.getBestandenLijst();
		length = bestandenLijst.getSize();
		
		for(int i=0; i< length;i++){
			JButton btn = new JButton("OPEN");
			btn.addActionListener(this);
			add(btn);
			openbuttons[i] = btn;
		}
		for(int i=0;i<length;i++)
		{
			JButton btn = new JButton("DELETE");
			btn.addActionListener(this);
			add(btn);
			deletebuttons[i] = btn;
		}
		JFrame frame = new JFrame("Filelist");
		JFrame.setDefaultLookAndFeelDecorated(true);
		frame.setBackground(Color.LIGHT_GRAY);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new GridLayout(length,4));
		
		
		for(int i = 0; i< length; i++)	
		{
			JPanel p = new JPanel();
			p.setLayout(new GridLayout(1, 4));
			p.add(new JTextField(bestand.getNaam()));	//get filename
			p.add(openbuttons[i]);
			p.add(deletebuttons[i]);
			//if(checkOwned(Bestand.getNaam(), BestandenLijst.BestandenLijst())){								//Check if file is local file
			//	p.add(new JButton("LOCAL_DELETE"));
			// }
			frame.add(p);
			frame.pack();
		}
	    
		JPanel p5 = new JPanel(new GridLayout(length+1,3));
		p5.add(logout);
		frame.add(p5);
		frame.pack();
	    frame.setVisible(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		JButton b = (JButton) e.getSource();
		int i=0;
		if(b == openbuttons[i]){
			bestand.getFile();
		}
		if(b == deletebuttons[i]){
			String name = bestand.getNaam();
			bestandenLijst.verwijderBestandMetNaam(name);
		}
		
	}

}