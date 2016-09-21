package com.hess.assignment1;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;

/**
 * This class is reponsible for drawing assets to the screen
 * 
 * @author Raistlin Hess
 *
 */
public class WorldRenderer implements Disposable
{
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private WorldController worldController;

	/**
	 * Instantiates a WorldController
	 * 
	 * @param worldController
	 */
	public WorldRenderer(WorldController worldController)
	{
		this.worldController = worldController;
		init();
	}

	/**
	 * Initializes the camera's position
	 */
	public void init()
	{
		batch = new SpriteBatch();
		camera = new OrthographicCamera(Constants.VIEWPORT_WIDTH,Constants.VIEWPORT_HEIGHT);
		camera.position.set(0,0,0);
		camera.update();
	}
	
	/**
	 * Draws sprites in the SpriteBatch to the screen
	 */
	public void render()
	{
		renderTestObjects();
	}
	
	/**
	 * Transforms sprites based on the camera and it's configuration and then draws the result
	 */
	private void renderTestObjects()
	{
		worldController.cameraHelper.applyTo(camera);
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		for(Sprite sprite: worldController.testSprites)
		{
			sprite.draw(batch);
		}
		batch.end();
	}
	
	/**
	 * This method updates the viewport of the camera based on the window size
	 * 
	 * @param width
	 * @param height
	 */
	public void resize(int width, int height)
	{
		camera.viewportWidth = (Constants.VIEWPORT_HEIGHT/height) * width;
		camera.update();
	}

	/**
	 * Cleans up sprites
	 */
	@Override
	public void dispose()
	{
		batch.dispose();
	}
}
