package us.gentasaur.balaga;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GameKeyListener extends KeyAdapter {

	private BalagaPanel panel;
	
	public GameKeyListener(BalagaPanel panel)
	{
		this.panel = panel;
	}
	
	public void keyPressed(KeyEvent e)
	{
		if(e.getKeyCode() == 113)
			panel.toggleFullscreen();
		else if(e.getKeyChar() == '`')
			panel.toggleDev();
		else if(e.getKeyCode() == 112 && panel.getWorld().isGameOver())
			panel.resetWorld();
		else if(e.getKeyCode() == 27 && !panel.getWorld().isGameOver())
			panel.togglePause();
		else
		{
			if(!panel.getKeys().contains(e.getKeyCode()))
				panel.getKeys().add(e.getKeyCode());
		}
	}
	
	public void keyReleased(KeyEvent e)
	{
		panel.getKeys().remove((Integer)e.getKeyCode());
	}
}
