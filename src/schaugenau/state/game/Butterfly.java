package schaugenau.state.game;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

import schaugenau.app.App;
import schaugenau.core.AnimEntity;

/**
 * Distributed under the MIT License. (See accompanying file LICENSE or copy at
 * https://github.com/raphaelmenges/schaugenau/blob/master/src/LICENSE)
 * 
 * Butterfly character.
 * 
 * @author Raphael Menges
 *
 */

public class Butterfly {

	/** enumerations **/
	public enum ButterflyState {
		START, FLY, DIE
	};

	/** defines **/
	protected final float speed = 4f;
	protected final float timeBetweenGlancingBack = 15;
	protected final float deviationOfTimeGlancingBack = 5;
	protected final float alphaTransistionSpeed = 2.0f;
	protected final float maximalAlpha = 1;
	protected final float minimalAlpha = 0.4f;
	protected final float alphaDistanceToCursor = 1;
	protected final float animationStartCutOff = 1.8f;
	protected final float animationPictureModeCutOff = 0.75f;

	/** fields **/
	protected App app;
	protected Node node;
	protected Node parent = null;
	protected AnimEntity entity;
	protected Vector3f targetPosition;
	protected ButterflyState state;
	protected float time;
	protected float timeUntilGlancingBack;
	protected float timeSinceGlancingBack;
	protected boolean glancingBack;
	protected float alpha;
	protected boolean playingPictureModeAnimation;

	/** methods **/

	/* constructor */
	public Butterfly(App app) {
		this.app = app;
		node = new Node("Butterfly");

		/* do it with AnimEntity */
		createEntity();

		setPaused(false);

		this.reset();
	}

	/* update it and returns transparency */
	public float update(float tpf, float gameSpeed, Vector3f targetPosition, boolean caughtBySpiderweb,
			Vector3f cursorWorldPosition) {

		/* adjust animation speed */
		entity.setAnimationSpeed(gameSpeed);

		/* update time */
		time += tpf;

		/* state machine */
		switch (state) {
		case START: {

			/* if state is about to change */
			if (time > (entity.getAnimationTime() - animationStartCutOff)) {
				entity.setAnimation("Flying", true, false);
				state = ButterflyState.FLY;
				time = 0;
				timeUntilGlancingBack = schaugenau.utilities.Helper.getValueWithDeviation(timeBetweenGlancingBack,
						deviationOfTimeGlancingBack);
			}

			/* alpha */
			alpha = 1;

			break;
		}
		case FLY: {

			if (playingPictureModeAnimation) {

				/* check, whether special animation is finished */
				if (time > entity.getAnimationTime() - animationPictureModeCutOff) {
					entity.setAnimation("Flying", true, false);
					playingPictureModeAnimation = false;
				}

				/* alpha */
				alpha = 1;

			} else {

				/* standard flying */
				this.targetPosition = targetPosition;
				Vector3f pos = node.getWorldTranslation().clone();
				pos.interpolate(targetPosition, speed * tpf);
				node.setLocalTranslation(pos);

				/* recover from glancing back */
				if (glancingBack) {
					timeSinceGlancingBack += tpf;

					if (timeSinceGlancingBack > (entity.getAnimationTime())) {
						entity.setAnimation("Flying", true, false);
						glancingBack = false;
					}
				}

				/* glancing back during flying */
				timeUntilGlancingBack -= tpf;

				if (timeUntilGlancingBack < 0) {
					entity.setAnimation("GlanceBack", false, false);
					timeUntilGlancingBack = schaugenau.utilities.Helper.getValueWithDeviation(timeBetweenGlancingBack,
							deviationOfTimeGlancingBack);
					timeSinceGlancingBack = 0;
					glancingBack = true;
				}

				/* if state is about to change */
				if (caughtBySpiderweb) {
					entity.setAnimation("End", false, false);
					state = ButterflyState.DIE;
				}

				/* alpha */
				if (this.getWorldTranslation().distance(cursorWorldPosition) < alphaDistanceToCursor) {
					alpha -= tpf * alphaTransistionSpeed;
				} else {
					alpha += tpf * alphaTransistionSpeed;
				}
			}

			break;
		}
		case DIE: {

			/* alpha */
			alpha += tpf * alphaTransistionSpeed;

			break;
		}
		}

		/* clamp alpha */
		alpha = Math.max(Math.min(alpha, maximalAlpha), minimalAlpha);

		return alpha;
	}

	/* play picture mode animation */
	public boolean playPictureModeAnimation(boolean chosenLeftPicture, boolean chosenCorrectPicture) {
		if (this.state == ButterflyState.FLY) {

			/* reminder for state machine */
			this.playingPictureModeAnimation = true;

			/* set up everything */
			node.setLocalTranslation(0, 0, 0);
			time = 0;

			/* hack to avoid rare animation bug */
			entity.detach();
			createEntity();

			/* set animation */
			if (chosenLeftPicture && chosenCorrectPicture) {
				entity.setAnimation("LeftCorrect", false, true);
			} else if (chosenLeftPicture && !chosenCorrectPicture) {
				entity.setAnimation("LeftIncorrect", false, true);
			} else if (!chosenLeftPicture && chosenCorrectPicture) {
				entity.setAnimation("RightCorrect", false, true);
			} else {
				entity.setAnimation("RightIncorrect", false, true);
			}

			/* paranoid: kill all glancing back */
			glancingBack = false;

			return true;
		} else {
			return false;
		}
	}

	/* reset */
	public void reset() {
		state = ButterflyState.START;
		time = 0;
		targetPosition = new Vector3f(0, 0, 0);
		node.setLocalTranslation(new Vector3f(0, 0, 0));
		entity.setAnimation("Start", false, true);
		glancingBack = false;
		alpha = 1;
		playingPictureModeAnimation = false;
	}

	/* create butterfly entity */
	public void createEntity() {
		entity = new AnimEntity(this.app, "Butterfly", "Unshaded", false, "png", false, false, false, true);
		entity.rotate(new Vector3f(0f, 3.14f, 0f));
		entity.attachTo(node);
	}

	/* attach to scene graph */
	public void attachTo(Node parent) {
		this.parent = parent;
		parent.attachChild(node);
	}

	/* detach from node */
	public void detach() {
		if (parent != null) {
			parent.detachChild(node);
		}
	}

	/* get world translation */
	public Vector3f getWorldTranslation() {
		return node.getWorldTranslation();
	}

	/* set paused */
	public void setPaused(boolean pause) {
		if (pause) {
			entity.setAnimationSpeed(0);
		} else {
			entity.setAnimationSpeed(1);
		}
	}

	/* set local scale */
	public void setLocalScale(float localScale) {
		this.node.setLocalScale(localScale);
	}

}
