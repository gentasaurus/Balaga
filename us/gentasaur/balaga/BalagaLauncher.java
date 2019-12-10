package us.gentasaur.balaga;

import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import javax.swing.JFrame;

public class BalagaLauncher {

	public static final int gameHeight = 631;
	public static final int gameWidth = (int)(gameHeight * (16.0/9));
	private double scaleFactor;
	
	private boolean fullscreen;
	private JFrame frame;
	private BalagaPanel panel;
	
	public static void main(String[] args)
	{
		new BalagaLauncher();
	}
	
	public BalagaLauncher()
	{
		GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		scaleFactor = device.getDisplayMode().getHeight() / (gameHeight * 1.0);
		fullscreen = true;
		panel = new BalagaPanel(this);
		generateFrame();
	}
	
	public void toggleFullscreen()
	{
		fullscreen = !fullscreen;
		frame.dispose();
		generateFrame();
	}
	
	private void generateFrame()
	{
		frame = new JFrame("Balaga");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(panel);
		if(fullscreen)
		{
			frame.setUndecorated(true);
			GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
			frame.setSize(device.getDisplayMode().getWidth(), device.getDisplayMode().getHeight());
			panel.setPreferredSize(new Dimension(device.getDisplayMode().getWidth(), device.getDisplayMode().getHeight()));
		}
		else
		{
			panel.setPreferredSize(new Dimension(gameWidth, gameHeight));
			frame.setUndecorated(false);
			frame.setSize(gameWidth, gameHeight);
			frame.setLocationRelativeTo(null);
		}
		frame.setResizable(false);
		frame.setVisible(true);
		frame.pack();
	}
	
	public double getScaleFactor()
	{
		return scaleFactor;
	}
	
	public boolean isFullscreen()
	{
		return fullscreen;
	}
}
