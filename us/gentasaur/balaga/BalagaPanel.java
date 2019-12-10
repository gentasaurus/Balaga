package us.gentasaur.balaga;

import java.awt.Color; 
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class BalagaPanel extends JPanel
{
	private ExecutorService executor;
	private BalagaLauncher launcher;
	
	private boolean paused;
	private boolean fullscreen;
	
	private GameWorld world;
	private int[] starX;
	private int[] starY;
	private LinkedList<Integer> keys;
	
	private int jitter;
	
	public BalagaPanel(BalagaLauncher launcher)
	{
		fullscreen = launcher.isFullscreen();
		this.launcher = launcher;
		executor = Executors.newFixedThreadPool(2);
		jitter = 0;
		starX = new int[20];
		starY = new int[20];
		keys = new LinkedList<Integer>();
		
		setFocusable(true);
		requestFocusInWindow();
		
		setBackground(Color.BLACK);
		setFont(AssetManager.getFont(getClass(), 24));
		
		addKeyListener(new GameKeyListener(this));
		
		for(int i = 0; i < 20;i++)
		{
			int randX = (int)(Math.random()*(BalagaLauncher.gameWidth+1));
			int randY = (int)(Math.random()*(BalagaLauncher.gameHeight+1));
			starX[i] = randX;
			starY[i] = randY;
		}
		world = new GameWorld(this);
	}
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Graphics2D gg = (Graphics2D) g;
		
		if(fullscreen != launcher.isFullscreen())
			launcher.toggleFullscreen();
		
		if(jitter > 0)
			jitter--;
		AffineTransform transform = new AffineTransform();
		if(launcher.isFullscreen())
			transform.scale(launcher.getScaleFactor(), launcher.getScaleFactor());
		
		int xShift = (Math.random() > .5) ? 1 : -1;
		int yShift = (Math.random() > .5) ? 1 : -1;
		transform.translate((jitter)*xShift, (jitter)*yShift);
		gg.setTransform(transform);
		//Draw Stars
		gg.setColor(Color.WHITE);
		for(int x = 0;x < 20;x++)
			gg.fillRect(starX[x], starY[x], 3, 3);
		
		//Draw Game
		world.paint(gg);
		
		if(paused)
		{
			gg.setColor(new Color(0, 0, 0, 150));
			gg.fillRect(-40, -40, this.getWidth()+80, this.getHeight()+80);
			gg.setColor(Color.WHITE);
			if(world.isGameOver())
				gg.drawString("GAME OVER", 450, BalagaLauncher.gameHeight/2);
			else
				gg.drawString("PAUSED", 492, BalagaLauncher.gameHeight/2);
		}
		
		executor.execute(new GameLoop(this));
		executor.execute(new RenderLoop(this));
	}

	public void updateGame() {
		if(launcher.isFullscreen() != fullscreen)
			launcher.toggleFullscreen();
		if(!paused)
			world.update(keys);
		if(world.isGameOver())
			paused = true;
	}

	public void setJitter(int amount) {
		jitter = amount;
	}
	
	public void resetWorld()
	{
		paused = false;
		world = new GameWorld(this);
	}

	public double getTimeScale() {
		double timeScale = 1;
		return timeScale;
	}
	
	public BalagaLauncher getLauncher()
	{
		return launcher;
	}
	
	public void togglePause()
	{
		paused = !paused;
	}
	
	public void toggleDev()
	{
		Settings.DEV = !Settings.DEV;
	}
	
	public void toggleFullscreen()
	{
		fullscreen = !fullscreen;
	}
	
	public LinkedList<Integer> getKeys()
	{
		return keys;
	}
	
	public GameWorld getWorld()
	{
		return world;
	}
}
