package sypan.game.entity.type.possession;

import java.util.Random;

/**
 * Stores personality data.
 * 
 * @author Carl Linley
 **/
public class Personality {

	public static final String[] ATTRIBUTE = {"Evilness", "Friendliness", "Foolishness", "Ruthlessness", "Reasonability", "Durability"};

	private float[] personalityTraits; // Evilness, Friendliness, Foolishness, Ruthlessness, Reasonability, Durability;

	public Personality(float[] personalityTraits) {
		for (int i = 0; i != personalityTraits.length; i++) {
			personalityTraits[i] *= 1.25f;

			if (personalityTraits[i] > 1) {
				personalityTraits[i] = 1;
			}
		}
		this.personalityTraits = personalityTraits;
	}

	public float getEvilness() {
		return personalityTraits[0];
	}

	public float getFriendliness() {
		return personalityTraits[1];
	}

	public float getFoolishness() {
		return personalityTraits[2];
	}

	public float getRuthlessness() {
		return personalityTraits[3];
	}

	public float getReasonability() {
		return personalityTraits[4];
	}

	public float getDurability() {
		return personalityTraits[5];
	}

	public float getTrait(int i) {
		return personalityTraits[i];
	}

	/**
	 * @return a pseudorandom personality.
	 **/
	public static Personality random() {
		Random random = new Random();

		return new Personality(new float[] {random.nextFloat(), random.nextFloat(), random.nextFloat(), random.nextFloat(), random.nextFloat(), random.nextFloat()});
	}

	/**
	 * @return a pseudorandom personality with a leaning towards evil / ruthless.
	 **/
	public static Personality randomBadGuy() {
		Random random = new Random();

		return new Personality(new float[] {random.nextFloat() * 1.25f, random.nextFloat(), random.nextFloat(), random.nextFloat() * 1.25f,
											random.nextFloat(), random.nextFloat()});
	}

	@Override
	public String toString() {
		return "Personality - Evil: " + (getEvilness()) + "% / "
						   + "Friendly: " + (getFriendliness()) + "% / "
						   + "Foolish: " + (getFoolishness()) + "% / "
						   + "Ruthless: " + (getRuthlessness()) + "% / "
						   + "Reason: " + (getReasonability()) + "% / "
						   + "Durability: " + (getDurability()) + "%";
	}
}