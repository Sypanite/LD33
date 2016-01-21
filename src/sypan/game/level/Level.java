package sypan.game.level;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import sypan.game.Game;
import sypan.game.entity.type.EntityType;
import sypan.game.object.AbstractObject;
import sypan.game.object.ObjectType;
import sypan.game.physics.PhysicsShape;
import sypan.utility.Logger;
import sypan.utility.Vector2i;

/**
 * @author Carl Linley
 **/
public class Level {

	enum LoadType {TILE_MAP, OBJECT_LIST, ENTITY_LIST, END};

	public final static char GROUND_MARKER = 'G';

	public final static float TILE_WU = 25f,
							  HALF_TILE_WU = TILE_WU / 2,
							  TILE_OFFSET = (HALF_TILE_WU * 2.5f);

	private final Shape[] lineType;

	private Game game;
	private LoadType loadType;

	private byte[][] tileMap;
	private Vec2 spawnPoint;

	private Vector2i levelSize;

	public Level(Game game, String levelName) {
		this.game = game;

		// Edges
		lineType = new Shape[4];
		lineType[0] = PhysicsShape.LINE(new Vec2(-HALF_TILE_WU, -HALF_TILE_WU), new Vec2(HALF_TILE_WU, -HALF_TILE_WU));
		lineType[1] = PhysicsShape.LINE(new Vec2(-HALF_TILE_WU, HALF_TILE_WU), new Vec2(HALF_TILE_WU, HALF_TILE_WU));
		lineType[2] = PhysicsShape.LINE(new Vec2(-HALF_TILE_WU, -HALF_TILE_WU), new Vec2(-HALF_TILE_WU, HALF_TILE_WU));
		lineType[3] = PhysicsShape.LINE(new Vec2(HALF_TILE_WU, -HALF_TILE_WU), new Vec2(HALF_TILE_WU, HALF_TILE_WU));

		loadMap(levelName);
		initPhysics();
	}

	/**
	 * Loads CSV
	 **/
	private void loadMap(String levelName) {
		long start = System.currentTimeMillis();
		Random random = new Random();

		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File("data/level/" + levelName + ".csv")));
			String line;
			String[] tokens = null;
			int y = 0;

			while ((line = reader.readLine()) != null) {
				if (line.startsWith("/")) {
					loadType = LoadType.valueOf(line.substring(1));
					Logger.logInfo("Loading " + loadType + "...");
				}
				else {
					tokens = line.split(",");

					switch(loadType) {
						case END:
							reader.close();
						break;
	
						case ENTITY_LIST:
							EntityType entityType = null;

							switch(tokens[0]) {
								case "PLAYER":
									entityType = EntityType.SPIRIT;
								break;

								case "CRIMINAL":
									entityType = EntityType.values()[1 + random.nextInt(5)];
								break;

								case "CIVILIAN":
									entityType = EntityType.values()[6 + random.nextInt(5)];
								break;

								default:
									try {
										entityType = EntityType.valueOf(tokens[0]);
									}
									catch(Exception e) {
									}
								break;
							}

							if (entityType != null) {
								game.getEntityManager().createEntity(entityType, new Vector2i(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2])));
							}
							else {
								Logger.logWarning("Invalid entity in level file: '" + tokens[0] + "'.");
							}
						break;
		
						case OBJECT_LIST:
							game.getObjectManager().createObject(ObjectType.valueOf(tokens[0]), new Vector2i(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2])));
						break;
		
						case TILE_MAP:
							if (tokens[0].equals("SIZE")) {
								levelSize = new Vector2i(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]));
								tileMap = new byte[getWidth()][getHeight()];
								Logger.logInfo("Size: " + levelSize + ".");
							}
							else {
								for (int x = 0; x != getWidth(); x++) {
									setTile(x, y, Byte.parseByte(tokens[x]));
								}
								y++;
							}
						break;
					}
				}
			}
			Logger.logInfo("Loaded level '" + levelName + "' in " + (System.currentTimeMillis() - start) + "ms.");
		}
		catch (IOException e) {
			Logger.logSevere("Error loading level '" + levelName + "': " + e + ".");
			e.printStackTrace();
		}
	}

	public void initPhysics() {
		World world = game.getPhysicsManager().getSimulation();

		for (int x = 0; x != tileMap.length; x++) {
			for (int y = 0; y != tileMap[0].length; y++) {
				if (getTile(x, y) != null && getTile(x, y).isClipped()) {
					for (int i = 0; i != 4; i++) {
						if (!checkAdjacentTile(x, y, i)) {
							BodyDef bodyDef = new BodyDef();
							bodyDef.setType(BodyType.STATIC);
							bodyDef.setPosition(new Vec2(x * TILE_OFFSET, -y * TILE_OFFSET));

							FixtureDef fixtureDef = new FixtureDef();
							fixtureDef.setShape(lineType[i]);

							if (i == 1) { // This tile is ground
								fixtureDef.setUserData(GROUND_MARKER);
							}

							Body body = world.createBody(bodyDef);
							body.createFixture(fixtureDef);
						}
					}
				}
			}	
		}
	}

	/**
	 * Used to only add tiles to the physics engine if necessary.
	 **/
	private boolean checkAdjacentTile(int x, int y, int checkAdjacent) {
		switch(checkAdjacent) {
			case 0: // Below
				return getTile(x, y + 1).isClipped();

			case 1: // Above
				return getTile(x, y - 1).isClipped();

			case 2: // Left
				return getTile(x - 1, y).isClipped();

			case 3: // Right
				return getTile(x + 1, y).isClipped();
		}
		return false;
	}

	protected void initMap(int width, int height) {
	}

	protected void setTile(TileType tileType, int x, int y) {
		if (!inBounds(x, y)) {
			return;
		}
		tileMap[x][y] = (byte) tileType.ordinal();
	}

	private void setTile(int x, int y, byte parsedByte) {
		tileMap[x][y] = parsedByte;
	}

	public TileType getTile(int x, int y) {
		if (inBounds(x, y)) {
			return TileType.values()[tileMap[x][y]];
		}
		return TileType.GROUND;
	}

	public boolean inBounds(int x, int y) {
		return x >= 0 && y >= 0 && x < getWidth() && y < getHeight();
	}

	public Vec2 getSpawn() {
		return spawnPoint;
	}

	public int getWidth() {
		return levelSize.getX();
	}

	public int getHeight() {
		return levelSize.getY();
	}

	public boolean isClipped(Vector2i checking) {
		if (getTile(checking.getX(), checking.getY()) == null) {
			return false;
		}
		if (!getTile(checking.getX(), checking.getY()).isClipped()) { // Are there any clipped objects in this tile?
			AbstractObject o = game.getObjectManager().getObject(checking.getX(), checking.getY());

			return (o != null && o.isClipped());
		}
		return true;
	}

	public TileType getTile(Vector2i t) {
		return getTile(t.getX(), t.getY());
	}
}