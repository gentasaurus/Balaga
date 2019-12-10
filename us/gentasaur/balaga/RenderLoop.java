package us.gentasaur.balaga;

public class RenderLoop implements Runnable
{
	private BalagaPanel panel;
	private static final int FPS = 60;
	
	public RenderLoop(BalagaPanel panel)
	{
		this.panel = panel;
	}
	
	public void run() {
		while(true)
		{
			try {
				panel.repaint();
				Thread.sleep(1000/(int)(FPS * panel.getTimeScale()));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
