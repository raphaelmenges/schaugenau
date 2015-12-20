package schaugenau.font;

import java.util.LinkedList;

import schaugenau.core.BaseObject;

/**
 * Distributed under the MIT License. (See accompanying file LICENSE or copy at
 * https://github.com/raphaelmenges/schaugenau/blob/master/src/LICENSE)
 * 
 * Box with text. Has its origin in the upper left corner of the box.
 * 
 * @author Raphael Menges
 *
 */

public class TextBox extends BaseObject {

	/** enumerations **/
	public enum Alignment {
		CENTER, LEFT, RIGHT, JUSTIFY
	}

	/** defines **/
	protected final float lineDistance = 1.2f; // independend from letter
												// height...

	/** fields **/
	protected String content;
	protected Font font;
	protected TextBox.Alignment alignment;
	protected float maxLineLength;

	/* one list per line */
	protected LinkedList<LinkedList<String>> words;
	protected LinkedList<Text> texts;

	/** methods **/

	/* constructor without content initialization */
	public TextBox(Font font, TextBox.Alignment alignment) {
		this(font, alignment, "");
	}

	/* constructor */
	public TextBox(Font font, TextBox.Alignment alignment, String content) {
		super("textBox");
		this.font = font;
		this.content = "";
		this.alignment = alignment;
		words = new LinkedList<LinkedList<String>>();
		texts = new LinkedList<Text>();

		this.setContent(content);
	}

	/* set new content */
	public void setContent(String content) {

		/* some fix for strings from database */
		content = content.replaceAll("\\\\n", "\n");

		/* check whether there is something to change */
		if (this.content.contentEquals(content)) {
			return;
		}

		/* save content */
		this.content = content;

		/* clear words */
		words.clear();

		/* find out longest line and save it as member */
		maxLineLength = -1;

		/* split into words */
		for (String line : this.content.split("\n")) {

			/* get length of this row */
			float lineLength = Text.calculateTextLength(this.font, line);
			if (lineLength > maxLineLength) {
				maxLineLength = lineLength;
			}

			/* create new list entry for line */
			words.add(new LinkedList<String>());

			/* fill words of line into list */
			for (String word : line.split(" ")) {
				words.getLast().add(word);
			}
		}

		/* create visual text objects */
		this.texts.clear();
		this.node.detachAllChildren();
		float verticalPosition = -lineDistance;
		float horizontalPosition = 0;
		float wordDistance = 0;

		/* iterate over lines */
		for (LinkedList<String> line : words) {

			/* ALIGNMENT */

			if (this.alignment == TextBox.Alignment.JUSTIFY) {
				/* justify alignment */
				horizontalPosition = 0;

				/* calculate adaptive word distance */
				float lengthOfWords = 0;
				for (String word : line) {
					lengthOfWords += Text.calculateTextLength(this.font, word);
				}
				float space = maxLineLength - lengthOfWords;
				wordDistance = space / (line.size() - 1);

			} else {

				/* left, right or center alignment */
				wordDistance = this.font.getLetterWidth(' ');

				/* left alignment */
				if (this.alignment == TextBox.Alignment.LEFT) {
					horizontalPosition = 0;
				}
				/* right or center alignment */
				else {
					/* calculate length of current line */
					float lengthOfLine = 0;
					for (String word : line) {
						lengthOfLine += Text.calculateTextLength(this.font, word);
					}
					lengthOfLine += this.font.getLetterWidth(' ') * (line.size() - 1);

					/* use length of line to determine horizontal position */
					if (this.alignment == TextBox.Alignment.RIGHT) {
						/* right alignment */
						horizontalPosition = -lengthOfLine;
					} else {
						/* center alignment */
						horizontalPosition = (maxLineLength - lengthOfLine) / 2.0f - (maxLineLength / 2.0f);
					}
				}
			}

			/* CREATION OF TEXT */

			/* iterate over words and create geometry */
			for (String word : line) {
				Text wordGeometry = new Text(this.font, Text.Alignment.LEFT, word);
				wordGeometry.move(horizontalPosition, verticalPosition, 0);
				wordGeometry.attachTo(this.node);
				horizontalPosition += wordGeometry.calculateLength() + wordDistance;
			}

			verticalPosition -= lineDistance;
		}
	}

	/* get maximal line length */
	public float getMaximalLineLength() {
		return this.maxLineLength;
	}
}
