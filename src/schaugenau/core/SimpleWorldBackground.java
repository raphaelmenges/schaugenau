package schaugenau.core;

import java.util.LinkedList;
import java.util.List;

import com.jme3.math.Vector3f;

import schaugenau.app.App;

/**
 * Distributed under the MIT License. (See accompanying file LICENSE or copy at
 * https://github.com/raphaelmenges/schaugenau/blob/master/src/LICENSE)
 * 
 * Background.
 * 
 * @author Raphael Menges
 *
 */

public class SimpleWorldBackground extends BaseObject {

	/** defines **/
	protected final Vector3f planePosition = new Vector3f(0, 0, -20);
	protected final Vector3f planeScale = new Vector3f(35, 25, 1);
	protected final float particleTranslationSpeed = -0.2f;
	protected final float particleScaleMin = 0.75f;
	protected final float particleScaleMax = 1.5f;
	protected final int particleCount = 200;
	protected final float particleRotationSpeed = 0.4f;
	protected final float particleSpawnXExtent = 15;
	protected final float particleSpawnY = 6f;
	protected final float particleKillY = -6f;
	protected final float particleSpawnZMin = -10;
	protected final float particleSpawnZMax = -5;

	/** fields **/
	protected StaticEntity plane;
	protected float time;
	protected List<BackgroundParticle> particles;
	protected boolean useParticles;

	/** methods **/

	/* constructor */
	public SimpleWorldBackground(App app, boolean useParticles) {
		super("SimpleWorldBackground");

		/* create plane */
		this.plane = new StaticEntity(app, "SimpleWorldBackground", "Plane", "Unshaded", "Background", true, "png",
				false, true, false, false);
		this.plane.setLocalScale(planeScale);
		this.plane.setLocalTranslation(planePosition);
		this.plane.attachTo(node);

		/* generate particles */
		this.useParticles = useParticles;
		if (this.useParticles) {
			this.particles = new LinkedList<>();

			for (int j = 0; j < 4; j++) {
				for (int i = 0; i < particleCount / 4; i++) {

					/* random flower */
					String texture;
					switch (j) {
					case 0:
						texture = "SoftDandelionParticle";
						break;
					case 1:
						texture = "SoftTulipParticle";
						break;
					case 2:
						texture = "SoftLilyParticle";
						break;
					default:
						texture = "SoftRoseParticle";
						break;
					}

					/* create particle */
					BackgroundParticle particle = new BackgroundParticle(app, texture);

					/* ranomize properties */
					particle.setLocalTranslation(
							schaugenau.utilities.Helper.randomInIntervall(-particleSpawnXExtent, particleSpawnXExtent),
							schaugenau.utilities.Helper.randomInIntervall(particleKillY, particleSpawnY),
							schaugenau.utilities.Helper.randomInIntervall(particleSpawnZMin, particleSpawnZMax));
					particle.setLocalScale(
							schaugenau.utilities.Helper.randomInIntervall(particleScaleMin, particleScaleMax));
					particle.setLocalRotation(new Vector3f(0, 0,
							schaugenau.utilities.Helper.randomInIntervall(-(float) Math.PI, (float) Math.PI)));
					particle.setTranslationSpeed(new Vector3f(0, particleTranslationSpeed, 0));
					particle.setRotationSpeed(schaugenau.utilities.Helper.randomInIntervall(-particleRotationSpeed,
							particleRotationSpeed));

					/* attach particle */
					particle.attachTo(node);
					this.particles.add(particle);
				}
			}
		}

		this.time = 0;
	}

	/* update */
	public void update(float tpf) {

		/* update time */
		time += tpf;

		/* animate plane */
		Vector3f offset = new Vector3f((float) (0.75 * Math.sin(0.4 * time)), (float) (0.3 * Math.cos(0.2 * time)), 0);
		this.plane.setLocalTranslation(planePosition.clone().add(offset));

		/* animate particles */
		if (useParticles) {
			for (BackgroundParticle particle : particles) {

				/* update them */
				particle.update(tpf);

				/* reset particle */
				if (particle.getLocalTranslation().y <= particleKillY) {
					particle.setLocalTranslation(
							schaugenau.utilities.Helper.randomInIntervall(-particleSpawnXExtent, particleSpawnXExtent),
							particleSpawnY,
							schaugenau.utilities.Helper.randomInIntervall(particleSpawnZMin, particleSpawnZMax));
				}
			}
		}
	}
}