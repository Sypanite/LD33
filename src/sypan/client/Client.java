package sypan.client;

import org.jbox2d.callbacks.DebugDraw;
import org.jbox2d.callbacks.QueryCallback;
import org.jbox2d.collision.AABB;
import org.jbox2d.common.OBBViewportTransform;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Fixture;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.BigImage;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import sypan.client.audio.AudioManager;
import sypan.client.audio.AudioManager.MusicType;
import sypan.client.sprite.SpriteManager;
import sypan.client.world.TileManager;
import sypan.game.Game;
import sypan.game.Renderable;
import sypan.game.entity.AbstractEntity;
import sypan.game.entity.Direction;
import sypan.game.entity.type.player.Spirit;
import sypan.game.level.Level;
import sypan.game.level.TileType;
import sypan.game.object.AbstractObject;
import sypan.utility.Logger;
import sypan.utility.Vector2i;

/**
 * @author Carl Linley
 **/
public class Client extends BasicGame {

	// Constants, constants, constants!
	private final static Vec2 CAMERA_OFFSET_WU = new Vec2(0, 200),
							  SPEECH_OFFSET_WU = new Vec2(0, 50);

	protected final static boolean DEBUG_MODE = false;
	private final static float TARGET_FRAMERATE = 60;
	private final static float TILE_SIZE = (Level.HALF_TILE_WU * 2.5f), TILE_SIZE_OFFSET = (TILE_SIZE / 2);
	private final static int RENDER_DISTANCE = 20;
	public static final Vec2 ZERO = new Vec2();

	// Game logic
	private Game game;

	// Synchronising
	private float timePerFrame;

	private AudioManager audioManager;

	// Drawing
	private GUIManager guiManager;
	private SpriteManager spriteManager;
	private TileManager tileManager;
	private BigImage background;
	private Image possessedGlow;

	// Rendering
	private DebugDraw debugDraw;
	private OBBViewportTransform viewport;
	protected Vec2 screenPositionStore;

	// Picking
	protected Vec2 mousePosition_world;
	protected AbstractEntity hoverEntity;
	private boolean fixtureFound;

	public static void main(String[] args) {
		Logger.init();

		try {
			AppGameContainer appgc;
			appgc = new AppGameContainer(new Client());
			appgc.setDisplayMode(1000, 800, false);
			appgc.setIcons(new String[] {"resources/ICON_64.png", "resources/ICON_32.png"});
			appgc.setShowFPS(DEBUG_MODE);
			appgc.setVSync(true);
			appgc.setAlwaysRender(true);
			appgc.start();
		}
		catch (SlickException ex) {
		}
	}

	public Client() {
		super("Dominion");
	}

	@Override
	public void init(GameContainer gameContainer) throws SlickException {
		spriteManager = new SpriteManager();
		guiManager = new GUIManager(this);
		audioManager = new AudioManager();
		audioManager.playMusic(MusicType.GAME);

		game = new Game(audioManager);
		game.loadLevel();

		tileManager = new TileManager();

		if (DEBUG_MODE) {
			debugDraw = new Slick2DJBox2DDebugDraw(gameContainer);
			debugDraw.setFlags(DebugDraw.e_shapeBit);
			game.getPhysicsManager().getSimulation().setDebugDraw(debugDraw);
			viewport = (OBBViewportTransform) debugDraw.getViewportTranform();
		}
		else {
			viewport = new OBBViewportTransform();
			viewport.setYFlip(true);
			viewport.setExtents(gameContainer.getWidth() / 2, gameContainer.getHeight() / 2);
		}
		screenPositionStore = new Vec2();
		mousePosition_world = new Vec2();

		background = new BigImage("resources/background.png");
		possessedGlow = new Image("resources/possessed_glow.png");
	}

	/**
	 * delta = milliseconds since last call.
	 **/
	@Override
	public void update(GameContainer gameContainer, int delta) throws SlickException {
		timePerFrame = delta / TARGET_FRAMERATE;

		if (game.changingLevel()) {
			game.loadLevel();
		}
		else {
			if (tileManager.getLevel() != game.getLevel()) {
				tileManager.buildArray(game.getLevel());
				Logger.logInfo("TileManager: built world.");
			}
	
			game.update(timePerFrame);
	
			if (game.hasMessage()) {
				guiManager.displayMessage(game.getDisplayMessage());
			}
			if (!game.gameOver() && game.getSpirit().isMoving()) {
				mouseMoved(0, 0, gameContainer.getInput().getMouseX(), gameContainer.getInput().getMouseY());
			}
		}
	}

	@Override
	public void render(GameContainer gameContainer, Graphics g) throws SlickException {
		if (!game.gameOver()) {
			// Update the viewport
			if (!game.movingPlayer()) { // Stops the camera jump bug (1.0)
				viewport.setCenter(game.getPlayerControlledEntity().getPosition().add(CAMERA_OFFSET_WU));
			}

			// Draw the background
			background.draw();

			// Render the proximate world
			Vector2i centralTile = (game.movingPlayer() ? game.getMoveTile() : game.getPlayerControlledEntity().getTile());

			for (int x = centralTile.getX() - RENDER_DISTANCE; x != centralTile.getX() + RENDER_DISTANCE; x++) {
				for (int y = centralTile.getY() - RENDER_DISTANCE; y != centralTile.getY() + RENDER_DISTANCE; y++) {
					drawTile(x, y, g);
				}
			}
			for (int x = centralTile.getX() - RENDER_DISTANCE; x != centralTile.getX() + RENDER_DISTANCE; x++) {
				for (int y = centralTile.getY() - RENDER_DISTANCE; y != centralTile.getY() + RENDER_DISTANCE; y++) {
					drawTile(x, y, g);
				}
			}

			// Render physics debug information, if applicable
			if (DEBUG_MODE) {
				game.getPhysicsManager().getSimulation().drawDebugData();
			}

			// Render objects
			for (AbstractObject o : game.getObjectManager().getObjects()) {
				draw(o, g);
			}

			// Render entities
			for (AbstractEntity e : game.getEntityManager().getEntities()) {
				if (!game.getSpirit().isPossessing(e)) {
					draw(e, g);
				}

				if (e.getSpeech() != null) {
					int speechLength = e.getSpeech().length();
					Vec2 speechPosition = e.getPosition().add(SPEECH_OFFSET_WU);
	
					speechPosition.x -= speechLength * 4;
	
					guiManager.drawString(e.getSpeech(), speechPosition, Color.yellow, g);
				}
			}

			if (game.getSpirit().isPossessing()) {
				draw((Renderable) game.getSpirit().getControlling(), g);
			}
		}

		// Check if there's anything under the mouse
		updatePicking(gameContainer);

		// Render the GUI
		guiManager.render(gameContainer, g);
	}

	private void drawTile(int x, int y, Graphics g) {
		viewport.getWorldToScreen(new Vec2(x * TILE_SIZE - TILE_SIZE_OFFSET, y * -TILE_SIZE + TILE_SIZE_OFFSET), screenPositionStore);

		if (game.getLevel().inBounds(x, y)) {
			tileManager.getTile(x, y).draw((int) screenPositionStore.x, (int) screenPositionStore.y);
		}
		else {
			g.drawImage(tileManager.getTile(TileType.GROUND), screenPositionStore.x, screenPositionStore.y);
		}
	}

	/**
	 * Draws the specified {@code Renderable} at its world location.
	 **/
	private void draw(Renderable toRender, Graphics g) {
		viewport.getWorldToScreen(toRender.getPosition(), screenPositionStore);

		if (!game.getSpirit().isControlled()) {
			if (game.getSpirit().getControlling() == toRender) {
				possessedGlow.drawCentered((int) screenPositionStore.x, (int) screenPositionStore.y);
			}
			if (toRender instanceof Spirit) {
				return;
			}
		}
		spriteManager.get(toRender, timePerFrame).drawCentered((int) screenPositionStore.x, (int) screenPositionStore.y);
	}

	/*
	 * Input
	 */

	@Override
	public void mousePressed(int button, int x, int y) {
		if (game.gameOver()) {
			return;
		}
		if (game.getSpirit().inDialogue() && guiManager.getHoverOption() >= 0 && guiManager.getHoverOption() <= 3) {
			game.getDialogueManager().selectAnswer(game.getSpirit().getDialogue(), guiManager.getHoverOption());
		}
		else {
			if (hoverEntity != null && !hoverEntity.isControlled()) {
				game.tryPossess(hoverEntity);
			}
			else {
				game.escape();
			}
		}
	}

	@Override
	public void mouseMoved(int oldX, int oldY, int newX, int newY) {
		guiManager.mouseMoved(newX, newY);
	}

	@Override
	public void keyPressed(int keyCode, char charPressed) {
		if (game.gameOver() && keyCode != Input.RESTART) {
			return;
		}
		switch(keyCode) {
			case 203:
			case Input.MOVE_LEFT:
				if (game.gameOver()) {
					return;
				}
				game.getPlayerControlledEntity().setMoving(Direction.LEFT, true);
			break;

			case 205:
			case Input.MOVE_RIGHT:
				game.getPlayerControlledEntity().setMoving(Direction.RIGHT, true);
			break;

			case 200:
			case Input.MOVE_UP:
				game.getPlayerControlledEntity().setMoving(Direction.UP, true);
			break;

			case 208:
			case Input.MOVE_DOWN:
				game.getPlayerControlledEntity().setMoving(Direction.DOWN, true);
			break;

			case Input.JUMP:
				game.getPlayerControlledEntity().jump();
			break;

			case 29:
			case 157:
			case Input.OPERATE:
				if (game.getSpirit().getProximateOperable() != null && game.getSpirit().isPossessing()) {
					game.getSpirit().getProximateOperable().onOperation();
					return;
				}
				if (hoverEntity != null && !hoverEntity.isControlled()) {
					game.tryPossess(hoverEntity);
				}
				else {
					game.escape();
				}
			break;

			case Input.RESTART:
				game.restartLevel();
			break;
		}
	}

	@Override
	public void keyReleased(int keyCode, char charReleased) {
		if (game.gameOver()) {
			return;
		}

		switch(keyCode) {
			case 203:
			case Input.MOVE_LEFT:
				game.getPlayerControlledEntity().setMoving(Direction.LEFT, false);
			break;

			case 205:
			case Input.MOVE_RIGHT:
				game.getPlayerControlledEntity().setMoving(Direction.RIGHT, false);
			break;

			case 200:
			case Input.MOVE_UP:
				game.getPlayerControlledEntity().setMoving(Direction.UP, false);
			break;

			case 208:
			case Input.MOVE_DOWN:
				game.getPlayerControlledEntity().setMoving(Direction.DOWN, false);
			break;
		}
	}

	@Override
	public boolean closeRequested() {
		game.setRunning(false);
		System.exit(0);
		return false;
	}

	private void updatePicking(GameContainer gameContainer) {
		mousePosition_world = new Vec2();
		viewport.getScreenToWorld(new Vec2(gameContainer.getInput().getMouseX(), gameContainer.getInput().getMouseY()), mousePosition_world);

		QueryCallback queryCallback = new QueryCallback() {

			@Override
			public boolean reportFixture(Fixture fixture) {
				if (fixture.getUserData() instanceof AbstractEntity
				 && !(fixture.getUserData() instanceof Spirit)) {
					hoverEntity = (AbstractEntity) fixture.getUserData();

					if (hoverEntity.isControlled()) {
						hoverEntity = null;
					}
					else {
						fixtureFound = true;
					}
				}
				return false;
			}
		};

		game.getPhysicsManager().getSimulation().queryAABB(queryCallback, new AABB(mousePosition_world, mousePosition_world));

		if (!fixtureFound) {
			hoverEntity = null;
		}
		fixtureFound = false;
	}

	public SpriteManager getSpriteManager() {
		return spriteManager;
	}

	public Game getGame() {
		return game;
	}

	public OBBViewportTransform getViewport() {
		return viewport;
	}

	public float getTimePerFrame() {
		return timePerFrame;
	}
}