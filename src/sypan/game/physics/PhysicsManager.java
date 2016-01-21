package sypan.game.physics;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import sypan.game.Game;
import sypan.game.entity.AbstractEntity;
import sypan.utility.Logger;

/**
 * @author Carl Linley
 **/
public class PhysicsManager {

	public static final Vec2 LEFT = new Vec2(-1, 0),
							 RIGHT = new Vec2(1, 0),
							 UP = new Vec2(0, -1);

	public static final float PHYS_TICK = 15;

	private final float PHYSICS_TIME_STEP = 1f / PHYS_TICK;
	private final int PHYSICS_VELOCITY_ITERATIONS = 6,
					  PHYSICS_POSITION_ITERATIONS = 2;

	private World physicsWorld;
	private CollisionListener collisionListener;

	public PhysicsManager(Game game) {
		collisionListener = new CollisionListener(game);
		initWorld();

		Logger.logInfo("PhysicsManager initialised.");
	}

	private void initWorld() {
		physicsWorld = new World(new Vec2(0, -10f));
		physicsWorld.setContactListener(collisionListener);
	}

	/**
	 * 'This should not vary'? I'd have framerate dependent physics...
	 **/
	public void update(float timePerFrame) {
		physicsWorld.step(PHYSICS_TIME_STEP, PHYSICS_VELOCITY_ITERATIONS, PHYSICS_POSITION_ITERATIONS);
	}

	public Body create(Physical physicalThing, Vec2 initialPosition) {
		BodyDef bodyDefinition = physicalThing.getPhysicalData().getBodyDefinition();
		bodyDefinition.setPosition(initialPosition);

		Body physicsBody = physicsWorld.createBody(bodyDefinition);
		FixtureDef fixtureDefinition = physicalThing.getPhysicalData().getFixtureDefinition();
		fixtureDefinition.setUserData(physicalThing);

		physicsBody.createFixture(fixtureDefinition);
		physicsBody.setFixedRotation(physicalThing.getPhysicalData().isFixedRotation());
		physicalThing.setPhysics(physicsBody);

		Logger.logInfo("Added " + physicalThing + " to physics simulation.");
		return physicsBody;
	}

	public Body createEntity(Physical physicalThing, Vec2 initialPosition) {
		BodyDef bodyDefinition = physicalThing.getPhysicalData().getBodyDefinition();
		bodyDefinition.setPosition(initialPosition);

		Body physicsBody = physicsWorld.createBody(bodyDefinition);
		FixtureDef fixtureDefinition = physicalThing.getPhysicalData().getFixtureDefinition();
		fixtureDefinition.setUserData(physicalThing);
		physicsBody.createFixture(fixtureDefinition);

		// 'Feet', for jumping/falling
		FixtureDef baseChecker = new FixtureDef();
		PolygonShape baseShape = new PolygonShape();
		baseShape.setAsBox(physicalThing.getPhysicalData().getSize().x, 2.5f, new Vec2(0, -physicalThing.getPhysicalData().getSize().y), 0);
		baseChecker.setShape(baseShape);
		baseChecker.setSensor(true);
		baseChecker.setUserData(new EntityBase((AbstractEntity) physicalThing));
		physicsBody.createFixture(baseChecker);

		physicsBody.setFixedRotation(physicalThing.getPhysicalData().isFixedRotation());
		physicalThing.setPhysics(physicsBody);

		Logger.logInfo("Added " + physicalThing + " to physics simulation.");
		return physicsBody;
	}

	public void remove(Physical physicalThing) {
		physicsWorld.destroyBody(physicalThing.getPhysics());
		Logger.logInfo("Removed " + physicalThing + " from physics simulation.");
	}

	public World getSimulation() {
		return physicsWorld;
	}

	public void clear() {
		initWorld();

/*		Body currentBody; // Causes a crash when finishing via graveyard..?

		while ((currentBody = physicsWorld.getBodyList().getNext()) != null) {
			physicsWorld.destroyBody(currentBody);
		}*/
	}
}