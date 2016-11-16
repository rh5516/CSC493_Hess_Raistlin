package game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import utilities.Constants;

/**
 * This class is responsible for defining the various objects that will be placed in the game
 * 
 * @author Raistlin Hess
 * 
 */
public class Assets implements Disposable, AssetErrorListener
{
	public static final String TAG = Assets.class.getName();
	public static final Assets instance = new Assets();
	private AssetManager assetManager;
	public AssetFonts fonts;
	public AssetMelonMan melonMan;
	public AssetRat rat;
	public AssetGround ground;
	public AssetRain rain;
	public AssetStar star;
	public AssetNextLevel nextLevel;
	public AssetLevelDecoration levelDecoration;
	public AssetSounds sounds;
	public AssetMusic music;
	
	private Assets(){}
	
	/**
	 * Initializes the assetManager and each unique object
	 */
	public void init(AssetManager assetManager)
	{
		this.assetManager = assetManager;
		
		//Set asset manager error handler
		assetManager.setErrorListener(this);
		
		//Load texture atlas
		assetManager.load(Constants.TEXTURE_ATLAS_OBJECTS,TextureAtlas.class);
		
		//Load sounds
		assetManager.load("sounds/jump.wav", Sound.class);
		assetManager.load("sounds/pickup_rain.wav", Sound.class);
		assetManager.load("sounds/pickup_star.wav", Sound.class);
		assetManager.load("sounds/live_lost.wav", Sound.class);
		
		//Load music
		assetManager.load("music/desert_bgm.mp3", Music.class);
		
		//Start loading assets and wait until finished
		assetManager.finishLoading();
		Gdx.app.debug(TAG, "# of assets loaded: "+assetManager.getAssetNames().size);
		for(String a: assetManager.getAssetNames())
		{
			Gdx.app.debug(TAG, "asset: "+a);
		}
		
		//Enable texture filtering for pixel smoothing
		TextureAtlas atlas = assetManager.get(Constants.TEXTURE_ATLAS_OBJECTS);
		for(Texture t: atlas.getTextures())
		{
			t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		}
		
		//Create game resource objects
		fonts = new AssetFonts();
		melonMan = new AssetMelonMan(atlas);
		rat = new AssetRat(atlas);
		ground = new AssetGround(atlas);
		rain = new AssetRain(atlas);
		star = new AssetStar(atlas);
		nextLevel = new AssetNextLevel(atlas);
		levelDecoration = new AssetLevelDecoration(atlas);
		sounds = new AssetSounds(assetManager);
		music = new AssetMusic(assetManager);
	}

	/**
	 * Tells assetManager to clean up 
	 */
	@Override
	public void dispose()
	{
		assetManager.dispose();
		fonts.defaultSmall.dispose();
		fonts.defaultNormal.dispose();
		fonts.defaultBig.dispose();
	}

	/**
	 * Prints out an error message along with the asset responsible
	 * 
	 * @param filename
	 * @param type
	 * @param throwable
	 */
//	@Override
	public void error(String filename, Class type, Throwable throwable)
	{
		Gdx.app.error(TAG, "Couldn't load asset '"+filename+"'",(Exception)throwable);
	}
	
	/**
	 * Prints out an error message along with the asset responsible
	 * 
	 * @param filename
	 * @param type
	 * @param throwable
	 */
	@Override
	public void error(AssetDescriptor asset, Throwable throwable)
	{
		Gdx.app.error(TAG, "Couldn't load asset '"+asset.fileName+"'",(Exception)throwable);
	}
	
	/**
	 * The following methods define what make up a game object, as well as pulling
	 * their texures out of the TextureAtlas
	 *
	 */
	
	public class AssetMelonMan
	{
		public final AtlasRegion head;
		public AssetMelonMan(TextureAtlas atlas)
		{
			head = atlas.findRegion("player");
		}
	}
	
	public class AssetRat
	{
		public final AtlasRegion head;
		public AssetRat(TextureAtlas atlas)
		{
			head = atlas.findRegion("rat");
		}
	}
	
	public class AssetGround
	{
		public final AtlasRegion edge;
		public AssetGround(TextureAtlas atlas) 
		{
			edge = atlas.findRegion("filler_sand");
		}
	}
	
	public class AssetRain
	{
		public final AtlasRegion rain;
		public AssetRain(TextureAtlas atlas)
		{
			rain = atlas.findRegion("rain");
		}
	}
	
	public class AssetStar
	{
		public final AtlasRegion star;
		public AssetStar (TextureAtlas atlas)
		{
			star = atlas.findRegion("star");
		}
	}
	
	public class AssetNextLevel
	{
		public final Animation animNextLevel;
		
		public AssetNextLevel(TextureAtlas atlas)
		{
			//Animations for next level post
			Array<AtlasRegion> regions = atlas.findRegions("nextLevel");
			
			animNextLevel = new Animation(1.0f/5.0f, regions, Animation.PlayMode.LOOP);
		}
	}
	
	public class AssetLevelDecoration
	{
		public final AtlasRegion cloud01;
		public final AtlasRegion cloud02;
		public final AtlasRegion pyramidFar;
		public final AtlasRegion pyramidNear;
		public final AtlasRegion cactus;
		public final AtlasRegion desertBG;
		public final AtlasRegion fg_sand;
		public final Animation sun;
		
		public AssetLevelDecoration(TextureAtlas atlas)
		{
			cloud01 = atlas.findRegion("cloud01");
			cloud02 = atlas.findRegion("cloud02");
			pyramidFar = atlas.findRegion("bg_far");
			pyramidNear = atlas.findRegion("bg_mid");
			cactus = atlas.findRegion("bg_close");
			desertBG = atlas.findRegion("desert_bg");
			fg_sand = atlas.findRegion("fg_sand");
			Array<AtlasRegion> regions = atlas.findRegions("sun");
			sun = new Animation(1.0f/8.0f, regions, Animation.PlayMode.LOOP_PINGPONG);
		}
	}
	
	/**
	 * The following class loads the fonts necessary for the GUI
	 * 
	 * @author Raistlin Hess
	 *
	 */
	public class AssetFonts
	{
		public final BitmapFont defaultSmall;
		public final BitmapFont defaultNormal;
		public final BitmapFont defaultBig;
		
		/**
		 * This initializes three different sizes of the same font and lineraly filters them
		 */
		public AssetFonts()
		{
			//Create three fonts using libGDX's 15px bitmap font
			defaultSmall = new BitmapFont(Gdx.files.internal("images/arial-15.fnt"),true);
			defaultNormal = new BitmapFont(Gdx.files.internal("images/arial-15.fnt"),true);
			defaultBig = new BitmapFont(Gdx.files.internal("images/arial-15.fnt"),true);
			
			//Set font sizes
			defaultSmall.getData().setScale(0.75f);
			defaultNormal.getData().setScale(1.0f);
			defaultBig.getData().setScale(2.0f);
			
			//Enable linear texture filtering to smoothen fonts
			defaultSmall.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
			defaultNormal.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
			defaultBig.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		}
	}
	
	/**
	 * This class attaches all of the sound effects for the game to an instance
	 * variable for easy calling and manipulation
	 * 
	 * @author Raistlin Hess
	 *
	 */
	public class AssetSounds
	{
		public final Sound jump;
		public final Sound pickupRain;
		public final Sound pickupStar;
		public final Sound liveLost;
		
		public AssetSounds(AssetManager am)
		{
			jump = am.get("sounds/jump.wav", Sound.class);
			pickupRain = am.get("sounds/pickup_rain.wav", Sound.class);
			pickupStar = am.get("sounds/pickup_star.wav", Sound.class);
			liveLost = am.get("sounds/live_lost.wav", Sound.class);
		}
	}
	
	/**
	 * This class attaches all of the music for the game to an instance
	 * variable for easy calling and manipulation
	 * 
	 * @author Raistlin Hess
	 *
	 */
	public class AssetMusic
	{
		public final Music song01;
		
		public AssetMusic(AssetManager am)
		{
			song01 = am.get("music/desert_bgm.mp3", Music.class);
		}
	}
}
