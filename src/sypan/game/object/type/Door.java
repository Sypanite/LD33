package sypan.game.object.type;

import sypan.client.audio.AudioManager.SoundType;
import sypan.game.Game;
import sypan.game.object.AbstractObject;
import sypan.game.object.ObjectType;
import sypan.game.object.classification.Operable;

/**
 * @author Carl Linley
 **/
public class Door extends AbstractObject implements Operable {

	@Override
	public void initialise(Game game) {
	}

	@Override
	public void onOperation() {
		getGame().getObjectManager().destroyObject(this);
		getGame().getObjectManager().createObject((isClosed() ? ObjectType.DOOR_OPEN : ObjectType.DOOR_CLOSED), getTile().add(0, 1)); // Offset... for some reason

		if (isClosed()) {
			getGame().getAudioManager().playSound(SoundType.OPEN_DOOR);
		}
		else {
			getGame().getAudioManager().playSound(SoundType.CLOSE_DOOR);
		}
	}

	private boolean isClosed() {
		return getType() == ObjectType.DOOR_CLOSED;
	}

	@Override
	public String getTooltip() {
		return isClosed() ? "Open" : "Close";
	}
}