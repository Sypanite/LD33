package sypan.game.entity.type;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;

import sypan.game.entity.AbstractEntity;
import sypan.game.entity.type.npc.BaseCivilian;
import sypan.game.entity.type.npc.BaseCriminal;
import sypan.game.entity.type.player.Spirit;
import sypan.game.physics.PhysicalData;
import sypan.game.physics.PhysicsShape;
import sypan.utility.Utility;

/**
 * @author Carl Linley
 **/
public enum EntityType {

	SPIRIT(new PhysicalData(BodyType.DYNAMIC, new Vec2(10f, 10f), PhysicsShape.BOX, 0, 100f, 0.1f, false, true, true), 5f, Spirit.class),

	// NPCs - Criminals
	CRIMINAL_A(PhysicalData.HUMAN, 5f, BaseCriminal.class),
	CRIMINAL_B(PhysicalData.HUMAN, 5f, BaseCriminal.class),
	CRIMINAL_C(PhysicalData.HUMAN, 5f, BaseCriminal.class),
	CRIMINAL_D(PhysicalData.HUMAN, 5f, BaseCriminal.class),
	CRIMINAL_E(PhysicalData.HUMAN, 5f, BaseCriminal.class),

	// NPCs - Civilians
	CIVILIAN_A(PhysicalData.HUMAN, 5f, BaseCivilian.class),
	CIVILIAN_B(PhysicalData.HUMAN, 5f, BaseCivilian.class),
	CIVILIAN_C(PhysicalData.HUMAN, 5f, BaseCivilian.class),
	CIVILIAN_D(PhysicalData.HUMAN, 5f, BaseCivilian.class),
	CIVILIAN_E(PhysicalData.HUMAN, 5f, BaseCivilian.class);

	private PhysicalData physicalData;
	private float moveSpeed;

	private Class<? extends AbstractEntity> entityClass;

	private EntityType(PhysicalData physicalData, float moveSpeed, Class<? extends AbstractEntity> entityClass) {
		this.physicalData = physicalData;
		this.moveSpeed = moveSpeed;
		this.entityClass = entityClass;
	}

	public PhysicalData getPhysicalData() {
		return physicalData;
	}

	public float getMoveSpeed() {
		return moveSpeed;
	}

	public String getName() {
		return Utility.formatName(toString());
	}

	public Class<? extends AbstractEntity> getEntityClass() {
		return entityClass;
	}
}