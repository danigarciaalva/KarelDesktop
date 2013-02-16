package UI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;

import Core.grammar.Ejecutable;
import Core.karelmovil.KGrammar;
import Core.karelmovil.KRunner;
import Core.karelmovil.KarelException;
import Tools.Exchanger;

public class BarraHerramientas extends JToolBar implements ActionListener{

	private static final int NUEVO_CODIGO = 1;
	private static final int ABRIR_CODIGO = 2;
	private static final int GUARDAR_CODIGO = 3;
	private static final int NUEVO_MUNDO = 4;
	private static final int ABRIR_MUNDO = 5;
	private static final int GUARDAR_MUNDO= 6;
	
	private static final long serialVersionUID = 1L;
	public static int SELECCION;
	
	private JButton new_code,open_code,save_code,new_world,open_world,save_world, run, add_beeper, remove_beeper, add_wall, remove_wall; 
	
	public BarraHerramientas(){
		new_code = new JButton(new ImageIcon("new_code.png"));
		new_code.addActionListener(this);
		open_code = new JButton(new ImageIcon("open_code.png"));
		open_code.addActionListener(this);
		save_code = new JButton(new ImageIcon("save_code.png"));
		save_code.addActionListener(this);
		new_world = new JButton(new ImageIcon("new_world.png"));
		new_world.addActionListener(this);
		open_world = new JButton(new ImageIcon("open_world.png"));
		open_world.addActionListener(this);
		save_world = new JButton(new ImageIcon("world_save.png"));
		save_world.addActionListener(this);
		add_beeper = new JButton(new ImageIcon("beeper_add.png"));
		add_beeper.addActionListener(this);
		add_wall = new JButton(new ImageIcon("beeper_add.png"));
		add_wall.addActionListener(this);
		remove_beeper = new JButton(new ImageIcon("beeper_add.png"));
		remove_beeper.addActionListener(this);
		add_beeper = new JButton(new ImageIcon("beeper_add.png"));
		add_beeper.addActionListener(this);
		run = new JButton(new ImageIcon("run.png"));
		run.addActionListener(this);
		add(new_code);
		add(open_code);
		add(save_code);
		add(new Separator());
		add(new_world);
		add(open_world);
		add(save_world);
		add(new Separator());
		add(run);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		 if(e.getSource() == new_code)
			 SELECCION = NUEVO_CODIGO;
		 else if(e.getSource() == open_code)
			 SELECCION = ABRIR_CODIGO;
		 else if(e.getSource() == save_code)
			 SELECCION = GUARDAR_CODIGO;
		 else if(e.getSource() == new_world)
			 SELECCION = NUEVO_MUNDO;
		 else if(e.getSource() == open_world)
			 SELECCION = ABRIR_MUNDO;
		 else if(e.getSource() == save_world)
			 SELECCION = GUARDAR_MUNDO;
		 else if(e.getSource() == run){
			 String codigo = Exchanger.editor.getText();
				InputStream is = new ByteArrayInputStream(codigo.getBytes());
				InputStreamReader isr = new InputStreamReader(is); 
				try{
					KGrammar grammar = new KGrammar(new BufferedReader(isr), true, false);
					grammar.verificar_sintaxis();
					Ejecutable exe = grammar.expandir_arbol();
					Exchanger.krunner = new KRunner(exe, Exchanger.kworld);
					Exchanger.SUCESS_EXECUTED = false;
					Exchanger.graphics.init();
				}catch(KarelException ex){
					JOptionPane.showMessageDialog(null, ex.getMessage());
				}
		 }
			 
	}
}
