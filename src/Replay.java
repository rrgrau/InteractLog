import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.ArrayList;
import java.util.ListIterator;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.awt.Graphics;

public class Replay extends JFrame implements KeyListener, WindowFocusListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static final String newline = System.getProperty("line.separator");
	private ListIterator<String[]> it;
	private Graphics2D g;
	private int screenw, screenh;
	private float strokewidth;
	private ArrayList<String[]> sizetemp,a;
	private Color fg,bg,rg,originalFg,originalBg;
	private String title;
	private Image stimImg,inputMaskImg;
	private boolean hasLostFocus,drawStim,drawInputMask;
	private int stimX,stimY,imX,imY;
	int x1,x2,ax1,cw,ch;
	int y1,y2,ay1;
	long n1 = 0;
	long n2 = 0;
	int line = 0;
	int strokenr;
	 
	Runnable updatePanel = new Runnable() {
	    public void run() { 
	    	long n1 = 0;
			long n2 = 0;
			rg = new Color(255,120,0);
			String[] temp = null;
			
			while(it.hasNext()) {
				
					line++;
					if(temp != null && line > 11) {
						n1 = toLong(temp[1]);
					}
					temp = (String[])it.next();
					n2 = toLong(temp[1]);
					//g = (Graphics2D)this.getGraphics();
					g.setColor(rg);
					g.fillRect(0, 0, 20, 20);

					g.setColor(fg);
					//time0 = System.currentTimeMillis();
					if(n1 != 0 && InteractLog.replayspeed != 0) {		
						/* //System.out.println(((n2-n1)/1000000)/SMouseLog.replayspeed);
						do {
							time1=System.currentTimeMillis();
						}
						while (time1-time0 < (((n2-n1)/1000000)/SMouseLog.replayspeed));
						*/
						 try {
							Thread.sleep((long)(((n2-n1)/1000000)/InteractLog.replayspeed));
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					if(temp[4].equals("ButtonDown")) {
							strokenr++;
							x1 = toInt(temp[2]);
							y1 = toInt(temp[3]);
							//System.out.print(temp[2]+","+temp[3]);
							
					}
					else if(temp[4].equals("ButtonReleased")){
						x2 = toInt(temp[2]);
						y2 = toInt(temp[3]);
						g.setStroke(new BasicStroke(strokewidth));
						g.drawLine(x1,y1,x2,y2);
						//drawPad.add(new JLabel("bla"+line));
						//System.out.println("Draw "+x1+","+y1+","+temp[2]+","+temp[3]);

						// Annotation
						if(InteractLog.showan) {
							
							// Variant 1: Bubbles on the strokes
							if(InteractLog.astyle == 1) {
				
								// determine position
								if(x1 <= x2) {
									int l = x2 - x1;
									ax1 = x1 + (l/4);
								}
								else {
									int l = x1 - x2;
									ax1 = x1 - (l/4);
								}
								if(y1 <= y2) {
									int l = y2 - y1;
									ay1 = y1 + (l/4);
								}
								else {
									int l = y1 - y2;
									ay1 = y1 - (l/4);
								}
								g.setStroke(new BasicStroke(1));
								g.setColor(InteractLog.af);
								if(InteractLog.showline) g.drawLine(ax1,ay1,x1,y1);
								if(InteractLog.showbubble) {
									g.setColor(InteractLog.ag);
							        g.fillOval(ax1-cw/2,ay1-ch/2, cw, ch);
							        g.setColor(InteractLog.af);
							        g.drawOval(ax1-cw/2,ay1-ch/2, cw, ch);
								}
								
						        g.setFont(new Font(InteractLog.afontface, Font.BOLD, InteractLog.afontsize));
						        int stringLen = (int) 
						                g.getFontMetrics().getStringBounds(strokenr+"", g).getWidth();
						            g.drawString(strokenr+"", ax1-stringLen/2+1, ay1+cw/2-7); 
						        
							}
							
							if(InteractLog.astyle == 2) {

						        // Variant 2: Bubbles alongside the strokes
								
								// determine position
								// lt - rb
								if(x1 <= x2 && y1 <= y2) {
									int l1 = x2 - x1;
									int l2 = y2 - y1;
									ax1 = x1 + (l1/4)+InteractLog.afontsize;
									ay1 = y1 + (l2/4)-InteractLog.afontsize;
								}
								// lb - rt
								else if (x1 <= x2 && y1 > y2) {
									int l1 = x2 - x1;
									int l2 = y1 - y2;
									ax1 = x1 + (l1/4)-InteractLog.afontsize;
									ay1 = y1 - (l2/4)-InteractLog.afontsize;
								}
								// rb - lt
								else if (x1 > x2 && y1 > y2) {
									int l1 = x1 - x2;
									int l2 = y1 - y2;
									ax1 = x1 - (l1/4)-InteractLog.afontsize;
									ay1 = y1 - (l2/4)+InteractLog.afontsize;
								}
								// rt - lb
								else if (x1 > x2 && y1 <= y2) {
									int l1 = x1 - x2;
									int l2 = y2 - y1;
									ax1 = x1 - (l1/4)+InteractLog.afontsize;
									ay1 = y1 + (l2/4)+InteractLog.afontsize;
								}
								
								g.setStroke(new BasicStroke(1));
								g.setColor(InteractLog.af);
								if(InteractLog.showline) g.drawLine(ax1,ay1,x1,y1);
								if(InteractLog.showbubble) {
									g.setColor(InteractLog.ag);
							        g.fillOval(ax1-cw/2,ay1-ch/2, cw, ch);
							        g.setColor(InteractLog.af);
							        g.drawOval(ax1-cw/2,ay1-ch/2, cw, ch);
								}
						        
						        g.setFont(new Font(InteractLog.afontface, Font.BOLD, InteractLog.afontsize));
						        
						        int stringLen = (int) 
						                g.getFontMetrics().getStringBounds(strokenr+"", g).getWidth();
						            g.drawString(strokenr+"", ax1-stringLen/2+1, ay1+cw/2-7); 
							}
						}
					}
					else if((temp[4].equals("InitialStimulusOn") || temp[4].equals("StimulusOn")) && InteractLog.showstim && drawStim){
						g.drawImage(stimImg, stimX,stimY, null);
						if(InteractLog.showstimp)
							drawStim = false;
						
					}
					else if((temp[4].equals("InitialStimulusOff") || temp[4].equals("StimulusOff")) && InteractLog.showstim && drawStim && !InteractLog.showstimp){
						//drawStim = false;
						g.clearRect(0, 0, screenw, screenh);
						revalidate();
						repaint();
						//if(drawInputMask && !drawStim) g.drawImage(inputMaskImg, imX,imY, null);
					}
					//else if((temp[4].equals("InitialStimulusOff") || temp[4].equals("StimulusOff")) && SMouseLog.showstim && drawStim){
						//drawStim = false;
					//	revalidate();
					//	repaint();
						//if(drawInputMask && !drawStim) g.drawImage(inputMaskImg, imX,imY, null);
					//}
					// initial show of IM
					if((temp[4].equals("InitialStimulusOff")) && InteractLog.showim && drawInputMask)
						g.drawImage(inputMaskImg, imX,imY, null);
					
					// only refresh image if a repeated stim was shown before
					if(temp[4].equals("StimulusOff") && InteractLog.showim && drawInputMask && drawStim)
						g.drawImage(inputMaskImg, imX,imY, null);
			
			}
			//draw final stimulus

			g.setColor(bg);
			g.fillRect(0, 0, 20, 20);
			g.dispose();
	    }
	};
	 
	public Replay(ArrayList<String[]> a) {
		super();
        this.setUndecorated(false);
        this.hasLostFocus = false;
        this.sizetemp = a;
        this.a = a;
        screenw=0;
        screenh=0;
		//it = this.a.iterator();
		//this.setMaximizedBounds(g.getMaximumWindowBounds());
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        int line = 0;
        ListIterator<String[]> s = sizetemp.listIterator();
        while(s.hasNext()) {
            String[] size = (String[])s.next();
			line++;
			if(size != null) {
				if (size[0].trim().equals("Name")) {
					title = "Replay of "+size[1];
				}
				else if (size[0].trim().equals("Trial Number")) {
					title += ", Trial Number "+size[1];
				}
				else if (size[0].trim().equals("Trial Start")) {
					title += ", Start: "+size[1];
				}
				else if (size[0].trim().equals("Trial End")) {
					title += ", End: "+size[1];
				}
				else if (size[0].trim().equals("Stroke Colour")) {
						fg = new Color(Integer.parseInt(size[1]),Integer.parseInt(size[2]),Integer.parseInt(size[3]),Integer.parseInt(size[4]));
						originalFg = fg;
				}
				else if (size[0].trim().equals("Background Colour")) {
						
						bg = new Color(Integer.parseInt(size[1]),Integer.parseInt(size[2]),Integer.parseInt(size[3]),Integer.parseInt(size[4]));
						originalBg = bg;
						this.setBackground(bg);
				}
				else if (size[0].trim().equals("Stroke Width")) {
					strokewidth = Float.parseFloat(size[1]);
				}
				else if (size[0].trim().equals("Stimulus File") && size[1] != "") {
					stimImg = new ImageIcon(size[1]).getImage();	
				}
				else if (size[0].trim().equals("Input Mask File") && size[1] != "") {
					inputMaskImg = new ImageIcon(size[1]).getImage();	
		}
				
				else if (size[0].trim().equals("Drawing Pad Size")) {
					screenw = toInt(size[1]);
					//System.out.println("Drawing Pad width was "+screenw);
					screenh = toInt(size[2]);
					//System.out.println("Drawing Pad height was "+screenh);
			        this.getContentPane().setPreferredSize(new Dimension(screenw,screenh));
			        //System.out.println("Line:"+line+" "+size[0]+"-"+size[1]);
			        //break;
				}
			}
			
        }
        this.setTitle(title);
        // this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.pack();
     //   display GUI 
        this.setVisible(true);
		this.addKeyListener(this);
		this.addWindowFocusListener(this);
		setFocusable(true);
		this.requestFocus();

        init();
	}
	
	public void init() {
		this.repaint();
		strokenr = 0;
		g = (Graphics2D)this.getContentPane().getGraphics();
		
		if(InteractLog.overrideColours == true) {
			fg = Color.BLACK;
			bg = Color.WHITE;
		}
		else {
			fg = originalFg;
			bg = originalBg;
		}
			
		if(InteractLog.showstim && stimImg != null) {
			stimX = (screenw - stimImg.getWidth(null)) / 2;
		    stimY = (screenh - stimImg.getHeight(null)) / 2;
			//g.drawImage(stimImg, stimX,stimY, null);	
			drawStim = true;
			//System.out.println("Drawing Stimulus Image");
		}
		else 
			drawStim = false;
		
		if(InteractLog.showim && inputMaskImg != null) {
			imX = (screenw - inputMaskImg.getWidth(null)) / 2;
		    imY = (screenh - inputMaskImg.getHeight(null)) / 2;
			//g.drawImage(inputMaskImg, imX,imY, null);	
		    drawInputMask = true;
		}
		else 
			drawInputMask = false;
		
		it = (this.a).listIterator();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(bg);
		g.fillRect(0, 0, screenw, screenh);
		line = 0;
		// determine the size of the annotation "bubbles"
		// height = fontsize + 12 pixels for the margins
		ch = InteractLog.afontsize+12;
		// width = the graphical width of the highest stroke number + 12 pixels for the margins
		// take nr of elements in the array - 5 for metadata, /2 because every stroke has two points (entries), +1 for division result correction
		cw = (((a.size()-5)/2)+1);
		cw = (int) g.getFontMetrics().getStringBounds(cw+"", g).getWidth()+12;

		// if the width < height, make it a circle
		if(cw < ch) cw = ch;
		//g.setStroke(new BasicStroke(strokewidth));
		
		//SwingUtilities.invokeLater(updatePanel);
		//repaint();
		//run();
    }
    // GUI updates are taken care of from a separate Thread to prevent interferences with time measurement
	
	public void paint(Graphics g) {
		//run();
		SwingUtilities.invokeLater(updatePanel);
		
	}
	

	public void keyPressed(KeyEvent e) {
		int kc = e.getKeyCode();
    	// if key pressed was ESCAPE, write to file and end trial, if successful
		if(kc == 82 || kc == KeyEvent.VK_SPACE || (e.isControlDown() && kc == KeyEvent.VK_R)) {
			//System.out.println(kc);
			init();
		}
		if(kc == KeyEvent.VK_ESCAPE || (e.isControlDown() && kc == KeyEvent.VK_ENTER)) {
			this.dispose();
		}
	}

	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
	}

	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub 	}
	}

	public JFrame getDisplayArea() {
		return this;
	}
	private int toInt(String s) {
		Integer i=0;
		try {
			i = Integer.parseInt(s);
			} catch (Exception E){
	//	NaN
			}
	return i;
}
	private Long toLong(String s) {
		Long i = 0L;
		try {
			i = Long.parseLong(s);
			} catch (Exception E){
			}
	return i;
	}

	public void windowGainedFocus(WindowEvent we) {
		if(hasLostFocus) {
			this.repaint();
			g = (Graphics2D)this.getGraphics();
		}
	}

	public void windowLostFocus(WindowEvent we) {
		hasLostFocus = true;
		
	}
}