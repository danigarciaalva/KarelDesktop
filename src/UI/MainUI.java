package UI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import Tools.Exchanger;

@SuppressWarnings("serial")
public class MainUI extends JFrame{

	public MainUI(){
		super("Karel Desktop");
        setSize(640, 480);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(new BarraHerramientas(),BorderLayout.NORTH);
        add(new KPanel(Color.black), BorderLayout.CENTER);
        setVisible(true);
	}
	
	class KPanel extends JPanel{
		
		public KPanel(Color color){
			setSize(800,800);
			setLayout(new BorderLayout()); 
			Exchanger.graphics = new KWorld(this.getWidth(), this.getHeight());
			Exchanger.editor = new KEditor(this.getHeight());
			add(Exchanger.editor,BorderLayout.WEST);
			add(Exchanger.graphics,BorderLayout.EAST);
		}
	}
}
