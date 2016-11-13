package objects;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import game.Assets;

/**
 * This represents a glowing post that will move the player between the two level. It is animated to look
 * like it is glowing
 * 
 * @author Raistlin Hess
 *
 */
public class NextLevel extends AbstractGameObject
{
	
	public NextLevel()
	{
		init();
	}
	
	/**
	 * Initializes the post
	 */
	private void init()
	{
		dimension.set(1.0f, 2.0f);
		
		//Set up animations
		setAnimation(Assets.instance.nextLevel.animNextLevel);
		
		//Set bounding box for collision detection
		bounds.set(0,0, dimension.x, dimension.y);
	}
	
	/**
	 * This method draws the post
	 */
	@Override
	public void render(SpriteBatch batch)
	{
		TextureRegion reg = null;
		reg = animation.getKeyFrame(stateTime, true);
		
		batch.draw(reg.getTexture(), position.x, position.y, origin.x, origin.y, dimension.x, dimension.y, scale.x, scale.y, rotation, reg.getRegionX(), reg.getRegionY(), reg.getRegionWidth(), reg.getRegionHeight(), false, false);
	}
}
