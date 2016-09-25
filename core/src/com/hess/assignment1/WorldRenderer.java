package com.hess.assignment1;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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
	private OrthographicCamera cameraGUI;
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
	 * Initializes the camera's position as well as the GUI camera
	 */
	public void init()
	{
		//Initialize the spritebatch and game camera
		batch = new SpriteBatch();
		camera = new OrthographicCamera(Constants.VIEWPORT_WIDTH,Constants.VIEWPORT_HEIGHT);
		camera.position.set(0,0,0);
		camera.update();
		
		//Initialize the GUI camera
		cameraGUI = new OrthographicCamera(Constants.VIEWPORT_GUI_WIDTH, Constants.VIEWPORT_GUI_HEIGHT);
		cameraGUI.position.set(0,0,0);
		cameraGUI.setToOrtho(true);	//Flips y-axis
		cameraGUI.update();
	}
	
	/**
	 * Draws sprites in the SpriteBatch to the screen
	 */
	public void render()
	{
		renderWorld(batch);
		renderGui(batch);
	}
	
	/**
	 * This method is responsible for transforming the camera and rendering each object
	 * for the level 
	 * 
	 * @param batch
	 */
	private void renderWorld(SpriteBatch batch)
	{
		worldController.cameraHelper.applyTo(camera);
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
			worldController.level.render(batch);
		batch.end();
	}
	
	/**
	 * This method renders all the components in the GUI
	 */
	private void renderGui(SpriteBatch batch)
	{
		batch.setProjectionMatrix(cameraGUI.combined);
		batch.begin();
			//Draw collected gold coins icon and text to top-left corner
			renderGuiScore(batch);
			
			//Draw extra lives icon and text to the top right edge
			renderGuiExtraLive(batch);
			
			//Draw FPS text to bottom right edge
			renderGuiFpsCounter(batch);
		batch.end();
	}
	
	/**
	 * This is responsible for rendering the score in the GUI
	 * 
	 * @param batch
	 */
	private void renderGuiScore(SpriteBatch batch)
	{
		float x = -15;
		float y = -15;
		batch.draw(Assets.instance.goldCoin.goldCoin, x, y, 50, 50, 100, 100, 0.35f, -0.35f, 0);
		Assets.instance.fonts.defaultBig.draw(batch, ""+worldController.score, x+75, y+37);
	}
	
	/**
	 * This is responsible for rendering the extra lives counter in
	 * the GUI
	 * 
	 * @param batch
	 */
	private void renderGuiExtraLive(SpriteBatch batch)
	{
		float x = cameraGUI.viewportWidth-50-Constants.LIVES_START*50;
		float y = -15;
		
		for(int i = 0; i < Constants.LIVES_START; i++)
		{
			if(worldController.lives <= i)
			{
				batch.setColor(0.5f, 0.5f, 0.5f, 0.5f);
			}
			batch.draw(Assets.instance.bunny.head, x+i*50, y, 50, 50, 120, 100, 0.35f, -0.35f, 0);
			batch.setColor(1,1,1,1);
		}
	}
	
	/**
	 * This method renders the FPS counter to the GUI camera
	 * 
	 * @param batch
	 */
	private void renderGuiFpsCounter(SpriteBatch batch)
	{
		float x = cameraGUI.viewportWidth-55;
		float y = cameraGUI.viewportHeight-15;
		int fps = Gdx.graphics.getFramesPerSecond();
		BitmapFont fpsFont = Assets.instance.fonts.defaultNormal;
		
		if(fps >= 45)
		{
			//Make font color green
			fpsFont.setColor(0,1,0,1);
		}
		else if(fps >= 30)
		{
			//Make font color yellow
			fpsFont.setColor(1,1,0,1);
		}
		else
		{
			//Make font color red
			fpsFont.setColor(1,0,0,1);
		}
		
		fpsFont.draw(batch, "FPS: "+fps, x, y);
		fpsFont.setColor(1,1,1,1);
	}
	
	/**
	 * This method updates the viewport of the camera and cameraGUI based on the window size
	 * 
	 * @param width
	 * @param height
	 */
	public void resize(int width, int height)
	{
		//Update game camera
		camera.viewportWidth = (Constants.VIEWPORT_HEIGHT/height) * width;
		camera.update();
		
		//Update GUI camera
		cameraGUI.viewportHeight = Constants.VIEWPORT_GUI_HEIGHT;
		cameraGUI.viewportWidth = (Constants.VIEWPORT_GUI_HEIGHT/(float)height) *(float)width;
		cameraGUI.position.set(cameraGUI.viewportWidth/2, cameraGUI.viewportHeight/2, 0);
		cameraGUI.update();
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
