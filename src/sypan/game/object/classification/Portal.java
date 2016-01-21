package sypan.game.object.classification;

import sypan.game.entity.type.EntityType;
import sypan.utility.Vector2i;

/**
 * If Player collides with a Portal object, they are teleported elsewhere.
 * 
 * @author Carl Linley
 **/
public interface Portal {

	Vector2i getDestination();

	boolean shouldTeleport(EntityType type);
}