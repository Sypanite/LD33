package sypan.game.object.classification;

import sypan.game.entity.AbstractEntity;

/**
 * If an entity collides with a fatal object, they die instantly.
 * 
 * @author Carl Linley
 **/
public interface Fatal {

	void onKill(AbstractEntity entity);
}