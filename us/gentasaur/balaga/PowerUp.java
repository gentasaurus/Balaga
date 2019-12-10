package us.gentasaur.balaga;

import java.awt.Color;

public class PowerUp
{
	private String id;
	private int level;
	private Color color;
	
	public PowerUp(String i, int l, Color c)
	{
		id = i;
		level = l;
		color = c;
	}
	
	public String getID()
	{
		return id;
	}
	
	public int getLevel()
	{
		return level;
	}
	
	public Color getColor()
	{
		return color;
	}
	
	public static final PowerUp HEAL_1 = new PowerUp("heal", 1, Color.PINK);
	public static final PowerUp HEAL_2 = new PowerUp("heal", 2, Color.PINK);
	public static final PowerUp HEAL_3 = new PowerUp("heal", 3, Color.PINK);
	public static final PowerUp FREEZE = new PowerUp("freeze", 1, Color.CYAN);
	public static final PowerUp RAPIDFIRE = new PowerUp("rapidfire", 1, Color.YELLOW);
	public static final PowerUp HOMING = new PowerUp("homing", 1, Color.BLUE);
	public static final PowerUp REPEL = new PowerUp("repel", 1, Color.decode("#6b609f"));
	public static final PowerUp BEAM = new PowerUp("beam", 1, Color.RED);
	public static final PowerUp HEAVY = new PowerUp("heavy", 1, Color.decode("#ff6600"));
	public static final PowerUp ENERGY = new PowerUp("energy", 1, Color.GREEN);
	
	public static PowerUp[] getPossible(int round)
	{
		PowerUp[] tier1 = {HEAL_1, FREEZE, RAPIDFIRE, HOMING};
		PowerUp[] tier2 = {HEAL_2, FREEZE, RAPIDFIRE, HOMING, REPEL, HEAVY};
		PowerUp[] tier3 = {HEAL_3, FREEZE, RAPIDFIRE, HOMING, REPEL, HEAVY, BEAM, ENERGY};
		
		PowerUp[] custom = tier3;
		
		if(Settings.DEV) return custom;
		
		if(round > 7)
			return tier3;
		else if(round > 4)
			return tier2;
		else
			return tier1;
	}
}
