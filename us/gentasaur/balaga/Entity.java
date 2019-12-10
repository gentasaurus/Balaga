package us.gentasaur.balaga;

import java.awt.Graphics2D;

public abstract class Entity {

	public GameWorld world;
	public Hitbox hb;
	
	abstract void update();
	abstract void paint(Graphics2D gg);
	
	public Entity(GameWorld gw)
	{
		world = gw;
	}
	
	public void die()
	{
		world.getEntities().remove(this);
	}
}
