import java.awt.Color;
import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Calendar;
import java.text.SimpleDateFormat;

import javax.swing.JLabel;
import javax.swing.Timer;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
//import javax.swing.border.LineBorder;

import java.util.Iterator;
import java.awt.Graphics;

public class InputPane extends JPanel implements MouseListener,MouseMotionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String datetime = "dd/MM/yyyy HH:mm:ss";
	private String data = "";
	private float linewidth; 

	private JLayeredPane canvas;
	StimPanel stimuluslayer;
	InputLayer inputlayer;
	private JLabel message;
	private JPanel foreground;
	
	private int eventCount=0,x1=0,x2=0,y1=0,y2=0,stimX=0,stimY=0,ilX=0,ilY=0,paintX,paintY;
	static final String newline = System.getProperty("line.separator");
	private ArrayList<int[]> linestorage;
	int[] temp;
	private int[] currentline;
	private Boolean draw;
	private Boolean firststim;
	private Boolean stimpresent;
	private Color fg,bg;
	private Graphics2D inputgraphics,inputgraphics2;
	private Timer t;
	private BufferedImage bi,bi2; // buffered images for drawing over input mask and stimulus
	
	
	public InputPane(Dimension d) {
		

        canvas = new JLayeredPane();
        fg = InteractLog.fg;
		bg = InteractLog.bg; 
		inputlayer = new InputLayer(new ImageIcon(InteractLog.inputlayerfilepath).getImage());
        // set fam or stim image, if set
		if(!InteractLog.famfilepath.isEmpty()) {
			stimuluslayer = new StimPanel(new ImageIcon(InteractLog.famfilepath).getImage());
			firststim = true;
		}
		else if (!InteractLog.stimfilepath.isEmpty()) {
			stimuluslayer = new StimPanel(new ImageIcon(InteractLog.stimfilepath).getImage());
			firststim = false;
		}
        message = new JLabel("");
        foreground = new JPanel();
        foreground.add(message);
        
        // centeralign images
        this.stimX = (d.width - stimuluslayer.getImgw()) / 2;
	    this.stimY = (d.height - stimuluslayer.getImgh()) / 2;
	    this.ilX = (d.width - inputlayer.getImgw()) / 2;
	    this.ilY = (d.height - inputlayer.getImgh()) / 2;
	    /*
        this.stimX = 0;
	    this.stimY = 0;
	    this.ilX = 0;
	    this.ilY = 0;
	    */
	    //bi = new BufferedImage((int)InteractLog.frameDimension.getWidth(),(int)InteractLog.frameDimension.getHeight(), BufferedImage.TYPE_INT_RGB);		
	    bi = new BufferedImage(d.width,d.height, BufferedImage.TYPE_INT_RGB);
	    bi2 = new BufferedImage(d.width,d.height, BufferedImage.TYPE_INT_RGB);
        inputgraphics = bi.createGraphics();
        inputgraphics2 = bi2.createGraphics();
        inputgraphics.setColor(bg);
        //inputgraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        //System.out.println((int)InteractLog.frameDimension.getWidth()+" "+(int)InteractLog.frameDimension.getHeight());
		
	    inputlayer.init();
	    stimuluslayer.init();
	    
        //canvas.setDoubleBuffered(true);
        //canvas.setLayout(new BoxLayout(canvas, BoxLayout.PAGE_AXIS));
        //foreground.setComponentZOrder(stimuluslayer, 0);
		canvas.setPreferredSize(d);
		canvas.add(stimuluslayer,new Integer(2), 2);
		canvas.add(inputlayer,new Integer(2), 1);
		
		//canvas.add(foreground,new Integer(2), 0);
				
		this.setBackground(bg);
		this.setForeground(fg);
		this.add(canvas);
		
		stimpresent = false;
		if(InteractLog.inputMaskHide)
			inputlayer.setVisible(false);
		if (InteractLog.famtime == 0) 
			init();
		
    }
	
	public String getDate() {
		Calendar c = Calendar.getInstance();
	    SimpleDateFormat sdf = new SimpleDateFormat(datetime);
	    return sdf.format(c.getTime());
	}
	public String getData() {
		return data;
	}
	
	public void addData(String datastring, String dataevent) {
		eventCount++;
		data+= eventCount+";"+System.nanoTime()+";"+datastring+";"+dataevent+newline;
	}
	
	public int getStimX() {
		return stimX;
	}
	public int getStimY() {
		return stimY;
	}
	public int getIlX() {
		return ilX;
	}
	public int getIlY() {
		return ilY;
	}
	
	public JLayeredPane getCanvas() {
		return canvas;
	}
	
	public void init() {
		canvas.addMouseListener(this);
		canvas.addMouseMotionListener(this);
        linewidth = InteractLog.strokewidth;
		//pane.add(text);
		//pane.setOpaque(true);
		draw = false;
    	linestorage = new ArrayList<int[]>();
		//ls = linestorage.iterator();
		// if(timeLimit>0) it.start();
    	addData("0;0","DrawingEnabled");
	}
	
	public Boolean getFirststim() {
		return firststim;
	}
	
	public Boolean getStimPresent() {
		return stimpresent;
	}
	
	public void setStimpresent(Boolean stimpresent) {
		this.stimpresent = stimpresent;
	}

	public void setFirststim(Boolean firststim) {
		this.firststim = firststim;
	}
	
	public Timer getT() {
		return t;
	}
	
	public void startTimer(int time) {
		t = new Timer(time,new ActionListener() {
		    public void actionPerformed(ActionEvent evt) {
		    	removeStim();
		    	setFirststim(false);
		    	t.removeActionListener(this);
		    	// added to avoid NPE
		    	init();
		        }    
		    });
		t.start();
	}
	public void stopTimer() {
		t.stop();
		init();
	}
	
	public void removeStim() { 
		
		if(firststim) {
			addData("0;0","FamiliarisationOff_"+InteractLog.famfilename);
			firststim = false;
			// set up first proper stimulus
			initStim();
			//stimuluslayer.setVisible(false);
			//stimpresent = false;
		}
		else if(stimpresent)
			addData("0;0","StimulusOff_"+InteractLog.stimfilename);
		stimuluslayer.setVisible(false);
		stimpresent = false;
		inputlayer.setVisible(true);
		//background.setImg(null);
		
	}
	
	public void initStim() {
		if (InteractLog.stimfilename != " ") {
			stimuluslayer.setImg(new ImageIcon(InteractLog.stimfilepath).getImage());
			stimuluslayer.init();
			firststim = false;
		}
	}
	
	public void showStim() { 
		if(InteractLog.stimdelay != 0) {
			t = new Timer(InteractLog.stimdelay,new ActionListener() {
			    public void actionPerformed(ActionEvent evt) {
			    	addData("0;0","StimulusOn_"+InteractLog.stimfilename);
			    	stimuluslayer.setVisible(true);
			    	stimpresent = true;
			    	
			    	//if (InteractLog.inputMaskHide)
			    		inputlayer.setVisible(false);
			    	//else
			    	//	stimuluslayer.setOpaque(false);
			    	
			    	t.removeActionListener(this);
			        }    
			    });
			t.start();
		}
		else {		
			addData("0;0","StimulusOn_"+InteractLog.stimfilename);
			stimuluslayer.setVisible(true);
			stimpresent = true;
			//if (InteractLog.inputMaskHide)
				inputlayer.setVisible(false);
			//else
	    	//	stimuluslayer.setOpaque(false);
		}

	}
	
	public void showWaitMessage() {
		message.setText("Keep holding down the "+InteractLog.repstim_keyname+" key and wait "+InteractLog.stimdelay+" seconds..."); 
		//repaint();
	}
	
	// DRAWING INTERACTION LISTENERS ///////////////////////
	
	public void mousePressed(MouseEvent m) {
		// System.out.println("press");
		// if stim now shown of, if drawing allows while stim is shown
		if ((stimpresent && InteractLog.stimdraw) || !stimpresent) {
			if (eventCount == 0) {
					eventCount++;			
			}
			if (draw) return;
				else {
					eventCount++;
					x1 = m.getX();
					y1 = m.getY();
					data+= eventCount+";"+System.nanoTime()+";"+x1+";"+y1+";ButtonDown"+newline;
					//if (draw)
		            //    return; 
					draw = true;
					paintX = x1;
					paintY = y1;
				}
		}
	}

	public void mouseReleased(MouseEvent m) {
		if ((stimpresent && InteractLog.stimdraw) || !stimpresent) {
			eventCount++;
			x2 = m.getX();
			y2 = m.getY();
			//System.out.println("Mouse: "+x2+":"+y2+" Comp: "+canvas.getX()+":"+canvas.getY());
			data+= eventCount+";"+System.nanoTime()+";"+x2+";"+y2+";ButtonReleased"+newline;
	    	currentline = new int[4];
			currentline[0] = x1;
			currentline[1] = y1;
			currentline[2] = x2;
			currentline[3] = y2;
			linestorage.add(currentline);
			if(!draw)
				return;
			
            draw = false;
			
			draw(inputgraphics, paintX, paintY, x2, y2, true);
			draw(inputgraphics2, paintX, paintY, x2, y2, true); 
			repaint(); 
		}
	}
	
	public void mouseDragged(MouseEvent m) {
		if ((stimpresent && InteractLog.stimdraw) || !stimpresent) {
			x2 = m.getX();
			y2 = m.getY();
			draw(inputgraphics, paintX, paintY, x2, y2, true);
			draw(inputgraphics2, paintX, paintY, x2, y2, true);
			repaint();
			paintX = x2;
			paintY = y2;
		}
	}

	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
	}

	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub	
	}

	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
	}
	// DRAWING EXTENSIONS
	
	private void draw(Graphics2D g, int x1, int y1, int x2, int y2, boolean refresh) {
        g.setColor(fg);
        g.setStroke(new BasicStroke(linewidth));
        g.drawLine(x1,y1,x2,y2); 
        if (refresh) {
        	// adjust for stroke size
        	int x = Math.min(x1,x2);
            int y = Math.min(y1,y2);
            int w = Math.abs(x1-x2);
            int h = Math.abs(y1-y2);
            repaint(x-13,y-13,w+26,h+26); 
        }
    }
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g.create();
        g2d.setStroke(new BasicStroke(linewidth));
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        draw(g2d,paintX,paintY, x2, y2,false);
	  }
	
	public BufferedImage getImage() {
		return bi;
		
	}
	


class StimPanel extends JPanel {

	  /**
	 * 
	 */
	private static final long serialVersionUID = 560966829203022843L;
	private Image img;
	  private Dimension imagesize;
	  private int imgw,imgh;

	  public StimPanel(Image img) {
	    this.img = img;
	    this.imgw = img.getWidth(null);
	    this.imgh = img.getHeight(null);
	  }

	  public void init() {
		    imagesize = new Dimension(imgw+getStimX(),imgh+getStimY());
		    setBackground(bg);
		    setVisible(true);
		 // override image size
		    setPreferredSize(imagesize);
		    setMinimumSize(imagesize);
		    setMaximumSize(imagesize);
		    setSize(imagesize);
		    setLayout(null);
		    if(InteractLog.stimdraw) {
		    	Graphics g = bi2.createGraphics();
			    g.drawImage(img, getIlX(),getIlY(), null);
		    }
		    if(InteractLog.famfilename != " " && firststim)
		    	addData("0;0","FamiliarisationOn_"+InteractLog.famfilename);
		       
	  }
	  public Image getBackgroundImage() {
		    return img;
		  }
	  public Dimension getImageSize() {
			return imagesize;
		}
	  
	  public void setImg(Image img) {
		this.img = img;
	}

	public int getImgw() {
		return imgw;
	}

	public int getImgh() {
		return imgh;
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if(InteractLog.stimdraw) 
			g.drawImage(bi2, getStimX(),getStimY(), null);
		else
			g.drawImage(img, getStimX(),getStimY(), null);
		
		//g.dispose();
	  }

}

class InputLayer extends JPanel {

	private static final long serialVersionUID = 1L;

	private Image img;
	  private Dimension imagesize;
	  private int imgw,imgh;

	  public InputLayer(Image img) {
	    this.img = img;
	    this.imgw = img.getWidth(null);
	    this.imgh = img.getHeight(null);
	    setDoubleBuffered(true);
	  }

	  public void init() {
		    imagesize = new Dimension(imgw+getIlX(),imgh+getIlY());
		    setBackground(bg);
		    setVisible(true);
		    // override image size
		    setPreferredSize(imagesize);
		    setMinimumSize(imagesize);
		    setMaximumSize(imagesize);
		    setSize(imagesize);
		    setLayout(null);
		    Graphics g = bi.createGraphics();
		    g.drawImage(img, getIlX(),getIlY(), null);
		    
	  }
	  public Image getBackgroundImage() {
		    return img;
		  }
	  public Dimension getImageSize() {
			return imagesize;
		}
	  
	  public void setImg(Image img) {
		this.img = img;
	}

	public int getImgw() {
		return imgw;
	}

	public int getImgh() {
		return imgh;
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(bi,getIlX(),getIlY(),null);
	  }
	}
}