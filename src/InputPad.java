/**
 *  This class manages all key press interaction
 *  It also compiles and writes collected pointer device data into a buffer and a data file  
 */

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.Calendar;
import java.text.SimpleDateFormat;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

import java.util.Iterator;


public class InputPad extends JFrame implements KeyListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String datetime = "dd/MM/yyyy - HH:mm:ss";
	SimpleDateFormat filedate;
	private Date expStartDate;
	private Experiment ex;
	private Trial trial;
	private String data = "";
	InputPane drawingPane;
	private String outputFileString;
	//private boolean stimpresent;
	private boolean startKeyReleased;
	private int lastKeyCode;
	private int stimCount = InteractLog.stimfiles.length;
	private int stimIndex;
	
	static final String newline = System.getProperty("line.separator");
	
	public InputPad(final String s, final Trial t, Experiment x, Dimension d) {
		super("InteractLog Recording Pad for "+t.getParticipantID()+", Trial "+t.getTrialnr());
		startKeyReleased = false;
		lastKeyCode = 78; // "n"-key pressed to open the window 
		stimIndex = -1;
		this.ex = x;
		this.trial = t;
		this.expStartDate = InteractLog.now;
		filedate = new SimpleDateFormat ("yyyy-MM-dd - HH-mm-ss");
        outputFileString = InteractLog.filepath+" (Trial "+trial.getTrialnr()+") "+filedate.format(expStartDate)+".txt";
        data =  "Name                 :"+t.getParticipantID()+newline+
        		"Experiment Start     :"+getDate(expStartDate)+newline+
        		"Stroke Colour        :"+InteractLog.fg.getRed()+","+InteractLog.fg.getGreen()+","+InteractLog.fg.getBlue()+","+InteractLog.fg.getAlpha()+newline+
        		"Background Colour    :"+InteractLog.bg.getRed()+","+InteractLog.bg.getGreen()+","+InteractLog.bg.getBlue()+","+InteractLog.bg.getAlpha()+newline+
        		"Stroke Width         :"+InteractLog.strokewidth+newline+
        		"Stimulus Files       :";
        	    // plot all defined stim file paths
        		if(InteractLog.stimfilepath != " " )
        			for (int i = 0; i < InteractLog.stimfiles.length; i++) {
        				data += InteractLog.stimfiles[i].getName();
        				// check if not last stimulus image
        				if(i+1 < InteractLog.stimfiles.length)
        					data+= ",";
        			}
        		data += newline+
        		"Familiarisation File :"+InteractLog.famfilename+newline+
        		"Input Mask File      :"+InteractLog.inputlayerfilename+newline+
        		"Drawing Pad Size     :"+d.width+","+d.height+newline+
        		"Trial Number         :"+trial.getTrialnr()+newline+
				"Trial Start          :"+getDate()+newline;
        this.setResizable(false);
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setBounds((screensize.width-d.width)/2,((screensize.height-d.height)/2)-30,this.getContentPane().getWidth(), this.getContentPane().getHeight());
        //Create and set up the content pane.
        drawingPane = new InputPane(d);
        drawingPane.setOpaque(true); //content panes must be opaque
        this.setContentPane(drawingPane);
		this.setMinimumSize(d);
        this.pack();
     //   display GUI 
        this.setVisible(true);
    }

	public String getDate() {
		Calendar c = Calendar.getInstance();
	    SimpleDateFormat sdf = new SimpleDateFormat(datetime);
	    return sdf.format(c.getTime());
	}
	
	public String getDate(Date d) {
	    SimpleDateFormat sdf = new SimpleDateFormat(datetime);
	    return sdf.format(d);
	}
	
	public void startTimer(int time) {
		drawingPane.startTimer(time);
	}
	public void stopTimer() {
		drawingPane.stopTimer();
	}
	
	public int getStimIndex() {
		return stimIndex;
	}

	public void setStimIndex(int stimIndex) {
		this.stimIndex = stimIndex;
	}

	public void init() {
		// this if/else is necessary to ensure correct key triggering with timed+non-timed stimuli
		// without, non-timed stimuli will not react to initial key presses
		if (InteractLog.famtime != 0) 
			drawingPane.setStimpresent(false);
		else
			drawingPane.setStimpresent(true);
		this.addKeyListener(this);
		
	}
	
	public void keyPressed(KeyEvent e) {
		int kc = e.getKeyCode();
		// check if key already pressed (avoid repeats)
		if(kc == lastKeyCode)
			return;
		else
			lastKeyCode = kc;
		
		/* I don't think this is required
		//if drawing is disallowed during stimulus display, disable mouse listener
		if(!InteractLog.stimdraw & stimpresent) {
			drawingPane.getCanvas().removeMouseListener(drawingPane);
			drawingPane.getCanvas().removeMouseMotionListener(drawingPane);
			//drawingPane.addData("0;0","DrawingDisabled");
		}
		*/
		
		if(isDataSeparator(e.getKeyChar()))
			drawingPane.addData("0;0","KeyPressed_"+kc+"("+InteractLog.data_separator_descr+":"+e.getKeyLocation()+")");
		else if(isReturnKey(kc))
			drawingPane.addData("0;0","KeyPressed_"+kc+"(RETURN"+e.getKeyLocation()+")");
		else if(isRepStimulusKey(kc))
			drawingPane.addData("0;0","KeyPressed_"+kc+"("+InteractLog.repstim_keyname+":"+e.getKeyLocation()+")");
		else if(isNextStimulusKey(kc))
			drawingPane.addData("0;0","KeyPressed_"+kc+"("+InteractLog.nextstim_keyname+":"+e.getKeyLocation()+")");
		else if(isPrevStimulusKey(kc))
			drawingPane.addData("0;0","KeyPressed_"+kc+"("+InteractLog.prevstim_keyname+":"+e.getKeyLocation()+")");
		else if(isHideStimulusKey(kc))
			drawingPane.addData("0;0","KeyPressed_"+kc+"("+InteractLog.hidestim_keyname+":"+e.getKeyLocation()+")");
		else if(isExitKey(kc))
			drawingPane.addData("0;0","KeyPressed_"+kc+"("+InteractLog.endexp_keyname+":"+e.getKeyLocation()+")");
		else
			drawingPane.addData("0;0","KeyPressed_"+kc+"("+e.getKeyChar()+":"+e.getKeyLocation()+")");
		
		//System.out.println(kc+"("+e.getKeyChar()+")"+":"+e.getKeyLocation()+":"+KeyEvent.getKeyText(kc));
    	// if key pressed was ESCAPE, write to file and end trial, if successful
		if(kc == KeyEvent.VK_ESCAPE || (e.isControlDown() && kc == KeyEvent.VK_ENTER)) {
			if(writeFile()) ex.endTrial();
		}
		// up arrow
		else if (kc == KeyEvent.VK_UP && !drawingPane.getStimPresent() && InteractLog.stimfilepath != " " && InteractLog.stimrepeat && !drawingPane.getFirststim()){
			// check if first stim display
			if(stimIndex < 0)
				stimIndex = 0;
			drawingPane.showStim();
		}
		// right arrow 
		else if (kc == KeyEvent.VK_RIGHT && InteractLog.stimfilepath != " " && !drawingPane.getFirststim()){
			// show the next stim if the previous is off, or if rpogression without mask display is on
			if(InteractLog.inputMaskHide || !drawingPane.getStimPresent()) {
				if(stimCount > 1 && stimIndex < InteractLog.stimfiles.length-1) {
					drawingPane.removeStim();
					stimIndex++;
					InteractLog.stimfilepath = InteractLog.stimfiles[stimIndex].getPath();
					InteractLog.stimfilename = InteractLog.stimfiles[stimIndex].getName();
					drawingPane.stimuluslayer.setImg(new ImageIcon(InteractLog.stimfilepath).getImage());
					drawingPane.stimuluslayer.init();
				    drawingPane.showStim();
				}
			}
			else {
				drawingPane.removeStim();
			}
		}
		// left arrow 
		else if (kc == KeyEvent.VK_LEFT && InteractLog.stimfilepath != " " && InteractLog.stimrepeat && !drawingPane.getFirststim()){
			if(InteractLog.inputMaskHide || !drawingPane.getStimPresent()) {
				if(stimCount > 1 && stimIndex <= InteractLog.stimfiles.length-1 && stimIndex > 0) {
					stimIndex--;
					drawingPane.removeStim();
					InteractLog.stimfilepath = InteractLog.stimfiles[stimIndex].getPath();
					InteractLog.stimfilename = InteractLog.stimfiles[stimIndex].getName();
					drawingPane.stimuluslayer.setImg(new ImageIcon(InteractLog.stimfilepath).getImage());
					drawingPane.stimuluslayer.init();
				    drawingPane.showStim();
				}
			}
			else {
				drawingPane.removeStim();
			}
		}
		else {
			// remove fam image if key press enabled
			if (drawingPane.getFirststim() && drawingPane.getStimPresent() && InteractLog.famfilepath != " " && InteractLog.uifam) {
				drawingPane.removeStim();
				//if (InteractLog.famtime != 0) 
				//	drawingPane.stopTimer();
			}
			else {
				drawingPane.removeStim();
			}
		}
		
	}

	public void keyReleased(KeyEvent e) {

		// ignore the first key release (the one that follows the start of the trial)
		if(!startKeyReleased) {
			startKeyReleased = true;
			return;
		}
		
		// reset repeat press identifier
		lastKeyCode = -1;
			
		if(!InteractLog.stimdraw && !drawingPane.getStimPresent()) {
			drawingPane.getCanvas().addMouseListener(drawingPane);
			drawingPane.getCanvas().addMouseMotionListener(drawingPane);
			//drawingPane.addData("0;0","DrawingEnabled");
		}
		
		int kc = e.getKeyCode();
		if(isDataSeparator(e.getKeyChar()))
			drawingPane.addData("0;0","KeyReleased_"+kc+"("+InteractLog.data_separator_descr+":"+e.getKeyLocation()+")");
		else if(isReturnKey(kc))
			drawingPane.addData("0;0","KeyReleased_"+kc+"(RETURN"+e.getKeyLocation()+")");
		else if(isRepStimulusKey(kc))
			drawingPane.addData("0;0","KeyReleased_"+kc+"("+InteractLog.repstim_keyname+":"+e.getKeyLocation()+")");
		else if(isNextStimulusKey(kc))
			drawingPane.addData("0;0","KeyReleased_"+kc+"("+InteractLog.nextstim_keyname+":"+e.getKeyLocation()+")");
		else if(isPrevStimulusKey(kc))
			drawingPane.addData("0;0","KeyReleased_"+kc+"("+InteractLog.prevstim_keyname+":"+e.getKeyLocation()+")");
		else if(isHideStimulusKey(kc))
			drawingPane.addData("0;0","KeyReleased_"+kc+"("+InteractLog.hidestim_keyname+":"+e.getKeyLocation()+")");
		else if(isExitKey(kc))
			drawingPane.addData("0;0","KeyReleased_"+kc+"("+InteractLog.endexp_keyname+":"+e.getKeyLocation()+")");
		else
			drawingPane.addData("0;0","KeyReleased_"+kc+"("+e.getKeyChar()+":"+e.getKeyLocation()+")");
		
		if (InteractLog.holdkey && drawingPane.getStimPresent() && InteractLog.stimfilepath != " " && InteractLog.uistim && InteractLog.stimrepeat && !drawingPane.getFirststim()){
			// doublecheck this statement - not sure it works
			if(!drawingPane.getFirststim() && InteractLog.stimdelay > 0)
				drawingPane.getT().stop();
			// remove current stim when released
			drawingPane.removeStim();
			
		}
		
		
	}

	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub 	}
	}
	
	public JFrame getDisplayArea() {
		return this;
	}
	
	private boolean isReturnKey(int k) {
		if(k == 10)
			return true;
		else
			return false;
		
	}
	
	private boolean isDataSeparator(char c) {
		if(c == InteractLog.data_separator)
			return true;
		else
			return false;
		
	}
	
	private boolean isRepStimulusKey(int kc) {
		if(kc == InteractLog.repstim_keycode)
			return true;
		else
			return false;
		
	}
	
	private boolean isNextStimulusKey(int kc) {
		if(kc == InteractLog.nextstim_keycode)
			return true;
		else
			return false;
		
	}
	
	private boolean isPrevStimulusKey(int kc) {
		if(kc == InteractLog.prevstim_keycode)
			return true;
		else
			return false;
		
	}
	
	private boolean isHideStimulusKey(int kc) {
		if(kc == InteractLog.hidestim_keycode)
			return true;
		else
			return false;
		
	}
	
	private boolean isExitKey(int kc) {
		if(kc == InteractLog.endexp_keycode)
			return true;
		else
			return false;
		
	}
	
	private Boolean writeFile() {
		// This method writes the contents of an ArrayList to a file
//		 Create file 
		data+="Trial End            :"+getDate()+newline+newline;
		data+=drawingPane.getData();
		if(InteractLog.filepath != "\\null") {
		try {
			BufferedWriter output = new BufferedWriter(new FileWriter(outputFileString));
			output.write(data);
	    //Close the output stream
	    output.close();
	    writePreprocessedData();
	    //writeKeyCodeFile();
	    
	    // image copy
	    String imageOutputFileString = outputFileString.substring(0, outputFileString.length()-8)+"img.png";
	    BufferedImage bi = drawingPane.getImage();  // retrieve image
	    File outputfile = new File(imageOutputFileString);
	        ImageIO.write(bi, "png", outputfile);
  	
	    return true;
	    }
	    catch (Exception e){//Catch exception if any
	      System.err.println("Error: " + e.getMessage());
	      return false;
	    }
		}
		else {
			System.err.println("Error: File path or -name missing");
			return false;	    
		}
	}

	private Boolean writePreprocessedData() {
		
		// keyPress buffer: keycode, key character, location, timestamp 
		KeyData keyPressBuffer[] = new KeyData[InteractLog.simultaneousInputs];
		int keyBufferIndex = 0;
		
		try {
			String strokesData = "",keysData = ""; 
			ArrayList<String[]> datafile2 = readDataFile(outputFileString);
			Iterator<String[]> it = datafile2.iterator();
			int strokesLine = 0, keysLine = 0;
			String x1="",y1="";
			while (it.hasNext()) {
				String[] temp = (String[])it.next();
				if (strokesLine < 12) {
					//System.out.println(temp[0]+";"+temp[1]);
					strokesData+= temp[0]+":"+temp[1]+newline;
					keysData+= temp[0]+":"+temp[1]+newline;
					strokesLine++;	
					keysLine++;	
				}
				else if (strokesLine == 12) {
					strokesData+= newline;
					keysData+= newline;
					strokesLine++;	
					keysLine++;	
				}
				else if (temp[4].trim().equals("ButtonDown")) {
					strokesData+= (strokesLine-12)+";"+temp[1];
					x1 = temp[2];
					y1 = temp[3];
				}
				else if (temp[4].trim().equals("ButtonReleased")) {
					strokesData+= ";"+temp[1]+";"+x1+";"+y1+";"+temp[2]+";"+temp[3]+newline;
					strokesLine++;	
				}
				else if (temp[4].trim().substring(0,10).equals("StimulusOn")) {
					; // make sure subsequent substring operations don't trip up on this
				}
				else if (temp[4].trim().substring(0,11).equals("StimulusOff")) {
					; // make sure subsequent substring operations don't trip up on this
				}				
				else if (temp[4].trim().substring(0, 10).equals("KeyPressed")) {
					int kc =  Integer.parseInt(temp[4].trim().substring(11,temp[4].trim().indexOf("(")));
					int cb = temp[4].trim().indexOf(")");
					String c;
					if(cb >= 0)
						c = temp[4].trim().substring(temp[4].trim().indexOf("(")+1,cb);
					else
						c = "N/A";
					
					// add to key buffer
					keyPressBuffer[keyBufferIndex] = new KeyData(keyBufferIndex,kc,c,temp[1]);
					
					for(int i=0; i < InteractLog.simultaneousInputs;i++) {
						// set next index to the first unoccupied position in the array
						if(keyPressBuffer[i] == null)
							keyBufferIndex = i;
					}
					
					//keysData+= (keysLine-11)+";"+"KP"+";"+temp[1]+";"+kc+";"+c+";"+KeyEvent.getKeyText(kc)+newline;
					//keysLine++;	
				}
				else if (temp[4].trim().substring(0, 11).equals("KeyReleased")) {
					int kc =  Integer.parseInt(temp[4].trim().substring(12,temp[4].trim().indexOf("(")));
					int cb = temp[4].trim().indexOf(")");
					String c;
					if(cb >= 0)
						c = temp[4].trim().substring(temp[4].trim().indexOf("(")+1,cb);
					else
						c = "N/A:N/A";
					
					//if(isSpecialKey(kc))
					//	c = getSpecialKeyName(kc);
					
					// create keydata object and compare with key buffer contents
					KeyData kr = new KeyData(-1,kc,c,temp[1]);

					for(KeyData kp : keyPressBuffer) {
						// if found, write data and remove key press from buffer
						if (kp != null) {
						  if(kp.equals(kr)) {
							// event nr; keyCode; keyChar; keyText; keyPressTime; keyReleaseTime; key press duration
							long t1 = kr.getTime();
							long t2 = kp.getTime();
							long kpDuration = t1-t2;
							keysData+= (keysLine-11)+";"+kr.keyCode+";"+kr.keyLoc+";"+kr.keyChar+";"+KeyEvent.getKeyText(kp.keyCode)+";"+kp.timestamp+";"+kr.timestamp+";"+kpDuration+newline;
							keysLine++;	
							// remove
							keyPressBuffer[kp.getIndex()] = null;
					      }
						}
					}

				}
				/* This will create a IOOB exception. the code is not needed, so leave commented out
				else if (temp[4].trim().substring(0,17).equals("FamiliarisationOn")) {
					; // make sure subsequent substring operations don't trip up on this
				}
				else if (temp[4].trim().substring(0,18).equals("FamiliarisationOff")) {
					; // make sure subsequent substring operations don't trip up on this
				}
				*/
				
			}
			outputFileString = InteractLog.filepath+" (Trial "+trial.getTrialnr()+") "+filedate.format(InteractLog.now)+"_strokes.txt";
			BufferedWriter output2 = new BufferedWriter(new FileWriter(outputFileString));
			output2.write(strokesData);
			outputFileString = InteractLog.filepath+" (Trial "+trial.getTrialnr()+") "+filedate.format(InteractLog.now)+"_keys.txt";
			BufferedWriter output3 = new BufferedWriter(new FileWriter(outputFileString));
			output3.write(keysData);
	    //Close the output stream
	    output2.close();
	    output3.close();
	    return true;
	    }
	   catch (Exception e){//Catch exception if any
	     System.err.println("Error processing data: " + e.toString());
	     return false;
	  }
		
	}
	
private static class KeyData {
	
	private int keyIndex;
	private int keyCode;
	private String keyLoc;
	private String keyChar;
	private String timestamp;
	
	public KeyData(int i, int c, String ch, String t) {
		keyIndex = i;
		keyCode = c;
		keyChar = ch.substring(0,ch.indexOf(":"));
		timestamp = t;
		keyLoc = ch.substring(ch.indexOf(":")+1);
	}
	
	public boolean equals(KeyData k) {
		if(this.keyCode == k.keyCode && this.keyChar.equals(k.keyChar))
			return true;
		else
			return false;
	}
	public int getIndex() {
		return this.keyIndex;
	}
	public long getTime() {
		return Long.parseLong((this.timestamp));
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
				data.add(line);
				//Scanner parseMetaData = new Scanner(line[1]);
				//parseMetaData.useDelimiter(",");
				/*
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
				else 
				
				{
					if(parseMetaData.hasNext()){ 
						temp[1] = parseMetaData.next().trim();
						temp[2] = "";
						temp[3] = "";
					    temp[4] = "";
					    data.add(temp);
					}
					//else continue;
				}
				
			    parseMetaData.close();
			    */
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
}
