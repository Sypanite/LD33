package sypan.game.object.type;

import sypan.game.Game;
import sypan.game.entity.AbstractEntity;
import sypan.game.object.AbstractObject;
import sypan.game.object.classification.Fatal;

/**
 * An inert object.
 * 
 * @author Carl Linley
 **/
public class Spikes extends AbstractObject implements Fatal {

	@Override
	public void initialise(Game game) {
	}

	@Override
	public void onKill(AbstractEntity entity) {
		entity.say("YOWCH!");
	}
}