package schaugenau.core;

import schaugenau.app.App;

/**
 * Distributed under the MIT License. (See accompanying file LICENSE or copy at
 * https://github.com/raphaelmenges/schaugenau/blob/master/src/LICENSE)
 * 
 * Entity without animation.
 * 
 * @author Raphael Menges
 *
 */

public class StaticEntity extends Entity {

	/** methods **/

	/* standard constructor */
	public StaticEntity(App app, String name, String model, String material, String texture, boolean isTransparent,
			String textureFormat, boolean isGUI, boolean clampTexture, boolean isMasked, boolean hasLightmap) {
		super(app, name, model, material, texture, isTransparent, textureFormat, isGUI, clampTexture, isMasked,
				hasLightmap);
	}

	/* constructor with custom texture path */
	public StaticEntity(App app, String name, String model, String material, String texture, String texturePath,
			boolean isTransparent, String textureFormat, boolean isGUI, boolean clampTexture, boolean isMasked,
			boolean hasLightmap) {

		super(app, name, model, material, texture, texturePath, isTransparent, textureFormat, isGUI, clampTexture,
				isMasked, hasLightmap);
	}

	/* simplified constructor */
	public StaticEntity(App app, String name, String material, boolean isTransparent, String textureFormat,
			boolean isGUI, boolean clampTexture, boolean isMasked, boolean hasLightmap) {

		this(app, name, name, material, name, isTransparent, textureFormat, isGUI, clampTexture, isMasked, hasLightmap);
	}

}
