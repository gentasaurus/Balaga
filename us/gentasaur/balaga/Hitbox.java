package us.gentasaur.balaga;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;

@SuppressWarnings("serial")
public class Hitbox extends Polygon
{
	public Hitbox(double centerX, double centerY, Point[] points, double deg)
	{
		super();
		for(Point p : points)
		{
			double theta = Math.toRadians(deg);
			if(theta == 0)
				addPoint(p.getX(), p.getY());
			else
			{
				double tempX = p.getX() - centerX;
				double tempY = p.getY() - centerY;
				
				double rotX = (tempX*Math.cos(theta) - tempY*Math.sin(theta));
				double rotY = (tempX*Math.sin(theta) + tempY*Math.cos(theta));
				
				addPoint((int)(rotX + centerX), (int)(rotY + centerY));
			}
		}
	}
	
	public void paint(Graphics2D gg)
	{
		if(Settings.DEV)
		{
			gg.setColor(new Color(255, 0, 0, 127));
			gg.fill(this);
			gg.setColor(Color.RED);
			gg.draw(this);
		}
	}
	
	public boolean touching(Hitbox hb)
	{	
		for(int i = 0;i < hb.npoints;i++)
		{
			int xCheck = hb.xpoints[i];
			int yCheck = hb.ypoints[i];
			if(this.contains(xCheck, yCheck))
				return true;
		}
		
		return false;
	}
}
