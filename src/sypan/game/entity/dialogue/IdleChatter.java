package sypan.game.entity.dialogue;

import java.util.Random;

/**
 * Bunch of generic chit-chat.
 * 
 * @author Carl Linley
 **/
public abstract class IdleChatter {

	private final static String[] POSSESSED = {"Ugh! What the!?", "Hellllllp!", "E-gad!", "Argh!", "Eek!", "Ergh!", "Yak!", "Mmpmmpp!",
											   "Gawwaah!", "What the..!?", "Yeaaaarrrrghhh!", "Eugh!", "Oh, no!"};

	private final static String[] LEFT = {"What the..?", "... how did I get here?", "... what just happened to me?", "... must be going crazy.",
										  "Better not tell the guys...", "I... ugh.", "Hmm... curious.", "What is going on!?", "I... nah, there's no such thing.",
										  "Hmm!?", "Strange trip..."};

	private final static String[] IDLE_CRIMINAL = {"I wonder what's for dinner tonight.", "Brr, it's suddenly cold...", "That guy won't mess with me again!", "Eerie night...",
												   "A life of crime is the life for me.", "I wish I had something to kill.", "I wish I had someone to mug.",
												   "Mitochondria is the powerhouse of the cell.", "Tyger, tyger, burning bright...", "My stomach go rumble.",
												   "Can't beat 'em, join 'em.", "The one thing I can't tolerate...", "I'd love to be an Oscar Mayer weiner.",
												   "I'd love a cup of Java.", "76, 68, 51, 51..."};

	private final static String[] IDLE_CIVILIAN = {"I wonder what's for dinner tonight.", "Brr, it's suddenly cold...", "I'd love a cup of Java.", "76, 68, 51, 51...",
												   "These criminals are so scary.", "I hope these monsters get what's coming to them.", "I miss Geraldine.", "I miss Mr Mittens.",
												   "When I come to power...", "Won't somebody help us?", "Why are we alone?", "Perfect night for my secret hobby.",
												   "I'll show them criminals...", "Sigh.", "I'd love a cheesecake.", "Circles are good.", "Rectangles are better.",
												   "Dum-de-dum-de-dum.", "I am out of idle chatter.", "I lied, how does this taste?", "76, 68, 51, 51!", "Am I but a lonely Slayer?"};

	private final static Random RANDOM = new Random();

	public static String randomPossessed() {
		return POSSESSED[RANDOM.nextInt(POSSESSED.length)];
	}

	public static String randomEscape() {
		return LEFT[RANDOM.nextInt(LEFT.length)];
	}

	public static String randomCriminalIdle() {
		return IDLE_CRIMINAL[RANDOM.nextInt(IDLE_CRIMINAL.length)];
	}

	public static String randomCivilianIdle() {
		return IDLE_CIVILIAN[RANDOM.nextInt(IDLE_CIVILIAN.length)];
	}
}