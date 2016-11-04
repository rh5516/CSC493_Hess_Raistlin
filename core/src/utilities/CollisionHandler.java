package utilities;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.utils.ObjectMap;
import game.Assets;
import game.WorldController;
import objects.AbstractGameObject;
import objects.Ground;
import objects.MelonMan;
import objects.Rain;

/**
 * This class is responsible for handling all Box2D collisions
 * 
 * @author Raistlin Hess
 *
 */
public class CollisionHandler implements ContactListener
{
	private ObjectMap<Short, ObjectMap<Short, ContactListener>> listeners;
	private WorldController world;

	/**
	 * Create the world and all ContactListeners
	 */
	public CollisionHandler(WorldController world)
	{
		this.world = world;
		listeners = new ObjectMap<Short, ObjectMap<Short, ContactListener>>();
	}

	/**
	 * Add two ContactListeners to see if categoryA collides with categoryB or
	 * vice versa
	 */
	public void addListener(short categoryA, short categoryB, ContactListener listener)
	{
		addListenerInternal(categoryA, categoryB, listener);
		addListenerInternal(categoryB, categoryA, listener);
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
//		Fixture fixtureA = contact.getFixtureA(); 
//		Fixture fixtureB = contact.getFixtureB();
//
//		ContactListener listener = getListener(fixtureA.getFilterData().categoryBits, fixtureB.getFilterData().categoryBits);
//		if (listener != null)
//		{
//			listener.beginContact(contact);
//		}
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
//		Fixture fixtureA = contact.getFixtureA();
//		Fixture fixtureB = contact.getFixtureB();
//
//		processContact(contact);
//
//		ContactListener listener = getListener(fixtureA.getFilterData().categoryBits,
//				fixtureB.getFilterData().categoryBits);
//		if (listener != null)
//		{
//			listener.endContact(contact);
//		}
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold)
	{
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();
		ContactListener listener = getListener(fixtureA.getFilterData().categoryBits,
				fixtureB.getFilterData().categoryBits);
		if (listener != null)
		{
			listener.preSolve(contact, oldManifold);
		}
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse)
	{
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();
		ContactListener listener = getListener(fixtureA.getFilterData().categoryBits,
				fixtureB.getFilterData().categoryBits);
		if (listener != null)
		{
			listener.postSolve(contact, impulse);
		}
	}

	/**
	 * Creates a ContactListener for objects categoryA and categoryB
	 */
	private void addListenerInternal(short categoryA, short categoryB, ContactListener listener)
	{
		ObjectMap<Short, ContactListener> listenerCollection = listeners.get(categoryA);
		if (listenerCollection == null)
		{
			listenerCollection = new ObjectMap<Short, ContactListener>();
			listeners.put(categoryA, listenerCollection);
		}
		listenerCollection.put(categoryB, listener);
	}

	/**
	 * Returns a ContactListener, if any, that listens for collision between
	 * categoryA and categoryB
	 */
	private ContactListener getListener(short categoryA, short categoryB)
	{
		ObjectMap<Short, ContactListener> listenerCollection = listeners.get(categoryA);
		if (listenerCollection == null)
		{
			return null;
		}
		return listenerCollection.get(categoryB);
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
			System.out.println("MelonContact: processContact");
		}
		else if (objB instanceof MelonMan)
		{
			processPlayerContact(fixtureB, fixtureA);
			System.out.println("MelonContact: processContact");
		}
	}

	/**
	 * Handle physics for when the player collides with objects
	 */
	private void processPlayerContact(Fixture playerFixture, Fixture objFixture)
	{
		//Handle collision with the ground
		if (objFixture.getBody().getUserData() instanceof Ground)
		{
			MelonMan melonMan = (MelonMan) playerFixture.getBody().getUserData();
			melonMan.acceleration.y = 0;
			melonMan.velocity.y = 0;
//			melonMan.jumpState = JUMP_STATE.GROUNDED;
			playerFixture.getBody().setLinearVelocity(melonMan.velocity);
		}
		
		//Handle collision with a rain drop
		else if (objFixture.getBody().getUserData() instanceof Rain)
		{
			world.score += 5;
			AudioManager.instance.play(Assets.instance.sounds.pickupRain);
			AudioManager.instance.play(Assets.instance.sounds.jump);
			AudioManager.instance.play(Assets.instance.sounds.liveLost);

			Rain rain = (Rain) objFixture.getBody().getUserData();
			world.flagForRemoval(rain);
		}
	}
}
