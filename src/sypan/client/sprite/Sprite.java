package sypan.client.sprite;

import sypan.game.Renderable;
import sypan.game.entity.AbstractEntity;
import sypan.game.entity.AnimationType;

/**
 * Everything implementing {@code Renderable} has an associated sprite.
 * 
 * @author Carl Linley
 **/
public class Sprite {

	private SpriteType spriteType;

	private AnimationType currentAnimation;
	private float currentFrame;

	protected Sprite(Renderable renderable, SpriteType spriteType) {
		this.spriteType = spriteType;
	}

	protected void checkAnimation(AbstractEntity e) {
		if (e.getCurrentAnimation() != currentAnimation) {
			currentAnimation = e.getCurrentAnimation();
			currentFrame = 0;
		}
		else if (currentFrame >= spriteType.getAnimationLength(currentAnimation)) {
			currentFrame = 0;
		}
	}

	protected int frame(float timePerFrame) {
		currentFrame += timePerFrame;

		return (int) currentFrame;
	}
}