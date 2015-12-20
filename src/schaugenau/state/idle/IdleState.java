package schaugenau.state.idle;

import java.util.Random;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

import schaugenau.app.App;
import schaugenau.core.AnimEntity;
import schaugenau.core.FadableState;
import schaugenau.core.SimpleWorldBackground;
import schaugenau.core.StaticEntity;
import schaugenau.database.ScoreOperations;
import schaugenau.font.Font;
import schaugenau.font.Text;
import schaugenau.font.Text.Alignment;
import schaugenau.font.TextBox;
import schaugenau.gui.GuiDecoration;

/**
 * Distributed under the MIT License. (See accompanying file LICENSE or copy at
 * https://github.com/raphaelmenges/schaugenau/blob/master/src/LICENSE)
 * 
 * Idle state.
 * 
 * @author Raphael Menges
 *
 */

public class IdleState extends FadableState {

	/** enumerations **/
	public enum InnerIdleState {
		HOURLY_HIGHSCORE, ALL_TIME_HIGHSCORE, CREDITS, LOGO, INSTRUCTIONS
	}

	/** defines */

	protected final float stateDuration = 10;
	protected final float stateMoveDuration = 3.0f;
	protected final Vector3f guiNodeRestPosition = new Vector3f(18, 0, 0);
	protected final Vector3f nodeRestPosition = new Vector3f(8, 0, 0);

	/* highscore */
	protected final float highscoreListNumbersHorizontalPosition = -3.5f;
	protected final float highscoreListPlayersHorizontalPosition = -3.3f;
	protected final float highscoreListScoresHorizontalPosition = 4.4f;
	protected final float highscoreListUpperStartPosition = 2.9f;
	protected final float highscoreListRowHeight = 0.8f;
	protected final float highscoreListTextScale = 0.65f;
	protected final int highscoreLength = 7;
	protected final int highscoreHourInterval = 3;
	protected final Vector3f highscoreHeadPosition = new Vector3f(0, 4, 0);
	protected final float lastPlayerAppendingOffset = 0.5f;
	protected final float separatorOffset = 0.85f;
	protected final String separatorContent = ". . .";
	protected final float scoreHeadBackgroundZ = -1;
	protected final float scoreHeadBackgroundOffset = 0.5f;
	protected final ColorRGBA scoreBackgroundColor = new ColorRGBA(1, 1, 0.6f, 0.6f);
	protected final float scoreBackgroundAnimationDuration = 2;

	/* credits */
	protected final float WeSTLogoScale = 0.0125f;
	protected final float UniLogoScale = 0.0065f;
	protected final float EyevidoLogoScale = 0.007f;
	protected final Vector3f WeSTLogoPosition = new Vector3f(0.2f, 4.0f, 0);
	protected final Vector3f UniLogoPosition = new Vector3f(0, -2.6f, 0);
	protected final Vector3f EyevidoLogoPosition = new Vector3f(0, -0.6f, 0);

	protected final String developersTextContent = "Kevin Schmidt\nRaphael Menges";
	protected final float developersTextScale = 0.5f;
	protected final Vector3f developersTextPosition = new Vector3f(0, 2.2f, 0);

	protected final float developedByTextScale = 0.35f;
	protected final Vector3f developedByTextPosition = new Vector3f(-2.2f, 2.2f, 0);

	protected final float supportedByTextScale = 0.35f;
	protected final Vector3f supportedByTextPosition = new Vector3f(-2.2f, 0.1f, 0);

	protected final String supportersTextContent = "Dr. Tina Walber - Christoph Schaefer";
	protected final float supportersTextScale = 0.25f;
	protected final Vector3f supportersTextPosition = new Vector3f(0, -1.5f, 0);

	protected final float musicianTextScale = 0.25f;
	protected final Vector3f musicianTextPosition = new Vector3f(0, -3.5f, 0);

	/* logo */
	protected final Vector3f logoPosition = new Vector3f(0.1f, 0.5f, 0);
	protected final float logoScale = 2;
	protected final float logoAnimationSpeed = 0.5f;
	protected final Vector3f logoSubTextPosition = new Vector3f(0, -0.7f, 0);
	protected final float logoSubTextScale = 0.25f;

	protected final String urlContent = "schaugenau.west.uni-koblenz.de";
	protected final float urlScale = 0.15f;
	protected final Vector3f urlPosition = new Vector3f(0, -0.9f, 0);

	/* press buzzer instruction */
	protected final Vector3f pressBuzzerPosition = new Vector3f(0, -7, -1);
	protected final Vector3f pressBuzzerTextPosition = new Vector3f(0, 1.5f, 1);
	protected final float pressBuzzerTextScale = 0.85f;
	protected final float pressBuzzerBackgroundScale = 2;
	protected final float pressBuzzerTimeMultiplicator = 0.8f;
	protected final float pressBuzzerAnimationOffset = 0.2f;
	protected final float pressBuzzerDownMultiplicator = -6f;

	/* instructions */
	protected final float instructionsTextYOffset = 2.95f;
	protected final float instructionsTextScale = 0.55f;
	protected final float instructionsScale = 1.2f;
	protected final float instructionsYOffset = 0.5f;
	protected final float instructionsBackgroundZOffset = -1f;
	protected final float instructionsHumanAnimationMultiplier = 0.5f;

	/* game style */
	protected final float durationOfGameStyleConstancy = 30;

	/* other */
	protected final float durationOfBuzzerPressing = 1f;
	protected final float durationUntilResetFromInstructions = 60;
	protected final String dbAliasForEmpty = "empty";

	/* score operations */
	protected final ScoreOperations scoreOperations;

	/** fields **/

	/* hourly highscore */
	protected Node hourlyHighscore;
	protected Node hourlyHighscoreList;
	protected Text hourlyHighscoreHeadText;
	protected StaticEntity hourlyScoreBackground;
	protected Text hourlySeparator;
	protected StaticEntity hourlyScoreHeadBackground;

	/* all-time highscore */
	protected Node allTimeHighscore;
	protected Node allTimeHighscoreList;
	protected Text allTimeHighscoreHeadText;
	protected StaticEntity allTimeScoreBackground;
	protected Text allTimeSeparator;
	protected StaticEntity allTimeScoreHeadBackground;

	/* credits */
	protected Node credits;
	protected TextBox developersText;
	protected Text developedByText;
	protected Text supportedByText;
	protected Text supportersText;
	protected Text musicianText;
	protected StaticEntity WeSTLogo;
	protected StaticEntity UniLogo;
	protected StaticEntity EyevidoLogo;

	/* logo */
	protected Node logo;
	protected AnimEntity logoEntity;
	protected Text logoSubText;
	protected Text url;

	/* inner state machine */
	protected InnerIdleState state;
	protected float stateTime;
	protected Node lastNode;
	protected Vector3f lastRestPosition;
	protected Node currentNode;
	protected Vector3f currentRestPosition;

	/* red buzzer */
	protected GuiDecoration pressBuzzer;
	protected int pressBuzzerTextIndex;
	protected int pressBuzzerBackgroundIndex;
	protected float pressBuzzerTime;
	protected Vector3f lastPressBuzzerPosition;
	protected boolean pressBuzzerRecovering;

	/* game style */
	protected Random random;
	protected float gameStyleConstancyTime;

	/* instructions */
	protected GuiDecoration instructions;
	protected int upperInstructionsTextIndex;
	protected int lowerInstructionsTextIndex;
	protected int humanInstructionsEntityIndex;
	protected float humandHeadAnimationTime;

	/* other */
	protected SimpleWorldBackground background;
	protected float timeUntilResetFromInstructions;
	protected float scoreBackgroundAnimationTime;

	/** methods **/

	/* constructor */
	public IdleState(App app, String name, boolean debugging) {
		super(app, name, debugging);

		/* database */
		scoreOperations = new ScoreOperations();

		/* dayly score */
		this.hourlyHighscore = new Node("DaylyHighscoreRoot");
		this.guiAdapter.attachChild(this.hourlyHighscore);

		this.hourlyHighscoreHeadText = new Text(this.app.getPrimaryGuiFont(), schaugenau.font.Text.Alignment.CENTER);
		this.hourlyHighscoreHeadText.setLocalTranslation(highscoreHeadPosition);
		this.hourlyHighscoreHeadText.attachTo(this.hourlyHighscore);

		this.hourlyHighscoreList = new Node("DaylyHighscoreListRoot");
		this.hourlyHighscore.attachChild(hourlyHighscoreList);

		this.hourlyScoreBackground = new StaticEntity(this.app, "HourlyScoreBackground", "ScoreBackground", "Unshaded",
				"White", true, "png", true, true, false, false);
		this.hourlyScoreBackground.setColorParameter(this.scoreBackgroundColor);

		this.hourlySeparator = new Text(this.app.getSecondaryGuiFont(), schaugenau.font.Text.Alignment.CENTER,
				separatorContent);

		this.hourlyScoreHeadBackground = new StaticEntity(this.app, "HourlyScoreHeadBackground", "ScoreHeadBackground",
				"Unshaded", "LaGa", false, "png", true, true, false, false);
		this.hourlyScoreHeadBackground.setLocalTranslation(this.highscoreHeadPosition.x,
				this.highscoreHeadPosition.y + this.scoreHeadBackgroundOffset,
				this.highscoreHeadPosition.z + this.scoreHeadBackgroundZ);
		this.hourlyScoreHeadBackground.attachTo(this.hourlyHighscore);

		/* all-time score */
		this.allTimeHighscore = new Node("AllTimeHighscoreRoot");
		this.guiAdapter.attachChild(this.allTimeHighscore);

		this.allTimeHighscoreHeadText = new Text(this.app.getPrimaryGuiFont(), schaugenau.font.Text.Alignment.CENTER);
		this.allTimeHighscoreHeadText.setLocalTranslation(highscoreHeadPosition);
		this.allTimeHighscoreHeadText.attachTo(this.allTimeHighscore);

		this.allTimeHighscoreList = new Node("AllTimeHighscoreListRoot");
		this.allTimeHighscore.attachChild(allTimeHighscoreList);

		this.allTimeScoreBackground = new StaticEntity(this.app, "AllTimeScoreBackground", "ScoreBackground",
				"Unshaded", "White", true, "png", true, true, false, false);
		this.allTimeScoreBackground.setColorParameter(this.scoreBackgroundColor);

		this.allTimeSeparator = new Text(this.app.getSecondaryGuiFont(), schaugenau.font.Text.Alignment.CENTER,
				separatorContent);

		this.allTimeScoreHeadBackground = new StaticEntity(this.app, "AllTimeScoreHeadBackground",
				"ScoreHeadBackground", "Unshaded", "LaGa", false, "png", true, true, false, false);
		this.allTimeScoreHeadBackground.setLocalTranslation(this.highscoreHeadPosition.x,
				this.highscoreHeadPosition.y + this.scoreHeadBackgroundOffset,
				this.highscoreHeadPosition.z + this.scoreHeadBackgroundZ);
		this.allTimeScoreHeadBackground.attachTo(this.allTimeHighscore);

		/* credits */
		this.credits = new Node("Credits");
		this.guiAdapter.attachChild(this.credits);

		this.developersText = new TextBox(this.app.getPrimaryGuiFont(), schaugenau.font.TextBox.Alignment.CENTER,
				developersTextContent);
		this.developersText.setLocalScale(developersTextScale);
		this.developersText.setLocalTranslation(developersTextPosition);
		this.developersText.attachTo(this.credits);

		this.developedByText = new Text(this.app.getPrimaryGuiFont(), schaugenau.font.Text.Alignment.LEFT);
		this.developedByText.setLocalScale(developedByTextScale);
		this.developedByText.setLocalTranslation(developedByTextPosition);
		this.developedByText.attachTo(this.credits);

		this.supportedByText = new Text(this.app.getPrimaryGuiFont(), schaugenau.font.Text.Alignment.LEFT);
		this.supportedByText.setLocalScale(supportedByTextScale);
		this.supportedByText.setLocalTranslation(supportedByTextPosition);
		this.supportedByText.attachTo(this.credits);

		this.supportersText = new Text(this.app.getPrimaryGuiFont(), schaugenau.font.Text.Alignment.CENTER,
				supportersTextContent);
		this.supportersText.setLocalScale(supportersTextScale);
		this.supportersText.setLocalTranslation(supportersTextPosition);
		this.supportersText.attachTo(this.credits);

		this.musicianText = new Text(this.app.getSecondaryGuiFont(), schaugenau.font.Text.Alignment.CENTER);
		this.musicianText.setLocalScale(musicianTextScale);
		this.musicianText.setLocalTranslation(musicianTextPosition);
		this.musicianText.attachTo(this.credits);

		this.WeSTLogo = new StaticEntity(this.app, "WEsTLogo", "Plane", "Unshaded", "WeST", true, "png", true, true,
				false, false);
		this.WeSTLogo.setLocalScale(this.WeSTLogo.getSizeOfTexture().x, this.WeSTLogo.getSizeOfTexture().y, 1);
		this.WeSTLogo.scale(WeSTLogoScale);
		this.WeSTLogo.setLocalTranslation(WeSTLogoPosition);
		this.WeSTLogo.attachTo(this.credits);

		this.UniLogo = new StaticEntity(this.app, "UniLogo", "Plane", "Unshaded", "UniKoblenzLandau", true, "png", true,
				true, false, false);
		this.UniLogo.setLocalScale(this.UniLogo.getSizeOfTexture().x, this.UniLogo.getSizeOfTexture().y, 1);
		this.UniLogo.scale(UniLogoScale);
		this.UniLogo.setLocalTranslation(UniLogoPosition);
		this.UniLogo.attachTo(this.credits);

		this.EyevidoLogo = new StaticEntity(this.app, "EyevidoLogo", "Plane", "Unshaded", "eyevido", true, "png", true,
				true, false, false);
		this.EyevidoLogo.setLocalScale(this.EyevidoLogo.getSizeOfTexture().x, this.EyevidoLogo.getSizeOfTexture().y, 1);
		this.EyevidoLogo.scale(EyevidoLogoScale);
		this.EyevidoLogo.setLocalTranslation(EyevidoLogoPosition);
		this.EyevidoLogo.attachTo(this.credits);

		/* logo */
		this.logo = new Node("LogoRoot");
		this.rootNode.attachChild(this.logo);

		this.logoEntity = new AnimEntity(this.app, "Logo", "Unshaded", false, "png", false, false, false, false);
		this.logoEntity.setAnimation("Idle", true, false);
		this.logoEntity.setAnimationSpeed(logoAnimationSpeed);
		this.logoEntity.setLocalScale(logoScale);
		this.logoEntity.setLocalTranslation(logoPosition);
		this.logoEntity.attachTo(this.logo);

		this.logoSubText = new Text(this.app.getPrimaryWorldFont(), schaugenau.font.Text.Alignment.CENTER);
		this.logoSubText.setLocalScale(logoSubTextScale);
		this.logoSubText.setLocalTranslation(logoSubTextPosition);
		this.logoSubText.attachTo(this.logo);

		this.url = new Text(this.app.getSecondaryWorldFont(), schaugenau.font.Text.Alignment.CENTER, urlContent);
		this.url.setLocalScale(urlScale);
		this.url.setLocalTranslation(urlPosition);
		this.url.attachTo(this.logo);

		/* buzzer hint for player */
		this.pressBuzzer = new GuiDecoration(this.app, this.guiAdapter, "PressBuzzer");
		this.pressBuzzer.setLocalTranslation(pressBuzzerPosition);
		this.pressBuzzer.attachTo(this.guiAdapter.getNode());

		this.pressBuzzerTextIndex = this.pressBuzzer.addText(this.app.getSecondaryGuiFont(),
				schaugenau.font.Text.Alignment.CENTER, "", pressBuzzerTextPosition, pressBuzzerTextScale);
		this.pressBuzzerBackgroundIndex = this.pressBuzzer.addStaticEntity("PressBuzzerBackground", "Unshaded", "LaGa",
				"png", false, new Vector3f(), new Vector3f(), this.pressBuzzerBackgroundScale);

		/* instructions */
		this.instructions = new GuiDecoration(this.app, this.guiAdapter, "Instructions");

		/* upper instruction text */
		this.upperInstructionsTextIndex = this.instructions.addText(this.app.getPrimaryGuiFont(),
				schaugenau.font.Text.Alignment.CENTER, "", new Vector3f(0, this.instructionsTextYOffset, 0),
				this.instructionsTextScale);

		/* lower instruction text */
		this.lowerInstructionsTextIndex = this.instructions.addText(this.app.getPrimaryGuiFont(),
				schaugenau.font.Text.Alignment.CENTER, "", new Vector3f(0, -this.instructionsTextYOffset, 0),
				this.instructionsTextScale);

		/* instructions entities */
		this.instructions.addStaticEntity("InstructionsBackground", "Unshaded", "LaGa", "png", false,
				new Vector3f(0, 0, instructionsBackgroundZOffset), new Vector3f(), 1);
		this.humanInstructionsEntityIndex = this.instructions.addStaticEntity("InstructionsHuman", "Unshaded", "LaGa",
				"png", false, new Vector3f(), new Vector3f(), 1);
		this.instructions.addStaticEntity("InstructionsScreen", "Unshaded", "BlurredScreenshot", "png", false,
				new Vector3f(0, 0, instructionsBackgroundZOffset), new Vector3f(), 1);

		/* scale instructions */
		this.instructions.setLocalScale(this.instructionsScale);
		this.instructions.setLocalTranslation(0, this.instructionsYOffset, 0);

		/* other */
		this.background = new SimpleWorldBackground(this.app, true);
		this.background.attachTo(rootNode);
		this.random = new Random();
	}

	/* update */
	@Override
	public boolean update(float tpf, boolean buzzerPressed) {
		boolean hasDetachedItself = super.update(tpf, buzzerPressed);

		this.background.update(tpf);

		/* some counter for game style change */
		this.gameStyleConstancyTime -= tpf;
		this.gameStyleConstancyTime = Math.max(this.gameStyleConstancyTime, 0);

		/* animate highlighting of last player */
		scoreBackgroundAnimationTime += tpf;
		scoreBackgroundAnimationTime = scoreBackgroundAnimationTime % scoreBackgroundAnimationDuration;
		ColorRGBA animatedScoreBackgroundColor = new ColorRGBA(scoreBackgroundColor.r, scoreBackgroundColor.g,
				scoreBackgroundColor.b, scoreBackgroundColor.a * (0.5f + 0.5f * (float) Math
						.sin(scoreBackgroundAnimationTime / scoreBackgroundAnimationDuration * 2 * Math.PI)));
		this.hourlyScoreBackground.setColorParameter(animatedScoreBackgroundColor);
		this.allTimeScoreBackground.setColorParameter(animatedScoreBackgroundColor);

		/* go out of idle */
		if (buzzerPressed && this.state != InnerIdleState.INSTRUCTIONS) {

			this.state = InnerIdleState.INSTRUCTIONS;
			this.stateTime = 0;

			/* press buzzer */
			this.lastPressBuzzerPosition = this.pressBuzzer.getLocalTranslation().clone();

			/* prepare resest from instructions screen */
			this.timeUntilResetFromInstructions = 0;

			/* show instructions */
			this.instructions.attachTo(guiAdapter.getNode());

			/* set buzzer pressed to false for this update */
			buzzerPressed = false;

			/* hide stuff */
			this.currentNode.setLocalTranslation(this.currentRestPosition);
			this.lastNode.setLocalTranslation(this.lastRestPosition);

		}

		/* move stuff */
		if (this.state != InnerIdleState.INSTRUCTIONS) {

			/* animate slides */
			float t = stateTime / stateMoveDuration;
			if (t < 1) {
				this.currentNode
						.setLocalTranslation(new Vector3f().interpolate(this.currentRestPosition, new Vector3f(), t));
				this.lastNode.setLocalTranslation(
						new Vector3f().interpolate(new Vector3f(), this.lastRestPosition.clone().negate(), t * t));
			} else {
				this.currentNode.setLocalTranslation(new Vector3f());
				this.lastNode.setLocalTranslation(this.lastRestPosition.clone().negate());
			}

			/* animate press buzzer */
			this.pressBuzzerTime += pressBuzzerTimeMultiplicator * tpf;
			this.pressBuzzerTime = (float) (this.pressBuzzerTime % (2 * Math.PI));
			this.pressBuzzer.setLocalTranslation(pressBuzzerPosition.clone().add(0f,
					pressBuzzerAnimationOffset * (float) Math.sin(pressBuzzerTime), 0f));
		}

		/* INNER STATE MACHINE */
		switch (this.state) {
		case HOURLY_HIGHSCORE: {
			if (stateTime >= stateDuration) {

				/* prepare next state */
				this.lastNode = hourlyHighscore;
				this.currentNode = allTimeHighscore;
				this.lastRestPosition = guiNodeRestPosition;
				this.currentRestPosition = guiNodeRestPosition;

				/* set next state */
				this.state = InnerIdleState.ALL_TIME_HIGHSCORE;
				this.stateTime = 0;
			}
			break;
		}
		case ALL_TIME_HIGHSCORE: {
			if (stateTime >= stateDuration) {

				/* prepare next state */
				this.lastNode = allTimeHighscore;
				this.currentNode = credits;
				this.lastRestPosition = guiNodeRestPosition;
				this.currentRestPosition = guiNodeRestPosition;

				/* set next state */
				this.state = InnerIdleState.CREDITS;
				this.stateTime = 0;
			}
			break;
		}
		case CREDITS: {
			if (stateTime >= stateDuration) {

				/* prepare next state */
				this.lastNode = credits;
				this.currentNode = logo;
				this.lastRestPosition = guiNodeRestPosition;
				this.currentRestPosition = nodeRestPosition;

				/* some special for the logo */
				this.logoEntity.restart();

				/* set next state */
				this.state = InnerIdleState.LOGO;
				this.stateTime = 0;
			}
			break;
		}
		case LOGO: {
			if (stateTime >= stateDuration) {

				/* prepare next state */
				this.lastNode = logo;
				this.currentNode = hourlyHighscore;
				this.lastRestPosition = nodeRestPosition;
				this.currentRestPosition = guiNodeRestPosition;

				/* set next state */
				this.state = InnerIdleState.HOURLY_HIGHSCORE;
				this.stateTime = 0;
			}
			break;
		}
		case INSTRUCTIONS: {

			/* animate human */
			this.humandHeadAnimationTime += tpf;
			this.humandHeadAnimationTime %= (float) (2.0f * Math.PI);
			this.instructions.getStaticEntity(this.humanInstructionsEntityIndex)
					.setLocalTranslation((float) Math.sin(this.humandHeadAnimationTime)
							* this.instructionsHumanAnimationMultiplier,
					(float) Math.cos(this.humandHeadAnimationTime) * this.instructionsHumanAnimationMultiplier * 0.25f,
					0);

			if (!this.pressBuzzerRecovering) {

				/* visualize buzzer moving down */
				float t = stateTime / durationOfBuzzerPressing;
				t = Math.min(1, t);
				this.pressBuzzer.setLocalTranslation(new Vector3f().interpolate(this.lastPressBuzzerPosition,
						this.pressBuzzerPosition.clone().add(0,
								this.pressBuzzerDownMultiplicator * pressBuzzerAnimationOffset, 0),
						(float) Math.sqrt(t)));

				/* check whether button was down */
				if (t >= 1) {
					this.pressBuzzerRecovering = true;
					this.pressBuzzer.getText(this.pressBuzzerTextIndex)
							.setContent(app.getMessages().getString("idle.pressBuzzerAgain"));
					this.lastPressBuzzerPosition = this.pressBuzzer.getLocalTranslation().clone();
					this.stateTime = 0;
				}

			} else {
				float t = stateTime / durationOfBuzzerPressing;

				/* recover it for moving it down again */
				if (t <= 1) {
					this.pressBuzzer.setLocalTranslation(
							new Vector3f().interpolate(this.lastPressBuzzerPosition, this.pressBuzzerPosition, t * t));
				} else {

					/* some up and down moving */
					t = t - 1;
					this.pressBuzzer.setLocalTranslation(this.pressBuzzerPosition.clone().add(0f,
							0.5f * pressBuzzerAnimationOffset * (float) Math.sin(t), 0f));
				}
			}

			/* start game if buzzer was again pressed */
			if (buzzerPressed) {

				/* save last press buzzer position */
				this.lastPressBuzzerPosition = this.pressBuzzer.getLocalTranslation().clone();
				/* move it down again */
				this.pressBuzzerRecovering = false;

				/* reset state time */
				this.stateTime = 0;

				/* only do calibration when eyetracking is enabled */
				if (this.app.getInput().isTracker()) {
					this.app.loadCalibrationState();
				} else {
					this.app.loadTutorialState();
				}
			}

			break;
		}
		}

		/* state time */
		this.stateTime += tpf;

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

		/* somehow ugly but should not be often used */
		if (this.state == InnerIdleState.INSTRUCTIONS) {
			this.timeUntilResetFromInstructions += tpf;
			if (this.timeUntilResetFromInstructions > durationUntilResetFromInstructions) {
				this.detach();
				this.attach();
			}
		}
	}

	/* fade out, returns if finished */
	@Override
	protected boolean fadeOut(float tpf) {

		boolean fadingDone = super.fadeOut(tpf);

		// nothing to do

		return fadingDone;
	}

	/* attach */
	@Override
	protected void attach() {
		super.attach();

		/* prepare inner state machine */
		this.state = InnerIdleState.HOURLY_HIGHSCORE;
		this.stateTime = stateMoveDuration;
		this.hourlyHighscore.setLocalTranslation(new Vector3f());
		this.allTimeHighscore.setLocalTranslation(guiNodeRestPosition);
		this.credits.setLocalTranslation(guiNodeRestPosition);
		this.logo.setLocalTranslation(nodeRestPosition);
		this.lastNode = this.logo;
		this.lastRestPosition = guiNodeRestPosition;
		this.currentNode = this.hourlyHighscore;
		this.currentRestPosition = guiNodeRestPosition;
		this.hourlyScoreBackground.detach();
		this.allTimeScoreBackground.detach();
		this.hourlySeparator.detach();
		this.allTimeSeparator.detach();

		/* hide instructions */
		this.instructions.detach();

		/* prepare highscore lists */
		prepareHighscoreList(this.hourlyHighscoreList, false);
		prepareHighscoreList(this.allTimeHighscoreList, true);

		/* fill texts */
		this.hourlyHighscoreHeadText.setContent(app.getMessages().getString("idle.hourlyHighscoreHead-A") + " "
				+ this.highscoreHourInterval + " " + app.getMessages().getString("idle.hourlyHighscoreHead-B"));
		this.allTimeHighscoreHeadText.setContent(app.getMessages().getString("idle.allTimeHighscoreHead"));
		this.pressBuzzer.getText(this.pressBuzzerTextIndex).setContent(app.getMessages().getString("idle.pressBuzzer"));
		this.logoSubText.setContent(app.getMessages().getString("idle.logoSubText"));
		this.instructions.getText(this.upperInstructionsTextIndex)
				.setContent(app.getMessages().getString("idle.upperInstructions"));
		this.instructions.getText(this.lowerInstructionsTextIndex)
				.setContent(app.getMessages().getString("idle.lowerInstructions"));
		this.developedByText.setContent(app.getMessages().getString("idle.developedBy"));
		this.supportedByText.setContent(app.getMessages().getString("idle.supportedBy"));
		this.musicianText.setContent(app.getMessages().getString("idle.musician"));

		/* other resets */
		this.pressBuzzerTime = 0;
		this.pressBuzzerRecovering = false;
		this.scoreBackgroundAnimationTime = 0;

		/* not perfect, because sometimes there is selfattaching */
		this.gameStyleConstancyTime = durationOfGameStyleConstancy;

		/* stop input */
		this.app.getInput().stop();
	}

	/* detach */
	@Override
	protected void detach() {
		super.detach();

		/* activate input */
		this.app.getInput().start();

		/* choose next game style */
		if (this.gameStyleConstancyTime <= 0) {
			int rand = this.random.nextInt(3);
			if (rand == 0) {
				this.app.setCurrentGameStyle(schaugenau.app.App.GameStyle.DIRECT);
			} else if (rand == 1) {
				this.app.setCurrentGameStyle(schaugenau.app.App.GameStyle.DIRECT_WITH_GRID);
			} else {
				this.app.setCurrentGameStyle(schaugenau.app.App.GameStyle.INDIRECT);
			}

			/* and enable survey */
			this.app.setDoSurvey(true);
		}

		/* game was started, so... */
		this.app.setLastGameFinished(false);
	}

	/* stop */
	@Override
	public void stop() {
		super.stop();
	}

	protected void prepareHighscoreList(Node root, boolean allTime) {

		/* clean it before everything else */
		root.detachAllChildren();

		/* get names and scores of best players */
		String[] bestPlayers = {};
		int[] bestScores = {};

		/* get last player's score */
		boolean highlightLastPlayer = app.isLastGameFinished();
		int lastPlayerAllTimeRank = -1;
		int lastPlayerHourlyRank = -1;
		if (highlightLastPlayer) {
			lastPlayerAllTimeRank = scoreOperations.queryHighscoreRank(app.getCurrentScore(), null);
			lastPlayerHourlyRank = scoreOperations.queryHighscoreRankOfHours(app.getCurrentScore(), null,
					highscoreHourInterval);
		}

		/* choose which scores to load */
		if (allTime) {
			bestPlayers = scoreOperations.loadBestScorers(highscoreLength);
			bestScores = scoreOperations.loadBestScores(highscoreLength);
			visualizeHighscores(root, bestPlayers, bestScores, highlightLastPlayer, lastPlayerAllTimeRank,
					this.app.getCurrentPlayersName(), this.app.getCurrentScore(), this.allTimeScoreBackground,
					this.allTimeSeparator);
		} else {
			bestPlayers = scoreOperations.loadBestScorersOfHours(highscoreLength, highscoreHourInterval);
			bestScores = scoreOperations.loadBestScoresOfHours(highscoreLength, highscoreHourInterval);
			visualizeHighscores(root, bestPlayers, bestScores, highlightLastPlayer, lastPlayerHourlyRank,
					this.app.getCurrentPlayersName(), this.app.getCurrentScore(), this.hourlyScoreBackground,
					this.hourlySeparator);
		}

	}

	protected void visualizeHighscores(Node root, String[] bestPlayers, int[] bestScores, boolean highlightLastPlayer,
			int lastPlayersRank, String lastPlayersName, int lastPlayersScore, StaticEntity highlightingBackground,
			Text separator) {

		/* get best and create visual texts from strings and integers */
		for (int i = 0; i <= highscoreLength; i++) {

			/* get font */
			Font font;
			if (i < 3) {
				font = this.app.getPrimaryGuiFont();
			} else {
				font = this.app.getSecondaryGuiFont();
			}

			/* create variables */
			String player = null;
			int score = 0;
			int rank = 1;
			float yPosition = 0;

			if (i < highscoreLength) {

				/* just use the the given arrays */
				player = bestPlayers[i];
				score = bestScores[i];
				rank = i + 1;
				yPosition = highscoreListUpperStartPosition - i * highscoreListRowHeight;

			} else {

				/*
				 * check, whether last player is lower ranked than the previous
				 */
				if (highlightLastPlayer && lastPlayersRank > highscoreLength) {

					/* set specific font */
					font = this.app.getPrimaryGuiFont();

					/* get data from parameters */
					player = lastPlayersName;
					score = lastPlayersScore;
					rank = lastPlayersRank;
					yPosition = highscoreListUpperStartPosition - i * highscoreListRowHeight
							- lastPlayerAppendingOffset;

					/* add the separator */
					separator.setLocalTranslation(0, yPosition + separatorOffset, 0);
					separator.attachTo(root);

				} else {
					break;
				}
			}

			/* abort, if there is no player */
			if (player == null || player.isEmpty() || player.equals(dbAliasForEmpty)) {
				break;
			}

			/* create texts */
			Text numberText = new Text(font, Alignment.RIGHT, "" + (rank) + ".");
			Text playerText = new Text(font, Alignment.LEFT, player);
			Text scoreText = new Text(font, Alignment.RIGHT, "" + score);

			/* scale texts */
			numberText.scale(highscoreListTextScale);
			playerText.scale(highscoreListTextScale);
			scoreText.scale(highscoreListTextScale);

			/* move texts */
			numberText.move(highscoreListNumbersHorizontalPosition, yPosition, 0);
			playerText.move(highscoreListPlayersHorizontalPosition, yPosition, 0);
			scoreText.move(highscoreListScoresHorizontalPosition, yPosition, 0);

			/* attach texts to node */
			numberText.attachTo(root);
			playerText.attachTo(root);
			scoreText.attachTo(root);

			/* set highlight behind last player */
			if (highlightLastPlayer && lastPlayersRank == rank) {
				highlightingBackground.setLocalTranslation(0, yPosition + highscoreListTextScale / 2, -1);
				highlightingBackground.attachTo(root);
			}
		}
	}
}