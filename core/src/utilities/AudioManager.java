package utilities;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

/**
 * This class is responsible for playing, stopping, and modifying audio
 * for the game. 
 * 
 * @author Raistlin Hess
 *
 */
public class AudioManager
{
	public static final AudioManager instance = new AudioManager();
	private Music playingMusic;
	
	//Create singleton instance
	private AudioManager() {}
	
	/**
	 * Basic play sound at full volume with no transformations
	 */
	public void play(Sound sound)
	{
		play(sound,1);
	}
	
	/**
	 * Basic play sound at given volume with no transformations
	 */
	public void play(Sound sound, float volume)
	{
		play(sound, volume, 1);
	}
	
	/**
	 * Basic play sound at given volume with given pitch
	 */
	public void play(Sound sound, float volume, float pitch)
	{
		play(sound, volume, pitch, 0);
	}
	
	/**
	 * Basic play sound at given volume, pitch, and panning
	 */
	public void play(Sound sound, float volume, float pitch, float pan)
	{
		if(!GamePreferences.instance.sound) return;
		
		sound.play(GamePreferences.instance.volSound*volume, pitch, pan);
	}
	
	/**
	 * Begin playing music after stopping any currently playing music
	 */
	public void play(Music music)
	{
		stopMusic();
		playingMusic = music;
		
		if(GamePreferences.instance.music)
		{
			music.setLooping(true);
			music.setVolume(GamePreferences.instance.volMusic);
			music.play();
		}
	}
	
	/**
	 * If any music is playing, stop it
	 */
	public void stopMusic()
	{
		if(playingMusic != null) playingMusic.stop();
	}
	
	/**
	 * Whenever the settings window has been closed, stop playing the
	 * music and start it again with the new settings for music volume
	 */
	public void onSettingsUpdated()
	{
		if(playingMusic == null) return;
		playingMusic.setVolume(GamePreferences.instance.volMusic);
		
		if(GamePreferences.instance.music)
		{
			if(!playingMusic.isPlaying()) playingMusic.play();
		}
		else
		{
			playingMusic.pause();
		}
	}
}
