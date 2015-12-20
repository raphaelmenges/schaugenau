package schaugenau.input;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;

import schaugenau.app.App;

/**
 * Distributed under the MIT License. (See accompanying file LICENSE or copy at
 * https://github.com/raphaelmenges/schaugenau/blob/master/src/LICENSE)
 * 
 * Input using EyeX.
 * 
 * @author Raphael Menges
 *
 */

public class EyeXInput extends TrackerInput {

	/** defines **/
	protected final float trackBoxFilterSpeed = 0.1f;

	/** fields **/
	protected Vector3f filteredLeftTrackBox;
	protected Vector3f filteredRightTrackBox;

	/** methods **/

	/* constructor */
	public EyeXInput(App app) {
		super(app);

		filteredLeftTrackBox = new Vector3f();
		filteredRightTrackBox = new Vector3f();
	}

	@Override
	/* acquire data */
	protected Vector4f aquireTrackerData() {

		Vector4f data = new Vector4f();
		/*
		 * data.x = (float) schaugenau.eyecontrol.EyeXUtil.getLeft_x(); data.y =
		 * (float) schaugenau.eyecontrol.EyeXUtil.getRight_x(); data.z = (float)
		 * schaugenau.eyecontrol.EyeXUtil.getLeft_y(); data.w = (float)
		 * schaugenau.eyecontrol.EyeXUtil.getRight_y();
		 */ // TODO
		return data;

	}

	@Override
	/* process input data */
	protected Vector2f processTrackingData(Vector4f data) {

		float leftX = data.x;
		float rightX = data.y;
		float leftY = data.z;
		float rightY = data.w;

		boolean leftEyeTracked = true;
		boolean rightEyeTracked = true;

		Vector2f processedData = new Vector2f();

		/* check whether input works */
		if (leftX == 0 && leftY == 0) {

			/* left eye could not be tracked */
			leftEyeTracked = false;
		}
		if (rightX == 0 && rightY == 0) {

			/* right eye could not be tracked */
			rightEyeTracked = false;
		}

		/* mirror y to fit internal coordinate system */
		leftY = 1.0f - leftY;
		rightY = 1.0f - rightY;

		/* use input */
		if (leftEyeTracked && rightEyeTracked) {

			/* everything worked */
			isTrackingCurrentlyWorking = true;
			processedData.x = (leftX + rightX) / 2.0f;
			processedData.y = (leftY + rightY) / 2.0f;

		} else if (leftEyeTracked) {

			/* only left eye was tracked */
			isTrackingCurrentlyWorking = true;
			processedData.x = leftX;
			processedData.y = leftY;

		} else if (rightEyeTracked) {

			/* only right eye was tracked */
			isTrackingCurrentlyWorking = true;
			processedData.x = rightX;
			processedData.y = rightY;

		} else {

			/* nothing was tracked */
			isTrackingCurrentlyWorking = false;
			processedData.x = 0;
			processedData.y = 0;
		}

		/* do scaling */
		processedData.x = processedData.x * app.getWindowResolution().x;
		processedData.y = processedData.y * app.getWindowResolution().y;

		/* merge with information about head state */
		isTrackingCurrentlyWorking = (isTrackingCurrentlyWorking && (this.getHeadState() == HeadState.OK));

		/* filter tracking box data */
		/*
		 * if (this.isLeftEyePositionAvailable()) {
		 * filteredLeftTrackBox.interpolate(new Vector3f((float)
		 * schaugenau.eyecontrol.EyeXUtil.getTrackboxLeft_x() - 0.5f, (float)
		 * schaugenau.eyecontrol.EyeXUtil.getTrackboxLeft_y() - 0.5f, (float)
		 * schaugenau.eyecontrol.EyeXUtil.getTrackboxLeft_z()),
		 * this.trackBoxFilterSpeed); }
		 * 
		 * if (this.isRightEyePositionAvailable()) {
		 * filteredRightTrackBox.interpolate(new Vector3f((float)
		 * schaugenau.eyecontrol.EyeXUtil.getTrackboxRight_x() - 0.5f, (float)
		 * schaugenau.eyecontrol.EyeXUtil.getTrackboxRight_y() - 0.5f, (float)
		 * schaugenau.eyecontrol.EyeXUtil.getTrackboxRight_z()),
		 * this.trackBoxFilterSpeed); }
		 */ // TODO

		return processedData;
	}

	@Override
	public boolean start() {
		if (super.start()) {
			// schaugenau.eyecontrol.EyeXUtil.startTracking(); // TODO
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean stop() {
		if (super.stop()) {
			// schaugenau.eyecontrol.EyeXUtil.stopTracking(); // TODO
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void forceStart() {
		// schaugenau.eyecontrol.EyeXUtil.startTracking(); // TODO
	}

	@Override
	public void forceStop() {
		// schaugenau.eyecontrol.EyeXUtil.stopTracking(); // TODO
	}

	@Override
	public void setCalibrationPoints(double[] points) {
		// schaugenau.eyecontrol.EyeXUtil.setCalibrationPoints(points); // TODO
	}

	@Override
	public void collectCalibrationDataAsync() {
		// schaugenau.eyecontrol.EyeXUtil.collectCalibrationDataAsync(); // TODO
	}

	@Override
	public void computeCalibrationDataAsync() {
		// schaugenau.eyecontrol.EyeXUtil.computeCalibrationDataAsync(); // TODO
	}

	@Override
	public void stopCalibrationAsync() {
		// schaugenau.eyecontrol.EyeXUtil.stopCalibrationAsync(); // TODO
	}

	@Override
	public HeadState getHeadState() {
		/* return current head state */
		/*
		 * switch (schaugenau.eyecontrol.EyeXUtil.getHeadPosition()) { case -1:
		 * return HeadState.NOT_DETECTED; case 0: return HeadState.OK; case 1:
		 * return HeadState.CLOSE; case 2: return HeadState.FAR; default: return
		 * HeadState.ERROR; }
		 */ // TODO
		return HeadState.ERROR;
	}

	@Override
	public Vector3f getLeftEyePosition() {
		return filteredLeftTrackBox.clone();
	}

	@Override
	public Vector3f getRightEyePosition() {
		return filteredRightTrackBox.clone();
	}

	@Override
	public boolean isLeftEyePositionAvailable() {
		// return (schaugenau.eyecontrol.EyeXUtil.getTrackboxLeft_z() != 0) &&
		// (this.getHeadState() != HeadState.NOT_DETECTED); // TODO
		return false;
	}

	@Override
	public boolean isRightEyePositionAvailable() {
		// return (schaugenau.eyecontrol.EyeXUtil.getTrackboxRight_z() != 0) &&
		// (this.getHeadState() != HeadState.NOT_DETECTED); // TODO
		return false;
	}

	@Override
	public boolean isConnected() {
		// return (0 != schaugenau.eyecontrol.EyeXUtil.isTrackerConnected()); //
		// TODO
		return false;
	}
}