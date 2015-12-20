package schaugenau.state.survey;

import com.jme3.math.Vector3f;

import schaugenau.app.App;
import schaugenau.gui.GuiAdapter;
import schaugenau.gui.IconButton;

/**
 * Distributed under the MIT License. (See accompanying file LICENSE or copy at
 * https://github.com/raphaelmenges/schaugenau/blob/master/src/LICENSE)
 * 
 * Decision item.
 * 
 * @author Raphael Menges
 *
 */

public class DecisionItem extends SurveyItem {

	/** defines **/
	protected float buttonXOffset = 3.5f;
	protected float buttonYOffset = -1.25f;
	protected float buttonScale = 3;

	/** fields **/
	protected IconButton yesButton;
	protected IconButton noButton;

	/** methods **/

	/* constructor */
	public DecisionItem(App app, GuiAdapter guiAdapter, String name, String question, int id) {
		super(app, guiAdapter, name, question, id);

		/* buttons */
		yesButton = new IconButton(this.app, this.guiAdapter, new Vector3f(buttonXOffset, buttonYOffset, 0),
				buttonScale, "Icon-Ok", "YesButton");
		yesButton.attachTo(this.node);
		noButton = new IconButton(this.app, this.guiAdapter, new Vector3f(-buttonXOffset, buttonYOffset, 0),
				buttonScale, "Icon-Minus", "NoButton");
		noButton.attachTo(this.node);

	}

	/* returns hit button, -1 if nothing happened this frame */
	@Override
	public int update(float tpf) {

		int hitIndex = -1;
		if (yesButton.update(tpf, false)) {

			/* yes button was hit */
			hitIndex = 0;
		}
		if (noButton.update(tpf, false)) {

			/* no button was hit */
			hitIndex = 1;
		}
		return hitIndex;
	}

}
