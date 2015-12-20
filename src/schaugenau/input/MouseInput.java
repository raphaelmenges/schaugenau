package schaugenau.input;

import com.jme3.math.Vector2f;

import schaugenau.app.App;

/**
 * Distributed under the MIT License. (See accompanying file LICENSE or copy at
 * https://github.com/raphaelmenges/schaugenau/blob/master/src/LICENSE)
 * 
 * Mouse input.
 * 
 * @author Raphael Menges
 *
 */

public class MouseInput extends Input {

	/** methods **/

	/* constructor */
	public MouseInput(App app) {
		super(app);
	}

	@Override
	public Vector2f update(float tpf) {
		input = app.getInputManager().getCursorPosition();
		return input.clone();
	}

	@Override
	public boolean isInputWorking() {

		/* mouse works always */
		return true;
	}

	@Override
	public boolean start() {
		return true;
	}

	@Override
	public boolean stop() {
		return true;
	}

	@Override
	public boolean isTracker() {
		return false;
	}

	@Override
	public boolean isStarted() {
		return true;
	}

	@Override
	public boolean isConnected() {
		return true;
	}

}