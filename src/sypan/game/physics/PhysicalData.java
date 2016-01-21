package sypan.game.physics;

import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

/**
 * Stores physics data for any given {@code Physical}.
 * 
 * @author Carl Linley
 **/
public class PhysicalData {

	public final static PhysicalData HUMAN = new PhysicalData(BodyType.DYNAMIC, new Vec2(15f, 23f), PhysicsShape.BOX, 10f, 100f, 0.1f, false, true, true);

	private BodyDef bodyDefinition;
	private FixtureDef fixtureDefinition;

	private boolean isBullet, fixedRotation;
	private Vec2 size;

	/**
	 * Should be used for dynamic bodies.
	 * @param f 
	 **/
	public PhysicalData(BodyType bodyType, Vec2 size, int shapeID, float density, float friction, float restitution, boolean isSensor, boolean isBullet, boolean fixedRotation) {
		initBody(bodyType);
		this.size = size;

		Shape shape = PhysicsShape.BOX(size.x, size.y);

		initFixture(shape, density, friction, restitution, isSensor);

		this.isBullet = isBullet;
		this.fixedRotation = fixedRotation;
	}

	/**
	 * Should be used for static bodies.
	 * @param isBullet 
	 **/
	public PhysicalData(BodyType bodyType, Shape box, boolean isSensor, boolean isBullet, boolean fixedRotation) {
		initBody(bodyType);
		initFixture(box, 0, 0, 0, isSensor);

		this.isBullet = isBullet;
		this.fixedRotation = fixedRotation;
	}

	private void initBody(BodyType bodyType) {
		bodyDefinition = new BodyDef();
		bodyDefinition.setType(bodyType);
	}

	private void initFixture(Shape containerShape, float density, float friction, float restitution, boolean isSensor) {
		fixtureDefinition = new FixtureDef();
		fixtureDefinition.setDensity(density);
		fixtureDefinition.setShape(containerShape);
		fixtureDefinition.setFriction(friction);
		fixtureDefinition.setRestitution(restitution);
		fixtureDefinition.setSensor(isSensor);
	}

	public BodyDef getBodyDefinition() {
		return bodyDefinition;
	}

	public FixtureDef getFixtureDefinition() {
		return fixtureDefinition;
	}

	public boolean isBullet() {
		return isBullet;
	}

	public boolean isFixedRotation() {
		return fixedRotation;
	}

	public Shape getShape() {
		return fixtureDefinition.getShape();
	}

	public Vec2 getSize() {
		return size;
	}
}