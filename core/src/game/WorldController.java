package game;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Rectangle;
import gui.MenuScreen;
import objects.Ground;
import objects.MelonMan;
import objects.MelonMan.JUMP_STATE;
import objects.Rain;
import objects.Star;
import utilities.CameraHelper;
import utilities.Constants;

/**
 * This class is responsible for updating information about the game objects, as well as the camera,
 * and handling input
 * 
 * @author Raistlin Hess
 *
 */
public class WorldController extends InputAdapter
{
	private static final String TAG = WorldController.class.getName();
	private Game game;
	private float timeLeftGameOverDelay;
	//Rectangles for collision detection
	private Rectangle r1 = new Rectangle();
	private Rectangle r2 = new Rectangle();
	public CameraHelper cameraHelper;
	public Level level;
	public int lives;
	public int score;

	public WorldController(Game game)
	{
		this.game = game;
		init();
	}
	
	/**
	 * This method sets up WorldController to accept and process input, initialize the CameraHelper,
	 * and sets the lives and call initLevel()
	 */
	public void init()
	{
		Gdx.input.setInputProcessor(this);
		cameraHelper = new CameraHelper();
		lives = Constants.LIVES_START;
		timeLeftGameOverDelay = 0;
		initLevel();
	}
	
	/**
	 * Sets the score to 0 and loads the first level
	 */
	public void initLevel()
	{
		score = 0;
		level = new Level(Constants.LEVEL_01);
		cameraHelper.setTarget(level.melonMan);
	}
	
	/**
	 * When the player dies, reset his position and the camera, but not his score
	 */
	public void onDeath()
	{
		level = new Level(Constants.LEVEL_01);
		cameraHelper.setTarget(level.melonMan);
	}
	
	/**
	 * Takes the player save the state of the world and return to the menu screen
	 */
	private void backToMenu()
	{
		//Switch to menu screen
		game.setScreen(new MenuScreen(game));
	}
	
	/**
	 * Allows the player to send input commands to MelonMan if the camera is following him
	 * 
	 * @param deltaTime
	 */
	private void handleInputGame(float deltaTime)
	{
		if(cameraHelper.hasTarget(level.melonMan))
		{
			//Player movement
			if(Gdx.input.isKeyPressed(Keys.LEFT))
			{
				level.melonMan.velocity.x = -level.melonMan.terminalVelocity.x;
			}
			else if(Gdx.input.isKeyPressed(Keys.RIGHT))
			{
				level.melonMan.velocity.x = level.melonMan.terminalVelocity.x;
			}
			else
			{
				//Execute auto-forward movement on non-desktop platform
				if(Gdx.app.getType() != ApplicationType.Desktop)
				{
					level.melonMan.velocity.x = level.melonMan.terminalVelocity.x;
				}
			}
			
			//Bunny jump
			if(Gdx.input.isTouched() || Gdx.input.isKeyPressed(Keys.SPACE))
			{
				level.melonMan.setJumping(true);
			}
			else
			{
				level.melonMan.setJumping(false);
			}
		}
	}
	
	/**
	 * This method handles any input and performs actions, such as moving the selected object,
	 * selecting another object, moving the camera, and zooming the camera
	 * 
	 * @param deltaTime
	 */
	private void handleDebugInput(float deltaTime)
	{
		if(Gdx.app.getType() != ApplicationType.Desktop)
		{
			return;
		}
		
		if(!cameraHelper.hasTarget(level.melonMan))
		{
			//Controls for camera movement
			float camMoveSpeed = 5*deltaTime;
			float camMoveSpeedAccelerationFactor = 5;
			if(Gdx.input.isKeyPressed(Keys.SHIFT_LEFT))
			{
				camMoveSpeed *= camMoveSpeedAccelerationFactor;
			}
			if(Gdx.input.isKeyPressed(Keys.LEFT))
			{
				moveCamera(-camMoveSpeed,0);
			}
			if(Gdx.input.isKeyPressed(Keys.RIGHT))
			{
				moveCamera(camMoveSpeed,0);
			}
			if(Gdx.input.isKeyPressed(Keys.UP))
			{
				moveCamera(0,camMoveSpeed);
			}
			if(Gdx.input.isKeyPressed(Keys.DOWN))
			{
				moveCamera(0,-camMoveSpeed);
			}
			if(Gdx.input.isKeyPressed(Keys.BACKSPACE))
			{
				cameraHelper.setPosition(0,0);
			}
			
			//Controls for camera zooming
			float camZoomSpeed = 1*deltaTime;
			float camZoomSpeedAccelerationFactor = 5;
			
			if(Gdx.input.isKeyPressed(Keys.SHIFT_LEFT))
			{
				camZoomSpeed *= camZoomSpeedAccelerationFactor;
			}
			if(Gdx.input.isKeyPressed(Keys.COMMA))
			{
				cameraHelper.addZoom(camZoomSpeed);
			}
			if(Gdx.input.isKeyPressed(Keys.PERIOD))
			{
				cameraHelper.addZoom(-camZoomSpeed);
			}
			if(Gdx.input.isKeyPressed(Keys.SLASH))
			{
				cameraHelper.setZoom(2.0f);
			}
		}
	}
	
	/**
	 * This method moves the camera based on the parameters x and y
	 */
	private void moveCamera(float x, float y)
	{
		x += cameraHelper.getPosition().x;
		y += cameraHelper.getPosition().y;
		cameraHelper.setPosition(x, y);
	}
	
	/**
	 * Overrides default input for keyUp so that it resets the game world
	 */
	@Override
	public boolean keyUp(int keycode)
	{
		//Reset game world
		if(keycode == Keys.R)
		{
			init();
			Gdx.app.debug(TAG, "Game world reset.");
		}
		
		//Toggle camera follow
		else if(keycode == Keys.ENTER)
		{
			cameraHelper.setTarget(cameraHelper.hasTarget() ? null : level.melonMan);
			Gdx.app.debug(TAG, "Camera Follow enabled: "+cameraHelper.hasTarget());
		}
		
		//Back to Menu
		else if(keycode == Keys.ESCAPE || keycode == Keys.BACK)
		{
			backToMenu();
		}
		
		return false;
	}
	
	/**
	 * Handle the physics for when MelonMan collides with a Rock
	 * 
	 * @param rock
	 */
	private void onCollisionMelonManWithGround(Ground ground)
	{
		MelonMan melonMan = level.melonMan;
		float heightDifference = Math.abs(melonMan.position.y-(ground.position.y+ground.bounds.height));
		
		if(heightDifference > 0.60f)
		{
			boolean hitRightEdge = melonMan.position.x > (ground.position.x+ground.bounds.width/2.0f);
			if(hitRightEdge)
			{
				melonMan.position.x = ground.position.x+ground.bounds.width;
			}
			else
			{
				melonMan.position.x = ground.position.x-melonMan.bounds.width;
			}
			return;
		}
		
		switch(melonMan.jumpState)
		{
			case GROUNDED:
				break;
			case FALLING:
			case JUMP_FALLING:
				melonMan.position.y = ground.position.y+melonMan.bounds.height-0.3f;
				melonMan.jumpState = JUMP_STATE.GROUNDED;
				break;
				
			case JUMP_RISING:
				melonMan.position.y = ground.position.y+melonMan.bounds.height-0.3f;
				break;
		}
	}
	
	/**
	 * Handle the physics for when MelonMan collides with a Rain object
	 * 
	 * @param rainDrop
	 */
	private void onCollisionMelonManWithRain(Rain rainDrop)
	{
		rainDrop.collected = true;
		score += rainDrop.getScore();
		Gdx.app.log(TAG, "Rain collected");
	}
	
	/**
	 * Handle the physics for when MelonMan collides with a Star
	 * 
	 * @param star
	 */
	private void onCollisionMelonManWithStar(Star star)
	{
		star.collected = true;
		score += star.getScore();
		level.melonMan.setStarPowerup(true);
	}
	
	/**
	 * This method tests to see if MelonMan collides with any of the other
	 * collidable objects in the level
	 */
	private void testCollisions()
	{
		r1.set(level.melonMan.position.x, level.melonMan.position.y, level.melonMan.bounds.width, level.melonMan.bounds.height);
		
		//Test collision: melonMan with the ground
		for(Ground ground: level.groundBlocks)
		{
			r2.set(ground.position.x, ground.position.y, ground.bounds.width, ground.bounds.height);
			if(!r1.overlaps(r2)) continue;
			onCollisionMelonManWithGround(ground);
		}
		
		//Test collisions melonMan with rain
		for(Rain rain: level.rainDrops)
		{
			if(rain.collected) continue;
			r2.set(rain.position.x, rain.position.y, rain.bounds.width, rain.bounds.height);
			if(!r1.overlaps(r2)) continue;
			onCollisionMelonManWithRain(rain);
			break;
		}
		
		//Test collision melonMan with stars
		for(Star star: level.stars)
		{
			if(star.collected) continue;
			r2.set(star.position.x, star.position.y, star.bounds.width, star.bounds.height);
			if(!r1.overlaps(r2)) continue;
			onCollisionMelonManWithStar(star);
			break;
		}
	}
	
	/**
	 * Returns true if the player has a lower y position than -5 meters
	 * 
	 * @return
	 */
	public boolean isPlayerUnderGround()
	{
		return level.melonMan.position.y < -5;
	}
	
	/**
	 * Determines if a game over occurred.  Returns true if lives < 0
	 * 
	 * @return
	 */
	public boolean isGameOver()
	{
		return lives < 0;
	}
	
	/**
	 * Updates the world relative to the previous update
	 * 
	 * @param deltaTime
	 */
	public void update(float deltaTime)
	{
		handleDebugInput(deltaTime);
		if(isGameOver())
		{
			timeLeftGameOverDelay -= deltaTime;
			if(timeLeftGameOverDelay < 0)
			{
				backToMenu();
			}
		}
		else
		{
			handleInputGame(deltaTime);
		}
		level.update(deltaTime);
		testCollisions();
		cameraHelper.update(deltaTime);
		if(!isGameOver() && isPlayerUnderGround())
		{
			lives--;
			if(isGameOver())
			{
				timeLeftGameOverDelay = Constants.TIME_DELAY_GAME_OVER;
			}
			else
			{
				onDeath();
			}
		}
		level.pyramidFar.updateScrollPosition(cameraHelper.getPosition());
		level.pyramidNear.updateScrollPosition(cameraHelper.getPosition());
	}
}
