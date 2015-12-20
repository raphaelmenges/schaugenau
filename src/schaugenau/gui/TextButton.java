package schaugenau.gui;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

import schaugenau.app.App;
import schaugenau.font.Font;
import schaugenau.font.Text;

/**
 * Distributed under the MIT License. (See accompanying file LICENSE or copy at
 * https://github.com/raphaelmenges/schaugenau/blob/master/src/LICENSE)
 * 
 * Specialization of button with text.
 * 
 * @author Raphael Menges
 *
 */

public class TextButton extends Button {

	/** defines **/
	protected final float textXOffset = -0.075f;
	protected final float textYOffset = -0.5f;

	/** fields **/
	protected Text text;

	/** methods **/

	/* constructor */
	public TextButton(App app, GuiAdapter guiAdapter, Vector3f localTranslation, float localScale, String name,
			String content, float textScale, Vector2f hitOffset, Vector2f hitBox) {
		super(app, guiAdapter, localTranslation, localScale, name, hitOffset, hitBox);

		Font font = this.app.getTertiaryGuiFont();

		/* create text */
		this.text = new Text(font, schaugenau.font.Text.Alignment.CENTER, content);
		this.text.setLocalTranslation(textXOffset * textScale, font.getLetterHeight() * textScale * textYOffset, 0);
		this.text.setLocalScale(textScale);

		/* attach the text to the symbol */
		this.text.attachTo(this.symbol);
	}

	/* simplified constructor */
	public TextButton(App app, GuiAdapter guiAdapter, Vector3f localTranslation, float localScale, String name,
			String content, float textScale) {
		this(app, guiAdapter, localTranslation, localScale, name, content, textScale, null, null);
	}

	/* set content of text */
	public void setContent(String content) {
		this.text.setContent(content);
	}

	/* update */
	public boolean update(float tpf, boolean focused) {
		return simpleUpdate(tpf, focused);
	}

}
