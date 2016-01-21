package sypan.game.physics;

import org.jbox2d.dynamics.Body;

/**
 * Implemented by... physical... stuff.
 * 
 * @author Carl Linley
 **/
public interface Physical {

	void setPhysics(Body phys);

	int getIndex();
	PhysicalData getPhysicalData();
	Body getPhysics();
}