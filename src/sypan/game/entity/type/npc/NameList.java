package sypan.game.entity.type.npc;

import java.util.Random;

/**
 * Bunch of random names.
 * 
 * @author Carl Linley
 **/
abstract class NameList {

	private final static String[] CRIMINAL_NICKNAMES = {"Dastardly", "Detrimental", "Darkly Dreaming", "Desperate", "Persuasive", "Quick", "Slow", "Swift", "Dim", "Doc",
											   			"Red", "Blue", "Short-sighted", "Hungry", "Blind", "Thirsty", "Colourful", "Salty", "Fat", "Yellow", "Far-sighted",
											   			"Arm", "Eyeballing", "Vicious", "Comma", "Slim", "Shady"};

	private final static String[] FORENAMES = {"Mary", "Ada", "Stephen", "Brian", "Bill", "Bob", "Henry", "Alex", "Sam", "Greg", "Eric", "Aaron", "Reece",
											   "Zac", "Charlotte", "L.D", "Dan", "Dave", "Dick", "Dom", "Sam", "Ant", "Bob", "Biff", "James", "Pete", "Lil",
											   "Dil", "John", "Dexter", "Jim", "Oswald", "Tony", "Kyle", "Deb", "Richard", "Andrew", "Robin", "Monty"};

	private final static String[] CIVILIAN_SURNAMES = {"Antoinette", "Lovelace", "Hawking", "Smith", "Wilson", "House", "McAllister", "Morgan",
													   "Haines", "Simpson", "Lovejoy", "Pennyworth", "Wayne", "Parker", "Stewart", "Python"};

	public static String randomCriminal() {
		Random random = new Random();

		return "\"" + CRIMINAL_NICKNAMES[random.nextInt(CRIMINAL_NICKNAMES.length)] + "\" " + FORENAMES[random.nextInt(FORENAMES.length)];
	}

	public static String randomCivilian() {
		Random random = new Random();

		return FORENAMES[random.nextInt(CRIMINAL_NICKNAMES.length)] + " " + CIVILIAN_SURNAMES[random.nextInt(CIVILIAN_SURNAMES.length)];
	}
}