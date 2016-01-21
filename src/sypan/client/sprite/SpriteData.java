package sypan.client.sprite;

import org.newdawn.slick.Image;
import org.newdawn.slick.SpriteSheet;

import sypan.utility.Logger;

/**
 * @author Carl Linley
 **/
public class SpriteData {

	private SpriteSheet spriteSheet;

	protected SpriteData(SpriteSheet spriteSheet) {
		this.spriteSheet = spriteSheet;
	}

	public Image get(int x, int y) {
		try {
			return spriteSheet.getSprite(x, y);
		}
		catch(Exception e) {
			Logger.logSevere("Error retrieving sprite: " + e);
			return spriteSheet.getSprite(0, 0);
		}
	}
}