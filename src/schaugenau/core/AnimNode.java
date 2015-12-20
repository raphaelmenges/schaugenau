package schaugenau.core;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 * Distributed under the MIT License. (See accompanying file LICENSE or copy at
 * https://github.com/raphaelmenges/schaugenau/blob/master/src/LICENSE)
 * 
 * Animated node. Can be shown or hidden.
 * 
 * @author Raphael Menges
 *
 */

public class AnimNode extends SimpleAutoUpdateObject {

	/** defines **/

	/** fields **/
	protected Node node;
	protected Vector3f showPosition;
	protected Vector3f hidePosition;
	protected float visibility;
	protected float animationDuration;
	protected boolean isVisible;

	/** methods **/

	/* constructor */
	public AnimNode(State state, String name, Vector3f showPosition, Vector3f hidePosition, float animationDuration,
			boolean initializeVisible) {
		super(state);

		/* create node */
		node = new Node(name);

		/* fill members */
		this.showPosition = showPosition;
		this.hidePosition = hidePosition;
		this.animationDuration = animationDuration;

		/* decide whether to initialize it shown or hidden */
		if (initializeVisible) {
			showImediatelly();
		} else {
			hideImediatelly();
		}
	}

	/* update */
	@Override
	public void update(float tpf) {

		/* update visibility */
		if (isVisible) {
			visibility = Math.min(1, (visibility + tpf / animationDuration));
		} else {
			visibility = Math.max(0, (visibility - tpf / animationDuration));
		}

		/* update position of node */
		node.setLocalTranslation(new Vector3f().interpolate(hidePosition, showPosition, visibility));
	}

	/* set soft visibility */
	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	/* set hard visibility */
	public void showImediatelly() {
		visibility = 1;
		isVisible = true;
		node.setLocalTranslation(new Vector3f(showPosition));
	}

	/* set hard visibility */
	public void hideImediatelly() {
		visibility = 0;
		isVisible = false;
		node.setLocalTranslation(new Vector3f(hidePosition));
	}

	public Node getNode() {
		return node;
	}

	public void attachChild(Spatial child) {
		this.node.attachChild(child);
	}

	public boolean isHiddenRightNow() {
		return visibility == 0;
	}
}
