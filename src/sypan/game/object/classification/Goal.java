package sypan.game.object.classification;

import sypan.game.entity.AbstractEntity;

/**
 * If Player collides with a goal, the level ends.
 * 
 * @author Carl Linley
 **/
public interface Goal {

	void onTouch(AbstractEntity a);
}