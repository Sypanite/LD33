package sypan.client.world;

import java.util.HashMap;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

import sypan.game.level.Level;
import sypan.game.level.TileType;
import sypan.utility.Logger;
import sypan.utility.Vector2i;

/**
 * @author Carl Linley
 **/
public class TileManager {

	public final static int TILE_SIZE_PIXELS = 32;

	private SpriteSheet terrainAtlas;
	private HashMap<TileType, Vector2i> baseAtlasLocations;

	private LinearisedXY[][] tileArray; // Stores atlas positions on a per-tile basis.
	private Level level;

	public TileManager() {
		try {
			terrainAtlas = new SpriteSheet("resources/world/terrain_atlas.png", TILE_SIZE_PIXELS, TILE_SIZE_PIXELS);

			baseAtlasLocations = new HashMap<TileType, Vector2i>();
			registerTileList();

			Logger.logInfo("Initialised TileManager.");
		}
		catch (SlickException e) {
			Logger.logSevere("Failed to load terrain atlas.");
			e.printStackTrace();
		}
	}

	/**
	 * Must be called after the level has initialised.
	 **/
	public void buildArray(Level level) {
		this.level = level;
		tileArray = new LinearisedXY[level.getWidth()][level.getHeight()];

		for (int x = 0; x != level.getWidth(); x++) {
			for (int y = 0; y != level.getHeight(); y++) {
				if (level.getTile(x, y) == TileType.GROUND) {
					boolean hasAbove = false, hasBelow = false, hasLeft = false, hasRight = false;

					if (!level.getTile(x - 1, y).isClipped()) {
						hasLeft = true;
					}
					if (!level.getTile(x + 1, y).isClipped()) {
						hasRight = true;
					}
					if (!level.getTile(x, y - 1).isClipped()) {
						hasAbove = true;
					}
					if (!level.getTile(x, y + 1).isClipped()) {
						hasBelow = true;
					}
					tileArray[x][y] = calculate(hasAbove, hasLeft, hasRight, hasBelow);
				}
				else {
					tileArray[x][y] = null;
				}
			}	
		}
	}

	private LinearisedXY calculate(boolean hasAbove, boolean hasLeft, boolean hasRight, boolean hasBelow) {
		if (!hasAbove && !hasLeft && !hasRight && !hasBelow) {
			return null;
		}
		if (hasAbove && hasLeft && hasRight && hasBelow) {
			return new LinearisedXY(4, 2);
		}
		if (hasAbove && hasBelow && !hasLeft && !hasRight) {
			return new LinearisedXY(5, 2);
		}
		if (hasBelow && !hasAbove && !hasLeft && !hasRight) {
			return new LinearisedXY(0, 2);
		}
		if (hasRight && !hasAbove && !hasLeft && !hasBelow) {
			return new LinearisedXY(5, 0);
		}
		if (hasLeft && !hasAbove && !hasRight && !hasBelow) {
			return new LinearisedXY(5, 1);
		}
		if (hasAbove && !hasLeft && !hasRight && !hasBelow) {
			return new LinearisedXY(1, 0);
		}
		if (hasAbove && hasBelow && hasRight && !hasLeft) {
			return new LinearisedXY(2, 2);
		}
		if (hasAbove && hasBelow && hasLeft && !hasRight) {
			return new LinearisedXY(3, 2);
		}
		if (hasLeft && hasRight && !hasAbove && !hasBelow) {
			return new LinearisedXY(1, 2);
		}
		if (hasAbove && hasLeft && !hasRight && !hasBelow) {
			return new LinearisedXY(3, 0);
		}
		if (hasAbove && hasRight && !hasLeft && !hasBelow) {
			return new LinearisedXY(2, 0);
		}
		if (hasBelow && hasLeft && !hasRight && !hasAbove) {
			return new LinearisedXY(3, 1);
		}
		if (hasBelow && hasRight && !hasLeft && !hasAbove) {
			return new LinearisedXY(2, 1);
		}
		if (hasAbove && hasLeft && hasRight && !hasBelow) {
			return new LinearisedXY(4, 0);
		}
		if (hasBelow && hasRight && hasLeft && !hasAbove) {
			return new LinearisedXY(4, 1);
		}
		return new LinearisedXY(1, 1);
	}

	private void registerTileList() {
		registerTile(TileType.AIR, 0, 0);
		registerTile(TileType.GROUND, 1, 1);
		registerTile(TileType.BACKING, 0, 1);
	}

	private void registerTile(TileType tileType, int atlasX, int atlasY) {
		baseAtlasLocations.put(tileType, new Vector2i(atlasX, atlasY));
	}

	public Image getTile(int x, int y) {
		try {
			if (tileArray[x][y] != null) { // Not a varying tile
				return terrainAtlas.getSubImage(tileArray[x][y].getX(), tileArray[x][y].getY());
			}
			else {
				Vector2i baseAtlas = baseAtlasLocations.get(level.getTile(x, y));
	
				return terrainAtlas.getSubImage(baseAtlas.getX(), baseAtlas.getY());
			}
		}
		catch(Exception e) {
			return terrainAtlas.getSubImage(0, 0);
		}
	}

	public Image getTile(TileType base) {
		return terrainAtlas.getSprite(baseAtlasLocations.get(base).getX(), baseAtlasLocations.get(base).getY());
	}

	public Level getLevel() {
		return level;
	}
}
class LinearisedXY { // Premature optimisation

	private int linearisedXY;// Linearised X and Y coordinate.

	public LinearisedXY(int mouseX, int mouseY) {
		setX(mouseX);
		setY(mouseY);
	}

	private void setX(int mouseX) {
		linearisedXY = (linearisedXY & 0xFFFFFFFF | (mouseX << 16));
	}

	private void setY(int mouseY) {
		linearisedXY = (linearisedXY & 0xFFFF0000 | mouseY);
	}

	public int getX() {
		return (linearisedXY >> 16) & 0xFFFFFFFF;
	}

	public int getY() {
		return linearisedXY & 0x0000FFFF;
	}
}