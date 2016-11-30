package utilities;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.math.MathUtils;

/**
 * Handles loading and saving changes to a preference file
 * 
 * @author Raistlin Hess
 *
 */
public class GamePreferences
{
	public static final String TAG = GamePreferences.class.getName();
	public static final GamePreferences instance = new GamePreferences();
	public boolean sound;
	public boolean music;
	public float volSound;
	public float volMusic;
	public int charSkin;
	public boolean showFpsCounter;
	private Preferences prefs;
	public Preferences scores;
	
	/**
	 * Singleton - Prevent instantiation from other classes
	 */
	private GamePreferences()
	{
		prefs = Gdx.app.getPreferences(Constants.PREFERENCES);
		scores = Gdx.app.getPreferences(Constants.HIGHSCORES);
		
		//Set up scores if necessary
		if(scores.getInteger("1") == 0)
			
		{
			for(int i = 1; i < 11; i++)
			{
				scores.putInteger(""+i, 0);
			}
		}
	}
	
	/**
	 * This method loads all of the settings out of the preferences file
	 */
	public void loadPrefs()
	{
		sound = prefs.getBoolean("sound", true);
		music = prefs.getBoolean("music", true);
		volSound = MathUtils.clamp(prefs.getFloat("volSound", 0.5f), 0.0f, 1.0f);
		volMusic = MathUtils.clamp(prefs.getFloat("volSound", 0.5f), 0.0f, 1.0f);
		charSkin = MathUtils.clamp(prefs.getInteger("charSkin", 0), 0, 2);
		showFpsCounter = prefs.getBoolean("showFpsCounter", false);
	}
	
	/**
	 * This method saves any changes done in the Options menu to the preferences file
	 */
	public void savePrefs()
	{
		prefs.putBoolean("sound", sound);
		prefs.putBoolean("music", music);
		prefs.putFloat("volSound", volSound);
		prefs.putFloat("volMusic", volMusic);
		prefs.putInteger("charSkin", charSkin);
		prefs.putBoolean("showFpsCounter", showFpsCounter);
		prefs.flush();
	}
	
	/**
	 * When the user reaches a game over, their score is passed into this.
	 * All of the scores are then read from the highscores list, and orders
	 * them in descending order
	 */
	public void updateScores(int score)
	{
		//Get all scores
		int i = 0;
		int scores[] = new int[11];
		while(i < scores.length-1)
		{
			scores[i] = this.scores.getInteger(""+i);
			i++;
		}
		scores[10] = score;
		
		//Sort in descending order
		boolean done = false;
		while(!done)
		{
			done = true;
			for(int j = 0; j < scores.length-1; j++)
			{
				if(scores[j] < scores[j+1])
				{
					int tmp = scores[j];
					scores[j] = scores[j+1];
					scores[j+1] = tmp;
					done = false;
				}
			}
		}
		
		//Replace scores in highscores list
		for(i = 1; i < 11; i++)
		{
			this.scores.putInteger(""+i, scores[i-1]);
		}
		this.scores.flush();
	}
}

