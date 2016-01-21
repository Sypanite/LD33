package sypan.game.entity.type.player;

import org.jbox2d.common.Vec2;

import sypan.game.Game;
import sypan.game.entity.AbstractEntity;
import sypan.game.entity.type.EntityType;
import sypan.game.entity.type.possession.Controllable;
import sypan.game.entity.type.possession.Personality;
import sypan.game.object.classification.Operable;
import sypan.utility.Logger;

/**
 * @author Carl Linley
 **/
public class Spirit extends AbstractEntity implements Controllable {

	private Operable proximateOperable;
	private Controllable controllingEntity;

	private float currentEctoplasm, // Ectoplasm drains when Player is moving in spirit form
				  currentKarma; // Karma is gained by getting criminals killed, and lost by getting civilians killed.

	private boolean isCooling;

	public Spirit(int index, Game game,EntityType entityType) {
		super(index, game, entityType);

		currentEctoplasm = 100;
		currentKarma = 0.5f;
	}

	@Override
	public void initialise() {
		getPhysics().setGravityScale(0); // No gravity, we're a ghost!
	}

	@Override
	public void logicUpdate() {
	}

	@Override
	public void jump() {
	}

	public void setProximateOperable(Operable proximateOperable) {
		this.proximateOperable = proximateOperable;
	}

	public void setControlling(Controllable controllingEntity) {
		if (controllingEntity == null) {
			this.controllingEntity = null;
			Logger.logInfo("Controlling entity: N/A");
		}
		else {
			this.controllingEntity = controllingEntity;
			Logger.logInfo("Controlling entity: " + controllingEntity);
		}
	}

	public Operable getProximateOperable() {
		return proximateOperable;
	}

	public Controllable getControlling() {
		if (controllingEntity == null) {
			return this;
		}
		return controllingEntity;
	}

	@Override
	public Vec2 getPosition() {
		if (controllingEntity == null) {
			return super.getPosition();
		}
		return controllingEntity.getPosition();
	}

	@Override
	public Personality getPersonality() {
		return null; // Player cannot possess themselves...
	}

	public boolean isPossessing() {
		return controllingEntity != null;
	}

	public boolean isPossessing(Object a) {
		return controllingEntity == a;
	}

	public float getEctoplasm() {
		return currentEctoplasm;
	}

	public void alterEctoplasm(float moveAmount) {
		currentEctoplasm += moveAmount;

		if (currentEctoplasm > 100) {
			currentEctoplasm = 100;
		}
		else if (currentEctoplasm < 0) {
			currentEctoplasm = 0;
		}
	}

	/**
	 * Prevent further confrontations for ten seconds, else it gets annoying.
	 **/
	public void cooldown() {
		isCooling = true;

		(new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(10000);
					isCooling = false;
				}
				catch (InterruptedException e) {
				}
			}
		}, "ConfrontationCooldownThread")).start();
	}

	public boolean isCooling() {
		return isCooling;
	}

	public void alterKarma(float alter) {
		currentKarma += alter;
	}

	public float getKarma() {
		return currentKarma;
	}
}