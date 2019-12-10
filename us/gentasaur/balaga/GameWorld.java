package us.gentasaur.balaga;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameWorld {
	
	public static final int asteroidBaseAmount = 5;
	
	private BalagaPanel panel;
	private Random random;

	private List<Ship> ships;
	private List<Bullet> bullets;
	private List<Asteroid> asteroids;
	private List<Entity> entities;
	
	private int planet;
	private int planetHealth;
	private Boss boss;
	
	private int round;
	private int score;
	private int asteroidsSpawned;
	
	private double shipBaseHealth;
	private int shipBaseCooldown;
	private int shipBaseShield;
	private double oldBaseHealth;
	private int oldBaseCooldown;
	private int oldBaseShield;
	
	private int ticksSinceStart;
	private int transitionTicks;
	private int asteroidTicks;
	
	public GameWorld(BalagaPanel panel)
	{
		this.panel = panel;
		random = new Random();
		
		round = 1;
		score = 0;
		asteroidsSpawned = 0;
		asteroidTicks = 0;
		
		shipBaseHealth = 20;
		shipBaseCooldown = 20;
		shipBaseShield = 0;
		
		ticksSinceStart = 180;
		transitionTicks = 0;
		
		ships = new ArrayList<Ship>();
		ships.add(new Ship(this, 1, "Standard", 65, 68, 87));
		//ships.add(new Ship(this, 2, "Bomber", 74, 76, 73));
		for(int i = 0;i < ships.size();i++)
			ships.get(i).moveToStartPos();
		
		bullets = new ArrayList<Bullet>();
		asteroids = new ArrayList<Asteroid>();
		entities = new ArrayList<Entity>();
		
		resetPlanet(100);
	}
	
	private void resetPlanet(int h)
	{
		planetHealth = h;
		if(round != 4 && round != 8 && round != 12)
			planet = round;
		else
		{
			planet = 0;
			if(round == 4)
				setBoss(new BossAsteroid(this, 200, false, 200));
		}
	}

	public void paint(Graphics2D gg)
	{
		if(boss != null)
			boss.paint(gg);
		
		if(hasPlanet())
			gg.drawImage(AssetManager.loadImage(getClass(), "planet" + planet + ".png"), 0, BalagaLauncher.gameHeight - 140 + getPlanetShift(), 1121, 200, null);
		
		for(int x = 0;x < bullets.size();x++)
			bullets.get(x).paint(gg);
		for(int x = 0;x < entities.size();x++)
			entities.get(x).paint(gg);
		for(int x = ships.size()-1;x >= 0;x--)
		{
			if(ships.get(x).isDead())
				ships.get(x).paint(gg);
		}
		for(int x = ships.size()-1;x >= 0;x--)
		{
			if(!ships.get(x).isDead())
				ships.get(x).paint(gg);
		}
		for(int x = 0;x < asteroids.size();x++)
			asteroids.get(x).paint(gg);
		
		gg.setColor(Color.GRAY);
		gg.fillRect(110, 15, 900, 10);
		if(hasPlanet() && planetHealth > 0)
		{
			gg.setColor(Color.GREEN);
			gg.fillRect(110, 15, planetHealth*9, 10);
		}
		
		if(boss != null && boss.getHealth() > 0)
		{
			gg.setColor(Color.RED);
			gg.fillRect(110, 15, (int)(boss.getHealth()*(900.0/boss.getMaxHealth())), 10);
			if(boss.getShield() > 0)
			{
				gg.setColor(new Color(0, 255, 255, 220));
				gg.fillRect(110, 15, (int)(boss.getShield()*(900.0/boss.getMaxHealth())), 10);
			}
		}
		
		gg.setColor(Color.CYAN);
		gg.drawString(score + "", 110, 60);
		
		gg.setColor(Color.WHITE);
		
		if(round != 4 && round != 8 && round != 12)
		{
			if(transitionTicks == 0)
				gg.drawString("ASTEROIDS LEFT:" + getRemainingAsteroids(), 275, 60);
			else if(transitionTicks > 100)
				gg.drawString("ASTEROIDS LEFT:0", 275, 60);;
		}
		
		int offset = 27;
		gg.drawImage(AssetManager.loadImage(getClass(), "health.png"), 735 + offset, 34, 25, 25, null);
		gg.drawString((int)shipBaseHealth + "", 763 + offset, 60);
		gg.drawImage(AssetManager.loadImage(getClass(), "cooldown.png"), 830 + offset, 34, 32, 25, null);
		gg.drawString(shipBaseCooldown + "", 865 + offset, 60);
		gg.drawImage(AssetManager.loadImage(getClass(), "shield.png"), 932 + offset, 34, 25, 25, null);
		gg.drawString(shipBaseShield + "", 960 + offset, 60);
		
		if(ticksSinceStart > 0)
			gg.drawString("ROUND " + round, 480, BalagaLauncher.gameHeight/2);
		else if(transitionTicks <= 200 && transitionTicks > 100)
			gg.drawString("END OF ROUND " + (round - 1), 390, BalagaLauncher.gameHeight/2);
		else if(transitionTicks == 100)
		{
			int titleHeight = BalagaLauncher.gameHeight/2 - 82;
			int baseX = 475;
			gg.drawString("UPGRADES", 467, titleHeight - 20);
			//
			gg.setColor(Color.WHITE);
			gg.drawImage(AssetManager.loadImage(getClass(), "health.png"), baseX - 60, titleHeight - 10, 50, 50, null);
			gg.drawString((int)shipBaseHealth + "", baseX, titleHeight + 30);
			gg.drawString("->", baseX + 63, titleHeight + 30);
			gg.drawString(shipBaseHealth != 50 ? ((int)(shipBaseHealth + 5) + "") : "50", baseX + 130, titleHeight + 30);
			gg.setColor(shipBaseHealth < 50 ? Color.CYAN : Color.GRAY);
			if(shipBaseHealth < 50)
				gg.drawString(Settings.CONTROLLERS ? "[A]" : "[1]", baseX + 200, titleHeight + 30);
			else
				gg.drawString("[MAX]", baseX + 200, titleHeight + 30);
			//
			gg.setColor(Color.WHITE);
			gg.drawImage(AssetManager.loadImage(getClass(), "cooldown.png"), baseX - 70, titleHeight + 50, 64, 50, null);
			gg.drawString((int)shipBaseCooldown + "", baseX, titleHeight + 90);
			gg.drawString("->", baseX + 63, titleHeight + 90);
			gg.drawString(shipBaseCooldown != 14 ? ((int)(shipBaseCooldown - 1) + "") : "14", baseX + 130, titleHeight + 90);
			gg.setColor(shipBaseCooldown > 14 ? Color.CYAN : Color.GRAY);
			if(shipBaseCooldown > 14)
				gg.drawString(Settings.CONTROLLERS ? "[B]" : "[2]", baseX + 200, titleHeight + 90);
			else
				gg.drawString("[MAX]", baseX + 200, titleHeight + 90);
			//
			gg.setColor(Color.WHITE);
			gg.drawImage(AssetManager.loadImage(getClass(), "shield.png"), baseX - 60, titleHeight + 110, 50, 50, null);
			gg.drawString((int)shipBaseShield + "", baseX, titleHeight + 150);
			gg.drawString("->", baseX + 63, titleHeight + 150);
			gg.drawString(shipBaseShield != 3 ? ((int)(shipBaseShield + 1) + "") : "3", baseX + 130, titleHeight + 150);
			gg.setColor(shipBaseShield < 3 ? Color.CYAN : Color.GRAY);
			if(shipBaseShield < 3)
				gg.drawString(Settings.CONTROLLERS ? "[X]" : "[3]", baseX + 200, titleHeight + 150);
			else
				gg.drawString("[MAX]", baseX + 200, titleHeight + 150);
		}
		else if(transitionTicks < 100 && transitionTicks > 0)
		{
			int titleHeight = BalagaLauncher.gameHeight/2 - 82;
			gg.drawString("ROUND " + round, 480, titleHeight - 20);
			//
			gg.setColor(Color.WHITE);
			gg.drawImage(AssetManager.loadImage(getClass(), "health.png"), 440, titleHeight - 10, 50, 50, null);
			gg.drawString((int)oldBaseHealth + "", 500, titleHeight + 30);
			gg.drawString("->", 563, titleHeight + 30);
			if(shipBaseHealth > oldBaseHealth) gg.setColor(Color.GREEN);
			gg.drawString((int)shipBaseHealth + "", 630, titleHeight + 30);
			//
			gg.setColor(Color.WHITE);
			gg.drawImage(AssetManager.loadImage(getClass(), "cooldown.png"), 430, titleHeight + 50, 64, 50, null);
			gg.drawString((int)oldBaseCooldown + "", 500, titleHeight + 90);
			gg.drawString("->", 563, titleHeight + 90);
			if(shipBaseCooldown < oldBaseCooldown) gg.setColor(Color.GREEN);
			gg.drawString((int)shipBaseCooldown + "", 630, titleHeight + 90);
			//
			gg.setColor(Color.WHITE);
			gg.drawImage(AssetManager.loadImage(getClass(), "shield.png"), 430, titleHeight + 110, 50, 50, null);
			gg.drawString((int)oldBaseShield + "", 500, titleHeight + 150);
			gg.drawString("->", 563, titleHeight + 150);
			if(shipBaseShield > oldBaseShield) gg.setColor(Color.GREEN);
			gg.drawString((int)shipBaseShield + "", 630, titleHeight + 150);
		}
	}
	
	private int getPlanetShift() {
		if(transitionTicks > 200)
			return 0;
		
		return (int)(-Math.abs(2*transitionTicks - 200) + 200);
	}

	public void update(List<Integer> keys)
	{	
		asteroidTicks++;
		
		if(ticksSinceStart > 0)
			ticksSinceStart--;
		
		if(transitionTicks > 0 && transitionTicks != 100)
			transitionTicks--;
			
		if(transitionTicks == 200)
			AssetManager.playSound(getClass(), "roundWin.wav");
		else if(transitionTicks == 101)
		{
			for(Ship s : ships)
			{
				if(s.isDead())
					s.revive();
				
				s.shield();
			}
		}
		else if(transitionTicks == 100)
		{
			if(keys.contains(49) && shipBaseHealth < 50)
			{
				oldBaseHealth = shipBaseHealth;
				oldBaseCooldown = shipBaseCooldown;
				oldBaseShield = shipBaseShield;
				upgradeShipHealth();
				transitionTicks--;
				resetPlanet(100);
			}
			else if(keys.contains(50) && shipBaseCooldown > 14)
			{
				oldBaseHealth = shipBaseHealth;
				oldBaseCooldown = shipBaseCooldown;
				oldBaseShield = shipBaseShield;
				upgradeShipCooldown();
				transitionTicks--;
				resetPlanet(100);
			}
			else if(keys.contains(51) && shipBaseShield < 3)
			{
				oldBaseHealth = shipBaseHealth;
				oldBaseCooldown = shipBaseCooldown;
				oldBaseShield = shipBaseShield;
				upgradeShipShield();
				transitionTicks--;
				resetPlanet(100);
			}
		}
		else if(transitionTicks == 1)
			advanceRound();
		
		for(int x = 0;x < ships.size();x++)
			ships.get(x).update(keys);
		for(int x = 0;x < asteroids.size();x++)
			asteroids.get(x).update();
		for(int x = 0;x < bullets.size();x++)
			bullets.get(x).update();
		for(int x = 0;x < entities.size();x++)
			entities.get(x).update();
		
		if(boss != null)
			boss.update();
		
		double spawnRate = ((1.0 + Math.log(round)*(1/2))/250)*(1 + ((ships.size()-1)*.5));
		if(hasPlanet() && (random.nextDouble() <= spawnRate || asteroidTicks > 300) && !asteroidsFinished() && transitionTicks == 0)
			spawnAsteroid();
	}
	
	private void spawnAsteroid()
	{
		int radius = 40;
		int randX = (int)(random.nextDouble()*(BalagaLauncher.gameWidth-39-(radius*4))+40);
		double health = .5*round + 4;
		PowerUp powerUp = null;
		if(round > 1 && random.nextDouble() <= (Settings.DEV ? 1 : .1))
			powerUp = PowerUp.getPossible(round)[(int)(random.nextDouble()*PowerUp.getPossible(round).length)];
		boolean frozen = (round > 2) ? (random.nextDouble() <= 0.1) : false;
		
		asteroids.add(new Asteroid(randX, radius, health, 1, this, powerUp, frozen));
		asteroidsSpawned++;
		asteroidTicks = 0;
	}
	
	public void spawnAsteroid(int r, double h, double v, boolean f)
	{
		int randX = (int)(random.nextDouble()*(BalagaLauncher.gameWidth-39-(r*4))+40);
		PowerUp powerUp = null;
		if(round > 1 && random.nextDouble() <= (Settings.DEV ? 1 : .1))
			powerUp = PowerUp.getPossible(round)[(int)(random.nextDouble()*PowerUp.getPossible(round).length)];
		
		asteroids.add(new Asteroid(randX, r, h, v, this, powerUp, f));
	}

	public void createBullet(int x, int y, Ship s)
	{
		bullets.add(new Bullet(x, y, s, this));
	}
	
	public void createBullet(int x, int y, int hSpeed, int vSpeed, boolean friendly, PowerUp powerUp, Ship s)
	{
		bullets.add(new Bullet(x, y, hSpeed, vSpeed, this, friendly, powerUp, s));
	}
	
	public void addEntity(Entity e)
	{
		entities.add(e);
	}

	public List<Bullet> getBullets()
	{
		return bullets;
	}
	
	public List<Asteroid> getAsteroids()
	{
		return asteroids;
	}
	
	public List<Ship> getShips()
	{
		return ships;
	}
	
	public List<Entity> getEntities()
	{
		return entities;
	}
	
	public Ship getShip(int player)
	{
		if(player > ships.size())
			return null;
		
		return ships.get(player-1);
	}
	
	public void jolt(int amount)
	{
		panel.setJitter(amount);
	}
	
	public void hurt(int amount)
	{
		planetHealth -= amount;
		if(isGameOver())
			AssetManager.playSound(getClass(), "gameOver.wav");
	}
	
	public boolean allShipsDead()
	{
		int shipsDead = 0;
		for(Ship s : ships)
		{
			if(s.isDead())
				shipsDead++;
		}
		
		return (shipsDead == ships.size());
	}
	
	public boolean isGameOver()
	{
		return (planetHealth <= 0) || allShipsDead();
	}
	
	public boolean isGameFinished()
	{
		return (round > 10);
	}
	
	public void score(int amount)
	{
		score += amount;
	}
	
	public boolean asteroidsFinished()
	{
		return (getRemainingAsteroids() - asteroids.size()) == 0;
	}
	
	public int getRemainingAsteroids()
	{
		return (asteroidBaseAmount*round)*ships.size() - (asteroidsSpawned - asteroids.size());
	}
	
	public void transition()
	{
		round++;
		transitionTicks = 300;
	}
	
	public void advanceRound()
	{
		asteroidsSpawned = 0;
	}
	
	public void killPlanet()
	{
		hurt(planetHealth);
	}

	public boolean hasPlanet() {
		return planet != 0;
	}

	public int getShipBaseCooldown() {
		return shipBaseCooldown;
	}

	public double getShipBaseHealth() {
		return shipBaseHealth;
	}
	
	public int getShipBaseShield() {
		return shipBaseShield;
	}
	
	public void upgradeShipCooldown()
	{
		shipBaseCooldown--;
	}
	
	public void upgradeShipHealth()
	{
		shipBaseHealth += 5;
		for(Ship s : ships)
			s.heal(5);
	}
	
	public void upgradeShipShield()
	{
		shipBaseShield++;
		shieldAllShips();
	}

	public boolean isTransitioning() {
		return transitionTicks > 0;
	}
	
	public void healAllShips()
	{
		for(int x = 0;x < ships.size();x++)
			ships.get(x).recover();
	}
	
	public void shieldAllShips()
	{
		for(int x = 0;x < ships.size();x++)
			ships.get(x).shield();
	}

	public void setBoss(Boss b) {
		healAllShips();
		boss = b;
	}

	public Boss getBoss() {
		return boss;
	}
}
