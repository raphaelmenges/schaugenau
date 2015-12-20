package schaugenau.core;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.LoopMode;

import schaugenau.app.App;

/**
 * Distributed under the MIT License. (See accompanying file LICENSE or copy at
 * https://github.com/raphaelmenges/schaugenau/blob/master/src/LICENSE)
 * 
 * Entity with bone based animation.
 * 
 * @author Raphael Menges
 *
 */

public class AnimEntity extends Entity implements AnimEventListener {

	/** fields **/
	protected AnimChannel channel;
	protected AnimControl control;

	/** methods **/

	/* full constructor */
	public AnimEntity(App app, String model, String name, String material, String texture, boolean isTransparent,
			String textureFormat, boolean isGUI, boolean clampTexture, boolean isMasked, boolean hasLightmap) {

		super(app, name, model, material, texture, isTransparent, textureFormat, isGUI, clampTexture, isMasked,
				hasLightmap);

		control = new AnimControl();
		control = spatial.getControl(AnimControl.class);
		control.addListener(this);
		channel = control.createChannel();
	}

	/* simplified constructor */
	public AnimEntity(App app, String name, String material, boolean isTransparent, String textureFormat, boolean isGUI,
			boolean clampTexture, boolean isMasked, boolean hasLightmap) {

		this(app, name, name, material, name, isTransparent, textureFormat, isGUI, clampTexture, isMasked, hasLightmap);
	}

	public void setAnimation(String anim, boolean looping, boolean reset) {
		if (reset) {
			channel.reset(true);
		}
		channel.setAnim(anim);
		if (looping) {
			channel.setLoopMode(LoopMode.Loop);
		} else {
			channel.setLoopMode(LoopMode.DontLoop);
		}
	}

	public float getAnimationTime() {
		return channel.getAnimMaxTime();
	}

	public void setAnimationSpeed(float speed) {
		channel.setSpeed(speed);
	}

	public void restart() {
		channel.setTime(0);
	}

	@Override
	public void onAnimChange(AnimControl arg0, AnimChannel arg1, String arg2) {

	}

	@Override
	public void onAnimCycleDone(AnimControl arg0, AnimChannel arg1, String arg2) {
	}

}
