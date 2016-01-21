package sypan.game;

import sypan.client.audio.AudioManager;
import sypan.client.audio.AudioManager.SoundType;
import sypan.game.entity.AbstractEntity;
import sypan.game.entity.EntityManager;
import sypan.game.entity.dialogue.DialogueManager;
import sypan.game.entity.dialogue.IdleChatter;
import sypan.game.entity.type.npc.BaseCivilian;
import sypan.game.entity.type.npc.BaseCriminal;
import sypan.game.entity.type.player.Spirit;
import sypan.game.entity.type.possession.Controllable;
import sypan.game.entity.type.possession.Personality;
import sypan.game.level.Level;
import sypan.game.object.ObjectManager;
import sypan.game.physics.PhysicsManager;
import sypan.utility.Logger;
import sypan.utility.Vector2i;

/**
 * @author Carl Linley
 **/
public class Game {

	private final static String[] LEVELS = {"Stonefell", "Norsebro", "Lanswod"};

	private Level level;
	private int currentLevel = 0;

	private DialogueManager dialogueManager;
	private EntityManager entityManager;
	private ObjectManager objectManager;
	private PhysicsManager physicsManager;
	private AudioManager audioManager;
	private GameTimer gameTimer;

	private String showMessage;
	private Vector2i movePlayer;

	private boolean isRunning = true, gameOver, changingLevel;
	private int criminalCount, initialCriminals,
				civilianCount, initialCivilians;

	public Game(AudioManager audioManager) {
		this.audioManager = audioManager;

		entityManager = new EntityManager(this);
		objectManager = new ObjectManager(this);
		dialogueManager = new DialogueManager(this);
		physicsManager = new PhysicsManager(this);

		gameTimer = new GameTimer(this);
		new Thread(gameTimer, "GameTimer").start();
	}

	public void loadLevel() {
		if (currentLevel < LEVELS.length) {
			changingLevel = true;
			load(LEVELS[currentLevel]); 
			changingLevel = false;
		}
		else {
			changingLevel = false;
			Logger.logInfo("Game over!");
			displayMessage("Nice, you beat the game! Thanks for playing. - Sypanite");
		}
	}

	private void load(String levelName) {
		gameTimer.setTime(120);
		level = new Level(this, levelName);
		level.initPhysics();

		updateStats();
		initialCriminals = criminalCount;
		initialCivilians = civilianCount;
		displayMessage("Level " + (currentLevel + 1) + " - " + levelName);
		changingLevel = false;
		gameOver = false;
	}

	private void stop() { // Stops the game
		gameOver = true;
		physicsManager.clear();
		entityManager.clear();
		objectManager.clear();
	}

	public void update(float timePerFrame) {
		if (movePlayer != null) {
			getSpirit().setTile(movePlayer);
			movePlayer = null;
		}
		physicsManager.update(timePerFrame);
		entityManager.update(timePerFrame);
	}

	public boolean tryPossess(AbstractEntity hoverEntity) {
		if (hoverEntity instanceof Controllable) {
			Personality personality = ((Controllable) hoverEntity).getPersonality();

			if (personality.getDurability() < 0.3f) { // Too weak
				possess((Controllable) hoverEntity);
			}
			/*else if (personality.getDurability() > 0.9f) { // Too strong
				displayMessage(hoverEntity.getName() + "'s will is too strong to be possessed!");
				return false;
			}*/
			else {
				possess((Controllable) hoverEntity);
			}
			return true;
		}
		return false;
	}

	public void displayMessage(String showMessage) {
		this.showMessage = showMessage;
	}

	private void possess(Controllable possessEntity) {
		possessEntity.say(IdleChatter.randomPossessed());
		possessEntity.stop();
		getSpirit().setControlling(possessEntity);
		audioManager.playSound(SoundType.POSSESS);
	}

	public void loadNextLevel() {
		stop();

		if (currentLevel < LEVELS.length) {
			Logger.logInfo("Level passed!");
			audioManager.playSound(SoundType.LEVEL_COMPLETE);
			currentLevel++;
			changingLevel = true;
		}
	}

	public void restartLevel() {
		stop();
		changingLevel = true;
	}

	public void failLevel(int endCode) {// 0 = dissipated
		audioManager.playSound(SoundType.LEVEL_FAILED);
		stop();

		switch(endCode) {
			case 0:
				displayMessage("Time ran out, and your spirit dissipated. Press R to restart.");
			break;
		}
	}

	public void escape() {
		if (!getSpirit().isPossessing()) {
			return;
		}

		movePlayer = getSpirit().getControlling().getTile();

		if (getSpirit().inDialogue()) {
			dialogueManager.alertAndDie(getSpirit().getDialogue());
			getSpirit().setControlling(null);
		}
		else {
			AbstractEntity oldBody = (AbstractEntity) getSpirit().getControlling();

			getSpirit().setControlling(null);
			oldBody.stop();
			oldBody.say(IdleChatter.randomEscape());
		}
		audioManager.playSound(SoundType.ESCAPE);
	}

	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
		Logger.logInfo("Game shutdown.");
	}

	public boolean isRunning() {
		return isRunning;
	}

	public boolean gameOver() {
		return gameOver;
	}

	public Level getLevel() {
		return level;
	}

	public DialogueManager getDialogueManager() {
		return dialogueManager;
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public AbstractEntity getPlayerControlledEntity() {
		return (AbstractEntity) getSpirit().getControlling();
	}

	public Spirit getSpirit() {
		return entityManager.getPlayer();
	}

	public PhysicsManager getPhysicsManager() {
		return physicsManager;
	}

	public ObjectManager getObjectManager() {
		return objectManager;
	}

	public int getTimeRemaining() {
		return gameTimer.getTime();
	}

	public AudioManager getAudioManager() {
		return audioManager;
	}

	public String getDisplayMessage() {
		String showMessage = this.showMessage;
		this.showMessage = null;
		return showMessage;
	}

	public void updateStats() {
		criminalCount = entityManager.count(BaseCriminal.class);
		civilianCount = entityManager.count(BaseCivilian.class);
	}

	public int currentCriminals() {
		return criminalCount;
	}

	public int currentCivilians() {
		return civilianCount;
	}

	public int initialCriminals() {
		return initialCriminals;
	}

	public int initialCivilians() {
		return initialCivilians;
	}

	public boolean hasMessage() {
		return showMessage != null;
	}

	public boolean changingLevel() {
		return changingLevel;
	}

	public boolean movingPlayer() {
		return movePlayer != null;
	}

	public Vector2i getMoveTile() {
		return movePlayer;
	}
}
class GameTimer implements Runnable {

	private int timeLeftSeconds;

	private Game game;

	GameTimer(Game game) {
		timeLeftSeconds = 120;
		this.game = game;

		Logger.logInfo("GameTimer initialised.");
	}

	@Override
	public void run() {
		while(game.isRunning()) {
			try {
				Thread.sleep(1000);

				if (timeLeftSeconds > 0 && !game.gameOver()) {
					timeLeftSeconds --;

					if (timeLeftSeconds == 0) {
						game.failLevel(0);
					}
					else if (timeLeftSeconds < 10) {
						game.getAudioManager().playSound(SoundType.TIME);
					}
				}
			}
			catch (InterruptedException e) {
			}
		}
	}

	public void setTime(int timeLeftSeconds) {
		this.timeLeftSeconds = timeLeftSeconds;
	}

	public int getTime() {
		return timeLeftSeconds;
	}
}