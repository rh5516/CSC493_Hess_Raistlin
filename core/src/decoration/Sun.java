package decoration;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import game.Assets;
import objects.AbstractGameObject;

/**
 * An animated sun that sits in the background and is
 * effected by parallax
 * 
 * @author Raistlin Hess
 *
 */
public class Sun extends AbstractGameObject
{
	public Sun()
	{
		init();
	}
	
	/**
	 * Initializes the size and animationsa
	 */
	private void init()
	{
		dimension.set(1.6f, 1.6f);
		
		//Set up animation
		setAnimation(Assets.instance.levelDecoration.sun);
	}
	
	/**
	 * Update the Pyramid's x position based on the camera's
	 * 
	 * @param camPosition
	 */
	public void updateScrollPosition(Vector2 camPosition)
	{
		position.set(camPosition.x-4.0f, this.position.y);
	}
	
	/**
	 * This renders each frame of animation for the sun
	 */
	@Override
	public void render(SpriteBatch batch)
	{
		TextureRegion reg = null;
		reg = animation.getKeyFrame(stateTime, true);
		
		float parallaxSpeedX = 0.9f;
		float yOffset = 2.6f;
		
		batch.draw(reg.getTexture(), position.x*parallaxSpeedX, position.y+yOffset, origin.x, origin.y, dimension.x, dimension.y, scale.x, scale.y, rotation, reg.getRegionX(), reg.getRegionY(), reg.getRegionWidth(), reg.getRegionHeight(), false, false);
	}
}
