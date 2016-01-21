package sypan.game;

import org.jbox2d.common.Vec2;

/**
 * Gives the renderer a base type.
 * 
 * @author Carl Linley
 **/
public interface Renderable {

	Object getType();
	Vec2 getPosition();
}