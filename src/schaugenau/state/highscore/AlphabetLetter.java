package schaugenau.state.highscore;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

import schaugenau.core.BaseObject;
import schaugenau.font.Font;
import schaugenau.font.Text;
import schaugenau.gui.GuiAdapter;

/**
 * Distributed under the MIT License. (See accompanying file LICENSE or copy at
 * https://github.com/raphaelmenges/schaugenau/blob/master/src/LICENSE)
 * 
 * Letter of alphabet.
 * 
 * @author Raphael Menges
 *
 */

public class AlphabetLetter extends BaseObject {

	/** defines **/
	protected final Vector2f hitBox = new Vector2f(1.05f, 2.7f);
	protected final float offsetMultiplicator = 1.9f;
	protected final float scaleMultiplicator = 1.8f;
	protected final float weightSuretyMultiplicator = 0.65f;
	protected final float suretySpeed = 0.5f;
	protected final float suretyMaximum = 2;

	/** fields **/
	protected Font font;
	protected Text text;
	protected GuiAdapter guiAdapter;
	protected Vector3f initialPosition;
	protected float initialScale;
	protected float offset;
	protected float scale;
	protected float surety;
	protected String letter;

	/** methods **/

	/* constructor */
	public AlphabetLetter(Font font, GuiAdapter guiAdapter, String letter, Vector3f initialPosition,
			float initialScale) {
		super("alphabetLetter-" + letter);

		/* fill members */
		this.font = font;
		this.letter = letter;
		this.text = new Text(this.font, schaugenau.font.Text.Alignment.CENTER, this.letter);
		this.guiAdapter = guiAdapter;
		this.initialPosition = initialPosition;
		this.initialScale = initialScale;

		/* set initial position */
		this.node.scale(this.initialScale);
		this.node.setLocalTranslation(this.initialPosition);

		/* mode letters pivot to top */
		this.text.move(0, -1, 0);

		/* attach letter to node */
		this.text.attachTo(this.node);

		/* reset values */
		this.reset();
	}

	/* returns true if hit by cursor */
	public boolean update(float tpf, float weight, float focusOffset) {

		/* calculate offset and scale */
		offset = weight * offsetMultiplicator * (float) Math.sqrt(Math.abs(focusOffset));
		scale = weight * (scaleMultiplicator
				* ((1 - Math.min(1, (float) Math.pow(Math.abs(focusOffset), 3))) + weightSuretyMultiplicator * surety));
		if (focusOffset < 0) {
			offset = -offset;
		}

		/* set weight */
		this.node.setLocalScale(this.initialScale + scale);
		this.node.setLocalTranslation(this.initialPosition.clone().add(offset, -surety * scale, 0));

		/* get translation and scale in gui space */
		Vector3f nodeTranslation = guiAdapter.getGuiSpaceTranslation(node);
		float nodeScale = guiAdapter.getGuiSpaceScale(node);

		/* letter has pivot on its top */
		nodeTranslation.y -= 0.5f * nodeScale;

		/* check for collision with the gaze */
		boolean hit = false;
		Vector2f cursor = guiAdapter.getCursor().clone();
		if (Math.abs(cursor.x - nodeTranslation.x) <= hitBox.x * 0.5f * nodeScale
				&& Math.abs(cursor.y - nodeTranslation.y) <= hitBox.y * 0.5f * nodeScale) {
			hit = true;
		}

		/* calculate surety for next update */
		if (hit) {
			this.surety += suretySpeed * tpf;
		} else {
			this.surety -= suretySpeed * tpf;
		}
		this.surety = Math.min(suretyMaximum, Math.max(0, this.surety));

		return hit;
	}

	public boolean checkForThreshold(float verticalThreshold) {
		return this.guiAdapter.getGuiSpaceTranslation(this.node).y < verticalThreshold;
	}

	public void reset() {
		this.offset = 0;
		this.scale = 0;
		this.surety = 0;
	}

	public float getGuiSpacedVerticalPosition() {
		return guiAdapter.getGuiSpaceTranslation(node).x;
	}
}
