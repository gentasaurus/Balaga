package us.gentasaur.balaga;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

public class Asteroid implements Targetable
{
	private int x;
	private int y;
	private int radius;
	private int vertSpeed;
	private int horSpeed;
	private double rot;
	private GameWorld world;
	private Hitbox hitbox;
	private double maxHealth;
	private double health;
	private PowerUp powerUp;
	private Bullet lastHitter;
	private int freezeTime;
	private int repelTime;
	private int energyTime;
	
	public Asteroid(int xLoc, int rad, double mh, double veloc, GameWorld gw, PowerUp power, boolean frozen)
	{
		freezeTime = frozen ? 600 : 0;
		repelTime = 0;
		energyTime = 0;
		lastHitter = null;
		powerUp = power;
		x = xLoc;
		y = -rad*3;
		radius = rad;
		rot = (int)(Math.random()*360);
		world = gw;
		vertSpeed = (int)(2*veloc);
		horSpeed = 0;
		maxHealth = mh;
		health = maxHealth;
		updateHitbox();
	}
	
	public void update()
	{
		if(freezeTime > 0)
			freezeTime--;
		
		if(repelTime > 0)
			repelTime--;
		
		if(energyTime > 0)
			energyTime--;
		
		if(repelTime > 0)
			rot -= (freezeTime > 0) ? .5 : 1;
		else if(energyTime > 0)
			rot += (energyTime / 180.0);
		else
			rot += (freezeTime > 0) ? .5 : 1;
		
		if(rot >= 360)
			rot -= 360;
		else if(rot < 0)
			rot += 360;
		
		if(freezeTime > 0)
		{
			y += (((repelTime > 0) ? -vertSpeed : vertSpeed) / 2);
			x += (horSpeed / 2);
		}
		else if(energyTime > 0)
		{
			y += vertSpeed * (energyTime / 180.0);
			x += horSpeed * (energyTime / 180.0);
		}
		else
		{
			y += (repelTime > 0) ? -vertSpeed : vertSpeed;
			x += horSpeed;
		}
		
		if(y > BalagaLauncher.gameHeight+110)
		{
			die();
			if(world.hasPlanet())
			{
				AssetManager.playSound(getClass(), "breakNormal.wav");
				world.hurt(10);
			}
		}
		
		if(energyTime > 1)
			health = maxHealth * (energyTime / 180.0);
		else if(energyTime == 1)
		{
			health = 0;
			AssetManager.playSound(getClass(), "breakEnergy.wav");
			world.createBullet(this.getCenter().getX()-6, this.getCenter().getY()-6, 0, -3, true, PowerUp.ENERGY, lastHitter.getShooter());
			world.createBullet(this.getCenter().getX()-6, this.getCenter().getY()-6, 3, -3, true, PowerUp.ENERGY, lastHitter.getShooter());
			world.createBullet(this.getCenter().getX()-6, this.getCenter().getY()-6, 3, 0, true, PowerUp.ENERGY, lastHitter.getShooter());
			world.createBullet(this.getCenter().getX()-6, this.getCenter().getY()-6, 3, 3, true, PowerUp.ENERGY, lastHitter.getShooter());
			world.createBullet(this.getCenter().getX()-6, this.getCenter().getY()-6, 0, 3, true, PowerUp.ENERGY, lastHitter.getShooter());
			world.createBullet(this.getCenter().getX()-6, this.getCenter().getY()-6, -3, 3, true, PowerUp.ENERGY, lastHitter.getShooter());
			world.createBullet(this.getCenter().getX()-6, this.getCenter().getY()-6, -3, 0, true, PowerUp.ENERGY, lastHitter.getShooter());
			world.createBullet(this.getCenter().getX()-6, this.getCenter().getY()-6, -3, -3, true, PowerUp.ENERGY, lastHitter.getShooter());
		}
		
		if(health <= 0)
		{
			if(lastHitter != null)
				dieFromBullet();
			else
				dieFromUnknown();
		}
		
		updateHitbox();
	}
	
	private void updateHitbox()
	{
		Point[] points = new Point[]{
				new Point(x + 5, y + 5),
				new Point(x + 5, y + (radius*2 - 5)),
				new Point(x + (radius*2 - 5), y + (radius*2 - 5)),
				new Point(x + (radius*2 - 5), y + 5)};
		hitbox = new Hitbox(x + radius, y + radius, points, rot);
	}

	public void paint(Graphics2D gg)
	{
		gg.setColor(Color.BLACK);
		gg.drawRect(x, y - 20, radius*2, 5);
		gg.setColor(Color.GRAY);
		gg.fillRect(x, y - 20, radius*2, 5);
		gg.setColor(Color.RED);
		gg.fillRect(x, y - 20, (int)(health*((radius*2) / maxHealth)), 5);
		
		AffineTransform old = gg.getTransform();
		AffineTransform trans = new AffineTransform(old);
		trans.rotate(Math.toRadians(rot), x+radius, y+radius);
		double scaleFactor = radius / 40.0;
		trans.translate(-x*scaleFactor, -y*scaleFactor);
		trans.scale(scaleFactor, scaleFactor);
		trans.translate(x/scaleFactor, y/scaleFactor);
		gg.setTransform(trans);
		gg.setColor(new Color(68, 48, 28));
		gg.fillRect(x, y, 80, 80);
		gg.setColor(new Color(122, 85, 44));
		gg.fillRect(x + 5, y + 5, 70, 70);
		//
		gg.setColor(new Color(35, 22, 10));
		gg.fillRect(x + 15, y + 15, 30, 30);
		gg.fillRect(x + 65, y + 25, 10, 25);
		gg.fillRect(x + 40, y + 50, 20, 20);
		//
		if(powerUp != null)
		{
			gg.setColor(powerUp.getColor());
			gg.fillRect(x + 20, y + 20, 20, 20);
			gg.fillRect(x + 70, y + 30, 5, 15);
			gg.fillRect(x + 45, y + 55, 10, 10);
		}
		//
		if(health < maxHealth)
		{
			int state = 5;
			double healthPercent = health/maxHealth;
			if(healthPercent <= .8)
			{
				state = 4;
				if(healthPercent <= .6)
				{
					state = 3;
					if(healthPercent <= .4)
					{
						state = 2;
						if(healthPercent <= .2)
						{
							state = 1;
						}
					}
				}
			}
			gg.drawImage(AssetManager.loadImage(getClass(), "ast" + state + ".png"), x, y, 80, 80, null);
		}
		//
		if(freezeTime > 0)
		{
			gg.setColor(new Color(145, 233, 255, 150));
			gg.fillRect(x, y, 80, 80);
		}
		
		if(repelTime > 0)
		{
			gg.setColor(new Color(107, 96, 159, 150));
			gg.fillRect(x, y, 80, 80);
		}
		
		if(energyTime > 0)
		{
			// Fade
			gg.setColor(new Color(0, 175, 0, 150));
			gg.fillRect(x, y, 80, 80);
			// Glows
			int innerRadius = (int)(((180 - energyTime) / 180.0) * 40);
			int outerRadius = innerRadius * 4;
			if(outerRadius > 40)
				outerRadius = 40;
			
			gg.setColor(Color.GREEN);
			gg.fillRect(x + 40 - outerRadius, y + 40 - outerRadius, outerRadius * 2, outerRadius * 2);
			gg.setColor(Color.WHITE);
			gg.fillRect(x + 40 - innerRadius, y + 40 - innerRadius, innerRadius * 2, innerRadius * 2);
		}
		
		gg.setTransform(old);
		hitbox.paint(gg);
	}
	
	public Hitbox getHitbox()
	{
		return hitbox;
	}
	
	public void dieFromBullet()
	{
		if(powerUp != null)
		{
			AssetManager.playSound(getClass(), "breakPower.wav");
			world.score(20);
		}
		else if(freezeTime > 0)
		{
			AssetManager.playSound(getClass(), "breakIce.wav");
			world.score(5);
		}
		else
		{
			AssetManager.playSound(getClass(), "breakNormal.wav");
			world.score(10);
		}
			
		
		if(powerUp != null)
			lastHitter.getShooter().setPowerUp(powerUp, 10);
		die();
	}
	
	public void dieFromCrash()
	{
		AssetManager.playSound(getClass(), "breakBig.wav");
		die();
	}
	
	public void dieFromUnknown()
	{
		AssetManager.playSound(getClass(), "breakNormal.wav");
		world.score(10);
		die();
	}
	
	public void die()
	{
		world.getAsteroids().remove(this);
		if(world.hasPlanet())
		{
			world.jolt(10);
			if(world.isGameOver())
				AssetManager.playSound(getClass(), "gameOver.wav");
			else if(world.asteroidsFinished() && world.getAsteroids().size() == 0)
				world.transition();
		}
	}
	
	public void hurt(int amount)
	{
		if(energyTime == 0)
		{
			if(freezeTime > 0) amount *= 2;
			health -= amount;
		}
	}
	
	public void hurt(int amount, Bullet source)
	{
		if(energyTime == 0)
		{
			hurt(amount);
			lastHitter = source;
		}
	}
	
	public PowerUp getPowerUp()
	{
		return powerUp;
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}

	public Point getCenter() {
		return new Point(x + radius, y + radius);
	}
	
	public void freeze()
	{
		if(energyTime == 0)
		{
			if(freezeTime == 0)
				AssetManager.playSound(getClass(), "freeze.wav");
			freezeTime = 600;
		}
	}
	
	public void repel()
	{
		if(repelTime == 0 && energyTime == 0)
		{
			AssetManager.playSound(getClass(), "repel.wav");
			repelTime = 300;
		}
	}
	
	public void energize()
	{
		if(energyTime == 0)
		{
			AssetManager.playSound(getClass(), "energize.wav");
			energyTime = 180;
		}
	}
}
