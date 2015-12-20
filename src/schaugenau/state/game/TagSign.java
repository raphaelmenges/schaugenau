package schaugenau.state.game;

import com.jme3.app.Application;
import com.jme3.math.Vector3f;

import schaugenau.core.BaseObject;
import schaugenau.font.Font;
import schaugenau.font.Text;

/**
 * Distributed under the MIT License. (See accompanying file LICENSE or copy at
 * https://github.com/raphaelmenges/schaugenau/blob/master/src/LICENSE)
 * 
 * Sign with flower tag.
 * 
 * @author Raphael Menges
 *
 */

public class TagSign extends BaseObject {

	/** enumerations **/
	public enum TagSignState {
		ENTER, TOP, LEAVE, DIE
	};

	/** defines **/
	protected final float enteringDuration = 2.0f;
	protected final float movingTopDuration = 0.5f;
	protected final float leavingDuration = 1;
	protected final float scale = 1.5f;
	protected final float deltaTopLeave = 2f;
	protected final Vector3f start = new Vector3f(0, 1.5f, -1.5f);
	protected final Vector3f position = new Vector3f(0, 3.6f, -1.5f);

	/** fields **/
	protected Font font;
	protected Text text;

	protected float time;
	protected TagSignState tagSignState;

	protected float speed;

	/** methods **/

	/* constructor */
	public TagSign(Application app, Font font, String text) {
		super("TagSign_text");

		/* set members */
		this.font = font;
		this.text = new Text(font, Text.Alignment.CENTER, text);
		tagSignState = TagSignState.ENTER;
		time = 0;
		this.setLocalTranslation(position);
		this.setLocalScale(0);

		/* attach text to root */
		this.text.attachTo(this.node);
	}

	/* update */
	public void update(float tpf, boolean isVisible) {

		/* use states for decision */
		switch (tagSignState) {
		case ENTER: {

			/* enter */
			this.setLocalScale(scale * (time / enteringDuration));
			this.setLocalTranslation(new Vector3f().interpolate(start, position, time / enteringDuration));

			/* check for state change */
			if (time >= enteringDuration) {

				this.setLocalScale(scale);
				this.setLocalTranslation(position.clone());

				tagSignState = TagSignState.TOP;
				time = 0;
			}
			break;
		}
		case TOP: {

			/* staying top */
			if (!isVisible) {
				tagSignState = TagSignState.LEAVE;
				time = 0;
			}
			break;
		}
		case LEAVE: {

			/* leaving */
			this.move(0, (deltaTopLeave * tpf) / leavingDuration, 0);

			if (time >= leavingDuration) {

				/* detach text */
				this.text.detach();

				tagSignState = TagSignState.DIE;
			}
			break;
		}
		case DIE: {
			/* wait do die */
			break;
		}
		}

		/* increment time */
		time += tpf;
	}
}
