package sypan.game.object;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;

import sypan.game.level.Level;
import sypan.game.object.type.Door;
import sypan.game.object.type.GenericObject;
import sypan.game.object.type.Graveyard;
import sypan.game.object.type.Spikes;
import sypan.game.physics.PhysicalData;
import sypan.game.physics.PhysicsShape;

/**
 * @author Carl Linley
 **/
public enum ObjectType {

	GRAVEYARD_ENTRANCE(new PhysicalData(BodyType.STATIC, PhysicsShape.BOX(60, 60), false, false, true), Graveyard.class),
	GRAVEYARD_CONTINUED(new PhysicalData(BodyType.STATIC, PhysicsShape.BOX(60, 60), true, false, true), Graveyard.class),
	GRAVE(new PhysicalData(BodyType.STATIC, PhysicsShape.BOX(15, 15), true, false, true), null), // Spawn / Scenery
	DEAD_TREE(new PhysicalData(BodyType.STATIC, PhysicsShape.BOX(32, 64), true, false, true), null),
	SPIKES(new PhysicalData(BodyType.STATIC, PhysicsShape.LINE(new Vec2(-Level.HALF_TILE_WU, -Level.HALF_TILE_WU * 0.75f), new Vec2(Level.HALF_TILE_WU, -Level.HALF_TILE_WU * 0.75f)), true, false, true), Spikes.class),
	DOOR_CLOSED(new PhysicalData(BodyType.STATIC, PhysicsShape.BOX(15, 30), false, false, true), Door.class),
	DOOR_OPEN(new PhysicalData(BodyType.STATIC, PhysicsShape.BOX(15, 30), true, false, true), Door.class);

	private PhysicalData physicalData;
	private Class<? extends AbstractObject> objectClass;

	private ObjectType(PhysicalData physicalData, Class<? extends AbstractObject> objectClass) {
		this.physicalData = physicalData;
		this.objectClass = objectClass;

		if (objectClass == null) {
			this.objectClass = GenericObject.class;
		}
	}

	public PhysicalData getPhysicalData() {
		return physicalData;
	}

	public Class<? extends AbstractObject> getClassType() {
		return objectClass;
	}
}