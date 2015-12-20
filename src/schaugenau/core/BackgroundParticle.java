package schaugenau.core;

import com.jme3.math.Vector3f;

import schaugenau.app.App;

/**
 * Distributed under the MIT License. (See accompanying file LICENSE or copy at
 * https://github.com/raphaelmenges/schaugenau/blob/master/src/LICENSE)
 * 
 * Simple particle.
 * 
 * @author Raphael Menges
 *
 */

public class BackgroundParticle extends BaseObject {

	/** fields **/
	protected Vector3f translationSpeed;
	protected float rotationSpeed;
	protected StaticEntity entity;

	/** methods **/

	public BackgroundParticle(App app, String texture) {
		super("Particle");

		this.entity = new StaticEntity(app, "Particle", "Plane", "Unshaded", texture, true, "png", false, true, false,
				false);
		this.entity.attachTo(this.node);
	}

	public void update(float tpf) {

		/* move and rotate base object, not entity */
		this.move(translationSpeed.clone().mult(tpf));
		this.rotate(new Vector3f(0, 0, rotationSpeed * tpf));
	}

	public Vector3f getTranslationSpeed() {
		return translationSpeed;
	}

	public void setTranslationSpeed(Vector3f translationSpeed) {
		this.translationSpeed = translationSpeed;
	}

	public float getRotationSpeed() {
		return rotationSpeed;
	}

	public void setRotationSpeed(float rotationSpeed) {
		this.rotationSpeed = rotationSpeed;
	}

}
