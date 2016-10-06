package gui;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import game.Assets;

/**
 * This class allows more control over the game. Instead of implementing the
 * ApplicationListener interface, it will extend the Game class. This allows the
 * ability to change which screen is displayed in the application window.
 * 
 * @author Raistlin Hess
 *
 */
public abstract class AbstractGameScreen implements Screen
{
	protected Game game;
	
	public AbstractGameScreen(Game game)
	{
		this.game = game;
	}
	
	public abstract void render(float deltaTime);
	public abstract void resize(int width, int height);
	public abstract void show();
	public abstract void hide();
	public abstract void pause();
	
	public void resume()
	{
		Assets.instance.init(new AssetManager());
	}
	
	public void dispose()
	{
		Assets.instance.dispose();
	}
}
