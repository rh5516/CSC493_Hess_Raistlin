package com.hess.assignment1;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.utils.Disposable;

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
	
	
	private Assets(){}
	
	/**
	 * Initializes the assetManager and each unique object
	 */
	public void init(AssetManager assetManager)
	{
		this.assetManager = assetManager;
		assetManager.setErrorListener(this);
		assetManager.load(Constants.TEXTURE_ATLAS_OBJECTS,TextureAtlas.class);
		assetManager.finishLoading();
		Gdx.app.debug(TAG, "# of assets loaded: "+assetManager.getAssetNames().size);
		for(String a: assetManager.getAssetNames())
		{
			Gdx.app.debug(TAG, "asset: "+a);
		}
		
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
		public AssetBunny(TextureAtlas atlas)
		{
			head = atlas.findRegion("bunny_head");
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
		public AssetGoldCoin(TextureAtlas atlas)
		{
			goldCoin = atlas.findRegion("item_gold_coin");
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
	
	public class AssetLevelDecoration
	{
		public final AtlasRegion cloud01;
		public final AtlasRegion cloud02;
		public final AtlasRegion cloud03;
		public final AtlasRegion mountainLeft;
		public final AtlasRegion mountainRight;
		public final AtlasRegion waterOverlay;
		public AssetLevelDecoration(TextureAtlas atlas)
		{
			cloud01 = atlas.findRegion("cloud01");
			cloud02 = atlas.findRegion("cloud02");
			cloud03 = atlas.findRegion("cloud03");
			mountainLeft = atlas.findRegion("mountain_left");
			mountainRight = atlas.findRegion("mountain_right");
			waterOverlay = atlas.findRegion("water_overlay");
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
			defaultSmall = new BitmapFont(Gdx.files.internal("../core/assets/images/arial-15.fnt"),true);
			defaultNormal = new BitmapFont(Gdx.files.internal("../core/assets/images/arial-15.fnt"),true);
			defaultBig = new BitmapFont(Gdx.files.internal("../core/assets/images/arial-15.fnt"),true);
			
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
}
