package schaugenau.state.game;

import java.util.LinkedList;

import com.jme3.app.Application;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;

import schaugenau.app.App;
import schaugenau.core.StaticEntity;

/**
 * Distributed under the MIT License. (See accompanying file LICENSE or copy at
 * https://github.com/raphaelmenges/schaugenau/blob/master/src/LICENSE)
 * 
 * Rose flower.
 * 
 * @author Raphael Menges
 *
 */

public class Rose extends Flower {

	/* constructor */
	public Rose(App app, int score, float butterflyX, float butterflyY, LinkedList<StaticEntity> terrainList,
			boolean debugging) {
		super(app, "Rose", butterflyX, butterflyY, terrainList, debugging);
		this.score = score;
		this.focusColor = new ColorRGBA(1.0f, 0.25f, 0.0f, 0.75f);
	}

	/* returns emitter for effect at picking */
	@Override
	public MortalEmitter getPickEmitter(Application app) {
		ParticleEmitter emitter = new ParticleEmitter("Debris", ParticleMesh.Type.Triangle, 7);
		Material emitterMat = new Material(app.getAssetManager(), "Materials/Particle.j3md");
		emitterMat.setTexture("Texture", app.getAssetManager().loadTexture("Textures/RoseParticles.png"));
		emitterMat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
		emitterMat.getAdditionalRenderState().setDepthWrite(false);

		emitter.setMaterial(emitterMat);
		emitter.setStartColor(new ColorRGBA(1, 1, 1, 1));
		emitter.setEndColor(new ColorRGBA(1, 1, 1, 1));
		emitter.setImagesX(2);
		emitter.setImagesY(2);
		emitter.setRotateSpeed(4);
		emitter.setSelectRandomImage(true);
		emitter.move(getWorldTranslation());
		emitter.setStartSize(0.35f);
		emitter.setEndSize(0);
		emitter.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 4, 0));
		emitter.setGravity(0, 6, 0);
		emitter.getParticleInfluencer().setVelocityVariation(.60f);

		MortalEmitter mortalEmitter = new MortalEmitter(emitter, 3);
		return mortalEmitter;
	}
}
