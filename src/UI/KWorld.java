package UI;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import Core.karelmovil.KCasilla;
import Core.karelmovil.KPosition;
import Core.karelmovil.KRunner;
import Tools.Exchanger;


public class KWorld extends JPanel{
	private static final long serialVersionUID = 1L;
	private static int TAM_CAS;	
	private static int FREE_SPACE; 
	private static int MAX_SCREEN_X;
	private static int MAX_SCREEN_Y;
	private static int MIN_SCREEN_X;
	private static int MIN_SCREEN_Y;
	private static int WALL_AREA;
	private Point size;
	private BufferedImage world,karelN,karelE,karelS,karelO;
	private int firstX,firstY,lastX,lastY;
	private static boolean estoyArrastrando = false, addingBeeper = false, addingWall = false, deletingBeeper = false, deletingWall = false;	
	private Thread t;
	private static int NUM_BEEPERS;
	private static int NUMBER_ITEMS;
	private  int WIDTH, HEIGHT;
	public KWorld(int width, int height){
		this.WIDTH = width;
		this.HEIGHT = height;
		setPreferredSize(new Dimension(width, height));
		setBackground(Color.black);
		setLayout(null);
		Exchanger.kworld = new Core.karelmovil.KWorld();
		this.init();
	}
	
	public void init() {
		loadItems();
		if(Exchanger.krunner != null){
			Exchanger.krunner.step_run();  //KRunner ya fue inicializado previamente, acomodamos las variables
			kThread();
		}
		//kThread();  
		NUM_BEEPERS = 0;
		NUMBER_ITEMS = 0;
		MouseAdapter listener = new MouseAdapter() {
			
			public void mouseDragged(MouseEvent e){
				if(estoyArrastrando){
					System.out.println("Arrastrando");
					/** Sumamos la cantidad de pixeles arrastrados hasta obtener una cantidad considerable para mover a Karel*/
					int offsetX = (int)e.getX();
					int offsetY = (int)e.getY();
					lastX+=(offsetX-lastX);
					lastY+=(offsetY-lastY);
					if(Math.abs(lastX-firstX) >= TAM_CAS/2){ // la última expresión denota la suavidad del scroll
						/** Una vez acumulados los pixeles que queremos, movemos el mundo virtual*/
						if(lastX > firstX){
							MIN_SCREEN_X-=1;
							MAX_SCREEN_X-=1;
						}else{
							MIN_SCREEN_X+=1;
							MAX_SCREEN_X+=1;
						}
						firstX = lastX;
						firstY = lastY;
					}
					if(Math.abs(lastY-firstY) >= TAM_CAS/2){
						if(lastY > firstY){
							MIN_SCREEN_Y+=1;
							MAX_SCREEN_Y+=1;
						}else{
							MIN_SCREEN_Y-=1;
							MAX_SCREEN_Y-=1;
						}
						firstX = lastX;
						firstY = lastY;
					}
					repaint();
				}
			}
			
			public void mousePressed(MouseEvent e) {
				if(deletingBeeper){
					int columna = ((int)e.getX()/TAM_CAS)+MIN_SCREEN_X;
					int fila = (MAX_SCREEN_Y - (((int)e.getY())+FREE_SPACE)/TAM_CAS);
					/** Buscamos las coordenadas obtenidas en todas las casillas para eliminar el item*/
					for(KCasilla casilla: Exchanger.kworld.casillas.values())
						if(casilla.fila == fila && casilla.columna == columna && casilla.zumbadores != 0){
							casilla.zumbadores = 0;
							NUMBER_ITEMS--;
							break;
						}
						else
							JOptionPane.showMessageDialog(null, "No hay zumbadores en esa casilla");
				}/*else if(deletingWall){
					double lastX = event.getX();
					double lastY = event.getY();
					int x = ((int)event.getX()/TAM_CAS)+MIN_SCREEN_X;
					int y = (MAX_SCREEN_Y - (((int)event.getY())+FREE_SPACE)/TAM_CAS);
					int columna = ((int)lastX/TAM_CAS)+1;
					int fila = ((int)(size.y-(event.getY()+FREE_SPACE)))/TAM_CAS+1;
					int toDelete;	
					if( lastX > (columna-1)*TAM_CAS && lastX < (columna-1)*TAM_CAS+WALL_AREA){
						toDelete = poodleDeveloper.karel.data.karelmovil.KWorld.OESTE;
					}else if(lastX > (columna*TAM_CAS-WALL_AREA) && lastX < (columna*TAM_CAS)){
						toDelete = poodleDeveloper.karel.data.karelmovil.KWorld.ESTE;
					}else if(lastY < size.y-(fila*TAM_CAS-WALL_AREA) && lastY > size.y-(fila*TAM_CAS)){
						toDelete = poodleDeveloper.karel.data.karelmovil.KWorld.NORTE;
					}else if(lastY > size.y-(fila-1)*TAM_CAS-WALL_AREA && lastY < size.y-(fila-1)*TAM_CAS){
						toDelete = poodleDeveloper.karel.data.karelmovil.KWorld.SUR;
					}
					NUMBER_ITEMS--;
				}*/
				else if(addingBeeper){
					int columna = ((int)e.getX()/TAM_CAS)+MIN_SCREEN_X;
					int fila = (MAX_SCREEN_Y - (((int)e.getY())+FREE_SPACE)/TAM_CAS);
					Exchanger.kworld.pon_zumbadores(new KPosition(fila, columna), NUM_BEEPERS);
					NUMBER_ITEMS++;
				}else if(addingWall || deletingWall){
					/** Obtenemos el evento*/
					double lastX = e.getX();
					double lastY = e.getY();
					/** Obtenemos las coordenadas en el mundo virtual de Karel*/
					int x = ((int)e.getX()/TAM_CAS)+MIN_SCREEN_X;
					int y = (MAX_SCREEN_Y - (((int)e.getY())+FREE_SPACE)/TAM_CAS);
					/** Obtenemos las coordenadas en la pantalla*/
					int columna = ((int)lastX/TAM_CAS)+1;
					int fila = ((int)(size.y-(e.getY()+FREE_SPACE)))/TAM_CAS+1;
					if( lastX > (columna-1)*TAM_CAS && lastX < (columna-1)*TAM_CAS+WALL_AREA){
						Exchanger.kworld.conmuta_pared(new KPosition(y, x), Core.karelmovil.KWorld.OESTE);
					}else if(lastX > (columna*TAM_CAS-WALL_AREA) && lastX < (columna*TAM_CAS)){
						Exchanger.kworld.conmuta_pared(new KPosition(y, x), Core.karelmovil.KWorld.ESTE); 
					}else if(lastY < size.y-(fila*TAM_CAS-WALL_AREA) && lastY > size.y-(fila*TAM_CAS)){
							Exchanger.kworld.conmuta_pared(new KPosition(y, x), Core.karelmovil.KWorld.NORTE);
					}else if(lastY > size.y-(fila-1)*TAM_CAS-WALL_AREA && lastY < size.y-(fila-1)*TAM_CAS){
							Exchanger.kworld.conmuta_pared(new KPosition(y, x), Core.karelmovil.KWorld.SUR);
					}
					NUMBER_ITEMS++;
				}
				estoyArrastrando = true;
				/** Obtenemos el primer punto donde hicimos click*/
				firstX = (int)e.getX();
				firstY = (int)e.getY();
				lastX = firstX;
				lastY = firstY;
			}
			
			public void mouseReleased(MouseEvent e) {
				estoyArrastrando = false; 
				addingBeeper = false;
			}
		};
		
		addMouseListener(listener);
		addMouseMotionListener(listener);
	}
	
	protected void kThread(){
		/** Hilo de ejecución de karel*/
		t = new Thread(){
			public void run(){
				int estado;
				while((estado = Exchanger.krunner.step()) == KRunner.ESTADO_OK){
					try{ 
						repaint();
						Thread.sleep(500); 
					}catch(Exception e){
						e.getMessage();
					} 
				}
				if(estado == Core.karelmovil.KRunner.ESTADO_ERROR){
					/** Mostramos en el hilo de la principal de la UI el mensaje*/
					JOptionPane.showMessageDialog(null, Exchanger.krunner.mensaje);
				} 
				else if(estado == KRunner.ESTADO_TERMINADO && !Exchanger.SUCESS_EXECUTED){
					JOptionPane.showMessageDialog(null, "FELICIDADES, Karel llegó a su destino");
				} 
			}
		};
		t.start();
	}
	
	 @Override
	public void paint(Graphics g) {
		 Rectangle a = g.getClipBounds();
	     g.setColor(Color.white);
	     g.clearRect(a.x, a.y, a.width, a.height);
		 Graphics2D g2d = (Graphics2D)g;
		 
			/** Pintamos las casillas */
			for(float i = 0; i < size.x+TAM_CAS; i+=TAM_CAS)
				for(float j = size.y-TAM_CAS; j > -TAM_CAS ; j-=TAM_CAS)
						g2d.drawImage(world, (int)i, (int)j, TAM_CAS, TAM_CAS, null);
		 //g2d.drawImage(world, 0, 0, TAM_CAS, TAM_CAS, null);
			/** En caso de haber zumbadores los agregamos*/
			int xB;
			int yB;
			//paint.setTextSize(TAM_CAS/3);
			for(KCasilla casilla : Exchanger.kworld.casillas.values()){
				if(casilla.fila < MAX_SCREEN_Y+1 && casilla.fila >= MIN_SCREEN_Y && casilla.columna < MAX_SCREEN_X+2 && casilla.columna >= MIN_SCREEN_X){
					xB = (casilla.columna-MIN_SCREEN_X)*TAM_CAS;
					yB = (((((int)(size.y/TAM_CAS))-(casilla.fila-MIN_SCREEN_Y))-1)*TAM_CAS)+FREE_SPACE;
					if(casilla.zumbadores != 0){
						 g2d.setPaint(Color.GREEN);
						 g2d.setStroke(new BasicStroke(28));
						 Ellipse2D.Double circ= new Ellipse2D.Double();
						 circ.setFrame(xB+(TAM_CAS/2), yB+(TAM_CAS/2), (TAM_CAS-50)/2, (TAM_CAS-50)/2);
						 g2d.draw(circ);
						 g2d.setStroke(new BasicStroke(1));
						 g2d.setPaint(Color.BLACK);
						 if(casilla.zumbadores > 9)
							 ;//g2d.dr.drawText(String.valueOf(casilla.zumbadores), xB+(TAM_CAS/3), yB+(TAM_CAS-(TAM_CAS/2))+5, paint);
						 else
							 ;//g.drawText(String.valueOf(casilla.zumbadores), xB+(TAM_CAS/3)+5, yB+(TAM_CAS-(TAM_CAS/2))+5, paint);
					}
					if(casilla.paredes.size() > 0){
						g2d.setStroke(new BasicStroke(6));
						g2d.setColor(Color.black);
						for(int pared : casilla.paredes){
							if(pared == Core.karelmovil.KWorld.NORTE)
								g2d.drawLine(xB, yB, xB+TAM_CAS, yB);
							else if(pared == Core.karelmovil.KWorld.ESTE)
								g2d.drawLine(xB+TAM_CAS, yB, xB+TAM_CAS, yB+TAM_CAS);
							else if(pared == Core.karelmovil.KWorld.SUR)
								g2d.drawLine(xB, yB+TAM_CAS, xB+TAM_CAS, yB+TAM_CAS);
							else if(pared == Core.karelmovil.KWorld.OESTE)
								g2d.drawLine(xB, yB, xB, yB+TAM_CAS);
						}
					}
				}
			}
			/** Sólo si Karel se encuentra dentro de los límites virtuales del mundo, lo pintamos*/
			if(Exchanger.kworld.karel.posicion.fila < MAX_SCREEN_Y+1 && Exchanger.kworld.karel.posicion.fila >= MIN_SCREEN_Y && 
					Exchanger.kworld.karel.posicion.columna < MAX_SCREEN_X+2 && Exchanger.kworld.karel.posicion.columna >= MIN_SCREEN_X){
				
				int x = (Exchanger.kworld.karel.posicion.columna-MIN_SCREEN_X)*TAM_CAS;
				int y = (((((int)(size.y/TAM_CAS))-(Exchanger.kworld.karel.posicion.fila-MIN_SCREEN_Y))-1)*TAM_CAS)+FREE_SPACE;
						
				switch (Exchanger.kworld.karel.orientacion) {
				case Core.karelmovil.KWorld.NORTE:
					g2d.drawImage(karelN,x,y,TAM_CAS,TAM_CAS,null);
					break;
				case Core.karelmovil.KWorld.ESTE:
					g2d.drawImage(karelE,x,y,TAM_CAS,TAM_CAS,null);
					break; 
				case Core.karelmovil.KWorld.SUR:
					g2d.drawImage(karelS,x,y,TAM_CAS,TAM_CAS,null);
					break;
				case Core.karelmovil.KWorld.OESTE:
					g2d.drawImage(karelO,x,y,TAM_CAS,TAM_CAS,null);
					break;
				default:
					break;
				}
			}
	    }
	
	public void loadItems(){
		try{
			world = ImageIO.read(getClass().getClassLoader().getResourceAsStream("res/kworld.png"));
			karelN = ImageIO.read(getClass().getClassLoader().getResourceAsStream("res/knorte.png"));
			karelS= ImageIO.read(getClass().getClassLoader().getResourceAsStream("res/ksur.png"));
			karelE = ImageIO.read(getClass().getClassLoader().getResourceAsStream("res/keste.png"));
			karelO = ImageIO.read(getClass().getClassLoader().getResourceAsStream("res/koeste.png"));
		}catch(IOException e){
			e.printStackTrace();
			System.out.println(e.getCause());
			System.out.println(e.getMessage());
		}
		TAM_CAS = world.getWidth();
		WALL_AREA = TAM_CAS/4;
		size = new Point();
		size.x = WIDTH;
		size.y = HEIGHT;
		MAX_SCREEN_X = (int)(size.x/TAM_CAS);
		MAX_SCREEN_Y = (int)(size.y/TAM_CAS);
		MIN_SCREEN_X = 1;
		MIN_SCREEN_Y = 1;
		FREE_SPACE = size.y-(((int)(size.y/TAM_CAS))*TAM_CAS);
	}

}
