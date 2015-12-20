package schaugenau.utilities;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;

import schaugenau.app.App;
import schaugenau.core.BaseObject;

/**
 * Distributed under the MIT License. (See accompanying file LICENSE or copy at
 * https://github.com/raphaelmenges/schaugenau/blob/master/src/LICENSE)
 * 
 * Line for visual debugging.
 * 
 * @author Raphael Menges
 *
 */

public class DebugLine extends BaseObject {

	/** defines **/

	/** fields **/
	protected Mesh mesh;
	protected Geometry geometry;

	/** methods **/

	/* constructor */
	public DebugLine(App app, Vector3f start, Vector3f end, ColorRGBA color) {
		super("debugLine");

		/* create mesh */
		mesh = new Mesh();
		mesh.setMode(Mesh.Mode.Lines);

		/* set buffer */
		Vector3f[] vertices = new Vector3f[2];
		vertices[0] = start.clone();
		vertices[1] = end.clone();
		mesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(vertices));

		/* create bounding box */
		mesh.updateBound();

		/* create material */
		Material material = new Material(app.getAssetManager(), app.pathMaterials + "Unshaded.j3md");
		material.setColor("Color", color);

		/* create geometry */
		geometry = new Geometry("debugLine", mesh);
		geometry.setMaterial(material);

		/* attach to own root */
		this.node.attachChild(geometry);
	}

	/* alternative constructor with ray */
	public DebugLine(App app, Ray ray, float length, ColorRGBA color) {
		this(app, ray.origin, new Vector3f(ray.direction.clone().mult(length)), color);
	}
}
