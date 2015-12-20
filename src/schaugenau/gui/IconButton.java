package schaugenau.gui;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

import schaugenau.app.App;
import schaugenau.core.StaticEntity;

/**
 * Distributed under the MIT License. (See accompanying file LICENSE or copy at
 * https://github.com/raphaelmenges/schaugenau/blob/master/src/LICENSE)
 * 
 * Specialization of button with icon.
 * 
 * @author Raphael Menges
 *
 */

public class IconButton extends Button {

	/** defines **/

	/** fields **/
	StaticEntity icon;

	/** methods **/

	/* constructor */
	public IconButton(App app, GuiAdapter guiAdapter, Vector3f localTranslation, float localScale, String icon,
			String name, Vector2f hitOffset, Vector2f hitBox) {
		super(app, guiAdapter, localTranslation, localScale, name, hitOffset, hitBox);

		/* create icon entity */
		this.icon = new StaticEntity(app, name + "Icon", "EyeButton-Icon", "Unshaded", icon, true, "png", true, false,
				false, false);

		/* attach icon to the symbol node */
		this.icon.attachTo(symbol);
	}

	/* simplified constructor */
	public IconButton(App app, GuiAdapter guiAdapter, Vector3f localTranslation, float localScale, String icon,
			String name) {
		this(app, guiAdapter, localTranslation, localScale, icon, name, null, null);
	}

	/* update */
	public boolean update(float tpf, boolean focused) {
		return simpleUpdate(tpf, focused);
	}

}
