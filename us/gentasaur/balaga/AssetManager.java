package us.gentasaur.balaga;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AssetManager {

	public static BufferedImage loadImage(Class<?> caller, String name)
	{
		BufferedImage image = null;
		try {
			image = ImageIO.read(caller.getResourceAsStream("/resources/images/" + name));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}
	
	public static void playSound(Class<?> caller, String name)
	{
		try {
			Clip clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(new BufferedInputStream(caller.getResourceAsStream("/resources/sounds/" + name))));
			clip.start();
		} catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
			e.printStackTrace();
		}
	}
	
	public static Font getFont(Class<?> caller, float size)
	{
		try {
			return Font.createFont(Font.TRUETYPE_FONT, caller.getResourceAsStream("/resources/font.ttf")).deriveFont(size);
		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
			return new Font("Sans-Serif", Font.BOLD, 40);
		}
	}
}
