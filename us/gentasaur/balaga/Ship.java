package us.gentasaur.balaga;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;

public class Ship implements Targetable {

	private int x;
	private int y;
	private int cooldown;
	private GameWorld world;
	private Hitbox hitbox;
	private double health;
	private int shieldHealth;
	private PowerUp powerUp;
	
	private int moveLeft;
	private int moveRight;
	private int shoot;
	
	private int number;
	private String model;
	private int animTicks;
	private int powerTicks;
	private int shockTicks;
	
	public Ship(GameWorld world, int number, String model, int lKey, int rKey, int sKey)
	{
		moveLeft = lKey;
		moveRight = rKey;
		shoot = sKey;
		this.model = model;
		this.number = number;
		this.world = world;
		health = getMaxHealth();
		shieldHealth = getMaxShield();
		powerUp = null;
		animTicks = 0;
		powerTicks = 0;
		shockTicks = 0;
		x = -80;
		y = BalagaLauncher.gameHeight-85;
		cooldown = 0;
	}

	public void paint(Graphics2D gg)
	{
		gg.setColor(Color.BLACK);
		gg.drawRect(x, y - 20, 80, 5);
		gg.setColor(Color.GRAY);
		gg.fillRect(x, y - 20, 80, 5);
		
		if(health > 0)
		{
			boolean secondBar = powerUp != null && !powerUp.getID().equals("heal");
			if(secondBar)
			{
				gg.setColor(Color.BLACK);
				gg.drawRect(x, y - 30, 80, 5);
				gg.setColor(Color.GRAY);
				gg.fillRect(x, y - 30, 80, 5);
			}
			double greenAmount = health*(80/getMaxHealth());
			double blueAmount = shieldHealth * (80/3.0);
			gg.setColor(Color.GREEN);
			gg.fillRect(x, secondBar ? y - 30 : y - 20, (int)greenAmount, 5);
			gg.setColor(Color.decode("#6699ff"));
			gg.fillRect(x, secondBar ? y - 30 : y - 20, (int)blueAmount, 5);
			if(powerUp != null && powerUp.getID().equals("heal"))
			{
				gg.setColor(Color.PINK);
				double healFactor = powerUp.getLevel()/60.0;
				double pinkAmount = (powerTicks*healFactor)*(80/getMaxHealth());
				if(pinkAmount + greenAmount > 80)
					pinkAmount = 80 - greenAmount;
				gg.fillRect((int)(x + greenAmount), y - 20, (int)pinkAmount, 5);
			}
			else if(powerUp != null)
			{
				gg.setColor(powerUp.getColor());
				gg.fillRect(x, y - 20, (int)(((powerUp == PowerUp.BEAM ? (600 - powerTicks) : powerTicks) / 600.0)*80), 5);
			}
			boolean turbo = (animTicks > 60) || (world.isTransitioning());
			gg.setColor(Color.BLACK);
			gg.drawRect(x, y - 20, 80, 5);
			if(secondBar) gg.drawRect(x, y - 30, 80, 5);
			gg.drawImage(AssetManager.loadImage(this.getClass(), "ship" + model + (turbo ? "Turbo" : "") + ".png"), x, y, 80, 80, null);
			if(shockTicks > 0)
			{
				if(shockTicks >= 200)
					gg.drawImage(AssetManager.loadImage(getClass(), "shocked1.png"), x, y, 80, 80, null, null);
				else if(shockTicks >= 100)
					gg.drawImage(AssetManager.loadImage(getClass(), "shocked2.png"), x, y, 80, 80, null, null);
				else
					gg.drawImage(AssetManager.loadImage(getClass(), "shocked3.png"), x, y, 80, 80, null, null);
			}
			
			if(shieldHealth > 0)
			{
				gg.setColor(new Color(102, 153, 255, 70*shieldHealth));
				gg.fillOval(x - 10, y - 10, 100, 170);
				gg.setColor(Color.decode("#6699ff"));
				gg.drawOval(x - 10, y - 10, 100, 170);
			}
			
		}
		else
			gg.drawImage(AssetManager.loadImage(this.getClass(), "ship" + model + "Dead.png"), x, y, 80, 80, null);
			
		hitbox.paint(gg);
	}
	
	public void update(List<Integer> keys)
	{
		if(health <= 0)
		{
			shockTicks = 0;
			powerTicks = 0;
			return;
		}
		
		if(powerUp == PowerUp.HEAL_1)
			health += (1.0/60);
		
		if(powerUp == PowerUp.HEAL_2)
			health += (1.0/30);
		
		if(powerUp == PowerUp.HEAL_3)
			health += (1.0/15);
		
		if(health > getMaxHealth())
			health = getMaxHealth();
		
		animTicks++;
		if(animTicks >= 120)
			animTicks -= 120;
		
		if(cooldown > 0)
			cooldown--;
		
		if(powerTicks > 0)
			powerTicks--;
		
		if(shockTicks > 0)
		{
			AssetManager.playSound(getClass(), "shocked.wav");
			shockTicks--;
		}
		
		if(powerUp == PowerUp.BEAM && powerTicks == 1)
		{
			powerTicks--;
			world.addEntity(new VBeam(world, this.getCenter().getX()));
			AssetManager.playSound(getClass(), "beamDown.wav");
			shockTicks = 300;
		}
		
		if(powerTicks == 0 && powerUp != null)
			powerUp = null;
			
		
		if(shockTicks == 0)
		{
			if(keys.contains(moveLeft) != keys.contains(moveRight))
			{
				if(keys.contains(moveLeft))
					move(-10);
				else
					move(10);
			}
			
			if(keys.contains(shoot) && cooldown == 0)
			{
				cooldown = getFireRate();
				world.createBullet(x+34, y, this);
				AssetManager.playSound(getClass(), "pew" + (powerUp == PowerUp.HEAVY ? "H" : number) + ".wav");
			}
		}
		
		for(int i = 0; i < world.getAsteroids().size(); i++)
		{
			Hitbox hb = world.getAsteroids().get(i).getHitbox();
			if(hb.touching(hitbox))
			{
				if(shieldHealth == 0) {
					world.getAsteroids().get(i).dieFromCrash();
					world.jolt(30);
				}
				else
					world.getAsteroids().get(i).die();
				
				hurt(5);
				
				if(world.isGameOver())
					AssetManager.playSound(getClass(), "gameOver.wav");
			}
		}
		
		for(int i = 0; i < world.getBullets().size(); i++)
		{
			if(!world.getBullets().get(i).isFriendly())
			{
				Hitbox hb = world.getBullets().get(i).getHitbox();
				if(hb.touching(hitbox))
				{
					if(shieldHealth == 0)
					{
						AssetManager.playSound(getClass(), "hit.wav");
						world.jolt(5);
					}
					
					world.getBullets().get(i).die();
					hurt(2);
					
					if(world.isGameOver())
						AssetManager.playSound(getClass(), "gameOver.wav");
				}
			}
		}
	}
	
	private int getFireRate()
	{
		int base = world.getShipBaseCooldown();
		if(powerUp == PowerUp.RAPIDFIRE)
			base -= 8;
		return base;
	}

	public void move(int amount)
	{
		if(x + amount > 10 && x + amount < BalagaLauncher.gameWidth-100)
			x += amount;
		updateHitbox();
	}

	private void updateHitbox() {
		Point[] points = new Point[]{
				new Point(x + (shieldHealth > 0 ? -10 : 15), y + (shieldHealth > 0 ? -10 : 0)),
				new Point(x + (shieldHealth > 0 ? -10 : 15), y + (shieldHealth > 0 ? 85 : 65)),
				new Point(x + (shieldHealth > 0 ? 90 : 65), y + (shieldHealth > 0 ? 85 : 65)),
				new Point(x + (shieldHealth > 0 ? 90 : 65), y + (shieldHealth > 0 ? -10 : 0))};
		hitbox = new Hitbox(x + 35, y + 32, points, 0);
	}
	
	public void hurt(int amount)
	{
		if(shieldHealth > 0)
		{
			AssetManager.playSound(getClass(), "shield.wav");
			shieldHealth--;
			return;
		}
		
		if(!Settings.DEV)
			health -= amount;
	}

	public PowerUp getPowerUp() {
		return powerUp;
	}

	public void setPowerUp(PowerUp powerUp, int seconds) {
		
		if(this.powerUp != PowerUp.BEAM)
		{
			this.powerUp = powerUp;
			if(powerUp == PowerUp.BEAM)
				AssetManager.playSound(getClass(), "beamUp.wav");
			powerTicks = (seconds*60) / (powerUp == PowerUp.ENERGY ? 3 : 1);
		}
	}
	
	public boolean isDead()
	{
		return (health <= 0);
	}
	
	public Hitbox getHitbox()
	{
		return hitbox;
	}
	
	public double getMaxHealth()
	{
		return world.getShipBaseHealth();
	}
	
	public int getMaxShield()
	{
		return world.getShipBaseShield();
	}

	public void moveToStartPos() {
		
		x = (number == 1) ? ((BalagaLauncher.gameWidth/2)-120) : ((BalagaLauncher.gameWidth/2)+40);
		
		/* DEPRECATED FOR RETRO VERSION
		if(world.getShips().size() == 1)
			x = (BalagaLauncher.gameWidth/2)-40;
		else if(world.getShips().size() == 2)
			x = (number == 1) ? ((BalagaLauncher.gameWidth/2)-120) : ((BalagaLauncher.gameWidth/2)+40);
		else if(world.getShips().size() == 3)
		{
			if(number == 1)
				x = (BalagaLauncher.gameWidth/2)-200;
			else if(number == 2)
				x = (BalagaLauncher.gameWidth/2)-40;
			else
				x = (BalagaLauncher.gameWidth/2)+120;
		}
		else
		{
			if(number == 1)
				x = (BalagaLauncher.gameWidth/2)-280;
			else if(number == 2)
				x = (BalagaLauncher.gameWidth/2)-120;
			else if(number == 3)
				x = (BalagaLauncher.gameWidth/2)+40;
			else
				x = (BalagaLauncher.gameWidth/2)+200;
		}
		*/
		
		updateHitbox();
	}
	
	public void heal(double amount) {
		health += amount;
	}

	public void revive() {
		health = getMaxHealth()/2;
	}
	
	public void recover() {
		health = getMaxHealth();
	}

	@Override
	public Point getCenter() {
		return new Point(x + 40, y + 40);
	}
	
	public void shock(int time) {
		shockTicks = time;
	}
	
	public void shield() {
		shieldHealth = getMaxShield();
	}
}
