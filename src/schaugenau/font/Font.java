package schaugenau.font;

import java.util.ArrayList;
import java.util.HashMap;

import com.jme3.asset.TextureKey;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.texture.Texture;
import com.jme3.util.BufferUtils;

import schaugenau.app.App;

/**
 * Distributed under the MIT License. (See accompanying file LICENSE or copy at
 * https://github.com/raphaelmenges/schaugenau/blob/master/src/LICENSE)
 * 
 * Font class. Letter meshes reach vertical from 0 to 1. Horizontal dimension
 * depends on aspect ratio of the letters in the font texture.
 * 
 * @author Raphael Menges
 *
 */

public class Font {

	/** defines **/

	/* indices of letters in texture */
	protected final HashMap<Character, Integer> letterIndex = new HashMap<Character, Integer>() {
		private static final long serialVersionUID = 2906178223729690724L;

		{
			put(' ', -1);
			put('A', 0);
			put('B', 1);
			put('C', 2);
			put('D', 3);
			put('E', 4);
			put('F', 5);
			put('G', 6);
			put('H', 7);
			put('I', 8);
			put('J', 9);
			put('K', 10);
			put('L', 11);
			put('M', 12);
			put('N', 13);
			put('O', 14);
			put('P', 15);
			put('Q', 16);
			put('R', 17);
			put('S', 18);
			put('T', 19);
			put('U', 20);
			put('V', 21);
			put('W', 22);
			put('X', 23);
			put('Y', 24);
			put('Z', 25);
			put('Ä', 26);
			put('Ö', 27);
			put('Ü', 28);
			put('a', 29);
			put('b', 30);
			put('c', 31);
			put('d', 32);
			put('e', 33);
			put('f', 34);
			put('g', 35);
			put('h', 36);
			put('i', 37);
			put('j', 38);
			put('k', 39);
			put('l', 40);
			put('m', 41);
			put('n', 42);
			put('o', 43);
			put('p', 44);
			put('q', 45);
			put('r', 46);
			put('s', 47);
			put('t', 48);
			put('u', 49);
			put('v', 50);
			put('w', 51);
			put('x', 52);
			put('y', 53);
			put('z', 54);
			put('ä', 55);
			put('ö', 56);
			put('ü', 57);
			put('ß', 58);
			put('0', 59);
			put('1', 60);
			put('2', 61);
			put('3', 62);
			put('4', 63);
			put('5', 64);
			put('6', 65);
			put('7', 66);
			put('8', 67);
			put('9', 68);
			put('+', 69);
			put('-', 70);
			put('=', 71);
			put('(', 72);
			put(')', 73);
			put('*', 74);
			put('/', 75);
			put(':', 76);
			put('.', 77);
			put(',', 78);
			put(';', 79);
			put('#', 80);
			put('<', 81);
			put('>', 82);
			put('?', 83);
			put('!', 84);
			put('\"', 85);
			put('\'', 86);
			put('_', 87);
		}
	};

	/*
	 * extent on x-axis of individual letters (for example, "i" has much space
	 * on the right hand side left)
	 */
	protected final HashMap<Character, Float> individualLetterXExtent = new HashMap<Character, Float>() {
		private static final long serialVersionUID = -9191358351415219219L;

		{
			put(' ', 0.4f);
			put('D', 0.95f);
			put('F', 0.9f);
			put('I', 0.8f);
			put('L', 0.95f);
			put('P', 0.95f);
			put('f', 0.65f);
			put('i', 0.45f);
			put('j', 0.45f);
			put('k', 0.7f);
			put('l', 0.6f);
			put('n', 0.9f);
			put('r', 0.8f);
			put('s', 0.85f);
			put('t', 0.6f);
			put('y', 0.9f);
			put('-', 0.95f);
			put('(', 0.5f);
			put(')', 0.5f);
			put('\'', 0.5f);
			put(':', 0.8f);
			put('.', 0.4f);
			put(',', 0.4f);
			put(';', 0.4f);
			put('?', 0.8f);
			put('!', 0.5f);
		}
	};

	/* shift for letters like "p" */
	protected final HashMap<Character, Float> individualLetterShift = new HashMap<Character, Float>() {
		private static final long serialVersionUID = 4085867204400447553L;

		{
			put('f', -0.2f);
			put('g', -0.25f);
			put('j', -0.2f);
			put('p', -0.15f);
			put('q', -0.15f);
			put('y', -0.15f);
		}
	};

	/** fields **/
	protected ArrayList<Mesh> meshes;
	protected Material material;
	protected boolean useInGui;

	protected float letterWidth;
	protected float letterHeight;

	/** methods **/

	/* constructor */
	public Font(App app, String materialName, String textureName, String textureFormat, ColorRGBA color,
			float letterWidth, float letterHeight, boolean isInFront, boolean useInGui) {

		/* save some values */
		this.useInGui = useInGui;
		this.letterWidth = letterWidth;
		this.letterHeight = letterHeight;

		/* calculate, how many symbols are in the font */
		int columnCount = (int) (1f / letterWidth);
		int definedLetterCount = letterIndex.size() - 1; // ' ' does not count

		/* create array list */
		meshes = new ArrayList<Mesh>();

		/* pregenerate ALL possible meshes (no mesh for space) */
		for (int i = 0; i < definedLetterCount; i++) {

			/* get vertical shift of letter */
			char c = schaugenau.utilities.Helper.getFirstKeyByValue(letterIndex, i);
			Float individualShift = individualLetterShift.get(c);
			if (individualShift == null) {
				individualShift = 0.0f;
			}

			/* create empty mesh */
			Mesh mesh = new Mesh();

			/* vertices */
			Vector3f[] vertices = new Vector3f[4];
			vertices[0] = new Vector3f(0, individualShift, 0);
			vertices[1] = new Vector3f(letterWidth / letterHeight, individualShift, 0);
			vertices[2] = new Vector3f(0, 1 + individualShift, 0);
			vertices[3] = new Vector3f(letterWidth / letterHeight, 1 + individualShift, 0);

			/* calulcate some helpers for texture coordinates */
			int row = i / columnCount;
			int column = i % columnCount;

			/* texture coordinates of vertices */
			Vector2f[] texCoord = new Vector2f[4];
			texCoord[0] = new Vector2f(column * letterWidth, 1f - ((row + 1) * letterHeight));
			texCoord[1] = new Vector2f((column + 1) * letterWidth, 1f - ((row + 1) * letterHeight));
			texCoord[2] = new Vector2f(column * letterWidth, 1f - (row * letterHeight));
			texCoord[3] = new Vector2f((column + 1) * letterWidth, 1f - (row * letterHeight));

			/* create quad out of two triangles */
			int[] indexes = { 2, 0, 1, 1, 3, 2 };

			/* create buffers */
			mesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
			mesh.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoord));
			mesh.setBuffer(Type.Index, 3, BufferUtils.createIntBuffer(indexes));

			/* create bounding box */
			mesh.updateBound();

			/* save mesh */
			meshes.add(mesh);
		}

		/* font material */
		material = new Material(app.getAssetManager(), app.pathMaterials + materialName + ".j3md");
		material.setColor("Color", color);

		material.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
		material.getAdditionalRenderState().setDepthWrite(false);
		material.getAdditionalRenderState().setDepthTest(!isInFront);

		/* font texture */
		TextureKey texKey = new TextureKey(app.pathTextures + textureName + "." + textureFormat, true);
		texKey.setGenerateMips(true);
		Texture tex = app.getAssetManager().loadTexture(texKey);

		/* set font texture */
		material.setTexture("ColorMap", tex);
	}

	/* simplified constructor */
	public Font(App app, String textureName, String textureFormat, ColorRGBA color, float letterWidth,
			float letterHeight, boolean isInFront, boolean useInGui) {
		this(app, "Unshaded", textureName, textureFormat, color, letterWidth, letterHeight, isInFront, useInGui);
	}

	/* set color of font */
	public void setColor(ColorRGBA color) {
		material.setColor("Color", color);
	}

	/* returns spatial with letter */
	public Spatial getLetter(char letter) {

		/* convert char to index in texture */
		int i = letterIndex.get(letter);

		/* check wether it is space */
		if (i < 0) {
			/* return just a node without a mesh */
			return new Node("letter_space");
		} else {
			/* create geometry */
			Geometry geo = new Geometry("letter_" + letter, meshes.get(i));

			/* set some transparency settings */
			if (!useInGui) {
				geo.setQueueBucket(Bucket.Transparent);
			}

			/* set right material */
			geo.setMaterial(material);

			/* return geometry */
			return geo;
		}
	}

	/* return width of letter meshes */
	public float getLetterWidth(char letter) {
		Float individualXExtent = individualLetterXExtent.get(letter);
		if (individualXExtent == null) {
			individualXExtent = 1.0f;
		}

		return individualXExtent * letterWidth / letterHeight;
	}

	/* return height of letter meshes */
	public float getLetterHeight() {
		return 1;
	}
}
