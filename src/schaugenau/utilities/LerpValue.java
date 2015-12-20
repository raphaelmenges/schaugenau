package schaugenau.utilities;

/**
 * Distributed under the MIT License. (See accompanying file LICENSE or copy at
 * https://github.com/raphaelmenges/schaugenau/blob/master/src/LICENSE)
 * 
 * Linear interpolation.
 * 
 * @author Raphael Menges
 *
 */

public class LerpValue {

	/** defines **/

	/** fields **/
	protected float value;
	protected float epsilon;

	/** methods **/

	/* constructor */
	public LerpValue(float value, float epsilon) {
		this.value = value;
		this.epsilon = epsilon;
	}

	/* other constructor */
	public LerpValue(float epsilon) {
		this.value = 0;
		this.epsilon = epsilon;
	}

	/* update the value */
	public void update(float tpf, float upSpeed, float downSpeed, float target) {
		if (Math.abs(this.value - target) < epsilon) {
			this.value = target;
		} else if (this.value < target) {
			this.value += tpf * upSpeed;
			if (this.value > target) {
				this.value = target;
			}
		} else {
			this.value -= tpf * downSpeed;
			if (this.value < target) {
				this.value = target;
			}
		}
	}

	/* update the value with equal speed for both directions */
	public void update(float tpf, float speed, float target) {
		this.update(tpf, speed, speed, target);
	}

	/* set value immediately */
	public void setValue(float value) {
		this.value = value;
	}

	/* get current value */
	public float getValue() {
		return this.value;
	}

	/* get epsilon */
	public float getEpsilon() {
		return this.epsilon;
	}

}
