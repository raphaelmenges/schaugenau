package schaugenau.state.highscore;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

import schaugenau.app.App;
import schaugenau.core.AnimNode;
import schaugenau.core.FadableState;
import schaugenau.core.SimpleWorldBackground;
import schaugenau.core.SoundManager.Sound;
import schaugenau.core.StaticEntity;
import schaugenau.database.ScoreOperations;
import schaugenau.font.Font;
import schaugenau.font.Text;
import schaugenau.font.TextBox;
import schaugenau.gui.GuiDecoration;
import schaugenau.gui.IconButton;
import schaugenau.utilities.LerpValue;

/**
 * Distributed under the MIT License. (See accompanying file LICENSE or copy at
 * https://github.com/raphaelmenges/schaugenau/blob/master/src/LICENSE)
 * 
 * Highscore state.
 * 
 * @author Raphael Menges
 *
 */

public class HighscoreState extends FadableState {

	/** defines **/

	/* title bar */
	protected final float titleBarTextScale = 0.75f;
	protected final Vector3f titleBarPosition = new Vector3f(0, 6.3f, 1);
	protected final Vector3f titleBarTextOffset = new Vector3f(0, -2.1f * titleBarTextScale, 1);
	protected final float titleBarEntityScale = 1.5f;

	/* alphabet */
	protected final String listOfLetters[] = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N",
			"O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "Ä", "Ö", "Ü" };
	protected final float initialLetterDistance = 0.65f;
	protected final float initialLetterScale = 0.9f;
	protected final Vector3f alphabetShowPosition = new Vector3f(0, 4.5f, 0);
	protected final Vector3f alphabetHidePosition = new Vector3f(0, 5.5f, 0);
	protected final float selectionThreshold = 3.25f;
	protected final float alphabetFadeOutDuration = 0.25f;
	protected final float fontAlpha = 1.0f;
	protected final float alphabetWeightInputOffset = 0.25f;
	protected final float alphabetRawOffsetEpsilon = 0.025f;
	protected final float alphabetZ = 0.5f;

	/* buttons */
	protected final Vector3f confirmButtonPosition = new Vector3f(2.8f, -3.42f, 0f);
	protected final Vector3f backButtonPosition = new Vector3f(0.2f, -3.42f, 0f);
	protected final Vector3f restartButtonPosition = new Vector3f(-4.0f, -3.42f, 0f);
	protected final float confirmButtonScale = 2.3f;
	protected final float backButtonScale = 1.95f;
	protected final float restartButtonScale = 2.0f;
	protected final Vector2f confirmButtonHitOffset = new Vector2f(0.135f, -0.05f);
	protected final Vector2f backButtonHitOffset = new Vector2f(-0.135f, -0.05f);
	protected final Vector2f buttonHitBox = new Vector2f(1.3f, 1.25f);

	/* decoration */
	protected final Vector3f guiBackgroundPosition = new Vector3f(0, -1.6f, -5);
	protected final float guiBackgroundScale = 1.5f;
	protected final Vector3f arrowPosition = new Vector3f(0, 1.5f, -2);
	protected final float arrowScale = 4;
	protected final float arrowPulseSpeed = 2;
	protected final float arrowVisibilityInputOffset = 1.1f;
	protected final ColorRGBA arrowColor = new ColorRGBA(1, 1, 1, 1.0f);
	protected final Vector3f instructionPosition = new Vector3f(0f, -1.55f, 0);
	protected final float instructionScale = 0.75f;
	protected final Vector3f restartDecorationPosition = new Vector3f(-5.3f, -3.42f, -2.5f);
	protected final float restartDecorationScale = 1.3f;

	/* name */
	protected final Vector3f namePosition = new Vector3f(0f, -1.8f, 0);
	protected final float nameScale = 1.3f;
	protected final int maxNameLength = 10;

	/* bad language */
	protected final String[] filesWithBadWords = { "badLanguage_en.txt", "badLanguage_de.txt" };
	protected final Vector3f badLanguagePosition = new Vector3f(0, -5.5f, 0);
	protected final float badLanguageScale = 0.5f;

	/* empty name */
	protected final Vector3f emptyNamePosition = new Vector3f(0, -5.5f, 0);
	protected final float emptyNameScale = 0.6f;

	/* info texts */
	protected final float scoreInfoScale = 0.6f;
	protected final float restartInfoScale = 0.4f;
	protected final Vector3f scoreInfoPosition = new Vector3f(4.1f, -2.7f, 0);
	protected final Vector3f restartInfoPosition = new Vector3f(-5.2f, -2.4f, 0);

	/* error background */
	protected final float errorBackgroundScale = 1.5f;
	protected final Vector3f errorBackgroundPosition = new Vector3f(0, -5.55f, -2.5f);

	/** fields **/

	/* database */
	protected ScoreOperations scoreOperations;

	/* screens */
	protected SimpleWorldBackground background;

	/* title bar */
	protected GuiDecoration titleBar;
	protected int titleBarTextIndex;

	/* buttons */
	protected IconButton confirmButton;
	protected IconButton backButton;
	protected IconButton restartButton;

	/* name */
	protected String name;
	protected Text nameText;

	/* decoration */
	protected StaticEntity guiBackground;
	protected StaticEntity arrow;
	protected float arrowVisibility;
	protected float arrowPulseTime;
	protected Text instructionText;
	protected StaticEntity restartDecoration;

	/* alphabet */
	protected Font alphabetFont;
	protected ColorRGBA alphabetFontColor;
	protected AnimNode alphabetAnimNode;
	protected Node alphabetNode;
	protected LinkedList<AlphabetLetter> alphabet;
	protected float alphabetFadeOutTime;
	protected LerpValue focus;
	protected int rawFocus;
	protected LerpValue alphabetOffset;
	protected float rawAlphabetOffset;
	protected LerpValue weight;
	protected float rawWeight;
	protected boolean selectionEffectOfLetterFinished;
	protected boolean alphabetNeeded;
	protected boolean alphabetFadingDone;
	protected boolean setFocusDirect;
	protected boolean badLanguageWasDetected;

	/* bad language */
	protected HashSet<String> badWords;
	protected Text badLanguageText;

	/* empty name */
	protected Text emptyNameText;

	/* error background */
	protected StaticEntity errorBackground;

	/* info texts */
	protected TextBox scoreInfo;
	protected TextBox restartInfo;

	/** methods **/

	/* constructor */
	public HighscoreState(App app, String name, boolean debugging) {
		super(app, name, debugging);

		/* database */
		scoreOperations = new ScoreOperations();

		/* title bar */
		this.titleBar = new GuiDecoration(this.app, this.guiAdapter, "TitleBar");
		this.titleBarTextIndex = this.titleBar.addText(this.app.getPrimaryGuiFont(),
				schaugenau.font.Text.Alignment.CENTER, "", titleBarPosition.clone().add(titleBarTextOffset),
				titleBarTextScale);
		this.titleBar.addStaticEntity("HighscoreTitleBar", "Unshaded", "LaGa", "png", false, titleBarPosition.clone(),
				new Vector3f(), titleBarEntityScale);
		this.titleBar.attachTo(this.guiAdapter.getNode());

		/* ALPHABET SCREEN */

		/* initialize some members */
		this.focus = new LerpValue(0.001f);
		this.weight = new LerpValue(0.001f);
		this.alphabetOffset = new LerpValue(0.001f);

		/* create own font */
		alphabetFontColor = new ColorRGBA(1, 1, 1, 1);
		this.alphabetFont = new Font(this.app, "Font", "png", alphabetFontColor.clone(), this.app.getLetterWidth(),
				app.getLetterHeight(), false, true);

		/* create anim node for alphabet */
		alphabetAnimNode = new AnimNode(this, "AlphabetAnimNode", alphabetShowPosition, alphabetHidePosition, 0.5f,
				false);
		guiAdapter.attachChild(alphabetAnimNode.getNode());

		/* create node for alphaebt */
		alphabetNode = new Node("AlphabetNode");
		alphabetAnimNode.attachChild(alphabetNode);

		/* fill alphabet */
		alphabet = new LinkedList<AlphabetLetter>();
		for (int i = 0; i < listOfLetters.length; i++) {
			Vector3f position = new Vector3f(initialLetterDistance * (i - (listOfLetters.length / 2.0f) + 0.5f), 0,
					alphabetZ);
			AlphabetLetter letter = new AlphabetLetter(this.alphabetFont, this.guiAdapter, listOfLetters[i], position,
					initialLetterScale);
			letter.attachTo(alphabetNode);
			alphabet.add(letter);
		}

		/* button for confimation */
		confirmButton = new IconButton(app, guiAdapter, confirmButtonPosition, confirmButtonScale, "Icon-Ok",
				"ConfirmButton", this.confirmButtonHitOffset, this.buttonHitBox);
		confirmButton.attachTo(guiAdapter.getNode());

		/* button to delete last letter */
		backButton = new IconButton(app, guiAdapter, backButtonPosition, backButtonScale, "Icon-Back", "BackButton",
				this.backButtonHitOffset, this.buttonHitBox);
		backButton.attachTo(guiAdapter.getNode());

		/* button for restart */
		restartButton = new IconButton(app, guiAdapter, restartButtonPosition, restartButtonScale, "Icon-Restart",
				"RestartButton", new Vector2f(), this.buttonHitBox);
		restartButton.attachTo(guiAdapter.getNode());

		/* name string */
		name = new String();

		/* name text */
		nameText = new Text(this.app.getPrimaryGuiFont(), Text.Alignment.CENTER);
		nameText.scale(nameScale);
		nameText.move(namePosition);
		nameText.attachTo(guiAdapter.getNode());

		/* instruction text */
		instructionText = new Text(this.app.getSecondaryGuiFont(), Text.Alignment.CENTER);
		instructionText.scale(instructionScale);
		instructionText.move(instructionPosition);

		/* background decoration */
		guiBackground = new StaticEntity(this.app, "Background", "HighscoreBackground", "Unshaded", "LaGa", false,
				"png", true, false, false, false);
		guiBackground.scale(guiBackgroundScale);
		guiBackground.move(guiBackgroundPosition);
		guiBackground.attachTo(guiAdapter.getNode());

		/* restart decoration */
		restartDecoration = new StaticEntity(this.app, "RestartDecoration", "HighscoreRestartDecoration", "Unshaded",
				"LaGa", false, "png", true, false, false, false);
		restartDecoration.scale(restartDecorationScale);
		restartDecoration.move(restartDecorationPosition);
		restartDecoration.attachTo(guiAdapter.getNode());

		/* arrow */
		arrow = new StaticEntity(this.app, "Arrow", "Plane", "Unshaded", "Icon-Arrow", true, "png", true, true, false,
				false);
		arrow.setColorParameter(new ColorRGBA(1, 1, 1, 0).mult(arrowColor));

		arrow.scale(arrowScale);
		arrow.move(arrowPosition);
		arrow.attachTo(guiAdapter.getNode());

		/* prepare list of bad words for filtering */
		BufferedReader bufferedReader;
		badWords = new HashSet<String>();
		String line;

		for (String fileName : filesWithBadWords) {
			try {
				bufferedReader = new BufferedReader(new FileReader(fileName));
				try {
					while ((line = bufferedReader.readLine()) != null) {
						if (!line.startsWith("#") && !line.isEmpty()) {
							badWords.add(line);
						}
					}
				} catch (IOException e) {
					logger.error("IOException at reading: " + fileName);
				}

			} catch (FileNotFoundException e) {
				logger.error("File not found: " + fileName);
			}
		}

		/* prepare message to user if bad language was found */
		badLanguageText = new Text(app.getPrimaryGuiFont(), Text.Alignment.CENTER, "");
		badLanguageText.move(badLanguagePosition);
		badLanguageText.scale(badLanguageScale);

		/* prepare message to user if name is empty */
		emptyNameText = new Text(app.getPrimaryGuiFont(), Text.Alignment.CENTER, "");
		emptyNameText.move(emptyNamePosition);
		emptyNameText.scale(emptyNameScale);

		/* info boxes */
		scoreInfo = new TextBox(this.app.getSecondaryGuiFont(), schaugenau.font.TextBox.Alignment.LEFT);
		scoreInfo.setLocalScale(scoreInfoScale);
		scoreInfo.setLocalTranslation(scoreInfoPosition);
		scoreInfo.attachTo(guiAdapter.getNode());

		restartInfo = new TextBox(this.app.getSecondaryGuiFont(), schaugenau.font.TextBox.Alignment.RIGHT);
		restartInfo.setLocalScale(restartInfoScale);
		restartInfo.setLocalTranslation(restartInfoPosition);
		restartInfo.attachTo(guiAdapter.getNode());

		/* error background */
		errorBackground = new StaticEntity(this.app, "ErrorBackground", "HighscoreErrorBackground", "Unshaded", "LaGa",
				false, "png", true, false, false, false);
		errorBackground.setLocalScale(errorBackgroundScale);
		errorBackground.setLocalTranslation(errorBackgroundPosition);
		errorBackground.attachTo(guiAdapter.getNode());

		/* OTHER */
		background = new SimpleWorldBackground(this.app, false);
		background.attachTo(rootNode);
	}

	/* update */
	@Override
	public boolean update(float tpf, boolean buzzerPressed) {
		boolean hasDetachedItself = super.update(tpf, buzzerPressed);

		/* things which have to be done each update */
		if (!paused) {
			this.background.update(tpf);

			/* restart button */
			if (restartButton.update(tpf, false)) {
				this.app.loadGameState();
			}
		}

		return hasDetachedItself;
	}

	/* fade in, returns if finished */
	@Override
	protected boolean fadeIn(float tpf) {

		boolean fadingDone = super.fadeIn(tpf);

		return fadingDone;

	}

	/* running */
	@Override
	protected void running(float tpf, boolean buzzerPressed) {
		super.running(tpf, buzzerPressed);

		if (!paused) {

			/* make alphabet visible if needed */
			if (this.alphabetNeeded && this.alphabetAnimNode.isHiddenRightNow() && name.length() != maxNameLength) {
				this.alphabetAnimNode.setVisible(true);
				this.alphabetFadingDone = false;
				this.alphabetFontColor.a = fontAlpha;
			}

			/* update alphabet if needed and the selection is finished */
			if (this.alphabetNeeded && this.selectionEffectOfLetterFinished) {

				/* calculate weight */
				rawWeight = (float) Math
						.sqrt(Math.min(1, Math.max(0, this.guiAdapter.getCursor().y + alphabetWeightInputOffset)));

				weight.update(tpf, 1.5f, 0.5f, rawWeight);

				/* if not yet focused, set weight to zero */
				if (this.setFocusDirect) {
					weight.setValue(0);

				} else if (weight.getValue() == 0) {

					/* if focusing stopped, next time focus directly */
					this.setFocusDirect = true;
				}

				/* move single letters */
				for (int i = 0; i < listOfLetters.length; i++) {

					/* update letters */
					if (alphabet.get(i).update(tpf, weight.getValue(), i - focus.getValue())) {
						rawFocus = i;
						rawAlphabetOffset = -(i - (alphabet.size() / 2.0f));

						/* focus direct on raw focus on initial focus */
						if (this.setFocusDirect) {

							/* set value of focus */
							focus.setValue(rawFocus);

							/* now, set focus direct to false */
							setFocusDirect = false;
							alphabetOffset.setValue(0);
						}
					}

					/* check threshold (whether selection is done) */
					if (alphabet.get(i).checkForThreshold(this.selectionThreshold)) {
						this.selectionEffectOfLetterFinished = false;
						this.name += listOfLetters[i];
						if (name.length() == maxNameLength) {
							this.alphabetNeeded = false;
						}

						/* detach information about empty name field */
						emptyNameText.detach();
						instructionText.detach();

						/* bad language stuff */
						if (badLanguageWasDetected) {
							if (!checkForBadLanguage(name)) {
								badLanguageText.detach();
								errorBackground.detach();
								badLanguageWasDetected = false;
							}
						} else {
							errorBackground.detach();
						}

						/* play nice sound */
						app.getSoundManager().playSound(Sound.CHOOSE_LETTER, true);

						/* only one letter per session */
						break;
					}

				}

				/* calculate speed for linear interpolations */
				float speed = 0.6f * (0.75f * Math.abs(this.guiAdapter.getCursor().x)
						+ 0.25f * Math.abs(rawFocus - focus.getValue()) + 1.5f);

				/* move focus to center */
				alphabetOffset.update(tpf, 0.5f * speed, rawAlphabetOffset);
				this.alphabetNode.setLocalTranslation(weight.getValue() * alphabetOffset.getValue(), 0, 0);

				/* update focus */
				focus.update(tpf, speed, rawFocus);

			} else {
				if (!this.alphabetFadingDone) {

					/* selection is done, do some eye candy */
					this.alphabetFadeOutTime += tpf;

					/* set alpha */
					this.alphabetFontColor.a = fontAlpha * (1.0f - (alphabetFadeOutTime / alphabetFadeOutDuration));

					/* move whole alphabet downwards */
					this.alphabetNode.move(0, -tpf, 0);

					/* check the time */
					if (alphabetFadeOutTime >= alphabetFadeOutDuration) {
						this.alphabetFadeOutTime = 0;
						this.selectionEffectOfLetterFinished = true;
						this.resetAlphabet();
						this.alphabetFadingDone = true;
					}
				}
			}

			this.alphabetFont.setColor(alphabetFontColor.clone());

			/* UPDATE GUI */

			/* arrow */
			float currentArrowVisibility = (1.0f - (float) Math
					.sqrt(Math.min(1, Math.max(0, this.guiAdapter.getCursor().y + arrowVisibilityInputOffset))));
			if (currentArrowVisibility > 0) {
				arrowVisibility += 0.5f * tpf * currentArrowVisibility;
			} else {
				arrowVisibility -= 3 * tpf;
			}
			if (name.length() == maxNameLength) {
				arrowVisibility = 0;
			}
			arrowVisibility = Math.min(1, Math.max(0, arrowVisibility));

			arrowPulseTime += arrowPulseSpeed * tpf;
			arrowPulseTime = (float) (arrowPulseTime % (2 * Math.PI));

			float arrowAlpha = 0.125f * (float) Math.sin(arrowPulseTime) + 0.25f;
			arrow.setColorParameter(new ColorRGBA(1, 1, 1, arrowVisibility * arrowAlpha).mult(arrowColor));

			/* back button */
			if (backButton.update(tpf, badLanguageWasDetected) && name.length() >= 1) {
				name = name.substring(0, name.length() - 1);
				if (badLanguageWasDetected) {
					if (!checkForBadLanguage(name)) {
						badLanguageText.detach();
						errorBackground.detach();
						badLanguageWasDetected = false;
					}
				}
				this.alphabetNeeded = true;
				if (name.isEmpty()) {
					instructionText.attachTo(this.guiAdapter.getNode());
				}
			}

			/* confirm button */
			if (confirmButton.update(tpf, name.length() == maxNameLength)) {

				/* first, check if there is a name */
				if (name.length() > 0) {

					/* check name for bad language */
					if (checkForBadLanguage(name)) {
						badLanguageText.attachTo(this.guiAdapter.getNode());
						errorBackground.attachTo(this.guiAdapter.getNode());
						badLanguageWasDetected = true;
					} else {
						badLanguageText.detach();
						errorBackground.detach();
						badLanguageWasDetected = false;

						/* name is ok, send data to database */
						scoreOperations.saveScore(this.app.getCurrentGameStyleString(), this.app.getCurrentScore(),
								name, this.app.getCurrentPlayedMiliSeconds(), this.app.getCurrentMaxMultiplicator(),
								this.app.getCurrentAvMultiplicator(), this.app.getCurrentCorrectPictures(),
								this.app.getCurrentIncorrectPictures());

						/* log everything */
						logger.debug("Following data was transmitted to database:");
						logger.debug("Name: " + name);
						logger.debug("Score: " + this.app.getCurrentScore());
						logger.debug("GameStyle: " + this.app.getCurrentGameStyleString());
						logger.debug("PlayedTime: " + this.app.getCurrentPlayedMiliSeconds());
						logger.debug("MaxMultiplicator: " + this.app.getCurrentMaxMultiplicator());
						logger.debug("AvgMultiplicator: " + this.app.getCurrentAvMultiplicator());
						logger.debug("CorrectPictures: " + this.app.getCurrentCorrectPictures());
						logger.debug("IncorrectPictures: " + this.app.getCurrentIncorrectPictures());

						/* display players score in idle state */
						this.app.setLastGameFinished(true);

						/* give name to app for displaying */
						this.app.setCurrentPlayersName(this.name);

						/* go to survey or idle state */
						if (this.app.getDoSurvey()) {
							this.app.loadSurveyState();
						} else {
							this.app.loadIdleState();
						}
					}
				} else {
					emptyNameText.attachTo(this.guiAdapter.getNode());
					errorBackground.attachTo(this.guiAdapter.getNode());
				}
			}

			/* update name text */
			this.nameText.setContent(this.name);
		}
	}

	/* fade out, returns if finished */
	@Override
	protected boolean fadeOut(float tpf) {

		boolean fadingDone = super.fadeOut(tpf);

		/* nothing to do */

		return fadingDone;
	}

	/* attach */
	@Override
	protected void attach() {
		super.attach();

		/* alphabet */
		this.selectionEffectOfLetterFinished = true;
		this.resetAlphabet();
		this.alphabetFontColor.a = fontAlpha;
		this.alphabetFont.setColor(alphabetFontColor.clone());
		this.alphabetNeeded = true;
		this.alphabetFadingDone = false;
		this.badLanguageWasDetected = false;
		this.errorBackground.detach();

		/* button */
		confirmButton.reset();
		backButton.reset();
		restartButton.reset();

		/* name */
		name = new String();
		nameText.setContent(name);

		/* instruction */
		instructionText.attachTo(this.guiAdapter.getNode());

		/* detach bad language text (not necessary) */
		badLanguageText.detach();

		/* detach empty name */
		emptyNameText.detach();

		/* arrow */
		arrowVisibility = 0;
		arrowPulseTime = 0;
		arrow.setColorParameter(new ColorRGBA(1, 1, 1, 0).mult(arrowColor));

		/* title bar */
		this.titleBar.getText(titleBarTextIndex).setContent(app.getMessages().getString("highscore.titleBar"));

		/* other texts */
		this.badLanguageText.setContent(app.getMessages().getString("highscore.badLanguage"));
		this.emptyNameText.setContent(app.getMessages().getString("highscore.emptyName"));
		this.instructionText.setContent(app.getMessages().getString("highscore.instruction"));
		this.scoreInfo
				.setContent("" + this.app.getCurrentScore() + " " + app.getMessages().getString("highscore.scoreInfo"));
		this.restartInfo.setContent(app.getMessages().getString("highscore.restartInfo"));
	}

	/* detach */
	@Override
	protected void detach() {
		super.detach();

	}

	/* stop */
	@Override
	public void stop() {
		super.stop();

	}

	/* reset alphabet and related values */
	public void resetAlphabet() {
		this.focus.setValue(listOfLetters.length / 2.0f);
		this.rawFocus = (int) this.focus.getValue();
		this.alphabetOffset.setValue(0);
		this.rawAlphabetOffset = 0;
		this.rawWeight = 0;
		this.weight.setValue(0);
		this.alphabetNode.setLocalTranslation(0, 0, 0);
		this.setFocusDirect = true;

		for (AlphabetLetter letter : alphabet) {
			letter.reset();
		}

		/* hide */
		alphabetAnimNode.hideImediatelly();
	}

	/* check string for bad words */
	protected boolean checkForBadLanguage(String word) {

		String lowerCaseWord = word.toLowerCase();

		String wordWithoutSpace = lowerCaseWord.replaceAll(" ", "");

		/* check all known bad words */
		for (String badWord : badWords) {
			if (lowerCaseWord.indexOf(badWord) != -1) {
				return true;
			}
			if (wordWithoutSpace.indexOf(badWord) != -1) {
				return true;
			}
		}

		/* no bad language found */
		return false;
	}

}