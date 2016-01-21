package sypan.game.object;

import java.util.ArrayList;

import sypan.game.Game;
import sypan.game.object.type.GenericObject;
import sypan.utility.Logger;
import sypan.utility.Vector2i;

/**
 * @author Carl Linley
 **/
public class ObjectManager {

	private Game game;

	private ArrayList<AbstractObject> objectList;
	private int uniqueObjectIndex;

	public ObjectManager(Game game) {
		this.game = game;
		objectList = new ArrayList<AbstractObject>();
		Logger.logInfo("ObjectManager initialised. " + ObjectType.values().length + " objects are defined.");
	}

	public AbstractObject createObject(ObjectType objectType, Vector2i spawnTile) {
		AbstractObject o;

		if ((o = getObject(spawnTile.getX(), spawnTile.getY())) != null) {
			destroyObject(o);
			o = null;
		}

		try {
			o = objectType.getClassType().newInstance();
			o.construct(game, objectType, uniqueObjectIndex ++); // TODO Construct this PROPERLY (Not enough time!)
		}
		catch(Exception ex) { // Too many exceptions to bother listing them all
			ex.printStackTrace();
			return null;
		}
		game.getPhysicsManager().create(o, spawnTile.toPhysics());

		if (o instanceof GenericObject) {
			Logger.logWarning("Using GenericObject for " + o + ".");
		}
		return addObject(o);
	}

	public AbstractObject addObject(AbstractObject object) {
		object.initialise(game);
		objectList.add(object);
		return object;
	}

	public AbstractObject getObject(int objectIndex) {
		for (AbstractObject o : objectList) {
			if (o.getIndex() == objectIndex) {
				return o;
			}
		}
		return null;
	}

	public void destroyObject(AbstractObject o) {
		if (o == null) {
			return;
		}
		game.getPhysicsManager().remove(o);
		objectList.remove(o);
	}

	public ArrayList<AbstractObject> getObjects() {
		return objectList;
	}

	public int count() {
		return objectList.size();
	}

	public AbstractObject getObject(int x, int y) {
		for (AbstractObject o : objectList) {
			if (o.getTile().equals(x, y)) {
				return o;
			}
		}
		return null;
	}

	public void clear() {
		objectList.clear();
		uniqueObjectIndex = 0;
	}
}