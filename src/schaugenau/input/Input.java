package schaugenau.input;

import com.jme3.math.Vector2f;

import schaugenau.app.App;

/**
 * Distributed under the MIT License. (See accompanying file LICENSE or copy at
 * https://github.com/raphaelmenges/schaugenau/blob/master/src/LICENSE)
 * 
 * Superclass of input.
 * 
 * @author Raphael Menges
 *
 */

public abstract class Input {

	/** fields **/

	/* alias of app */
	protected App app;

	protected Vector2f input;

	/** methods **/

	/* constructor */
	public Input(App app) {

		/* save alias of app */
		this.app = app;

		/* construct initial input */
		this.input = new Vector2f();
	}

	/* update input and return cursor position in pixel coordinates. */
	public abstract Vector2f update(float tpf);

	/* returns, whether input is ok */
	public abstract boolean isInputWorking();

	/* returns, if input is started */
	public abstract boolean isStarted();

	/* start it and return, whether was not running before */
	public abstract boolean start();

	/* start it and return, whether was running before */
	public abstract boolean stop();

	/* check if input is a tracker */
	public abstract boolean isTracker();

	/* check connection */
	public abstract boolean isConnected();
}
