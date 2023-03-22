import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class InteractLog {
	
	static JFrame mainframe = null;
	public static final char data_separator = ';'; // semicolon
	public static final String data_separator_descr = "Semicolon";
	public static final int repstim_keycode = 38; // UP key
	public static final String repstim_keyname = "UP";
	public static final int nextstim_keycode = 39; // RIGHT key
	public static final String nextstim_keyname = "RIGHT";
	public static final int prevstim_keycode = 37; // LEFT key
	public static final String prevstim_keyname = "LEFT";
	public static final int hidestim_keycode = 40; // DOWN key
	public static final String hidestim_keyname = "DOWN";
	public static final int endexp_keycode = 27; // ESC key
	public static final String endexp_keyname = "ESCAPE";
	
	public static final int simultaneousInputs = 12; // sim. keyPresses held in buffer
	
	static String participant_ID;
	static String[] filedata,imagefiledata,configfiledata;
	static String filepath;
	static String stimfilename = " ";
	static String stimfilepath = " ";
	static File[] stimfiles = new File[0];
	static String inputlayerfilepath = " ";
	static String inputlayerfilename = " ";
	static File[] imfile = new File[0];
	static String famfilepath = " ";
	static String famfilename = " ";
	static File[] famfile = new File[0];
	static String configfilepath = "config/default.ini";
	static String version = "2.3";
	static String versiondate = "February 2022";
	static Dimension frameDimension;
	public static double replayspeed;
	public static String afontface;
	public static int afontsize;
	public static int astyle;
	public static float strokewidth;
	public static int stimtime = 0; // stimulus timer
	public static int famtime = 0;  // familiarisation timer
	public static int stimdelay = 0;
	public static Date now;
	static JFileChooser stimChooser = new JFileChooser();
	static JFileChooser famChooser = new JFileChooser(); 
	static JFileChooser imChooser = new JFileChooser();
	public static boolean showrb,uistim,stimrepeat,stimdraw,showstim,showim,showstimp,showan,showline,showbubble,overrideColours,inputMaskHide,uifam,holdkey;
	//public static boolean ssaf; - will not use this
	static Font msgFont = new Font("Message", Font.PLAIN, 24);
	static Font msgFontsmall = new Font("Message", Font.PLAIN, 18);
	public static JPanel contentPane;
	public static Color bg,fg,af,ag;
	static String experimentSession_ID = ""; // There are no diff sessions in SML
	static final String newline = System.getProperty("line.separator");
	private static JMenuBar mainmenu;
	private static JCheckBoxMenuItem sb5,sb6,sb7,sb8,an1,do1,do11,do2,scOverride,asx1,asx2,rbx1,fb1,fb2;
	private static JRadioButtonMenuItem as1,as2,option025,option05,option075,option1,option2,option3,option4,option5,option6;
	private static ButtonGroup replayGroup;
	
	public static void main(String[] args) {
 		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			    public void run() {
			    	buildGUI(mainframe);
			    }
			});
    }

	private static void buildGUI(JFrame mainframe) {
		
		// check whether the mainframe has been defined already
		
	if (mainframe == null) {
		
		// create the main window.
        mainframe = new JFrame("InteractLog "+version);
        // create main menu;
        mainframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      
        // create the main pane
        contentPane = new JPanel();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));
        Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
        mainframe.setBounds(screensize.width/2-350,screensize.height/2-200,screensize.width, screensize.height);
        contentPane.setBounds(0,0,screensize.width, screensize.height);
        contentPane.setPreferredSize(new Dimension(700,400));

        // contentPane.setBorder(BorderFactory.createLineBorder(Color.black));
        // contentPane.setBackground(Color.darkGray);
        mainframe.setContentPane(contentPane);
        buildMainMenu(mainframe);
       // set the system's L&F
        
        try {
        	UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
             }	
        catch (ClassNotFoundException e) {
        } 
        catch (UnsupportedLookAndFeelException e) {
        } 
        catch (Exception e) {
        }
        
        try {
			// create default folders, if not exist
        	new File("stimuli").mkdirs();
			new File("inputmasks").mkdirs();
			new File("config").mkdirs();
			//read current config
			loadConfiguration(configfilepath);

		} catch (Exception e1) {
			// if config doesn't exist or faulty, create default config
				if(createDefaultConfig(configfilepath))
					try {
						loadConfiguration(configfilepath);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		}
	}
	
	//	 organize everything
	SwingUtilities.updateComponentTreeUI(mainframe);
	mainframe.pack();
	//   display GUI 
	mainframe.setVisible(true);
}
	
	// set up new trial with the participant info given
    private static void startExperiment(int tn) {
    	Trial t = new Trial(tn);
		t.setParticipantID(participant_ID);
		t.setExperimentSessionID(experimentSession_ID);
    	new Experiment(t);
	}
    
    private static void replayData(ArrayList<String[]> a) {
    	new Replay(a);
	}
    
	private static void quit()
    {
		System.exit(0);
    }

	//@SuppressWarnings("deprecation")
	private static void buildMainMenu(JFrame mainWindow) {
		
//		 create main menu bar and menu entries
        mainmenu = new JMenuBar();
        final int SHORTCUT_MASK =
            Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
		
     // initialize menu

        bg = new Color(255,255,255); 
        fg = new Color(0,0,0);
        ag = new Color(255,200,80); 
        af = new Color(0,0,255);
        afontsize = 14;		
        afontface = "Arial";
        astyle = 1;
		strokewidth = 2;
		showrb = true;
		showstim = true;
		showstimp = false;
		showim = true;
		stimrepeat = true;
		stimdelay = 0;
		stimdraw = false; // allow drawing while stimulus shown
		showan = true;
		showline = true;
		showbubble = true;
		overrideColours = false;
		inputMaskHide = true;
		stimtime = 0;
		uifam = true;  // allow keypress to end fam
        uistim = true; // allow keypress to trigger stim
        holdkey = true;
        //ssaf = false; // Show First Stimulus After Familiarisation
		replayspeed = 2;
		// init stimfiles to prevent NPE when no stim is selected before starting experiment

/////////////////////  ACTION  //////////////////////////////////////////	  
        JMenu filemenu = new JMenu("Action");
        	final JMenuItem open = new JMenuItem("New Experiment");
	        	open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, SHORTCUT_MASK));
	        	open.addActionListener(new ActionListener() {
                       public void actionPerformed(ActionEvent e) { 
                       			contentPane.removeAll();
                            	participant_ID = (String)JOptionPane.showInputDialog(
                                            mainframe,
                                            "Please enter participant name: ","New Session",
                                            JOptionPane.PLAIN_MESSAGE,
                                            null,
                                            null,
                                            "Anonymous Participant");
                            	if(participant_ID == null)  
     	                        	participant_ID = "Anonymous Participant";
                            	
                            	now = new Date();
                            	SimpleDateFormat folderdate = new SimpleDateFormat ("yyyy-MM-dd");

                            	// create data directories
                            	new File("data/"+folderdate.format(now)).mkdirs();
                            	
                            	filepath = "data/"+folderdate.format(now)+"/"+participant_ID;
                                //System.out.println(filepath);
                                //System.out.println(System.getProperty("user.dir"));
                            	JLabel l1;
                            	if(participant_ID.equals("Anonymous Participant")) 
                            		 l1 = new JLabel("Welcome to this experiment.");
                            	else
                            		 l1 = new JLabel("Welcome to this experiment, "+participant_ID+".");
                        		//JLabel l2 = new JLabel("Resize this window to set the dimensions of the recording pad as desired.");
                        		JLabel l3 = new JLabel("When you are ready to start, press 'N' to open the recording pad.");
                        		JLabel l4 = new JLabel("When you are finished drawing, press ESC to close the recording pad.");
                        		l1.setAlignmentX(Component.CENTER_ALIGNMENT);
                        		l1.setAlignmentY(Component.CENTER_ALIGNMENT);
                        		//l2.setAlignmentX(Component.CENTER_ALIGNMENT);
                        		l3.setAlignmentX(Component.CENTER_ALIGNMENT);
                        		l4.setAlignmentX(Component.CENTER_ALIGNMENT);
                        		l1.setFont(msgFont);
                        		//l2.setFont(msgFontsmall);
                        		l3.setFont(msgFontsmall);
                        		l4.setFont(msgFontsmall);
                        		
                        		contentPane.add(new JLabel(" "));
                        		contentPane.add(l1);
                        		//contentPane.add(new JLabel(" "));
                        		//contentPane.add(l2);
                        		contentPane.add(new JLabel(" "));
                        		contentPane.add(l3);
                        		contentPane.add(new JLabel(" "));
                        		contentPane.add(l4);
                        		contentPane.revalidate();
                        		contentPane.setFocusable(true);
								contentPane.requestFocusInWindow();
								contentPane.grabFocus();
								contentPane.addKeyListener(new KeyAdapter() {
									@Override
									public void keyPressed(KeyEvent e) 	{
										if(e.getKeyCode() == KeyEvent.VK_N) {
											
											contentPane.removeAll();
										    contentPane.repaint();
											//contentPane.setFocusable(false);
											startExperiment(1);
											contentPane.removeKeyListener(this);
										}
										
									}
									});
                            	
							open.setEnabled(false);
							open.removeActionListener(this);
							
                       }
							
	            });
	        	
	        	final JMenuItem loadConfig = new JMenuItem("Load Configuration");
	        	loadConfig.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, SHORTCUT_MASK));
	        	loadConfig.addActionListener(new ActionListener() {
                       public void actionPerformed(ActionEvent e) {  
                       configfiledata = (loadFile(new Frame(), "Open Configuration File", "config", "default.ini"));
                       try {
                        	// create image filepath
                        	if (configfiledata[1] != null) {
                        		configfilepath = "config/"+configfiledata[1];
        						loadConfiguration(configfilepath);
                        	}}
                        	catch (Exception e1) {
								// TODO Auto-generated catch block
								//e1.printStackTrace();
                        		System.out.println("Error reading file: "+newline+e1+newline+newline+"Program shut down.");
								quit();
							}
                       } 
							
	            });
	        	
	        	final JMenuItem saveConfig = new JMenuItem("Save Current Configuration");
	        	
	        	saveConfig.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, SHORTCUT_MASK));
	        	saveConfig.addActionListener(new ActionListener() {
                       public void actionPerformed(ActionEvent e) {  
                       configfiledata = (selectFile(new Frame(), "Enter a name for this configuration or select file to overwrite", "config", "default"));	   
                       try {
                        	if (configfiledata[1] != null) {
                        		configfilepath = "config/"+configfiledata[1];
                        		saveConfiguration(configfilepath);
                        	}}
                        	catch (Exception e1) {
                        		System.out.println("Error saving file: "+newline+e1+newline+newline+"Program shut down.");
								quit();
							}
                       } 
							
	            });
	        	
	        	JMenuItem replayData = new JMenuItem("Replay Trial");
	        	replayData.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, SHORTCUT_MASK));
	        	replayData.addActionListener(new ActionListener() {
		                       public void actionPerformed(ActionEvent e) { 
		                    	   String[] filedata = new String[2];
	                               filedata = (loadFile(new Frame(), "Open Data File for Replay", "data", "*.txt"));
	                               filepath = filedata[0];
	                               
	                               try {
	                                	// parse the experiment file
	                                	if (filedata[1] != null) {
	                                		contentPane.removeAll();
										    contentPane.repaint();
										    contentPane.setFocusable(false);
											replayData(readDataFile(filedata[0]+filedata[1]));
	                                	}}
	                                	catch (Exception e1) {
											// TODO Auto-generated catch block
											//e1.printStackTrace();
	                                		System.out.println("Error reading file: "+newline+e1+newline+newline+"Program shut down.");
											quit();
										}
	                                	//open.setEnabled(false);
		                       } 
		                   });

	    	JMenuItem quit = new JMenuItem("Quit");
	        //quit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, SHORTCUT_MASK));
	    	quit.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) { quit(); } 
	                   });
	    	
/////////////////////  INPUT MASK  //////////////////////////////////////////	  	        	
	        JMenu inputlayeroptionsmenu = new JMenu("Input Mask");
	        	
	        	final JMenuItem il1 = new JMenuItem("Set Input Mask Image",new InputLayerIcon());
	        	
	        	//il1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, SHORTCUT_MASK));
	        	il1.addActionListener(new ActionListener() {
	        		public void actionPerformed(ActionEvent e) {
	                       imChooser.setCurrentDirectory(new File("inputmasks"));
	                       // this needs to be true, else the updating of the icon doesn't work properly
	                       imChooser.setMultiSelectionEnabled(true);
	                       imChooser.showOpenDialog(new Frame());
	                       imfile = imChooser.getSelectedFiles();
	                       if (imfile.length > 0) {
	                    	   inputlayerfilepath = imfile[0].getPath();
	                    	   inputlayerfilename = imfile[0].getName();
	                         
	                       }    
                       } 
							
	            });
	        	final JMenuItem il2 = new JMenuItem("Clear Image");
        		//il2.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, SHORTCUT_MASK));
	        	il2.addActionListener(new ActionListener() {
                       public void actionPerformed(ActionEvent e) { 
                       imfile = new File[0];
                       inputlayerfilepath = " ";
							// sb2.removeActionListener(this);
                       }
							
	            });
/////////////////////  FAMILIARISATION  //////////////////////////////////////////	  	        	
        JMenu familiarisationmenu = new JMenu("Familiarisation");
        	
        	final JMenuItem fam1 = new JMenuItem("Set Familiarisation Image",new FamIcon());
        	fam1.addActionListener(new ActionListener() {
                   public void actionPerformed(ActionEvent e) {
                   famChooser.setCurrentDirectory(new File("stimuli"));
                // this needs to be true, else the updating of the icon doesn't work properly
                   famChooser.setMultiSelectionEnabled(true);
                   famChooser.showOpenDialog(new Frame());
                   famfile = famChooser.getSelectedFiles();
                   if (famfile.length > 0) {
                	   famfilepath = famfile[0].getPath();
                       famfilename = famfile[0].getName();
                     
                   }                    
               }
						
            });
        	final JMenuItem fam2 = new JMenuItem("Clear Image");
        	fam2.addActionListener(new ActionListener() {
                   public void actionPerformed(ActionEvent e) { 
                	    famfile = new File[0];
                   		famfilepath = " ";
                   }
						
            });
        	
        	final JMenuItem famo1 = new JMenuItem("Set Familiarisation Time Limit",new TimeIcon("famtimer"));
        	famo1.addActionListener(new ActionListener() {
                   public void actionPerformed(ActionEvent e) { ; 
                       try {
							famtime = Integer.parseInt((String) JOptionPane.showInputDialog(
							           mainframe,
							           "Milliseconds: ","Familiarisation Timer",
							           JOptionPane.PLAIN_MESSAGE,
							           null,
							           null,
							           famtime));
							// setting a fam timer disables the keypress option 
							
							
						} catch (NumberFormatException e1) {
							// TODO Auto-generated catch block
							//e1.printStackTrace();
						}
                        if(famtime > 0) {
							uifam = false;
							fb1.setSelected(false);
                        }
                   }
						
            });
        	fb1 = new JCheckBoxMenuItem("Allow Keypress To End Familiarisation");
        	fb1.setSelected(true);
        	fb1.addActionListener(new ActionListener() {
                   public void actionPerformed(ActionEvent e) { ;
                   		if (uifam == false) uifam = true; 
                   		else if (uifam == true) uifam = false; 
                   		// enabling this option disables the fam timer
                   		if (uifam == true)
                   			famtime = 0;
                   }
            });
        	/*
        	fb2 = new JCheckBoxMenuItem("Show First Stimulus After Familiarisation");
        	fb2.setSelected(false);
        	fb2.addActionListener(new ActionListener() {
                   public void actionPerformed(ActionEvent e) { ;
                   		if (ssaf == false) ssaf = true; 
                   		else if (ssaf == true) ssaf = false; 
                   		// enabling this option disables the fam timer
                   }
            });
            */
        	
/////////////////////  STIMULUS  //////////////////////////////////////////	        	
	        JMenu stimoptionsmenu = new JMenu("Stimulus");
	        	
	        	final JMenu sb1 = new JMenu("Set Stimulus Images");
	        	sb1.setIcon(new StimIcon(0));
	        	sb1.addMouseListener(new MouseAdapter() {
	        		@Override
	                public void mouseClicked(MouseEvent e) { 
	        			// reinit on click in case user has clicked the menu before
	        			   stimfiles = new File[0];
                   		   stimfilepath = " ";
	        			   stimChooser.setCurrentDirectory(new File("stimuli"));
	        			   stimChooser.setMultiSelectionEnabled(true);
	                       stimChooser.showOpenDialog(new Frame());
	                       stimfiles = stimChooser.getSelectedFiles();
	                       // set the first file to use as stim
	                       if (stimfiles.length > 0 && InteractLog.stimfilepath != " ") {
		                       stimfilepath = stimfiles[0].getPath();
		                       stimfilename = stimfiles[0].getName();
		                    // print file list as sub menu
		                       
		       	        		//if(InteractLog.stimfilepath != " " ) ;
		       	        		/*	
			               			for (int i = 0; i < InteractLog.stimfiles.length; i++) {
			               				// store the file currently handled - must be final for the actionlistener to be able to access it   
			               				final File currentStimFile = InteractLog.stimfiles[i];
			               				// add image icon for the file
			               				sb1.add(new JMenuItem(InteractLog.stimfiles[i].getName(),new StimIcon(i)));
			               				// add actionlistener to open the file in image program for inspection
			               				sb1.getItem(i).addActionListener(new ActionListener() {
			                                public void actionPerformed(ActionEvent e) { ; 
			     	                    	  try {
												Desktop.getDesktop().open(currentStimFile);
											} catch (IOException e1) {
												// TODO Auto-generated catch block
												e1.printStackTrace();
											}			                          
			                            }	
			               				});
			               		} 
			               		*/
		                   }
                       }
							
	            });
	        	// update submenu on load config
	        	sb1.addChangeListener(new ChangeListener() {
					public void stateChanged(ChangeEvent arg0) {
						if (stimfiles.length > 0 && !stimfilepath.isEmpty()) {
	                   		   sb1.removeAll();
   	               				for (int i = 0; i < InteractLog.stimfiles.length; i++) {
			               				// store the file currently handled - must be final for the actionlistener to be able to access it   
			               				final File currentStimFile = InteractLog.stimfiles[i];
			               				// add image icon for the file
			               				sb1.add(new JMenuItem(InteractLog.stimfiles[i].getName(),new StimIcon(i)));
			               				// add actionlistener to open the file in image program for inspection
			               				sb1.getItem(i).addActionListener(new ActionListener() {
			                                public void actionPerformed(ActionEvent e) { ; 
			     	                    	  try {
												Desktop.getDesktop().open(currentStimFile);
											} catch (IOException e1) {
												// TODO Auto-generated catch block
												e1.printStackTrace();
											}
			                            }	
			     	            });
			               		}
		                   }
					}
	        	});
	        	
	        	final JMenuItem sb2 = new JMenuItem("Clear Images");
	        	sb2.addActionListener(new ActionListener() {
                       public void actionPerformed(ActionEvent e) { ; 
                       		stimfiles = new File[0];
                       		stimfilepath = " ";
                       		sb1.removeAll();
                       }
							
	            });
	        	final JMenuItem sb3 = new JMenuItem("Set Stimulus Time Limit",new TimeIcon("stimtimer"));
        		sb3.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, SHORTCUT_MASK));
	        	sb3.addActionListener(new ActionListener() {
                       public void actionPerformed(ActionEvent e) { ; 
	                       try {
								stimtime = Integer.parseInt((String) JOptionPane.showInputDialog(
								           mainframe,
								           "Milliseconds: ","Stimulus Timer",
								           JOptionPane.PLAIN_MESSAGE,
								           null,
								           null,
								           stimtime));
							} catch (NumberFormatException e1) {
								// TODO Auto-generated catch block
								//e1.printStackTrace();
							}
                       }
							
	            });
	        	final JMenuItem sb4 = new JMenuItem("Set Delay for repeated display",new TimeIcon("delay"));
	        	sb4.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, SHORTCUT_MASK));
	        	sb4.addActionListener(new ActionListener() {
                       public void actionPerformed(ActionEvent e) { ; 
	                       try {
							stimdelay = Integer.parseInt((String) JOptionPane.showInputDialog(
							           mainframe,
							           "Milliseconds: ","Stimulus Delay",
							           JOptionPane.PLAIN_MESSAGE,
							           null,
							           null,
							           stimdelay));
							           //sb3.removeActionListener(this);
							} catch (NumberFormatException e1) {
								// TODO Auto-generated catch block
								//e1.printStackTrace();
							}
                       }
	            });
	        	sb5 = new JCheckBoxMenuItem("Hold Key to Display Stimulus");
	        	sb5.addActionListener(new ActionListener() {
	        		   public void actionPerformed(ActionEvent e) {  
                       		if (holdkey == false) holdkey = true; else if (holdkey == true) holdkey = false; 
                       }
	            });
	        	sb6 = new JCheckBoxMenuItem("Hide Input Mask During Stimulus Progression");
	        	sb6.addActionListener(new ActionListener() {
                       public void actionPerformed(ActionEvent e) {  
                       		if (inputMaskHide == false) inputMaskHide = true; else if (inputMaskHide == true) inputMaskHide = false; 
                       }
	            });
	        	sb7 = new JCheckBoxMenuItem("Allow Repeated Stimulus Display");
	        	sb7.addActionListener(new ActionListener() {
                       public void actionPerformed(ActionEvent e) {  
                       		if (stimrepeat == false) stimrepeat = true; else if (stimrepeat == true) stimrepeat = false; 
                       }
	            });
	        	sb8 = new JCheckBoxMenuItem("Allow Drawing During Stimulus Display");
	        	sb8.addActionListener(new ActionListener() {
                       public void actionPerformed(ActionEvent e) {  
                       if (stimdraw == false) stimdraw = true; else if (stimdraw == true) stimdraw = false; 
							//sb4.removeActionListener(this);
                       //System.out.println(uistim);
                       }
							
	            });

/////////////////////  DRAWING //////////////////////////////////////////	  
	        JMenu drawoptionsmenu = new JMenu("Drawing");
	        	ButtonGroup drawGroup2 = new ButtonGroup();
	        	
	        	final JMenuItem ws1 = new JMenuItem("Drawing Pad Width", new TextIcon("width"));
        		ws1.addActionListener(new ActionListener() {
                       public void actionPerformed(ActionEvent e) { ; 
                       try {
   						frameDimension.setSize(Integer.parseInt((String) JOptionPane.showInputDialog(
   						           mainframe,
   						           "Pixels: ","Width",
   						           JOptionPane.PLAIN_MESSAGE,
   						           null,
   						           null,
   						           (int)frameDimension.getWidth())), (int)frameDimension.getHeight());
   					} catch (NumberFormatException e1) {
   						// TODO Auto-generated catch block
   						//e1.printStackTrace();
   					}
                       }
	            });
        		final JMenuItem ws2 = new JMenuItem("Drawing Pad Height", new TextIcon("height"));
        		ws2.addActionListener(new ActionListener() {
                       public void actionPerformed(ActionEvent e) { ; 
                       try {
   						frameDimension.setSize((int)frameDimension.getWidth(), Integer.parseInt((String) JOptionPane.showInputDialog(
   						           mainframe,
   						           "Pixels: ","Height",
   						           JOptionPane.PLAIN_MESSAGE,
   						           null,
   						           null,
   						           (int)frameDimension.getHeight())));
   					} catch (NumberFormatException e1) {
   						// TODO Auto-generated catch block
   						//e1.printStackTrace();
   					}
                       }
	            });
	        	
	        	final JMenu sw0 = new JMenu("Stroke Size");
	        	sw0.setIcon(new CurrentStrokeIcon());
	        	
        		final JRadioButtonMenuItem sw1 = new JRadioButtonMenuItem("1 pixel",new StrokeIcon(1));
        		sw1.addActionListener(new ActionListener() {
                       public void actionPerformed(ActionEvent e) { ; 
                            strokewidth = 1;
                       }
							
	            });
        		final JRadioButtonMenuItem sw2 = new JRadioButtonMenuItem("2 pixels",new StrokeIcon(2));
        		sw2.addActionListener(new ActionListener() {
                       public void actionPerformed(ActionEvent e) { ; 
                            strokewidth = 2;
                       }
							
	            });
        		final JRadioButtonMenuItem sw3 = new JRadioButtonMenuItem("3 pixels",new StrokeIcon(3));
        		sw3.addActionListener(new ActionListener() {
                       public void actionPerformed(ActionEvent e) { ; 
                            strokewidth = 3;
							//sw3.removeActionListener(this);
                       }
							
	            });
        		final JRadioButtonMenuItem sw4 = new JRadioButtonMenuItem("4 pixels",new StrokeIcon(4));
        		sw4.addActionListener(new ActionListener() {
                       public void actionPerformed(ActionEvent e) { ; 
                            strokewidth = 4;
							//sw3.removeActionListener(this);
                       }
							
	            });
        		final JRadioButtonMenuItem sw5 = new JRadioButtonMenuItem("5 pixels",new StrokeIcon(5));
        		sw5.addActionListener(new ActionListener() {
                       public void actionPerformed(ActionEvent e) { ; 
                            strokewidth = 5;
							//sw3.removeActionListener(this);
                       }
							
	            });
        		final JRadioButtonMenuItem sw6 = new JRadioButtonMenuItem("6 pixels",new StrokeIcon(6));
        		sw6.addActionListener(new ActionListener() {
                       public void actionPerformed(ActionEvent e) { ; 
                            strokewidth = 6;
							//sw3.removeActionListener(this);
                       }
							
	            });
        		final JRadioButtonMenuItem sw7 = new JRadioButtonMenuItem("7 pixels",new StrokeIcon(7));
        		sw7.addActionListener(new ActionListener() {
                       public void actionPerformed(ActionEvent e) { ; 
                            strokewidth = 7;
							//sw3.removeActionListener(this);
                       }
							
	            });
        		final JRadioButtonMenuItem sw8 = new JRadioButtonMenuItem("8 pixels",new StrokeIcon(8));
        		sw8.addActionListener(new ActionListener() {
                       public void actionPerformed(ActionEvent e) { ; 
                            strokewidth = 8;
							//sw3.removeActionListener(this);
                       }
							
	            });
        		
        		//final JMenu cs0 = new JMenu("Stroke Colours");
        		
        		final JMenuItem cs1 = new JMenuItem("Stroke Colour",new ColorIcon(1));
        		cs1.addActionListener(new ActionListener() {
                       public void actionPerformed(ActionEvent e) { ; 
                       fg = JColorChooser.showDialog(
                               mainframe,
                               "Choose Stroke Color",
                               fg);
							//cs1.removeActionListener(this);
                       }
	            });
        		
        		final JMenuItem cs2 = new JMenuItem("Background Colour",new ColorIcon(2));
        		cs2.addActionListener(new ActionListener() {
                       public void actionPerformed(ActionEvent e) { ; 
                       bg = JColorChooser.showDialog(
                               mainframe,
                               "Choose Background Color",
                               bg);
							//cs2.removeActionListener(this);
                       }
							
	            });
        		
        		rbx1 = new JCheckBoxMenuItem("Show Rubberband");
	        	rbx1.addActionListener(new ActionListener() {
                       public void actionPerformed(ActionEvent e) { ; 
                       if (showrb == false) showrb = true; 
                       else if (showrb == true) showrb = false; 
                       }
	            });
/////////////////////  REPLAY //////////////////////////////////////////	         		        		
	        JMenu optionsmenu = new JMenu("Replay");
        		replayGroup = new ButtonGroup();
        		
	        	final JMenu roptions = new JMenu("Replay Speed");
	        	/*
	        	if (replayspeed == 0)
		        	roptions.setIcon(new TextIcon("Instant"));
	        	else
	        		roptions.setIcon(new TextIcon(replayspeed+"x"));
	        	*/
	        	option025 = new JRadioButtonMenuItem("0.25x Speed");
        		option025.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, SHORTCUT_MASK));
	        	option025.addActionListener(new ActionListener() {
                       public void actionPerformed(ActionEvent e) { ; 
                            replayspeed = 0.25;
							//option025.removeActionListener(this);
                       }
	            });
	        	
	        	option05 = new JRadioButtonMenuItem("0.5x Speed");
        		option05.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, SHORTCUT_MASK));
	        	option05.addActionListener(new ActionListener() {
                       public void actionPerformed(ActionEvent e) { ; 
                            replayspeed = 0.5;
							//option05.removeActionListener(this);
                       }
	            });
	        	
	        	option075 = new JRadioButtonMenuItem("0.75x Speed");
        		option075.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3, SHORTCUT_MASK));
	        	option075.addActionListener(new ActionListener() {
                       public void actionPerformed(ActionEvent e) { ; 
                            replayspeed = 0.75;
							//option075.removeActionListener(this);
                       }
	            });
	        	
	        	option1 = new JRadioButtonMenuItem("Instant Result");
	        		option1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_0, SHORTCUT_MASK));
		        	option1.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) { ; 
	                            replayspeed = 0;
								//option1.removeActionListener(this);
	                       }
		            });
		        	
		        option2 = new JRadioButtonMenuItem("1x Speed");
		        	option2.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_4, SHORTCUT_MASK));
		        	option2.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) { ; 
	                            replayspeed = 1;			
								//option2.removeActionListener(this);
	                       }
		            });
		        	
	        	option3 = new JRadioButtonMenuItem("2x Speed");
	        		option3.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_5, SHORTCUT_MASK));
	        		option3.addActionListener(new ActionListener() {
                       public void actionPerformed(ActionEvent e) { ; 
                            replayspeed = 2;	
							//option3.removeActionListener(this);
                       }
	            });
	        		
	        	option4 = new JRadioButtonMenuItem("4x Speed");
	        		option4.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_6, SHORTCUT_MASK));
	        		option4.addActionListener(new ActionListener() {
                       public void actionPerformed(ActionEvent e) { ; 
                            replayspeed = 4;
							//option4.removeActionListener(this);
                       }
	            });
	        		
	        	option5 = new JRadioButtonMenuItem("6x Speed");
	        		option5.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_7, SHORTCUT_MASK));
	        		option5.addActionListener(new ActionListener() {
                       public void actionPerformed(ActionEvent e) { ; 
                            replayspeed = 6;
							//option5.removeActionListener(this);
                       }
	            });
	        		
	        	option6 = new JRadioButtonMenuItem("8x Speed");
	        		option6.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_8, SHORTCUT_MASK));
	        		option6.addActionListener(new ActionListener() {
                       public void actionPerformed(ActionEvent e) { ; 
                            replayspeed = 8;	
							//option6.removeActionListener(this);
                       }
	            });
	        		
	        	final JMenu as0 = new JMenu("Annotation Style");
	        	
	        	ButtonGroup aStyleGroup = new ButtonGroup();
	        	
	        	as1 = new JRadioButtonMenuItem("On-Stroke", true);
	        	as1.addActionListener(new ActionListener() {
                       public void actionPerformed(ActionEvent e) { ; 
                            astyle = 1;
							//option1.removeActionListener(this);
                       }
	            });
	        	
	        	as2 = new JRadioButtonMenuItem("Off-Stroke", true);
	        	as2.addActionListener(new ActionListener() {
                       public void actionPerformed(ActionEvent e) { ; 
                            astyle = 2;
							//option1.removeActionListener(this);
                       }
	            });
	        	
	        	asx1 = new JCheckBoxMenuItem("Show Annotation Bubbles", true);
	        	asx1.addActionListener(new ActionListener() {
                       public void actionPerformed(ActionEvent e) { ; 
                       if (showbubble == false) showbubble = true; 
                       else if (showbubble == true) showbubble = false; 
                       }
	            });
	        	
	        	asx2 = new JCheckBoxMenuItem("Show Connecting Lines", true);
	        	asx2.addActionListener(new ActionListener() {
                       public void actionPerformed(ActionEvent e) { ; 
                       if (showline == false) showline = true; 
                       else if (showline == true) showline = false; 
                       }
	            });
	        		
	        	final JMenu ff0 = new JMenu("Annotation Font Face");
	        	ButtonGroup fontFaceGroup = new ButtonGroup();
	        		
	        		final JMenuItem ff1 = new JRadioButtonMenuItem(new TextFaceIcon("Arial"));
	        		ff1.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) { ; 
	                       		afontface = "Arial";
	                       //cs1.removeActionListener(this);
	                       }
		            });
	        		final JMenuItem ff2 = new JRadioButtonMenuItem(new TextFaceIcon("Courier"));
	        		ff2.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) { ; 
	                       		afontface = "Courier";
	                       //cs1.removeActionListener(this);
	                       }
		            });
	        		final JMenuItem ff3 = new JRadioButtonMenuItem(new TextFaceIcon("Tahoma"));
	        		ff3.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) { ; 
	                       		afontface = "Tahoma";
	                       //cs1.removeActionListener(this);
	                       }
		            });
	        		final JMenuItem ff4 = new JRadioButtonMenuItem(new TextFaceIcon("Times New Roman"));
	        		ff4.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) { ; 
	                       		afontface = "Times New Roman";
	                       //cs1.removeActionListener(this);
	                       }
		            });
	        		
        		final JMenu af0 = new JMenu("Annotation Font Size");
	        		ButtonGroup fontSizeGroup = new ButtonGroup();
	        		
	        		final JRadioButtonMenuItem font10 = new JRadioButtonMenuItem(new TextSizeIcon(10));
	        		//font10.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, SHORTCUT_MASK));
	        		font10.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) { ; 
	                            afontsize = 10;
								//option025.removeActionListener(this);
	                       }
		            });
	        		final JRadioButtonMenuItem font11 = new JRadioButtonMenuItem(new TextSizeIcon(11));
	        		//font10.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, SHORTCUT_MASK));
	        		font11.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) { ; 
	                            afontsize = 11;
								//option025.removeActionListener(this);
	                       }
		            });
	        		final JRadioButtonMenuItem font12 = new JRadioButtonMenuItem(new TextSizeIcon(12));
	        		//font10.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, SHORTCUT_MASK));
	        		font12.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) { ; 
	                            afontsize = 12;
								//option025.removeActionListener(this);
	                       }
		            });
	        		final JRadioButtonMenuItem font14 = new JRadioButtonMenuItem(new TextSizeIcon(14));
	        		//font10.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, SHORTCUT_MASK));
	        		font14.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) { ; 
	                            afontsize = 14;
								//option025.removeActionListener(this);
	                       }
		            });
	        		final JRadioButtonMenuItem font16 = new JRadioButtonMenuItem(new TextSizeIcon(16));
	        		//font10.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, SHORTCUT_MASK));
	        		font16.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) { ; 
	                            afontsize = 16;
								//option025.removeActionListener(this);
	                       }
		            });
	        		final JRadioButtonMenuItem font18 = new JRadioButtonMenuItem(new TextSizeIcon(18));
	        		//font10.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, SHORTCUT_MASK));
	        		font18.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) { ; 
	                            afontsize = 18;
								//option025.removeActionListener(this);
	                       }
		            });
	        		final JRadioButtonMenuItem font20 = new JRadioButtonMenuItem(new TextSizeIcon(20));
	        		//font10.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, SHORTCUT_MASK));
	        		font20.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) { ; 
	                            afontsize = 20;
								//option025.removeActionListener(this);
	                       }
		            });
        		
        		final JMenu ac0 = new JMenu("Annotation Colours");
        		
        		final JMenuItem ac1 = new JMenuItem("Font Colour",new ColorIcon(3));
        		ac1.addActionListener(new ActionListener() {
                       public void actionPerformed(ActionEvent e) { ; 
                       af = JColorChooser.showDialog(
                               mainframe,
                               "Choose Annotation Font Color",
                               af);
							//cs1.removeActionListener(this);
                       }
	            });
        		final JMenuItem ac2 = new JMenuItem("Background Colour",new ColorIcon(4));
        		ac2.addActionListener(new ActionListener() {
                       public void actionPerformed(ActionEvent e) { ; 
                       ag = JColorChooser.showDialog(
                               mainframe,
                               "Choose Annotation Background Color",
                               ag);
							//cs2.removeActionListener(this);
                       }
	            });
	        		
        		an1 = new JCheckBoxMenuItem("Show Annotations", true);
	        	an1.addActionListener(new ActionListener() {
                       public void actionPerformed(ActionEvent e) { ; 
                       if (showan == false) showan = true; 
                       else if (showan == true) showan = false; 
                       }
	            });
	        	final JRadioButtonMenuItem rb1 = new JRadioButtonMenuItem("Show Rubberband", true);
	        	rb1.addActionListener(new ActionListener() {
                       public void actionPerformed(ActionEvent e) { ; 
                            showrb = true;
							//rb1.removeActionListener(this);
                       }
							
	            });
	        	final JRadioButtonMenuItem rb2 = new JRadioButtonMenuItem("Hide Rubberband", true);
	        	rb2.addActionListener(new ActionListener() {
                       public void actionPerformed(ActionEvent e) { ; 
                            showrb = false;
							//rb2.removeActionListener(this);
                       }
							
	            });
        		do1 = new JCheckBoxMenuItem("Show Stimulus", true);
		        	do1.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) { ; 
	                       if (!showstim) {
	                    	   showstim = true; 
	                   
	                       }
	                       else if (showstim) {
	                    	   showstim = false; 
	                    	   showstimp = false;
                    		   do11.setSelected(false);
	                       }
	                       }
		        });
		        do11 = new JCheckBoxMenuItem("Show Stimulus permanently", false);
		        	do11.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) { ; 
	                       if (!showstimp) {
	                    	   showstimp = true;
	                    	   showstim = true;
                    		   do1.setSelected(true);
                    		   showim = false;
                    		   do2.setSelected(false);
	                       }
	                       else if (showstimp) {
	                    	   showstimp = false; 
	                       }
	                       }
		        });
	        	do2 = new JCheckBoxMenuItem("Show Input Mask", true);
	        	do2.addActionListener(new ActionListener() {
                       public void actionPerformed(ActionEvent e) { ; 
                       if (showim == false) {
                    	   showim = true; 
                    	   showstimp = false;
                		   do11.setSelected(false);
                       }
                       else if (showim == true) {
                    	   showim = false; 
                       }
                       }
		        });
		        	
		        scOverride = new JCheckBoxMenuItem("Override Stroke Colours (B/W)", false);
		        	scOverride.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) { ; 
	                       if (overrideColours == false) overrideColours = true; 
	                       else if (overrideColours == true) overrideColours = false; 
	                       }
		        });

/////////////////////  HELP/ABOUT //////////////////////////////////////////	  	        	
        JMenu helpmenu = new JMenu("Help");
        JMenuItem manual = new JMenuItem("Manual");
        manual.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, SHORTCUT_MASK));
        manual.addActionListener(new ActionListener() {
                   public void actionPerformed(ActionEvent e) { 
                	   JFrame popup = new JFrame("InteractLog User Manual");
                	   JLabel title = new JLabel(" Coming soon...");
                	   title.setFont(msgFont);
                	   popup.getContentPane().add(title);
                	   
                	   popup.setLayout((new BoxLayout(popup.getContentPane(), BoxLayout.Y_AXIS)));
                	   popup.setPreferredSize(new Dimension(400,300));
                	   Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                	   popup.setLocation(screenSize.width/2-110, screenSize.height/2-170);
                	   popup.pack();
                	   popup.setVisible(true);
                	   ; } 
               }); 
        	JMenuItem about = new JMenuItem("About");
				about.addActionListener(new ActionListener() {
                       public void actionPerformed(ActionEvent e) { 
                    	   JFrame popup = new JFrame("About InteractLog");
                    	   popup.setLayout((new BoxLayout(popup.getContentPane(), BoxLayout.Y_AXIS)));
                    	   JLabel title = new JLabel("InteractLog "+version);
                    	   title.setFont(msgFont);
                    	   JLabel subtitle = new JLabel("Pointer device interaction logger");
                    	   subtitle.setFont(new Font("Description", Font.PLAIN, 14));
                    	   JTextArea descr = new JTextArea(" Ronald Grau"+newline+" r.r.grau@sussex.ac.uk"+newline+" "+versiondate+newline);
                    	   descr.setBackground(contentPane.getBackground());
                    	   descr.setEditable(false);
                    	   descr.setFont(new Font("Description", Font.PLAIN, 12));
                    	   popup.getContentPane().setPreferredSize(new Dimension(330,170));
                    	   popup.getContentPane().add(new JLabel(" "));
                    	   popup.getContentPane().add(title);
                    	   popup.getContentPane().add(subtitle);
                    	   popup.getContentPane().add(new JLabel(" "));
                    	   popup.getContentPane().add(descr);
                    	   Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                    	   popup.setLocation(screenSize.width/2-110, screenSize.height/2-170);
                    	   popup.pack();
                    	   popup.setVisible(true);
                    	   ; } 
                   });  
        
        	
				
		replayGroup.add(option1);
		replayGroup.add(option025);
		replayGroup.add(option05);
		replayGroup.add(option075);
		replayGroup.add(option2);
		replayGroup.add(option3);
		replayGroup.add(option4);
		replayGroup.add(option5);
		replayGroup.add(option6);
		replayGroup.setSelected(option3.getModel(), true);

		aStyleGroup.add(as1);
		aStyleGroup.add(as2);
		aStyleGroup.setSelected(as1.getModel(), true);
		
		fontFaceGroup.add(ff1);
		fontFaceGroup.add(ff2);
		fontFaceGroup.add(ff3);
		fontFaceGroup.add(ff4);
		fontSizeGroup.setSelected(ff1.getModel(), true);
		
		fontSizeGroup.add(font10);
		fontSizeGroup.add(font11);
		fontSizeGroup.add(font12);
		fontSizeGroup.add(font14);
		fontSizeGroup.add(font16);
		fontSizeGroup.add(font18);
		fontSizeGroup.add(font20);
		fontSizeGroup.setSelected(font14.getModel(), true);
		
		//drawGroup1.add(rb1);
		//drawGroup1.add(rb2);
		//drawGroup1.setSelected(rb1.getModel(), true);
		
		drawGroup2.add(sw1);
		drawGroup2.add(sw2);
		drawGroup2.add(sw3);
		drawGroup2.setSelected(sw2.getModel(), true);


        mainWindow.setJMenuBar(mainmenu);
	        mainmenu.add(filemenu);
	        	filemenu.add(open);
	        	filemenu.add(loadConfig);
	        	filemenu.add(saveConfig);
	        	filemenu.add(replayData);
	        	//filemenu.add(openData);
	        	filemenu.add(quit);
//////////////////// temporarily disabled items        	
	        	replayData.setEnabled(false);
	        	
	        mainmenu.add(new JLabel(" | "));
	        mainmenu.add(inputlayeroptionsmenu);
	        inputlayeroptionsmenu.add(il1);
	        inputlayeroptionsmenu.add(il2);
	        
	        mainmenu.add(new JLabel(" | "));
	        mainmenu.add(familiarisationmenu);
        	familiarisationmenu.add(fam1);
        	familiarisationmenu.add(fam2);
        	familiarisationmenu.add(famo1);
        	familiarisationmenu.add(fb1);
        	//familiarisationmenu.add(fb2);
        	
        	mainmenu.add(new JLabel(" | "));
	        mainmenu.add(stimoptionsmenu);
	        	stimoptionsmenu.add(sb1);
	        	stimoptionsmenu.add(sb2);
	        	// stim timer not required (we have familiarisation now) 
	        	//stimoptionsmenu.add(sb3);
	        	stimoptionsmenu.add(sb4);
	        	stimoptionsmenu.add(new JSeparator());
	        	stimoptionsmenu.add(sb5);
	        	stimoptionsmenu.add(sb6);
	        	stimoptionsmenu.add(sb7);
	        	stimoptionsmenu.add(sb8);
	        	
	        mainmenu.add(new JLabel(" | "));
	        mainmenu.add(drawoptionsmenu);
		        drawoptionsmenu.add(ws1);
		        drawoptionsmenu.add(ws2);
		        drawoptionsmenu.add(cs1);
		        drawoptionsmenu.add(cs2);
		        drawoptionsmenu.add(sw0);
		        	sw0.add(sw1);
		        	sw0.add(sw2);
		        	sw0.add(sw3);
		        	sw0.add(sw4);
		        	sw0.add(sw5);
		        	sw0.add(sw6);
		        	sw0.add(sw7);
		        	sw0.add(sw8);
	        	//drawoptionsmenu.add(cs0);
	        	//cs0.add(cs1);
	        	//cs0.add(cs2);
	        	drawoptionsmenu.add(new JSeparator());
	        	drawoptionsmenu.add(rbx1);
	        
	        mainmenu.add(new JLabel(" | "));
	        	mainmenu.add(optionsmenu);
	        	optionsmenu.add(roptions);
	        		roptions.add(option1);
	        		roptions.add(option025);
	        		roptions.add(option05);
	        		roptions.add(option075);
	        		roptions.add(option2);
	        		roptions.add(option3);
	        		roptions.add(option4);
	        		roptions.add(option5);
	        		roptions.add(option6);
	        	optionsmenu.add(as0);
	        		as0.add(as1);
	        		as0.add(as2);
	        		as0.add(new JSeparator());
	        		as0.add(asx1);
	        		as0.add(asx2);
	        	optionsmenu.add(ff0);
	        		ff0.add(ff1);
	        		ff0.add(ff2);
	        		ff0.add(ff3);
	        		ff0.add(ff4);
	        	optionsmenu.add(af0);
	        		af0.add(font10);
	        		af0.add(font11);
	        		af0.add(font12);
	        		af0.add(font14);
	        		af0.add(font16);
	        		af0.add(font18);
	        		af0.add(font20);
	        	optionsmenu.add(ac0);
	        		ac0.add(ac1);
	        		ac0.add(ac2);
	        	optionsmenu.add(new JSeparator());
	        	optionsmenu.add(do1);
	        	optionsmenu.add(do11);
	        	optionsmenu.add(do2);
	        	optionsmenu.add(an1);
	        	optionsmenu.add(scOverride);
	        
	        optionsmenu.setEnabled(false);
	        mainmenu.add(Box.createHorizontalGlue());
	        mainmenu.add(helpmenu);
		        helpmenu.add(manual);	
		        helpmenu.add(about);
		        	
	        	
	}
	private static void loadConfiguration(String filePath) throws Exception {

		  FileInputStream fstream = new FileInputStream(filePath);
		  DataInputStream in = new DataInputStream(fstream);
		  BufferedReader br = new BufferedReader(new InputStreamReader(in));
		  String currentRecord;
		  while((currentRecord = br.readLine()) != null) {
			try {

					// split contents by ":"
				    String[] temp = new String[5];
					String[] line = currentRecord.split(":", 2);
					
					if(line.length > 1) {
					Scanner parseMetaData = new Scanner(line[1]);
					parseMetaData.useDelimiter(",");
					
					temp[0] = line[0].trim();
					//System.out.println(temp[0]);
					if (temp[0].equals("recording pad size")) {
						temp[1] = parseMetaData.next().trim();
						temp[2] = parseMetaData.next().trim();
						frameDimension = new Dimension(Integer.parseInt(temp[1]),Integer.parseInt(temp[2]));
					}
					else if (temp[0].equals("input mask image")) {
						
						if(line[1] != null) {
							inputlayerfilepath = line[1].trim();
							File im =  new File(inputlayerfilepath);
							File[] ims = new File[1];
							ims[0] = im;
							imfile = ims.clone();	
							inputlayerfilename = im.getName();
						}
						else
							inputlayerfilepath = " ";
					}
					else if (temp[0].equals("familiarisation image")) {
						if(line[1] != null) {
							famfilepath = line[1].trim();
							File f =  new File(famfilepath);
							File[] ffs = new File[1];
							ffs[0] = f;
							famfile = ffs.clone();
							famfilename = f.getName();
						}
						else
							famfilepath = " ";
						
					}
					else if (temp[0].equals("stimulus images")) {
						if(line[1] != "") {
							String stims = line[1].trim();
							String[] stimf = stims.split(",");
							stimfilepath = stimf[0];
							File[] files = new File[stimf.length];
							for(int i=0; i < stimf.length; i++) {
								File f = new File(stimf[i]);
								files[i] = f;
							}
							stimfiles = files.clone();
							stimfilename = stimfiles[0].getName();
							SwingUtilities.updateComponentTreeUI(mainmenu); 
						}
						else
							stimfilepath = " ";
						
					}
					else if (temp[0].equals("allow repeated stim display")) {
						if(line[1] != null)
							stimrepeat = Boolean.parseBoolean(line[1].trim());
						else
							stimrepeat = true;
						if(mainmenu != null)
							sb7.setState(stimrepeat);
					}
					else if (temp[0].equals("timer for familiarisation(ms)")) {
						if(line[1] != null)
							famtime =  Integer.parseInt(line[1]);
						else 
							famtime = 0;
					}
					else if (temp[0].equals("delay repeat stim display(ms)")) {
						if(line[1] != null)
							stimdelay =  Integer.parseInt(line[1]);
						else 
							stimdelay = 0;					
					}
					else if (temp[0].equals("hold key to show stimulus")) {
						if(line[1] != null)
							holdkey =  Boolean.parseBoolean(line[1].trim());
						else 
							holdkey = true;	
						if(mainmenu != null)
							sb5.setState(holdkey);
					}
					else if (temp[0].equals("allow key-press to turn off")) {
						if(line[1] != null)
							uifam =  Boolean.parseBoolean(line[1].trim());
						else 
							uifam = true;
						if(mainmenu != null)
							fb1.setState(uifam);
					}
					else if (temp[0].equals("allow drawing over stimulus")) {
						if(line[1] != null)
							stimdraw =  Boolean.parseBoolean(line[1].trim());
						else 
							stimdraw = true;
						if(mainmenu != null)
							sb8.setState(stimdraw);
					}
					else if (temp[0].equals("hide input mask dur stim prog")) {
						if(line[1] != null)
							inputMaskHide =  Boolean.parseBoolean(line[1].trim());
						else 
							inputMaskHide = true;
						if(mainmenu != null)
							sb6.setState(inputMaskHide);
					}
					else if (temp[0].equals("stroke width")) {
						if(line[1] != null)
							strokewidth =  Float.parseFloat(line[1]);
						else 
							strokewidth = 2;
					}
					else if (temp[0].equals("stroke colour")) {
						if(line[1] != null) {
							temp[1] = parseMetaData.next().trim();
							temp[2] = parseMetaData.next().trim();
							temp[3] = parseMetaData.next().trim();
							temp[4] = parseMetaData.next().trim();
							fg = new Color(Integer.parseInt(temp[1]),Integer.parseInt(temp[2]),Integer.parseInt(temp[3]),Integer.parseInt(temp[4]));	
						}
						else
							fg = new Color(255,255,255,255);
						
					}
					else if (temp[0].equals("background colour")) {
						if(line[1] != null) {
							temp[1] = parseMetaData.next().trim();
							temp[2] = parseMetaData.next().trim();
							temp[3] = parseMetaData.next().trim();
							temp[4] = parseMetaData.next().trim();
							bg = new Color(Integer.parseInt(temp[1]),Integer.parseInt(temp[2]),Integer.parseInt(temp[3]),Integer.parseInt(temp[4]));	
						}
						else
							bg = new Color(0,0,0,255);
					}
					else if (temp[0].equals("show rubberband")) {
						if(line[1] != null)
							showrb =  Boolean.parseBoolean(line[1].trim());
						else 
							showrb = true;
						if(mainmenu != null)
							rbx1.setState(showrb);
					}
					else if (temp[0].equals("replay speed")) {
						if(line[1] != null)
							replayspeed =  Double.parseDouble(line[1]);
						//System.out.println(replayspeed);
						if(mainmenu != null) {
							if(replayspeed == 0.25)
								option025.setSelected(true);
							else if(replayspeed == 0.5)
								option05.setSelected(true);
							else if(replayspeed == 0.75)
								option075.setSelected(true);
							else if(replayspeed == 0)
								option1.setSelected(true);
							else if(replayspeed == 1)
								option2.setSelected(true);
							else if(replayspeed == 2)
								option3.setSelected(true);
							else if(replayspeed == 4)
								option4.setSelected(true);
							else if(replayspeed == 6)
								option5.setSelected(true);
							else if(replayspeed == 8) {
								option6.setSelected(true);
							}
								
							else {
								replayspeed = 2;
								option3.setSelected(true);
							}
						}
					}
					else if (temp[0].equals("show annotations")) {
						if(line[1] != null)
							showan =  Boolean.parseBoolean(line[1].trim());
						else 
							showan = true;
						if(mainmenu != null)
							an1.setState(showan);
					}
					else if (temp[0].equals("show stimulus (replay)")) {
						if(line[1] != null)
							showstim =  Boolean.parseBoolean(line[1].trim());
						else 
							showstim = true;
						if(mainmenu != null)
							do1.setState(showstim);
					}
					else if (temp[0].equals("stimulus permanent (replay)")) {
						if(line[1] != null)
							showstimp =  Boolean.parseBoolean(line[1].trim());
						else 
							showstimp = false;
						if(mainmenu != null)
							do11.setState(showstimp);
							do2.setState(!showstimp);
					}
					
					
					else if (temp[0].equals("show input mask (replay)")) {
						if(line[1] != null)
							showim =  Boolean.parseBoolean(line[1].trim());
						else 
							showim = true;
						if(mainmenu != null)
							do2.setState(showim);
					}
					else if (temp[0].equals("annotation style")) {
						if(line[1] != null)
							if(line[1].trim().equals("On-Stroke")) {
								astyle =1;
								if(mainmenu != null)
									as1.setSelected(true);
							}
								
							else if(line[1].trim().equals("Off-Stroke")) {
								astyle =2;
								if(mainmenu != null)
									as2.setSelected(true);
							}	
						else 
							astyle =1;
					}
					else if (temp[0].equals("show annotation bubbles")) {
						if(line[1] != null)
							showbubble =  Boolean.parseBoolean(line[1].trim());
						else 
							showbubble = true;
						if(mainmenu != null)
							asx1.setState(showbubble);
					}
					else if (temp[0].equals("show connecting lines")) {
						if(line[1] != null)
							showline =  Boolean.parseBoolean(line[1].trim());
						else 
							showline = true;
						if(mainmenu != null)
							asx2.setState(showline);
					}
					else if (temp[0].equals("override colours (B/W)")) {
						if(line[1] != null)
							overrideColours =  Boolean.parseBoolean(line[1].trim());
						else 
							overrideColours = false;
						if(mainmenu != null)
							scOverride.setState(overrideColours);
					}
					else if (temp[0].equals("annotation colour")) {
						if(line[1] != null) {
							temp[1] = parseMetaData.next().trim();
							temp[2] = parseMetaData.next().trim();
							temp[3] = parseMetaData.next().trim();
							temp[4] = parseMetaData.next().trim();
							af = new Color(Integer.parseInt(temp[1]),Integer.parseInt(temp[2]),Integer.parseInt(temp[3]),Integer.parseInt(temp[4]));	
						}
						else
							af = new Color(0,0,255,255);
					}
					else if (temp[0].equals("annotation background colour")) {
						if(line[1] != null) {
							temp[1] = parseMetaData.next().trim();
							temp[2] = parseMetaData.next().trim();
							temp[3] = parseMetaData.next().trim();
							temp[4] = parseMetaData.next().trim();
							ag = new Color(Integer.parseInt(temp[1]),Integer.parseInt(temp[2]),Integer.parseInt(temp[3]),Integer.parseInt(temp[4]));	
						}
						else
							ag = new Color(255,255,225,255);
					}
					else if (temp[0].equals("annotation font size")) {
						if(line[1] != null)
							afontsize =  Integer.parseInt(line[1].trim());
						else 
							afontsize = 14;
					}
					else if (temp[0].equals("annotation font face")) {
						if(line[1] != null)
							afontface =  line[1];
						else 
							afontface = "Arial";
					}

					parseMetaData.close();
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}}
		  // close reader
		  br.close();
	}
	
	
	private static Boolean createDefaultConfig(String filePath)
	
	// This method creates a default config file
	
	{		
		String defaultconfigtext = 
		"*********************************************"+newline+
		"*** INTERACT LOG CONFIGURATION" +newline+
		"***"+newline+
		"*** Compatibility Version 2.2+"+newline+
		"***"+newline+
		"*********************************************"+newline+newline+

		"*** GENERAL *********************************"+newline+newline+

		"recording pad size           :1260,600"+newline+newline+

		"*** INPUT MASK ******************************"+newline+newline+

		"input mask image             :"+newline+newline+

		"*** FAMILIARISATION**************************"+newline+newline+

		"familiarisation image        :"+newline+newline+
		
		"timer for familiarisation(ms):0"+newline+
	    "allow key-press to turn off  :true"+newline+newline+
		
		"*** STIMULUS ********************************"+newline+newline+

		"stimulus images              :"+newline+newline+
		
		"delay repeat stim display(ms):0"+newline+
		"hold key to show stimulus    :true"+newline+
		"hide input mask dur stim prog:true"+newline+
		"allow repeated stim display  :true"+newline+
		"allow drawing over stimulus  :false"+newline+newline+

		"*** DRAWING *********************************"+newline+newline+

		"stroke width                 :2"+newline+
		"stroke colour                :0,0,0,255"+newline+
		"background colour            :255,255,255,255"+newline+
		"show rubberband              :true"+newline+newline+

		"*** REPLAY **********************************"+newline+newline+

		"replay speed                 :2"+newline+newline+

		"show annotations             :true"+newline+
		"show stimulus (replay)       :true"+newline+
		"stimulus permanent (replay)  :false"+newline+
		"show input mask (replay)     :true"+newline+newline+

		"annotation style             :On-Stroke"+newline+
		"show annotation bubbles      :true"+newline+
		"show connecting lines        :true"+newline+
		"override colours with B/W    :false"+newline+newline+

		"annotation colour            :0,0,255,255"+newline+
		"annotation background colour :255,255,225,255"+newline+
		"annotation font size         :14"+newline+
		"annotation font face         :Arial";
		
		if(InteractLog.configfilepath != "\\null" || InteractLog.configfilepath != null) {
			try {
				
				BufferedWriter output = new BufferedWriter(new FileWriter(configfilepath));
				output.write(defaultconfigtext);
		    //Close the output stream
		    output.close();
		    return true;
		    }
		    catch (Exception e){//Catch exception if any
		      System.err.println("Error: " + e.getMessage());
		      return false;
		    }
			}
		else {
			System.err.println("Error: Configuration file path or name empty");
				return false;	    
		}
		
	}
	
	private static Boolean saveConfiguration(String filePath)
	
	// This method creates a custom config file
	
	{		
		String annostyle;
		if(astyle == 1) annostyle = "On-Stroke";
		else if (astyle == 2) annostyle = "Off-Stroke";
		else annostyle = "";
		// TODO: stim files printout
		String defaultconfigtext = 
				"*********************************************"+newline+
				"*** INTERACT LOG CONFIGURATION" +newline+
				"***"+newline+
				"*** Compatibility Version 2.2+"+newline+
				"***"+newline+
				"*********************************************"+newline+newline+

				"*** GENERAL *********************************"+newline+newline+

				"recording pad size           :"+(int)frameDimension.getWidth()+","+(int)frameDimension.getHeight()+newline+newline+

				"*** INPUT MASK ******************************"+newline+newline+

				"input mask image             :"+inputlayerfilepath+newline+newline+
				
				"*** FAMILIARISATION**************************"+newline+newline+

				"familiarisation image        :"+famfilepath+newline+newline+
		
				"timer for familiarisation(ms):"+famtime+newline+
				
				"allow key-press to turn off  :"+uifam+newline+newline+

				"*** STIMULUS ********************************"+newline+newline+

				"stimulus images              :";
        	    // plot all defined stim file paths
        		if(!InteractLog.stimfilepath.isEmpty())
        			for (int i = 0; i < InteractLog.stimfiles.length; i++) {
        				defaultconfigtext += InteractLog.stimfiles[i].getPath();
        				// check if not last stimulus image
        				if(i+1 < InteractLog.stimfiles.length)
        					defaultconfigtext+= ",";
        			}
        		defaultconfigtext += newline+newline+
        		"delay repeat stim display(ms):"+stimdelay+newline+
        		"hold key to show stimulus    :"+holdkey+newline+
        		"hide input mask dur stim prog:"+inputMaskHide+newline+
        		"allow repeated stim display  :"+stimrepeat+newline+
        		"allow drawing over stimulus  :"+stimdraw+newline+newline+

				"*** DRAWING *********************************"+newline+newline+

				"stroke width                 :"+strokewidth+newline+
				"stroke colour                :"+fg.getRed()+","+fg.getGreen()+","+fg.getBlue()+","+fg.getAlpha()+newline+
				"background colour            :"+bg.getRed()+","+bg.getGreen()+","+bg.getBlue()+","+bg.getAlpha()+newline+
				"show rubberband              :"+showrb+newline+newline+

				"*** REPLAY **********************************"+newline+newline+

				"replay speed                 :"+(int)replayspeed+newline+newline+

				"show annotations             :"+showan+newline+
				"show stimulus (replay)       :"+showstim+newline+
				"stimulus permanent (replay)  :"+showstimp+newline+
				"show input mask (replay)     :true"+newline+newline+

				"annotation style             :"+annostyle+newline+
				"show annotation bubbles      :"+showbubble+newline+
				"show connecting lines        :"+showline+newline+
				"override colours with B/W    :"+overrideColours+newline+newline+

				"annotation colour            :"+af.getRed()+","+af.getGreen()+","+af.getBlue()+","+af.getAlpha()+newline+
				"annotation background colour :"+ag.getRed()+","+ag.getGreen()+","+ag.getBlue()+","+ag.getAlpha()+newline+
				"annotation font size         :"+afontsize+newline+
				"annotation font face         :"+afontface;
				
			if(InteractLog.configfilepath != "\\null") {
				try {
					if(configfilepath.lastIndexOf(".ini") == -1)
						configfilepath+=".ini";
						
					BufferedWriter output = new BufferedWriter(new FileWriter(configfilepath));
					output.write(defaultconfigtext);
			    //Close the output stream
			    output.close();
			    return true;
			    }
			    catch (Exception e){//Catch exception if any
			      System.err.println("Error: " + e.getMessage());
			      return false;
			    }
			}
			else {
				System.err.println("Error: Configuration file path or name missing");
				return false;	    
			}
	}
	
	private static ArrayList<String[]> readDataFile(String filePath) throws Exception
	
	// This method parses the data file and separates the data sets
	
	{		
	  ArrayList<String[]> data = new ArrayList<String[]>();
	  	  
	  FileInputStream fstream = new FileInputStream(filePath);
	  DataInputStream in = new DataInputStream(fstream);
	  BufferedReader br = new BufferedReader(new InputStreamReader(in));

	  String currentRecord;
	  
	  int lineCount = 0;
	  while((currentRecord = br.readLine()) != null) {
		try {
			String[] temp = new String[5];
			lineCount++;
			// until line 13 - META INFO
			if (lineCount < 13) {
				// split contents by ":"
				
				String[] line = currentRecord.split(":", 2);
				
				Scanner parseMetaData = new Scanner(line[1]);
				parseMetaData.useDelimiter(",");
				
				temp[0] = line[0];
				
				// interpret 
				if ( (temp[0].trim().equals("Stroke Colour") || temp[0].trim().equals("Background Colour") ) && parseMetaData.hasNext()) {
					temp[1] = parseMetaData.next().trim();
					temp[2] = parseMetaData.next().trim();
					temp[3] = parseMetaData.next().trim();
				    temp[4] = parseMetaData.next().trim();
				    data.add(temp);
				}
				else if (temp[0].trim().equals("Drawing Pad Size") && parseMetaData.hasNext()) {
					temp[1] = parseMetaData.next().trim();
					temp[2] = parseMetaData.next().trim();
					temp[3] = "";
				    temp[4] = "";
				    data.add(temp);
				}
				else {
					if(parseMetaData.hasNext()){ 
						temp[1] = parseMetaData.next().trim();
						temp[2] = "";
						temp[3] = "";
					    temp[4] = "";
					    data.add(temp);
					}
					else continue;
				}
				
			    parseMetaData.close();
			}
			else {
				// read data
				Scanner parseData = new Scanner(currentRecord);
				parseData.useDelimiter(";");
			    if ( parseData.hasNext() ){
			    	temp[0] = parseData.next();
				    temp[1] = parseData.next();
				    temp[2] = parseData.next();
				    temp[3] = parseData.next();
				    temp[4] = parseData.next();
				    data.add(temp);
			    }
			    parseData.close();
        
			}
			
			}
			catch (Exception e) {
		  
			}}
	  // close reader
	  br.close();
	  return data;
	}
	private static String[] selectFile (Frame f, String title, String defDir, String fileType) {
		
	// This simple method realises a File Dialog window
	// determines experiment file name and directory
		String[] filedata = new String[2];
		filedata[0] = "";
		filedata[1] = "";
		do {
		  FileDialog fd = new FileDialog(f, title, FileDialog.SAVE);
		  fd.setFile(fileType);
		  fd.setDirectory(defDir);
		  fd.setLocation(50, 50);
		  fd.setVisible(true);
		  
		  filedata[0] = fd.getDirectory();
		  filedata[1] = fd.getFile();
		}
		while (filedata[0].equals("//") && filedata[1] == null);
	  return filedata;
	}
	
	private static String[] loadFile (Frame f, String title, String defDir, String fileType) {
		
		// This simple method realises an Open File Dialog window
		// determines experiment file name and directory
			
		  FileDialog fd = new FileDialog(f, title, FileDialog.LOAD);
		  fd.setFile(fileType);
		  fd.setDirectory(defDir);
		  fd.setLocation(50, 50);
		  fd.setVisible(true);
		  String[] filedata = new String[2];
		  filedata[0] = fd.getDirectory();
		  filedata[1] = fd.getFile();
		  return filedata;
		}
	
	private static class StrokeIcon implements Icon{

	    private int width = 24;
	    private int height = 20;

	    private int stroke;

	    public StrokeIcon(int w) {
	    	this.stroke = w;
	    }
	    public void paintIcon(Component c, Graphics g, int x, int y) {
	        Graphics2D g2d = (Graphics2D) g.create();
	        //g2d.setColor(Color.BLACK);
	        g2d.setColor(bg);
	        g2d.fillRect(x ,y,width,height);
	        g2d.setColor(fg);
	        g2d.setStroke(new BasicStroke(stroke));
	        g2d.drawLine(x+6, (height/2)+2, width-3, (height/2)+2);
	        g2d.dispose();
	    }

	    public int getIconWidth() {
	        return width;
	    }

	    public int getIconHeight() {
	        return height;
	    }

	}
	private static class CurrentStrokeIcon implements Icon{

	    private int width = 24;
	    private int height = 20;

	    public CurrentStrokeIcon() {

	    }
	    public void paintIcon(Component c, Graphics g, int x, int y) {
	        Graphics2D g2d = (Graphics2D) g.create();
	        //g2d.setColor(Color.BLACK);
	        g2d.setColor(bg);
	        g2d.fillRect(x ,y,width,height);
	        g2d.setColor(fg);
	        g2d.setStroke(new BasicStroke(InteractLog.strokewidth));
	        g2d.drawLine(x+6, (height/2)+2, width-3, (height/2)+2);
	        g2d.dispose();
	    }

	    public int getIconWidth() {
	        return width;
	    }

	    public int getIconHeight() {
	        return height;
	    }

	}
	
	private static class TextSizeIcon implements Icon{

	    private int width = 35;
	    private int height = 30;
	    private int fontsize;
	    private int fonty;
	    private int fontx;

	    public TextSizeIcon(int s) {
	    	this.fontsize = s;
	    	this.fontx = 2+((width-fontsize)/2);
	    	this.fonty = fontsize+((height-fontsize)/2);
	    	
	    }
	    public void paintIcon(Component c, Graphics g, int x, int y) {
	        Graphics2D g2d = (Graphics2D) g.create();
	        //g2d.setColor(Color.BLACK);
	        g2d.setColor(ag);
	        g2d.fillRect(x ,y,width,height);
	        g2d.setColor(af);
	        g2d.setFont(new Font(afontface, Font.BOLD, fontsize));
	        // do stuff
	        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	        g2d.drawString(fontsize+"", fontx, fonty);
	        g2d.dispose();
	    }

	    public int getIconWidth() {
	        return width;
	    }

	    public int getIconHeight() {
	        return height;
	    }

	}
	
	private static class TextFaceIcon implements Icon{

	    private int width = 130;
	    private int height = 30;
	    private String fontface;
	    private int fonty;
	    private int fontx;

	    public TextFaceIcon(String f) {
	    	this.fontface = f;
	    	this.fontx = 10;
	    	this.fonty = 22;
	    	
	    }
	    public void paintIcon(Component c, Graphics g, int x, int y) {
	        Graphics2D g2d = (Graphics2D) g.create();
	        //g2d.setColor(Color.BLACK);
	        g2d.setColor(ag);
	        g2d.fillRect(x ,y,width,height);
	        g2d.setColor(af);
	        g2d.setFont(new Font(fontface, Font.BOLD, 14));
	        // do stuff
	        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	        g2d.drawString(fontface, fontx, fonty);
	        g2d.dispose();
	    }

	    public int getIconWidth() {
	        return width;
	    }

	    public int getIconHeight() {
	        return height;
	    }

	}

	private static class ColorIcon implements Icon{

	    private int width = 20;
	    private int height = 20;
	    private int s;

	    public ColorIcon(int s) {
	    	this.s = s;
	    }
	    public void paintIcon(Component c, Graphics g, int x, int y) {
	        Graphics2D g2d = (Graphics2D) g.create();
	        //g2d.setColor(Color.BLACK);
	        g2d.setColor(Color.BLACK);
	        g2d.setStroke(new BasicStroke(1));
	        g2d.drawRect(x ,y,width,height);
	        if (s == 1) g2d.setColor(fg);
	        if (s == 2) g2d.setColor(bg);
	        if (s == 3) g2d.setColor(af);
	        if (s == 4) g2d.setColor(ag);
	        g2d.fillRect(x ,y,width,height);
	        g2d.dispose();
	    }

	    public int getIconWidth() {
	        return width;
	    }

	    public int getIconHeight() {
	        return height;
	    }

	}
	

	private static class StimIcon implements Icon {

	    private int width = 120;
	    private int height = 90;
	    //private double wd;
	    //private double hd;
	    private Image img;
	    private int index;

	    public StimIcon(int i) {
    		this.index = i;
	    }

	    public void paintIcon(Component c, Graphics g, int x, int y) {
	        Graphics2D g2d = (Graphics2D) g.create();
	        if(InteractLog.stimfiles.length > 0) {
	        	img = new ImageIcon(InteractLog.stimfiles[index].getPath()).getImage();
	        	//wd = img.getWidth(c);
	        	//hd = img.getHeight(c);
	        	//height = (int) (width * hd/wd);
		    	g2d.drawImage(img, x,y, width,height,null);
	        }
	        else {
	        	g2d.setFont(new Font("Tahoma", Font.BOLD, 12));
	        	g2d.drawString("No images set", 20, 53);
	        }
	        	
	    	
	        g2d.dispose();
	    }

	    public int getIconWidth() {
	        return width;
	    }

	    public int getIconHeight() {
	        return height;
	    }

	}
	
	private static class FamIcon implements Icon {

	    private int width = 120;
	    private int height = 90;
	    //private double wd;
	    //private double hd;
	    private Image img;

	    public FamIcon() {
	    }
	    

	    public void paintIcon(Component c, Graphics g, int x, int y) {
	        Graphics2D g2d = (Graphics2D) g.create();
	        if(InteractLog.famfilepath != " " && InteractLog.famfile.length > 0) {
	        	img = new ImageIcon(InteractLog.famfilepath).getImage();
		    	g2d.drawImage(img, x,y, width,height,null);
	        }
	        else {
	        	g2d.setFont(new Font("Tahoma", Font.BOLD, 12));
	        	g2d.drawString("No image set", 20, 53);
	        }
	        g2d.dispose();
	    }

	    public int getIconWidth() {
	        return width;
	    }

	    public int getIconHeight() {
	        return height;
	    }

	}

	private static class InputLayerIcon implements Icon {

	    private int width = 120;
	    private int height = 90;
	    //private double wd;
	    //private double hd;
	    private Image img;

	    public InputLayerIcon() {
    		
	    }

	    public void paintIcon(Component c, Graphics g, int x, int y) {
	        Graphics2D g2d = (Graphics2D) g.create();
	        if(InteractLog.inputlayerfilepath != " " && InteractLog.imfile.length > 0) {
	        	img = new ImageIcon(InteractLog.inputlayerfilepath).getImage();
		    	g2d.drawImage(img, x,y, width,height,null);	
	        }
	        else {
	        	g2d.setFont(new Font("Tahoma", Font.BOLD, 12));
	        	g2d.drawString("No image set", 20, 52);
	        }
	        	
	    	g2d.dispose();
	    }

	    public int getIconWidth() {
	        return width;
	    }

	    public int getIconHeight() {
	        return height;
	    }

	}
	
	private static class TimeIcon implements Icon {

	    private int t;
	    private String s;
	    private String s2;
	    private int width=120;
	    private int height=25;

	    public TimeIcon(String s) {
    			this.s = s;
	    }

	    public void paintIcon(Component c, Graphics g, int x, int y) {
	        Graphics2D g2d = (Graphics2D) g.create();
	        g2d.setFont(new Font("Tahoma", Font.BOLD, 12));
    		if(s == "stimtimer") {
    			s2 = "timer";
    			t = InteractLog.stimtime;
    		}
    		else if (s == "famtimer") {
    			s2 = "timer";
    			t = InteractLog.famtime;
    		}
    		else if (s == "delay") {
    			s2 = "delay";
    			t = InteractLog.stimdelay;
    		}
    		
    		if (t > 0)
	        	g2d.drawString(t+" ms", 20, 19);
	        else
	        	g2d.drawString("No "+s2+ " set", 20, 19);
	        
	        g2d.dispose();
	    }
	    public int getIconWidth() {
	        return width;
	    }

	    public int getIconHeight() {
	        return height;
	    }
	}
	
	private static class TextIcon implements Icon {

		private String s;
		private int width=70;
	    private int height=25;

	    public TextIcon(String s) {
    			this.s = s;
	    }

	    public void paintIcon(Component c, Graphics g, int x, int y) {
	        Graphics2D g2d = (Graphics2D) g.create();
	        g2d.setFont(new Font("Tahoma", Font.BOLD, 12));
	        
	        if(s == "width") {
	        	g2d.drawString((int)InteractLog.frameDimension.getWidth()+" px", 20, 19);
    		}
    		else if (s == "height") {
    			g2d.drawString(""+(int)InteractLog.frameDimension.getHeight()+" px", 20, 19);
    		}
    		else
    			g2d.drawString(s, 20, 19);
	        
    		
	        g2d.dispose();
	    }
	    public int getIconWidth() {
	        return width;
	    }

	    public int getIconHeight() {
	        return height;
	    }
	}
	/*
	private static void printTrialInfo() {
		// TODO Auto-generated method stub
		Iterator iter = trials.iterator();
        while(iter.hasNext() ) {
            System.out.println((Trial)iter.next());
        }
	}
	*/


}