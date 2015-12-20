package schaugenau.state.game;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JOptionPane;

import com.jme3.app.Application;
import com.jme3.asset.plugins.FileLocator;

import schaugenau.database.PictureOperations;

/**
 * Distributed under the MIT License. (See accompanying file LICENSE or copy at
 * https://github.com/raphaelmenges/schaugenau/blob/master/src/LICENSE)
 * 
 * Image loader.
 * 
 * @author Raphael Menges
 *
 */

public class ImageLoader {

	/** defines **/
	protected final String pathToImages = "images/";
	protected final String prefixCorrectImage = "Correct";
	protected final String prefixIncorrectImage = "Incorrect";
	protected final String postfixImage = "png";

	/** fields **/
	protected Application app;
	protected ExecutorService service;

	protected int correctPictureID;
	protected int incorrectPictureID;

	/* database */
	protected PictureOperations pictureOperation;

	/** methods **/

	/* constructor */
	public ImageLoader(Application app) {

		/* defaults */
		correctPictureID = -1;
		incorrectPictureID = -1;

		/* database */
		pictureOperation = new PictureOperations();

		/* clean up images folder, maybe some old are left */
		File folder = new File(pathToImages);
		File[] listOfFiles = folder.listFiles();

		/* folder there? */
		if (!folder.exists()) {
			JOptionPane.showMessageDialog(null,
					"No 'images' folder found, please create one in the same folder as the .jar");
		}

		/* delete all files in it */
		for (File file : listOfFiles) {
			file.delete();
		}

		/* add folder to asset manager */
		this.app = app;
		this.app.getAssetManager().registerLocator(pathToImages, FileLocator.class);

		/* create thread pool */
		service = Executors.newFixedThreadPool(2);
	}

	/* load next images */
	public void loadNextImages() {
		deleteCurrentImages();

		/* do it in a thread */
		service.submit(new Runnable() {
			@Override
			public void run() {
				try {
					pictureOperation.loadPictures();
				} catch (Exception e) {
					e.printStackTrace();
				}
				correctPictureID = pictureOperation.getCorPictID();
				incorrectPictureID = pictureOperation.getIncorPictID();
			}
		});
	}

	/* delete last images */
	private void deleteCurrentImages() {

		/* images can be deleted safely */
		File file = new File(pathToImages + prefixCorrectImage + correctPictureID + "." + postfixImage);
		if (file.exists()) {
			file.delete();
		}
		file = new File(pathToImages + prefixIncorrectImage + incorrectPictureID + "." + postfixImage);
		if (file.exists()) {
			file.delete();
		}
	}

	/* shut down */
	public void shutdownNow() {
		deleteCurrentImages();
		service.shutdownNow();
	}

	/* get id of correct image */
	public int getCurrentCorrectID() {
		return correctPictureID;
	}

	/* get id of incorrect image */
	public int getCurrentIncorrectID() {
		return incorrectPictureID;
	}

	/* get tag */
	public String getCurrentTag() {
		String tag = PictureOperations.getCorPictTag();
		return tag == null ? "no tag found" : tag;
	}

	/* getter */
	public String getPrefixCorrectImage() {
		return prefixCorrectImage;
	}

	/* getter */
	public String getPrefixIncorrectImage() {
		return prefixIncorrectImage;
	}

	/* getter */
	public String getPostfixImage() {
		return postfixImage;
	}

	/* getter */
	public String getPathToImages() {
		return pathToImages;
	}

}
