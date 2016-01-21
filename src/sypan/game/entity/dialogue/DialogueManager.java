package sypan.game.entity.dialogue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import sypan.game.Game;
import sypan.game.entity.AbstractEntity;
import sypan.game.entity.Direction;
import sypan.game.entity.dialogue.Dialogue.DialogueStage;
import sypan.game.entity.type.possession.Personality;
import sypan.utility.Set;

/**
 * Handles the dialogue within the game. Not very pretty, but time is of the essence...
 * 
 * @author Carl Linley
 **/
public class DialogueManager {

	private final float LENIENCE = 0.25f;

	private final String[] CONFRONT = {"Hey @, there's something off about you...",
									   "Stop right there! What is wrong with you?",
									   "Your eyes look kinda funny...",
									   "What is wrong with you?",
									   "You seem... off.",
									   "Hey @, are you high?",
									   "You seem... different. Explain.",
									   "What's gotten into you?",
									   "What's with the glow - you pregnant or something?"};

	private final String[] ESCALATE = {"What? That's not like you...",
									   "What!? The guy I know would never say that...",
									   "Huh? What HAS gotten into you?",
									   "... huh? Are you high?",
									   "What..? Are you possessed or something(!)?",
									   "Are you messing with me, punk?",
									   "You think that's funny?",
									   "You what, mate?"};

	private final String[] FOUND = {"Argh! He's possessed!",
									"Argh! You're DEAD!",
									"Argh! Shoot him!",
									"Game over, man! Game over!",
									"Someone call the exorcist!",
									"Someone call the Ghostbusters!",
									"Someone call my mother!"};

	private final String[] EVADED = {"Whatever, man.",
									 "Go see a doctor.",
									 "Fine, whatever.",
									 "Okay, sure, move along.",
									 "Later, loser.",
									 "Fine, get out of my face."};

	private Game game;

	private HashMap<String, Answer> answerStore;
	private ArrayList<String> answerList;

	private Random random;

	public DialogueManager(Game game) {
		this.game = game;

		registerAnswers();
		random = new Random();
	}

	private void registerAnswers() {
		answerStore = new HashMap<String, Answer>();
		answerList = new ArrayList<String>();

		registerAnswer("What do you mean?", new Answer(0.1f, 0.5f, 0.25f));
		registerAnswer("Huh?", new Answer(0.1f, 0.3f, 0.4f));
		registerAnswer("Get lost!", new Answer(0.75f, 0f, 0.25f));
		registerAnswer("Don't make me do something I'll regret.", new Answer(0.75f, 0f, 0.1f));
		registerAnswer("Mind your own!", new Answer(0.4f, 0.1f, 0f));
		registerAnswer("I feel fine.", new Answer(0f, 0.6f, 0.1f));
		registerAnswer("I don't know what you're referring to.", new Answer(0f, 0.7f, 0.1f));
		registerAnswer("Nonsense!", new Answer(0.5f, 0.1f, 0f));
		registerAnswer("Leave me be!", new Answer(0.1f, 0.8f, 0.1f));
		registerAnswer("Whatever do you mean?", new Answer(0.1f, 0.5f, 0.1f));
		registerAnswer("I don't know what you're on about.", new Answer(0.2f, 0.25f, 0.3f));
		registerAnswer("I'm fine, my good man.", new Answer(0f, 0.75f, 0.1f));
		registerAnswer("Dur... durhrrh...", new Answer(-LENIENCE, -LENIENCE, 0.75f));
		registerAnswer("Me idiot.", new Answer(-LENIENCE, -LENIENCE, 0.65f));
	}

	private void registerAnswer(String string, Answer answer) {
		answerList.add(string);
		answerStore.put(string, answer);
	}

	public void selectAnswer(Dialogue dialogue, int answerID) {
		Answer answer = answerStore.get(dialogue.getOptions()[answerID]);

		// Does the answer roughly match the entity's personality?
		if (check(answer)) { // Yep, well picked
			game.getSpirit().cooldown();
			endDialogue(dialogue);
			dialogue.getEntity().say(randomEvaded());
		}
		else {
			if (dialogue.getStage() == DialogueStage.CONFRONTATION) { // Nope, try again
				dialogue.set(randomEscalation(), randomOptions());
				dialogue.setStage(DialogueStage.ESCALATION);
			}
			else { // Ah. Whoops!
				alertAndDie(dialogue);
			}
		}
	}

	public void alertAndDie(Dialogue dialogue) {
		endDialogue(dialogue);
		dialogue.getEntity().say(randomFound());
		game.getPlayerControlledEntity().kill(true); // Goodbye.
	}

	public void endDialogue(Dialogue dialogue) {
		dialogue.getEntity().setDialogue(null);
		game.getSpirit().setDialogue(null);
	}

	/**
	 * @return true if the selected answer is in line with the entity's personality.
	 * +/- LENIENCE on any value is allowed.
	 **/
	private boolean check(Answer answer) {
		Personality p = game.getSpirit().getControlling().getPersonality();

		return (p.getEvilness() > answer.getEvilness() - LENIENCE && p.getEvilness() < answer.getEvilness() + LENIENCE)
			|| (p.getFriendliness() > answer.getFriendliness() - LENIENCE && p.getFriendliness() < answer.getFriendliness() + LENIENCE)
			|| (p.getFoolishness() > answer.getFriendliness() - LENIENCE && p.getFoolishness() < answer.getFoolishness() + LENIENCE);
	}

	/**
	 * Start interrogation!
	 **/
	public void startDialogue(AbstractEntity dialogueEntity) { 
		Dialogue dialogue = new Dialogue(game.getSpirit(), randomConfrontation(), randomOptions(), dialogueEntity);

		dialogueEntity.setDialogue(dialogue);
		game.getSpirit().setDialogue(dialogue);

		if (dialogueEntity.getPosition().x < game.getSpirit().getPosition().x) {
			game.getSpirit().turn(Direction.LEFT);
			dialogueEntity.turn(Direction.RIGHT);
		}
		else {
			dialogueEntity.turn(Direction.LEFT);
			game.getSpirit().turn(Direction.RIGHT);
		}
	}

	private String[] randomOptions() {
		Set<String> options = new Set<String>();
		String[] asStringArray;

		while(options.size() != 4) {
			options.add(answerList.get(random.nextInt(answerList.size())));
		}
		asStringArray = new String[4];

		return options.toArray(asStringArray);
	}

	private String randomConfrontation() {
		return CONFRONT[random.nextInt(CONFRONT.length)];
	}

	private String randomEvaded() {
		return EVADED[random.nextInt(EVADED.length)];
	}

	private String randomEscalation() {
		return ESCALATE[random.nextInt(ESCALATE.length)];
	}

	private String randomFound() {
		return FOUND[random.nextInt(FOUND.length)];
	}
}
class Answer {

	private float[] traits;

	Answer(float evilness, float friendliness, float foolishness) {
		traits = new float[] {evilness, friendliness, foolishness};
	}

	float getEvilness() {
		return traits[0];
	}

	float getFriendliness() {
		return traits[1];
	}

	float getFoolishness() {
		return traits[2];
	}

	@Override
	public String toString() {
		return getEvilness() + " / " + getFriendliness() + " / " + getFoolishness();
	}
}