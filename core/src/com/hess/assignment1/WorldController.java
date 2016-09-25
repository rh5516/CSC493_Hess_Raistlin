package com.hess.assignment1;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

/**
 * This class is responsible for updating information about the game objects, as well as the camera,
 * and handling input
 * 
 * @author Raistlin Hess
 *
 */
public class WorldController extends InputAdapter
{
	private static final String TAG = WorldController.class.getName();
	public CameraHelper cameraHelper;
	public Level level;
	public int lives;
	public int score;
	
	public WorldController()
	{
		init();
	}
	
	/**
	 * This method sets up WorldController to accept and process input, initialize the CameraHelper,
	 * and sets the lives and call initLevel()
	 */
	public void init()
	{
		Gdx.input.setInputProcessor(this);
		cameraHelper = new CameraHelper();
		lives = Constants.LIVES_START;
		initLevel();
	}
	
	/**
	 * Sets the score to 0 and loads the first level
	 */
	public void initLevel()
	{
		score = 0;
		level = new Level(Constants.LEVEL_01);
	}
		
	/**
	 * This method generates images on the fly
	 * 
	 * @param width
	 * @param height
	 * @return
	 */
	private Pixmap createProceduralPixmap(int width, int height)
	{
		Pixmap pixmap = new Pixmap(width, height, Format.RGBA8888);
		//Fill square with transparent red
		pixmap.setColor(1,0,0,0.5f);
		pixmap.fill();
		
		//Draw yellow X on square
		pixmap.setColor(1,1,0,1);
		pixmap.drawLine(0, 0, width, height);
		pixmap.drawLine(width, 0, 0, height);
		
		//Draw cyan border on square
		pixmap.setColor(0,1,1,1);
		pixmap.drawRectangle(0, 0, width, height);
		
		return pixmap;
	}
	
	/**
	 * This method handles any input and performs actions, such as moving the selected object,
	 * selecting another object, moving the camera, and zooming the camera
	 * 
	 * @param deltaTime
	 */
	private void handleDebugInput(float deltaTime)
	{
		if(Gdx.app.getType() != ApplicationType.Desktop)
		{
			return;
		}
		
		//Controls for camera movement
		float camMoveSpeed = 5*deltaTime;
		float camMoveSpeedAccelerationFactor = 5;
		if(Gdx.input.isKeyPressed(Keys.SHIFT_LEFT))
		{
			camMoveSpeed *= camMoveSpeedAccelerationFactor;
		}
		if(Gdx.input.isKeyPressed(Keys.LEFT))
		{
			moveCamera(-camMoveSpeed,0);
		}
		if(Gdx.input.isKeyPressed(Keys.RIGHT))
		{
			moveCamera(camMoveSpeed,0);
		}
		if(Gdx.input.isKeyPressed(Keys.UP))
		{
			moveCamera(0,camMoveSpeed);
		}
		if(Gdx.input.isKeyPressed(Keys.DOWN))
		{
			moveCamera(0,-camMoveSpeed);
		}
		if(Gdx.input.isKeyPressed(Keys.BACKSPACE))
		{
			cameraHelper.setPosition(0,0);
		}
		
		//Controls for camera zooming
		float camZoomSpeed = 1*deltaTime;
		float camZoomSpeedAccelerationFactor = 5;
		
		if(Gdx.input.isKeyPressed(Keys.SHIFT_LEFT))
		{
			camZoomSpeed *= camZoomSpeedAccelerationFactor;
		}
		if(Gdx.input.isKeyPressed(Keys.COMMA))
		{
			cameraHelper.addZoom(camZoomSpeed);
		}
		if(Gdx.input.isKeyPressed(Keys.PERIOD))
		{
			cameraHelper.addZoom(-camZoomSpeed);
		}
		if(Gdx.input.isKeyPressed(Keys.SLASH))
		{
			cameraHelper.setZoom(1);
		}
	}
	
	/**
	 * This method moves the camera based on the parameters x and y
	 */
	private void moveCamera(float x, float y)
	{
		x += cameraHelper.getPosition().x;
		y += cameraHelper.getPosition().y;
		cameraHelper.setPosition(x, y);
	}
	
	/**
	 * Overrides default input for keyUp so that it resets the game world
	 */
	@Override
	public boolean keyUp(int keycode)
	{
		//Reset game world
		if(keycode == Keys.R)
		{
			init();
			Gdx.app.debug(TAG, "Game world reset.");
		}
		
		return false;
	}
	
	/**
	 * Updates the world relative to the previous update
	 * 
	 * @param deltaTime
	 */
	public void update (float deltaTime)
	{
		handleDebugInput(deltaTime);
		cameraHelper.update(deltaTime);
	}
}
