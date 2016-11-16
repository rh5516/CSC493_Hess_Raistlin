package game;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import gui.MenuScreen;
import objects.AbstractGameObject;
import objects.Ground;
import objects.MelonMan;
import objects.Rain;
import objects.Rat;
import objects.Star;
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
public class WorldController extends InputAdapter implements ContactListener
{
	private static final String TAG = WorldController.class.getName();
	private Game game;
	private float timeLeftGameOverDelay;
	private boolean newGame;
	public Array<AbstractGameObject> objectsForRemoval;
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
	public float ratTimer;
	public Vector2 ratSpawn;

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
		cameraHelper.setTarget(level.melonMan);
		ratTimer = MathUtils.random(0.5f, 2.0f);
		ratSpawn = new Vector2();
		ratSpawn.x = level.ratSpawnPos.x;
		ratSpawn.y = level.ratSpawnPos.y;
		
		initPhysics();
	}
	
	/**
	 * Sets up the Box2D physics world and all objects' collision properties
	 */
	private void initPhysics()
	{
		//Set a limit on the amount of rain that spawns based on the amount 
		//in the level's image
		levelRainLimit = level.numberOfRainDrops*4;
		numRainAlive = 1;
		
		//Find highest y position of all ground blocks in the level
		for(Ground ground: level.groundBlocks)
		{
			highestGroundBlock = Math.max(highestGroundBlock, ground.position.y);
		}
		
		if(world != null) world.dispose();
		world = new World(new Vector2(0, -9.81f), true);
		world.setContactListener(this);

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
		
		//Create collisions for Star blocks
		for(Star star: level.stars)
		{
			BodyDef bodyDef = new BodyDef();
			bodyDef.type = BodyType.StaticBody;
			bodyDef.position.set(star.position);
			Body body = world.createBody(bodyDef);
			star.body = body;
			PolygonShape shape = new PolygonShape();
			origin.x = star.bounds.width/2.0f;
			origin.y = star.bounds.height/2.0f;
			shape.setAsBox(star.bounds.width/2.0f, star.bounds.height/2.0f, origin, 0);
			FixtureDef fixtureDef = new FixtureDef();
			fixtureDef.shape = shape;
			body.createFixture(fixtureDef);
			body.setUserData(star);
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
		body.createFixture(fixtureDef);
		body.setUserData(obj);
		obj.body = body;
		shape.dispose();
		
		//Create collision for the Rat
		Rat rat = level.rat;
		bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(rat.position);
		
		Body ratBody = world.createBody(bodyDef);
		
		shape = new PolygonShape();
		origin.x = rat.bounds.width/2.0f;
		origin.y =  rat.bounds.height/2.0f;
		shape.setAsBox(rat.bounds.width/2.0f, rat.bounds.height/2.0f, origin, 0);
		
		fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.friction = rat.friction;
		ratBody.createFixture(fixtureDef);
		ratBody.setUserData(rat);
		rat.body = ratBody;
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
		}
		else if (objB instanceof MelonMan)
		{
			processPlayerContact(fixtureB, fixtureA);
		}
		//Determine if either object was a Rat
		else if(objA instanceof Rat)
		{
			if(objB instanceof Rain)
			{
				processRatContactRain(fixtureA, fixtureB);
			}
		}
		else if(objB instanceof Rat)
		{
			if(objA instanceof Rain)
			{
				processRatContactRain(fixtureB, fixtureA);
			}
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
			flagForRemoval(obj);
		}
		
		//Handle collision with a star
		if (objFixture.getBody().getUserData() instanceof Star)
		{
			Star obj = (Star) objFixture.getBody().getUserData();
			score += obj.getScore();
			obj.collected = true;
			AudioManager.instance.play(Assets.instance.sounds.pickupStar);
			level.melonMan.setStarPowerup(true);
			flagForRemoval(obj);
		}
	}
	
	/**
	 * Handle physics for when a Rat collides with Rain. 
	 */
	private void processRatContactRain(Fixture ratFixture, Fixture rainFixture)
	{
		//Remove rain from the world
		Rain obj = (Rain) rainFixture.getBody().getUserData();
		flagForRemoval(obj);
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
			body.setGravityScale(0.35f);
			
//			rain.splash.setPosition(rain.position.x+rain.dimension.x/2.0f, rain.position.y);
			level.rainDrops.add(rain);
			
			numRainAlive++;
		}
//		Gdx.app.log(TAG, "numRainALive: "+numRainAlive);
	}
	
	/**
	 * Checks to see if there is a rat alive, otherwise decrement a counter until
	 * it hits 0 and then create a new one
	 */
	public void spawnNewRat(float deltaTime)
	{
		//Check to see if the Rat fell off the stage
		if(level.rat != null)
		{
			if(level.rat.position.y <= -5f)
			{
				objectsForRemoval.add(level.rat);
			}
		}
				
		if(level.rat == null)
		{
			ratTimer -= deltaTime;
			if(ratTimer <= 0.0f)
			{
				Rat rat = new Rat();
 				rat.position.x = ratSpawn.x;
 				rat.position.y = ratSpawn.y;
				Gdx.app.log(TAG, "Rat's Start Position: "+rat.position.x+","+rat.position.y);
				
				//Create collision for the Rat
				BodyDef bodyDef = new BodyDef();
				bodyDef.type = BodyType.DynamicBody;
				bodyDef.position.set(rat.position);
				
				Body ratBody = world.createBody(bodyDef);
				
				PolygonShape shape = new PolygonShape();
				rat.origin.x = rat.bounds.width/2.0f;
				rat.origin.y =  rat.bounds.height/2.0f;
				shape.setAsBox(rat.bounds.width/2.0f, rat.bounds.height/2.0f, rat.origin, 0);
				
				FixtureDef fixtureDef = new FixtureDef();
				fixtureDef.shape = shape;
				fixtureDef.friction = rat.friction;
				ratBody.createFixture(fixtureDef);
				ratBody.setUserData(rat);
				rat.body = ratBody;
				shape.dispose();
				
				level.rat = rat;
				level.rat.body.setLinearVelocity(-0.01f, -0.01f);
				ratTimer = MathUtils.random(0.5f, 2.0f);
			}
		}
	}
	
	/**
	 * Removes all objects from the list of objects to remove, as well
	 * as any Box2D physics bodies
	 */
	public void removeObjects()
	{
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
				else if(obj instanceof Star)
				{
					int index = level.stars.indexOf((Star) obj, true);
					if(index != -1)
					{
						level.stars.removeIndex(index);
						world.destroyBody(obj.body);
					}
				}
				else if(obj instanceof Rat)
				{
					if(level.rat != null)
					{
						level.rat = null;
						world.destroyBody(obj.body);
					}
				}
			}
			objectsForRemoval.removeRange(0, objectsForRemoval.size - 1);
		}
	}
	
	/**
	 * Updates the world relative to the previous update
	 * 
	 * @param deltaTime
	 */
	public void update(float deltaTime)
	{
		//Check if player touched the nextLevel object. If so, clean up all
		//bodies and create new level
//		if(level.nextLevel.touched)
//		{
//			if(level.nextLevel.levelToLoad == 1)
//			{
//				
//			}
//			else
//			{
//				
//			}
//		}
		
		//Flag any rain drops that have been collected or go
		//too far under the level for removal
		for(Rain rain: level.rainDrops)
		{
			if(rain.collected || rain.position.y <= -10.0f || rain.decay <= 0.0f)// || rain.body.getLinearVelocity().y == 0.0f)
			{
//				Gdx.app.log(TAG, "Rain flagged for removal.");
				objectsForRemoval.add(rain);
			}
			else if(rain.body.getLinearVelocity().y == 0.0f)
			{
				rain.decaying = true;
				rain.splash.setDuration(0);
			}
		}
		
		//Decrease counter until ratTimer is 0 if there is no rat, then create a new one
		spawnNewRat(deltaTime);
		
		//Remove all objects and their Body's in the removal list
		removeObjects();
		
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
		
		//Generate rain drops randomly around the level
		if(MathUtils.random(0.0f, 0.2f) < deltaTime)
		{
		    Vector2 centerPos = new Vector2(MathUtils.random(0.0f, level.levelWidth), level.melonMan.position.y);
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
		level.sun.updateScrollPosition(cameraHelper.getPosition());
		
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

	/**
	 * When a collision is detected, this method is called continously until the
	 * objects are no longer colliding
	 */
	@Override
	public void beginContact(Contact contact)
	{
		AbstractGameObject fixtureA = (AbstractGameObject) contact.getFixtureA().getBody().getUserData();
		AbstractGameObject fixtureB = (AbstractGameObject) contact.getFixtureA().getBody().getUserData();
		
		//Check if either fixture was a MelonMan
		if(fixtureA instanceof MelonMan)
		{
			fixtureA.startContact();
			processContact(contact);
		}
		else if(fixtureB instanceof MelonMan)
		{
			fixtureB.startContact();
			processContact(contact);
		}
	}
	
	/**
	 * When a collision ends, this method is called
	 */
	@Override
	public void endContact(Contact contact)
	{
		AbstractGameObject fixtureA = (AbstractGameObject) contact.getFixtureA().getBody().getUserData();
		AbstractGameObject fixtureB = (AbstractGameObject) contact.getFixtureA().getBody().getUserData();
		
		//Check if either fixture was a MelonMan
		if(fixtureA instanceof MelonMan)
		{
			fixtureA.endContact();
		}
		else if(fixtureB instanceof MelonMan)
		{
			fixtureB.endContact();
		}
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold){}
	
	@Override
	public void postSolve(Contact contact, ContactImpulse impulse){}
}
