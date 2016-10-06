package game;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import gui.MenuScreen;

/**
 * This class is the heart and soul of the game. 
 * 
 * @author Raistlin Hess
 *
 */
public class MelonManMain extends Game
{
	@Override
	public void create()
	{
		//Set libGDX log level
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		
		//Load assets
		Assets.instance.init(new AssetManager());
		
		//Start game at menu screen
		setScreen(new MenuScreen(this));
	}
}
