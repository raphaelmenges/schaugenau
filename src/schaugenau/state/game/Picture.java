package schaugenau.state.game;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

import schaugenau.app.App;
import schaugenau.core.SoundManager.Sound;
import schaugenau.core.StaticEntity;
import schaugenau.gui.GuiAdapter;
import schaugenau.gui.GuiElement;

/**
 * Distributed under the MIT License. (See accompanying file LICENSE or copy at
 * https://github.com/raphaelmenges/schaugenau/blob/master/src/LICENSE)
 * 
 * Picture. Object which displays image.
 * 
 * @author Raphael Menges
 *
 */

public class Picture extends GuiElement {

	private static Logger logger = Logger.getLogger(Picture.class);

	/** enumerations **/
	public enum PictureState {
		ENTER, SHOW, WAIT_TO_LEAVE, LEAVE, DIE
	};

	/** defines **/
	protected final float maxXLength = 8.0f;
	protected final float maxYLength = 6.0f;
	protected final float durationEntering = 3.0f;
	protected final float transitionEnteringShow = 1.0f;
	protected final float durationWaitToLeave = 3.0f;
	protected final float transitionShowWaitToLeave = 1f;
	protected final float durationLeaving = 1f;

	protected final Vector3f startPosition = new Vector3f(15f, -1.0f, -1f);
	protected final float startScale = 0.5f;
	protected final Vector3f showPosition = new Vector3f(5.0f, -0.5f, -1f);
	protected final float showScale = 1.0f;
	protected final Vector3f diePosition = new Vector3f(15f, -0.5f, -1f);

	protected final ColorRGBA initialColor = new ColorRGBA(1, 1, 1, 1);

	protected final ColorRGBA correctColor = new ColorRGBA(1, 1, 1, 1);
	protected final ColorRGBA incorrectColor = new ColorRGBA(0, 0, 0, 0);
	protected final float focusSpeed = 2.0f;

	protected final String gazeDataPath = "gazeData/";
	protected final String gazeDataFileTitle = "Image";

	protected final Vector3f glowPosition = new Vector3f(0, 0, -1);
	protected final float glowMaxScale = 1.5f;
	protected final ColorRGBA glowColor = new ColorRGBA(1, 1, 0.6f, 0.5f);
	protected final float glowScaleDuration = 0.2f;

	/** fields **/
	protected int ID;
	protected StaticEntity entity;
	protected PictureState pictureState;
	protected float time;
	protected boolean isRightPicture;
	protected boolean isCorrectPicture;
	protected float focus;
	protected PrintWriter gazeData;
	protected PrintWriter gazeDataBackup;
	protected List<String> gazeDataOutput;
	protected Vector2f textureResolution;
	protected boolean imageLoaded;
	protected float gazeTime;
	protected float presentationTime;
	protected StaticEntity glow;
	protected float glowScale;
	protected float glowScaleTime;
	protected boolean wasChosen;

	/** methods **/

	/* constructor */
	public Picture(App app, GuiAdapter guiAdapter, boolean isRightPicture, boolean isCorrectPicture,
			ImageLoader imageLoader) {

		super(app, guiAdapter, "Picture");

		/* init members */
		this.pictureState = PictureState.ENTER;
		this.time = 0;
		this.isRightPicture = isRightPicture;
		this.isCorrectPicture = isCorrectPicture;
		this.gazeDataOutput = new LinkedList<String>();
		this.gazeTime = 0;
		this.presentationTime = 0;
		this.glowScale = 1;
		this.glowScaleTime = 0;
		this.wasChosen = false;

		/* prepare texture loading */
		String name = isCorrectPicture ? imageLoader.getPrefixCorrectImage() : imageLoader.getPrefixIncorrectImage();
		this.ID = isCorrectPicture ? imageLoader.getCurrentCorrectID() : imageLoader.getCurrentIncorrectID();

		/* test whether image is there and load it */
		File file = new File(imageLoader.getPathToImages() + name + ID + "." + imageLoader.getPostfixImage());
		if (file.exists() && file.length() > 0) {
			entity = new StaticEntity(app, "Picture", "Plane", "Unshaded", name + ID, "", true,
					imageLoader.getPostfixImage(), true, true, false, false);
			this.imageLoaded = true;
		} else {
			entity = new StaticEntity(app, "Picture", "Plane", "Unshaded", "ImageNotFound", true, "png", true, true,
					false, false);
			this.imageLoaded = false;
		}
		entity.attachTo(node);

		/* scale plane corresponding size of image */
		this.textureResolution = entity.getSizeOfTexture();
		float x = textureResolution.x;
		float y = textureResolution.y;

		/* scale entity to fit into view */
		if (x > y) {
			entity.scale(new Vector3f(maxXLength, y / x * maxXLength, 1));
		} else {
			entity.scale(new Vector3f(x / y * maxYLength, maxYLength, 1));
		}
		entity.setColorParameter(initialColor);

		/* swap x value if necessary and set position and scale */
		if (!this.isRightPicture) {
			startPosition.setX(-startPosition.getX());
			showPosition.setX(-showPosition.getX());
			diePosition.setX(-diePosition.getX());
		}
		this.setLocalTranslation(startPosition);
		this.setLocalScale(startScale);

		/* glow of selection */
		glow = new StaticEntity(app, "Glow", "Plane", "Unshaded", "PictureGlow", true, "png", true, true, false, false);
		glow.setColorParameter(glowColor);
		glow.setLocalTranslation(glowPosition);
		glow.setLocalScale(entity.getLocalScale().clone());
		glow.attachTo(node);
	}

	/* update, returns focus */
	float update(float sTpf, float tpf, boolean focused, boolean selectionFinished, boolean isChosen) {

		/* use states for decision */
		switch (pictureState) {
		case ENTER: {

			/* enter animation with curve */
			float t = Math.min(1, time / durationEntering);
			float a = (float) Math.sqrt(Math.sqrt(t));
			float b = t * t;

			/* use square of t for interesting animation */
			Vector3f targetPosition = new Vector3f();
			targetPosition.x = (1 - a) * startPosition.x + a * showPosition.x;
			targetPosition.y = (1 - b) * startPosition.y + b * showPosition.y;
			targetPosition.z = (1 - t) * startPosition.z + t * showPosition.z;
			this.setLocalTranslation(targetPosition);

			float targetScale = (1 - b) * startScale + b * showScale;
			this.setLocalScale(targetScale);

			/* check for state change */
			if (t >= 1) {
				pictureState = PictureState.SHOW;
				this.setLocalTranslation(showPosition.clone());
				this.setLocalScale(showScale);
				time = 0;
			}
			break;
		}
		case SHOW: {

			/* calculate focus */
			if (focused) {
				focus += focusSpeed * sTpf;
			} else {
				focus -= focusSpeed * sTpf;
			}
			focus = Math.max(0, Math.min(1, focus));

			/* track player's gaze on image of picture */
			collectGazeData(tpf);
			presentationTime += tpf;

			/* glow of chosen picture */
			showGlow(isChosen, sTpf);

			/* play sound if it was chosen right now */
			if (!this.wasChosen && isChosen) {
				this.app.getSoundManager().playSound(Sound.SELECT_PICTURE, true);
				this.wasChosen = true;
			}
			if (!isChosen) {
				this.wasChosen = false;
			}

			/* check for state change */
			if (selectionFinished) {
				pictureState = PictureState.WAIT_TO_LEAVE;

				/* detach glow if already necessary */
				if (!this.isCorrectPicture) {
					glow.detach();
				}

				/* save gaze data of this pictures's image */
				if (imageLoaded) {
					saveGazeDataOfImage();
				}

				time = 0;
			}
			break;
		}
		case WAIT_TO_LEAVE: {

			/* calculate transition time */
			float transition = Math.min(1, time / transitionShowWaitToLeave);

			/* calculate color using transition */
			ColorRGBA targetColor = new ColorRGBA();
			if (isCorrectPicture) {
				targetColor.interpolate(initialColor, correctColor, transition);
			} else {
				targetColor.interpolate(initialColor, incorrectColor, transition);
			}
			entity.setColorParameter(targetColor);

			/* fade out glow */
			ColorRGBA targetGlowColor = glowColor.clone().mult(new ColorRGBA(1, 1, 1, 1 - transition));
			glow.setColorParameter(targetGlowColor);

			/* calculate t */
			float t = Math.min(1, time / (durationWaitToLeave + transitionShowWaitToLeave));

			/* next state */
			if (t >= 1) {
				pictureState = PictureState.LEAVE;
				glow.detach();
				time = 0;
			}
			break;
		}
		case LEAVE: {

			/* leave animation */
			float t = Math.min(1, time / durationLeaving);
			this.setLocalTranslation(new Vector3f().interpolate(showPosition, diePosition, t));

			/* check whether picture has reached end of life */
			if (t >= 1) {
				pictureState = PictureState.DIE;
			}
			break;
		}
		case DIE: {

			/* wait to die */
			break;
		}
		}

		/* increment time (after everything else) */
		time += sTpf;

		return focus;
	}

	/* is out of sight */
	public boolean outOfSight() {
		return pictureState == PictureState.DIE;
	}

	/* show or not show glow */
	protected void showGlow(boolean show, float tpf) {

		/* do scaling of glow */
		float t = glowScaleTime / glowScaleDuration;
		glowScale = t * glowMaxScale + (1.0f - t);
		glow.setLocalScale(entity.getLocalScale().clone().mult(glowScale));

		/* use time per frame animation of scaling */
		if (show) {
			glowScaleTime += tpf;
		} else {
			glowScaleTime -= tpf;
		}

		/* clamp time */
		glowScaleTime = Math.min(glowScaleDuration, Math.max(0, glowScaleTime));
	}

	/* collect gaze date */
	protected void collectGazeData(float tpf) {
		Vector2f cursor = this.guiAdapter.getCursor();
		Vector3f scale = entity.getLocalScale();

		float gazeX = cursor.x - this.getLocalTranslation().x;
		float gazeY = cursor.y - this.getLocalTranslation().y;
		if (Math.abs(gazeX) < (scale.x / 2.0f) && Math.abs(gazeY) < (scale.y / 2.0f)) {

			float relativeGazeX = (gazeX / scale.x) + 0.5f;
			float relativeGazeY = 1.0f - ((gazeY / scale.y) + 0.5f);

			float xResolution = textureResolution.x;
			float yResolution = textureResolution.y;

			int absoluteGazeX = (int) (relativeGazeX * xResolution);
			int absoluteGazeY = (int) (relativeGazeY * yResolution);

			/* data formatter */
			DecimalFormat format = new DecimalFormat("#.#####");
			DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols();
			formatSymbols.setDecimalSeparator('.');
			format.setDecimalFormatSymbols(formatSymbols);

			gazeDataOutput.add("" + String.format("%04d", absoluteGazeX) + "; " + String.format("%04d", absoluteGazeY)
					+ "; " + format.format(gazeTime));

			gazeTime += tpf;

		} else {

			gazeTime = 0;
		}
	}

	/* save gaze data of image */
	protected void saveGazeDataOfImage() {

		/* path to file */
		DateFormat fileDateFormat = new SimpleDateFormat("yyyy-MM");
		Date fileDate = new Date();
		String path = gazeDataPath + gazeDataFileTitle;
		String fileName = String.format("%04d", ID) + "." + fileDateFormat.format(fileDate) + ".txt";

		/* format for floats */
		DecimalFormat format = new DecimalFormat();
		DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols();
		formatSymbols.setDecimalSeparator('.');
		format.setDecimalFormatSymbols(formatSymbols);

		/* open file */
		try {
			gazeData = new PrintWriter(new BufferedWriter(new FileWriter(path + fileName, true)));

			/* date */
			DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ssZ");
			Date gazeDate = new Date();
			gazeData.println(dateFormat.format(gazeDate));

			/* screen resolution */
			gazeData.println("Display resolution: " + (int) this.app.getWindowResolution().x + "x"
					+ (int) this.app.getWindowResolution().y);

			/* screen covered by image */
			float wholeArea = guiAdapter.getWidth() * guiAdapter.getHeight();
			float imageArea = entity.getLocalScale().x * entity.getLocalScale().y * showScale;
			float screenCoveredByImage = 100.0f * imageArea / wholeArea;

			format.applyPattern("#.##");
			gazeData.println("Screen covered by image: " + format.format(screenCoveredByImage) + "%");

			/* time image was presented to player */
			format.applyPattern("#.####");
			gazeData.println("Seconds of presentation: " + format.format(presentationTime));

			/* displayed as correct or incorrect image */
			String displayedAs;
			if (this.isCorrectPicture) {
				displayedAs = "CORRECT";
			} else {
				displayedAs = "INCORRECT";
			}
			gazeData.println("Image displayed as: " + displayedAs);
			if (this.wasChosen) {
				gazeData.println("Image was chosen by user");
			} else {
				gazeData.println("Image was not chosen by user");
			}

			/* output raw gaze data */
			gazeData.println("Following: 'X; Y; Seconds since start of gaze'");
			for (String output : gazeDataOutput) {
				gazeData.println(output);
			}

			gazeData.println();
			gazeData.flush();
			gazeData.close();

		} catch (IOException e) {
			logger.error(path + fileName + " not found!", e);
		}

	}

	public int getID() {
		return this.ID;
	}
}
