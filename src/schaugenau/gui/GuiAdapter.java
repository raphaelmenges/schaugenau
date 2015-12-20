package schaugenau.gui;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

/**
 * Distributed under the MIT License. (See accompanying file LICENSE or copy at
 * https://github.com/raphaelmenges/schaugenau/blob/master/src/LICENSE)
 * 
 * Adapter to handle different coordinate systems.
 * 
 * @author Raphael Menges
 *
 */

public class GuiAdapter {

	/** defines **/
	protected final float expectedAspectRatio;

	/** fields **/
	protected float left;
	protected float right;
	protected float top;
	protected float bottom;
	protected float aspectRatio;
	protected float width;
	protected float height;
	protected float expectedVerticalExtent;
	protected Vector2f resolution;
	protected Vector2f cursor = new Vector2f(0, 0);;
	protected Node node;

	/** methods **/

	/* constructor */
	public GuiAdapter(Vector2f windowResolution, float expectedAspectRatio, float horizontalExtentOfExpectedAspectRatio,
			String name) {

		/* simple things */
		node = new Node(name + "Root");
		aspectRatio = windowResolution.x / windowResolution.y;
		resolution = windowResolution;
		this.expectedAspectRatio = expectedAspectRatio;

		/* decide whether to expand height or width */
		if (aspectRatio > this.expectedAspectRatio) {
			/* calculate height by using given aspect and extent */
			height = horizontalExtentOfExpectedAspectRatio / this.expectedAspectRatio;

			/* expand width */
			width = height * aspectRatio;

		} else {
			/* use given extent as width */
			width = horizontalExtentOfExpectedAspectRatio;

			/* expand height */
			height = horizontalExtentOfExpectedAspectRatio / aspectRatio;
		}

		/* calculate extents of space */
		left = -width / 2f;
		right = width / 2f;
		top = (height) / 2f;
		bottom = -(height) / 2f;

		/* translate space */
		node.setLocalTranslation(new Vector3f((-left * resolution.x) / width, (-bottom * resolution.y) / height, 0));

		/* scale space */
		node.setLocalScale(resolution.x / width);

		/* expected vertical extend */
		this.expectedVerticalExtent = horizontalExtentOfExpectedAspectRatio / expectedAspectRatio;
	}

	/* update the adapter for a updated cursor position */
	public void update(Vector2f relativeCursor) {
		cursor.x = relativeCursor.x * (right - left) + left;
		cursor.y = relativeCursor.y * (top - bottom) + bottom;
	}

	/* calculate translation in adapter space */
	public Vector3f getGuiSpaceTranslation(Node node) {
		Vector3f pixelSpaceTranslation = node.getWorldTranslation();
		return new Vector3f(pixelSpaceTranslation.x / resolution.x * width + left,
				pixelSpaceTranslation.y / resolution.y * height + bottom, pixelSpaceTranslation.z);
	}

	/* calculate scale in adapter space (uses only x value of world scale) */
	public float getGuiSpaceScale(Node node) {
		float scale = node.getWorldScale().x;
		return scale / resolution.x * width;
	}

	/* calculate world coordinate of gui spaced coordinate */
	public Vector3f convertGuiToWorldSpace(Vector3f guiSpacedVector) {

		return new Vector3f((guiSpacedVector.x - left) / width * resolution.x,
				(guiSpacedVector.y - bottom) / height * resolution.y, guiSpacedVector.z);
	}

	/* attach node */
	public void attachTo(Node parent) {
		parent.attachChild(node);
	}

	/* detach node */
	public void detachFrom(Node parent) {
		parent.detachChild(node);
	}

	/* attach child */
	public void attachChild(Node child) {
		this.node.attachChild(child);
	}

	/* detach child */
	public void detachChild(Node child) {
		this.node.detachChild(child);
	}

	/* detach all children */
	public void detachAllChildren() {
		this.node.detachAllChildren();
	}

	/* get cursor */
	public Vector2f getCursor() {
		return cursor;
	}

	/* get left */
	public float getLeft() {
		return left;
	}

	/* get right */
	public float getRight() {
		return right;
	}

	/* get top */
	public float getTop() {
		return top;
	}

	/* get bottom */
	public float getBottom() {
		return bottom;
	}

	/* get aspect */
	public float getAspect() {
		return aspectRatio;
	}

	/* get width */
	public float getWidth() {
		return width;
	}

	/* get height */
	public float getHeight() {
		return height;
	}

	/* get resolution */
	public Vector2f getResolution() {
		return resolution;
	}

	/* get expectec vertical extend */
	public float getExpectedVerticalExtent() {
		return expectedVerticalExtent;
	}

	/* get node */
	public Node getNode() {
		return node;
	}
}
