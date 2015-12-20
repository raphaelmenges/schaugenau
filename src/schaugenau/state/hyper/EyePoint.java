package schaugenau.state.hyper;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

import schaugenau.app.App;
import schaugenau.core.StaticEntity;
import schaugenau.gui.GuiAdapter;
import schaugenau.gui.GuiElement;
import schaugenau.utilities.LerpValue;

/**
 * Distributed under the MIT License. (See accompanying file LICENSE or copy at
 * https://github.com/raphaelmenges/schaugenau/blob/master/src/LICENSE)
 * 
 * Point to visualize eye.
 * 
 * @author Raphael Menges
 *
 */

public class EyePoint extends GuiElement {

	/** defines **/
	protected final float eyePointZ = 3f;
	protected final float eyePointYOffset = 0.3f;
	protected final Vector2f eyePointMovementMultiplier = new Vector2f(8, 4);
	protected final float initialScale = 1;
	protected final float scaleSpeed = 2.0f;

	/** fields **/
	protected StaticEntity entity;
	protected LerpValue scale;

	/** methods **/

	/* constructor */
	public EyePoint(App app, GuiAdapter guiAdapter, String name) {
		super(app, guiAdapter, name);

		/* initialize members */
		this.scale = new LerpValue(this.initialScale, 0.01f);

		/* create entity */
		this.entity = new StaticEntity(this.app, "LeftEyePoint", "EyePoint", "Unshaded", "LaGa", true, "png", true,
				true, false, false);
		this.entity.setLocalTranslation(0, 0, eyePointZ);
		this.entity.attachTo(this.node);
	}

	/* update */
	public void update(float tpf, boolean positionAvailable, Vector3f eyeWorldPosition) {

		float x, y, z;

		/* translate eye point */
		x = -eyeWorldPosition.x;
		x *= eyePointMovementMultiplier.x;
		y = -eyeWorldPosition.y;
		y *= eyePointMovementMultiplier.y;
		y += eyePointYOffset;
		z = 1.0f - eyeWorldPosition.z;
		this.node.setLocalTranslation(x, y, eyePointZ);

		/* scale eye point */
		z = 1.0f - eyeWorldPosition.z;
		if (!positionAvailable) {

			/* no eye detected */
			scale.update(tpf, scaleSpeed, 0);

		} else {

			/* scale it corresponding to distance */
			scale.update(tpf, scaleSpeed, z);

		}
		this.node.setLocalScale(scale.getValue());
	}

	/* set color parameter of entity */
	public void setColorParameter(ColorRGBA color) {
		this.entity.setColorParameter(color);
	}
}
