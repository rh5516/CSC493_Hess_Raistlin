package decoration;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import game.Assets;
import objects.AbstractGameObject;

/**
 * This class represents a group of Cloud game objects. They are dispersed in the background
 * based on the length provided upon instantiation 
 * 
 * @author Raistlin Hess
 *
 */
public class Clouds extends AbstractGameObject
{
	private float length;
	private Array<TextureRegion> regClouds;
	private Array<Cloud> clouds;
	
	/**
	 * Sets the Clouds length to the parameter length
	 * 
	 * @param length
	 */
	public Clouds(float length)
	{
		this.length = length;
		init();
	}
	
	/**
	 * This loads each different cloud asset and also creates a number of clouds based 
	 * on the distFac
	 */
	private void init()
	{
		dimension.set(3.0f, 1.5f);
		regClouds = new Array<TextureRegion>();
		regClouds.add(Assets.instance.levelDecoration.cloud01);
		regClouds.add(Assets.instance.levelDecoration.cloud02);
		regClouds.add(Assets.instance.levelDecoration.cloud02);
		
		int distFac = 5;
		int numClouds = (int) (length/distFac);
		clouds = new Array<Cloud>(2*numClouds);
		
		for(int i = 0; i < numClouds; i++)
		{
			Cloud cloud = spawnCloud();
			cloud.position.x = i*distFac;
			clouds.add(cloud);
		}
	}
	
	/**
	 * This method is responsible for loading random cloud assets and returning a Cloud whose
	 * vertical position is determined randomly within certain bounds
	 * 
	 * @return
	 */
	private Cloud spawnCloud()
	{
		Cloud cloud = new Cloud();
		cloud.dimension.set(dimension);
		
		//Select random cloud asset
		cloud.setRegion(regClouds.random());
		Vector2 pos = new Vector2();
		pos.x = length+10;	//Position after end of the level
		pos.y += 3.0f;		//Base position
		pos.y += MathUtils.random(0.0f, 0.8f) * (MathUtils.randomBoolean() ? 1 : -1);	//Adds random value to position
		cloud.position.set(pos);
		return cloud;
	}
	
	/**
	 * Iterates over every Cloud in the Clouds ArrayList and renders them
	 */
	@Override
	public void render(SpriteBatch batch)
	{
		for(Cloud cloud: clouds)
		{
			cloud.render(batch);
		}
	}
	
	/**
	 * This class represents a single Cloud game object
	 * 
	 * @author Raistlin Hess
	 *
	 */
	private class Cloud extends AbstractGameObject
	{
		private TextureRegion regCloud;
		
		public Cloud(){}
		
		public void setRegion(TextureRegion region)
		{
			regCloud = region;
		}
		
		@Override
		public void render(SpriteBatch batch)
		{
			TextureRegion reg = regCloud;
			batch.draw(reg.getTexture(), position.x+origin.x, position.y+origin.y, origin.x, origin.y, dimension.x, dimension.y, scale.x, scale.y, rotation, reg.getRegionX(), reg.getRegionY(), reg.getRegionWidth(), reg.getRegionHeight(), false, false);
		}
	}
}
