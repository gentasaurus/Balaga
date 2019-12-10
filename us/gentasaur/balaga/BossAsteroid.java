package us.gentasaur.balaga;

import java.awt.Graphics2D;
import java.util.Random;

public class BossAsteroid extends Boss
{
	private Random random;
	
	public BossAsteroid(GameWorld world, int h, boolean s, int height) {
		super(world, h, s, height, 300);
		random = new Random();
	}
	
	public void paint(Graphics2D gg)
	{
		gg.drawImage(AssetManager.loadImage(getClass(), "bossAsteroid.png"), 0, getY()-200, 1121, 400, null);
	}
	
	public void attackA()
	{
		for(int i = 0;i < 8;i++)
			getWorld().spawnAsteroid(40, 5, 1, (random.nextDouble() <= .1));
	}
	
	public void attackB()
	{
		for(int i = 0;i < 3;i++)
			getWorld().spawnAsteroid(100, 20, .5, false);
	}
	
	public void attackC()
	{
		for(int i = 0;i < 30;i++)
			getWorld().spawnAsteroid(15, 1, 3, false);
	}

}
