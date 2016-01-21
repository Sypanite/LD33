package sypan.utility;

import org.jbox2d.common.Vec2;

import sypan.game.level.Level;

/**
 * @author Carl Linley
 **/
public class Vector2i {

	public static final Vector2i ZERO = new Vector2i();

	protected int x, y;

	public Vector2i() {
	}

	public Vector2i(int a, int b) {
		x = a;
		y = b;
	}

	public Vector2i(Vec2 asVec2) {
		x = (int) asVec2.x;
		y = (int) asVec2.y;
	}

	public Vector2i add(int i, int j) {
		return alter(i, j);
	}

	public Vector2i addLocal(int i, int j) {
		x += i;
		y += j;
		return this;
	}

	public Vector2i add(Vector2i otherVector) {
		return alter(otherVector.getX(), otherVector.getY());
	}

	public Vector2i addLocal(Vector2i otherVector) {
		x += otherVector.getX();
		y += otherVector.getY();
		return this;
	}

	public Vector2i alter(int a, int b) {
		return new Vector2i(x + a, y + b);
    }

	public Vector2i divide(int i) {
		return clone().divideLocal(i);
	}

	public Vector2i divideLocal(int i) {
		x /= i;
		y /= i;
		return this;
	}

	public Vector2i mult(int scalar) {
		return new Vector2i(x * scalar, y * scalar);
	}

	public Vector2i negateY() {
		y = -y;
		return this;
	}

	public Vector2i set(int a, int b) {
		x = a;
		y = b;
		return this;
	}

	public void set(Vector2i setTo) {
		if (setTo == null) {
			return;
		}
		this.x = setTo.getX();
		this.y = setTo.getY();
	}

	public Vector2i subtract(int i, int j) {
		return alter(-i, -j);
	}

	public Vector2i subtract(Vector2i v) {
		return alter(-v.getX(), -v.getY());
	}

	
	public Vec2 toPhysics() {
		Vec2 toPhys = new Vec2(x, -y);
		toPhys.x *= Level.TILE_OFFSET;
		toPhys.y *= Level.TILE_OFFSET;
	
		return toPhys;
	}

    public int distanceSquared(int otherX, int otherY) {
        int resultX = x - otherX, resultY = y - otherY;

        return (resultX * resultX + resultY * resultY);
    }

    public int distance(Vector2i v) {
        return (int) Math.sqrt(distanceSquared(v.getX(), v.getY()));
    }

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Vector2i) {
			Vector2i otherVector = (Vector2i) o;

			return x == otherVector.getX() && y == otherVector.getY();
		}
		return false;
	}

	public boolean equals(int i, int j) {
		return x == i && y == j;
	}

	@Override
	public Vector2i clone() {
		return new Vector2i(x, y);
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";

	}

	public static Vector2i physicsToTile(Vec2 position) {
		Vec2 r = position.clone();

		r.x /= Level.TILE_OFFSET;
		r.y /= Level.TILE_OFFSET;

		int tileX = Math.round(r.x),
			tileY = Math.round(r.y) + 1;

		return new Vector2i(tileX, tileY).negateY();
	}
}