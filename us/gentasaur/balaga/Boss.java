package us.gentasaur.balaga;

import java.awt.Graphics2D;

public abstract class Boss {

	private int y;
	private int height;
	private GameWorld world;
	private int health;
	private int shield;
	private int maxHealth;
	private int waitTicks;
	private int cooldown;
	private int deathTicks;
	
	public Boss(GameWorld world, int h, boolean s, int height, int c)
	{
		this.world = world;
		maxHealth = h*world.getShips().size();
		health = maxHealth;
		shield = s ? maxHealth : 0;
		this.height = height;
		y = -height;
		deathTicks = 0;
		waitTicks = c;
		cooldown = c;
	}

	public int getMaxHealth()
	{
		return maxHealth;
	}
	
	public int getHealth()
	{
		return health;
	}
	
	public int getShield()
	{
		return shield;
	}
	
	protected int getY()
	{
		return y;
	}
	
	public void hurt(int amount)
	{
		if(y < 0 || health <= 0)
			return;
		
		if(shield > 0)
			shield -= amount;
		else
		{
			world.jolt(5);
			AssetManager.playSound(getClass(), "breakNormal.wav");
			health -= amount;
		}
		
		if(health <= 0)
		{
			deathTicks = height;
			world.jolt(50);
			AssetManager.playSound(getClass(), "breakHuge.wav");
		}
	}
	
	public void update()
	{
		if(y < 0 && deathTicks == 0)
			y++;
		else if(deathTicks > 0)
		{
			deathTicks--;
			y--;
			if(deathTicks == 0)
				die();
			return;
		}
		
		if(waitTicks > 0)
		{
			waitTicks--;
			return;
		}
		
		//ATTACK
		int chosenAttack = (int)(Math.random()*3);
		if(chosenAttack == 0)
			attackA();
		else if(chosenAttack == 1)
			attackB();
		else
			attackC();
		waitTicks = cooldown;
	}
	
	public void paint(Graphics2D gg)
	{}
	
	protected void die() {
		world.score(100);
		world.setBoss(null);
		world.getAsteroids().clear();
		world.transition();
	}

	public void holdFor(int time)
	{
		waitTicks = time;
	}
	
	public abstract void attackA();
	public abstract void attackB();
	public abstract void attackC();
	
	protected GameWorld getWorld()
	{
		return world;
	}
	
	protected int getDeathTicks()
	{
		return deathTicks;
	}
}
