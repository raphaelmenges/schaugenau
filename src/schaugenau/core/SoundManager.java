package schaugenau.core;

import java.util.EnumMap;

import com.jme3.audio.AudioNode;

import schaugenau.app.App;

/**
 * Distributed under the MIT License. (See accompanying file LICENSE or copy at
 * https://github.com/raphaelmenges/schaugenau/blob/master/src/LICENSE)
 * 
 * Manager for sounds. Obtains a map with aim to load and represent every sound
 * only one time.
 * 
 * @author Raphael Menges
 *
 */

public class SoundManager {

	/** enumerations **/
	public enum Sound {
		BUTTON_PRESSED, BUZZER_PRESSED, CHOOSE_LETTER, CORRECT_PICTURE, END, FLOWER, INCORRECT_PICTURE, SPIDERWEB, TICK, SELECT_PICTURE, COUNT_DOWN, MULTIPLICATOR_UP, MULTIPLICATOR_DOWN
	};

	/** defines **/
	protected final float soundVolume = 0.5f;

	/** fields **/
	protected App app;

	/* map for sounds */
	protected EnumMap<Sound, AudioNode> soundMap = new EnumMap<Sound, AudioNode>(Sound.class);

	/** methods **/

	/* constructor */
	public SoundManager(App app) {
		this.app = app;

		/* put sounds */
		putSound(Sound.BUTTON_PRESSED, "ButtonPressed", 0.8f);
		putSound(Sound.BUZZER_PRESSED, "BuzzerPressed");
		putSound(Sound.CHOOSE_LETTER, "ChooseLetter");
		putSound(Sound.CORRECT_PICTURE, "CorrectPicture");
		putSound(Sound.END, "End");
		putSound(Sound.FLOWER, "Flower");
		putSound(Sound.INCORRECT_PICTURE, "IncorrectPicture");
		putSound(Sound.SPIDERWEB, "Spiderweb");
		putSound(Sound.TICK, "Tick", 0.2f);
		putSound(Sound.SELECT_PICTURE, "SelectPicture");
		putSound(Sound.COUNT_DOWN, "Countdown");
		putSound(Sound.MULTIPLICATOR_UP, "MultiplicatorUp");
		putSound(Sound.MULTIPLICATOR_DOWN, "MultiplicatorDown");
	}

	/* play single sound */
	public void playSound(Sound sound, boolean reset) {
		if (reset) {
			soundMap.get(sound).stop();
		}
		soundMap.get(sound).play();
	}

	/* stop all sounds */
	public void stopAllSounds() {
		for (AudioNode audioNode : soundMap.values()) {
			audioNode.stop();
		}
	}

	/* simplified put sound into map */
	protected void putSound(Sound sound, String filename) {
		this.putSound(sound, filename, 1);
	}

	/* put sound into map */
	protected void putSound(Sound sound, String filename, float volume) {

		/* create audio node */
		AudioNode audioNode = new AudioNode(app.getAssetManager(), app.pathSounds + filename + ".ogg", false);
		audioNode.setVolume(soundVolume * volume);
		audioNode.setPositional(false);
		audioNode.setLooping(false);
		app.getSceneRoot().attachChild(audioNode);
		soundMap.put(sound, audioNode);
	}
}
