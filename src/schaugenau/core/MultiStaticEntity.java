package schaugenau.core;

import java.util.LinkedList;

import com.jme3.collision.CollisionResults;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

/**
 * Distributed under the MIT License. (See accompanying file LICENSE or copy at
 * https://github.com/raphaelmenges/schaugenau/blob/master/src/LICENSE)
 * 
 * Container for multiple entities of same type.
 * 
 * @author Raphael Menges
 *
 */

public class MultiStaticEntity<T extends StaticEntity> {

	/** fields **/
	protected Node node;
	protected Node parent = null;
	protected LinkedList<T> list;

	/** methods **/

	public MultiStaticEntity(String name) {
		list = new LinkedList<T>();
		node = new Node(name);
	}

	/* add new static entity */
	public void add(T staticEntity) {

		staticEntity.attachTo(node);
		list.add(staticEntity);

	}

	/* get list */
	public LinkedList<T> getList() {
		return list;
	}

	/* attach it to node */
	public void attachTo(Node parent) {
		this.parent = parent;
		parent.attachChild(node);
	}

	/* detach from node */
	public void detach() {
		if (parent != null) {
			parent.detachChild(node);
		}
	}

	/* relative move */
	public void move(Vector3f offset) {
		node.move(offset);
	}

	/* relative rotate */
	public void rotate(Vector3f angle) {
		node.rotate(angle.x, angle.y, angle.z);
	}

	/* relative scale */
	public void scale(float size) {
		node.scale(size);
	}

	/* relative scale */
	public void scale(Vector3f size) {
		node.scale(size.x, size.y, size.z);
	}

	/* set local translation */
	public void setLocalTranslation(Vector3f localTranslation) {
		node.setLocalTranslation(localTranslation);
	}

	/* get world translation */
	public Vector3f getWorldTranslation() {
		return node.getWorldTranslation();
	}

	/* get name */
	public String getName() {
		return node.getName();
	}

	/* collide with ray */
	public void collideWith(Ray ray, CollisionResults results) {
		node.collideWith(ray, results);
	}

	/* change color parameter of materials */
	public void setColorParameter(ColorRGBA color) {
		for (T entity : list) {
			entity.setColorParameter(color);
		}
	}
}
