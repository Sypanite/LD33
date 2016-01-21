package sypan.game.entity.type.npc;

import java.util.Random;

import sypan.game.Game;
import sypan.game.entity.AbstractEntity;
import sypan.game.entity.dialogue.IdleChatter;
import sypan.utility.Logger;

/**
 * @author Carl Linley
 **/
class IdleChatterThread implements Runnable {

	private final static Random RANDOM = new Random();

	private Game game;
	private AbstractEntity chatterEntity;

	IdleChatterThread(Game game, AbstractEntity chatterEntity) {
		this.game = game;
		this.chatterEntity = chatterEntity;
	}

	@Override
	public void run() {
		while(game.isRunning() && chatterEntity.isAlive()) {
			try {
				Thread.sleep(10000 + RANDOM.nextInt(20000));

				if (!chatterEntity.inDialogue()) {
					chatterEntity.say(isCriminal() ? IdleChatter.randomCriminalIdle() : IdleChatter.randomCivilianIdle());
				}
			}
			catch (InterruptedException e) {
				Logger.logInfo(chatterEntity + ":IdleChatterThread interrupted.");
				break;
			}
		}
	}

	private boolean isCriminal() {
		return chatterEntity instanceof BaseCriminal;
	}
}