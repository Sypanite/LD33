package sypan.game.physics;

import sypan.game.entity.AbstractEntity;

/**
 * @author Carl Linley
 **/
public class EntityBase {

	AbstractEntity abstractEntity;

	public EntityBase(AbstractEntity abstractEntity) {
		this.abstractEntity = abstractEntity;
	}

	public AbstractEntity getEntity() {
		return abstractEntity;
	}
}