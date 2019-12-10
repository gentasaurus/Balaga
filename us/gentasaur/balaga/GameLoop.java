package us.gentasaur.balaga;

public class GameLoop implements Runnable
{
	private BalagaPanel panel;
	private static final int TPS = 60;
	
	public GameLoop(BalagaPanel panel)
	{
		this.panel = panel;
	}
	
	public void run() {
		while(true)
		{
			try {
				panel.updateGame();
				Thread.sleep(1000/(int)(TPS * panel.getTimeScale()));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
