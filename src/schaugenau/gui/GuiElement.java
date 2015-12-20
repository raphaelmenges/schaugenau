package schaugenau.gui;

import com.jme3.scene.Node;

import schaugenau.app.App;
import schaugenau.core.BaseObject;

/**
 * Distributed under the MIT License. (See accompanying file LICENSE or copy at
 * https://github.com/raphaelmenges/schaugenau/blob/master/src/LICENSE)
 * 
 * Super class for gui elements.
 * 
 * @author Raphael Menges
 *
 */

public class GuiElement extends BaseObject {

	/** fields **/
	protected App app;
	protected GuiAdapter guiAdapter;

	/** methods **/

	/* constructor */
	public GuiElement(App app, GuiAdapter guiAdapter, String name) {
		super(name);

		/* save alias of gui adapter */
		this.guiAdapter = guiAdapter;

		/* save alias of app */
		this.app = app;
	}

	/* get node to attach further gui elements */
	public Node getNode() {
		return node;
	}

}
