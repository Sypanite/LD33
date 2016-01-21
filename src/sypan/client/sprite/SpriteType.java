package sypan.client.sprite;

import org.jbox2d.common.Vec2;

import sypan.game.entity.AnimationType;
import sypan.game.entity.type.EntityType;

/**
 * @author Carl Linley
 **/
public enum SpriteType {

	// Entities
	SPIRIT(new Vec2(32, 32), new int[] {5, -1, -1, -1}),
	CRIMINAL_A(SpriteManager.HUMAN, new int[] {1, 9, -1, -1}),
	CRIMINAL_B(SpriteManager.HUMAN, new int[] {1, 9, -1, -1}),
	CRIMINAL_C(SpriteManager.HUMAN, new int[] {1, 9, -1, -1}),
	CRIMINAL_D(SpriteManager.HUMAN, new int[] {1, 9, -1, -1}),
	CRIMINAL_E(SpriteManager.HUMAN, new int[] {1, 9, -1, -1}),

	CIVILIAN_A(SpriteManager.HUMAN, new int[] {1, 9, -1, -1}),
	CIVILIAN_B(SpriteManager.HUMAN, new int[] {1, 9, -1, -1}),
	CIVILIAN_C(SpriteManager.HUMAN, new int[] {1, 9, -1, -1}),
	CIVILIAN_D(SpriteManager.HUMAN, new int[] {1, 9, -1, -1}),
	CIVILIAN_E(SpriteManager.HUMAN, new int[] {1, 9, -1, -1}),

	// Items

	// Objects
	GRAVEYARD_ENTRANCE(new Vec2(128, 128), null),
	GRAVEYARD_CONTINUED(new Vec2(128, 128), null),
	GRAVE(new Vec2(32, 64), null),
	DEAD_TREE(new Vec2(128, 128), null),
	SPIKES(new Vec2(32, 32), null),
	DOOR_CLOSED(new Vec2(32, 64), null),
	DOOR_OPEN(new Vec2(32, 64), null);

	private Vec2 spriteSize;
	private int[] animationLength;

	/**
	 * @param spriteSize - the size of the sprite, in pixels.
	 * @param animationLength - an array containing the amount of frames in each animation. The Indices correspond to the values
	 * 							in {@link AnimationType} (via ordinal()). E.g index 0 is 'IDLE', 1 is 'WALK', etc. If a sprite does
	 * 							not support a given animation, the frame count should be -1 for clarity.
	 **/
	private SpriteType(Vec2 spriteSize, int[] animationLength) {
		this.spriteSize = spriteSize;
		this.animationLength = animationLength;

		if (animationLength != null) {
			for (int i = 0; i != animationLength.length; i++) {
				animationLength[i] --;
			}
		}
	}

	/**
	 * @param spriteSize - the size of the sprite, in pixels.
	 **/
	private SpriteType(Vec2 spriteSize) {
		this.spriteSize = spriteSize;
	}

	public boolean isEntity() {
		try {
			EntityType.valueOf(toString());
			return true;
		}
		catch(IllegalArgumentException e) {
			return false;
		}
	}

	public int getAnimationLength(AnimationType animationType) {
		if (animationLength == null) {
			return 0;
		}
		return animationLength[animationType.ordinal()];
	}

	public int getWidth() {
		return (int) spriteSize.x;
	}

	public int getHeight() {
		return (int) spriteSize.y;
	}
}