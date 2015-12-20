package schaugenau.state.survey;

import schaugenau.app.App;
import schaugenau.core.StaticEntity;
import schaugenau.font.TextBox;
import schaugenau.gui.GuiAdapter;
import schaugenau.gui.GuiElement;

/**
 * Distributed under the MIT License. (See accompanying file LICENSE or copy at
 * https://github.com/raphaelmenges/schaugenau/blob/master/src/LICENSE)
 * 
 * Superclass of survey items.
 * 
 * @author Raphael Menges
 *
 */

public abstract class SurveyItem extends GuiElement {

	/** defines **/
	protected float questionYOffset = 4.85f;
	protected float backgroundZOffset = -10;
	protected float questionBoxScale = 0.75f;

	/** fields **/
	protected String question;
	protected TextBox questionBox;
	protected StaticEntity background;
	protected int id;

	/** methods **/

	/* constructor */
	public SurveyItem(App app, GuiAdapter guiAdapter, String name, String question, int id) {
		super(app, guiAdapter, name);

		/* member */
		this.question = question;

		/* background */
		background = new StaticEntity(this.app, "SurveyItemBackground", "SurveyBackground", "Unshaded", "LaGa", false,
				"png", true, true, false, false);
		background.move(0, 0, backgroundZOffset);
		background.attachTo(this.node);

		/* id */
		this.id = id;

		/* question */
		this.questionBox = new TextBox(this.app.getPrimaryGuiFont(), schaugenau.font.TextBox.Alignment.CENTER,
				question);
		this.questionBox.setLocalScale(questionBoxScale);
		this.questionBox.setLocalTranslation(0, questionYOffset, 0);
		this.questionBox.attachTo(node);
	}

	/* returns hit button, -1 if nothing happened this frame */
	public abstract int update(float tpf);

	/* returns id */
	public int getId() {
		return id;
	}

}
