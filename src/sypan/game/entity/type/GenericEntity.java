package sypan.game.entity.type;

import sypan.game.Game;
import sypan.game.entity.AbstractEntity;

/**
 * Used if there is not an explicit class for a given entity type.
 * 
 * @author Carl Linley
 **/
public class GenericEntity extends AbstractEntity {

	public GenericEntity(int index, Game game, EntityType entityType) {
		super(index, game, entityType);
	}

	@Override
	public void initialise() {
	}

	@Override
	public void logicUpdate() {
	}
}