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
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import gui.MenuScreen;
import objects.AbstractGameObject;
import objects.Ground;
import objects.MelonMan;
import objects.Rain;
import objects.Star;
import utilities.AudioManager;
import utilities.CameraHelper;
import utilities.CollisionHandler;
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
	private boolean newGame;
	public Array<AbstractGameObject> objectsForRemoval;
	private Rectangle r1 = new Rectangle();
	private Rectangle r2 = new Rectangle();
	public CameraHelper cameraHelper;
	public Level level;
	public int levelRainLimit;
	public int numRainAlive;
	public float highestGroundBlock;
	public int lives;
	public int score;
	public float livesVisual;
	public float scoreVisual;
	public World world;
	

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
		newGame = true;
		Gdx.input.setInputProcessor(this);
		cameraHelper = new CameraHelper();
		lives = Constants.LIVES_START;
		timeLeftGameOverDelay = 0;
		objectsForRemoval = new Array<AbstractGameObject>();
		initLevel();
	}
	
	/**
	 * Sets the score to 0 and loads the first level
	 */
	public void initLevel()
	{
		score = 0;
		scoreVisual = score;
		if(newGame)
		{
			level = new Level(Constants.LEVEL_01);
		}
		else
		{
			level = new Level(Constants.LEVEL_02);
		}
		cameraHelper.setTarget(level.melonMan);
		initPhysics();
	}
	
	/**
	 * Sets up the Box2D physics world and all objects' collision properties
	 */
	private void initPhysics()
	{
		//Set a limit on the amount of rain that spawns based on the amount 
		//in the level's image
		levelRainLimit = level.numberOfRainDrops;
		numRainAlive = 1;
		for(Ground ground: level.groundBlocks)
		{
			highestGroundBlock = Math.max(highestGroundBlock, ground.position.y);
		}
		
		if(world != null) world.dispose();
		world = new World(new Vector2(0, -9.81f), true);
		world.setContactListener(new CollisionHandler(this));

		Vector2 origin = new Vector2();
		
		//Create collisions for Ground blocks
		for(Ground ground: level.groundBlocks)
		{
			BodyDef bodyDef = new BodyDef();
			bodyDef.type = BodyType.StaticBody;
			bodyDef.position.set(ground.position);
			Body body = world.createBody(bodyDef);
			ground.body = body;
			PolygonShape shape = new PolygonShape();
			origin.x = ground.bounds.width/2.0f;
			origin.y = ground.bounds.height/2.0f;
			shape.setAsBox(ground.bounds.width/2.0f, ground.bounds.height/2.0f, origin, 0);
			FixtureDef fixtureDef = new FixtureDef();
			fixtureDef.shape = shape;
			body.createFixture(fixtureDef);
			body.setUserData(ground);
			shape.dispose();
		}
		
		//Create collision for the player
		MelonMan obj = level.melonMan;
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(obj.position);
		
		Body body = world.createBody(bodyDef);
		
		PolygonShape shape = new PolygonShape();
		origin.x = obj.bounds.width/2.0f;
		origin.y =  obj.bounds.height/2.0f;
		shape.setAsBox(obj.bounds.width/2.0f, obj.bounds.height/2.0f, origin, 0);
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.friction = obj.friction;
//		fixtureDef.density = 5.0f;
		body.createFixture(fixtureDef);
		body.setUserData(obj);
		obj.body = body;
		shape.dispose();
	}
	
	/**
	 * When the player dies, reset his position and the camera, but not his score
	 */
	public void onDeath()
	{
		level = new Level(Constants.LEVEL_01);
		cameraHelper.setTarget(level.melonMan);
		initPhysics();
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
				level.melonMan.viewDirection = level.melonMan.viewDirection.LEFT;
				level.melonMan.body.applyForceToCenter(-level.melonMan.acceleration.x, 0.0f, true);
			}
			else if(Gdx.input.isKeyPressed(Keys.RIGHT))
			{
				level.melonMan.viewDirection = level.melonMan.viewDirection.RIGHT;
				level.melonMan.body.applyForceToCenter(level.melonMan.acceleration.x, 0.0f, true);
			}
			
			//Allow jumping if melonMan is on the ground
			if(Gdx.input.isTouched() || Gdx.input.isKeyPressed(Keys.SPACE))
			{
				if(level.melonMan.body.getLinearVelocity().y == 0.0f)
				{
					level.melonMan.body.applyLinearImpulse(0.0f, level.melonMan.acceleration.y, level.melonMan.origin.x, level.melonMan.origin.y, true);
				}
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
		
//		switch(melonMan.jumpState)
//		{
//			case GROUNDED:
//				break;
//			case FALLING:
//			case JUMP_FALLING:
//				melonMan.position.y = ground.position.y+melonMan.bounds.height-0.3f;
//				melonMan.jumpState = JUMP_STATE.GROUNDED;
//				break;
//				
//			case JUMP_RISING:
//				melonMan.position.y = ground.position.y+melonMan.bounds.height-0.3f;
//				break;
//		}
	}
	
	/**
	 * Handle the physics for when MelonMan collides with a Rain object
	 * 
	 * @param rainDrop
	 */
	private void onCollisionMelonManWithRain(Rain rainDrop)
	{
		rainDrop.collected = true;
		AudioManager.instance.play(Assets.instance.sounds.pickupRain);
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
		AudioManager.instance.play(Assets.instance.sounds.pickupStar);
		score += star.getScore();
		level.melonMan.setStarPowerup(true);
	}
	
	/**
	 * Handle removing rain if it hits the ground
	 */
	private void onCollisionRainWithGround(Rain rain)
	{
		objectsForRemoval.add(rain);
	}
	
	/**
	 * This method tests to see if MelonMan collides with any of the other
	 * collidable objects in the level
	 */
	private void testCollisions()
	{
		//Determine if any contacts have been made
		for(Contact contact: world.getContactList())
		{
			processContact(contact);
			
		}
		
		//Test collision melonMan with stars
		r1.set(level.melonMan.position.x, level.melonMan.position.y, level.melonMan.bounds.width, level.melonMan.bounds.height);
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
	 * Perform processing that based on the objects that have collided
	 */
	private void processContact(Contact contact)
	{
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();
		AbstractGameObject objA = (AbstractGameObject) fixtureA.getBody().getUserData();
		AbstractGameObject objB = (AbstractGameObject) fixtureB.getBody().getUserData();

		//Determine if either object was the player
		if (objA instanceof MelonMan)
		{
			processPlayerContact(fixtureA, fixtureB);
//			System.out.println(objA.getClass().getName()+":"+objB.getClass().getName());
		}
		else if (objB instanceof MelonMan)
		{
			processPlayerContact(fixtureB, fixtureA);
//			System.out.println(objB.getClass().getName()+":"+objA.getClass().getName());
		}
	}

	/**
	 * Handle physics for when the player collides with objects
	 */
	private void processPlayerContact(Fixture playerFixture, Fixture objFixture)
	{
		//Handle collision with a rain drop
		if (objFixture.getBody().getUserData() instanceof Rain)
		{
			Rain obj = (Rain) objFixture.getBody().getUserData();
			score += obj.getScore();
			AudioManager.instance.play(Assets.instance.sounds.pickupRain);
			AudioManager.instance.play(Assets.instance.sounds.jump);
			AudioManager.instance.play(Assets.instance.sounds.liveLost);

			Rain rain = (Rain) objFixture.getBody().getUserData();
			flagForRemoval(rain);
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
	 * Add objects to a queue that will be deleted on the next update
	 */
	public void flagForRemoval(AbstractGameObject obj)
	{
		objectsForRemoval.add(obj);
	}
	
	/**
	 * Spawns rain within a range of the player at a random height above 
	 * the height of the highest Ground block
	 */
	private void spawnRain(Vector2 pos)
	{
		if(numRainAlive < levelRainLimit)
		{
			Rain rain = new Rain();
			float x = MathUtils.random(-Constants.RAIN_SPAWN_RADIUS, Constants.RAIN_SPAWN_RADIUS);
			float y = MathUtils.random(highestGroundBlock+4.0f, highestGroundBlock+8.0f);
			
			BodyDef bodyDef = new BodyDef();
			bodyDef.position.set(pos);
			bodyDef.position.x += x;
			bodyDef.position.y += y;
			rain.position.set(pos);
			bodyDef.angle= 0;
			
			Body body = world.createBody(bodyDef);
			body.setType(BodyType.DynamicBody);
			body.setUserData(rain);
			body.setLinearVelocity(rain.velocity);
			rain.body = body;
			
			PolygonShape shape = new PolygonShape();
			rain.origin.x = rain.bounds.width/2.0f;
			rain.origin.y = rain.bounds.height/2.0f;
			shape.setAsBox(rain.bounds.width/2.0f-0.13f, rain.bounds.height/2.0f-0.13f, rain.origin, 0);
			FixtureDef fixtureDef = new FixtureDef();
			fixtureDef.density = 0.0f;
			fixtureDef.shape = shape;
			fixtureDef.friction = rain.friction;
			body.createFixture(fixtureDef);
			shape.dispose();
			level.rainDrops.add(rain);
			
			numRainAlive++;
		}
//		Gdx.app.log(TAG, "numRainALive: "+numRainAlive);
	}
	
	/**
	 * Updates the world relative to the previous update
	 * 
	 * @param deltaTime
	 */
	public void update(float deltaTime)
	{
		//Flag any rain drops that have been collected or go
		//too far under the level for removal
		for(Rain rain: level.rainDrops)
		{
			if(rain.collected || rain.position.y <= -10.0f || rain.body.getLinearVelocity().y == 0.0f)
			{
//				Gdx.app.log(TAG, "Rain flagged for removal.");
				objectsForRemoval.add(rain);
			}
		}
		
		//Remove all objects and their Body's in the removal list
		if (objectsForRemoval.size > 0)
		{
			for (AbstractGameObject obj : objectsForRemoval)
			{
				if (obj instanceof Rain)
				{
					int index = level.rainDrops.indexOf((Rain) obj, true);
					if (index != -1)
					{
						numRainAlive--;
					    level.rainDrops.removeIndex(index);
					    world.destroyBody(obj.body);
					}
				}
			}
			objectsForRemoval.removeRange(0, objectsForRemoval.size - 1);
		}
		
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
		
		//Generate rain drops randomly near the player
//		if(MathUtils.random(0.0f, 2.0f) < deltaTime)
		{
		    //Set starting position to either plus or minus melonMan's position and width
		    Vector2 centerPos = new Vector2(level.melonMan.position);
		    centerPos.x += level.melonMan.bounds.width*MathUtils.random(-1.0f, 1.0f);
		    spawnRain(centerPos);
		}
		world.step(deltaTime, 8, 3);
		level.update(deltaTime);
		testCollisions();
		cameraHelper.update(deltaTime);
		if(!isGameOver() && isPlayerUnderGround())
		{
			AudioManager.instance.play(Assets.instance.sounds.liveLost);
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
}
