package schaugenau.gui;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

import schaugenau.app.App;
import schaugenau.core.SoundManager.Sound;
import schaugenau.core.StaticEntity;

/**
 * Distributed under the MIT License. (See accompanying file LICENSE or copy at
 * https://github.com/raphaelmenges/schaugenau/blob/master/src/LICENSE)
 * 
 * Button gui element.
 * 
 * @author Raphael Menges
 *
 */

public class Button extends GuiElement {

	/** defines **/
	protected final float forwardSpeed = 300;
	protected final float backwardSpeed = 600;
	protected final float backwardAccelerationTime = 3;
	protected final float pressAnimationDuration = 0.3f;
	protected final float pressAnimationScale = 0.2f;
	protected final float focusPulseFrequence = 0.5f;

	/** fields **/
	protected App app;

	protected StaticEntity ring;
	protected StaticEntity background;
	protected StaticEntity shadow;
	protected StaticEntity fill;
	protected Node symbol;

	protected float symbolScale;
	protected float angle;
	protected float backwardAcceleration;
	protected boolean pressed;
	protected float pressAnimation;
	protected float focusTime;
	protected float focusPulseScale;

	protected Vector2f hitOffset;
	protected Vector2f hitBox;
	protected boolean useHitBox;

	/** methods **/

	/* constructor */
	public Button(App app, GuiAdapter guiAdapter, Vector3f localTranslation, float localScale, String name,
			Vector2f hitOffset, Vector2f hitBox) {
		super(app, guiAdapter, name);

		/* app alias is always useful */
		this.app = app;

		/* nasty way for hit box usage */
		if (hitOffset == null || hitBox == null) {
			this.useHitBox = false;
		} else {
			this.hitOffset = hitOffset.clone();
			this.hitBox = hitBox.clone();
			this.useHitBox = true;
		}

		/* symbol */
		this.symbol = new Node(name + "Symbol");

		/* create static entities */
		this.ring = new StaticEntity(app, name + "Ring", "EyeButton-Ring", "Unshaded", "EyeButton", false, "png", true,
				false, false, false);
		this.background = new StaticEntity(app, name + "Background", "EyeButton-Background", "Unshaded", "EyeButton",
				false, "png", true, false, false, false);
		this.shadow = new StaticEntity(app, name + "Shadow", "EyeButton-Shadow", "Unshaded", "EyeButton-Shadow", true,
				"png", true, true, false, false);
		this.fill = new StaticEntity(app, name + "Fill", "EyeButton-Fill", "Unshaded", "LaGa", false, "png", true,
				false, false, false);

		/* attach all static entities to root node */
		this.ring.attachTo(node);
		this.background.attachTo(node);
		this.node.attachChild(symbol);

		this.shadow.attachTo(node);
		this.shadow.scale(1.4f);

		this.fill.attachTo(node);

		/* move them, because no z-Buffer in GUI or so */
		this.background.move(new Vector3f(0, 0, -1));
		this.shadow.move(new Vector3f(0, 0, -2));
		this.fill.move(new Vector3f(0, 0, -0.5f));

		/* transform root */
		this.node.setLocalTranslation(localTranslation);
		this.node.scale(localScale);
		this.symbolScale = 1;

		/* some presets */
		reset();
	}

	/* constructor without hit box */
	public Button(App app, GuiAdapter guiAdapter, Vector3f localTranslation, float localScale, String name) {
		this(app, guiAdapter, localTranslation, localScale, name, null, null);
	}

	/* update, returns true when pressed */
	public boolean simpleUpdate(float tpf, boolean focused) {

		/* HITTING */

		/* reset of pressed */
		pressed = false;

		/* check for collision */
		float deltaAngle = 0;

		/* get translation and scale in gui space */
		Vector3f translation = guiAdapter.getGuiSpaceTranslation(node);
		float scale = guiAdapter.getGuiSpaceScale(node);

		/* some stuff needed for further calculations */
		Vector2f cursor = guiAdapter.getCursor();

		/* calculate distance vector */
		Vector2f vector = new Vector2f((translation.x - cursor.x), (translation.y - cursor.y));

		/* calculate length of distance vector */
		float distance = vector.length();

		/* decide whether there was a hit */
		boolean hit;
		if (useHitBox) {
			boolean xHit = Math.abs(cursor.x - translation.x - hitOffset.x * scale) <= (hitBox.x * 0.5f * scale);
			boolean yHit = Math.abs(cursor.y - translation.y - hitOffset.y * scale) <= (hitBox.y * 0.5f * scale);
			hit = xHit && yHit;
		} else {

			/* decide whether hit or not */
			hit = (distance <= 0.5f * scale);
		}

		/* CIRCULAR ANIMATION */

		float distanceDependentAcceleration = 1;

		/* forward */
		if (hit) {

			/* collision is there, do something */
			distanceDependentAcceleration = (float) Math.exp(-1.5f * distance * (1f / scale));
			backwardAcceleration = 0;
			deltaAngle = -forwardSpeed * distanceDependentAcceleration * tpf;

			/* check whether ring is one time around the button */
			if (angle <= -360) {
				pressed = true;
				pressAnimation = 1;
				angle = 0;

				/* play sound */
				this.app.getSoundManager().playSound(Sound.BUTTON_PRESSED, true);
			} else {

				/* play ticking sound */
				this.app.getSoundManager().playSound(Sound.TICK, false);
			}
		}

		/* backward */
		if (!hit) {

			/* check whether ring can go back */
			distanceDependentAcceleration = (float) Math.exp(-0.5f * distance * (1f / scale));
			if (angle < 0) {
				backwardAcceleration += tpf / backwardAccelerationTime;
				deltaAngle = backwardSpeed * (1 - distanceDependentAcceleration) * backwardAcceleration * tpf;
			} else {
				angle = 0;
			}
		}

		/* use delta angle, whether forward or backward */
		angle += deltaAngle;
		if (deltaAngle != 0) {
			ring.setLocalRotation(new Vector3f(0, 0, (float) Math.toRadians(angle)));
		}

		/* ICON ANIMATION */

		/* visualize focus on button */
		if (focused) {
			focusTime += tpf;
			focusPulseScale = 1.0f + 0.1f * (float) Math.sin(focusTime * focusPulseFrequence * Math.PI * 2.0);

			/* reset time */
			if (focusTime >= 1.0f / focusPulseFrequence) {
				focusTime = 0;
			}
		} else {
			focusTime = 0;
			focusPulseScale = 1;
		}

		/* press animation */
		if (pressAnimation > 0) {
			pressAnimation -= tpf / pressAnimationDuration;

			/* may not be negative */
			if (pressAnimation < 0) {
				pressAnimation = 0;
			}
		}

		/* do animation and scaling of icon */
		symbol.setLocalScale((1 - pressAnimationScale * (float) Math.sin(Math.toRadians(180 * pressAnimation)))
				* symbolScale * focusPulseScale);

		/* FILL ANIMATION */
		fill.setLocalScale(Math.abs(angle) / 360.0f);

		/* return whether pressed or not */
		return pressed;
	}

	/* reset button */
	public void reset() {
		angle = 0;
		focusTime = 0;
		focusPulseScale = 1;
		pressed = false;
		ring.setLocalRotation(new Vector3f(0, 0, 0));
		pressAnimation = 0;
		symbol.setLocalScale(symbolScale);
		fill.setLocalScale(0);
	}

	/* get node */
	@Override
	public Node getNode() {
		return node;
	}
}
