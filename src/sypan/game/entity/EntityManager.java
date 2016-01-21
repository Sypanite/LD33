package sypan.game.entity;

import java.util.ArrayList;

import org.jbox2d.common.Vec2;

import sypan.client.Client;
import sypan.game.Game;
import sypan.game.entity.type.EntityType;
import sypan.game.entity.type.GenericEntity;
import sypan.game.entity.type.player.Spirit;
import sypan.game.physics.PhysicsManager;
import sypan.utility.Logger;
import sypan.utility.Vector2i;

/**
 * @author Carl Linley
 **/
public class EntityManager {

	// Used in creating entities
	private final Class<?>[] CONSTRUCTOR = new Class<?>[] {int.class, Game.class, EntityType.class};

	private Game game;

	private ArrayList<AbstractEntity> entityList, removeQueue;
	private int uniqueEntityIndex;

	private Spirit playerEntity;

	public EntityManager(Game game) {
		this.game = game;

		entityList = new ArrayList<AbstractEntity>();
		removeQueue = new ArrayList<AbstractEntity>();

		(new Thread(new EntityLogicUpdateThread(this), "EntityLogicUpdateThread")).start();
		Logger.logInfo("EntityManager initialised. " + EntityType.values().length + " entities are defined.");
	}

	public AbstractEntity addEntity(AbstractEntity entity) {
		entityList.add(entity);
		return entity;
	}

	/**
	 * Creates a new entity using the specified data.
	 * 
	 * @param entityType - the entity's type.
	 * @param spawnPosition - the entity's spawn position, in tiles.
	 * @return the new entity.
	 **/
	public AbstractEntity createEntity(EntityType entityType, Vector2i spawnTile) {
		AbstractEntity newEntity = null;

		try {
			Class<? extends AbstractEntity> entityClass = entityType.getEntityClass();

			newEntity = entityClass.getDeclaredConstructor(CONSTRUCTOR).newInstance(uniqueEntityIndex ++, game, entityType);

			if (entityClass == GenericEntity.class) {
				Logger.logWarning("Using GenericEntity - EntityType:" + entityType + ".");
			}
		}
		catch (Exception e) { // Too many exceptions to care
			Logger.logSevere("Error creating entity: " + e + ".");
			e.printStackTrace();
		}
		if (newEntity instanceof Spirit) {
			playerEntity = (Spirit) newEntity;
		}
		game.getPhysicsManager().createEntity(newEntity, spawnTile.toPhysics());
		newEntity.initialise();
		return addEntity(newEntity);
	}

	public void update(float timePerFrame) {
		for (AbstractEntity entity : entityList) {
			if (entity.isAlive()) {
				if (entity.isMoving() && !entity.inDialogue()) {
					Vec2 moveVector = new Vec2();
					float moveAmount = (PhysicsManager.PHYS_TICK * timePerFrame) * entity.getType().getMoveSpeed();
	
					if (entity.isSpirit() && !game.getSpirit().isPossessing()) {
						float ectoCost = moveAmount / 200;
	
						if (game.getSpirit().getEctoplasm() < ectoCost) {
							game.displayMessage("You do not have enough ectoplasm to move!");
							game.getSpirit().stop();
							return;
						}
						game.getSpirit().alterEctoplasm(-ectoCost);
					}
	
					if (!entity.hasJumped()) {
						if (entity.isMoving(Direction.LEFT)) {
							moveVector.x = -moveAmount;
						}
						if (entity.isMoving(Direction.RIGHT)) {
							moveVector.x = moveAmount;
						}
		
						if (entity.canFly()) {
							if (entity.isMoving(Direction.UP)) {
								moveVector.y = moveAmount;
							}
							else if (entity.isMoving(Direction.DOWN)) {
								moveVector.y = -moveAmount;
							}
						}
						else if (entity.isFalling()) {
							return;
						}
						entity.getPhysics().applyForceToCenter(moveVector.mul(1000000000));
					}
				}
				else {
					if (entity.canFly() && !entity.getPhysics().getLinearVelocity().equals(Client.ZERO)) {
						entity.getPhysics().setLinearVelocity(Client.ZERO);
					}
					if (entity instanceof Spirit) {
						game.getSpirit().alterEctoplasm(0.01f);
					}
				}
			}
			else { // Destroy
				removeQueue.add(entity);
			}
		}
		for (AbstractEntity e : removeQueue) {
			entityList.remove(e);
			game.getPhysicsManager().remove(e);
			game.updateStats();
		}
		if (!removeQueue.isEmpty()) {
			removeQueue.clear();
		}
	}

	protected void tick() {
		for (AbstractEntity e : entityList) {
			e.logicUpdate();
			e.postUpdate();
		}
	}

	public ArrayList<AbstractEntity> getEntities() {
		return entityList;
	}

	public Spirit getPlayer() {
		return playerEntity;
	}

	public boolean isRunning() {
		return game.isRunning();
	}

	public int count() {
		return entityList.size();
	}

	public int count(Class<? extends AbstractEntity> baseType) {
		int count = 0;

		for (AbstractEntity e : entityList) {
			if (e.getClass() == baseType) {
				count ++;
			}
		}
		return count;
	}

	public void clear() {
		for (AbstractEntity e : entityList) {
			e.destroy();
		}
		entityList.clear();
		playerEntity = null;
		uniqueEntityIndex = 0;
	}
}
class EntityLogicUpdateThread implements Runnable {

	private EntityManager entityManager;

	protected EntityLogicUpdateThread(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public void run() {
		while(entityManager.isRunning()) {
			entityManager.tick();

			try {
				Thread.sleep(500);
			}
			catch(InterruptedException e) {
				Logger.logSevere("EntityLogicUpdateThread interrupted: " + e);
				e.printStackTrace();
			}
		}
		Logger.logInfo("EntityLogicUpdateThread destroyed.");
	}
}