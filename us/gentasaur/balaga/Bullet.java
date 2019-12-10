package us.gentasaur.balaga;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.LinkedList;

public class Bullet {
	
	private int x;
	private int y;
	private int horSpeed;
	private int vertSpeed;
	private boolean friendly;
	private GameWorld world;
	private Hitbox hitbox;
	private Ship shooter;
	private PowerUp powerUp;
	private Targetable target;
	
	public Bullet(int locX, int locY, int hSpeed, int vSpeed, GameWorld gw, boolean f, PowerUp pUp, Ship s)
	{
		x = locX;
		y = locY;
		world = gw;
		target = f ? null : getShipTarget();
		horSpeed = hSpeed;
		vertSpeed = vSpeed;
		shooter = s;
		friendly = f;
		powerUp = pUp;
		updateHitbox();
		if(target != null)
			checkHoming();
	}
	
	public Bullet(int locX, int locY, Ship s, GameWorld gw)
	{
		powerUp = s.getPowerUp();
		x = locX;
		y = locY;
		shooter = s;
		friendly = true;
		world = gw;
		if(powerUp == PowerUp.HOMING)
			target = getHostileTarget();
		horSpeed = 0;
		vertSpeed = -10;
		updateHitbox();
	}

	private Targetable getHostileTarget() {
		
		Targetable target = null;
		LinkedList<Targetable> allPossible = new LinkedList<Targetable>();
		for(int i = 0; i < world.getAsteroids().size();i++)
			allPossible.add(world.getAsteroids().get(i));
		for(Targetable targ : allPossible)
		{
			if(targ.getCenter().getY() < y + 5)
			{
				target = targ;
				break;
			}
		}
		return target;
	}
	
	private Targetable getShipTarget() {
		
		Targetable target = null;
		LinkedList<Targetable> allPossible = new LinkedList<Targetable>();
		for(int i = 0;i < world.getShips().size();i++)
			allPossible.add(world.getShips().get(i));
		
		target = allPossible.get((int)(Math.random()*world.getShips().size()));

		return target;
	}

	public void paint(Graphics2D gg)
	{
		if(friendly)
		{
			if(powerUp == null || powerUp.getID().equals("heal") || powerUp == PowerUp.BEAM)
				gg.setColor(Color.YELLOW);
			else
				gg.setColor(powerUp.getColor());
			
			if(powerUp == PowerUp.HEAVY)
			{
				gg.setColor(powerUp.getColor());
				gg.fillRect(x - 4, y - 4, 18, 18);
			}
				
			else
			{
				gg.fillRect(x, y, 12, 12);
				gg.setColor(Color.YELLOW);
				gg.fillRect(x + 3, y + 3, 6, 6);
			}
		}
		else
		{
			gg.setColor(Color.RED); 
			gg.fillRect(x, y, 12, 12);
		}
		
		hitbox.paint(gg);
	}
	
	public void update()
	{
		if(friendly && target != null)
			checkHoming();
		
		if(powerUp == PowerUp.HOMING && !world.getAsteroids().contains(target))
			target = getHostileTarget();
		
		x += horSpeed;
		y += vertSpeed;
		updateHitbox();
		
		if(y > BalagaLauncher.gameHeight+161)
			die();
		
		if(y < -20)
		{
			if(world.getBoss() != null && world.getBoss() instanceof BossAsteroid) world.getBoss().hurt(1);
			die();
		}
		
		for(int i = 0; i < world.getAsteroids().size();i++)
		{
			Asteroid astr = world.getAsteroids().get(i);
			Hitbox hb = astr.getHitbox();
			if(hb.touching(hitbox))
			{
				die();
				astr.hurt(powerUp == PowerUp.HEAVY ? 4 : 1, this);
				if(powerUp == PowerUp.FREEZE)
					astr.freeze();
				else if(powerUp == PowerUp.REPEL)
					astr.repel();
				else if(powerUp == PowerUp.ENERGY)
					astr.energize();
			}
		}
	}
	
	private void checkHoming()
	{
		int centerX = x + 6;
		int centerY = y + 6;
		int targetX = target.getCenter().getX();
		int targetY = target.getCenter().getY();
		int speedFactor = friendly ? 10 : 30;
		if(speedFactor != 0)
		{
			vertSpeed = -(centerY - targetY) / speedFactor;
			horSpeed = -(centerX - targetX) / speedFactor;
		}
		else
		{
			horSpeed = 0;
			vertSpeed = -10;
		}
	}
	
	private void updateHitbox() {
		if(powerUp == PowerUp.HEAVY)
		{
			Point[] points = new Point[]{
					new Point(x - 4, y - 4),
					new Point(x + 16, y - 4),
					new Point(x + 16, y + 16),
					new Point(x - 4, y + 16)};
			hitbox = new Hitbox(x + 6, y + 6, points, 0);
		}
		else
		{
			Point[] points = new Point[]{
					new Point(x, y),
					new Point(x + 12, y),
					new Point(x + 12, y + 12),
					new Point(x, y + 12)};
			hitbox = new Hitbox(x + 6, y + 6, points, 0);
		}
	}

	public void die()
	{
		world.getBullets().remove(this);
	}
	
	public Ship getShooter()
	{
		return shooter;
	}

	public Hitbox getHitbox() {
		return hitbox;
	}
	
	public boolean isFriendly()
	{
		return friendly;
	}
}
