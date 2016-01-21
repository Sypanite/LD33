package sypan.game.entity.type.possession;

import org.jbox2d.common.Vec2;

import sypan.utility.Vector2i;

/**
 * Implemented by entities that can be possessed.
 * 
 * @author Carl Linley
 **/
public interface Controllable {

	Personality getPersonality();

	Vec2 getPosition();
	Vector2i getTile();

	void say(String possessedSpeech);
	void stop();

	String getName();
}