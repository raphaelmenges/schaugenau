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
 * Choice item.
 * 
 * @author Raphael Menges
 *
 */
public class ChoiceItem extends SurveyItem {

	/** defines **/
	protected float leftRightAnswerXOffset = 6;
	protected float answerYOffset = 0.6f;
	protected float answerButtonYOffset = -2.25f;
	protected float answerTextScale = 0.6f;
	protected float answerButtonScale = 2.5f;

	/** fields **/
	protected List<TextBox> answersBoxs;
	protected List<IconButton> answersButtons;

	/** methods **/

	/* constructor */
	public ChoiceItem(App app, GuiAdapter guiAdapter, String name, String question, List<String> answers, int id) {
		super(app, guiAdapter, name, question, id);

		/* members */
		answersBoxs = new LinkedList<>();
		answersButtons = new LinkedList<>();

		/* answers */
		int i = 0;
		for (String answer : answers) {

			/* text */
			TextBox text = new TextBox(this.app.getPrimaryGuiFont(), schaugenau.font.TextBox.Alignment.CENTER, answer);
			text.scale(answerTextScale);
			text.move(-leftRightAnswerXOffset + leftRightAnswerXOffset * i, answerYOffset, 0);
			text.attachTo(this.node);
			answersBoxs.add(text);

			/* button */
			IconButton button = new IconButton(this.app, this.guiAdapter,
					new Vector3f(-leftRightAnswerXOffset + leftRightAnswerXOffset * i, answerButtonYOffset, 0),
					answerButtonScale, "Icon-Ok", "AnswerButton");
			button.attachTo(this.node);
			answersButtons.add(button);

			/* counter */
			i++;
		}
	}

	/* returns hit button, -1 if nothing happened this frame */
	@Override
	public int update(float tpf) {

		int hitIndex = -1;
		for (int i = 0; i < answersButtons.size(); i++) {
			if (answersButtons.get(i).update(tpf, false)) {

				/* a button was hit */
				hitIndex = i;
			}
		}

		return hitIndex;
	}

}
