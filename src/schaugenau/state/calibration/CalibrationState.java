package schaugenau.state.calibration;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

import schaugenau.app.App;
import schaugenau.core.FadableState;
import schaugenau.core.SimpleWorldBackground;
import schaugenau.font.TextBox;
import schaugenau.gui.IconButton;
import schaugenau.input.Input;
import schaugenau.input.TrackerInput;

/**
 * Distributed under the MIT License. (See accompanying file LICENSE or copy at
 * https://github.com/raphaelmenges/schaugenau/blob/master/src/LICENSE)
 * 
 * State for calibration.
 * 
 * @author Raphael Menges
 *
 */

public class CalibrationState extends FadableState {

	/** defines */
	protected final float showDuration = 3f;
	protected final float dotInstructionDuration = 2.0f;
	protected final Vector3f dotInstructionPosition = new Vector3f(0, 1.15f, -1f);
	protected final float dotInstructionScale = 0.8f;
	protected final float continueButtonFinalScale = 3;
	protected final float continueButtonGrowSpeed = 3;
	protected final Vector3f continueButtonPosition = new Vector3f(0, 0, 5f);
	protected final Vector2f dotFinalRelativeScreenPosition = new Vector2f(0.5f, 0.5f);

	/** fields **/
	protected int calibrationDotIndex;
	protected float showTime;
	protected boolean calibrationStarted;
	protected CalibrationDot calibrationDot;
	protected boolean calibrationDotMoving;
	protected SimpleWorldBackground background;
	protected TrackerInput tracker;
	protected float dotInstructionTime;
	protected TextBox dotInstruction;
	protected Node dotInstructionNode;
	protected IconButton continueButton;
	protected float continueButtonScale;
	protected boolean showContinueButton;

	// GazeSDK coordinate system:
	// 2 { 0.1, 0.1 } --- 3 { 0.9, 0.1 }
	// -------- 1 { 0.5, 0.5 } ---------
	// 5 { 0.1, 0.9 } --- 4 { 0.9, 0.9 }
	protected double[] gazeSDKCalibrationPoints = { 0.5, 0.5, 0.1, 0.1, 0.9, 0.1, 0.9, 0.9, 0.1, 0.9 };

	/** methods **/

	/* constructor */
	public CalibrationState(App app, String name, boolean debugging) {
		super(app, name, debugging, 1, 1);

		/* calibration dot */
		calibrationDot = new CalibrationDot(this.app, this.guiAdapter,
				new Vector2f((float) gazeSDKCalibrationPoints[0], (float) gazeSDKCalibrationPoints[1]));
		calibrationDot.attachTo(this.guiAdapter.getNode());

		/* dot instruction */
		dotInstructionNode = new Node("DotInstructionNode");
		this.guiAdapter.attachChild(dotInstructionNode);

		dotInstruction = new TextBox(this.app.getPrimaryGuiFont(), schaugenau.font.TextBox.Alignment.CENTER);
		dotInstruction.setLocalTranslation(dotInstructionPosition);
		dotInstruction.setLocalScale(dotInstructionScale);
		dotInstruction.attachTo(dotInstructionNode);

		/* continue button */
		continueButton = new IconButton(app, guiAdapter, continueButtonPosition, this.continueButtonScale, "Icon-Ok",
				"ContinueButton");
		continueButton.attachTo(this.guiAdapter.getNode());

		/* get tracker from app */
		Input input = this.app.getInput();
		if (input.isTracker()) {
			this.tracker = (TrackerInput) input;
		}

		/* other */
		this.background = new SimpleWorldBackground(this.app, false);
		this.background.attachTo(rootNode);
	}

	/* update */
	@Override
	public boolean update(float tpf, boolean buzzerPressed) {
		boolean hasDetachedItself = super.update(tpf, buzzerPressed);

		if (!paused) {

			/* background */
			this.background.update(tpf);

			/* dot instruction animation */
			if (dotInstructionTime > 0) {
				dotInstructionTime -= tpf;
				if (dotInstructionTime < 0) {
					dotInstructionTime = 0;
				}

				/* t */
				float t = (dotInstructionTime / dotInstructionDuration);

				/* scaling */
				dotInstructionNode.setLocalScale(Math.min(1, 3 * (float) Math.sqrt(t)));
			}
		}

		return hasDetachedItself;
	}

	/* fade in, returns if finished */
	@Override
	protected boolean fadeIn(float tpf) {

		boolean fadingDone = super.fadeIn(tpf);

		if (fadingDone) {
			/* show the dot after fade in is done */
			calibrationDot.show();
		}

		return fadingDone;
	}

	/* running */
	@Override
	protected void running(float tpf, boolean buzzerPressed) {
		super.running(tpf, buzzerPressed);

		if (!paused && tracker != null) {

			/* calibration */
			if (!calibrationStarted) {

				/* tell input about calibration points */
				tracker.setCalibrationPoints(gazeSDKCalibrationPoints);

				/* start collecting gaze data for first dot */
				tracker.collectCalibrationDataAsync();
				calibrationStarted = true;
			}

			if (!calibrationDotMoving && !showContinueButton) {
				if (showTime > showDuration) {
					showTime -= showDuration;
					calibrationDotIndex += 2;

					if (calibrationDotIndex < gazeSDKCalibrationPoints.length - 1) {

						/* show next calibration dot */
						calibrationDot.nextPosition(new Vector2f((float) gazeSDKCalibrationPoints[calibrationDotIndex],
								(float) gazeSDKCalibrationPoints[calibrationDotIndex + 1]));
						calibrationDotMoving = true;

						/* start collecting gaze data of next dot */
						tracker.collectCalibrationDataAsync();

					} else {
						tracker.computeCalibrationDataAsync();

						/* show continue button */
						this.showContinueButton = true;

						/* move calibration dot into center */
						this.calibrationDot.nextPosition(dotFinalRelativeScreenPosition);
					}
				} else {
					showTime += tpf;
				}
			}

			if (showContinueButton) {

				/* let the button grow */
				continueButtonScale += tpf * continueButtonGrowSpeed;

				boolean doButtonUpdate = false;

				/* check, whether it was pressed */
				if (continueButtonScale >= continueButtonFinalScale) {

					/* set it to final scale */
					continueButtonScale = continueButtonFinalScale;
					doButtonUpdate = true;
				}

				/* set scale */
				continueButton.setLocalScale(continueButtonScale);

				/* growing done */
				if (doButtonUpdate) {

					/* update it */
					if (continueButton.update(tpf, true)) {
						app.loadTutorialState();
					}
				}
			}

			/* update calibration dot all the time */
			if (calibrationDot.update(tpf)) {
				/* calibration dot has stopped moving */
				calibrationDotMoving = false;
			}
		}

	}

	/* fade out, returns if finished */
	@Override
	protected boolean fadeOut(float tpf) {

		boolean fadingDone = super.fadeOut(tpf);

		/* for sake of completeness */
		continueButton.update(tpf, true);

		return fadingDone;
	}

	/* attach */
	@Override
	protected void attach() {
		super.attach();

		/* just for paranoia */
		if (tracker == null) {
			logger.fatal("Tried to calibrate a non tracker!");

			/* should be never ever used */
			this.app.loadGameState();
		}

		/* reset dot instruction */
		this.dotInstructionTime = dotInstructionDuration;
		this.dotInstructionNode.setLocalScale(1.0f);

		/* reset the calibration */
		this.resetCalibration();

		/* set instruction text */
		dotInstruction.setContent(app.getMessages().getString("calibration.followDot"));
	}

	@Override
	public boolean pause() {

		/* abort calibration */
		if (this.calibrationStarted && tracker != null) {
			tracker.stopCalibrationAsync();
		}

		/* super */
		return super.pause();
	}

	@Override
	public boolean unpause() {

		/* do calibration again */
		this.resetCalibration();
		this.calibrationDot.show();

		/* super */
		return super.unpause();
	}

	protected void resetCalibration() {
		this.showTime = 0;
		this.calibrationStarted = false;

		/* reset dot */
		this.calibrationDotIndex = 0;
		this.calibrationDotMoving = false;
		calibrationDot.reset();

		/* reset continue button */
		this.continueButtonScale = 0;
		this.continueButton.setLocalScale(continueButtonScale);
		this.continueButton.reset();
		this.showContinueButton = false;
	}

}