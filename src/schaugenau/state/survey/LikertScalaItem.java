package schaugenau.state.survey;

import java.util.LinkedList;
import java.util.List;

import com.jme3.math.Vector3f;

import schaugenau.app.App;
import schaugenau.font.TextBox;
import schaugenau.gui.GuiAdapter;
import schaugenau.gui.IconButton;

/**
 * Distributed under the MIT License. (See accompanying file LICENSE or copy at
 * https://github.com/raphaelmenges/schaugenau/blob/master/src/LICENSE)
 * 
 * Likert scala item.
 * 
 * @author Raphael Menges
 *
 */

public class LikertScalaItem extends SurveyItem {

	/** defines **/
	protected float buttonDistance = 3.2f;
	protected int buttonCount = 5;
	protected float buttonScale = 2.2f;
	protected float buttonYOffset = -0.5f;
	protected float messagesXOffset = 8.0f;
	protected float messagesYOffset = -1.75f;
	protected float messagesScale = 0.4f;

	/** fields **/
	protected List<IconButton> buttons;

	protected String leftMessage;
	protected String rightMessage;
	protected TextBox leftMessageBox;
	protected TextBox rightMessageBox;

	/** methods **/

	/* constructor */
	public LikertScalaItem(App app, GuiAdapter guiAdapter, String name, String question, String leftMessage,
			String rightMessage, int id) {
		super(app, guiAdapter, name, question, id);

		/* member */
		this.leftMessage = leftMessage;
		this.rightMessage = rightMessage;

		/* buttons */
		buttons = new LinkedList<>();
		for (int i = 0; i < this.buttonCount; i++) {

			/* icon texture name */
			String iconName;
			switch (i) {
			case 0:
				iconName = "Icon-MinusMinus";
				break;
			case 1:
				iconName = "Icon-Minus";
				break;
			case 2:
				iconName = "Icon-Neutral";
				break;
			case 3:
				iconName = "Icon-Plus";
				break;
			default:
				iconName = "Icon-PlusPlus";
			}

			/* icon button */
			IconButton button = new IconButton(this.app, guiAdapter, new Vector3f(), buttonScale, iconName,
					"LikertButton");
			button.setLocalTranslation(((-this.buttonCount / 2.0f + i) + 0.5f) * this.buttonDistance, buttonYOffset, 0);
			button.attachTo(node);
			buttons.add(button);
		}

		/* left message */
		this.leftMessageBox = new TextBox(this.app.getSecondaryGuiFont(), schaugenau.font.TextBox.Alignment.LEFT,
				leftMessage);
		this.leftMessageBox.setLocalScale(messagesScale);
		this.leftMessageBox.setLocalTranslation(-messagesXOffset, messagesYOffset, 0);
		this.leftMessageBox.attachTo(node);

		/* right message */
		this.rightMessageBox = new TextBox(this.app.getSecondaryGuiFont(), schaugenau.font.TextBox.Alignment.RIGHT,
				rightMessage);
		this.rightMessageBox.setLocalScale(messagesScale);
		this.rightMessageBox.setLocalTranslation(messagesXOffset, messagesYOffset, 0);
		this.rightMessageBox.attachTo(node);

	}

	/* returns hit button, -1 if nothing happened this frame */
	@Override
	public int update(float tpf) {

		int hitIndex = -1;
		for (int i = 0; i < buttons.size(); i++) {
			if (buttons.get(i).update(tpf, false)) {

				/* a button was hit */
				hitIndex = i;
			}
		}

		return hitIndex;
	}
}
