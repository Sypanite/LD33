package sypan.client.sprite;

import java.util.HashMap;

import org.jbox2d.common.Vec2;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

import sypan.game.Renderable;
import sypan.game.entity.AbstractEntity;
import sypan.game.entity.Direction;
import sypan.game.entity.type.player.Spirit;
import sypan.utility.Logger;

/**
 * @author Carl Linley
 **/
public class SpriteManager {

	public final static Vec2 HUMAN = new Vec2(64, 64);

	private HashMap<SpriteType, SpriteData> spriteData;
	private HashMap<Renderable, Sprite> spriteList;

	public SpriteManager() throws SlickException {
		spriteData = new HashMap<SpriteType, SpriteData>();
		spriteList = new HashMap<Renderable, Sprite>();

		for (SpriteType spriteType : SpriteType.values()) {
			spriteData.put(spriteType, new SpriteData(new SpriteSheet(new Image("resources/sprite/" + spriteType + ".png"), spriteType.getWidth() - 1, spriteType.getHeight() - 1)));
		}
		Logger.logInfo("SpriteManager initialised. Registered " + spriteData.size() + " sprites.");
	}

	/**
	 * @return the current sprite that should be drawn for the specified {@code Renderable}.
	 **/
	public Image get(Renderable r, float timePerFrame) {
		SpriteType type = getType(r);
		SpriteData data = spriteData.get(type);
		Sprite sprite = spriteList.get(r);

		if (sprite == null) {
			sprite = createSprite(r);
		}
		if (type.isEntity()) {
			AbstractEntity e = (AbstractEntity) r;

			sprite.checkAnimation(e);

			Image image = data.get(sprite.frame(timePerFrame), e.getCurrentAnimation().ordinal());

			if (!(r instanceof Spirit)) {
				return (e.getFaceDirection() == Direction.LEFT ? image : image.getFlippedCopy(true, false));
			}
			return image;
		}
		return data.get(0, 0);
	}

	public Sprite createSprite(Renderable r) {
		Sprite newSprite = new Sprite(r, getType(r));

		spriteList.put(r, newSprite);
		return newSprite;
	}

	public SpriteType getType(Renderable r) {
		try {
			return SpriteType.valueOf(r.getType().toString());
		}
		catch(IllegalArgumentException e) {
			Logger.logWarning("Failed to retrieve sprite: " + r + "! No SpriteType value for " + r.getType() + ".");
			return null;
		}
	}

	public SpriteData getData(Renderable toRender) {
		return spriteData.get(getType(toRender));
	}
}