package schaugenau.font;

import java.util.LinkedList;

import com.jme3.scene.Spatial;

import schaugenau.core.BaseObject;

/**
 * Distributed under the MIT License. (See accompanying file LICENSE or copy at
 * https://github.com/raphaelmenges/schaugenau/blob/master/src/LICENSE)
 * 
 * Text object.
 * 
 * @author Raphael Menges
 *
 */

public class Text extends BaseObject {

	/** enumerations **/
	public enum Alignment {
		CENTER, LEFT, RIGHT
	}

	/** defines */
	public static final float letterDistanceMulitplicator = 0.775f;

	/** fields **/
	protected LinkedList<Spatial> letterSpatials;
	protected Font font;
	protected String content;
	protected Text.Alignment alignment;

	/** methods **/

	/* constructor */
	public Text(Font font, Text.Alignment alignment, String content) {
		super("Text");
		this.font = font;
		this.content = "";
		this.alignment = alignment;
		letterSpatials = new LinkedList<Spatial>();

		this.setContent(content);
	}

	/* constructor without string */
	public Text(Font font, Alignment alignment) {
		this(font, alignment, "");
	}

	/* set new content */
	public void setContent(String content) {

		/* check whether there is something to change */
		if (this.content.contentEquals(content)) {
			return;
		}

		/* save text */
		this.content = content;

		/* do alignment */
		float start = 0;

		if (alignment == Alignment.LEFT) {
			start = 0;
		} else if (alignment == Alignment.CENTER) {
			start = -1.0f / 2 * calculateLength();
		} else if (alignment == Alignment.RIGHT) {
			start = -1.0f * calculateLength();
		}

		/* get necessary spatials */
		this.letterSpatials.clear();
		this.node.detachAllChildren();
		float offset = 0;
		for (int i = 0; i < content.length(); i++) {
			Spatial spatial = font.getLetter(this.content.charAt(i));
			spatial.move(start + letterDistanceMulitplicator * offset, 0, 0);
			node.attachChild(spatial);
			this.letterSpatials.add(spatial);
			offset += font.getLetterWidth(this.content.charAt(i));
		}
	}

	/* calculate own length by using static method */
	public float calculateLength() {
		return calculateTextLength(this.font, this.content);
	}

	/* calculates text length */
	public static float calculateTextLength(Font font, String text) {
		float length = 0;
		for (Character c : text.toCharArray()) {
			length += font.getLetterWidth(c);
		}
		return length * letterDistanceMulitplicator;
	}
}
