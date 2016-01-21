package sypan.game.entity;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

import sypan.client.audio.AudioManager.SoundType;
import sypan.game.Game;
import sypan.game.Renderable;
import sypan.game.entity.dialogue.Dialogue;
import sypan.game.entity.type.EntityType;
import sypan.game.entity.type.npc.BaseCivilian;
import sypan.game.entity.type.player.Spirit;
import sypan.game.object.ObjectType;
import sypan.game.physics.Physical;
import sypan.game.physics.PhysicalData;
import sypan.utility.Logger;
import sypan.utility.Vector2i;

/**
 * Superclass of all entities.
 * 
 * @author Carl Linley
 **/
public abstract class AbstractEntity implements Physical, Renderable {

	private Game game;

	private int index;
	private boolean isAlive, hasJumped;
	private EntityType entityType;

	private AnimationType currentAnimation;
	private Dialogue currentDialogue;
	private Direction faceDirection;

	private boolean isMoving[]; // Left, Right, Up, Down
	private int touchingGround;

	private Body entityPhys;
	private String speech;

	public AbstractEntity(int index, Game game, EntityType entityType) {
		this.game = game;
		this.entityType = entityType;
		this.index = index;

		currentAnimation = AnimationType.IDLE;
		isAlive = true;

		isMoving = new boolean[4];
	}

	/**
	 * Called just after the entity is created.
	 **/
	public abstract void initialise();

	/**
	 * Called every 500ms.
	 **/
	public abstract void logicUpdate();

	/**
	 * Called when the entity is forcibly destroyed (e.g end game over).
	 **/
	public void destroy() {
	}

	/**
	 * Called just after logicUpdate().
	 **/
	public void postUpdate() {
	}

	/**
	 * Moves this entity to the specified tile.
	 **/
	public void setTile(Vector2i destination) {
		getPhysics().setTransform(destination.toPhysics(), 0);
	}

	public void stop() {
		for (int i = 0; i != 4; i++) {
			setMoving(Direction.values()[i], false);
		}
		currentAnimation = AnimationType.IDLE;
	}

	public void turn(Direction direction) {
		faceDirection = direction;

		if (isSpirit() && getGame().getSpirit().isPossessing()) {
			((AbstractEntity) getGame().getSpirit().getControlling()).turn(direction);
		}
	}

	/**
	 * Makes this entity jump... verrry slowly.
	 **/
	public void jump() {
		if (isFalling()) {
			return;
		}
		int x = 0;

		if (isMoving(Direction.LEFT)) {
			x = -30000;
		}
		else if (isMoving(Direction.RIGHT)) {
			x = 30000;
		}
		getPhysics().applyLinearImpulse(new Vec2(getPhysics().getMass() * x, getPhysics().getMass() * 300000), getPhysics().getWorldCenter(), true);
		hasJumped = true;
		game.getAudioManager().playSound(SoundType.JUMP);
	}

	/**
	 * Marks this entity as dead.
	 * @param spawnGrave - if true, a gravestone appears in its place.
	 **/
	public void kill(boolean spawnGrave) {
		if (!isAlive) {
			return;
		}
		isAlive = false;

		if (isControlled()) {
			game.escape();

			if (this instanceof BaseCivilian) {
				game.getSpirit().alterKarma(-0.1f);
			}
			else {
				game.getSpirit().alterKarma(0.1f);
			}
		}
		if (spawnGrave) {
			game.getObjectManager().createObject(ObjectType.GRAVE, getTile().add(0, 1));
		}
		if (inDialogue()) {
			game.getDialogueManager().endDialogue(game.getSpirit().getDialogue());
		}
		Logger.logInfo(this + " died!");
		game.getAudioManager().playSound(SoundType.SHOT);
	}

	public void setMoving(Direction moveDirection, boolean isMoving) {
		if (inDialogue() && isMoving) {
			return;
		}
		if (!canFly() && (moveDirection == Direction.UP || moveDirection == Direction.DOWN)) {
			return;
		}

		this.isMoving[moveDirection.ordinal()] = isMoving;

		// Avoid conflicts
		if (isMoving) {
			if (moveDirection == Direction.LEFT && isMoving(Direction.RIGHT)) {
				setMoving(Direction.RIGHT, false);
			}
			if (moveDirection == Direction.RIGHT && isMoving(Direction.LEFT)) {
				setMoving(Direction.LEFT, false);
			}
			if (moveDirection == Direction.UP && isMoving(Direction.DOWN)) {
				setMoving(Direction.DOWN, false);
			}
			if (moveDirection == Direction.DOWN && isMoving(Direction.UP)) {
				setMoving(Direction.UP, false);
			}
		}

		if (isMoving()) {
			if (!isPlayer()) {
				setAnimation(AnimationType.WALK);
			}
			faceDirection = calculateFace();
		}
		else {
			setAnimation(AnimationType.IDLE);
		}
	}

	public boolean isMoving() {
		for (Direction d : Direction.values()) {
			if (isMoving(d)) {
				return true;
			}
		}
		return false;
	}

	private Direction calculateFace() {
		if (isMoving(Direction.LEFT)) {
			return Direction.LEFT;
		}
		if (isMoving(Direction.RIGHT)) {
			return Direction.RIGHT;
		}
		return faceDirection;
	}

	/**
	 * Text!
	 **/
	public void say(String string) {
		if (game.getSpirit().isPossessing(this)) {
			return;
		}
		speech = string;

		(new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(1500);
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
				speech = null;
			}
		})).start();
	}

	public void alterGround(int i) {
		touchingGround += i;

		if (hasJumped && isFalling()) {
			hasJumped = false;
		}
	}

	private void setAnimation(AnimationType currentAnimation) {
		if (inDialogue()) {
			return;
		}
		this.currentAnimation = currentAnimation;
	}

	@Override
	public void setPhysics(Body entityPhys) {
		this.entityPhys = entityPhys;
	}

	public void setDialogue(Dialogue currentDialogue) {
		this.currentDialogue = currentDialogue;
		stop();

		if (isSpirit() && getGame().getSpirit().isPossessing()) {
			((AbstractEntity) getGame().getSpirit().getControlling()).setDialogue(currentDialogue);
		}
	}

	public boolean canFly() {
		return entityPhys.getGravityScale() == 0;
	}

	public boolean hasJumped() {
		return hasJumped;
	}

	public boolean inDialogue() {
		if (!isSpirit() && isControlled()) {
			return getGame().getSpirit().inDialogue();
		}
		return currentDialogue != null;
	}

	public boolean isControlled() {
		return getGame().getSpirit().getControlling() == this;
	}

	public boolean isMoving(Direction dir) {
		return isMoving[dir.ordinal()];
	}

	public boolean isAlive() {
		return isAlive;
	}

	public boolean isFalling() {
		return touchingGround == 0;
	}

	public boolean isPlayer() {
		return this instanceof Spirit;
	}

	public AnimationType getCurrentAnimation() {
		return currentAnimation;
	}

	@Override
	public PhysicalData getPhysicalData() {
		return entityType.getPhysicalData();
	}

	@Override
	public Body getPhysics() {
		return entityPhys;
	}

	@Override
	public int getIndex() {
		return index;
	}

	public Dialogue getDialogue() {
		return currentDialogue;
	}

	@Override
	public EntityType getType() {
		return entityType;
	}

	@Override
	public Vec2 getPosition() {
		return entityPhys.getPosition();
	}

	public Direction getFaceDirection() {
		return faceDirection;
	}

	public String getName() {
		return entityType.getName();
	}

	public Vector2i getTile() {
		return Vector2i.physicsToTile(getPosition());
	}

	public String getSpeech() {
		return speech;
	}

	public Game getGame() {
		return game;
	}

	@Override
	public String toString() {
		return "Entity:" + entityType + ":" + (int) getPosition().x + ", " + (int) getPosition().y;
	}

	public boolean isSpirit() {
		return getType() == EntityType.SPIRIT;
	}
}