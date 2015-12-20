package schaugenau.app;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import com.jme3.app.Application;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.system.AppSettings;

import schaugenau.core.FadableState;
import schaugenau.core.SoundManager;
import schaugenau.core.SoundManager.Sound;
import schaugenau.database.PictureOperations;
import schaugenau.database.SurveyOperations;
import schaugenau.font.Font;
import schaugenau.input.DistortedMouseInput;
import schaugenau.input.EyeXInput;
import schaugenau.input.Input;
import schaugenau.input.MouseInput;
import schaugenau.state.calibration.CalibrationState;
import schaugenau.state.game.GameState;
import schaugenau.state.highscore.HighscoreState;
import schaugenau.state.hyper.HyperState;
import schaugenau.state.hyper.HyperState.MusicTrack;
import schaugenau.state.idle.IdleState;
import schaugenau.state.survey.SurveyState;
import schaugenau.state.tutorial.TutorialState;

/**
 * Distributed under the MIT License. (See accompanying file LICENSE or copy at
 * https://github.com/raphaelmenges/schaugenau/blob/master/src/LICENSE)
 * 
 * Entry point of game. App class owns all game states and takes care of
 * initialization.
 * 
 * @author Raphael Menges
 *
 */

public class App extends Application {

	public final float version = 1.02f;

	private static final Logger logger = Logger.getLogger(App.class);

	/** enumerations **/
	public enum Preset {
		HIGH_FULLSCREEN_1080, HIGH_FULLSCREEN_720, HIGH_WINDOWED_720, HIGH_FULLSCREEN_900
	}

	public enum InputType {
		MOUSE, EYE_X, EYE_TRIBE, DISTORTED_MOUSE
	}

	public enum GameStyle {
		DIRECT, DIRECT_WITH_GRID, INDIRECT
	}

	public enum DbConnectivity {
		ONLINE, OFFLINE
	}

	public enum Language {
		GERMAN, ENGLISH
	}

	/** defines */

	/* settings */
	protected final String title = "Schau genau!";
	protected final Preset preset = Preset.HIGH_WINDOWED_720; // Preset.HIGH_FULLSCREEN_900;
	protected final InputType inputType = InputType.MOUSE; // InputType.EYE_X;
	protected final boolean debugging = false;
	protected final boolean forceInvisbleCursor = false;
	protected final boolean vsync = true;
	public static final DbConnectivity connectivity = DbConnectivity.ONLINE; // DbConnectivity.OFFLINE;
	protected final Language initialLanguage = Language.ENGLISH;
	protected final GameStyle initialGameStyle = GameStyle.INDIRECT;
	protected final boolean loadMusic = false;

	/* values for high settings */
	protected final int samples = 4;

	/* font */
	protected final float letterWidth = 1f / 12f;
	protected final float letterHeight = (70f / 72f) / 10f;
	protected final String fontTextureName = "Font";

	/* paths */
	public final String pathMaterials = "Materials/";
	public final String pathModels = "Models/";
	public final String pathTextures = "Textures/";
	public final String pathSounds = "Sounds/";

	/* other */
	protected final float buzzerRepressDuration = 0.5f;

	/** fields **/

	/* general */
	protected Node sceneRoot;
	protected Node guiRoot;
	protected FadableState activeState = null;
	protected FadableState nextState = null;
	protected static boolean buzzerPressed = false;
	protected static boolean gazeDotsToggle = false;
	protected boolean activeStateAboutToChange;
	protected boolean wasPaused = false;
	protected ResourceBundle messages;
	protected boolean abortable;
	protected boolean lostInput;
	protected boolean buzzerForRestartPressed;
	protected boolean inputWasNotWorking;
	protected float lastTpf;
	protected boolean lastGameFinished;
	protected Language language;
	protected boolean doSurvey;
	protected boolean stopped;

	/* sound */
	protected SoundManager soundManager;

	/* members need by multiple states */
	protected GameStyle currentGameStyle;
	protected int currentPlayedMiliSeconds;
	protected int currentMaxMultiplicator;
	protected double currentAvMultiplicator;
	protected int currentCorrectPictures;
	protected int currentIncorrectPictures;
	protected int currentScore;
	protected String currentPlayersName;

	/* settings */
	protected int windowWidth;
	protected int windowHeight;
	// graphics settings 0 = low, 1 = medium, 2 = high
	protected int graphicsSettings;
	protected boolean fullscreen;

	/* font */
	protected Font primaryGuiFont;
	protected Font secondaryGuiFont;
	protected Font tertiaryGuiFont;
	protected Font primaryWorldFont;
	protected Font secondaryWorldFont;

	/* input */
	protected Vector2f cursor;
	protected Input input;

	/* states */
	protected IdleState idleState;
	protected CalibrationState calibrationState;
	protected TutorialState tutorialState;
	protected GameState gameState;
	protected HighscoreState highscoreState;
	protected SurveyState surveyState;

	/* super state which is always attached */
	protected HyperState hyperState;

	/* controls */
	protected final AppActionListener appActionListener = new AppActionListener();

	/* database */
	protected PictureOperations pictureOperations;

	/* other */
	protected float buzzerRepressTime;

	/** main function **/
	public static void main(String[] args) {
		App app = new App();
		app.start();
	}

	/** methods **/

	/* start */
	@Override
	public void start() {

		logger.debug("App starts");
		logger.debug("Version: " + this.version);

		/* fill settings with preset */
		fillSettingsWithPreset();

		/* use own settings */
		AppSettings settings = new AppSettings(true);
		settings.setResolution(windowWidth, windowHeight);
		settings.setTitle(title);
		settings.setFullscreen(fullscreen);
		settings.setFrameRate(60);
		settings.setVSync(vsync);

		if (graphicsSettings < 2) {
			settings.setSamples(0);
		} else {
			settings.setSamples(samples);
		}

		/* give app the alias of settings */
		this.setSettings(settings);

		/* database */
		pictureOperations = new PictureOperations();

		/* set initial language */
		this.setLanguage(initialLanguage);

		/* create necessary folders if not yet exisisting */
		this.createFolders();

		super.start();

		logger.debug("... started");
	}

	/* initialize */
	@Override
	public void initialize() {
		super.initialize();

		/* better pause when focus is lost */
		this.setPauseOnLostFocus(true);

		sceneRoot = new Node("sceneRootNode");
		guiRoot = new Node("guiRootNode");

		activeStateAboutToChange = false;
		lostInput = false;
		buzzerForRestartPressed = false;
		inputWasNotWorking = false;
		abortable = false;
		lastGameFinished = false;
		doSurvey = false;
		buzzerRepressTime = 0;
		stopped = false;

		/* minimal setup */
		guiRoot.setQueueBucket(Bucket.Gui);
		guiRoot.setCullHint(CullHint.Never);
		viewPort.attachScene(sceneRoot);
		guiViewPort.attachScene(guiRoot);

		/* should be overwritten by intro and game state */
		currentGameStyle = initialGameStyle;
		currentPlayedMiliSeconds = 0;
		currentMaxMultiplicator = 0;
		currentAvMultiplicator = 0;
		currentCorrectPictures = 0;
		currentIncorrectPictures = 0;

		logger.debug("Create SoundManager");

		/* create sound manager */
		this.soundManager = new SoundManager(this);

		logger.debug("... created");
		logger.debug("Get input");

		/* create input device */
		switch (inputType) {
		case MOUSE:
			input = new MouseInput(this);
			break;
		case EYE_X:
			input = new EyeXInput(this);
			break;
		case EYE_TRIBE:
			// NOT YET IMPLEMENTED
			input = new MouseInput(this);
			break;
		case DISTORTED_MOUSE:
			input = new DistortedMouseInput(this);
			break;
		}

		logger.debug("... got");

		if (forceInvisbleCursor) {
			this.getInputManager().setCursorVisible(false);
		}

		/* input mapping */
		getInputManager().addMapping("exit", new KeyTrigger(KeyInput.KEY_ESCAPE));
		getInputManager().addMapping("buzzer", new KeyTrigger(KeyInput.KEY_RETURN));
		getInputManager().addMapping("gazeDotsToggle", new KeyTrigger(KeyInput.KEY_SPACE));

		/* input listener */
		getInputManager().addListener(appActionListener, "exit");
		getInputManager().addListener(appActionListener, "buzzer");
		getInputManager().addListener(appActionListener, "gazeDotsToggle");

		/* create gui font */
		primaryGuiFont = new Font(this, fontTextureName, "png", new ColorRGBA(1, 1, 1, 1), letterWidth, letterHeight,
				false, true);
		secondaryGuiFont = new Font(this, fontTextureName, "png", new ColorRGBA(1f, 1f, 1f, 0.6f), letterWidth,
				letterHeight, false, true);
		tertiaryGuiFont = new Font(this, fontTextureName, "png", new ColorRGBA(0.453f, 0.703f, 0.133f, 1.0f),
				letterWidth, letterHeight, false, true);

		/* create world font */
		primaryWorldFont = new Font(this, fontTextureName, "png", new ColorRGBA(1, 1, 1, 1), letterWidth, letterHeight,
				false, false);
		secondaryWorldFont = new Font(this, fontTextureName, "png", new ColorRGBA(1f, 1f, 1f, 0.6f), letterWidth,
				letterHeight, false, false);

		logger.debug("Create states");

		/* create hyper state and attach it */
		hyperState = new HyperState(this, "Hyper", debugging, loadMusic);
		hyperState.attach();

		/* create idle state */
		idleState = new IdleState(this, "Idle", debugging);

		/* create calibration state */
		calibrationState = new CalibrationState(this, "Calibration", debugging);

		/* create tutorial state */
		tutorialState = new TutorialState(this, "Tutorial", debugging);

		/* create game state */
		gameState = new GameState(this, "Game", debugging);

		/* create highscore state */
		highscoreState = new HighscoreState(this, "Highscore", debugging);

		/* create survey state */
		surveyState = new SurveyState(this, "Survey", debugging);

		logger.debug("... created");

		/* set first state */
		loadIdleState();

	}

	/* update */
	@Override
	public void update() {
		super.update();

		/* do system restart, if eyetracker died */
		if (input.isStarted()) {
			if (!input.isConnected()) {
				try {
					logger.debug("Input not connected. Restart of computer.");
					@SuppressWarnings("unused")
					Process process = new ProcessBuilder("C:\\Windows\\System32\\shutdown.exe", "/r", "/f", "/t", "000")
							.start();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		/* speed */
		if (speed == 0 || paused) {
			wasPaused = true;
			return;
		}

		/* check whether app was paused */
		if (wasPaused) {
			/* skip one frame */
			wasPaused = false;
			return;
		}

		/* get frame time */
		float tpf = timer.getTimePerFrame() * speed;

		/* compensate lag */
		if (tpf > 0.5) {
			tpf = lastTpf;
		} else {
			lastTpf = tpf;
		}

		/* manage buzzer pressing */
		if (buzzerPressed && buzzerRepressTime <= 0) {
			buzzerRepressTime = buzzerRepressDuration;
		} else {
			/* buzzer was pressed too soon */
			buzzerPressed = false;
		}
		buzzerRepressTime -= tpf;
		buzzerRepressTime = Math.max(buzzerRepressTime, 0);

		/* play sound, if buzzer was pressed */
		if (buzzerPressed) {
			soundManager.playSound(Sound.BUZZER_PRESSED, true);
		}

		/* update input */
		cursor = input.update(tpf);

		/* hyper stuff (buzzer and not working input */
		if (abortable) {

			/* input not working */
			if (!activeStateAboutToChange && !this.input.isInputWorking()) {
				if (!inputWasNotWorking) {
					/* set reminder */
					inputWasNotWorking = true;
					if (!buzzerForRestartPressed) {
						/* only pause if not already paused */
						activeState.pause();
					}
				}
			} else {
				/* input now working */
				if (inputWasNotWorking) {
					/* set reminder */
					inputWasNotWorking = false;
					if (!buzzerForRestartPressed) {
						/* only unpause if other dialog is not there */
						activeState.unpause();
					}
				}
			}

			/* buzzer pressed */
			if (!activeStateAboutToChange && buzzerPressed) {
				if (!buzzerForRestartPressed) {
					buzzerForRestartPressed = true;
					if (!inputWasNotWorking) {
						activeState.pause();
					}
				} else {
					buzzerForRestartPressed = false;
					if (!inputWasNotWorking) {
						activeState.unpause();
					}
				}
			}

			/* show it to player via hyper state */
			if (hyperState.showDialog(tpf, buzzerForRestartPressed, inputWasNotWorking)) {

				/* dialog tells us: restart */
				buzzerForRestartPressed = false;
				inputWasNotWorking = false;
				loadIdleState();

				/* reset dialog */
				hyperState.showDialog(0, false, false);
			}
		}

		/* update active state */
		boolean stateMayChange = activeState.update(tpf, buzzerPressed);

		/* check whether switch to next state is necessary */
		if (activeStateAboutToChange) {
			if (stateMayChange) {

				/* set next state */
				setNextState();

				/* better update new active state once */
				activeState.update(tpf, buzzerPressed);

			}
		}

		/* update hyper state */
		hyperState.update(tpf, buzzerPressed, gazeDotsToggle);

		/* reset buzzer pressed */
		buzzerPressed = false;

		/* internal updates */
		sceneRoot.updateLogicalState(tpf);
		guiRoot.updateLogicalState(tpf);
		sceneRoot.updateGeometricState();
		guiRoot.updateGeometricState();

		/* render states */
		renderManager.render(tpf, context.isRenderable());
	}

	/* destroy */
	@Override
	public void destroy() {
		this.stop();
		logger.debug("... destroyed");
	}

	/* stop */
	@Override
	public void stop() {

		if (!stopped) {

			/* stop states */
			idleState.stop();
			calibrationState.stop();
			tutorialState.stop();
			gameState.stop();
			highscoreState.stop();
			hyperState.stop();

			/* stop sounds */
			soundManager.stopAllSounds();

			/* stop input */
			input.stop();

			/* stop audio renderer */
			this.getAudioRenderer().cleanup();

			/* stop super */
			super.stop();

			stopped = true;
			logger.debug("... stopped");
		}
	}

	/* action listener of app */
	private class AppActionListener implements ActionListener {
		@Override
		public void onAction(String name, boolean value, float tpf) {
			if (!value) {
				return;
			} else if (name.equals("exit")) {
				stop();
			} else if (name.equals("buzzer")) {
				App.buzzerPressed = true;
			} else if (name.equals("gazeDotsToggle")) {
				App.gazeDotsToggle = !App.gazeDotsToggle;
			}
		}
	}

	protected void loadNextState(FadableState state) {

		nextState = state;

		if (activeState != null) {

			/* outro active state first */
			activeState.outro();
			activeStateAboutToChange = true;
		} else {

			/* set next state immediately */
			setNextState();
		}

	}

	/* set active state */
	protected void setNextState() {

		/* attach next state */
		nextState.intro();
		activeState = nextState;
		nextState = null;
		activeStateAboutToChange = false;

		/* get active state and set inner state machine */
		if (activeState instanceof IdleState) {
			abortable = false;

			/* some resets */
			buzzerForRestartPressed = false;
			hyperState.hideDialogImmediatelly();

		} else {
			abortable = true;
		}
	}

	/* states need graphics settings for adjusting */
	public int getGraphicsSettings() {
		return graphicsSettings;
	}

	/* get scene root node */
	public Node getSceneRoot() {
		return sceneRoot;
	}

	/* get gui root node */
	public Node getGuiRoot() {
		return guiRoot;
	}

	/* start to load idle state */
	public void loadIdleState() {
		this.loadNextState(idleState);
		this.hyperState.setMusicTrack(MusicTrack.IDLE);
	}

	/* start to load calibration state */
	public void loadCalibrationState() {
		this.loadNextState(calibrationState);
		this.hyperState.setMusicTrack(MusicTrack.CALIBRATION);
	}

	/* start to load tutorial state */
	public void loadTutorialState() {
		this.loadNextState(tutorialState);
		this.hyperState.setMusicTrack(MusicTrack.TUTORIAL);
	}

	/* start to load game state */
	public void loadGameState() {
		this.loadNextState(gameState);
		this.hyperState.setMusicTrack(MusicTrack.GAME);
	}

	/* start to load highscore state */
	public void loadHighscoreState() {
		this.loadNextState(highscoreState);
		this.hyperState.setMusicTrack(MusicTrack.HIGHSCORE);
	}

	/* start to load survey state */
	public void loadSurveyState() {
		this.loadNextState(surveyState);
		this.hyperState.setMusicTrack(MusicTrack.SURVEY);
	}

	/* get cursor coordinates in pixel space */
	public Vector2f getPixelCursor() {
		return cursor;
	}

	/* get cursor position in relative space */
	public Vector2f getRelativeCursor() {
		return new Vector2f(cursor.x / windowWidth, cursor.y / windowHeight);
	}

	/* get window resolution */
	public Vector2f getWindowResolution() {
		return new Vector2f(windowWidth, windowHeight);
	}

	/* get primary gui font */
	public Font getPrimaryGuiFont() {
		return primaryGuiFont;
	}

	/* get secondary gui font */
	public Font getSecondaryGuiFont() {
		return secondaryGuiFont;
	}

	/* get tertiary gui font */
	public Font getTertiaryGuiFont() {
		return tertiaryGuiFont;
	}

	/* get primary world font */
	public Font getPrimaryWorldFont() {
		return primaryWorldFont;
	}

	/* get secondary world font */
	public Font getSecondaryWorldFont() {
		return secondaryWorldFont;
	}

	/* get letter width of font */
	public float getLetterWidth() {
		return letterWidth;
	}

	/* get letter height of font */
	public float getLetterHeight() {
		return letterHeight;
	}

	/* get font texture name */
	public String getFontTextureName() {
		return fontTextureName;
	}

	/* get current style of game as string */
	public String getCurrentGameStyleString() {
		switch (currentGameStyle) {
		case DIRECT:
			return "A";
		case DIRECT_WITH_GRID:
			return "B";
		case INDIRECT:
			return "C";
		default:
			return "A";
		}
	}

	/* get current style of game */
	public GameStyle getCurrentGameStyle() {
		return currentGameStyle;
	}

	/* set current style of game */
	public void setCurrentGameStyle(GameStyle currentGameStyle) {
		this.currentGameStyle = currentGameStyle;
	}

	/* fill settings with presets */
	protected void fillSettingsWithPreset() {

		switch (preset) {
		case HIGH_FULLSCREEN_1080: {

			graphicsSettings = 2;
			fullscreen = true;
			windowWidth = 1920;
			windowHeight = 1080;
			break;
		}
		case HIGH_FULLSCREEN_720: {

			graphicsSettings = 2;
			fullscreen = true;
			windowWidth = 1280;
			windowHeight = 720;
			break;
		}
		case HIGH_WINDOWED_720: {

			graphicsSettings = 2;
			fullscreen = false;
			windowWidth = 1280;
			windowHeight = 720;
			break;
		}
		case HIGH_FULLSCREEN_900: {

			graphicsSettings = 2;
			fullscreen = true;
			windowWidth = 1600;
			windowHeight = 900;
			break;
		}
		}
	}

	/* get input device */
	public Input getInput() {
		return this.input;
	}

	/* get current played time */
	public int getCurrentPlayedMiliSeconds() {
		return currentPlayedMiliSeconds;
	}

	/* set current played time */
	public void setCurrentPlayedMiliSeconds(int currentPlayedMiliSeconds) {
		this.currentPlayedMiliSeconds = currentPlayedMiliSeconds;
	}

	/* get current max multiplicator */
	public int getCurrentMaxMultiplicator() {
		return currentMaxMultiplicator;
	}

	/* set current max multiplicator */
	public void setCurrentMaxMultiplicator(int currentMaxMultiplicator) {
		this.currentMaxMultiplicator = currentMaxMultiplicator;
	}

	/* get current average multiplicator */
	public double getCurrentAvMultiplicator() {
		return currentAvMultiplicator;
	}

	/* set current average multiplicator */
	public void setCurrentAvMultiplicator(double currentAvMultiplicator) {
		this.currentAvMultiplicator = currentAvMultiplicator;
	}

	/* get current correct pictures */
	public int getCurrentCorrectPictures() {
		return currentCorrectPictures;
	}

	/* set current correct pictures */
	public void setCurrentCorrectPictures(int currentCorrectPictures) {
		this.currentCorrectPictures = currentCorrectPictures;
	}

	/* get current incorrect pictures */
	public int getCurrentIncorrectPictures() {
		return currentIncorrectPictures;
	}

	/* set current incorrect pictures */
	public void setCurrentIncorrectPictures(int currentIncorrectPictures) {
		this.currentIncorrectPictures = currentIncorrectPictures;
	}

	public int getCurrentScore() {
		return this.currentScore;
	}

	public void setCurrentScore(int score) {
		this.currentScore = score;
	}

	public String getCurrentPlayersName() {
		return currentPlayersName;
	}

	public void setCurrentPlayersName(String currentPlayersName) {
		this.currentPlayersName = currentPlayersName;
	}

	/* get messages */
	public ResourceBundle getMessages() {
		return messages;
	}

	/* set language */
	public void setLanguage(Language language) {
		this.language = language;
		switch (language) {
		case GERMAN:
			PictureOperations.setLanguage("German");
			SurveyOperations.setLanguage("German");

			break;
		default:
			PictureOperations.setLanguage("English");
			SurveyOperations.setLanguage("English");
			break;
		}

		messages = getSpecificMessages(language);
	}

	/* get current language */
	public Language getLanguage() {
		return this.language;
	}

	/* get specific message bundle */
	public ResourceBundle getSpecificMessages(Language language) {

		Locale currentLocale = null;

		switch (language) {
		case GERMAN:
			currentLocale = new Locale("de", "DE");
			break;
		default:
			currentLocale = new Locale("en", "US");
			break;
		}

		return ResourceBundle.getBundle("MessagesBundle", currentLocale);
	}

	/* create folders if necessary */
	protected void createFolders() {

		File dir;

		/* images */
		dir = new File("images/");
		dir.mkdir();

		/* gaze data */
		dir = new File("gazeData/");
		dir.mkdir();
	}

	public boolean isLastGameFinished() {
		return lastGameFinished;
	}

	public void setLastGameFinished(boolean lastGameFinished) {
		this.lastGameFinished = lastGameFinished;
	}

	public void updateHyperStateLanguage() {
		this.hyperState.updateTextContent();
	}

	public SoundManager getSoundManager() {
		return this.soundManager;
	}

	public void setDoSurvey(boolean doSurvey) {
		this.doSurvey = doSurvey;
	}

	public boolean getDoSurvey() {
		return doSurvey;
	}

}