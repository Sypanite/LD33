package sypan.game.object;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

import sypan.game.Game;
import sypan.game.Renderable;
import sypan.game.physics.Physical;
import sypan.game.physics.PhysicalData;
import sypan.utility.Logger;
import sypan.utility.Utility;
import sypan.utility.Vector2i;

/**
 * The superclass of all objects.
 * 
 * @author Carl Linley
 **/
public abstract class AbstractObject implements Physical, Renderable {

	private Game game;

	private int objectIndex;
	private ObjectType objectType;
	private Body objectPhysics;

	/**
	 * Used internally.
	 **/
	protected void construct(Game game, ObjectType objectType, int objectIndex) {
		if (hasConstructed()) {
			return;
		}
		this.game = game;
		this.objectIndex = objectIndex;
		this.objectType = objectType;
		Logger.logInfo("Constructed object " + getIndex() + ":" + getType() + ".");
	}

	public abstract void initialise(Game game);

	@Override
	public void setPhysics(Body objectPhysics) {
		this.objectPhysics = objectPhysics;
	}

	/**
	 * @return the object's 'name' - by default, this is the type (formatted).
	 **/
	public String getName() {
		return Utility.formatName(objectType.toString());
	}

	@Override
	public PhysicalData getPhysicalData() {
		return objectType.getPhysicalData();
	}

	@Override
	public Body getPhysics() {
		return objectPhysics;
	}

	@Override
	public Vec2 getPosition() {
		return objectPhysics.getPosition();
	}

	public float getX() {
		return getPosition().x;
	}

	public float getY() {
		return getPosition().y;
	}

	@Override
	public int getIndex() {
		return objectIndex;
	}

	@Override
	public ObjectType getType() {
		return objectType;
	}

	public Vector2i getTile() {
		return Vector2i.physicsToTile(getPosition());
	}

	protected Game getGame() {
		return game;
	}

	@Override
	public String toString() {
		return getIndex() + ":" + getType() + " " + getPosition();
	}

	private boolean hasConstructed() {
		return objectType != null;
	}

	public boolean isClipped() {
		return !objectPhysics.getFixtureList().isSensor();
	}
}