package schaugenau.core;

import com.jme3.math.ColorRGBA;

import schaugenau.app.App;

/**
 * Distributed under the MIT License. (See accompanying file LICENSE or copy at
 * https://github.com/raphaelmenges/schaugenau/blob/master/src/LICENSE)
 * 
 * Base class of all states which are fadeable.
 * 
 * @author Raphael Menges
 *
 */

public abstract class FadableState extends State {

	/** enumerations **/
	public enum PresentationState {
		INTRO, RUNNING, OUTRO
	}

	/** defines **/
	protected final static ColorRGBA standardFadingColor = new ColorRGBA(0.5f, 0.55f, 0.6f, 1);
	protected final static float standardInFadingDuration = 1.0f;
	protected final static float standardOutFadingDuration = 0.5f;

	/** fields **/
	protected ColorRGBA fadeInColor;
	protected ColorRGBA fadeOutColor;
	protected float fadeInDuration;
	protected float fadeOutDuration;
	protected StaticEntity fadeCurtain;
	protected float fadeAlpha;
	protected float fadeInTime;
	protected float fadeOutTime;
	protected PresentationState presentationState;

	/** methods **/

	/* constructor */
	public FadableState(App app, String name, boolean debugging, ColorRGBA fadeInColor, float fadeInDuration,
			ColorRGBA fadeOutColor, float fadeOutDuration) {
		super(app, name, debugging);

		/* fading stuff */
		this.fadeInColor = fadeInColor;
		this.fadeInDuration = fadeInDuration;
		this.fadeOutColor = fadeOutColor;
		this.fadeOutDuration = fadeOutDuration;

		/* create fadeCurtain */
		this.fadeCurtain = new StaticEntity(app, name + "FadeCurtain", "Plane", "Unshaded", "White", true, "png", true,
				false, false, false);
		this.fadeCurtain.scale(guiAdapter.getWidth(), guiAdapter.getHeight(), 0);
		this.fadeCurtain.move(0, 0, fadeCurtainZ);
		this.fadeCurtain.attachTo(guiAdapter.getNode());

		this.fadeAlpha = 1;
	}

	/* simplified constructor */
	public FadableState(App app, String name, boolean debugging, float fadeInDuration, float fadeOutDuration) {
		this(app, name, debugging, standardFadingColor.clone(), fadeInDuration, standardFadingColor.clone(),
				fadeOutDuration);
	}

	/* another simplified constructor */
	public FadableState(App app, String name, boolean debugging) {
		this(app, name, debugging, standardInFadingDuration, standardOutFadingDuration);
	}

	/* update */
	@Override
	public boolean update(float tpf, boolean buzzerPressed) {
		super.update(tpf, buzzerPressed);

		boolean hasDetachedItself = false;

		/* state machine */
		switch (presentationState) {
		case INTRO:

			/* do introduction */
			if (fadeIn(tpf)) {
				presentationState = PresentationState.RUNNING;
			}
			break;
		case RUNNING:

			/* state externally changed */
			running(tpf, buzzerPressed);
			break;
		case OUTRO:

			/* do outfading */
			if (fadeOut(tpf)) {
				detach();
				hasDetachedItself = true;
			}
			break;
		}
		return hasDetachedItself;
	}

	/* fade in, returns if finished */
	protected boolean fadeIn(float tpf) {

		boolean fadingDone = false;
		fadeAlpha = fadeInTime / fadeInDuration;

		/* test if fading is done */
		if (fadeInTime <= 0) {
			fadeAlpha = 0;
			fadingDone = true;
		}

		/* some funny function for alpha */
		float visualFadeAlpha = fadeAlpha * fadeAlpha;

		/* do fading */
		fadeCurtain.setColorParameter(new ColorRGBA(fadeInColor.r, fadeInColor.g, fadeInColor.b, visualFadeAlpha));

		/* relative fade in time */
		fadeInTime -= tpf;

		return fadingDone;

	}

	/* running */
	protected void running(float tpf, boolean buzzerPressed) {

	}

	/* fade out, returns if finished */
	protected boolean fadeOut(float tpf) {

		boolean fadingDone = false;
		fadeAlpha = fadeOutTime / fadeOutDuration;

		/* test if fading is done */
		if (fadeOutTime >= fadeOutDuration) {
			fadeAlpha = 1;
			fadingDone = true;
		}

		/* some funny function for alpha */
		float visualFadeAlpha = fadeAlpha * fadeAlpha;

		/* do fading */
		fadeCurtain.setColorParameter(new ColorRGBA(fadeOutColor.r, fadeOutColor.g, fadeOutColor.b, visualFadeAlpha));

		/* relative fade out time */
		fadeOutTime += tpf;

		return fadingDone;
	}

	/* introduce state */
	public void intro() {
		attach();
		fadeAlpha = 1;
		fadeCurtain.setColorParameter(new ColorRGBA(fadeInColor.r, fadeInColor.g, fadeInColor.b, fadeAlpha));
		presentationState = PresentationState.INTRO;
	}

	/* exit state */
	public void outro() {
		presentationState = PresentationState.OUTRO;
	}

	/* attach */
	@Override
	protected void attach() {
		super.attach();

		/* set up fade times */
		fadeInTime = fadeInDuration;
		fadeOutTime = 0;
	}

}
