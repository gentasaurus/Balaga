package us.gentasaur.balaga;

import java.awt.Color;
import java.awt.Graphics2D;

public class VBeam extends Entity
{
	private int liveTicks;
	private int xPos;
	private int yPos;
	
	public VBeam(GameWorld w, int x)
	{
		super(w);
		xPos = x;
		yPos = BalagaLauncher.gameHeight-85;
		liveTicks = 300;
	}

	@Override
	public void update() {
		
		liveTicks--;
		if(liveTicks == 0)
			die();
		
		Point[] points = new Point[]{
				new Point(xPos, yPos),
				new Point(xPos + calcRadius(), -30),
				new Point(xPos - calcRadius(), -30)
		};
		hb = new Hitbox(xPos, yPos, points, 0);
		
		for(int i = 0; i < world.getAsteroids().size();i++)
		{
			Asteroid astr = world.getAsteroids().get(i);
			Hitbox hb = astr.getHitbox();
			if(hb.touching(hb))
				astr.hurt(1);
		}
		
		world.jolt(10);
	}
	
	public int calcRadius()
	{
		return (int)((8.0/3.0)*(liveTicks - 300) + 800);
	}

	@Override
	public void paint(Graphics2D gg) {
		gg.setColor(Color.RED);
		gg.fillPolygon(hb.xpoints, hb.ypoints, 3);
		gg.setColor(Color.WHITE);
		gg.fillPolygon(new int[]{xPos, xPos + (calcRadius()/4), xPos - (calcRadius()/4)}, new int[]{yPos, -30, -30}, 3);
	}
}
