import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;


public final class Experiment {
	private Font msgFont = new Font("Message", Font.PLAIN, 24);
	private Font msgFontsmall = new Font("Message", Font.PLAIN, 18);
	private Trial currenttrial;
	InputPad trialpane;
	
	public Experiment(Trial t) {
		super();
		currenttrial = t;
		startNextTrial(t);
	}
	
	public void endTrial() {
		JLabel l1 = new JLabel("The experiment is finished, thanks for taking part.");
		JLabel l2 = new JLabel("You can now close this program or");
		JLabel l3 = new JLabel("press 'N' to continue with Trial "+(currenttrial.getTrialnr()+1));
		l1.setAlignmentX(Component.CENTER_ALIGNMENT);
		l2.setAlignmentX(Component.CENTER_ALIGNMENT);
		l3.setAlignmentX(Component.CENTER_ALIGNMENT);
		l1.setFont(msgFont);
		l2.setFont(msgFontsmall);
		l3.setFont(msgFontsmall);
		InteractLog.contentPane.add(new JLabel(" "));
		InteractLog.contentPane.add(l1);
		InteractLog.contentPane.add(new JLabel(" "));
		InteractLog.contentPane.add(l2);
		InteractLog.contentPane.add(l3);
		InteractLog.contentPane.revalidate();
		InteractLog.contentPane.repaint();
		
		// Close the mouse input pad
		trialpane.dispose();
		
		InteractLog.contentPane.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) 	{
				if(e.getKeyCode() == KeyEvent.VK_N) {
				InteractLog.contentPane.removeAll();
				InteractLog.contentPane.repaint();
			    	//contentPane.removeKeyListener(this);
					//contentPane.setFocusable(false);
				currenttrial.setTrialnr((currenttrial.getTrialnr()+1));
				startNextTrial(currenttrial);
				InteractLog.contentPane.removeKeyListener(this);
				}
				
			}
			});
		
	}
	
	public void startNextTrial(Trial t) {
		// reset stim sequence
		if (InteractLog.stimfiles.length > 0)
			InteractLog.stimfilepath = InteractLog.stimfiles[0].getPath();
		// check relevant image sizes and adjust the pad size in case the images are bigger than the frameDimension 
		Image padimg1 = new ImageIcon(InteractLog.stimfilepath).getImage();
		Image padimg2 = new ImageIcon(InteractLog.inputlayerfilepath).getImage();
		int padw,padh,refWidth,refHeight;
		if (padimg1.getWidth(null) >= padimg2.getWidth(null))
			refWidth = padimg1.getWidth(null);
		else
			refWidth = padimg2.getWidth(null);
		if (padimg1.getHeight(null) >= padimg2.getHeight(null))
			refHeight = padimg1.getHeight(null);
		else
			refHeight = padimg2.getHeight(null);
		
		if (refWidth > InteractLog.frameDimension.getWidth()) 
			padw = refWidth; 
		else 
			padw = (int)InteractLog.frameDimension.getWidth();
		if (refHeight > InteractLog.frameDimension.getHeight()) 
			padh = refHeight; 
		else 
			padh = (int)InteractLog.frameDimension.getHeight();
		Dimension paddim = new Dimension(padw,padh);
		InteractLog.contentPane.removeAll(); // clear window
	    trialpane = new InputPad("input", t, this, paddim);
    	trialpane.init();
	    if (InteractLog.famtime != 0) {
	    	trialpane.startTimer(InteractLog.famtime);
	    }
	}
}