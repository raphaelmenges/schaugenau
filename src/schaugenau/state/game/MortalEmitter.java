package schaugenau.state.game;

import com.jme3.effect.ParticleEmitter;

/**
 * Distributed under the MIT License. (See accompanying file LICENSE or copy at
 * https://github.com/raphaelmenges/schaugenau/blob/master/src/LICENSE)
 * 
 * Emitter with lifetime.
 * 
 * @author Raphael Menges
 *
 */

public class MortalEmitter {

	/** fields **/
	protected ParticleEmitter emitter;
	protected float lifeTime;

	/** methods **/

	/* constructor */
	public MortalEmitter(ParticleEmitter emitter, float lifeTime) {
		this.emitter = emitter;
		this.lifeTime = lifeTime;
	}

	/* getter for emitter */
	public ParticleEmitter getEmitter() {
		return this.emitter;
	}

	/* decreasor of lifetime, returns whether survived or not */
	public boolean decreaseLifeTime(float deltaLifeTime) {
		this.lifeTime -= deltaLifeTime;
		return (this.lifeTime > 0);
	}

}
