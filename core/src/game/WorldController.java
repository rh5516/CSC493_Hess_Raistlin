package game;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import gui.MenuScreen;
import objects.BunnyHead;
import objects.Carrot;
import objects.Feather;
import objects.GoldCoin;
import objects.Rock;
import objects.BunnyHead.JUMP_STATE;
import utilities.AudioManager;
import utilities.CameraHelper;
import utilities.Constants;

/**
 * This class is responsible for updating information about the game objects, as well as the camera,
 * and handling input
 * 
 * @author Raistlin Hess
 *
 */
public class WorldController extends InputAdapter implements Disposable
{
	private static final String TAG = WorldController.class.getName();
	private Game game;
	private float timeLeftGameOverDelay;
	//Rectangles for collision detection
	private Rectangle r1 = new Rectangle();
	private Rectangle r2 = new Rectangle();
	private boolean goalReached;
	public CameraHelper cameraHelper;
	public Level level;
	public int lives;
	public int score;
	public float livesVisual;
	public float scoreVisual;
	public World b2world;
	
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
		livesVisual = lives;
		timeLeftGameOverDelay = 0;
		initLevel();
	}
	
	/**
	 * Sets the score to 0 and loads the first level
	 */
	public void initLevel()
	{
		score = 0;
		scoreVisual = score;
		goalReached = false;
		level = new Level(Constants.LEVEL_01);
		cameraHelper.setTarget(level.bunnyHead);
		initPhysics();
	}
	
	/**
	 * Sets up the Box2D physics for all objects that have a RigidBody
	 */
	private void initPhysics()
	{
		if(b2world != null) b2world.dispose();
		b2world = new World(new Vector2(0, -9.81f), true);
		
		//Rocks
		Vector2 origin = new Vector2();
		for(Rock rock: level.rocks)
		{
			BodyDef bodyDef = new BodyDef();
			bodyDef.type = BodyType.KinematicBody;
			bodyDef.position.set(rock.position);
			
			Body body = b2world.createBody(bodyDef);
			rock.body = body;
			
			PolygonShape polygonShape = new PolygonShape();
			origin.x = rock.bounds.width/2.0f;
			origin.y = rock.bounds.height/2.0f;
			polygonShape.setAsBox(rock.bounds.width/2.0f, rock.bounds.height/2.0f, origin, 0);
			
			FixtureDef fixtureDef = new FixtureDef();
			fixtureDef.shape = polygonShape;
			body.createFixture(fixtureDef);
			polygonShape.dispose();
		}
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
	 * Allows the player to send input commands to the bunny if the camera is following it
	 * 
	 * @param deltaTime
	 */
	private void handleInputGame(float deltaTime)
	{
		if(cameraHelper.hasTarget(level.bunnyHead))
		{
			//Player movement
			if(Gdx.input.isKeyPressed(Keys.LEFT))
			{
				level.bunnyHead.velocity.x = -level.bunnyHead.terminalVelocity.x;
			}
			else if(Gdx.input.isKeyPressed(Keys.RIGHT))
			{
				level.bunnyHead.velocity.x = level.bunnyHead.terminalVelocity.x;
			}
			else
			{
				//Execute auto-forward movement on non-desktop platform
				if(Gdx.app.getType() != ApplicationType.Desktop)
				{
					level.bunnyHead.velocity.x = level.bunnyHead.terminalVelocity.x;
				}
			}
			
			//Bunny jump
			if(Gdx.input.isTouched() || Gdx.input.isKeyPressed(Keys.SPACE))
			{
				level.bunnyHead.setJumping(true);
			}
			else
			{
				level.bunnyHead.setJumping(false);
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
		
		if(!cameraHelper.hasTarget(level.bunnyHead))
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
				cameraHelper.setZoom(1);
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
	 * Accepts input which affects the game, not the player.
	 * Includes resetting the game, toggling camera control, and returning
	 * to the menu screen
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
			cameraHelper.setTarget(cameraHelper.hasTarget() ? null : level.bunnyHead);
			Gdx.app.debug(TAG, "Camera Follow enabled: "+cameraHelper.hasTarget());
		}
		
		//Back to menu
		else if(keycode == Keys.ESCAPE || keycode == Keys.BACK)
		{
			backToMenu();
		}
		
		return false;
	}
	
	/**
	 * Handle the physics for when the bunny collides with a Rock
	 * 
	 * @param rock
	 */
	private void onCollisionBunnyHeadWithRock(Rock rock)
	{
		BunnyHead bunnyHead = level.bunnyHead;
		float heightDifference = Math.abs(bunnyHead.position.y-(rock.position.y+rock.bounds.height));
	
		if(heightDifference > 0.25f)
		{
			boolean hitRightEdge = bunnyHead.position.x > (rock.position.x+rock.bounds.width/2.0f);
			if(hitRightEdge)
			{
				bunnyHead.position.x = rock.position.x+rock.bounds.width;
			}
			else
			{
				bunnyHead.position.x = rock.position.x-bunnyHead.bounds.width;
			}
			return;
		}
		
		switch(bunnyHead.jumpState)
		{
			case GROUNDED:
				break;
			case FALLING:
			case JUMP_FALLING:
				bunnyHead.position.y = rock.position.y+bunnyHead.bounds.height+bunnyHead.origin.y;
				bunnyHead.jumpState = JUMP_STATE.GROUNDED;
				break;
				
			case JUMP_RISING:
				bunnyHead.position.y = rock.position.y+bunnyHead.bounds.height+bunnyHead.origin.y;
				break;
		}
	}
	
	/**
	 * Handle the physics for when the bunny collides with a GoldCoin
	 * 
	 * @param goldCoin
	 */
	private void onCollisionBunnyWithGoldCoin(GoldCoin goldCoin)
	{
		goldCoin.collected = true;
		AudioManager.instance.play(Assets.instance.sounds.pickupCoin);
		score += goldCoin.getScore();
		Gdx.app.log(TAG, "Gold coin collected");
	}
	
	/**
	 * Handle the physics for when the bunny collides with a Feather
	 * 
	 * @param feather
	 */
	private void onCollisionBunnyWithFeather(Feather feather)
	{
		feather.collected = true;
		AudioManager.instance.play(Assets.instance.sounds.pickupFeather);
		score += feather.getScore();
		level.bunnyHead.setFeatherPowerup(true);
	}
	
	/**
	 * Handle the physics for when the bunny collides with the Goal
	 */
	private void onCollisionBunnyWithGoal()
	{
		goalReached = true;
		timeLeftGameOverDelay = Constants.TIME_DELAY_GAME_FINISHED;
		
		Vector2 centerPosBunnyHead = new Vector2(level.bunnyHead.position);
		centerPosBunnyHead.x += level.bunnyHead.bounds.width;
		spawnCarrots(centerPosBunnyHead, Constants.CARROTS_SPAWN_MAX, Constants.CARROTS_SPAWN_RADIUS);
	}
	
	/**
	 * Create several carrots based on Constants' value and drop them from
	 * the sky
	 */
	private void spawnCarrots(Vector2 pos, int numCarrots, float radius)
	{
		float carrotShapeScale = 0.5f;
		
		//Create carrots with box2d body and fixture
		for(int i = 0; i < numCarrots; i++)
		{
			Carrot carrot = new Carrot();
			
			//Calculate random spawn position, rotation, and scale
			float x = MathUtils.random(-radius, radius);
			float y = MathUtils.random(5.0f, 15.0f);
			float rotation = MathUtils.random(0.0f, 360.0f)*MathUtils.degreesToRadians;
			float carrotScale = MathUtils.random(0.5f, 1.5f);
			
			//Create box2d body for carrot with start position and
			//angle of rotation
			BodyDef bodyDef = new BodyDef();
			bodyDef.position.set(pos);
			bodyDef.position.add(x,y);
			bodyDef.angle = rotation;
			
			Body body = b2world.createBody(bodyDef);
			body.setType(BodyType.DynamicBody);
			carrot.body = body;
			
			//Create rectangular shape for carrot to allow collisions
			//with other objects
			PolygonShape polygonShape = new PolygonShape();
			float halfWidth = carrot.bounds.width/2.0f*carrotScale;
			float halfHeight = carrot.bounds.height/2.0f*carrotScale;
			polygonShape.setAsBox(halfWidth*carrotShapeScale, halfHeight*carrotShapeScale);
			
			//Set physics attributes
			FixtureDef fixtureDef = new FixtureDef();
			fixtureDef.shape = polygonShape;
			fixtureDef.density = 50;
			fixtureDef.restitution = 0.5f;
			fixtureDef.friction = 0.5f;
			body.createFixture(fixtureDef);
			polygonShape.dispose();
			
			//Finally, add new carrot to list for updating/rendering
			level.carrots.add(carrot);
		}
	}
	
	/**
	 * This method tests to see if the bunnyHead collides with any of the other
	 * collidable objects in the level
	 */
	private void testCollisions()
	{
		r1.set(level.bunnyHead.position.x, level.bunnyHead.position.y, level.bunnyHead.bounds.width, level.bunnyHead.bounds.height);
		
		//Test collision: Bunnyhead with Rocks
		for(Rock rock: level.rocks)
		{
			r2.set(rock.position.x, rock.position.y, rock.bounds.width, rock.bounds.height);
			if(!r1.overlaps(r2)) continue;
			onCollisionBunnyHeadWithRock(rock);
			//IMPORTANT: must do all collisions for valid edge testing on rocks
		}
		
		//Test collisions bunnyhead with gold coins
		for(GoldCoin goldCoin: level.goldCoins)
		{
			if(goldCoin.collected) continue;
			r2.set(goldCoin.position.x, goldCoin.position.y, goldCoin.bounds.width, goldCoin.bounds.height);
			if(!r1.overlaps(r2)) continue;
			onCollisionBunnyWithGoldCoin(goldCoin);
			break;
		}
		
		//Test collision bunnyhead with feathers
		for(Feather feather: level.feathers)
		{
			if(feather.collected) continue;
			r2.set(feather.position.x, feather.position.y, feather.bounds.width, feather.bounds.height);
			if(!r1.overlaps(r2)) continue;
			onCollisionBunnyWithFeather(feather);
			break;
		}
		
		//Test collision bunnyhead with goal
		if(!goalReached)
		{
			r2.set(level.goal.bounds);
			r2.x += level.goal.position.x;
			r2.y += level.goal.position.y;
			if(r1.overlaps(r2))
			{
				onCollisionBunnyWithGoal();
			}
		}
			
	}
	
	/**
	 * Returns true if the player has a lower y position than -5 meters
	 * 
	 * @return
	 */
	public boolean isPlayerInWater()
	{
		return level.bunnyHead.position.y < -5;
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
	public void update (float deltaTime)
	{
		handleDebugInput(deltaTime);
		if(isGameOver() || goalReached)
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
		b2world.step(deltaTime, 8, 3);
		cameraHelper.update(deltaTime);
		if(!isGameOver() && isPlayerInWater())
		{
			AudioManager.instance.play(Assets.instance.sounds.liveLost);
			lives--;
			if(isGameOver())
			{
				timeLeftGameOverDelay = Constants.TIME_DELAY_GAME_OVER;
			}
			else
			{
				initLevel();
			}
		}
		
		//Update mountains' position
		level.mountains.updateScrollPosition(cameraHelper.getPosition());
		
		//Timer for losing a life
		if(livesVisual > lives)
		{
			livesVisual = Math.max(lives, livesVisual-1*deltaTime);
		}
		
		//Timer for changing score
		if(scoreVisual < score)
		{
			scoreVisual = Math.min(score, scoreVisual+250*deltaTime);
		}
	}

	@Override
	public void dispose()
	{
//		if(b2world != null) b2world.dispose();
	}
}
