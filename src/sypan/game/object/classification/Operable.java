package sypan.game.object.classification;

import org.jbox2d.common.Vec2;

/**
 * Implemented by object classes that can be operated by the player.
 * 
 * @author Carl Linley
 **/
public interface Operable {

	void onOperation();
	String getTooltip();

	Vec2 getPosition();
}