package sypan.game.level;

/**
 * @author Carl Linley
 **/
public enum TileType {

	AIR(false),
	GROUND(true),
	BACKING(false);

	private boolean isClipped;

	private TileType(boolean isClipped) {
		this.isClipped = isClipped;
	}

	public boolean isClipped() {
		return isClipped;
	}
}