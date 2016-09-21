package com.hess.assignment1;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;

/**
 * This class is the heart and soul of the game. 
 * 
 * @author Raistlin Hess
 *
 */
public class MelonManMain implements ApplicationListener
{
	private static final String TAG = MelonManMain.class.getName();
	private WorldController worldController;
	private WorldRenderer worldRenderer;
	private boolean paused;
	
	/**
	 * This method initializes necessary classes, including AssetManager, WorldController, and WorldRenderer
	 */
	@Override
	public void create()
	{
		//Set log level to debug
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		
		Assets.instance.init(new AssetManager());
		
		//Init controller and renderer
		worldController = new WorldController();
		worldRenderer = new WorldRenderer(worldController);
		
		//Android: Game world is active on start
		paused = false;
	}
	
	/**
	 * Resizes the game world based on the current window size
	 */
	@Override
	public void resize(int width, int height)
	{
		worldRenderer.resize(width, height);
	}
	
	/**
	 * This draws all of the sprites to the screen and updates any variables
	 */
	@Override
	public void render()
	{
		//Android: Don't update world when paused
		if(!paused)
		{
			//Update the game world based upon the time a frame rendered
			worldController.update(Gdx.graphics.getDeltaTime());
		}
		
		//Sets background clear color to a blue
		Gdx.gl.glClearColor(0x64/255.0f, 0x95/255.0f, 0xed/255.0f, 0xff/255.0f);//100,149,237,255);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		//Render world to the screen
		worldRenderer.render();
	}
	
	/**
	 * Unused method for Android platform
	 */
	@Override
	public void pause()
	{
		paused = true;
	}
	
	/**
	 * Unused method for Android platform
	 */
	@Override
	public void resume()
	{
		paused = false;
	}
	
	/**
	 * Cleans up all assets and the world
	 */
	@Override
	public void dispose()
	{
		worldRenderer.dispose();
		Assets.instance.dispose();
	}
	
}
