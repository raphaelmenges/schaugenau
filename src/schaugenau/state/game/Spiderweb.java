package schaugenau.state.game;

import java.util.LinkedList;

import com.jme3.math.Vector3f;

import schaugenau.app.App;
import schaugenau.core.StaticEntity;

/**
 * Distributed under the MIT License. (See accompanying file LICENSE or copy at
 * https://github.com/raphaelmenges/schaugenau/blob/master/src/LICENSE)
 * 
 * Spiderweb.
 * 
 * @author Raphael Menges
 *
 */

public class Spiderweb extends Pickable {

	/* constructor */
	public Spiderweb(App app, float butterflyX, float butterflyY, LinkedList<StaticEntity> terrainList,
			boolean debugging) {
		super(app, "Spiderweb", butterflyX, butterflyY, terrainList, debugging);
		this.shadowScale = new Vector3f(2, 0, 0.5f);
		this.entity.rotate(new Vector3f(0, 0, schaugenau.utilities.Helper.randomInIntervall(0, 360)));
	}
}
