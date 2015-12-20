package schaugenau.gui;

import java.util.LinkedList;

import com.jme3.collision.CollisionResults;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

import schaugenau.app.App;
import schaugenau.core.Entity;
import schaugenau.core.StaticEntity;
import schaugenau.font.Font;
import schaugenau.font.Text;

/**
 * Distributed under the MIT License. (See accompanying file LICENSE or copy at
 * https://github.com/raphaelmenges/schaugenau/blob/master/src/LICENSE)
 * 
 * Decoration for gui.
 * 
 * @author Raphael Menges
 *
 */

public class GuiDecoration extends GuiElement {

	/** defines **/

	/** fields **/
	protected LinkedList<StaticEntity> entities;
	protected LinkedList<Text> texts;

	/** methods **/

	/* constructor */
	public GuiDecoration(App app, GuiAdapter guiAdapter, String name) {
		super(app, guiAdapter, name);

		/* initialize list of entities */
		entities = new LinkedList<StaticEntity>();

		/* initialize list of texts */
		texts = new LinkedList<Text>();
	}

	/* update, returns cursor is over decoration */
	public boolean update(float tpf) {

		/* check for collision with cursor */
		Vector2f cursor = guiAdapter.getCursor();

		/* convert cursor back to worldspace because collide with needs it */
		Ray ray = new Ray(guiAdapter.convertGuiToWorldSpace(new Vector3f(cursor.x, cursor.y, 5)),
				new Vector3f(0, 0, -1));
		boolean collisionDetected = false;

		/* check for collision with all entities */
		for (StaticEntity entity : entities) {
			CollisionResults results = new CollisionResults();
			entity.collideWith(ray, results);

			if (results.size() > 0) {
				collisionDetected = true;
				break;
			}
		}

		/* return the result */
		return collisionDetected;
	}

	/* add static entity, returns index of entity */
	public int addStaticEntity(String mesh, String material, String texture, String textureFormat,
			boolean isTransparent, Vector3f localTranslation, Vector3f localRotation, float localScale) {

		/* entity initialization */
		int index = internalAddStaticEntity(mesh, material, texture, textureFormat, isTransparent);

		/* transform it */
		StaticEntity entity = this.getStaticEntity(index);
		entity.scale(localScale);
		entity.rotate(localRotation);
		entity.move(localTranslation);

		return index;
	}

	/* add static entity, returns index of entity */
	public int addStaticEntity(String mesh, String material, String texture, String textureFormat,
			boolean isTransparent, Vector3f localTranslation, Vector3f localRotation, Vector3f localScale) {

		/* entity initialization */
		int index = internalAddStaticEntity(mesh, material, texture, textureFormat, isTransparent);

		/* transform it */
		StaticEntity entity = this.getStaticEntity(index);
		entity.scale(localScale);
		entity.rotate(localRotation);
		entity.move(localTranslation);

		return index;
	}

	protected int internalAddStaticEntity(String mesh, String material, String texture, String textureFormat,
			boolean isTransparent) {

		/* entity initialization */
		StaticEntity entity = new StaticEntity(app, name + "-" + mesh, mesh, "Unshaded", texture, isTransparent,
				textureFormat, true, false, false, false);
		entities.add(entity);

		/* attach it to local root */
		entity.attachTo(node);

		return entities.size() - 1;
	}

	/* add text, returns index of text */
	public int addText(Font font, Text.Alignment alignment, String content, Vector3f localTranslation,
			float localScale) {

		/* text initialization */
		Text text = new Text(font, alignment, content);
		texts.add(text);

		/* transform it */
		text.scale(localScale);
		text.move(localTranslation);

		/* attach it to local root */
		text.attachTo(node);

		return texts.size() - 1;
	}

	/* get static entity by index */
	public StaticEntity getStaticEntity(int index) {
		return entities.get(index);
	}

	/* get text by index */
	public Text getText(int index) {
		return texts.get(index);
	}

	/* sets color parameter for all entities */
	public void setColorParameterOfEntities(ColorRGBA color) {
		for (Entity entity : this.entities) {
			entity.setColorParameter(color.clone());
		}
	}
}