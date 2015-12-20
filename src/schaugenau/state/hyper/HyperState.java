package schaugenau.state.hyper;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.jme3.audio.AudioNode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

import schaugenau.app.App;
import schaugenau.core.State;
import schaugenau.core.StaticEntity;
import schaugenau.font.Font;
import schaugenau.font.TextBox;
import schaugenau.gui.GuiDecoration;
import schaugenau.input.Input;
import schaugenau.input.TrackerInput;
import schaugenau.utilities.LerpValue;

/**
 * Distributed under the MIT License. (See accompanying file LICENSE or copy at
 * https://github.com/raphaelmenges/schaugenau/blob/master/src/LICENSE)
 * 
 * Hyper state. This state is active across different states and manages sound.
 * 
 * @author Raphael Menges
 *
 */

public class HyperState extends State {

	/** enumerations **/
	public enum MusicTrack {
		IDLE, CALIBRATION, TUTORIAL, GAME, HIGHSCORE, SURVEY
	};

	/** defines **/

	protected final float guiAdpaterAgentZOffset = 200;
	protected final float alphaFadeSpeed = 3f;

	/* curtain */
	protected final float curtainZ = -5;
	protected final float curtainMaxAlpha = 0.9f;

	/* gaze dots */
	protected final float gazeDotSpawningInterval = 0.5f;
	protected final float gazeDotDistanceForceSpawn = 0.2f;
	protected final float gazeDotZ = 10;

	/* dialog */
	protected final float decorationZ = 2f;
	protected final float restartDuration = 9;
	protected final float inputNotWorkingDuration = 15;
	protected final Vector3f backgroundPosition = new Vector3f(0, 0, -0.5f);

	/* restart */
	protected final Vector3f restartTextAPosition = new Vector3f(0, -0.85f, 0);
	protected final Vector3f restartTextBPosition = new Vector3f(0, -2.55f, 0);
	protected final float restartTextAScale = 0.5f;
	protected final float restartTextBScale = 0.65f;
	protected final Vector3f restartBuzzerPosition = new Vector3f(0, 1.8f, 0.0f);
	protected final Vector3f restartBuzzerFundamentPosition = new Vector3f(0, 0.55f, 0.5f);
	protected final float restartBuzzerAnimationOffset = 0.125f;
	protected final float restartBuzzerAnimationDuration = 0.75f;
	protected final float entityScale = 1.25f;

	/* lost input */
	protected final Vector3f lostInputTextPosition = new Vector3f(0, -2.95f, 0);
	protected final float lostInputTextScale = 0.45f;
	protected final Vector3f inputInstructionBoxPosition = new Vector3f(0, 1.55f, 0);
	protected final float inputInstructionBoxScale = 0.75f;
	protected final float inputInstructionMaxAlpha = 0.75f;

	/* music */
	protected final float musicFadingDuration = 0.5f;
	protected final float musicVolume = 1.0f;

	/** fields **/
	protected TrackerInput tracker;
	protected Node guiAdapterAgent;
	protected boolean visible;

	/* curtain */
	protected StaticEntity curtain;
	protected LerpValue alpha;
	protected Font font;

	/* gaze dots */
	protected List<GazeDot> gazeDots;
	protected Vector2f lastGazeDotPosition;
	protected float timeUntilNextGazeDot;

	/* restart */
	protected GuiDecoration restartDecoration;
	protected DecimalFormat timeFormat;
	protected float timeLeftUntilRestart;
	protected boolean restart;
	protected int restartTextAIndex;
	protected int restartTextBIndex;
	protected int restartBuzzerIndex;
	protected int inputLostTextIndex;
	protected float restartBuzzerAnimationTime;

	/* input lost */
	protected float timeLeftForWorkingInput;
	protected GuiDecoration inputNotWorkingDecoration;
	protected boolean inputNotWorking;
	protected EyePoint leftEyePoint;
	protected EyePoint rightEyePoint;
	protected Font inputInstructionFont;
	protected TextBox inputInstructionBox;
	protected LerpValue inputInstructionAlpha;

	/* music */
	protected AudioNode currentMusicTrack;
	protected AudioNode lastMusicTrack;

	protected AudioNode idleMusicTrack;
	protected AudioNode calibrationMusicTrack;
	protected AudioNode tutorialMusicTrack;
	protected AudioNode gameMusicTrack;
	protected AudioNode highscoreMusicTrack;
	protected AudioNode surveyMusicTrack;
	protected AudioNode hyperMusicTrack;

	protected float musicFading;

	/** methods **/

	/* constructor */
	public HyperState(App app, String name, boolean debugging, boolean loadMusic) {
		super(app, name, debugging);

		/* use own font object */
		this.font = new Font(this.app, "Font", "png", new ColorRGBA(1, 1, 1, 0), this.app.getLetterWidth(),
				app.getLetterHeight(), false, true);

		/* move gui adapter agent */
		this.guiAdapterAgent = new Node("GuiAdapterAgent");
		this.guiAdapterAgent.move(0, 0, guiAdpaterAgentZOffset);
		this.guiAdapter.getNode().attachChild(guiAdapterAgent);
		/* USE AGENT ONLY FOR GUI ELEMENTS !!! */

		/* create list for gaze dots */
		this.gazeDots = new ArrayList<>();

		/* initialize some variables */
		this.lastGazeDotPosition = new Vector2f();

		/* create curtain */
		this.curtain = new StaticEntity(app, name + "FadeCurtain", "Plane", "Unshaded", "White", true, "png", true,
				false, false, false);
		this.curtain.scale(guiAdapter.getWidth(), guiAdapter.getHeight(), 0);
		this.curtain.move(0, 0, curtainZ);
		this.curtain.setColorParameter(new ColorRGBA(0, 0, 0, 0));
		this.curtain.attachTo(guiAdapterAgent);

		/* restart text */
		this.restartDecoration = new GuiDecoration(this.app, this.guiAdapter, "RestartDecoration");
		this.restartDecoration.setLocalTranslation(0, 0, decorationZ);
		this.restartTextAIndex = this.restartDecoration.addText(this.font, schaugenau.font.Text.Alignment.CENTER, "",
				restartTextAPosition, restartTextAScale);
		this.restartTextBIndex = this.restartDecoration.addText(this.font, schaugenau.font.Text.Alignment.CENTER, "",
				restartTextBPosition, restartTextBScale);
		this.restartBuzzerIndex = this.restartDecoration.addStaticEntity("HyperBuzzer", "Unshaded", "LaGa", "png", true,
				this.restartBuzzerPosition, new Vector3f(), entityScale);
		this.restartDecoration.addStaticEntity("HyperBuzzerFundament", "Unshaded", "LaGa", "png", true,
				this.restartBuzzerFundamentPosition, new Vector3f(), entityScale);
		this.restartDecoration.addStaticEntity("HyperBuzzerBackground", "Unshaded", "LaGa", "png", true,
				this.backgroundPosition, new Vector3f(), entityScale);

		/* input not working text */
		this.inputNotWorkingDecoration = new GuiDecoration(this.app, this.guiAdapter, "InputNotWorkingGuiDecoration");
		this.inputNotWorkingDecoration.setLocalTranslation(0, 0, decorationZ);
		this.inputNotWorkingDecoration.addStaticEntity("HyperLostInputBackground", "Unshaded", "LaGa", "png", true,
				this.backgroundPosition, new Vector3f(), entityScale);
		this.inputLostTextIndex = this.inputNotWorkingDecoration.addText(font, schaugenau.font.Text.Alignment.CENTER,
				"", lostInputTextPosition, lostInputTextScale);

		/* eye points */
		this.leftEyePoint = new EyePoint(this.app, this.guiAdapter, "LeftEyePoint");
		this.leftEyePoint.attachTo(this.inputNotWorkingDecoration.getNode());
		this.rightEyePoint = new EyePoint(this.app, this.guiAdapter, "RightEyePoint");
		this.rightEyePoint.attachTo(this.inputNotWorkingDecoration.getNode());

		/* input instruction */
		this.inputInstructionFont = new Font(this.app, "Font", "png", new ColorRGBA(1, 1, 1, 1),
				this.app.getLetterWidth(), app.getLetterHeight(), false, true);
		this.inputInstructionBox = new TextBox(this.inputInstructionFont, schaugenau.font.TextBox.Alignment.CENTER);
		this.inputInstructionBox.setLocalTranslation(inputInstructionBoxPosition);
		this.inputInstructionBox.setLocalScale(inputInstructionBoxScale);
		this.inputInstructionBox.attachTo(this.inputNotWorkingDecoration.getNode());
		this.inputInstructionAlpha = new LerpValue(0);

		/* format time */
		timeFormat = new DecimalFormat();
		DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols();
		formatSymbols.setDecimalSeparator('.');
		timeFormat.setDecimalFormatSymbols(formatSymbols);
		timeFormat.applyPattern("#");

		/* tracker input */
		Input input = this.app.getInput();
		if (input.isTracker()) {
			this.tracker = (TrackerInput) input;
		}

		/* initialize members */
		this.restart = false;
		this.inputNotWorking = false;

		/* music */
		if (loadMusic) {
			idleMusicTrack = new AudioNode(app.getAssetManager(), "Music/Idle.ogg", false);
			idleMusicTrack.setLooping(true);
			idleMusicTrack.setPositional(false);

			calibrationMusicTrack = new AudioNode(app.getAssetManager(), "Music/Calibration.ogg", false);
			calibrationMusicTrack.setLooping(true);
			calibrationMusicTrack.setPositional(false);

			tutorialMusicTrack = new AudioNode(app.getAssetManager(), "Music/Tutorial.ogg", false);
			tutorialMusicTrack.setLooping(true);
			tutorialMusicTrack.setPositional(false);

			gameMusicTrack = new AudioNode(app.getAssetManager(), "Music/Game.ogg", false);
			gameMusicTrack.setLooping(true);
			gameMusicTrack.setPositional(false);

			highscoreMusicTrack = new AudioNode(app.getAssetManager(), "Music/Highscore.ogg", false);
			highscoreMusicTrack.setLooping(true);
			highscoreMusicTrack.setPositional(false);

			surveyMusicTrack = new AudioNode(app.getAssetManager(), "Music/Survey.ogg", false);
			surveyMusicTrack.setLooping(true);
			surveyMusicTrack.setPositional(false);

			hyperMusicTrack = new AudioNode(app.getAssetManager(), "Music/Hyper.ogg", false);
			hyperMusicTrack.setLooping(true);
			hyperMusicTrack.setPositional(false);
		}
	}

	/* update */
	public boolean update(float tpf, boolean buzzerPressed, boolean generateGazeDots) {
		boolean hasDetachedItself = super.update(tpf, buzzerPressed);

		/* update gaze dots */
		List<GazeDot> toBeRemoved = new LinkedList<>();
		for (GazeDot dot : this.gazeDots) {
			if (dot.update(tpf)) {
				toBeRemoved.add(dot);
			}
		}
		for (GazeDot dot : toBeRemoved) {
			dot.detach();
			this.gazeDots.remove(dot);
		}

		/* add new dot to gaze dots */
		if (generateGazeDots) {
			Vector2f cursor = this.guiAdapter.getCursor();

			if (timeUntilNextGazeDot <= 0 || cursor.distance(lastGazeDotPosition) >= gazeDotDistanceForceSpawn) {
				GazeDot gazeDot = new GazeDot(this.app, this.guiAdapter, "gazeDot",
						new Vector3f(cursor.x, cursor.y, gazeDotZ));
				gazeDot.attachTo(this.guiAdapterAgent);
				gazeDots.add(gazeDot);
				lastGazeDotPosition = cursor.clone();
				timeUntilNextGazeDot = gazeDotSpawningInterval;
			} else {
				timeUntilNextGazeDot -= tpf;
			}
		}

		/* only update visibile stuff when necessary */
		if (this.visible && this.alpha.getValue() <= this.alpha.getEpsilon()) {
			this.visible = false;
			this.setAlphaValues(0);

			/* lets fade in current music */
			if (lastMusicTrack != null) {
				lastMusicTrack.stop();
				lastMusicTrack.setVolume(0);
				rootNode.detachChild(lastMusicTrack);
				lastMusicTrack = null;
			}

			/* start normal music again */
			if (this.currentMusicTrack != null) {
				this.currentMusicTrack.play();
				currentMusicTrack.setVolume(0);
			}
			if (this.hyperMusicTrack != null) {
				this.hyperMusicTrack.stop();
			}

			musicFading = musicFadingDuration;

		} else if (!this.visible && this.alpha.getValue() > this.alpha.getEpsilon()) {
			this.visible = true;

			/* start special hyper music */
			if (this.currentMusicTrack != null) {
				this.currentMusicTrack.pause();
			}
			if (this.hyperMusicTrack != null) {
				this.hyperMusicTrack.play();
			}
		}

		if (this.visible) {

			/* update buzzer animation */
			restartBuzzerAnimationTime += tpf;
			restartBuzzerAnimationTime = restartBuzzerAnimationTime % restartBuzzerAnimationDuration;
			this.restartDecoration.getStaticEntity(restartBuzzerIndex)
					.setLocalTranslation(this.restartBuzzerPosition.clone().add(0,
							restartBuzzerAnimationOffset * (float) Math
									.sin(restartBuzzerAnimationTime * Math.PI * 2 / restartBuzzerAnimationDuration),
							0));

			this.setAlphaValues(this.alpha.getValue());
		}

		/* update music */
		musicFading -= tpf;
		musicFading = Math.max(0, musicFading);
		float t = musicFading / musicFadingDuration;

		/* last music */
		if (lastMusicTrack != null) {

			/* detach last music track if possible */
			if (t <= 0) {
				lastMusicTrack.stop();
				lastMusicTrack.setVolume(0);
				rootNode.detachChild(lastMusicTrack);
				lastMusicTrack = null;
			} else {
				/* otherwise, fade it out */
				lastMusicTrack.setVolume(t * musicVolume);
			}
		}

		/* current music */
		float currentVolume = (1.0f - t) * musicVolume;
		if (currentMusicTrack != null && currentMusicTrack.getVolume() != currentVolume) {
			currentMusicTrack.setVolume(currentVolume);
		}

		return hasDetachedItself;
	}

	/* attach */
	@Override
	public void attach() {
		super.attach();

		this.timeUntilNextGazeDot = this.gazeDotSpawningInterval;

		this.restart = false;
		this.inputNotWorking = false;
		this.restartBuzzerAnimationTime = 0;

		/* alpha */
		this.alpha = new LerpValue(0);

		/* visibility */
		this.visible = false;

		/* set texts content */
		this.updateTextContent();
	}

	/* detach */
	@Override
	public void detach() {
		super.detach();
	}

	/* stop */
	@Override
	public void stop() {

		/* clean up music */
		if (lastMusicTrack != null) {
			lastMusicTrack.stop();
		}
		if (currentMusicTrack != null) {
			currentMusicTrack.stop();
		}

		/* stop super */
		super.stop();
	}

	/* hide the restart dialog immediately */
	public void hideDialogImmediatelly() {
		alpha.setValue(0);
	}

	/* returns whether restart is necessary */
	public boolean showDialog(float tpf, boolean restart, boolean inputNotWorking) {

		boolean doReset = false;

		/* input */
		if (!inputNotWorking) {
			if (this.inputNotWorking) {
				this.inputNotWorking = false;
			}
		} else {

			/* attach */
			if (!this.inputNotWorking) {
				this.inputNotWorking = true;
				if (!this.restart) {

					/* only show when no other dialog there */
					this.inputNotWorkingDecoration.attachTo(guiAdapterAgent);
					this.restartDecoration.detach();
				}
				this.timeLeftForWorkingInput = this.inputNotWorkingDuration;
			}

			/* update */
			if (!this.restart) {
				this.timeLeftForWorkingInput -= tpf;
				if (this.timeLeftForWorkingInput <= 0) {

					/* restart of game */
					doReset = true;
					this.inputNotWorking = false;

				}

				/* show user its eyes */
				if (this.tracker != null) {

					/* update eye points */
					leftEyePoint.update(tpf, this.tracker.isLeftEyePositionAvailable(),
							this.tracker.getLeftEyePosition());
					rightEyePoint.update(tpf, this.tracker.isRightEyePositionAvailable(),
							this.tracker.getRightEyePosition());

					/* update instruction */
					if (leftEyePoint.getCombinedLocalScale() <= 0 && rightEyePoint.getCombinedLocalScale() <= 0) {
						inputInstructionAlpha.update(tpf, 1, this.inputInstructionMaxAlpha);
					} else {
						inputInstructionAlpha.update(tpf, 1, 0);
					}
				}
			}
		}

		/* restart via buzzer */
		if (!restart) {

			/* no buzzer pressed */
			if (this.restart) {
				this.restart = false;

				/* check, whether input works at the moment */
				if (this.inputNotWorking) {

					/* if not, attach correct decoration and detach previous */
					this.inputNotWorkingDecoration.attachTo(guiAdapterAgent);
					this.timeLeftForWorkingInput = this.inputNotWorkingDuration;
					this.restartDecoration.detach();
				}
			}

		} else {

			/* attach */
			if (!this.restart) {
				this.restart = true;
				this.restartDecoration.attachTo(guiAdapterAgent);
				this.inputNotWorkingDecoration.detach();
				this.timeLeftUntilRestart = this.restartDuration;
			}

			/* update */
			this.timeLeftUntilRestart -= tpf;
			if (this.timeLeftUntilRestart <= 0) {

				/* restart game */
				doReset = true;
				this.restart = false;

			}

			/* visual stuff */
			this.restartDecoration.getText(restartTextBIndex)
					.setContent(app.getMessages().getString("hyper.restartB-1") + " "
							+ timeFormat.format(Math.max(this.timeLeftUntilRestart, 0)) + " "
							+ app.getMessages().getString("hyper.restartB-2"));

		}

		/* curtain update */
		if (this.restart || this.inputNotWorking) {
			alpha.update(tpf, alphaFadeSpeed, 1);
		} else {
			alpha.update(tpf, alphaFadeSpeed, 0);
		}

		return doReset;
	}

	/* update texts' content */
	public void updateTextContent() {

		this.restartDecoration.getText(this.restartTextAIndex)
				.setContent(app.getMessages().getString("hyper.restartA"));
		this.inputNotWorkingDecoration.getText(this.inputLostTextIndex)
				.setContent(app.getMessages().getString("hyper.inputLost"));
		this.inputInstructionBox.setContent(app.getMessages().getString("hyper.inputInstruction"));
	}

	/* set current music */
	public void setMusicTrack(MusicTrack track) {

		/* local variable for next track */
		AudioNode nextTrack = null;

		/* decide, which music track to play next */
		switch (track) {
		case IDLE:
			nextTrack = idleMusicTrack;
			break;
		case CALIBRATION:
			nextTrack = calibrationMusicTrack;
			break;
		case TUTORIAL:
			nextTrack = tutorialMusicTrack;
			break;
		case GAME:
			nextTrack = gameMusicTrack;
			break;
		case HIGHSCORE:
			nextTrack = highscoreMusicTrack;
			break;
		case SURVEY:
			nextTrack = surveyMusicTrack;
			break;
		}

		/* only change when necessary */
		if (nextTrack != currentMusicTrack) {

			/* reset the last track because there will be new one */
			if (lastMusicTrack != null) {
				lastMusicTrack.stop();
				lastMusicTrack.setVolume(0);
				rootNode.detachChild(lastMusicTrack);
				lastMusicTrack = null;
			}

			/* remember last track for fading */
			lastMusicTrack = currentMusicTrack;

			/* set next one as current */
			currentMusicTrack = nextTrack;
			rootNode.attachChild(currentMusicTrack);

			/* set initial volume to zero and start playing */
			currentMusicTrack.setVolume(0);
			currentMusicTrack.play();

			/* set fading */
			musicFading = musicFadingDuration;
		}

	}

	/* set alpha of all used decoration */
	protected void setAlphaValues(float value) {
		/* set alphas */
		this.curtain.setColorParameter(new ColorRGBA(0, 0, 0, value * this.curtainMaxAlpha));
		this.font.setColor(new ColorRGBA(1, 1, 1, alpha.getValue()));
		this.restartDecoration.setColorParameterOfEntities(new ColorRGBA(1, 1, 1, value));
		this.inputNotWorkingDecoration.setColorParameterOfEntities(new ColorRGBA(1, 1, 1, value));
		this.leftEyePoint.setColorParameter(new ColorRGBA(1, 1, 1, value));
		this.rightEyePoint.setColorParameter(new ColorRGBA(1, 1, 1, value));
		this.inputInstructionFont.setColor(new ColorRGBA(1, 1, 1, value * inputInstructionAlpha.getValue()));
	}
}
