package sypan.game.entity.type.npc;

import java.util.Random;

import sypan.game.Game;
import sypan.game.entity.AbstractEntity;
import sypan.game.entity.Direction;
import sypan.game.entity.type.EntityType;
import sypan.game.entity.type.possession.Controllable;
import sypan.game.entity.type.possession.Personality;
import sypan.utility.Vector2i;

/**
 * @author Carl Linley
 **/
public class BaseCriminal extends AbstractEntity implements Controllable {

	private Random random;

	private Personality personality;
	private String name;

	private Thread idleChatterThread;

	public BaseCriminal(int index, Game game, EntityType entityType) {
		super(index, game, entityType);

		personality = Personality.random();
		name = NameList.randomCriminal();
		random = new Random();
	}

	@Override
	public void initialise() {
		idleChatterThread = new Thread(new IdleChatterThread(getGame(), this), toString() + ":IdleChatterThread");
		idleChatterThread.start();
	}

	@Override
	public void logicUpdate() {
		if (isControlled() || inDialogue()) {
			return;
		}

		if (distanceToPlayer() < 3 && getGame().getSpirit().isPossessing()
		&& !getGame().getSpirit().isCooling()) {
			if (!getGame().getSpirit().inDialogue() && random.nextInt(5) == 0) {
				getGame().getDialogueManager().startDialogue(this);
			}
		}

		int roamChance = random.nextInt(10);

		if (roamChance == 0) {
			idleRoam();
		}
		else if (roamChance == 9 && isMoving()) {
			stop();
		}
		if (isMoving() && !canMove()) {
			stop();
		}
	}

	@Override
	public void destroy() {
		idleChatterThread.interrupt();
	}

	private int distanceToPlayer() {
		return getTile().distance(getGame().getSpirit().getTile());
	}

	private boolean canMove() {
		int dir = (isMoving(Direction.LEFT) ? -1 : 1);

		Vector2i nextHead = getTile().add(dir, 0),
				 nextLegs = nextHead.add(0, 1);

		return !(getGame().getLevel().isClipped(nextHead) || getGame().getLevel().isClipped(nextLegs));
	}

	/**
	 * Very basic roaming
	 **/
	private void idleRoam() {
		Direction d = Direction.values()[random.nextInt(2)];

		setMoving(d, true);	

		if (!canMove()) {
			setMoving(d, false);
		}
	}

	@Override
	public Personality getPersonality() {
		return personality;
	}

	@Override
	public String getName() {
		return name;
	}
}