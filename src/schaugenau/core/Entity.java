package schaugenau.core;

import com.jme3.asset.TextureKey;
import com.jme3.bounding.BoundingBox;
import com.jme3.bounding.BoundingSphere;
import com.jme3.collision.CollisionResults;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;

import schaugenau.app.App;

/**
 * Distributed under the MIT License. (See accompanying file LICENSE or copy at
 * https://github.com/raphaelmenges/schaugenau/blob/master/src/LICENSE)
 * 
 * Base class of entities.
 * 
 * @author Raphael Menges
 *
 */

public abstract class Entity extends BaseObject {

	/** fields **/
	protected App app;
	protected Spatial spatial;
	protected Spatial collision;
	protected Material mat;
	protected Vector2f texSize;

	/** methods **/

	/* constructor with custom texture path */
	public Entity(App app, String name, String model, String material, String texture, String texturePath,
			boolean isTransparent, String textureFormat, boolean useInGui, boolean clampTexture, boolean isMasked,
			boolean hasLightmap) {
		super(name);

		/* save alias of app */
		this.app = app;

		/* load assets */
		spatial = app.getAssetManager().loadModel(app.pathModels + model + ".j3o");
		mat = new Material(app.getAssetManager(), app.pathMaterials + material + ".j3md");

		this.setTexture(texture, texturePath, textureFormat, clampTexture);
		if (hasLightmap) {
			this.setLightmap(name, texturePath, textureFormat);
		}

		/* is it transparent */
		if (isTransparent) {
			mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
			mat.getAdditionalRenderState().setDepthWrite(false);
			if (!useInGui) {
				spatial.setQueueBucket(Bucket.Transparent);
			}
		} else if (isMasked) {
			mat.setBoolean("DiscardAlpha", true);
			mat.setFloat("AlphaDiscardThreshold", 0.5f);
		}

		spatial.setMaterial(mat);

		/* attach to node */
		node.attachChild(spatial);
	}

	/* standard construtor */
	public Entity(App app, String name, String model, String material, String texture, boolean isTransparent,
			String textureFormat, boolean isGUI, boolean clampTexture, boolean isMasked, boolean hasLightmap) {
		this(app, name, model, material, texture, app.pathTextures, isTransparent, textureFormat, isGUI, clampTexture,
				isMasked, hasLightmap);
	}

	/* collide ray with spatial or if available with collision hull */
	public void collideWith(Ray ray, CollisionResults results) {
		if (collision == null) {
			spatial.collideWith(ray, results);
		} else {
			collision.setLocalTranslation(this.node.getWorldTranslation());
			collision.collideWith(ray, results);
		}
	}

	/* change color parameter of material */
	public void setColorParameter(ColorRGBA color) {
		mat.setColor("Color", color);
	}

	/* get size of texture */
	public Vector2f getSizeOfTexture() {
		return texSize;
	}

	/* set custom bounding box */
	public void setBoundingBox(Vector3f min, Vector3f max) {
		collision = new Geometry("" + name + "-collision", new Box(min, max));
		spatial.setModelBound(new BoundingBox(min, max));

	}

	/* set custom bounding sphere */
	public void setBoundingSphere(int zSamples, int radialSamples, float radius, Vector3f center) {
		collision = new Geometry("" + name + "-collision", new Sphere(zSamples, radialSamples, radius));
		spatial.setModelBound(new BoundingSphere(radius, center));
	}

	/* set texture to material */
	public void setTexture(String texture, String texturePath, String textureFormat, boolean clamp) {

		TextureKey textureKey = new TextureKey(texturePath + texture + "." + textureFormat, false);
		textureKey.setGenerateMips(true);

		Texture tex = app.getAssetManager().loadTexture(textureKey);
		if (clamp) {
			tex.setWrap(WrapMode.Clamp);
		} else {
			tex.setWrap(WrapMode.Repeat);
		}

		this.texSize = new Vector2f(tex.getImage().getWidth(), tex.getImage().getHeight());
		this.mat.setTexture("ColorMap", tex);
	}

	/* set lightmap to material */
	public void setLightmap(String name, String texturePath, String textureFormat) {

		/* Use secondary uv's */
		this.mat.setBoolean("SeparateTexCoord", true);

		/* load and set lightmap */
		TextureKey textureKey = new TextureKey(texturePath + name + "_lightmap." + textureFormat, false);
		textureKey.setGenerateMips(true);
		Texture tex = app.getAssetManager().loadTexture(textureKey);
		tex.setWrap(WrapMode.Clamp);
		this.mat.setTexture("LightMap", tex);

	}

	/* set texture object */
	public void setTexture(Texture texture) {
		this.mat.setTexture("ColorMap", texture);
	}
}