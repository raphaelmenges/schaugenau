package schaugenau.state.game;

import java.util.LinkedList;

import com.jme3.app.Application;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;

import schaugenau.app.App;
import schaugenau.core.StaticEntity;
import schaugenau.font.Font;
import schaugenau.font.Text;

/**
 * Distributed under the MIT License. (See accompanying file LICENSE or copy at
 * https://github.com/raphaelmenges/schaugenau/blob/master/src/LICENSE)
 * 
 * Superclass of flowers.
 * 
 * @author Raphael Menges
 *
 */

public abstract class Flower extends Pickable {

	/** fields **/
	protected int score;
	protected float scale = 0.725f;
	protected float focus = 0;
	protected float focusSpeed = 1.5f;
	protected StaticEntity focusEntity;
	protected ColorRGBA focusColor = new ColorRGBA(1, 1, 1, 1);

	/** methods **/

	/* constructor */
	public Flower(App app, String type, float butterflyX, float butterflyY, LinkedList<StaticEntity> terrainList,
			boolean debugging) {
		super(app, type, butterflyX, butterflyY, terrainList, debugging);
		this.entity.scale(scale);
		this.shadow.scale(scale);

		/* create focus entity */
		focusEntity = new StaticEntity(app, "Focus", "ShortFaded", true, "png", false, true, false, false);
		focusEntity.attachTo(node);
		focusEntity.move(new Vector3f(0, 0, -1.1f));
	}

	/* override updateFocus method */
	@Override
	protected void updateFocus(float tpf, boolean isFocused) {
		super.updateFocus(tpf, isFocused);
		/* set focus */
		if (isFocused) {
			focus += focusSpeed * tpf;
			if (focus > 1) {
				focus = 1;
			}
		} else {
			focus -= focusSpeed * tpf;
			if (focus < 0) {
				focus = 0;
			}
		}
		focusEntity.setColorParameter(new ColorRGBA(focusColor.r, focusColor.g, focusColor.b, focusColor.a * focus));
	}

	/* returns score */
	public int getScore() {
		return this.score;
	}

	/* returns emitter for effect at picking */
	public abstract MortalEmitter getPickEmitter(Application app);

	/* returns texts which appeares after picking */
	public Text getScoreText(Font font) {
		return new Text(font, schaugenau.font.Text.Alignment.CENTER, "" + this.score);
	}

}
