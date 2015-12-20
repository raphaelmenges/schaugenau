package schaugenau.state.tutorial;

import java.util.ResourceBundle;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

import schaugenau.app.App;
import schaugenau.core.FadableState;
import schaugenau.core.SimpleWorldBackground;
import schaugenau.core.StaticEntity;
import schaugenau.font.Font;
import schaugenau.font.Text;
import schaugenau.font.TextBox;
import schaugenau.gui.IconButton;
import schaugenau.utilities.LerpValue;

/**
 * Distributed under the MIT License. (See accompanying file LICENSE or copy at
 * https://github.com/raphaelmenges/schaugenau/blob/master/src/LICENSE)
 * 
 * Tutorial state.
 * 
 * @author Raphael Menges
 *
 */

public class TutorialState extends FadableState {

	/** defines **/
	protected final float headerScale = 0.6f;
	protected final float boxScale = 0.35f;
	protected final float headerBoxOffset = -0.1f;

	protected final Vector3f leftTextAHeaderPosition = new Vector3f(-1.9f, 4.8f, 0);
	protected final Vector3f leftTextBHeaderPosition = new Vector3f(-1.75f, 1.9f, 0);
	protected final Vector3f leftTextCHeaderPosition = new Vector3f(-1.75f, -0.75f, 0);
	protected final Vector3f leftTextDHeaderPosition = new Vector3f(-1.9f, -3.75f, 0);

	protected final Vector3f rightTextAHeaderPosition = new Vector3f(1.85f, 4.8f, 0);
	protected final Vector3f rightTextBHeaderPosition = new Vector3f(1.7f, 1.9f, 0);
	protected final Vector3f rightTextCHeaderPosition = new Vector3f(1.7f, -0.75f, 0);
	protected final Vector3f rightTextDHeaderPosition = new Vector3f(1.85f, -3.75f, 0);

	protected final float flagZ = -5f;

	protected final float buttonScale = 3.25f;
	protected final float germanButtonTextScale = 0.35f;
	protected final float englishButtonTextScale = 0.4f;
	protected final Vector3f germanButtonPosition = new Vector3f(-7.75f, 0, 0);
	protected final Vector3f englishButtonPosition = new Vector3f(7.75f, 0, 0);
	protected final Vector2f buttonBoxScale = new Vector2f(1.5f, 2.0f);

	protected final float maxFlagAnimationOffset = 0.8f;
	protected final float flagAnimationDuration = 3;

	protected final float flagScale = 1.3f;
	protected final float maxFlagAlpha = 0.6f;
	protected final float minFlagAlpha = 0.2f;

	protected final float flagAnimationStrengthInterpolationSpeed = 0.4f;

	protected final float flagAnimationFreeWidth = 1.5f;

	protected final float gameStyleTextScale = 0.3f;
	protected final Vector3f gameStyleTextPosition = new Vector3f(9.85f, -5.5f, 0);

	/** fields **/
	protected IconButton germanButton;
	protected IconButton englishButton;
	protected SimpleWorldBackground background;
	protected boolean gameStarted;

	protected StaticEntity tutorialPictures;
	protected StaticEntity germanFlag;
	protected StaticEntity englishFlag;
	protected Vector3f germanFlagInitialPosition;
	protected Vector3f englishFlagInitialPosition;
	protected float flagAnimationTime;
	protected LerpValue germanFlagAnimationStrength;
	protected LerpValue englishFlagAnimationStrength;

	protected Text leftTextAHeader;
	protected TextBox leftTextABox;
	protected Text leftTextBHeader;
	protected TextBox leftTextBBox;
	protected Text leftTextCHeader;
	protected TextBox leftTextCBox;
	protected Text leftTextDHeader;
	protected TextBox leftTextDBox;

	protected Text rightTextAHeader;
	protected TextBox rightTextABox;
	protected Text rightTextBHeader;
	protected TextBox rightTextBBox;
	protected Text rightTextCHeader;
	protected TextBox rightTextCBox;
	protected Text rightTextDHeader;
	protected TextBox rightTextDBox;

	protected Text gameStyleText;

	/** methods **/

	/* constructor */
	public TutorialState(App app, String name, boolean debugging) {
		super(app, name, debugging);

		Font headerFont = this.app.getPrimaryGuiFont();
		Font boxFont = this.app.getSecondaryGuiFont();

		/* left texts */
		schaugenau.font.Text.Alignment leftHeaderAlignment = schaugenau.font.Text.Alignment.RIGHT;
		schaugenau.font.TextBox.Alignment leftBoxAlignment = schaugenau.font.TextBox.Alignment.RIGHT;

		/* message bundle */
		ResourceBundle messages = this.app.getSpecificMessages(schaugenau.app.App.Language.GERMAN);

		/* text A */
		createTutorialText(leftTextAHeader, leftTextABox, headerFont, boxFont, leftHeaderAlignment, leftBoxAlignment,
				leftTextAHeaderPosition, messages.getString("tutorial.textAHeader"),
				messages.getString("tutorial.textABox"));

		/* text b */
		createTutorialText(leftTextBHeader, leftTextBBox, headerFont, boxFont, leftHeaderAlignment, leftBoxAlignment,
				leftTextBHeaderPosition, messages.getString("tutorial.textBHeader"),
				messages.getString("tutorial.textBBox"));

		/* text c */
		createTutorialText(leftTextCHeader, leftTextCBox, headerFont, boxFont, leftHeaderAlignment, leftBoxAlignment,
				leftTextCHeaderPosition, messages.getString("tutorial.textCHeader"),
				messages.getString("tutorial.textCBox"));

		/* text d */
		createTutorialText(leftTextDHeader, leftTextDBox, headerFont, boxFont, leftHeaderAlignment, leftBoxAlignment,
				leftTextDHeaderPosition, messages.getString("tutorial.textDHeader"),
				messages.getString("tutorial.textDBox"));

		/* button */
		this.germanButton = new IconButton(this.app, this.guiAdapter, germanButtonPosition, buttonScale, "Icon-German",
				"GermanButton", new Vector2f(), buttonBoxScale);
		this.germanButton.attachTo(guiAdapter.getNode());

		/* right texts */
		schaugenau.font.Text.Alignment rightHeaderAlignment = schaugenau.font.Text.Alignment.LEFT;
		schaugenau.font.TextBox.Alignment rightBoxAlignment = schaugenau.font.TextBox.Alignment.LEFT;

		/* other message bundle */
		messages = this.app.getSpecificMessages(schaugenau.app.App.Language.ENGLISH);

		/* text A */
		createTutorialText(rightTextAHeader, rightTextABox, headerFont, boxFont, rightHeaderAlignment,
				rightBoxAlignment, rightTextAHeaderPosition, messages.getString("tutorial.textAHeader"),
				messages.getString("tutorial.textABox"));

		/* text b */
		createTutorialText(rightTextBHeader, rightTextBBox, headerFont, boxFont, rightHeaderAlignment,
				rightBoxAlignment, rightTextBHeaderPosition, messages.getString("tutorial.textBHeader"),
				messages.getString("tutorial.textBBox"));

		/* text c */
		createTutorialText(rightTextCHeader, rightTextCBox, headerFont, boxFont, rightHeaderAlignment,
				rightBoxAlignment, rightTextCHeaderPosition, messages.getString("tutorial.textCHeader"),
				messages.getString("tutorial.textCBox"));

		/* text d */
		createTutorialText(rightTextDHeader, rightTextDBox, headerFont, boxFont, rightHeaderAlignment,
				rightBoxAlignment, rightTextDHeaderPosition, messages.getString("tutorial.textDHeader"),
				messages.getString("tutorial.textDBox"));

		/* button */
		this.englishButton = new IconButton(this.app, this.guiAdapter, englishButtonPosition, buttonScale,
				"Icon-English", "EnglishButton", new Vector2f(), buttonBoxScale);
		this.englishButton.attachTo(guiAdapter.getNode());

		this.background = new SimpleWorldBackground(this.app, false);
		this.background.attachTo(rootNode);

		/* entity stuff */
		float verticalScale = this.guiAdapter.getExpectedVerticalExtent();
		float horizontalScale;

		/* tutorial pictures */
		this.tutorialPictures = new StaticEntity(this.app, "TutorialPictures", "Plane", "Unshaded", "Tutorial", true,
				"png", true, true, false, false);
		horizontalScale = verticalScale * this.tutorialPictures.getSizeOfTexture().x
				/ this.tutorialPictures.getSizeOfTexture().y;
		this.tutorialPictures.setLocalScale(new Vector3f(horizontalScale, verticalScale, 1));
		this.tutorialPictures.attachTo(guiAdapter.getNode());

		/* german flag */
		this.germanFlag = new StaticEntity(this.app, "GermanFlag", "Plane", "Unshaded", "GermanFlag", true, "png", true,
				true, false, false);
		horizontalScale = verticalScale * this.germanFlag.getSizeOfTexture().x / this.germanFlag.getSizeOfTexture().y;
		this.germanFlagInitialPosition = new Vector3f(this.guiAdapter.getLeft() + horizontalScale * 0.5f, 0, flagZ);
		this.germanFlag.setLocalTranslation(germanFlagInitialPosition);
		this.germanFlag.setLocalScale(new Vector3f(horizontalScale, verticalScale, 1));
		this.germanFlag.scale(flagScale);
		this.germanFlag.attachTo(guiAdapter.getNode());

		/* english flag */
		this.englishFlag = new StaticEntity(this.app, "EnglishFlag", "Plane", "Unshaded", "EnglishFlag", true, "png",
				true, true, false, false);
		horizontalScale = verticalScale * this.englishFlag.getSizeOfTexture().x / this.englishFlag.getSizeOfTexture().y;
		this.englishFlagInitialPosition = new Vector3f(this.guiAdapter.getRight() - horizontalScale * 0.5f, 0, flagZ);
		this.englishFlag.setLocalTranslation(englishFlagInitialPosition);
		this.englishFlag.setLocalScale(new Vector3f(horizontalScale, verticalScale, 1));
		this.englishFlag.scale(flagScale);
		this.englishFlag.attachTo(guiAdapter.getNode());

		/* flag animations */
		germanFlagAnimationStrength = new LerpValue(0);
		englishFlagAnimationStrength = new LerpValue(0);

		/* game style hint text */
		this.gameStyleText = new Text(this.app.getSecondaryGuiFont(), schaugenau.font.Text.Alignment.RIGHT);
		this.gameStyleText.setLocalScale(this.gameStyleTextScale);
		this.gameStyleText.setLocalTranslation(this.gameStyleTextPosition);
		this.gameStyleText.attachTo(guiAdapter.getNode());

	}

	/* update */
	@Override
	public boolean update(float tpf, boolean buzzerPressed) {
		boolean hasDetachedItself = super.update(tpf, buzzerPressed);

		if (!paused) {

			/* flag animation */
			float germanFlagAnimationTarget = 0;
			float englishFlagAnimationTarget = 0;
			float cursorX = this.guiAdapter.getCursor().x;
			if (cursorX < -flagAnimationFreeWidth * 0.5f) {
				/* target animation strength */
				germanFlagAnimationTarget = 1;
			}
			if (cursorX > flagAnimationFreeWidth * 0.5f) {
				/* target animation strength */
				englishFlagAnimationTarget = 1;
			}

			/* interpolate animation strength */
			germanFlagAnimationStrength.update(tpf, flagAnimationStrengthInterpolationSpeed, germanFlagAnimationTarget);
			englishFlagAnimationStrength.update(tpf, flagAnimationStrengthInterpolationSpeed,
					englishFlagAnimationTarget);

			/* pure sinus animation */
			flagAnimationTime += tpf;
			flagAnimationTime = flagAnimationTime % flagAnimationDuration;
			float flagAnimation = (float) Math.sin(Math.PI * 2 * flagAnimationTime / flagAnimationDuration);

			germanFlag.setLocalTranslation(this.germanFlagInitialPosition.clone().add(new Vector3f(
					germanFlagAnimationStrength.getValue() * flagAnimation * maxFlagAnimationOffset, 0, 0)));
			englishFlag.setLocalTranslation(this.englishFlagInitialPosition.clone().add(new Vector3f(
					englishFlagAnimationStrength.getValue() * flagAnimation * maxFlagAnimationOffset, 0, 0)));

			/* set alpha of flags */
			germanFlag.setColorParameter(new ColorRGBA(1, 1, 1, germanFlagAnimationStrength.getValue() * maxFlagAlpha
					+ (1 - germanFlagAnimationStrength.getValue()) * minFlagAlpha));
			englishFlag.setColorParameter(new ColorRGBA(1, 1, 1, englishFlagAnimationStrength.getValue() * maxFlagAlpha
					+ (1 - englishFlagAnimationStrength.getValue()) * minFlagAlpha));

			/* background animation */
			this.background.update(tpf);

			/* logic */
			boolean startGermanGame = germanButton.update(tpf, true);
			boolean startEnglishGame = englishButton.update(tpf, true);

			if (!this.gameStarted) {
				if (startGermanGame) {
					this.app.setLanguage(schaugenau.app.App.Language.GERMAN);
					this.app.updateHyperStateLanguage();
					this.app.loadGameState();
					this.gameStarted = true;

				} else if (startEnglishGame) {
					this.app.setLanguage(schaugenau.app.App.Language.ENGLISH);
					this.app.updateHyperStateLanguage();
					this.app.loadGameState();
					this.gameStarted = true;
				}
			}
		}
		return hasDetachedItself;
	}

	/* fade in, returns if finished */
	@Override
	protected boolean fadeIn(float tpf) {

		boolean fadingDone = super.fadeIn(tpf);

		/* nothing to do */

		return fadingDone;

	}

	/* running */
	@Override
	protected void running(float tpf, boolean buzzerPressed) {
		super.running(tpf, buzzerPressed);

		/* nothing to do */

	}

	/* fade out, returns if finished */
	@Override
	protected boolean fadeOut(float tpf) {

		boolean fadingDone = super.fadeOut(tpf);

		/* nothing to do */

		return fadingDone;
	}

	/* attach */
	@Override
	protected void attach() {
		super.attach();

		gameStarted = false;
		germanButton.reset();
		englishButton.reset();
		flagAnimationTime = 0;
		germanFlagAnimationStrength.setValue(0);
		englishFlagAnimationStrength.setValue(0);

		this.gameStyleText.setContent(this.app.getCurrentGameStyleString());
	}

	/* detach */
	@Override
	protected void detach() {
		super.detach();
	}

	/* stop */
	@Override
	public void stop() {
		super.stop();
	}

	/* helper to create all that texts */
	protected void createTutorialText(Text header, TextBox box, Font headerFont, Font boxFont,
			schaugenau.font.Text.Alignment headerAlignment, schaugenau.font.TextBox.Alignment boxAlignment,
			Vector3f headerPosition, String headerContent, String boxContent) {

		header = new Text(headerFont, headerAlignment, headerContent);
		header.setLocalTranslation(headerPosition.clone());
		header.scale(this.headerScale);
		header.attachTo(this.guiAdapter.getNode());

		box = new TextBox(boxFont, boxAlignment, boxContent);
		box.setLocalTranslation(headerPosition.clone().add(new Vector3f(0, this.headerBoxOffset, 0)));
		box.scale(this.boxScale);
		box.attachTo(this.guiAdapter.getNode());
	}

}