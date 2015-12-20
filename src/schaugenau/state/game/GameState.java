package schaugenau.state.game;

import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.jme3.collision.CollisionResults;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.texture.FrameBuffer;
import com.jme3.texture.Image.Format;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;

import schaugenau.app.App;
import schaugenau.app.App.GameStyle;
import schaugenau.core.AnimNode;
import schaugenau.core.FadableState;
import schaugenau.core.SoundManager.Sound;
import schaugenau.core.StaticEntity;
import schaugenau.database.PictureOperations;
import schaugenau.font.Font;
import schaugenau.font.Text;
import schaugenau.gui.GuiDecoration;
import schaugenau.utilities.DebugLine;
import schaugenau.utilities.Helper;

/**
 * Distributed under the MIT License. (See accompanying file LICENSE or copy at
 * https://github.com/raphaelmenges/schaugenau/blob/master/src/LICENSE)
 * 
 * Game state.
 * 
 * @author Raphael Menges
 *
 */

public class GameState extends FadableState {

	/** enumerations **/
	public enum InnerGameState {
		FLOWER_SPAWNING, TAG_SIGN_APPEREANCE, BUTTERFLY_HIDING, PICTURE_SPAWNING, PICTURE_MODE
	}

	/** defines **/

	/* camera */
	protected final float[] cameraStartAndEndAngles = { (float) (-0.25 * Math.PI), (float) Math.PI, 0 };
	protected final float cameraAngleTransitionDuration = 3f;
	protected final float cameraAngleTransitionAfterButterflyIsCaughtWaitDuration = 1.0f;

	/* gui */
	protected final float pictureModeHintShowOffset = 0.25f;
	protected final float pictureModeHintScale = 0.5f;
	protected final float pictureModeHintAnimationDuration = 2f;
	protected final float bottomHudHideOffset = -2f;
	protected final float scoreTextScale = 0.3f;
	protected final Vector3f scoreTextOffset = new Vector3f(0, -0.15f, 0.0f);
	protected final float scoreTextSpeed = 4.0f;

	/* flowers */
	protected final float timeBetweenPickables = 1f;
	protected final float deviationOfTimeBetweenPickables = 0.3f;
	protected final float timeBeforeFirstPickable = 1f;

	/* scores */
	protected final int scoreRose = 50;
	protected final int scoreLily = 30;
	protected final int scoreTulip = 20;
	protected final int scoreDandelion = 10;

	/* frequencies */
	protected final float freqRose = 5;
	protected final float freqLily = 10;
	protected final float freqTulip = 20;
	protected final float freqDandelion = 30;
	protected final float freqSpiderweb = 0;
	protected final float freqRaiseSpiderwebPerSecond = 0.25f;

	/* picture mode */
	protected final float timeBetweenPictures = 7;
	protected final float deviationOfTimeBetweenPictures = 2;
	protected final float waitBeforeTagSignAppears = 6.5f;
	protected final float waitBeforeButterflyHides = 1.0f;
	protected final float waitBeforePicturesAppear = 0.0f;
	protected final float durationPictureSelection = 9.0f;
	protected final float waitBeforePickablesSpawnAfterPicturesAppear = 10.0f;
	protected final float pictureModeFocusAreaWidth = 0.45f;
	protected final float pictureModeTimeIndicatorWidth = 5;
	protected final float pictureModeTimeIndicatorZ = -50;
	protected final ColorRGBA pictureModeTimeIndicatorColor = new ColorRGBA(1, 1, 0.6f, 0.15f);

	/* butterfly */
	protected final float butterflyScale = 0.7f;
	protected final float butterflyX = 3.0f;
	protected final float butterflyY = 1.7f;
	protected final float butterflyCollisionDepth = 2.0f;
	protected final float timeToHideButterfly = 5f;
	protected final Vector3f upperButterflyCollisionOffset = new Vector3f(0.75f, 0.6f, 1);
	protected final Vector3f centerButterflyCollisionOffset = new Vector3f(0.5f, 0, 1);
	protected final Vector3f lowerButterflyCollisionOffset = new Vector3f(0.45f, -0.5f, 1);
	protected final Vector3f butterflyHidePosition = new Vector3f(0, -6, 0);
	protected final float butterflyQuadZOffset = -100;

	/* other */
	protected final float cullZ = 10;
	protected final int caughtToHighscoreDuration = 2;
	protected final float speedUpPerSecond = 0.009f;
	protected final int minimumMultiplicator = 1;
	protected final int maximumMultiplicator = 99;
	protected final int minimumGameScore = 0;
	protected final int maximumGameScore = 99999;

	/* control type */
	protected final Vector2f gridSize = new Vector2f(3, 3);
	protected final float indirectControlFadeSpeed = 1.0f;

	/** fields **/

	/* game style */
	protected boolean useGridForButterfly;
	protected boolean useGridForPickables;
	protected boolean useIndirectControl;

	/* camera */
	protected float cameraAngleTransitionTime;
	protected float cameraAngleTransitionAfterButterflyIsCaughtWaitTime;

	/* gui */
	protected HudFooter hudFooter;
	protected AnimNode pictureModeHintNode;
	protected GuiDecoration pictureModeHint;

	/* environment */
	protected Environment environment;

	/* inner state machine */
	protected InnerGameState state;

	/* pickables list */
	LinkedList<Pickable> pickablesList;
	LinkedList<MortalEmitter> pickEmitterList;

	/* multiplicator and score */
	protected int multiplicator;
	protected int gameScore;
	protected int oldMultiplicator;

	/* picture mode */
	protected TagSign tagSign = null;
	protected Picture rightPicture;
	protected Picture leftPicture;
	protected boolean rightPictureIsCorrect;
	protected boolean rightPictureChosen;
	protected boolean leftPictureChosen;
	protected boolean pictureModeFinished;
	protected boolean pictureModeEndActionsHaveBeenDone;
	protected ExecutorService pictureService;
	protected Callable<Picture> callableRightPicture;
	protected Callable<Picture> callableLeftPicture;
	protected Future<Picture> futureRightPicture;
	protected Future<Picture> futureLeftPicture;
	protected int pictureModeHintTextIndex;
	protected ExecutorService pictureInformationService;
	protected static int staticCorrectPictureID;
	protected static boolean staticCorrectPictureWasChosen;
	protected StaticEntity pictureModeTimeIndicator;
	protected boolean correctPictureWasChosen;

	/* flowers */
	protected float timeUntilNextPickableSpawn;
	protected float timeUntilFirstPickableMaySpawn;
	protected LinkedList<Text> scoreTexts;
	protected Font scoreTextFont;

	/* butterfly */
	protected Butterfly butterfly;
	protected boolean butterflyCaughtBySpiderweb;
	protected float indirectControlWeight;
	protected Vector3f indirectButterflyPosition;
	protected float timeOfHidingButterfly;
	protected boolean hideButterfly;
	protected ViewPort butterflyView;
	protected FrameBuffer butterflyBuffer;
	protected Texture2D butterflyTexture;
	protected Node butterflyViewRoot;
	protected StaticEntity butterflyQuad;

	/* other */
	protected float timeUntilHighscore;
	protected float gameSpeed;
	protected double freqRaiseSpiderweb;
	protected ImageLoader imageLoader;
	protected float innerStateTime;
	protected float durationUntilPictureMode;
	protected long startMiliSeconds;
	protected boolean firstFlowerMode;
	protected float pauseTime;

	/* debugging */
	protected LinkedList<DebugLine> butterflyDebugLines;

	/* database */
	protected PictureOperations pictureOperation;

	/** methods **/

	/* constructor */
	public GameState(App app, String name, boolean debugging) {
		super(app, name, debugging);

		/* database */
		pictureOperation = new PictureOperations();

		/* hud footer */
		hudFooter = new HudFooter(this.app, this, this.guiAdapter, new Vector3f(0, this.guiAdapter.getBottom(), 0),
				new Vector3f(0, this.guiAdapter.getBottom() + bottomHudHideOffset, 0));
		hudFooter.attachTo(guiAdapter.getNode());

		/* picture mode hint */
		pictureModeHintNode = new AnimNode(this, "PictureModeHindNode",
				new Vector3f(0, this.guiAdapter.getBottom() + pictureModeHintShowOffset, 0),
				new Vector3f(0, this.guiAdapter.getBottom() + bottomHudHideOffset, 0), pictureModeHintAnimationDuration,
				false);
		this.guiAdapter.attachChild(pictureModeHintNode.getNode());
		pictureModeHint = new GuiDecoration(this.app, this.guiAdapter, "PictureModeHint");
		pictureModeHint.attachTo(pictureModeHintNode.getNode());

		pictureModeHintTextIndex = pictureModeHint.addText(this.app.getSecondaryGuiFont(),
				schaugenau.font.Text.Alignment.CENTER, "", new Vector3f(), pictureModeHintScale);

		/* score texts */
		scoreTexts = new LinkedList<Text>();
		scoreTextFont = new Font(this.app, "ShortFaded", this.app.getFontTextureName(), "png",
				new ColorRGBA(1f, 1f, 1f, 0.6f), this.app.getLetterWidth(), this.app.getLetterHeight(), true, false);

		/* environment */
		environment = new Environment(app);
		if (app.getGraphicsSettings() > 0) {
			environment.attachTo(rootNode);
		}

		/* flower */
		pickablesList = new LinkedList<Pickable>();
		pickEmitterList = new LinkedList<MortalEmitter>();

		/* butterfly viewport */
		int width = (int) this.app.getWindowResolution().x;
		int height = (int) this.app.getWindowResolution().y;
		butterflyView = app.getRenderManager().createPreView("ButterflyView", this.app.getCamera());
		butterflyView.setClearFlags(true, true, true);
		butterflyView.setBackgroundColor(new ColorRGBA(0, 0, 0, 0));

		/* butterfly buffer */
		butterflyBuffer = new FrameBuffer(width, height, 1);

		/* butterfly texture */
		butterflyTexture = new Texture2D(width, height, Format.RGBA8);
		butterflyTexture.setMinFilter(Texture.MinFilter.Trilinear);
		butterflyTexture.setMagFilter(Texture.MagFilter.Bilinear);

		/* connect butterfly things */
		butterflyBuffer.setDepthBuffer(Format.Depth);
		butterflyBuffer.setColorTexture(butterflyTexture);
		butterflyView.setOutputFrameBuffer(butterflyBuffer);

		/* create root node for butterfly view */
		butterflyViewRoot = new Node("ButterflyViewRoot");
		butterflyView.attachScene(butterflyViewRoot);

		/* butterfly itself */
		butterfly = new Butterfly(app);
		butterfly.setLocalScale(butterflyScale);
		butterfly.attachTo(butterflyViewRoot);

		/* update once for sake of jmonkey */
		butterflyViewRoot.updateLogicalState(0);
		butterflyViewRoot.updateGeometricState();

		/* butterfly quad */
		butterflyQuad = new StaticEntity(app, name, "ScreenFillingQuad", "Unshaded", "Black", true, "png", true, true,
				false, false);
		butterflyQuad.setLocalTranslation(0, 0, butterflyQuadZOffset);
		butterflyQuad.setTexture(butterflyTexture);
		butterflyQuad.setLocalScale(guiAdapter.getWidth(), guiAdapter.getHeight(), 1);
		butterflyQuad.attachTo(guiAdapter.getNode());

		/* imageLoader */
		imageLoader = new ImageLoader(app);

		/* create thread pool for pictures */
		pictureService = Executors.newFixedThreadPool(2);
		pictureInformationService = Executors.newFixedThreadPool(2);

		/* debugging stuff */
		if (debugging) {
			butterflyDebugLines = new LinkedList<DebugLine>();
			rootNode.attachChild(schaugenau.utilities.Helper.drawAxes(app));
		}

		/* picture mode time indicator */
		pictureModeTimeIndicator = new StaticEntity(app, name, "ScreenFillingQuad", "Unshaded", "Stripe", true, "png",
				true, true, false, false);
		pictureModeTimeIndicator.setLocalScale(pictureModeTimeIndicatorWidth, guiAdapter.getHeight(), 1);
		pictureModeTimeIndicator.setColorParameter(pictureModeTimeIndicatorColor);
		pictureModeTimeIndicator.attachTo(this.guiAdapter.getNode());
	}

	/* update */
	@Override
	public boolean update(float tpf, boolean buzzerPressed) {

		/* call super update */
		boolean hasDetachedItself = super.update(tpf, buzzerPressed);

		if (!paused) {

			/* camera animation */
			if (!butterflyCaughtBySpiderweb) {
				cameraAngleTransitionTime += tpf;
			} else {
				if (cameraAngleTransitionAfterButterflyIsCaughtWaitTime > cameraAngleTransitionAfterButterflyIsCaughtWaitDuration) {

					cameraAngleTransitionTime -= tpf;
				}
				cameraAngleTransitionAfterButterflyIsCaughtWaitTime += tpf;
			}
			cameraAngleTransitionTime = Math.min(cameraAngleTransitionDuration, Math.max(0, cameraAngleTransitionTime));

			float t = cameraAngleTransitionTime / cameraAngleTransitionDuration;
			t = (float) Math.sqrt(Math.sqrt(Math.sqrt(t)));
			this.setCamera(cameraPosition,
					new Quaternion().slerp(new Quaternion(cameraStartAndEndAngles), new Quaternion(cameraAngles), t));
		}

		/* update emitter */
		handleEmitters(tpf);

		/* update butterfly viewport and buffer */
		butterflyViewRoot.updateLogicalState(tpf);
		butterflyViewRoot.updateGeometricState();

		return hasDetachedItself;
	}

	/* fade in, returns if finished */
	@Override
	protected boolean fadeIn(float tpf) {
		boolean fadingDone = super.fadeIn(tpf);

		if (!paused) {
			/* just update the environment */
			environment.update(tpf, true, false, false, false, true);
		}

		if (fadingDone) {
			hudFooter.setVisible(true);
		}

		return fadingDone;

	}

	/* running */
	@Override
	protected void running(float tpf, boolean buzzerPressed) {
		super.running(tpf, buzzerPressed);

		/* tpf are depending on speed of game */
		float sTpf = gameSpeed * tpf;

		/* check for pause */
		if (!paused) {

			/* CHECK FOR STATE CHANGE */

			/* decide whether not to update pickables or to do it */
			if (butterflyCaughtBySpiderweb) {
				timeUntilHighscore -= tpf;

				/* state change if butterfly is caught */
				if (timeUntilHighscore < 0) {
					this.prepareAndLoadHighscoreState();
				}
			} else {
				/* speed up tpf */
				gameSpeed += speedUpPerSecond * tpf;
			}

			/* raise probability for spiderweb to appear */
			freqRaiseSpiderweb += tpf * freqRaiseSpiderwebPerSecond;

			/* INNER STATE MACHINE */

			/* switch inner states */
			if (!butterflyCaughtBySpiderweb) {
				switch (state) {

				case FLOWER_SPAWNING: {

					manageFlowermode(sTpf);

					/* change of state */
					if (innerStateTime >= durationUntilPictureMode) {

						/* calculate time until next picture mode */
						durationUntilPictureMode = schaugenau.utilities.Helper
								.getValueWithDeviation(timeBetweenPictures, deviationOfTimeBetweenPictures);

						/* do picture instanciation in separate threads */
						rightPictureIsCorrect = Math.random() < 0.5;

						callableRightPicture = new Callable<Picture>() {
							@Override
							public Picture call() {
								return new Picture(app, guiAdapter, true, rightPictureIsCorrect, imageLoader);
							}
						};
						callableLeftPicture = new Callable<Picture>() {
							@Override
							public Picture call() {
								return new Picture(app, guiAdapter, false, !rightPictureIsCorrect, imageLoader);
							}
						};

						/* kill futures if still running */
						if (futureRightPicture != null) {
							futureRightPicture.cancel(true);
							futureRightPicture = null;
						}
						if (futureLeftPicture != null) {
							futureLeftPicture.cancel(true);
							futureLeftPicture = null;
						}

						/* start threads */
						futureRightPicture = pictureService.submit(callableRightPicture);
						futureLeftPicture = pictureService.submit(callableLeftPicture);

						/* set next state */
						innerStateTime = 0;
						state = InnerGameState.TAG_SIGN_APPEREANCE;
					}
					break;
				}
				case TAG_SIGN_APPEREANCE: {

					/* change of state */
					if (innerStateTime >= waitBeforeTagSignAppears) {

						/* create new tag sign */
						if (tagSign != null) {
							tagSign.detach();
							tagSign = null;
						}
						tagSign = new TagSign(app, app.getPrimaryGuiFont(), imageLoader.getCurrentTag());
						tagSign.attachTo(guiAdapter.getNode());

						/* set next state */
						innerStateTime = 0;
						state = InnerGameState.BUTTERFLY_HIDING;
					}
					break;
				}
				case BUTTERFLY_HIDING: {

					/* change of state */
					if (innerStateTime >= waitBeforeButterflyHides) {

						/* hide butterfly */
						hideButterfly = true;
						timeOfHidingButterfly = 0;

						/* set next state */
						innerStateTime = 0;
						state = InnerGameState.PICTURE_SPAWNING;
					}
					break;
				}

				case PICTURE_SPAWNING: {

					/* change of state */
					if (innerStateTime >= waitBeforePicturesAppear) {

						/* get right picture from future object */
						if (rightPicture != null) {
							rightPicture.detach();
							rightPicture = null;
						}

						try {
							rightPicture = futureRightPicture.get();
						} catch (InterruptedException | ExecutionException e) {

							/* then do the work now */
							rightPicture = new Picture(app, guiAdapter, true, rightPictureIsCorrect, imageLoader);
						}

						/* get left picture from future object */
						if (leftPicture != null) {
							leftPicture.detach();
							leftPicture = null;
						}

						try {
							leftPicture = futureLeftPicture.get();
						} catch (InterruptedException | ExecutionException e) {

							/* then do the work now */
							leftPicture = new Picture(app, guiAdapter, false, !rightPictureIsCorrect, imageLoader);
						}

						/* start loading next images during picture mode */
						imageLoader.loadNextImages();

						/* attach already created pictures */
						rightPicture.attachTo(guiAdapter.getNode());
						leftPicture.attachTo(guiAdapter.getNode());

						/* hide hud and show hint */
						hudFooter.setVisible(false);
						pictureModeHintNode.setVisible(true);

						/* set next state */
						innerStateTime = 0;
						state = InnerGameState.PICTURE_MODE;
					}
					break;
				}
				case PICTURE_MODE: {

					managePictureMode(sTpf, tpf);

					if (pictureModeFinished) {
						if (!pictureModeEndActionsHaveBeenDone) {
							this.pictureModeEndActionsHaveBeenDone = true;

							this.correctPictureWasChosen = (rightPictureIsCorrect && rightPictureChosen)
									|| (!rightPictureIsCorrect && leftPictureChosen);

							/* show hud again */
							hudFooter.setVisible(true);
							pictureModeHintNode.setVisible(false);

							/* unhide butterfly */
							timeOfHidingButterfly = 0;

							boolean somePictureWasChosen = rightPictureChosen || leftPictureChosen;

							/* show butterfly with evaluation */
							if (somePictureWasChosen) {
								this.butterfly.playPictureModeAnimation(leftPictureChosen, correctPictureWasChosen);
							}

							/* tell spider webs to get ready */
							this.firstFlowerMode = false;

							/*
							 * update player information for database and play
							 * sound
							 */
							if (somePictureWasChosen) {
								if (correctPictureWasChosen) {
									this.app.setCurrentCorrectPictures(this.app.getCurrentCorrectPictures() + 1);
									this.app.getSoundManager().playSound(Sound.CORRECT_PICTURE, true);

								} else {
									this.app.setCurrentIncorrectPictures(this.app.getCurrentIncorrectPictures() + 1);
									this.app.getSoundManager().playSound(Sound.INCORRECT_PICTURE, true);
								}
							}
							if (this.multiplicator > this.app.getCurrentMaxMultiplicator()) {
								this.app.setCurrentMaxMultiplicator(this.multiplicator);
							}

							/* update picture information for database */
							if (this.rightPictureIsCorrect) {
								staticCorrectPictureID = this.rightPicture.getID();
							} else {
								staticCorrectPictureID = this.leftPicture.getID();
							}
							staticCorrectPictureWasChosen = correctPictureWasChosen;

							/* do it in a thread */
							pictureInformationService.submit(new Runnable() {
								@Override
								public void run() {
									pictureOperation.incrementUsedAsCorrect(staticCorrectPictureID);
									if (staticCorrectPictureWasChosen) {
										pictureOperation.incrementChosenAsCorrect(staticCorrectPictureID);
									}
								}
							});

						}
					}

					/* spawn some flowers to avoid big gap after pictures */
					if (innerStateTime >= waitBeforePickablesSpawnAfterPicturesAppear) {
						manageFlowermode(sTpf);
					}

					/* change of state when no more pictures are visible */
					if (leftPicture.outOfSight() && rightPicture.outOfSight()) {
						pictureModeFinished = false;
						pictureModeEndActionsHaveBeenDone = false;
						rightPictureChosen = false;
						leftPictureChosen = false;

						/* refresh hud footer with new multiplicator */
						hudFooter.setMultiplicator(multiplicator);

						/* play multiplicator sound */
						if (multiplicator <= minimumMultiplicator) {
							app.getSoundManager().playSound(Sound.MULTIPLICATOR_DOWN, true);
						} else {
							app.getSoundManager().playSound(Sound.MULTIPLICATOR_UP, true);
						}

						/* set next state */
						innerStateTime = 0;
						state = InnerGameState.FLOWER_SPAWNING;

					}
					break;
				}
				}
			}

			innerStateTime += sTpf;

			/* BUTTERFLY CONTROL */

			/* butterfly control */
			Vector3f cursorWorldPosition = schaugenau.utilities.Helper.mousePickingZ(app, app.getPixelCursor(), 0.0f);
			Vector3f butterflyPosition = cursorWorldPosition.clone();

			/* use indirect control of butterfly */
			if (useIndirectControl) {
				butterflyPosition = handleIndirectButterflyControl(sTpf, butterflyPosition);
			}

			/* clamp position of butterfly */
			schaugenau.utilities.Helper.clamp(butterflyPosition, -butterflyX, butterflyX, -butterflyY, butterflyY, 0f,
					0f);

			/* grid */
			if (useGridForButterfly) {
				Vector2f gridCoord = schaugenau.utilities.Helper.snapToGrid(gridSize,
						new Vector2f(-butterflyX, -butterflyY), new Vector2f(butterflyX, butterflyY),
						new Vector2f(butterflyPosition.x, butterflyPosition.y));
				butterflyPosition.x = gridCoord.x;
				butterflyPosition.y = gridCoord.y;
			}

			/* butterfly hiding */
			if (hideButterfly) {
				timeOfHidingButterfly += sTpf;
			} else {
				timeOfHidingButterfly -= sTpf;
			}
			timeOfHidingButterfly = Math.min(timeToHideButterfly, Math.max(0, timeOfHidingButterfly));
			float butterflyHidingValue = timeOfHidingButterfly / timeToHideButterfly;
			butterflyPosition = butterflyPosition.interpolate(butterflyHidePosition, butterflyHidingValue);

			/* update butterfly */
			butterflyHidingValue = Math.min(1, 2 * (float) Math.sqrt(butterflyHidingValue));
			float butterflyAlpha = butterfly.update(sTpf, gameSpeed, butterflyPosition, butterflyCaughtBySpiderweb,
					cursorWorldPosition);
			butterflyQuad.setColorParameter(new ColorRGBA(1, 1, 1, butterflyAlpha * (1 - butterflyHidingValue)));

			/* OTHER UPDATES */

			/* terrain */
			environment.update(sTpf, !butterflyCaughtBySpiderweb, state == InnerGameState.PICTURE_MODE,
					pictureModeFinished || state == InnerGameState.FLOWER_SPAWNING, correctPictureWasChosen, false);

			/* update gui */
			hudFooter.update(sTpf);

			/* tag sign */
			if (tagSign != null) {
				tagSign.update(sTpf, state == InnerGameState.PICTURE_MODE);
			}

			/* update score texts */
			LinkedList<Text> scoreTextsToBeRemovedList = new LinkedList<Text>();
			for (Text scoreText : scoreTexts) {
				scoreText.move(0, 0, scoreTextSpeed * sTpf);
				if (scoreText.getLocalTranslation().z > cameraPosition.z) {
					scoreTextsToBeRemovedList.add(scoreText);
				}
			}
			for (Text scoreText : scoreTextsToBeRemovedList) {
				scoreText.detach();
				scoreTexts.remove(scoreText);
			}

			/* updates which must stop when butterfly is caught */
			if (!butterflyCaughtBySpiderweb) {

				/* pickables */
				updatePickables(sTpf);

				/* update average multiplicator */
				double pastTime = ((double) ((System.currentTimeMillis() - this.startMiliSeconds) / 1000.0f)
						- this.pauseTime);
				double currentTime = pastTime + tpf;
				app.setCurrentAvMultiplicator(app.getCurrentAvMultiplicator() * (pastTime / currentTime)
						+ this.multiplicator * (tpf / currentTime));
			}
		} else {
			/* count wasted time */
			this.pauseTime += tpf;
		}
	}

	/* fade out, returns if finished */
	@Override
	protected boolean fadeOut(float tpf) {
		boolean fadingDone = super.fadeOut(tpf);

		/* play end sound */
		if (this.butterflyCaughtBySpiderweb) {
			app.getSoundManager().playSound(Sound.END, false);
		}

		return fadingDone;
	}

	/* attach */
	@Override
	protected void attach() {
		super.attach();

		/* simple presets */
		multiplicator = minimumMultiplicator;
		gameScore = minimumGameScore;
		gameSpeed = 1;
		freqRaiseSpiderweb = 0;

		/* delete pickables */
		for (Pickable pickable : pickablesList) {
			pickable.detach();
		}
		pickablesList.clear();

		/* delete emitter */
		for (MortalEmitter mortalEmitter : pickEmitterList) {
			rootNode.detachChild(mortalEmitter.getEmitter());
		}
		pickEmitterList.clear();

		/* delete score texts */
		for (Text scoreText : scoreTexts) {
			scoreText.detach();
		}
		scoreTexts.clear();

		/* image loader reset */
		imageLoader.loadNextImages();

		/* detach tag sign */
		if (tagSign != null) {
			tagSign.detach();
			tagSign = null;
		}

		/* detach pictures */
		if (leftPicture != null) {
			leftPicture.detach();
			leftPicture = null;
		}

		if (rightPicture != null) {
			rightPicture.detach();
			rightPicture = null;
		}

		/* butterfly */
		hideButterfly = false;
		indirectControlWeight = 0;
		indirectButterflyPosition = new Vector3f(0, 0, 0);
		butterflyCaughtBySpiderweb = false;
		timeOfHidingButterfly = 0;
		butterfly.reset();

		/* state machine */
		state = InnerGameState.FLOWER_SPAWNING;
		pictureModeFinished = false;
		firstFlowerMode = true;
		durationUntilPictureMode = schaugenau.utilities.Helper.getValueWithDeviation(timeBetweenPictures,
				deviationOfTimeBetweenPictures);
		timeUntilNextPickableSpawn = schaugenau.utilities.Helper.getValueWithDeviation(timeBetweenPickables,
				deviationOfTimeBetweenPickables);
		timeUntilHighscore = caughtToHighscoreDuration;
		innerStateTime = 0;
		hudFooter.reset(multiplicator, gameScore);
		pictureModeHintNode.hideImediatelly();
		pictureModeEndActionsHaveBeenDone = false;
		timeUntilFirstPickableMaySpawn = this.timeBeforeFirstPickable;
		correctPictureWasChosen = false;

		/* camera */
		cameraAngleTransitionTime = 0;
		cameraAngleTransitionAfterButterflyIsCaughtWaitTime = 0;

		/* picture mode time indicator */
		pictureModeTimeIndicator.setLocalTranslation(guiAdapter.getLeft() - (pictureModeTimeIndicatorWidth / 2.0f), 0,
				pictureModeTimeIndicatorZ);

		/* get current style of game from app */
		changeStyleOfGame(app.getCurrentGameStyle());

		/* initialize stuff for highscore state */
		this.app.setCurrentPlayedMiliSeconds(0);
		this.app.setCurrentMaxMultiplicator(0);
		this.app.setCurrentCorrectPictures(0);
		this.app.setCurrentIncorrectPictures(0);
		this.app.setCurrentAvMultiplicator(minimumMultiplicator);
		this.pauseTime = 0;

		/* save start time */
		this.startMiliSeconds = System.currentTimeMillis();

		/* fill texts */
		pictureModeHint.getText(pictureModeHintTextIndex)
				.setContent(app.getMessages().getString("game.pictureModeHint"));
	}

	/* detach */
	@Override
	protected void detach() {
		super.detach();
	}

	/* stop */
	@Override
	public void stop() {

		/* shut down thread pools */
		imageLoader.shutdownNow();
		pictureService.shutdownNow();
		pictureInformationService.shutdownNow();

		super.stop();
	}

	/* prepare and load highscore state */
	protected void prepareAndLoadHighscoreState() {

		/* played time */
		this.app.setCurrentPlayedMiliSeconds(
				(int) (System.currentTimeMillis() - this.startMiliSeconds) - (int) (this.pauseTime * 1000));

		/* score */
		this.app.setCurrentScore(this.gameScore);

		/* other values updated in running() */

		this.app.loadHighscoreState();
	}

	/* change style of game */
	protected void changeStyleOfGame(GameStyle gameStyle) {

		/* change it */
		switch (gameStyle) {
		case DIRECT: {
			useGridForButterfly = false;
			useGridForPickables = false;
			useIndirectControl = false;
			break;
		}
		case DIRECT_WITH_GRID: {
			useGridForButterfly = true;
			useGridForPickables = true;
			useIndirectControl = true;
			break;
		}
		case INDIRECT: {
			useGridForButterfly = false;
			useGridForPickables = false;
			useIndirectControl = true;
			break;
		}
		}
	}

	/* manage flowermode */
	protected void manageFlowermode(float tpf) {

		/* check, whether spawing is allowed */
		if (timeUntilFirstPickableMaySpawn >= 0) {

			timeUntilFirstPickableMaySpawn -= tpf;

		} else {

			timeUntilNextPickableSpawn -= tpf;

			/* spawn pickables */
			if (timeUntilNextPickableSpawn < 0) {

				timeUntilNextPickableSpawn = schaugenau.utilities.Helper.getValueWithDeviation(timeBetweenPickables,
						deviationOfTimeBetweenPickables);

				/* random routine to spawn flowers and spider webs */
				Pickable pickable = generatePickable();

				/* grid */
				if (useGridForPickables) {
					Vector2f gridCoord = schaugenau.utilities.Helper.snapToGrid(gridSize,
							new Vector2f(-butterflyX, -butterflyY), new Vector2f(butterflyX, butterflyY),
							new Vector2f(pickable.getWorldTranslation().x, pickable.getWorldTranslation().y));
					pickable.setXY(gridCoord);
				}

				pickable.attachTo(rootNode);
				pickablesList.add(pickable);
			}
		}
	}

	/* update pickables */
	protected void updatePickables(float tpf) {

		/* collision result list */
		CollisionResults collisionResults = new CollisionResults();

		/* move rays of butterfly to right position */
		LinkedList<Ray> butterflyRays = generateButterflyRays(this.butterfly.getWorldTranslation().clone());

		/* collide */
		LinkedList<Pickable> pickablesToBeRemovedList = new LinkedList<Pickable>();
		if (!pickablesList.isEmpty()) {
			for (Pickable pickable : pickablesList) {
				boolean focused = false;
				for (Ray ray : butterflyRays) {

					/* already collided with that pickable? */
					if (!pickablesToBeRemovedList.contains(pickable)) {

						/* no? then do it */
						pickable.collideWith(ray, collisionResults);
						if (collisionResults.size() > 0) {
							focused = true;
							Vector3f pt = collisionResults.getClosestCollision().getContactPoint();

							/* are they at the position of butterfly? */
							if ((pt.z > -butterflyCollisionDepth) && (pt.z < butterflyCollisionDepth)) {
								if (pickable instanceof Flower) {
									gameScore += ((Flower) pickable).getScore() * multiplicator;
									gameScore = Math.min(maximumGameScore, gameScore);

									/* show score as text */
									Text scoreText = ((Flower) pickable).getScoreText(scoreTextFont);
									scoreText.scale(scoreTextScale);
									scoreText.setLocalTranslation(
											new Vector3f(pickable.getWorldTranslation()).add(scoreTextOffset));
									scoreText.attachTo(rootNode);
									scoreTexts.add(scoreText);

									/* update hud footer */
									hudFooter.setFlowerScore(((Flower) pickable).getScore());

									/* play sound */
									app.getSoundManager().playSound(Sound.FLOWER, true);

									/* add pick emitter to list */
									MortalEmitter mortalEmitter = ((Flower) pickable).getPickEmitter(app);
									rootNode.attachChild(mortalEmitter.getEmitter());
									mortalEmitter.getEmitter().emitAllParticles();
									pickEmitterList.add(mortalEmitter);

									/* remove flower from list */
									pickablesToBeRemovedList.add(pickable);
								}
								if (pickable instanceof Spiderweb) {

									butterflyCaughtBySpiderweb = true;
									gameSpeed = 1;

									app.getSoundManager().playSound(Sound.SPIDERWEB, true);
								}
							}
						}
						pickable.update(tpf, focused);
						collisionResults.clear();
					}
				}

				/* already in culling zone? */
				if (cullZ < pickable.getWorldTranslation().z) {
					if (!pickablesToBeRemovedList.contains(pickable)) {
						pickablesToBeRemovedList.add(pickable);
					}
				}
			}

			/* delete pickables from list */
			for (Pickable pickable : pickablesToBeRemovedList) {

				/* for all pickables */
				pickable.detach();
				pickablesList.remove(pickable);
			}
		}
	}

	/* manages picture mode */
	protected void managePictureMode(float sTpf, float tpf) {

		/* picture mode time indicator */
		float t = innerStateTime / durationPictureSelection;
		float pictureModeTimeIndicatorX = (1 - t) * (guiAdapter.getLeft() - (pictureModeTimeIndicatorWidth / 2.0f))
				+ t * (guiAdapter.getRight() + (pictureModeTimeIndicatorWidth / 2.0f));
		pictureModeTimeIndicator.setLocalTranslation(pictureModeTimeIndicatorX, 0, pictureModeTimeIndicatorZ);

		/* now the important stuff */
		float leftFocus = 0;
		float rightFocus = 0;
		boolean leftPictureInFocusAtThisFrame = false;
		boolean rightPictureInFocusAtThisFrame = false;

		if (!pictureModeFinished) {

			/* check for focus on one of the pictures */
			if ((app.getRelativeCursor().x - pictureModeFocusAreaWidth) < 0) {
				leftPictureInFocusAtThisFrame = true;
			} else if ((app.getRelativeCursor().x + pictureModeFocusAreaWidth) >= 1.0f) {
				rightPictureInFocusAtThisFrame = true;
			}
		}

		/* pictures want to be updated */
		leftFocus = leftPicture.update(sTpf, tpf, leftPictureInFocusAtThisFrame, pictureModeFinished,
				leftPictureChosen);
		rightFocus = rightPicture.update(sTpf, tpf, rightPictureInFocusAtThisFrame, pictureModeFinished,
				rightPictureChosen);

		/* calculate chosen picture for glow and later for decision */
		if (leftFocus == rightFocus) {

			/* no picture was chosen */
			this.rightPictureChosen = false;
			this.leftPictureChosen = false;

		} else if (leftFocus > rightFocus) {

			/* left picture was chosen */
			this.rightPictureChosen = false;
			this.leftPictureChosen = true;

		} else {

			/* right picture was chosen */
			this.rightPictureChosen = true;
			this.leftPictureChosen = false;
		}

		/* picture selected? */
		if (!pictureModeFinished && innerStateTime >= durationPictureSelection) {

			/* set multiplicator */
			oldMultiplicator = multiplicator;
			if ((leftPictureChosen || rightPictureChosen) && ((rightPictureChosen && rightPictureIsCorrect)
					|| (leftPictureChosen && !rightPictureIsCorrect))) {
				multiplicator++;
				multiplicator = Math.min(maximumMultiplicator, multiplicator);
			} else {
				multiplicator = minimumMultiplicator;
			}

			/* hud footer is told later about new multiplicator */

			/* start the end of picture mode */
			pictureModeFinished = true;
			hideButterfly = false;
		}
	}

	/* game is paused */
	@Override
	public boolean pause() {
		if (super.pause()) {
			butterfly.setPaused(true);
			return true;
		}
		return true;
	}

	/* game is unpaused */
	@Override
	public boolean unpause() {
		if (super.unpause()) {
			butterfly.setPaused(false);
			return true;
		}
		return false;
	}

	/* generate pickable */
	protected Pickable generatePickable() {

		/* all frequencies sumed up */
		float omega = freqRose + freqLily + freqTulip + freqDandelion + freqSpiderweb + (float) freqRaiseSpiderweb;
		float val = Helper.randomInIntervall(0, omega);

		/* just a little helper */
		int border = 0;

		if (border <= val && val <= border + freqRose) {
			Rose rose = new Rose(app, scoreRose, butterflyX, butterflyY, environment.getTerrainList(), this.debugging);
			return rose;
		}
		border += freqRose;

		if (border < val && val <= border + freqTulip) {
			Tulip tulip = new Tulip(app, scoreTulip, butterflyX, butterflyY, environment.getTerrainList(),
					this.debugging);
			return tulip;
		}
		border += freqTulip;

		if (border < val && val <= border + freqLily) {
			Lily lily = new Lily(app, scoreLily, butterflyX, butterflyY, environment.getTerrainList(), this.debugging);
			return lily;
		}
		border += freqLily;

		if (border < val && val <= border + freqDandelion) {
			Dandelion dandelion = new Dandelion(app, scoreDandelion, butterflyX, butterflyY,
					environment.getTerrainList(), this.debugging);
			return dandelion;
		}
		border += freqDandelion;

		/* else do */
		if (!this.firstFlowerMode) {
			Spiderweb spiderweb = new Spiderweb(app, butterflyX, butterflyY, environment.getTerrainList(),
					this.debugging);
			return spiderweb;
		} else {
			/* instead of spiderweb, spawn dandelion */
			Dandelion dandelion = new Dandelion(app, scoreDandelion, butterflyX, butterflyY,
					environment.getTerrainList(), this.debugging);
			return dandelion;
		}
	}

	/* generate rays */
	protected LinkedList<Ray> generateButterflyRays(Vector3f origin) {

		/* generate the rays */
		LinkedList<Ray> butterflyRays = new LinkedList<Ray>();

		/* direction */
		Vector3f dir = new Vector3f(0, 0, -1);

		/* offsets */
		Vector3f upperOffset = upperButterflyCollisionOffset.clone().mult(butterflyScale);
		Vector3f centerOffset = centerButterflyCollisionOffset.clone().mult(butterflyScale);
		Vector3f lowerOffset = lowerButterflyCollisionOffset.clone().mult(butterflyScale);

		/* upper rays */
		butterflyRays.add(new Ray(upperOffset.clone().add(origin), dir));
		butterflyRays.add(new Ray(new Vector3f(-upperOffset.x, upperOffset.y, upperOffset.z).add(origin), dir));

		/* rays at center */
		butterflyRays.add(new Ray(origin, dir));
		butterflyRays.add(new Ray(centerOffset.clone().add(origin), dir));
		butterflyRays.add(new Ray(new Vector3f(-centerOffset.x, centerOffset.y, centerOffset.z).add(origin), dir));

		/* lower rays */
		butterflyRays.add(new Ray(lowerOffset.clone().add(origin), dir));
		butterflyRays.add(new Ray(new Vector3f(-lowerOffset.x, lowerOffset.y, lowerOffset.z).add(origin), dir));

		/* debugging */
		if (this.debugging) {

			/* detach old ones */
			for (DebugLine line : butterflyDebugLines) {
				line.detach();
			}
			butterflyDebugLines.clear();

			/* some parameters */
			ColorRGBA debugLineColor = new ColorRGBA(1, 0, 0, 1);
			float debugLineLength = 200;

			/* create and attach new lines */
			for (Ray ray : butterflyRays) {
				DebugLine line = new DebugLine(this.app, ray, debugLineLength, debugLineColor);
				line.attachTo(this.rootNode);
				butterflyDebugLines.add(line);
			}
		}

		return butterflyRays;
	}

	/* handle emitters */
	protected void handleEmitters(float tpf) {

		/* update emitter lifetime */
		LinkedList<MortalEmitter> emitterToBeRemovedList = new LinkedList<MortalEmitter>();
		for (MortalEmitter mortalEmitter : pickEmitterList) {
			boolean alive = mortalEmitter.decreaseLifeTime(tpf);
			if (!alive) {
				emitterToBeRemovedList.add(mortalEmitter);
			}
		}

		/* kill emitter */
		for (MortalEmitter mortalEmitter : emitterToBeRemovedList) {
			rootNode.detachChild(mortalEmitter.getEmitter());
			pickEmitterList.remove(mortalEmitter);
		}
	}

	/* calculate indirect position of butterfly */
	protected Vector3f handleIndirectButterflyControl(float tpf, Vector3f directButterflyPosition) {

		/* handle weight of indirect control */
		if (indirectControlWeight > 0) {
			indirectControlWeight -= indirectControlFadeSpeed * tpf;
		} else {
			indirectControlWeight = indirectControlFadeSpeed * tpf;
		}
		indirectControlWeight = Math.max(0, Math.min(1, indirectControlWeight));

		/* create ray */
		Vector2f cursor = app.getPixelCursor();
		Ray ray = new Ray(app.getCamera().getWorldCoordinates(cursor, 0), app.getCamera().getWorldCoordinates(cursor, 1)
				.subtractLocal(app.getCamera().getWorldCoordinates(cursor, 0)).normalizeLocal());

		/* result list */
		CollisionResults results = new CollisionResults();

		/* closest collision point */
		Vector3f closestCollion = null;

		/* collide ray with pickables */
		for (Pickable pickable : pickablesList) {
			pickable.collideWith(ray, results);
			if (results.size() > 0) {

				/* code for excluding spider webs outcommented */
				// if (!(pickable instanceof Spiderweb)) {
				if (closestCollion == null) {
					closestCollion = results.getClosestCollision().getContactPoint();
				} else {
					/* get closest pickable */
					if (closestCollion.z < results.getClosestCollision().getContactPoint().z) {
						closestCollion = results.getClosestCollision().getContactPoint();
					}
				}

				// }
			}
		}

		/* set butterfly's position to collision point */
		if (closestCollion != null) {
			indirectControlWeight = 1;
			indirectButterflyPosition = new Vector3f(closestCollion.x, closestCollion.y, directButterflyPosition.z);
		}

		return new Vector3f(
				directButterflyPosition.x * (1 - indirectControlWeight)
						+ indirectButterflyPosition.x * indirectControlWeight,
				directButterflyPosition.y * (1 - indirectControlWeight)
						+ indirectButterflyPosition.y * indirectControlWeight,
				directButterflyPosition.z * (1 - indirectControlWeight)
						+ indirectButterflyPosition.z * indirectControlWeight);
	}

	/* return current score */
	public int getScore() {
		return this.gameScore;
	}
}