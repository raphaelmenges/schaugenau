package schaugenau.state.calibration;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

import schaugenau.app.App;
import schaugenau.core.StaticEntity;
import schaugenau.gui.GuiAdapter;
import schaugenau.gui.GuiElement;

/**
 * Distributed under the MIT License. (See accompanying file LICENSE or copy at
 * https://github.com/raphaelmenges/schaugenau/blob/master/src/LICENSE)
 * 
 * Dot for calibration.
 * 
 * @author Raphael Menges
 *
 */

public class CalibrationDot extends GuiElement {

	/** enumeration **/
	public enum InnerDotState {
		HIDING, CALIBRATING, MOVING
	};

	/** defines **/
	protected final float moveDuration = 0.5f;
	protected final float hidingScale = 0;
	protected final float movingScale = 0.5f;
	protected final float calibratingMaxScale = 1.6f;
	protected final float calibratingMinScale = 0.2f;
	protected final float scaleAnimationSpeed = 2;
	protected final float calibrationAnimationDuration = 2;
	protected final ColorRGBA color = new ColorRGBA(1, 1, 0.6f, 1);

	/** fields **/
	protected InnerDotState state;
	protected StaticEntity dot;
	protected Vector3f targetPosition;
	protected Vector3f initialPosition;
	protected Vector3f oldPosition;
	protected float moveTime;
	protected float calibratingAnimationTime;

	/** methods **/

	/* constructor */
	public CalibrationDot(App app, GuiAdapter guiAdapter, Vector2f initialRelativeScreenPosition) {
		super(app, guiAdapter, "calibrationDot");

		this.initialPosition = convertToGuiPosition(initialRelativeScreenPosition);

		/* create dot */
		this.dot = new StaticEntity(app, name, "Plane", "Unshaded", "CalibrationDot", true, "png", true, true, false,
				false);
		this.dot.setLocalTranslation(this.initialPosition);
		this.dot.setColorParameter(this.color);
		this.dot.attachTo(this.node);

		this.reset();
	}

	/* update, returns whether dot is at desired position */
	public boolean update(float tpf) {

		/* changed through state machine */
		boolean isAtDesiredPosition = false;
		float targetScale = 1;

		/* inner state machine */
		switch (state) {

		case HIDING:
			targetScale = 0;
			break;

		case CALIBRATING:

			/* scaling */
			calibratingAnimationTime += tpf;
			calibratingAnimationTime = calibratingAnimationTime % calibrationAnimationDuration;

			float scale = (float) Math.sin(calibratingAnimationTime / calibrationAnimationDuration * Math.PI * 2);
			scale = (0.5f * scale + 0.5f) * (calibratingMaxScale - calibratingMinScale) + calibratingMinScale;
			targetScale = scale;

			/* tell owner about success */
			isAtDesiredPosition = true;
			break;

		case MOVING:

			/* move dot */
			moveTime += tpf;
			this.dot.setLocalTranslation(
					new Vector3f().interpolate(oldPosition, targetPosition, moveTime / moveDuration));
			if (moveTime > moveDuration) {
				this.dot.setLocalTranslation(targetPosition);
				this.state = InnerDotState.CALIBRATING;
				calibratingAnimationTime = 0;
			}

			/* set scale */
			targetScale = movingScale;

			break;
		}

		/* animate scale */
		float currScale = dot.getCombinedLocalScale();
		if (targetScale != currScale) {
			float scale = 1;
			if (targetScale - currScale > 0) {
				scale = currScale + tpf * scaleAnimationSpeed;
				if (scale > targetScale) {
					scale = targetScale;
				}
			} else {
				scale = currScale - tpf * scaleAnimationSpeed;
				if (scale < targetScale) {
					scale = targetScale;
				}
			}
			this.dot.setLocalScale(scale);
		}

		return isAtDesiredPosition;
	}

	/* show dot */
	public void show() {
		this.state = InnerDotState.CALIBRATING;
		calibratingAnimationTime = 0;
	}

	/* set next position */
	public void nextPosition(Vector2f relativeScreenPosition) {
		targetPosition = convertToGuiPosition(relativeScreenPosition);
		oldPosition = this.dot.getLocalTranslation().clone();
		this.state = InnerDotState.MOVING;
		this.moveTime = 0;
	}

	/* transform relative position to gui one */
	private Vector3f convertToGuiPosition(Vector2f relativeScreenPosition) {
		float width = guiAdapter.getWidth();
		float height = guiAdapter.getHeight();

		Vector3f guiPosition = new Vector3f((relativeScreenPosition.x - 0.5f) * width,
				-(relativeScreenPosition.y - 0.5f) * height, 0);

		return guiPosition;
	}

	/* reset dot */
	public void reset() {
		this.dot.setLocalTranslation(this.initialPosition);
		this.dot.setLocalScale(0);
		this.state = InnerDotState.HIDING;
	}
}
