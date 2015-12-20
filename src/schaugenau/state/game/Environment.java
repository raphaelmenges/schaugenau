package schaugenau.state.game;

import java.util.LinkedList;

import com.jme3.collision.CollisionResults;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;

import schaugenau.app.App;
import schaugenau.core.BaseObject;
import schaugenau.core.StaticEntity;

/**
 * Distributed under the MIT License. (See accompanying file LICENSE or copy at
 * https://github.com/raphaelmenges/schaugenau/blob/master/src/LICENSE)
 * 
 * Environment.
 * 
 * @author Raphael Menges
 *
 */

public class Environment extends BaseObject {

	/** defines **/

	/* colors */
	protected final ColorRGBA brightColor = new ColorRGBA(1, 1, 1, 1);
	protected final ColorRGBA dimmedColor = new ColorRGBA(0.15f, 0.2f, 0.25f, 1);
	protected final ColorRGBA correctColor = new ColorRGBA(0.1f, 0.3f, 0.1f, 1);
	protected final ColorRGBA incorrectColor = new ColorRGBA(0.3f, 0.05f, 0.00f, 1);
	protected final float dimmingSpeed = 0.5f;
	protected final float colorFadingSpeed = 2f;

	/* terrain */
	protected final int terrainCount = 8;
	protected final float terrainLength = 200;
	protected final float terrainWidth = 500;
	protected final float terrainHeight = -10;
	protected final float terrainSpeed = 20;
	protected final float terrainXScale = 1.5f;

	/* sky */
	protected final float skyScale = 3000;

	/* trees */
	protected final int treeACount = 40;
	protected final int treeBCount = 40;
	protected final int treeCCount = 60;
	protected final float treeYOffset = -0.5f;
	protected final float treeFreeWidth = 60;
	protected final float treeMaxScale = 7f;
	protected final float treeMinScale = 5f;
	protected final float treeTerrainWidthUsage = 1f;

	/* stones */
	protected final int stoneACount = 12;
	protected final int stoneBCount = 12;
	protected final int stoneCCount = 12;
	protected final float stoneYOffset = -0.1f;
	protected final float stoneFreeWidth = 55;
	protected final float stoneMaxScale = 4f;
	protected final float stoneMinScale = 3f;
	protected final float stoneTerrainWidthUsage = 0.8f;

	/* grass */
	protected final int grassCount = 400;
	protected final float grassMaxScale = 2.0f;
	protected final float grassMinScale = 1.5f;
	protected final float grassYOffset = -1.5f;
	protected final float grassFreeWidth = 20;
	protected final float grassTerrainWidthUsage = 0.4f;

	/** fields **/
	protected LinkedList<StaticEntity> terrainList;
	protected StaticEntity sky;
	protected LinkedList<StaticEntity> treeList;
	protected LinkedList<StaticEntity> stoneList;
	protected LinkedList<StaticEntity> grassList;
	protected float dimmingTransition;
	protected ColorRGBA currentColor;

	/** methods **/

	/* constructor */
	public Environment(App app) {
		super("enviromentRoot");

		dimmingTransition = 0;

		/* create terrain */
		terrainList = new LinkedList<StaticEntity>();
		for (int i = 0; i < terrainCount; i++) {
			/* terrain */
			StaticEntity terrain = new StaticEntity(app, "Terrain", "LongFaded", false, "png", false, false, false,
					false);
			terrain.move(new Vector3f(0f, terrainHeight, -(i * terrainLength)));
			terrain.scale(terrainXScale, 1, 1);
			terrainList.add(terrain);
			terrain.attachTo(node);
		}

		/* create sky */
		sky = new StaticEntity(app, "Sky", "Unshaded", false, "png", false, false, false, false);
		sky.scale(skyScale);
		sky.attachTo(node);

		/* create trees */
		treeList = new LinkedList<StaticEntity>();

		for (int i = 0; i < treeACount; i++) {
			StaticEntity tree = new StaticEntity(app, "Tree-A", "Tree-A", "LongFaded", "Tree", false, "png", false,
					false, true, true);
			tree.setLocalTranslation(
					generatePositionOnTerrain(false, treeFreeWidth, treeYOffset, treeTerrainWidthUsage));
			tree.scale(schaugenau.utilities.Helper.randomInIntervall(treeMinScale, treeMaxScale));
			tree.rotate(new Vector3f(0, (float) (Math.random() * 360.0f), 0));
			tree.attachTo(node);
			treeList.add(tree);
		}

		for (int i = 0; i < treeBCount; i++) {
			StaticEntity tree = new StaticEntity(app, "Tree-B", "Tree-B", "LongFaded", "Tree", false, "png", false,
					false, true, true);
			tree.setLocalTranslation(
					generatePositionOnTerrain(false, treeFreeWidth, treeYOffset, treeTerrainWidthUsage));
			tree.scale(schaugenau.utilities.Helper.randomInIntervall(treeMinScale, treeMaxScale));
			tree.rotate(new Vector3f(0, (float) (Math.random() * 360.0f), 0));
			tree.attachTo(node);
			treeList.add(tree);
		}

		for (int i = 0; i < treeCCount; i++) {
			StaticEntity tree = new StaticEntity(app, "Tree-C", "Tree-C", "LongFaded", "Tree", false, "png", false,
					false, true, true);
			tree.setLocalTranslation(
					generatePositionOnTerrain(false, treeFreeWidth, treeYOffset, treeTerrainWidthUsage));
			tree.scale(schaugenau.utilities.Helper.randomInIntervall(treeMinScale, treeMaxScale));
			tree.rotate(new Vector3f(0, (float) (Math.random() * 360.0f), 0));
			tree.attachTo(node);
			treeList.add(tree);
		}

		/* create stones */
		stoneList = new LinkedList<StaticEntity>();

		for (int i = 0; i < stoneACount; i++) {
			StaticEntity stone = new StaticEntity(app, "Stone-A", "Stone-A", "LongFaded", "Stone", false, "png", false,
					false, false, true);
			stone.setLocalTranslation(
					generatePositionOnTerrain(false, stoneFreeWidth, stoneYOffset, stoneTerrainWidthUsage));
			stone.scale(schaugenau.utilities.Helper.randomInIntervall(stoneMinScale, stoneMaxScale));
			stone.rotate(new Vector3f(0, (float) (Math.random() * 360.0f), 0));
			stone.attachTo(node);
			stoneList.add(stone);
		}

		for (int i = 0; i < stoneBCount; i++) {
			StaticEntity stone = new StaticEntity(app, "Stone-B", "Stone-B", "LongFaded", "Stone", false, "png", false,
					false, false, true);
			stone.setLocalTranslation(
					generatePositionOnTerrain(false, stoneFreeWidth, stoneYOffset, stoneTerrainWidthUsage));
			stone.scale(schaugenau.utilities.Helper.randomInIntervall(stoneMinScale, stoneMaxScale));
			stone.rotate(new Vector3f(0, (float) (Math.random() * 360.0f), 0));
			stone.attachTo(node);
			stoneList.add(stone);
		}

		for (int i = 0; i < stoneCCount; i++) {
			StaticEntity stone = new StaticEntity(app, "Stone-C", "Stone-C", "LongFaded", "Stone", false, "png", false,
					false, false, true);
			stone.setLocalTranslation(
					generatePositionOnTerrain(false, stoneFreeWidth, stoneYOffset, stoneTerrainWidthUsage));
			stone.scale(schaugenau.utilities.Helper.randomInIntervall(stoneMinScale, stoneMaxScale));
			stone.rotate(new Vector3f(0, (float) (Math.random() * 360.0f), 0));
			stone.attachTo(node);
			stoneList.add(stone);
		}

		/* create grass */
		grassList = new LinkedList<StaticEntity>();
		for (int i = 0; i < grassCount; i++) {
			StaticEntity grass = new StaticEntity(app, "Grass", "Grass", "LongFaded", "Grass", true, "png", false, true,
					false, false);
			grass.setLocalTranslation(
					generatePositionOnTerrain(false, grassFreeWidth, grassYOffset, grassTerrainWidthUsage));
			grass.scale(schaugenau.utilities.Helper.randomInIntervall(grassMinScale, grassMaxScale));
			grass.attachTo(node);
			grassList.add(grass);
		}

		/* set last color */
		this.currentColor = dimmedColor.clone();

	}

	/* update it */
	public void update(float tpf, boolean moving, boolean dimmed, boolean showCorrectness, boolean isCorrect,
			boolean forceUndimmed) {

		/* choose right color */
		ColorRGBA targetColor;
		if (showCorrectness) {
			if (isCorrect) {
				targetColor = correctColor;
			} else {
				targetColor = incorrectColor;
			}
		} else {
			targetColor = dimmedColor;
		}

		/* interpolate current color */
		currentColor.interpolate(targetColor, tpf * colorFadingSpeed);

		/* do dimming */
		ColorRGBA color = new ColorRGBA();
		if (forceUndimmed) {
			color = brightColor.clone();
			currentColor = dimmedColor.clone();
			dimmingTransition = 0;
		} else {
			if (dimmed) {
				dimmingTransition += dimmingSpeed * tpf;
			} else {
				dimmingTransition -= dimmingSpeed * tpf;
			}
			dimmingTransition = Math.min(1, Math.max(0, dimmingTransition));
			color.interpolate(brightColor, currentColor, dimmingTransition);
		}

		/* movement */
		float movement = 0;
		if (moving) {
			movement = terrainSpeed * tpf;
		}

		/* update terrain */
		for (StaticEntity terrain : terrainList) {
			terrain.setColorParameter(color);
			terrain.move(new Vector3f(0f, 0f, movement));
			if (terrain.getWorldTranslation().z > (terrainLength / 2)) {
				terrain.move(new Vector3f(0f, 0f, -(terrainCount * terrainLength)));
			}
		}

		/* update sky */
		sky.setColorParameter(color);

		/* update trees */
		for (StaticEntity tree : treeList) {
			tree.setColorParameter(color);
			tree.move(new Vector3f(0f, 0f, movement));
			if (tree.getWorldTranslation().z > (terrainLength / 2)) {
				tree.setLocalTranslation(
						generatePositionOnTerrain(true, treeFreeWidth, treeYOffset, treeTerrainWidthUsage));
				tree.rotate(new Vector3f(0, (float) (Math.random() * 360.0f), 0));
			}
		}

		/* update stones */
		for (StaticEntity stone : stoneList) {
			stone.setColorParameter(color);
			stone.move(new Vector3f(0f, 0f, movement));
			if (stone.getWorldTranslation().z > (terrainLength / 2)) {
				stone.setLocalTranslation(
						generatePositionOnTerrain(true, stoneFreeWidth, stoneYOffset, stoneTerrainWidthUsage));
				stone.rotate(new Vector3f(0, (float) (Math.random() * 360.0f), 0));
			}
		}

		/* update grass */
		for (StaticEntity grass : grassList) {
			grass.setColorParameter(color);
			grass.move(new Vector3f(0f, 0f, movement));
			if (grass.getWorldTranslation().z > (terrainLength / 2)) {
				grass.setLocalTranslation(
						generatePositionOnTerrain(true, grassFreeWidth, grassYOffset, grassTerrainWidthUsage));
			}
		}
	}

	/* get terrain list for shadows */
	public LinkedList<StaticEntity> getTerrainList() {
		return terrainList;
	}

	/* generate position on terrrain */
	protected Vector3f generatePositionOnTerrain(boolean setOnlyIntoFog, float freeWidth, float yOffset,
			float terrainWidthUsage) {

		/* x and y */
		float x = generateXPositionOnTerrain(freeWidth, terrainWidthUsage);
		float z = generateZPositionOnTerrain(setOnlyIntoFog);

		/* y */
		float y = -10;
		float startHeight = 100;
		CollisionResults results = new CollisionResults();
		Ray ray = new Ray(new Vector3f(x, startHeight, y), new Vector3f(0, -1, 0));
		for (StaticEntity terrain : terrainList) {
			terrain.collideWith(ray, results);
			if (results.size() > 0) {
				y = results.getClosestCollision().getContactPoint().y + yOffset;
				break;
			}
		}
		return new Vector3f(x, y, z);
	}

	/* generate x position */
	protected float generateXPositionOnTerrain(float entityFreeWidth, float terrainWidthUsage) {
		float x = entityFreeWidth / 2
				+ ((terrainWidth * terrainWidthUsage * terrainXScale - entityFreeWidth) / 2 * (float) Math.random());
		if (Math.random() >= 0.5) {
			x = -x;
		}
		return x;
	}

	/* generate z Position */
	protected float generateZPositionOnTerrain(boolean setOnlyIntoFog) {
		if (setOnlyIntoFog) {
			return (-(terrainCount * terrainLength) + 0.75f * (float) Math.random() * terrainLength);
		} else {
			return -(terrainCount * terrainLength * (float) Math.random() - (terrainLength / 2));
		}
	}

}
