package schaugenau.state.game;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

import schaugenau.app.App;
import schaugenau.core.AnimNode;
import schaugenau.core.State;
import schaugenau.font.Font;
import schaugenau.font.Text;
import schaugenau.gui.GuiAdapter;
import schaugenau.gui.GuiDecoration;

/**
 * Distributed under the MIT License. (See accompanying file LICENSE or copy at
 * https://github.com/raphaelmenges/schaugenau/blob/master/src/LICENSE)
 * 
 * HUD for multiplicator and points.
 * 
 * @author Raphael Menges
 *
 */

public class HudFooter {

	/** defines **/
	protected final float animationDuration = 2f;
	protected final float scale = 1.1f;
	protected final float scoreTransitionSpeed = 25f;
	protected final float durationBeforeTransition = 0.4f;

	protected final Vector3f multiplicatorPosition = new Vector3f(-3.35f, 0.07f, 1f);
	protected final float multiplicatorAnimationScale = 2;
	protected final float multiplicatorAnimationDuration = 2;
	protected final Vector3f flowerScorePosition = new Vector3f(-1.24f, 0.07f, 0);
	protected final Vector3f gameScorePosition = new Vector3f(2.13f, 0.07f, 0);
	protected final Vector3f xPosition = new Vector3f(-2.65f, 0.02f, 0);
	protected final float noneMultiplicatorAlphaDuringAnimation = 0.2f;

	/** fields **/
	protected AnimNode animNode;
	protected Node parent;
	protected GuiDecoration guiDecoration;
	protected Font font;
	protected Font multiplicatorFont;
	protected int multiplicatorTextIndex;
	protected int flowerScoreTextIndex;
	protected int gameScoreTextIndex;
	protected float timeBeforeTransition;
	protected float multiplicatorAnimationTime;

	protected double internalMultiplicator;
	protected double internalFlowerScore;
	protected double internalGameScore;

	/** methods **/

	/* constructor */
	public HudFooter(App app, State state, GuiAdapter guiAdapter, Vector3f showPosition, Vector3f hidePosition) {

		/* create fonts */
		font = new Font(app, "Unshaded", app.getFontTextureName(), "png", new ColorRGBA(1f, 1f, 1f, 1f),
				app.getLetterWidth(), app.getLetterHeight(), false, true);
		multiplicatorFont = new Font(app, "Unshaded", app.getFontTextureName(), "png", new ColorRGBA(1f, 1f, 1f, 1f),
				app.getLetterWidth(), app.getLetterHeight(), false, true);

		/* create animated node */
		animNode = new AnimNode(state, "HudFooter-AnimNode", showPosition, hidePosition, animationDuration, false);
		animNode.getNode().setLocalScale(scale);

		/* create gui decoration */
		guiDecoration = new GuiDecoration(app, guiAdapter, "HudFooter-GuiDecoration");
		guiDecoration.attachTo(animNode.getNode());

		/* add static entity to gui decoration */
		guiDecoration.addStaticEntity("HudFooter", "Unshaded", "LaGa", "png", false, new Vector3f(), new Vector3f(), 1);

		/* add texts to gui decoration */
		multiplicatorTextIndex = guiDecoration.addText(multiplicatorFont, schaugenau.font.Text.Alignment.CENTER, "00",
				multiplicatorPosition, 1f);
		flowerScoreTextIndex = guiDecoration.addText(font, schaugenau.font.Text.Alignment.CENTER, "00",
				flowerScorePosition, 1f);
		gameScoreTextIndex = guiDecoration.addText(font, schaugenau.font.Text.Alignment.CENTER, "00000",
				gameScorePosition, 1f);
		guiDecoration.addText(font, schaugenau.font.Text.Alignment.LEFT, "*", xPosition, 1f);

	}

	/* update */
	public void update(float tpf) {

		if (timeBeforeTransition < 0) {

			/* add flower score to game score */
			if (internalFlowerScore > 0) {

				double prevInternalFlowerScore = internalFlowerScore;
				internalFlowerScore = Math.max(0, (internalFlowerScore - scoreTransitionSpeed * tpf));

				internalGameScore += (prevInternalFlowerScore - internalFlowerScore) * internalMultiplicator;

				/* update visualization */
				updateFlowerScoreVisualization();
				updateGameScoreVisualization();
			}
		}

		timeBeforeTransition -= tpf;

		/* multiplicator animation */

		Text multiplicatorText = guiDecoration.getText(multiplicatorTextIndex);
		if (multiplicatorAnimationTime <= multiplicatorAnimationDuration) {
			multiplicatorAnimationTime += tpf;
			float relativeTime = multiplicatorAnimationTime / multiplicatorAnimationDuration;
			float sinus = (float) (Math.sin(relativeTime * Math.PI));
			multiplicatorText.setLocalScale(1 + multiplicatorAnimationScale * sinus);
			float quadsinus = sinus * sinus;
			font.setColor(
					new ColorRGBA(1f, 1f, 1f, (1.0f - quadsinus) + quadsinus * noneMultiplicatorAlphaDuringAnimation));
		} else {
			multiplicatorText.setLocalScale(1);
			font.setColor(new ColorRGBA(1f, 1f, 1f, 1f));
		}

	}

	/* set multiplicator */
	public void setMultiplicator(int multiplicator) {

		cleanUpInternalFlowerScore();
		internalMultiplicator = multiplicator;
		updateMultiplicatorVisualization();
		multiplicatorAnimationTime = 0;
	}

	/* set new flower score */
	public void setFlowerScore(int flowerScore) {

		cleanUpInternalFlowerScore();
		internalFlowerScore = flowerScore;
		updateFlowerScoreVisualization();
		timeBeforeTransition = durationBeforeTransition;

	}

	/* clean up flower score */
	public void cleanUpInternalFlowerScore() {

		internalGameScore += internalFlowerScore * internalMultiplicator;

		updateFlowerScoreVisualization();
		updateGameScoreVisualization();
	}

	/* reset */
	public void reset(int multiplicator, int gameScore) {
		setMultiplicator(multiplicator);
		internalGameScore = gameScore;
		timeBeforeTransition = durationBeforeTransition;
		internalFlowerScore = 0;
		multiplicatorAnimationTime = Float.MAX_VALUE;
		font.setColor(new ColorRGBA(1f, 1f, 1f, 1f));

		updateMultiplicatorVisualization();
		updateFlowerScoreVisualization();
		updateGameScoreVisualization();

		animNode.hideImediatelly();
	}

	/* set visibility */
	public void setVisible(boolean isVisible) {
		animNode.setVisible(isVisible);
	}

	/* attach entity to other node */
	public void attachTo(Node parent) {
		detach();
		this.parent = parent;
		parent.attachChild(animNode.getNode());
	}

	/* detach from node */
	public void detach() {
		if (parent != null) {
			parent.detachChild(animNode.getNode());
			parent = null;
		}
	}

	/* update multiplicator visualization */
	protected void updateMultiplicatorVisualization() {

		String content;

		if ((int) internalMultiplicator < 10) {
			content = "0" + (int) internalMultiplicator;
		} else {
			content = "" + (int) internalMultiplicator;
		}

		guiDecoration.getText(multiplicatorTextIndex).setContent(content);
	}

	/* update flower score visualization */
	protected void updateFlowerScoreVisualization() {

		String content;

		if ((int) Math.round(internalFlowerScore) < 10) {
			content = "0" + (int) Math.round(internalFlowerScore);
		} else {
			content = "" + (int) Math.round(internalFlowerScore);
		}

		guiDecoration.getText(flowerScoreTextIndex).setContent(content);
	}

	/* update game score visualization */
	protected void updateGameScoreVisualization() {

		String content;

		int score = (int) Math.round(internalGameScore);

		if (score < 10) {
			content = "000000" + score;
		} else if (score < 100) {
			content = "00000" + score;
		} else if (score < 1000) {
			content = "0000" + score;
		} else if (score < 1000) {
			content = "000" + score;
		} else if (score < 10000) {
			content = "00" + score;
		} else if (score < 100000) {
			content = "0" + score;
		} else {
			content = "" + score;
		}

		guiDecoration.getText(gameScoreTextIndex).setContent(content);
	}
}
