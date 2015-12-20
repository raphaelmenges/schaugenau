package schaugenau.input;

import java.util.Random;

import com.jme3.math.Vector2f;

import schaugenau.app.App;

/**
 * Distributed under the MIT License. (See accompanying file LICENSE or copy at
 * https://github.com/raphaelmenges/schaugenau/blob/master/src/LICENSE)
 * 
 * Input simulating eye tracking by adding noise to mouse position.
 * 
 * @author Raphael Menges
 *
 */

public class DistortedMouseInput extends MouseInput {

	/** defines **/
	protected final Vector2f pixelOffset = new Vector2f(0, -40);
	protected final float pixelDeviation = 20;

	/** members **/
	protected Random random;

	/** methods **/

	/* constructor */
	public DistortedMouseInput(App app) {
		super(app);
		this.random = new Random();
	}

	@Override
	public Vector2f update(float tpf) {
		Vector2f input = super.update(tpf);
		Vector2f offset = new Vector2f();
		offset.x = pixelOffset.x + (float) random.nextGaussian() * pixelDeviation;
		offset.y = pixelOffset.y + (float) random.nextGaussian() * pixelDeviation;
		return input.add(offset);
	}

}
