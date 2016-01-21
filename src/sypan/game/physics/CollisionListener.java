package sypan.game.physics;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.contacts.Contact;

import sypan.game.Game;
import sypan.game.entity.AbstractEntity;
import sypan.game.entity.type.player.Spirit;
import sypan.game.level.Level;
import sypan.game.object.AbstractObject;
import sypan.game.object.classification.Fatal;
import sypan.game.object.classification.Goal;
import sypan.game.object.classification.Operable;
import sypan.game.object.classification.Portal;

/**
 * @author Carl Linley
 **/
public class CollisionListener implements ContactListener {

	private Game game;

	public CollisionListener(Game game) {
		this.game = game;
	}

	@Override
	public void beginContact(Contact contact) {
		if (isValid(contact)) {
			Physical a = (Physical) contact.getFixtureA().getUserData(),
					 b = (Physical) contact.getFixtureB().getUserData();

			if (isEntity(a)) {
				if (isObject(b)) {
					if (isPlayer(a)) {
						if (isOperable(b)) {
							game.getSpirit().setProximateOperable((Operable) b);
						}
						if (isGoal(b)) {
							((Goal) b).onTouch((AbstractEntity) a);
						}
					}

					AbstractEntity asEntity = (AbstractEntity) a;

					if (isPortal(b)) {
						Portal asPortal = (Portal) b;

						if (asPortal.shouldTeleport(asEntity.getType())) {
							asEntity.setTile(asPortal.getDestination());
						}
					}
				}
			}
		}
		else {
			Object a = contact.getFixtureA().getUserData(),
				   b = contact.getFixtureB().getUserData();

			if (isGround(a) && isEntityBase(b)
			 || isGround(b) && isEntityBase(a)) {
				EntityBase base;

				if (b instanceof EntityBase) {
					base = (EntityBase) b;
				}
				else {
					base = (EntityBase) a;
				}
				base.getEntity().alterGround(1);
			}
			else if (isObject(a) && isEntityBase(b)
				  || isObject(b) && isEntityBase(a)) { // Spikes require special treatment
				AbstractEntity entity;
				AbstractObject object;

				entity = ((EntityBase) (isObject(b) ? a : b)).getEntity();
				object = (AbstractObject) (isObject(b) ? b : a);

				if (isFatal(object)) {
					Fatal asFatal = (Fatal) object;

					if (!entity.isSpirit()) {
						asFatal.onKill(entity);
						entity.kill(false);
					}
				}
			}
		}
	}

	@Override
	public void endContact(Contact contact) {
		if (isValid(contact)) {
			Physical a = (Physical) contact.getFixtureA().getUserData(),
					 b = (Physical) contact.getFixtureB().getUserData();
	
			if (isEntity(a)) {
				if (isPlayer(a) && isOperable(b)) {
					if (game.getSpirit().getProximateOperable() == b) {
						game.getSpirit().setProximateOperable(null);
					}
				}
			}
		}
		else {
			Object a = contact.getFixtureA().getUserData(),
				   b = contact.getFixtureB().getUserData();

			if (isGround(a) && isEntityBase(b)
			 || isGround(b) && isEntityBase(a)) {
				EntityBase base;

				if (b instanceof EntityBase) {
					base = (EntityBase) b;
				}
				else {
					base = (EntityBase) a;
				}
				base.getEntity().alterGround(-1);
			}
		}
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		if (isValid(contact)) {
			Physical a = (Physical) contact.getFixtureA().getUserData(),
					 b = (Physical) contact.getFixtureB().getUserData();
	
			if (isEntity(a) && isEntity(b)) {
				contact.setEnabled(false);
			}
		}
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
	}

	private boolean isValid(Contact contact) {
		return contact.getFixtureA().getUserData() instanceof Physical
			&& contact.getFixtureB().getUserData() instanceof Physical;
	}

	private boolean isObject(Object b) {
		return b instanceof AbstractObject;
	}

	private boolean isGround(Object o) {
		if (o == null) {
			return false;
		}
		return o.equals(Level.GROUND_MARKER);
	}

	private boolean isEntityBase(Object o) {
		return o instanceof EntityBase;
	}

	private boolean isGoal(Physical b) {
		return b instanceof Goal;
	}

	private boolean isPortal(Physical b) {
		return b instanceof Portal;
	}

	private boolean isFatal(Physical b) {
		return b instanceof Fatal;
	}

	private boolean isObject(Physical b) {
		return b instanceof AbstractObject;
	}

	private boolean isOperable(Physical b) {
		return b instanceof Operable;
	}

	private boolean isPlayer(Physical a) {
		return a instanceof Spirit || (game.getSpirit().isPossessing(a));
	}

	private boolean isEntity(Physical a) {
		return a instanceof AbstractEntity;
	}
}