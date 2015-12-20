package schaugenau.state.game;

import java.util.LinkedList;

import com.jme3.collision.CollisionResults;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;

import schaugenau.app.App;
import schaugenau.core.StaticEntity;

/**
 * Distributed under the MIT License. (See accompanying file LICENSE or copy at
 * https://github.com/raphaelmenges/schaugenau/blob/master/src/LICENSE)
 * 
 * Stuff that can be picked by player, like flower or spiderweb.
 * 
 * @author Raphael Menges
 *
 */

public abstract class Pickable {

	/** defines **/
	protected final float startZ = -200;
	protected final float collisionSphereRadius = 0.85f;
	protected final ColorRGBA collisionHullColor = new ColorRGBA(0, 1, 0, 1);
	protected final int collisionZSamples = 8;
	protected final int collisionRadialSamples = 6;
	protected final float speed = 5;
	protected final float shadowOffset = 2.0f;
	protected final Vector3f standardShadowScale = new Vector3f(2, 0, 2);
	protected final float shadowAlpha = 0.5f;

	/** fields **/
	protected Vector3f shadowScale;

	/* node */
	protected Node node;
	protected Node parent = null;

	/* static entities */
	protected StaticEntity entity;
	protected StaticEntity shadow;

	/* starting coordinates */
	protected Vector3f start;

	/* terrainList for shadows */
	protected LinkedList<StaticEntity> terrainList;

	/* focus */
	protected boolean isFocused = false;

	/* debugging */

	/** methods **/

	/* constructor */
	public Pickable(App app, String name, String model, String texture, String texturePath, float butterflyX,
			float butterflyY, LinkedList<StaticEntity> terrainList, boolean debugging) {

		/* set standard scale of shadow */
		shadowScale = standardShadowScale;

		/* set terrain list */
		this.terrainList = terrainList;

		/* local root node */
		node = new Node(name);

		/* create pickable */
		if (texturePath == null) {
			entity = new StaticEntity(app, name, model, "ShortFaded", texture, true, "png", false, false, false, false);
		} else {
			entity = new StaticEntity(app, name, model, "ShortFaded", texture, texturePath, true, "png", false, false,
					false, false);
		}

		/* set custom collision hull */
		entity.setBoundingSphere(collisionZSamples, collisionRadialSamples, collisionSphereRadius, new Vector3f());

		/* debugging */
		if (debugging) {
			Sphere sphere = new Sphere(collisionZSamples, collisionRadialSamples, collisionSphereRadius);
			sphere.setMode(Mesh.Mode.Lines);
			Material material = new Material(app.getAssetManager(), app.pathMaterials + "Unshaded.j3md");
			material.setColor("Color", collisionHullColor);
			Geometry collisionHull = new Geometry("pickableCollisionHull", sphere);
			collisionHull.setMaterial(material);
			this.node.attachChild(collisionHull);
		}

		/* start position */
		start = new Vector3f();
		start.x = schaugenau.utilities.Helper.randomInIntervall(-butterflyX, butterflyX);
		start.y = schaugenau.utilities.Helper.randomInIntervall(-butterflyY, butterflyY);
		start.z = startZ;

		/* attach pickable to node */
		entity.attachTo(node);

		/* create shadow */
		shadow = new StaticEntity(app, "black", "Shadow", "ShortFaded", "Shadow", true, "png", false, true, false,
				false);
		shadow.setColorParameter(new ColorRGBA(1, 1, 1, shadowAlpha));

		/* attach shadow to node */
		shadow.scale(shadowScale);
		shadow.attachTo(node);

		/* move to start */
		node.move(start);
	}

	/* simplified constructor */
	public Pickable(App app, String type, float butterflyX, float butterflyY, LinkedList<StaticEntity> terrainList,
			boolean debugging) {
		this(app, type, type, type, butterflyX, butterflyY, terrainList, debugging);
	}

	/* normal constructor */
	public Pickable(App app, String name, String model, String texture, float butterflyX, float butterflyY,
			LinkedList<StaticEntity> terrainList, boolean debugging) {
		this(app, name, model, texture, null, butterflyX, butterflyY, terrainList, debugging);
	}

	/* update it */
	public void update(float tpf, boolean isFocused) {

		/* move towards the player */
		node.move(new Vector3f(0, 0, tpf * speed));

		/* calc shadow position */
		CollisionResults results = new CollisionResults();
		Ray ray = new Ray(node.getWorldTranslation(), new Vector3f(0, -1, -0.5f));
		for (StaticEntity terrain : terrainList) {
			terrain.collideWith(ray, results);
			if (results.size() > 0) {
				shadow.setLocalTranslation(
						new Vector3f(0, results.getClosestCollision().getContactPoint().y + shadowOffset, 0));
				break;
			}
		}
		updateFocus(tpf, isFocused);
	}

	/* update focus */
	protected void updateFocus(float tpf, boolean isFocused) {
		/* do nothing here in base class */
	}

	/* attach to scene graph */
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

	/* get world translation */
	public Vector3f getWorldTranslation() {
		return node.getWorldTranslation();
	}

	/* collide with ray */
	public void collideWith(Ray ray, CollisionResults results) {
		this.entity.collideWith(ray, results);
	}

	/* set xy coords */
	public void setXY(Vector2f coord) {
		node.setLocalTranslation(new Vector3f(coord.x, coord.y, node.getWorldTranslation().z));
	}
}
