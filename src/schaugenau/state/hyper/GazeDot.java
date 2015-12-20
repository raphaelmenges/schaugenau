package schaugenau.state.hyper;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;

import schaugenau.app.App;
import schaugenau.core.StaticEntity;
import schaugenau.gui.GuiAdapter;
import schaugenau.gui.GuiElement;

/**
 * Distributed under the MIT License. (See accompanying file LICENSE or copy at
 * https://github.com/raphaelmenges/schaugenau/blob/master/src/LICENSE)
 * 
 * Dot to visualize gaze.
 * 
 * @author Raphael Menges
 *
 */

public class GazeDot extends GuiElement {

	/** defines **/
	protected final ColorRGBA color = new ColorRGBA(1.0f, 1.1f, 1.2f, 1);
	protected final float fadeOutDuration = 1.5f;
	protected final float scaleMultiplier = 1.0f;
	protected final float alphaMultiplier = 0.5f;

	/** fields **/
	protected StaticEntity dot;
	protected float alpha;

	/** methods **/

	/* constructor */
	public GazeDot(App app, GuiAdapter guiAdapter, String name, Vector3f position) {
		super(app, guiAdapter, name);

		/* create dot */
		this.dot = new StaticEntity(app, name, "Plane", "Unshaded", "GazeDot", true, "png", true, true, false, false);
		this.dot.setLocalTranslation(position);
		this.dot.setColorParameter(new ColorRGBA(color.r, color.g, color.b, alphaMultiplier));
		this.dot.setLocalScale(scaleMultiplier);
		this.dot.attachTo(this.node);

		/* setup alpha value */
		this.alpha = 1;
	}

	/* returns true, if finished */
	public boolean update(float tpf) {

		/* calc new alpha */
		this.alpha -= tpf / fadeOutDuration;
		this.alpha = Math.max(0, this.alpha);

		/* set alpha of dot */
		this.dot.setColorParameter(new ColorRGBA(color.r, color.g, color.b, alpha * alphaMultiplier));

		/* set scale of dot */
		this.dot.setLocalScale(alpha * scaleMultiplier);

		/* return true when finished */
		if (this.alpha <= 0) {
			return true;
		} else {
			return false;
		}
	}

}
