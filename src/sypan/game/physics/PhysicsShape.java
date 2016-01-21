package sypan.game.physics;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.EdgeShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;

/**
 * @author Carl Linley
 **/
public abstract class PhysicsShape {

	public static final int BOX = 0,
							CIRCLE = 1,
							LINE = 2;

	public static Shape CIRCLE(float radius) {
		CircleShape circle = new CircleShape();
		circle.setRadius(radius);
		return circle;
	}

	public static Shape BOX(float boxWidth, float boxHeight) {
		PolygonShape box = new PolygonShape();
		box.setAsBox(boxWidth, boxHeight);
		return box;
	}

	public static Shape LINE(Vec2 origin, Vec2 end) {
		EdgeShape line = new EdgeShape();
		line.set(origin, end);

		return line;
	}
}