package sypan.game.object.type;

import sypan.game.Game;
import sypan.game.entity.AbstractEntity;
import sypan.game.object.AbstractObject;
import sypan.game.object.classification.Goal;

/**
 * An inert object.
 * 
 * @author Carl Linley
 **/
public class Graveyard extends AbstractObject implements Goal {

	@Override
	public void initialise(Game game) {
	}

	@Override
	public void onTouch(AbstractEntity e) {
		if (!getGame().gameOver()) {
			if (e.isPlayer()) {
				if (getGame().currentCriminals() == 0) {
					getGame().loadNextLevel();
				}
				else {
					getGame().displayMessage("You have unfinished business!");
				}
			}
			else if (e.isControlled()) {
				getGame().displayMessage("You must leave your corporeal form!");
			}
		}
	}
}