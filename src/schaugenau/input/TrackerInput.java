package schaugenau.input;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;

import schaugenau.app.App;

/**
 * Distributed under the MIT License. (See accompanying file LICENSE or copy at
 * https://github.com/raphaelmenges/schaugenau/blob/master/src/LICENSE)
 * 
 * Superclass for input using trackers like EyeX or EyeTribe.
 * 
 * @author Raphael Menges
 *
 */

public abstract class TrackerInput extends Input {

	/* enumeration */
	public enum HeadState {
		OK, CLOSE, FAR, NOT_DETECTED, ERROR
	}

	/** defines **/
	protected final float subtractionFromLastProbesPerSecond = 0.5f;
	protected final float isWorkingThreshold = 0;
	protected final float thresholdDurationPausing = 3.0f;
	protected final float thresholdDurationUnpausing = 1.0f;

	/** fields **/
	protected boolean isTrackingCurrentlyWorking;
	protected boolean isTrackingWorking;
	protected float tresholdTime;
	protected boolean started;
	protected Vector2f lastWorkingInput;

	/** methods **/

	/* constructor */
	public TrackerInput(App app) {
		super(app);

		/* hide mouse cursor */
		app.getInputManager().setCursorVisible(false);

		this.isTrackingWorking = true;
		this.started = false;
		this.lastWorkingInput = new Vector2f(app.getWindowResolution().clone().mult(0.5f));

	}

	@Override
	/* should be called by sub class at last */
	public Vector2f update(float tpf) {

		/* aquire tracker data, returns (leftX, rightX, leftY and rightY) */
		Vector4f rawData = aquireTrackerData();

		/* process data from tracker */
		Vector2f processedData = processTrackingData(rawData);

		/* check data */
		this.input = doTrackingCheck(processedData, tpf);

		return this.input.clone();
	}

	/* aquires data from tracker */
	protected abstract Vector4f aquireTrackerData();

	/* process tracking data */
	protected abstract Vector2f processTrackingData(Vector4f data);

	/* checks whether tracking works, returns usable input data */
	protected Vector2f doTrackingCheck(Vector2f currentInput, float tpf) {

		if (isTrackingWorking) {

			/* check whether it still works */
			if (isTrackingCurrentlyWorking) {
				/* Does work one time? Then reset threshold time */
				tresholdTime = thresholdDurationPausing;
			} else {
				if (tresholdTime <= 0) {
					this.isTrackingWorking = false;
					tresholdTime = thresholdDurationUnpausing;
				} else {
					tresholdTime -= tpf;
				}
			}

		} else {

			/* tracker seams to take a break */
			if (tresholdTime <= 0) {
				this.isTrackingWorking = true;
				tresholdTime = thresholdDurationPausing;
			} else {
				if (isTrackingCurrentlyWorking) {
					tresholdTime -= tpf;
				} else {
					tresholdTime = thresholdDurationUnpausing;
				}
			}
		}

		/* return usable input */
		Vector2f usableInput;
		if (isTrackingCurrentlyWorking) {
			usableInput = currentInput.clone();
			/* save usable input for bad times */
			this.lastWorkingInput = currentInput.clone();
		} else {
			usableInput = this.lastWorkingInput.clone();
		}
		return usableInput;
	}

	@Override
	public boolean start() {
		if (this.started) {
			return false;
		} else {
			/* reset pausing stuff */
			this.isTrackingWorking = true;
			tresholdTime = thresholdDurationPausing;

			/* the important boolean itself */
			this.started = true;

			return true;
		}
	}

	@Override
	public boolean stop() {
		if (!this.started) {
			return false;
		} else {
			this.started = false;
			return true;
		}
	}

	public abstract void forceStart();

	public abstract void forceStop();

	@Override
	/* returns, whether input is ok */
	public boolean isInputWorking() {
		return isTrackingWorking;
	}

	@Override
	public boolean isTracker() {
		return true;
	}

	@Override
	public boolean isStarted() {
		return this.started;
	}

	public abstract void setCalibrationPoints(double[] points);

	public abstract void collectCalibrationDataAsync();

	public abstract void computeCalibrationDataAsync();

	public abstract void stopCalibrationAsync();

	public abstract HeadState getHeadState();

	public abstract Vector3f getLeftEyePosition();

	public abstract Vector3f getRightEyePosition();

	public abstract boolean isLeftEyePositionAvailable();

	public abstract boolean isRightEyePositionAvailable();
}
