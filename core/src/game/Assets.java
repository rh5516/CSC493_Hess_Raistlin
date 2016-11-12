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
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import utilities.Constants;

/**
 * This class is responsible for defining the various objects that will be placed in the game
 * 
 * @author Raistlin Hes
 * 
 */
public class Assets implements Disposable, AssetErrorListener
{
	public static final String TAG = Assets.class.getName();
	public static final Assets instance = new Assets();
	private AssetManager assetManager;
	public AssetFonts fonts;
	public AssetBunny bunny;
	public AssetRock rock;
	public AssetGoldCoin goldCoin;
	public AssetFeather feather;
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
		assetManager.load("sounds/jump_with_feather.wav", Sound.class);
		assetManager.load("sounds/pickup_coin.wav", Sound.class);
		assetManager.load("sounds/pickup_feather.wav", Sound.class);
		assetManager.load("sounds/live_lost.wav", Sound.class);
		
		//Load music
		assetManager.load("music/keith303_-_brand_new_highscore.mp3", Music.class);
		
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
		bunny = new AssetBunny(atlas);
		rock = new AssetRock(atlas);
		goldCoin = new AssetGoldCoin(atlas);
		feather = new AssetFeather(atlas);
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
	public class AssetBunny
	{
		public final AtlasRegion head;
		public final Animation animNormal;
		public final Animation animCopterTransform;
		public final Animation animCopterTransformBack;
		public final Animation animCopterRotate;
		public AssetBunny(TextureAtlas atlas)
		{
			head = atlas.findRegion("bunny_head");
			
			Array<AtlasRegion> regions = null;
			AtlasRegion region = null;
			
			//Animation: Bunny Normal
			regions = atlas.findRegions("anim_bunny_normal");
			animNormal = new Animation(1.0f/10.0f, regions, Animation.PlayMode.LOOP_PINGPONG);
			
			//Animation: Bunny Copter - knot ears
			regions = atlas.findRegions("anim_bunny_copter");
			animCopterTransform = new Animation(1.0f/10.0f, regions);
			
			//Animation: Bunny Copter - unknot ears
			regions = atlas.findRegions("anim_bunny_copter");
			animCopterTransformBack = new Animation(1.0f/10.0f, regions, Animation.PlayMode.REVERSED);
			
			//Animation: Bunny Copter - rotate ears
			regions = new Array<AtlasRegion>();
			regions.add(atlas.findRegion("anim_bunny_copter", 4));
			regions.add(atlas.findRegion("anim_bunny_copter", 5));
			animCopterRotate = new Animation(1.0f/15.0f, regions);
		}
	}
	
	public class AssetRock
	{
		public final AtlasRegion edge;
		public final AtlasRegion middle;
		public AssetRock(TextureAtlas atlas) 
		{
			edge = atlas.findRegion("rock_edge");
			middle = atlas.findRegion("rock_middle");
		}
	}
	
	public class AssetGoldCoin
	{
		public final AtlasRegion goldCoin;
		public final Animation animGoldCoin;
		public AssetGoldCoin(TextureAtlas atlas)
		{
			goldCoin = atlas.findRegion("item_gold_coin");
			
			//Animation: Gold Coin
			Array<AtlasRegion> regions = atlas.findRegions("anim_gold_coin");
			AtlasRegion region = regions.first();
			for(int i = 0; i < 10; i++)
			{
				regions.insert(0, region);
			}
			animGoldCoin = new Animation(1.0f/20.0f, regions, Animation.PlayMode.LOOP_PINGPONG);
		}
	}
	
	public class AssetFeather
	{
		public final AtlasRegion feather;
		public AssetFeather (TextureAtlas atlas)
		{
			feather = atlas.findRegion("item_feather");
		}
	}
	
	/**
	 * This class loads images from the atlas and stores them in instance variables.
	 * These are all assets that are part of the background
	 * 
	 * @author Raistlin Hess
	 *
	 */
	public class AssetLevelDecoration
	{
		public final AtlasRegion cloud01;
		public final AtlasRegion cloud02;
		public final AtlasRegion cloud03;
		public final AtlasRegion mountainLeft;
		public final AtlasRegion mountainRight;
		public final AtlasRegion waterOverlay;
		public final AtlasRegion carrot;
		public final AtlasRegion goal;
		
		public AssetLevelDecoration(TextureAtlas atlas)
		{
			cloud01 = atlas.findRegion("cloud01");
			cloud02 = atlas.findRegion("cloud02");
			cloud03 = atlas.findRegion("cloud03");
			mountainLeft = atlas.findRegion("mountain_left");
			mountainRight = atlas.findRegion("mountain_right");
			waterOverlay = atlas.findRegion("water_overlay");
			carrot = atlas.findRegion("carrot");
			goal = atlas.findRegion("goal");
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
		public final Sound jumpWithFeather;
		public final Sound pickupCoin;
		public final Sound pickupFeather;
		public final Sound liveLost;
		
		public AssetSounds(AssetManager am)
		{
			jump = am.get("sounds/jump.wav", Sound.class);
			jumpWithFeather = am.get("sounds/jump_with_feather.wav", Sound.class);
			pickupCoin = am.get("sounds/pickup_coin.wav", Sound.class);
			pickupFeather = am.get("sounds/pickup_feather.wav", Sound.class);
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
			song01 = am.get("music/keith303_-_brand_new_highscore.mp3", Music.class);
		}
	}
}
