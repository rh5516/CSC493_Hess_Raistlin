package gui;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import game.Assets;
import utilities.AudioManager;
import utilities.CharacterSkin;
import utilities.Constants;
import utilities.GamePreferences;

/**
 * This is a nice menu screen with a nice background image and
 * different choices for the player (i.e. Play, Options) 
 * 
 * @author Raistlin Hess
 *
 */
public class MenuScreen extends AbstractGameScreen
{
	private static final String TAG = MenuScreen.class.getName();
	private Stage stage;
	private Skin skinMelonMan;
	private Skin skinLibgdx;
	//Menu
	private Image imgBackground;
	private Image imgLogo;
	private Image imgInfo;
	private Image imgCoins;
	private Image imgBunny;
	private Button btnMenuPlay;
	private Button btnMenuOptions;
	//Options
	private Window winOptions;
	private TextButton btnWinOptSave;
	private TextButton btnWinOptCancel;
	private CheckBox chkSound;
	private Slider sldSound;
	private CheckBox chkMusic;
	private Slider sldMusic;
	private SelectBox<CharacterSkin> selCharSkin;
	private Image imgCharSkin;
	private CheckBox chkShowFpsCounter;
	//Debug
	private final float DEBUG_REBUILD_INTERVAL = 5.0f;
	private boolean debugEnabled = false;
	private float debugRebuildStage;
	
	public MenuScreen(Game game)
	{
		super(game);
	}
	
	/**
	 * This loads the preference settings from an GamePreference instance and
	 * applies them to this class
	 */
	private void loadSettings()
	{
		GamePreferences prefs = GamePreferences.instance;
		prefs.loadPrefs();
		chkSound.setChecked(prefs.sound);
		sldSound.setValue(prefs.volSound);
		chkMusic.setChecked(prefs.music);
		sldMusic.setValue(prefs.volMusic);
		selCharSkin.setSelectedIndex(prefs.charSkin);
		onCharSkinSelected(prefs.charSkin);
		chkShowFpsCounter.setChecked(prefs.showFpsCounter);
	}
	
	/**
	 * This sends the current settings to the GamePreferences to save them to
	 * the preferences file
	 */
	private void saveSettings()
	{
		GamePreferences prefs = GamePreferences.instance;
		prefs.sound = chkSound.isChecked();
		prefs.volSound = sldSound.getValue();
		prefs.music = chkMusic.isChecked();
		prefs.volMusic = sldMusic.getValue();
		prefs.charSkin = selCharSkin.getSelectedIndex();
		prefs.showFpsCounter = chkShowFpsCounter.isChecked();
		prefs.savePrefs();
	}
	
	/**
	 * This sets the player's skin based on the index that was chosen
	 * 
	 * @param index
	 */
	private void onCharSkinSelected(int index)
	{
		CharacterSkin skin = CharacterSkin.values()[index];
		imgCharSkin.setColor(skin.getColor());
	}
	
	/**
	 * This calls the saveSettings() method and then exits the settings menu
	 */
	private void onSaveClicked()
	{
		saveSettings();
		AudioManager.instance.onSettingsUpdated();
		onCancelClicked();
	}
	
	/**
	 * This exits the settings menu and displays the default main menu screen
	 */
	private void onCancelClicked()
	{
		showMenuButtons(true);
		showOptionsWindow(false, true);
		AudioManager.instance.onSettingsUpdated();
	}
	
	/**
	 * Animate the options menu opening and closing
	 */
	private void showMenuButtons(boolean visible)
	{
		float moveDuration = 1.0f;
		Interpolation moveEasing = Interpolation.swing;
		float delayOptionsButton = 0.25f;

		float moveX = 300 * (visible ? -1 : 1);
		float moveY = 0 * (visible ? -1 : 1);
		final Touchable touchEnabled = visible ? Touchable.enabled : Touchable.disabled;
		btnMenuPlay.addAction(moveBy(moveX, moveY, moveDuration, moveEasing));
		btnMenuOptions.addAction(sequence(delay(delayOptionsButton), moveBy(moveX, moveY, moveDuration, moveEasing)));

		SequenceAction seq = sequence();
		if (visible)
			seq.addAction(delay(delayOptionsButton + moveDuration));
		seq.addAction(run(new Runnable()
		{
			public void run()
			{
				btnMenuPlay.setTouchable(touchEnabled);
				btnMenuOptions.setTouchable(touchEnabled);
			}
		}));
		stage.addAction(seq);
	}
	
	/**
	 * Gradually fades the options window
	 */
	private void showOptionsWindow(boolean visible, boolean animated)
	{
		float alphaTo = visible ? 0.8f : 0.0f;
		float duration = animated ? 1.0f : 0.0f;
		Touchable touchEnabled = visible ? Touchable.enabled : Touchable.disabled;
		winOptions.addAction(sequence(
				touchable(touchEnabled),
				alpha(alphaTo, duration)
				));
	}
	
	/**
	 * Set up all of the objects that will be assembled to create the menus screen
	 */
	private void rebuildStage()
	{
		skinMelonMan = new Skin(Gdx.files.internal(Constants.SKIN_MELONMAN_UI), new TextureAtlas(Constants.TEXTURE_ATLAS_UI));
		skinLibgdx = new Skin(Gdx.files.internal(Constants.SKIN_LIBGDX_UI), new TextureAtlas(Constants.TEXTURE_ATLAS_LIBGDX_UI));
		
		//Build all layers 
		Table layerBackground = buildBackgroundLayer();
		Table layerObjects = buildObjectsLayer();
		Table layerLogos = buildLogosLayer();
		Table layerControls = buildControlsLayer();
		Table layerOptionsWindow = buildOptionsWindowLayer();
		
		//Assemble stage in menu screen
		stage.clear();
		Stack stack = new Stack();
		stage.addActor(stack);
		stack.setSize(Constants.VIEWPORT_GUI_WIDTH,Constants.VIEWPORT_GUI_HEIGHT);
		stack.add(layerBackground);
		stack.add(layerObjects);
		stack.add(layerLogos);
		stack.add(layerControls);
		stage.addActor(layerOptionsWindow);
	}
	
	/**
	 * Returns a new Table with the background image in it
	 */
	private Table buildBackgroundLayer()
	{
		Table layer = new Table();
		
		// + Background
		imgBackground = new Image(skinMelonMan, "background");
		layer.add(imgBackground);
		
		return layer;
	}
	
	/**
	 * Returns a new Table with the coins and bunny in it
	 */
	private Table buildObjectsLayer()
	{
		Table layer = new Table();
		
		// + Coins
		imgCoins = new Image(skinMelonMan, "coins");
		layer.addActor(imgCoins);
		imgCoins.setPosition(135, 80);
		
		// + Bunny
		imgBunny = new Image(skinMelonMan, "bunny");
		layer.addActor(imgBunny);
		imgBunny.setPosition(355, 40);
		
		return layer;
	}
	
	/**
	 * Returns a new Table with the game logos in it
	 */
	private Table buildLogosLayer()
	{
		Table layer = new Table();
		layer.left().top();
		
		// + Game Logo
		imgLogo = new Image(skinMelonMan, "logo");
		layer.add(imgLogo);
		layer.row().expandY();
		
		// + Info logos
		imgInfo = new Image(skinMelonMan, "info");
		layer.add(imgInfo).bottom();
		if(debugEnabled) layer.debug();
		
		return layer;
	}
	
	/**
	 * Returns a new Table
	 */
	private Table buildControlsLayer()
	{
		Table layer = new Table();
		layer.right().bottom();
		
		// + Play Button
		btnMenuPlay = new Button(skinMelonMan, "play");
		layer.add(btnMenuPlay);
		btnMenuPlay.addListener(
			new ChangeListener()
			{
				@Override
				public void changed(ChangeEvent event, Actor actor)
				{
					onPlayClicked();
				}
			}
		);
		// + Options Button
		btnMenuOptions = new Button(skinMelonMan, "options");
		layer.add(btnMenuOptions);
		btnMenuOptions.addListener(
			new ChangeListener()
			{
				@Override
				public void changed(ChangeEvent event, Actor actor)
				{
					onOptionsClicked();
				}
			}
		);
		if(debugEnabled) layer.debug();
		
		return layer;
	}
	
	/**
	 * Returns a table that has all of the checkboxes and sliders for 
	 * sound and music
	 */
	private Table buildOptWinAudioSettings()
	{
		Table tbl = new Table();
		
		// + Title: "Audio"
		tbl.pad(10, 10, 0, 10);
		tbl.add(new Label("Audio", skinLibgdx, "default-font", Color.ORANGE)).colspan(3);
		tbl.row();
		tbl.columnDefaults(0).padRight(10);
		tbl.columnDefaults(1).padRight(10);
		
		// + Checkbox, "Sound", volSound slider
		chkSound = new CheckBox("", skinLibgdx);
		tbl.add(chkSound);
		tbl.add(new Label("Sound", skinLibgdx));
		sldSound = new Slider(0, 1, 0.1f, false, skinLibgdx);
		tbl.add(sldSound);
		tbl.row();
		
		//+ Checkbox, "Music", volMusic slider
		chkMusic = new CheckBox("", skinLibgdx);
		tbl.add(chkMusic);
		tbl.add(new Label("Music", skinLibgdx));
		sldMusic = new Slider(0, 1, 0.1f, false, skinLibgdx);
		tbl.add(sldMusic);
		tbl.row();
		
		return tbl;
	}
	
	/**
	 * Returns a table containing choices for the player's skin, as well as
	 * a preview of them 
	 */
	private Table buildOptWinSkinSelection()
	{
		Table tbl = new Table();
		
		// + Title "Character Skin"
		tbl.pad(10, 10, 0, 10);
		tbl.add(new Label("Character Skin", skinLibgdx, "default-font", Color.ORANGE)).colspan(2);
		tbl.row();
		
		// + Drop down box filled with skins
		selCharSkin = new SelectBox<CharacterSkin>(skinLibgdx);
		selCharSkin.setItems(CharacterSkin.values());
		selCharSkin.addListener(
			new ChangeListener()
			{
				@Override
				public void changed(ChangeEvent event, Actor actor)
				{
					onCharSkinSelected(((SelectBox<CharacterSkin>)actor).getSelectedIndex());
				}
			}
		);
		tbl.add(selCharSkin).width(120).padRight(20);
		
		// + Skin preview image
		imgCharSkin = new Image(Assets.instance.melonMan.head);
		tbl.add(imgCharSkin).width(50).height(50);
		
		return tbl;
	}
	
	/**
	 * Returns a new Table
	 */
	private Table buildOptionsWindowLayer()
	{
		winOptions = new Window("Options", skinLibgdx);
		
		// + Audio settings: Sound/music checkbox and sliders
		winOptions.add(buildOptWinAudioSettings()).row();
		
		// + Character skin: selection box (white, gray, brown)
		winOptions.add(buildOptWinSkinSelection()).row();
		
		// + Debug: Show fps
		winOptions.add(buildOptWinDebug()).row();
		
		// + Separator and buttons (save, cancel)
		winOptions.add(buildOptWinButtons()).pad(10, 0, 10, 0);
		
		//Make options window slightly transparent
		winOptions.setColor(1, 1, 1, 0.8f);
		
		//Hide options window by default
//		winOptions.setVisible(false);
		showOptionsWindow(false, false);
		if(debugEnabled) winOptions.debug();
		
		//Let TableLayout recalculate widget sizes and positions
		winOptions.pack();
		
		//Move options window to bottom right corner
		winOptions.setPosition(Constants.VIEWPORT_GUI_WIDTH-winOptions.getWidth()-50, 50);
		
		return winOptions;
	}
	
	/**
	 * This returns a Table with checkboxes for toggling the FPS counter
	 */
	private Table buildOptWinDebug()
	{
		Table tbl = new Table();
		
		// + Title "Debug"
		tbl.pad(10, 10, 0, 10);
		tbl.add(new Label("Debug", skinLibgdx, "default-font", Color.RED)).colspan(3);
		tbl.row();
		tbl.columnDefaults(0).padRight(10);
		tbl.columnDefaults(1).padRight(10);
		
		// Checkbox "Show FPS Counter"
		chkShowFpsCounter = new CheckBox("", skinLibgdx);
		tbl.add(new Label("Show FPS Counter", skinLibgdx));
		tbl.add(chkShowFpsCounter);
		tbl.row();
		
		return tbl;
	}
	
	/**
	 * Returns a Table containing the buttons for the Options window and sets
	 * up an action listener
	 */
	private Table buildOptWinButtons()
	{
		Table tbl = new Table();
		
		// + Separator
		Label lbl = null;
		lbl = new Label("", skinLibgdx);
		lbl.setColor(0.75f, 0.75f, 0.75f, 1);
		lbl.setStyle(new LabelStyle(lbl.getStyle()));
		lbl.getStyle().background = skinLibgdx.newDrawable("white");
		tbl.add(lbl).colspan(2).height(1).width(220).pad(0, 0, 0, 1);
		tbl.row();
		
		lbl = new Label("", skinLibgdx);
		lbl.setColor(0.5f, 0.5f, 0.5f, 1);
		lbl.setStyle(new LabelStyle(lbl.getStyle()));
		lbl.getStyle().background = skinLibgdx.newDrawable("white");
		tbl.add(lbl).colspan(2).height(1).width(220).pad(0, 1, 5, 0);
		tbl.row();
		
		// + Save button with event handler
		btnWinOptSave = new TextButton("Save", skinLibgdx);
		tbl.add(btnWinOptSave).padRight(30);
		btnWinOptSave.addListener(
			new ChangeListener()
			{
				@Override
				public void changed(ChangeEvent event, Actor actor)
				{
					onSaveClicked();
				}
			}
		);
		
		// + Cancel Button with event handler
		btnWinOptCancel = new TextButton("Canel", skinLibgdx);
		tbl.add(btnWinOptCancel);
		btnWinOptCancel.addListener(
			new ChangeListener()
			{
				@Override
				public void changed(ChangeEvent event, Actor actor)
				{
					onCancelClicked();
				}
			}
		);
		
		return tbl;
	}
	
	/**
	 * Method to handle the Play button being clicked
	 */
	private void onPlayClicked()
	{
		game.setScreen(new GameScreen(game));
	}
	
	/**
	 * Method to handle the Options button being clicked
	 */
	private void onOptionsClicked()
	{
		loadSettings();
		showMenuButtons(false);
		showOptionsWindow(true, true);
		AudioManager.instance.onSettingsUpdated();
	}
	
	/**
	 * Displays a nice image for the menu screen.
	 * If debugEnabled is set to true, a debug visuals will be drawn also
	 */
	@Override
	public void render(float deltaTime)
	{
		Gdx.gl.glClearColor(0,0,0,1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		if(debugEnabled)
		{
			debugRebuildStage -= deltaTime;
			if(debugRebuildStage <= 0)
			{
				debugRebuildStage = DEBUG_REBUILD_INTERVAL;
				rebuildStage();
			}
		}
		stage.act(deltaTime);
		stage.draw();
//		stage.setDebugAll(true);
	}

	/**
	 * Scales the menu screen and its objects based on the window size
	 */
	@Override
	public void resize(int width, int height)
	{
		stage.getViewport().update(width, height, true);
	}
	
	/**
	 * Creates a new stage and rebuilds the menu screen
	 */
	@Override
	public void show()
	{
		stage = new Stage(new StretchViewport(Constants.VIEWPORT_GUI_WIDTH,Constants.VIEWPORT_GUI_HEIGHT));
		Gdx.input.setInputProcessor(stage);
		rebuildStage();
	}
	
	/**
	 * Cleans up the stage and textures from memory
	 */
	@Override
	public void hide()
	{
		stage.dispose();
		skinMelonMan.dispose();
		skinLibgdx.dispose();
	}
	
	@Override
	public void pause(){};
}
