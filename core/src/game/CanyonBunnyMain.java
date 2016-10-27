package game;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import gui.MenuScreen;
import utilities.AudioManager;
import utilities.GamePreferences;

/**
 * This class is the heart and soul of the game. 
 * 
 * @author Raistlin Hess
 *
 */
public class CanyonBunnyMain extends Game
{
	@Override
	public void create()
	{
		//Set libGDX log level
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		
		//Load assets
		Assets.instance.init(new AssetManager());
		
		//Load preferences for audio settings and start music
		GamePreferences.instance.load();
		AudioManager.instance.play(Assets.instance.music.song01);
		
		//Start game at menu screen
		setScreen(new MenuScreen(this));
	}
	
}
