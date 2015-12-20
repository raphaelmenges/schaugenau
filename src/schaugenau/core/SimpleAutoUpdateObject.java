package schaugenau.core;

/**
 * Distributed under the MIT License. (See accompanying file LICENSE or copy at
 * https://github.com/raphaelmenges/schaugenau/blob/master/src/LICENSE)
 * 
 * Superclass of all objects which registers themselves for updates.
 * 
 * @author Raphael Menges
 *
 */

public abstract class SimpleAutoUpdateObject {

	public SimpleAutoUpdateObject(State state) {
		state.addToUpdateSet(this);
	}

	public abstract void update(float tpf);
}
