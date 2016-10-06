package objects;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import game.Assets;

/**
 * This class represents a Star object. They can be collected and makes the player
 * move faster for a set duration 
 * 
 * @author Raistlin Hess
 *
 */
public class Star extends AbstractGameObject
{
	private TextureRegion regStar;
	public boolean collected;
	
	public Star()
	{
		init();
	}
	
	/**
	 * Initializes the Star to not be collected and adds a bounding box
	 */
	private void init()
	{
		dimension.set(0.5f, 0.5f);
		regStar = Assets.instance.star.star;
		
		//Set bounding box for collision detection
		bounds.set(0,0, dimension.x, dimension.y);
		collected = false;
	}
	
	/**
	 * This method returns a value to be added to the player's score
	 * 
	 * @return
	 */
	public int getScore()
	{
		return 25;
	}
	
	/**
	 * This method draws the Star
	 */
	@Override
	public void render(SpriteBatch batch)
	{
		TextureRegion reg = null;
		reg = regStar;
		batch.draw(reg.getTexture(), position.x, position.y, origin.x, origin.y, dimension.x, dimension.y, scale.x, scale.y, rotation, reg.getRegionX(), reg.getRegionY(), reg.getRegionWidth(), reg.getRegionHeight(), false, false);
	}

}
