package gui;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import game.WorldController;
import game.WorldRenderer;
import utilities.GamePreferences;

/**
 * This class represents the game world and handles rendering
 * 
 * @author Raistlin Hess
 *
 */
public class GameScreen extends AbstractGameScreen
{
	private static final String TAG = GameScreen.class.getName();
	private WorldController worldController;
	private WorldRenderer worldRenderer;
	private boolean paused;
	
	public GameScreen(Game game)
	{
		super(game);
	}
	
	/**
	 * Renders the game screen to the application windows if it is in focus
	 */
	@Override
	public void render(float deltaTime)
	{
		//Do not update game world when paused
		if(!paused)
		{
			//Update game world by the time that has passed since last
			//rendered frame
			worldController.update(deltaTime);
		}
		
		//Sets the clear screen color
		Gdx.gl.glClearColor(0x64/255.0f, 0x95/255.0f, 0xed/255.0f, 1);
		
		//Clears the screen
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		//Render game world to screen
		worldRenderer.render();
	}
	
	/**
	 * Resize the game world based on the window dimensions
	 */
	@Override
	public void resize(int width, int height)
	{
		worldRenderer.resize(width, height);
	}
	
	/**
	 * Initializes the level, including loading saved preferences.
	 * Equivalent to CanyonBunnyMain's create()
	 */
	@Override
	public void show()
	{
		GamePreferences.instance.load();
		worldController = new WorldController(game);
		worldRenderer = new WorldRenderer(worldController);
		Gdx.input.setCatchBackKey(true);
	}
	
	/**
	 * Cleans up assets. Equivalent to CanyonBunnyMain's dispose()
	 */
	@Override
	public void hide()
	{
		worldController.dispose();
		worldRenderer.dispose();
		Gdx.input.setCatchBackKey(false);
	}
	
	/**
	 * Pauses game rendering and input handling.
	 * Used for Mobile devices
	 */
	@Override
	public void pause()
	{
		paused = true;
	}
	
	/**
	 * Resumes game rendering and input handling.
	 * Used for Mobile devices
	 */
	@Override
	public void resume()
	{
		paused = false;
	}
}
