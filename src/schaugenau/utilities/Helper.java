package schaugenau.utilities;

import java.util.Map;
import java.util.Map.Entry;

import com.jme3.app.Application;
import com.jme3.collision.CollisionResults;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.shape.Box;

/**
 * Distributed under the MIT License. (See accompanying file LICENSE or copy at
 * https://github.com/raphaelmenges/schaugenau/blob/master/src/LICENSE)
 * 
 * Helpers.
 * 
 * @author Raphael Menges
 *
 */

public class Helper {

	/* draw the x,y,z axes */
	public static Node drawAxes(Application app) {
		Node axis = new Node();
		Arrow xAxe = new Arrow(new Vector3f(1, 0, 0));
		Material mat1 = new Material(app.getAssetManager(), "Materials/Unshaded.j3md");
		Geometry geom1 = new Geometry("xAxe", xAxe);
		mat1.setColor("Color", ColorRGBA.Red);
		geom1.setMaterial(mat1);
		Arrow yAxe = new Arrow(new Vector3f(0, 1, 0));
		Material mat2 = new Material(app.getAssetManager(), "Materials/Unshaded.j3md");
		Geometry geom2 = new Geometry("yAxe", yAxe);
		mat2.setColor("Color", ColorRGBA.Green);
		geom2.setMaterial(mat2);
		Arrow zAxe = new Arrow(new Vector3f(0, 0, 1));
		Material mat3 = new Material(app.getAssetManager(), "Materials/Unshaded.j3md");
		Geometry geom3 = new Geometry("zAxe", zAxe);
		mat3.setColor("Color", ColorRGBA.Blue);
		geom3.setMaterial(mat3);

		axis.attachChild(geom1);
		axis.attachChild(geom2);
		axis.attachChild(geom3);

		return axis;
	}

	/* returns a random int between two values */
	public static float randomInIntervall(float low, float high) {
		return (float) (Math.random() * (high - low) + low);
	}

	/* mouse picking with z-Layer */
	public static Vector3f mousePickingZ(Application app, Vector2f cursorCoords, float z) {

		/* create box and use front as layer for player */
		Box box = new Box(100, 100, 0);
		Geometry geom = new Geometry("Box", box);
		geom.move(0, 0, z);

		/* crazy ray calculations */
		Ray mouseRay = new Ray();
		Vector3f camPos = app.getCamera().getWorldCoordinates(cursorCoords, 0f).clone();
		Vector3f camDir = app.getCamera().getWorldCoordinates(cursorCoords, 1f).clone();
		camDir.subtractLocal(camPos).normalizeLocal();
		mouseRay.setOrigin(camPos);
		mouseRay.setDirection(camDir);
		CollisionResults results = new CollisionResults();
		geom.collideWith(mouseRay, results);

		/* return result */
		if (results.size() > 0) {
			return results.getClosestCollision().getContactPoint();
		} else {
			return new Vector3f();
		}
	}

	/* clamp vector3f */
	public static void clamp(Vector3f vec, float minX, float maxX, float minY, float maxY, float minZ, float maxZ) {
		vec.x = vec.x < minX ? minX : vec.x;
		vec.x = vec.x > maxX ? maxX : vec.x;

		vec.y = vec.y < minY ? minY : vec.y;
		vec.y = vec.y > maxY ? maxY : vec.y;

		vec.z = vec.z < minZ ? minZ : vec.z;
		vec.z = vec.z > maxZ ? maxZ : vec.z;
	}

	/* calc value with deviation */
	public static float getValueWithDeviation(float value, float deviation) {
		return value + (float) (2.0f * deviation * (Math.random() - 0.5f));
	}

	/* snap 2D coords to the centers of a grid */
	public static Vector2f snapToGrid(Vector2f grid, Vector2f min, Vector2f max, Vector2f coord) {

		float length;
		float value;
		Vector2f result = new Vector2f();

		/* x */

		length = max.x - min.x;
		value = coord.x - min.x;

		/* calc value in grid */
		value = value / length;
		value *= grid.x;
		value = (int) value;
		if (value == grid.x) {
			value--;
		}
		value += 0.5f;

		/* get real coord */
		result.x = value * (length / grid.x) + min.x;

		/* y */

		length = max.y - min.y;
		value = coord.y - min.y;

		/* calc value in grid */
		value = value / length;
		value *= grid.y;
		value = (int) value;
		if (value == grid.y) {
			value--;
		}
		value += 0.5f;

		/* get real coord */
		result.y = value * (length / grid.y) + min.y;

		return result;
	}

	/* get first key by value */
	public static <T, E> T getFirstKeyByValue(Map<T, E> map, E value) {
		for (Entry<T, E> entry : map.entrySet()) {
			if (value.equals(entry.getValue())) {
				return entry.getKey();
			}
		}
		return null;
	}
}