package schaugenau.state.survey;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

import schaugenau.app.App;
import schaugenau.app.App.Language;
import schaugenau.core.FadableState;
import schaugenau.core.SimpleWorldBackground;
import schaugenau.database.ScoreOperations;
import schaugenau.database.SurveyOperations;
import schaugenau.font.TextBox;
import schaugenau.gui.IconButton;

/**
 * Distributed under the MIT License. (See accompanying file LICENSE or copy at
 * https://github.com/raphaelmenges/schaugenau/blob/master/src/LICENSE)
 * 
 * Survey state.
 * 
 * @author Raphael Menges
 *
 */

public class SurveyState extends FadableState {

	/** defines **/
	protected float welcomeTextBoxScale = 0.8f;
	protected Vector3f welcomeTextBoxPosition = new Vector3f(0, 2.5f, 0);
	protected float welcomeButtonScale = 2.5f;
	protected Vector3f welcomeButtonPosition = new Vector3f(0, -2.25f, 0);

	/* items */
	protected Vector3f restPosition = new Vector3f(25, 0, 0);
	protected float itemAnimationDuration = 1.0f;

	/** fields **/

	/* welcome screen */
	protected Node welcomeNode;
	protected TextBox welcomeTextBox;
	protected IconButton welcomeButton;
	protected boolean welcomeAttached;

	/* items */
	protected List<SurveyItem> items;
	protected int itemIndex;
	protected SurveyItem currentItem;
	protected SurveyItem previousItem;
	protected float itemAnimationTime;

	/* other */
	protected SurveyOperations surveyOperations;
	protected ScoreOperations scoreOperations;
	protected Random rand;
	protected SimpleWorldBackground background;

	/** methods **/

	/* constructor */
	public SurveyState(App app, String name, boolean debugging) {
		super(app, name, debugging);

		/* welcome screen */
		welcomeNode = new Node("WelcomeNode");

		welcomeTextBox = new TextBox(this.app.getPrimaryGuiFont(), schaugenau.font.TextBox.Alignment.CENTER);
		welcomeTextBox.setLocalScale(welcomeTextBoxScale);
		welcomeTextBox.setLocalTranslation(welcomeTextBoxPosition);
		welcomeTextBox.attachTo(welcomeNode);

		welcomeButton = new IconButton(this.app, this.guiAdapter, welcomeButtonPosition, welcomeButtonScale, "Icon-Ok",
				"WelcomeButton");
		welcomeButton.attachTo(welcomeNode);

		/* database stuff */
		surveyOperations = new SurveyOperations();
		scoreOperations = new ScoreOperations();
		/* other */
		background = new SimpleWorldBackground(this.app, false);
		background.attachTo(rootNode);
		rand = new Random();

	}

	/* update */
	@Override
	public boolean update(float tpf, boolean buzzerPressed) {
		boolean hasDetachedItself = super.update(tpf, buzzerPressed);

		/* no items ? */
		if (items.isEmpty()) {
			this.app.loadIdleState();
		}

		if (!paused && !items.isEmpty()) {
			this.background.update(tpf);

			/* say hello */
			if (welcomeAttached) {
				if (welcomeButton.update(tpf, true)) {
					guiAdapter.detachChild(welcomeNode);
					welcomeAttached = false;

					/* attach first item */
					this.nextItem();

				}
			}
			/* show items */
			else {

				/* animation */
				itemAnimationTime -= tpf;
				itemAnimationTime = Math.max(itemAnimationTime, 0);
				float t = 1.0f - (itemAnimationTime / itemAnimationDuration);

				/* animate current item */
				currentItem.setLocalTranslation(new Vector3f().interpolate(restPosition, new Vector3f(), t));

				/* animate previous item */
				if (previousItem != null) {
					previousItem.setLocalTranslation(
							new Vector3f().interpolate(new Vector3f(), restPosition.clone().negateLocal(), t));
					previousItem.update(tpf);
				}

				/* wait for feedback from current */
				int result = currentItem.update(tpf);
				if (result != -1) {

					/* save results */
					/* save results */
					int newestScore = scoreOperations.getMaxPkey();
					surveyOperations.saveResult(app.getCurrentGameStyleString(), app.getCurrentScore(),
							currentItem.getId(), result, newestScore);

					/* next items, if possible */
					if (!nextItem()) {

						/* no further items in this session */
						this.app.setDoSurvey(false);
						this.app.loadIdleState();
					}
				}
			}
		}

		return hasDetachedItself;
	}

	/* fade in, returns if finished */
	@Override
	protected boolean fadeIn(float tpf) {

		boolean fadingDone = super.fadeIn(tpf);

		/* nothing to do */

		return fadingDone;

	}

	/* running */
	@Override
	protected void running(float tpf, boolean buzzerPressed) {
		super.running(tpf, buzzerPressed);

		/* nothing to do */
	}

	/* fade out, returns if finished */
	@Override
	protected boolean fadeOut(float tpf) {

		boolean fadingDone = super.fadeOut(tpf);

		/* nothing to do */

		return fadingDone;
	}

	/* attach */
	@Override
	protected void attach() {
		super.attach();

		/* fetch items from database */
		items = this.fetchSurveyItems(this.app.getLanguage() == Language.ENGLISH);

		/* say hello */
		guiAdapter.attachChild(welcomeNode);
		welcomeAttached = true;
		welcomeTextBox.setContent(this.app.getMessages().getString("survey.welcome"));

		/* other */
		currentItem = null;
		itemIndex = 0;
	}

	/* detach */
	@Override
	protected void detach() {
		super.detach();

		/* detach survey items */
		for (SurveyItem item : items) {
			item.detach();
		}

	}

	/* stop */
	@Override
	public void stop() {
		super.stop();

		/* nothing to do */
	}

	/* fetch items from database */
	protected List<SurveyItem> fetchSurveyItems(boolean english) {

		/* create empty list */
		List<SurveyItem> items = new LinkedList<>();

		/* set language */
		if (english) {
			SurveyOperations.setLanguage("english");
		} else {
			SurveyOperations.setLanguage("german");
		}

		/* try to get items */
		try {
			int count = surveyOperations.getSessionCount();
			int session = this.rand.nextInt(count) + 1;
			items = surveyOperations.getSession(session, this.app, this.guiAdapter);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}

		/* attach all items an place them on the right side */
		for (SurveyItem item : items) {
			item.attachTo(guiAdapter.getNode());
			item.setLocalTranslation(restPosition.clone());
		}

		/* return items */
		return items;
	}

	/* attach next items if possible */
	protected boolean nextItem() {

		/* check, whether there are further items */
		if (itemIndex < items.size()) {

			if (currentItem != null) {
				if (previousItem != null) {
					/* hide previous */
					previousItem.setLocalTranslation(restPosition.clone().negateLocal());
				}
				previousItem = currentItem;
			}
			currentItem = items.get(itemIndex);
			itemIndex++;

			itemAnimationTime = itemAnimationDuration;

			return true;
		} else {
			return false;
		}
	}
}
