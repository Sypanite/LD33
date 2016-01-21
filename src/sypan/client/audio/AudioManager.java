package sypan.client.audio;

import java.io.IOException;
import java.util.HashMap;

import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.util.ResourceLoader;

import sypan.utility.Logger;

/**
 * Last two hours!
 * 
 * @author Carl Linley
 **/
public class AudioManager {

	public enum SoundType {POSSESS, ESCAPE, JUMP, LAND, OPEN_DOOR, CLOSE_DOOR, SHOT, LEVEL_COMPLETE, TIME, LEVEL_FAILED};
	public enum MusicType {GAME};

	private HashMap<SoundType, Audio> soundStore;
	private HashMap<MusicType, Audio> musicStore;

	public AudioManager() {
		soundStore = new HashMap<SoundType, Audio>();
		musicStore = new HashMap<MusicType, Audio>();

		for (SoundType soundType : SoundType.values()) {
			try {
				soundStore.put(soundType, AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("resources/sound/" + soundType + ".wav")));
				Logger.logInfo("Loaded sound: " + soundType + ".");
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		for (MusicType musicType : MusicType.values()) {
			try {
				musicStore.put(musicType, AudioLoader.getAudio("OGG", ResourceLoader.getResourceAsStream("resources/music/" + musicType + ".ogg")));
				Logger.logInfo("Loaded music: " + musicType + ".");
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void playSound(SoundType soundType) {
		soundStore.get(soundType).playAsSoundEffect(1f, 1f, false);
	}

	public void playMusic(MusicType musicType) {
		musicStore.get(musicType).playAsMusic(1f, 1f, true);
	}
}