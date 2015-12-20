package schaugenau.core;

import java.util.HashSet;

import org.apache.log4j.Logger;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

import schaugenau.app.App;
import schaugenau.gui.GuiAdapter;

/**
 * Distributed under the MIT License. (See accompanying file LICENSE or copy at
 * https://github.com/raphaelmenges/schaugenau/blob/master/src/LICENSE)
 * 
 * Superclass for states.
 * 
 * @author Raphael Menges
 *
 */

public abstract class State {

	/** defines **/

	/* camera */
	protected final float fov = 30;
	protected final float near = 0.1f;
	protected final float far = 10000;
	protected final float left = -10;
	protected final float right = 10;
	protected final float expectedAspectRatio = 16f / 9f;
	protected final float guiHorizontalExtentOfExpectedAspectRatio = 20;
	protected final float fadeCurtainZ = 100;
	protected final Vector3f cameraPosition = new Vector3f(0, 0, 9);
	protected final float[] cameraAngles = { 0, (float) Math.PI, 0 };

	/** fields **/
	protected Node rootNode;
	protected GuiAdapter guiAdapter;
	protected App app;

	protected HashSet<SimpleAutoUpdateObject> updateSet;
	protected boolean debugging;
	protected boolean paused;

	protected Logger logger = Logger.getLogger(State.class);

	/** methods **/

	/* constructor */
	public State(App app, String name, boolean debugging) {

		/* create root nodes */
		rootNode = new Node(name + "RootNode");
		guiAdapter = new GuiAdapter(app.getWindowResolution(), expectedAspectRatio,
				guiHorizontalExtentOfExpectedAspectRatio, name + "GuiAdapter");

		/* save alias of app */
		this.app = app;

		/* save whether debugging is enabled */
		this.debugging = debugging;

		/* create set for automatic updated objects */
		this.updateSet = new HashSet<SimpleAutoUpdateObject>();

		/* set unpaused */
		this.paused = false;
	}

	/* update */
	protected boolean update(float tpf, boolean buzzerPressed) {
		guiAdapter.update(app.getRelativeCursor());

		if (!paused) {
			/* update all auto updated objects */
			for (SimpleAutoUpdateObject simpleAutoUpdateObject : updateSet) {
				simpleAutoUpdateObject.update(tpf);
			}
		}

		return true;
	}

	/* attach */
	protected void attach() {
		app.getSceneRoot().attachChild(rootNode);
		guiAdapter.attachTo(app.getGuiRoot());

		/* unpause just in case */
		unpause();

		/* set up camera */
		setCamera(cameraPosition, new Quaternion(cameraAngles));
	}

	/* detach */
	protected void detach() {
		app.getSceneRoot().detachChild(rootNode);
		guiAdapter.detachFrom(app.getGuiRoot());
	}

	/* stop */
	public void stop() {

	}

	/* pause, returns wether successful */
	public boolean pause() {
		if (!this.paused) {
			this.paused = true;
			return true;
		}
		return false;
	}

	/* unpause */
	public boolean unpause() {
		if (this.paused) {
			this.paused = false;
			return true;
		}
		return false;
	}

	/* set camera */
	public void setCamera(Vector3f position, Quaternion rotation) {

		app.getCamera().setLocation(position);
		app.getCamera().setRotation(rotation);
		app.getCamera().setParallelProjection(false);
		app.getCamera().setFrustumPerspective(fov, app.getWindowResolution().x / app.getWindowResolution().y, near,
				far);
	}

	/* add to update set */
	public void addToUpdateSet(SimpleAutoUpdateObject autoUpdateObject) {
		updateSet.add(autoUpdateObject);
	}
}
