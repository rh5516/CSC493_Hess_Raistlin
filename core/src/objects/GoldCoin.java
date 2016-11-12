package objects;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import game.Assets;

/**
 * This represents a GoldCoin object. It is stationary and can be collected
 * to increase the score
 * 
 * @author Raistlin Hess
 *
 */
public class GoldCoin extends AbstractGameObject
{
	private TextureRegion regGoldCoin;
	public boolean collected;
	
	public GoldCoin()
	{
		init();
	}
	
	/**
	 * Sets the dimensions of the coin and initilaizes its bounding box 
	 * and collected to false
	 */
	private void init()
	{
		dimension.set(0.5f, 0.5f);
		
		setAnimation(Assets.instance.goldCoin.animGoldCoin);
		stateTime = MathUtils.random(0.0f, 1.0f);
		
		//Set bounding box for collision detection
		bounds.set(0, 0, dimension.x, dimension.y);
		collected = false;
	}
	
	/**
	 * This method will draw the coin if it has not been collected
	 */
	@Override
	public void render(SpriteBatch batch)
	{
		if(collected) return;
		
		TextureRegion reg = null;
		reg = animation.getKeyFrame(stateTime, true);
		batch.draw(reg.getTexture(), position.x, position.y, origin.x, origin.y, dimension.x, dimension.y, scale.x, scale.y, rotation, reg.getRegionX(), reg.getRegionY(), reg.getRegionWidth(), reg.getRegionHeight(), false, false);
	}
	
	/**
	 * Returns the value of this coin
	 * 
	 * @return
	 */
	public int getScore()
	{
		return 100;
	}

}
