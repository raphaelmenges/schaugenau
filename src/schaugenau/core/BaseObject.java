package schaugenau.core;

import com.jme3.math.Matrix3f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

/**
 * Distributed under the MIT License. (See accompanying file LICENSE or copy at
 * https://github.com/raphaelmenges/schaugenau/blob/master/src/LICENSE)
 * 
 * Base class of all objects.
 * 
 * @author Raphael Menges
 *
 */

public class BaseObject {

	/** fields **/
	protected Node node;
	protected Node parent = null;
	protected String name;

	/** methods **/

	/* constructor */
	public BaseObject(String name) {
		node = new Node(name);
		this.name = name;
	}

	/* attach entity to other node */
	public void attachTo(Node parent) {
		detach();
		this.parent = parent;
		parent.attachChild(node);
	}

	/* detach from node */
	public void detach() {
		if (parent != null) {
			parent.detachChild(node);
			parent = null;
		}
	}

	/* relative move */
	public void move(Vector3f offset) {
		node.move(offset);
	}

	/* relative move */
	public void move(float x, float y, float z) {
		node.move(x, y, z);
	}

	/* relative rotate */
	public void rotate(Vector3f rotation) {
		node.rotate(rotation.x, rotation.y, rotation.z);
	}

	/* relative scale */
	public void scale(float scale) {
		node.scale(scale);
	}

	/* relative scale */
	public void scale(Vector3f scale) {
		node.scale(scale.x, scale.y, scale.z);
	}

	/* relative scale */
	public void scale(float x, float y, float z) {
		node.scale(x, y, z);
	}

	/* set local translation */
	public void setLocalTranslation(Vector3f localTranslation) {
		node.setLocalTranslation(localTranslation);
	}

	/* set local translation */
	public void setLocalTranslation(float x, float y, float z) {
		node.setLocalTranslation(x, y, z);
	}

	/* set local rotation */
	public void setLocalRotation(Vector3f localRotation) {
		node.setLocalRotation(new Matrix3f());
		this.rotate(localRotation);
	}

	/* set local scale */
	public void setLocalScale(float localScale) {
		node.setLocalScale(localScale);
	}

	/* set local scale */
	public void setLocalScale(float x, float y, float z) {
		node.setLocalScale(x, y, z);
	}

	/* set local scale */
	public void setLocalScale(Vector3f localScale) {
		node.setLocalScale(localScale);
	}

	/* get world translation */
	public Vector3f getWorldTranslation() {
		return node.getWorldTranslation();
	}

	/* get local translation */
	public Vector3f getLocalTranslation() {
		return node.getLocalTranslation();
	}

	/* get local scale */
	public float getCombinedLocalScale() {
		return (node.getLocalScale().x + node.getLocalScale().y + node.getLocalScale().z) / 3;
	}

	/* get local axis scales */
	public Vector3f getLocalScale() {
		return node.getLocalScale();
	}

	/* get name */
	public String getName() {
		return node.getName();
	}
}
